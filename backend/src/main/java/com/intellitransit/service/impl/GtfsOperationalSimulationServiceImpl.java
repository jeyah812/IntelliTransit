package com.intellitransit.service.impl;

import com.intellitransit.dto.GtfsSimulationRequest;
import com.intellitransit.dto.GtfsSimulationResponse;
import com.intellitransit.entity.*;
import com.intellitransit.entity.enums.TripStatus;
import com.intellitransit.exception.GtfsValidationException;
import com.intellitransit.repository.*;
import com.intellitransit.service.GtfsOperationalSimulationService;
import com.intellitransit.util.GtfsSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class GtfsOperationalSimulationServiceImpl implements GtfsOperationalSimulationService {

    private static final Logger log = LoggerFactory.getLogger(GtfsOperationalSimulationServiceImpl.class);

    private final TripRepository tripRepository;
    private final TripLogRepository tripLogRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final GtfsScheduleTripRepository scheduleTripRepository;
    private final GtfsScheduleStopTimeRepository scheduleStopTimeRepository;
    private final GtfsCalendarRepository calendarRepository;
    private final PlatformTransactionManager transactionManager;

    public GtfsOperationalSimulationServiceImpl(TripRepository tripRepository,
                                                TripLogRepository tripLogRepository,
                                                RouteRepository routeRepository,
                                                BusRepository busRepository,
                                                DriverRepository driverRepository,
                                                GtfsScheduleTripRepository scheduleTripRepository,
                                                GtfsScheduleStopTimeRepository scheduleStopTimeRepository,
                                                GtfsCalendarRepository calendarRepository,
                                                PlatformTransactionManager transactionManager) {
        this.tripRepository = tripRepository;
        this.tripLogRepository = tripLogRepository;
        this.routeRepository = routeRepository;
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.scheduleTripRepository = scheduleTripRepository;
        this.scheduleStopTimeRepository = scheduleStopTimeRepository;
        this.calendarRepository = calendarRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public GtfsSimulationResponse simulateOperationalTrips(GtfsSimulationRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new GtfsValidationException("Start date and end date are required for simulation.");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new GtfsValidationException("Start date cannot be after end date.");
        }

        List<GtfsScheduleTrip> stagedTrips;
        if (request.getBatchId() != null && !request.getBatchId().trim().isEmpty()) {
            stagedTrips = scheduleTripRepository.findByBatchId(request.getBatchId().trim());
            if (stagedTrips.isEmpty()) {
                stagedTrips = scheduleTripRepository.findAll();
            }
        } else {
            stagedTrips = scheduleTripRepository.findAll();
        }

        if (stagedTrips.isEmpty()) {
            return GtfsSimulationResponse.builder()
                    .batchId(request.getBatchId())
                    .tripsGenerated(0)
                    .logsCreated(0)
                    .avgArrivalDelayMinutes(0.0)
                    .status("COMPLETED")
                    .message("No staged GTFS schedule trips found for simulation.")
                    .generatedAt(LocalDateTime.now())
                    .build();
        }

        int maxSamples = (request.getSampleSize() != null && request.getSampleSize() > 0) ? request.getSampleSize() : 100;
        long masterSeed = (request.getSeed() != null) ? request.getSeed() : 42L;

        List<Bus> availableBuses = busRepository.findAll();
        List<Driver> availableDrivers = driverRepository.findAll();

        if (availableBuses.isEmpty() || availableDrivers.isEmpty()) {
            throw new GtfsValidationException("Operation fleet error: No active Bus or Driver records available for assignment.");
        }

        long tripsGenerated = 0;
        long logsCreated = 0;
        double totalArrivalDelayMinutes = 0.0;

        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);

        int busIndex = 0;
        int driverIndex = 0;

        List<GtfsScheduleTrip> targetSubset = stagedTrips.subList(0, Math.min(maxSamples, stagedTrips.size()));

        for (LocalDate targetDate = request.getStartDate(); !targetDate.isAfter(request.getEndDate()); targetDate = targetDate.plusDays(1)) {
            for (GtfsScheduleTrip stagedTrip : targetSubset) {
                // 1. GTFS Service-Date Validation against Staged Calendar Data (Exact Batch Lineage Required)
                Optional<GtfsCalendar> calendarOpt = resolveCalendarForTrip(stagedTrip);
                if (calendarOpt.isEmpty()) {
                    continue; // Skip trip if exact batch calendar missing or un-staged
                }

                GtfsCalendar calendar = calendarOpt.get();
                if (!isCalendarActiveOnDate(calendar, targetDate)) {
                    continue; // Skip inactive GTFS service dates
                }

                // 2. Resolve Operational Route by Feed & Version Provenance
                String feedId = calendar.getFeedId();
                String feedVersion = calendar.getFeedVersion();

                Optional<Route> routeOpt = routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion(stagedTrip.getGtfsRouteId(), feedId, feedVersion);
                if (routeOpt.isEmpty()) {
                    routeOpt = routeRepository.findByGtfsRouteIdAndFeedVersion(stagedTrip.getGtfsRouteId(), feedVersion);
                }
                if (routeOpt.isEmpty()) {
                    routeOpt = routeRepository.findByGtfsRouteId(stagedTrip.getGtfsRouteId());
                }
                if (routeOpt.isEmpty()) {
                    continue; // Skip if matching Route provenance cannot be resolved
                }
                Route route = routeOpt.get();

                // 3. Resolve Start and End Times from Schedule Stop Times
                List<GtfsScheduleStopTime> stopTimes = scheduleStopTimeRepository.findByGtfsTripIdOrderByStopSequenceAsc(stagedTrip.getGtfsTripId());
                if (stopTimes.isEmpty()) {
                    continue;
                }

                LocalTime schedStartLocal = stopTimes.get(0).getArrivalTime() != null ? stopTimes.get(0).getArrivalTime() : LocalTime.of(8, 0);
                LocalTime schedEndLocal = stopTimes.get(stopTimes.size() - 1).getDepartureTime() != null ? stopTimes.get(stopTimes.size() - 1).getDepartureTime() : schedStartLocal.plusMinutes(45);

                LocalDateTime scheduledStart = LocalDateTime.of(targetDate, schedStartLocal);
                LocalDateTime scheduledEnd = LocalDateTime.of(targetDate, schedEndLocal);
                if (scheduledEnd.isBefore(scheduledStart) || scheduledEnd.isEqual(scheduledStart)) {
                    scheduledEnd = scheduledStart.plusMinutes(45);
                }

                // 4. Feed-Safe Idempotency Check
                boolean exists = tripRepository.existsByRouteFeedIdAndRouteFeedVersionAndGtfsTripIdAndScheduledStart(
                        route.getFeedId(), route.getFeedVersion(), stagedTrip.getGtfsTripId(), scheduledStart
                );
                if (exists) {
                    continue; // Skip duplicate execution creation
                }

                // 5. Fleet Allocation (Round-Robin without mutating live Bus/Driver status)
                Bus bus = availableBuses.get(busIndex % availableBuses.size());
                Driver driver = availableDrivers.get(driverIndex % availableDrivers.size());
                busIndex++;
                driverIndex++;

                // 6. Deterministic Formula Engine
                long seed = Math.abs((long) stagedTrip.getGtfsTripId().hashCode() * 31L + (long) scheduledStart.hashCode() * 17L + masterSeed);
                Random rng = new Random(seed);

                int hour = scheduledStart.getHour();
                int dayOfWeekVal = scheduledStart.getDayOfWeek().getValue();
                double distanceKm = route.getDistanceKm() != null ? route.getDistanceKm().doubleValue() : 10.0;
                long schedDurationMinutes = ChronoUnit.MINUTES.between(scheduledStart, scheduledEnd);

                // Btime
                double bTime;
                if ((hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 19)) {
                    bTime = 10.0 + rng.nextDouble() * 15.0;
                } else if (hour >= 10 && hour <= 16) {
                    bTime = 2.0 + rng.nextDouble() * 8.0;
                } else {
                    bTime = -1.0 + rng.nextDouble() * 4.0;
                }

                // Mday
                double mDay = (dayOfWeekVal <= 5) ? 1.25 : 0.75;

                // Sroute
                double sRoute = Math.min(1.8, 0.6 + distanceKm / 25.0);

                // Cdur
                double cDur = Math.min(10.0, 0.05 * (double) schedDurationMinutes);

                // Start delay & Arrival delay
                long startDelay = Math.round(Math.max(-2.0, -2.0 + rng.nextDouble() * 4.0 + bTime * 0.25));
                long arrivalDelay = Math.round(Math.max(-4.0, (double) startDelay + (bTime * mDay * sRoute) + cDur));

                LocalDateTime actualStart = scheduledStart.plusMinutes(startDelay);
                LocalDateTime actualEnd = scheduledEnd.plusMinutes(arrivalDelay);

                // Invariant Safety Constraint: actualEnd > actualStart
                if (!actualEnd.isAfter(actualStart)) {
                    actualEnd = actualStart.plusMinutes(Math.max(1, (int) schedDurationMinutes - 2));
                }

                long durationMinutes = ChronoUnit.MINUTES.between(actualStart, actualEnd);

                // 7. Save Operational Trip & TripLog in Simulation Transaction
                final String finalGtfsTripId = stagedTrip.getGtfsTripId();
                final LocalDateTime fSchedStart = scheduledStart;
                final LocalDateTime fSchedEnd = scheduledEnd;
                final LocalDateTime fActualStart = actualStart;
                final LocalDateTime fActualEnd = actualEnd;
                final long fArrivalDelay = arrivalDelay;

                txTemplate.executeWithoutResult(status -> {
                    Trip trip = Trip.builder()
                            .gtfsTripId(finalGtfsTripId)
                            .route(route)
                            .bus(bus)
                            .driver(driver)
                            .scheduledStart(fSchedStart)
                            .scheduledEnd(fSchedEnd)
                            .actualStart(fActualStart)
                            .actualEnd(fActualEnd)
                            .status(TripStatus.COMPLETED)
                            .build();

                    Trip savedTrip = tripRepository.save(trip);

                    TripLog logRecord = TripLog.builder()
                            .trip(savedTrip)
                            .actualStart(fActualStart)
                            .actualEnd(fActualEnd)
                            .durationMinutes((int) durationMinutes)
                            .notes("Simulated GTFS operational execution. Arrival delay: " + fArrivalDelay + " mins")
                            .build();

                    tripLogRepository.save(logRecord);
                });

                tripsGenerated++;
                logsCreated++;
                totalArrivalDelayMinutes += arrivalDelay;
            }
        }

        double avgDelay = tripsGenerated > 0 ? (totalArrivalDelayMinutes / (double) tripsGenerated) : 0.0;

        return GtfsSimulationResponse.builder()
                .batchId(request.getBatchId())
                .tripsGenerated(tripsGenerated)
                .logsCreated(logsCreated)
                .avgArrivalDelayMinutes(Math.round(avgDelay * 100.0) / 100.0)
                .status("COMPLETED")
                .message("Operational trip simulation completed successfully. Generated " + tripsGenerated + " historical trips.")
                .generatedAt(LocalDateTime.now())
                .build();
    }

    private Optional<GtfsCalendar> resolveCalendarForTrip(GtfsScheduleTrip trip) {
        if (trip == null || trip.getGtfsServiceId() == null || !GtfsSanitizer.isSanitary(trip.getGtfsServiceId())) {
            return Optional.empty();
        }
        if (trip.getBatchId() == null || trip.getBatchId().trim().isEmpty()) {
            return Optional.empty();
        }

        // Strict Batch Lineage Calendar Lookup: gtfsServiceId + batchId (No cross-batch fallback!)
        return calendarRepository.findByGtfsServiceIdAndBatchId(trip.getGtfsServiceId().trim(), trip.getBatchId().trim());
    }

    private boolean isCalendarActiveOnDate(GtfsCalendar calendar, LocalDate targetDate) {
        if (calendar == null || targetDate == null) {
            return false;
        }

        // 1. Verify start_date <= targetDate <= end_date
        if (targetDate.isBefore(calendar.getStartDate()) || targetDate.isAfter(calendar.getEndDate())) {
            return false;
        }

        // 2. Verify actual day-of-week flag for targetDate is 1
        DayOfWeek dow = targetDate.getDayOfWeek();
        return switch (dow) {
            case MONDAY -> calendar.getMonday() != null && calendar.getMonday() == 1;
            case TUESDAY -> calendar.getTuesday() != null && calendar.getTuesday() == 1;
            case WEDNESDAY -> calendar.getWednesday() != null && calendar.getWednesday() == 1;
            case THURSDAY -> calendar.getThursday() != null && calendar.getThursday() == 1;
            case FRIDAY -> calendar.getFriday() != null && calendar.getFriday() == 1;
            case SATURDAY -> calendar.getSaturday() != null && calendar.getSaturday() == 1;
            case SUNDAY -> calendar.getSunday() != null && calendar.getSunday() == 1;
        };
    }
}

package com.intellitransit.service.impl;

import com.intellitransit.dto.SimulatedVehicleDTO;
import com.intellitransit.entity.RouteStop;
import com.intellitransit.entity.Stop;
import com.intellitransit.entity.Trip;
import com.intellitransit.entity.enums.TripStatus;
import com.intellitransit.repository.RouteStopRepository;
import com.intellitransit.repository.TripRepository;
import com.intellitransit.service.TransitSimulationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TransitSimulationServiceImpl implements TransitSimulationService {

    private final TripRepository tripRepository;
    private final RouteStopRepository routeStopRepository;

    @Value("${intellitransit.simulation.acceleration-factor:10}")
    private double accelerationFactor = 10.0;

    // Cache of active simulated vehicle states keyed by trip ID
    private final Map<Long, SimulatedVehicleDTO> activeVehicles = new ConcurrentHashMap<>();

    // Keep track of simulation start baseline for smooth progression
    private final LocalDateTime simulationBaseline = LocalDateTime.now();

    public TransitSimulationServiceImpl(TripRepository tripRepository, RouteStopRepository routeStopRepository) {
        this.tripRepository = tripRepository;
        this.routeStopRepository = routeStopRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimulatedVehicleDTO> getCurrentSimulationState() {
        if (activeVehicles.isEmpty()) {
            tickSimulation();
        }
        return new ArrayList<>(activeVehicles.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimulatedVehicleDTO> getSimulationStateByRouteId(Long routeId) {
        if (routeId == null) return Collections.emptyList();
        return getCurrentSimulationState().stream()
                .filter(v -> routeId.equals(v.getRouteId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SimulatedVehicleDTO getSimulationStateByTripId(Long tripId) {
        if (tripId == null) return null;
        if (activeVehicles.isEmpty()) {
            tickSimulation();
        }
        return activeVehicles.get(tripId);
    }

    @Override
    @Scheduled(fixedDelayString = "${intellitransit.simulation.update-interval-ms:3000}")
    @Transactional(readOnly = true)
    public void tickSimulation() {
        List<Trip> trips = tripRepository.findAll();
        if (trips.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();
        // Calculate accelerated virtual simulation time
        long secondsElapsedReal = ChronoUnit.SECONDS.between(simulationBaseline, now);
        long acceleratedSeconds = (long) (secondsElapsedReal * accelerationFactor);

        for (Trip trip : trips) {
            // Only simulate SCHEDULED or IN_PROGRESS trips (skip CANCELLED)
            if (trip.getStatus() == TripStatus.CANCELLED) {
                continue;
            }

            Long routeId = trip.getRoute() != null ? trip.getRoute().getId() : null;
            if (routeId == null) continue;

            List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequenceAsc(routeId);
            if (routeStops.isEmpty()) continue;

            SimulatedVehicleDTO dto = calculateVehicleState(trip, routeStops, acceleratedSeconds, now);
            if (dto != null) {
                activeVehicles.put(trip.getId(), dto);
            }
        }
    }

    private SimulatedVehicleDTO calculateVehicleState(Trip trip, List<RouteStop> routeStops, long acceleratedSeconds, LocalDateTime now) {
        LocalDateTime schedStart = trip.getScheduledStart();
        LocalDateTime schedEnd = trip.getScheduledEnd();

        long totalPlannedSeconds = (schedStart != null && schedEnd != null)
                ? Math.max(600, ChronoUnit.SECONDS.between(schedStart, schedEnd))
                : 1800; // default 30 mins

        // Calculate bounded, deterministic delay per trip (between 0 and 180 seconds)
        int boundedDelaySeconds = (int) ((trip.getId() * 37) % 181);

        // Progress ratio based on accelerated elapsed time
        double overallProgress = 0.0;
        if (trip.getStatus() == TripStatus.COMPLETED) {
            overallProgress = 1.0;
        } else {
            // Determine progress through total planned duration
            long tripTimeOffset = (trip.getId() * 45) % totalPlannedSeconds;
            long effectiveSeconds = (acceleratedSeconds + tripTimeOffset) % (totalPlannedSeconds + 120);

            if (effectiveSeconds >= totalPlannedSeconds) {
                overallProgress = 1.0;
            } else {
                overallProgress = Math.min(1.0, Math.max(0.0, (double) effectiveSeconds / (double) totalPlannedSeconds));
            }
        }

        int numStops = routeStops.size();
        int numSegments = Math.max(1, numStops - 1);

        double segmentLength = 1.0 / numSegments;
        int currentSegIdx = Math.min(numSegments - 1, (int) (overallProgress / segmentLength));
        double segmentProgress = (overallProgress - (currentSegIdx * segmentLength)) / segmentLength;
        segmentProgress = Math.min(1.0, Math.max(0.0, segmentProgress));

        RouteStop fromRouteStop = routeStops.get(currentSegIdx);
        RouteStop toRouteStop = (currentSegIdx + 1 < numStops) ? routeStops.get(currentSegIdx + 1) : fromRouteStop;

        Stop currentStop = fromRouteStop.getStop();
        Stop nextStop = toRouteStop.getStop();

        BigDecimal currentLat = currentStop != null ? currentStop.getLatitude() : BigDecimal.valueOf(13.0827);
        BigDecimal currentLon = currentStop != null ? currentStop.getLongitude() : BigDecimal.valueOf(80.2707);

        BigDecimal nextLat = nextStop != null ? nextStop.getLatitude() : currentLat;
        BigDecimal nextLon = nextStop != null ? nextStop.getLongitude() : currentLon;

        // Fallback default coordinates if null in DB
        if (currentLat == null) currentLat = BigDecimal.valueOf(13.0827 + (currentSegIdx * 0.01));
        if (currentLon == null) currentLon = BigDecimal.valueOf(80.2707 + (currentSegIdx * 0.01));
        if (nextLat == null) nextLat = currentLat.add(BigDecimal.valueOf(0.005));
        if (nextLon == null) nextLon = currentLon.add(BigDecimal.valueOf(0.005));

        // Perform linear coordinate interpolation between current and next stop
        double interpolatedLat = currentLat.doubleValue() + (nextLat.doubleValue() - currentLat.doubleValue()) * segmentProgress;
        double interpolatedLon = currentLon.doubleValue() + (nextLon.doubleValue() - currentLon.doubleValue()) * segmentProgress;

        BigDecimal vehicleLat = BigDecimal.valueOf(interpolatedLat).setScale(6, RoundingMode.HALF_UP);
        BigDecimal vehicleLon = BigDecimal.valueOf(interpolatedLon).setScale(6, RoundingMode.HALF_UP);

        // Determine vehicle status
        String vehicleStatus;
        if (overallProgress >= 1.0) {
            vehicleStatus = "COMPLETED";
        } else if (boundedDelaySeconds > 90) {
            vehicleStatus = "DELAYED";
        } else if (segmentProgress < 0.15 || segmentProgress > 0.85) {
            vehicleStatus = "AT_STOP";
        } else {
            vehicleStatus = "EN_ROUTE";
        }

        String busNumber = trip.getBus() != null ? trip.getBus().getBusNumber() : "BUS-" + trip.getId();
        String driverName = trip.getDriver() != null ? trip.getDriver().getFullName() : "Driver " + trip.getId();
        String routeNumber = trip.getRoute() != null ? trip.getRoute().getRouteNumber() : "R" + trip.getId();
        String routeName = trip.getRoute() != null ? trip.getRoute().getRouteName() : "Route " + trip.getId();

        return SimulatedVehicleDTO.builder()
                .tripId(trip.getId())
                .gtfsTripId(trip.getGtfsTripId())
                .routeId(trip.getRoute() != null ? trip.getRoute().getId() : null)
                .routeNumber(routeNumber)
                .routeName(routeName)
                .busNumber(busNumber)
                .driverName(driverName)
                .currentStopId(currentStop != null ? currentStop.getId() : null)
                .currentStopName(currentStop != null ? currentStop.getName() : "Origin Stop")
                .nextStopId(nextStop != null ? nextStop.getId() : null)
                .nextStopName(nextStop != null ? nextStop.getName() : "Terminus Stop")
                .stopSequence(fromRouteStop.getStopSequence())
                .currentLatitude(vehicleLat)
                .currentLongitude(vehicleLon)
                .progressBetweenStops(BigDecimal.valueOf(segmentProgress).setScale(4, RoundingMode.HALF_UP).doubleValue())
                .status(vehicleStatus)
                .delaySeconds(boundedDelaySeconds)
                .lastUpdatedAt(now)
                .build();
    }
}

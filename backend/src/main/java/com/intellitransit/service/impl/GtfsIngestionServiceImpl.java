package com.intellitransit.service.impl;

import com.intellitransit.dto.GtfsImportRequest;
import com.intellitransit.dto.GtfsImportResponse;
import com.intellitransit.entity.*;
import com.intellitransit.exception.GtfsValidationException;
import com.intellitransit.repository.*;
import com.intellitransit.service.GtfsIngestionService;
import com.intellitransit.util.GtfsSanitizer;
import com.intellitransit.util.GtfsTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GtfsIngestionServiceImpl implements GtfsIngestionService {

    private static final Logger log = LoggerFactory.getLogger(GtfsIngestionServiceImpl.class);

    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final RouteStopRepository routeStopRepository;
    private final GtfsFeedMetadataRepository feedMetadataRepository;
    private final GtfsScheduleTripRepository scheduleTripRepository;
    private final GtfsScheduleStopTimeRepository scheduleStopTimeRepository;
    private final GtfsQuarantineLogRepository quarantineLogRepository;
    private final GtfsCalendarRepository calendarRepository;
    private final PlatformTransactionManager transactionManager;

    public GtfsIngestionServiceImpl(RouteRepository routeRepository,
                                   StopRepository stopRepository,
                                   RouteStopRepository routeStopRepository,
                                   GtfsFeedMetadataRepository feedMetadataRepository,
                                   GtfsScheduleTripRepository scheduleTripRepository,
                                   GtfsScheduleStopTimeRepository scheduleStopTimeRepository,
                                   GtfsQuarantineLogRepository quarantineLogRepository,
                                   GtfsCalendarRepository calendarRepository,
                                   PlatformTransactionManager transactionManager) {
        this.routeRepository = routeRepository;
        this.stopRepository = stopRepository;
        this.routeStopRepository = routeStopRepository;
        this.feedMetadataRepository = feedMetadataRepository;
        this.scheduleTripRepository = scheduleTripRepository;
        this.scheduleStopTimeRepository = scheduleStopTimeRepository;
        this.quarantineLogRepository = quarantineLogRepository;
        this.calendarRepository = calendarRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public GtfsImportResponse importGtfsFeed(GtfsImportRequest request) {
        File dir = new File(request.getDirectoryPath());
        if (!dir.exists() || !dir.isDirectory()) {
            throw new GtfsValidationException("GTFS directory does not exist or is not a directory: " + request.getDirectoryPath());
        }

        File routesFile = new File(dir, "routes.txt");
        File stopsFile = new File(dir, "stops.txt");
        File tripsFile = new File(dir, "trips.txt");
        File stopTimesFile = new File(dir, "stop_times.txt");

        if (!routesFile.exists() || !stopsFile.exists() || !tripsFile.exists() || !stopTimesFile.exists()) {
            throw new GtfsValidationException("Required GTFS files missing. Required: routes.txt, stops.txt, trips.txt, stop_times.txt");
        }

        String batchId = "BATCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String feedId = (request.getFeedId() != null && !request.getFeedId().trim().isEmpty()) ? request.getFeedId().trim() : "MTC";
        String feedVersion = (request.getFeedVersion() != null && !request.getFeedVersion().trim().isEmpty()) ? request.getFeedVersion().trim() : "2.0";
        LocalDateTime importTime = LocalDateTime.now();

        long quarantinedCount = 0;
        long routesImported = 0;
        long stopsImported = 0;
        long tripsStaged = 0;
        long stopTimesStaged = 0;

        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);

        // 1. Process Calendar / Feed Info if present
        File calendarFile = new File(dir, "calendar.txt");
        if (calendarFile.exists()) {
            quarantinedCount += processCalendarFile(calendarFile, batchId, feedId, feedVersion, txTemplate);
        }

        // 2. Import Stops (Authoritative GTFS stop_id + feedId + feedVersion Identity)
        Map<String, Stop> gtfsStopMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(stopsFile))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new GtfsValidationException("stops.txt is empty");
            }
            Map<String, Integer> colIndex = parseHeader(headerLine);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (!GtfsSanitizer.isSanitary(line)) {
                    quarantineRecord(batchId, "stops.txt", line, "Malicious string payload detected");
                    quarantinedCount++;
                    continue;
                }
                String[] tokens = parseCsvLine(line);
                String stopId = getVal(tokens, colIndex, "stop_id");
                String stopName = GtfsSanitizer.normalize(getVal(tokens, colIndex, "stop_name"));
                String latStr = getVal(tokens, colIndex, "stop_lat");
                String lonStr = getVal(tokens, colIndex, "stop_lon");

                if (stopId == null || stopName == null || latStr == null || lonStr == null) {
                    quarantineRecord(batchId, "stops.txt", line, "Missing required stop fields");
                    quarantinedCount++;
                    continue;
                }

                double lat, lon;
                try {
                    lat = Double.parseDouble(latStr);
                    lon = Double.parseDouble(lonStr);
                } catch (NumberFormatException e) {
                    quarantineRecord(batchId, "stops.txt", line, "Invalid numeric coordinates");
                    quarantinedCount++;
                    continue;
                }

                // Geo-boundary validation for Chennai region
                if (lat < 12.0 || lat > 14.0 || lon < 79.5 || lon > 80.6) {
                    quarantineRecord(batchId, "stops.txt", line, "Geographic coordinates out of Chennai region bounds");
                    quarantinedCount++;
                    continue;
                }

                BigDecimal bLat = BigDecimal.valueOf(lat).setScale(8, RoundingMode.HALF_UP);
                BigDecimal bLon = BigDecimal.valueOf(lon).setScale(8, RoundingMode.HALF_UP);

                // Authoritative GTFS stop_id + feedId + feedVersion lookup
                Optional<Stop> existingStop = stopRepository.findByGtfsStopIdAndFeedIdAndFeedVersion(stopId, feedId, feedVersion);

                Stop stop;
                if (existingStop.isPresent()) {
                    stop = existingStop.get();
                } else {
                    Stop newStop = new Stop(null, stopId, feedId, feedVersion, stopName, bLat, bLon, importTime);
                    stop = txTemplate.execute(status -> stopRepository.save(newStop));
                    stopsImported++;
                }

                gtfsStopMap.put(stopId, stop);
            }
        } catch (Exception e) {
            if (e instanceof GtfsValidationException) throw (GtfsValidationException) e;
            throw new GtfsValidationException("Error processing stops.txt: " + e.getMessage());
        }

        // 3. Import Routes (Authoritative gtfs_route_id + feedId + feedVersion & Direction Disambiguation)
        Map<String, Route> gtfsRouteMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(routesFile))) {
            String headerLine = br.readLine();
            if (headerLine == null) throw new GtfsValidationException("routes.txt is empty");
            Map<String, Integer> colIndex = parseHeader(headerLine);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (!GtfsSanitizer.isSanitary(line)) {
                    quarantineRecord(batchId, "routes.txt", line, "Malicious string payload detected");
                    quarantinedCount++;
                    continue;
                }
                String[] tokens = parseCsvLine(line);
                String routeId = getVal(tokens, colIndex, "route_id");
                String shortName = GtfsSanitizer.normalize(getVal(tokens, colIndex, "route_short_name"));
                String longName = GtfsSanitizer.normalize(getVal(tokens, colIndex, "route_long_name"));

                if (routeId == null) {
                    quarantineRecord(batchId, "routes.txt", line, "Missing route_id");
                    quarantinedCount++;
                    continue;
                }

                String displayShortName = (shortName != null && !shortName.isEmpty()) ? shortName : routeId;
                String displayLongName = (longName != null && !longName.isEmpty()) ? longName : displayShortName;

                // Unique routeNumber disambiguation per feedId and feedVersion
                String candidateRouteNumber = displayShortName;
                Optional<Route> existingRoute = routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion(routeId, feedId, feedVersion);
                Route route;
                if (existingRoute.isPresent()) {
                    route = existingRoute.get();
                } else {
                    Optional<Route> collision = routeRepository.findByRouteNumber(candidateRouteNumber);
                    if (collision.isPresent()) {
                        Route cRoute = collision.get();
                        cRoute.setGtfsRouteId(routeId);
                        cRoute.setFeedId(feedId);
                        cRoute.setFeedVersion(feedVersion);
                        cRoute.setRouteName(displayLongName);
                        route = txTemplate.execute(status -> routeRepository.save(cRoute));
                    } else {
                        Route newRoute = Route.builder()
                                .gtfsRouteId(routeId)
                                .feedId(feedId)
                                .feedVersion(feedVersion)
                                .routeNumber(candidateRouteNumber)
                                .routeName(displayLongName)
                                .origin("Origin Stop")
                                .destination("Destination Stop")
                                .distanceKm(BigDecimal.ZERO)
                                .estimatedDurationMinutes(30)
                                .active(true)
                                .createdAt(importTime)
                                .build();
                        route = txTemplate.execute(status -> routeRepository.save(newRoute));
                        routesImported++;
                    }
                }

                gtfsRouteMap.put(routeId, route);
            }
        } catch (Exception e) {
            if (e instanceof GtfsValidationException) throw (GtfsValidationException) e;
            throw new GtfsValidationException("Error processing routes.txt: " + e.getMessage());
        }

        // 4. Stage GTFS Trips in Chunks of 1,000
        Map<String, GtfsScheduleTrip> gtfsTripMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(tripsFile))) {
            String headerLine = br.readLine();
            if (headerLine == null) throw new GtfsValidationException("trips.txt is empty");
            Map<String, Integer> colIndex = parseHeader(headerLine);
            String line;
            List<GtfsScheduleTrip> batchTrips = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (!GtfsSanitizer.isSanitary(line)) {
                    quarantineRecord(batchId, "trips.txt", line, "Malicious string payload detected");
                    quarantinedCount++;
                    continue;
                }
                String[] tokens = parseCsvLine(line);
                String tripId = getVal(tokens, colIndex, "trip_id");
                String routeId = getVal(tokens, colIndex, "route_id");
                String serviceId = getVal(tokens, colIndex, "service_id");
                String dirStr = getVal(tokens, colIndex, "direction_id");

                if (tripId == null || routeId == null || serviceId == null) {
                    quarantineRecord(batchId, "trips.txt", line, "Missing required trip fields");
                    quarantinedCount++;
                    continue;
                }

                if (!gtfsRouteMap.containsKey(routeId)) {
                    quarantineRecord(batchId, "trips.txt", line, "Foreign key error: route_id not found");
                    quarantinedCount++;
                    continue;
                }

                Integer directionId = null;
                if (dirStr != null) {
                    try { directionId = Integer.parseInt(dirStr); } catch (Exception ignored) {}
                }

                GtfsScheduleTrip stagedTrip = new GtfsScheduleTrip(null, tripId, routeId, serviceId, directionId, batchId);
                batchTrips.add(stagedTrip);
                gtfsTripMap.put(tripId, stagedTrip);

                if (batchTrips.size() >= 1000) {
                    List<GtfsScheduleTrip> chunk = new ArrayList<>(batchTrips);
                    txTemplate.executeWithoutResult(status -> scheduleTripRepository.saveAll(chunk));
                    tripsStaged += chunk.size();
                    batchTrips.clear();
                }
            }
            if (!batchTrips.isEmpty()) {
                List<GtfsScheduleTrip> chunk = new ArrayList<>(batchTrips);
                txTemplate.executeWithoutResult(status -> scheduleTripRepository.saveAll(chunk));
                tripsStaged += chunk.size();
            }
        } catch (Exception e) {
            if (e instanceof GtfsValidationException) throw (GtfsValidationException) e;
            throw new GtfsValidationException("Error processing trips.txt: " + e.getMessage());
        }

        // 5. Stage Stop Times in Bounded Transaction Chunks of 1,000 Records
        Map<Long, Map<Integer, Stop>> routePatternStopSequenceMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(stopTimesFile))) {
            String headerLine = br.readLine();
            if (headerLine == null) throw new GtfsValidationException("stop_times.txt is empty");
            Map<String, Integer> colIndex = parseHeader(headerLine);
            String line;
            List<GtfsScheduleStopTime> batchStopTimes = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (!GtfsSanitizer.isSanitary(line)) {
                    quarantineRecord(batchId, "stop_times.txt", line, "Malicious string payload detected");
                    quarantinedCount++;
                    continue;
                }
                String[] tokens = parseCsvLine(line);
                String tripId = getVal(tokens, colIndex, "trip_id");
                String stopId = getVal(tokens, colIndex, "stop_id");
                String arrStr = getVal(tokens, colIndex, "arrival_time");
                String depStr = getVal(tokens, colIndex, "departure_time");
                String seqStr = getVal(tokens, colIndex, "stop_sequence");

                if (tripId == null || stopId == null || seqStr == null) {
                    quarantineRecord(batchId, "stop_times.txt", line, "Missing required stop_times fields");
                    quarantinedCount++;
                    continue;
                }

                if (!gtfsTripMap.containsKey(tripId) || !gtfsStopMap.containsKey(stopId)) {
                    quarantineRecord(batchId, "stop_times.txt", line, "Foreign key error: trip_id or stop_id not found");
                    quarantinedCount++;
                    continue;
                }

                int seq;
                try {
                    seq = Integer.parseInt(seqStr);
                } catch (Exception e) {
                    quarantineRecord(batchId, "stop_times.txt", line, "Invalid stop_sequence integer");
                    quarantinedCount++;
                    continue;
                }

                GtfsTimeUtil.GtfsTimeResult arrRes = GtfsTimeUtil.parseGtfsTime(arrStr != null ? arrStr : depStr);
                GtfsTimeUtil.GtfsTimeResult depRes = GtfsTimeUtil.parseGtfsTime(depStr != null ? depStr : arrStr);

                GtfsScheduleStopTime st = new GtfsScheduleStopTime(
                        null,
                        tripId,
                        stopId,
                        arrRes.getLocalTime(),
                        depRes.getLocalTime(),
                        arrRes.getDayOffset(),
                        seq,
                        batchId
                );
                batchStopTimes.add(st);

                // Collect stop sequence topology preserving exact GTFS stop_sequence
                GtfsScheduleTrip parentTrip = gtfsTripMap.get(tripId);
                Route parentRoute = gtfsRouteMap.get(parentTrip.getGtfsRouteId());
                if (parentRoute != null) {
                    routePatternStopSequenceMap.computeIfAbsent(parentRoute.getId(), k -> new TreeMap<>());
                    Stop mappedStop = gtfsStopMap.get(stopId);
                    if (mappedStop != null) {
                        routePatternStopSequenceMap.get(parentRoute.getId()).putIfAbsent(seq, mappedStop);
                    }
                }

                // Dedicated Bounded Transaction Batch Commit every 1000 records
                if (batchStopTimes.size() >= 1000) {
                    List<GtfsScheduleStopTime> chunk = new ArrayList<>(batchStopTimes);
                    txTemplate.executeWithoutResult(status -> scheduleStopTimeRepository.saveAll(chunk));
                    stopTimesStaged += chunk.size();
                    batchStopTimes.clear();
                }
            }
            if (!batchStopTimes.isEmpty()) {
                List<GtfsScheduleStopTime> chunk = new ArrayList<>(batchStopTimes);
                txTemplate.executeWithoutResult(status -> scheduleStopTimeRepository.saveAll(chunk));
                stopTimesStaged += chunk.size();
            }
        } catch (Exception e) {
            if (e instanceof GtfsValidationException) throw (GtfsValidationException) e;
            throw new GtfsValidationException("Error processing stop_times.txt: " + e.getMessage());
        }

        // 6. Populate RouteStop Topology Preserving GTFS Stop Sequence and Cumulative Estimated Distance
        final long finalRoutes = routesImported;
        final long finalStops = stopsImported;
        final long finalQuarantined = quarantinedCount;

        txTemplate.executeWithoutResult(status -> {
            for (Map.Entry<Long, Map<Integer, Stop>> entry : routePatternStopSequenceMap.entrySet()) {
                Long routeId = entry.getKey();
                Map<Integer, Stop> seqStopMap = entry.getValue();
                Optional<Route> rOpt = routeRepository.findById(routeId);
                if (rOpt.isPresent()) {
                    Route route = rOpt.get();
                    List<Map.Entry<Integer, Stop>> sortedEntries = new ArrayList<>(seqStopMap.entrySet());
                    if (!sortedEntries.isEmpty()) {
                        route.setOrigin(sortedEntries.get(0).getValue().getName());
                        route.setDestination(sortedEntries.get(sortedEntries.size() - 1).getValue().getName());
                    }

                    double cumulativeDistanceKm = 0.0;
                    Stop prevStop = null;
                    for (Map.Entry<Integer, Stop> seqEntry : sortedEntries) {
                        int seq = seqEntry.getKey();
                        Stop currentStop = seqEntry.getValue();

                        if (prevStop != null) {
                            cumulativeDistanceKm += haversineDistanceKm(
                                    prevStop.getLatitude().doubleValue(), prevStop.getLongitude().doubleValue(),
                                    currentStop.getLatitude().doubleValue(), currentStop.getLongitude().doubleValue()
                            );
                        }
                        prevStop = currentStop;

                        if (!routeStopRepository.existsByRouteIdAndStopSequence(route.getId(), seq)) {
                            RouteStop rs = new RouteStop(
                                    null,
                                    route,
                                    currentStop,
                                    seq,
                                    BigDecimal.valueOf(cumulativeDistanceKm).setScale(2, RoundingMode.HALF_UP)
                            );
                            routeStopRepository.save(rs);
                        }
                    }

                    route.setDistanceKm(BigDecimal.valueOf(cumulativeDistanceKm).setScale(2, RoundingMode.HALF_UP));
                    routeRepository.save(route);
                }
            }

            // 7. Save Provenance Metadata
            GtfsFeedMetadata metadata = new GtfsFeedMetadata(
                    null,
                    feedId,
                    feedVersion,
                    batchId,
                    importTime,
                    finalRoutes,
                    finalStops,
                    finalQuarantined,
                    "COMPLETED"
            );
            feedMetadataRepository.save(metadata);
        });

        return GtfsImportResponse.builder()
                .batchId(batchId)
                .importedAt(importTime)
                .totalRoutesImported(routesImported)
                .totalStopsImported(stopsImported)
                .totalTripsStaged(tripsStaged)
                .totalStopTimesStaged(stopTimesStaged)
                .quarantinedRecordsCount(quarantinedCount)
                .status("COMPLETED")
                .message("GTFS Ingestion completed successfully. Static trips isolated in staging.")
                .build();
    }

    private long scanAndQuarantineFile(File file, String batchId, String fileName) {
        long count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!GtfsSanitizer.isSanitary(line)) {
                    quarantineRecord(batchId, fileName, line, "Malicious payload or script detected in " + fileName);
                    count++;
                }
            }
        } catch (Exception ignored) {}
        return count;
    }

    private void quarantineRecord(String batchId, String fileName, String rawRecord, String reason) {
        GtfsQuarantineLog logRecord = new GtfsQuarantineLog(null, batchId, fileName, rawRecord, reason, LocalDateTime.now());
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        txTemplate.executeWithoutResult(status -> quarantineLogRepository.save(logRecord));
    }

    private double haversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0; // Earth radius in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private Map<String, Integer> parseHeader(String headerLine) {
        Map<String, Integer> map = new HashMap<>();
        String[] tokens = parseCsvLine(headerLine);
        for (int i = 0; i < tokens.length; i++) {
            map.put(tokens[i].trim().toLowerCase(), i);
        }
        return map;
    }

    private String[] parseCsvLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    private String getVal(String[] tokens, Map<String, Integer> colIndex, String colName) {
        Integer idx = colIndex.get(colName.toLowerCase());
        if (idx == null || idx >= tokens.length) return null;
        String val = tokens[idx].trim();
        if (val.startsWith("\"") && val.endsWith("\"")) {
            val = val.substring(1, val.length() - 1);
        }
        return val.isEmpty() ? null : val;
    }

    private long processCalendarFile(File calendarFile, String batchId, String feedId, String feedVersion, TransactionTemplate txTemplate) {
        long quarantined = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(calendarFile))) {
            String headerLine = br.readLine();
            if (headerLine == null) return 0;
            Map<String, Integer> colIndex = parseHeader(headerLine);
            String line;
            List<GtfsCalendar> calendarBatch = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (!GtfsSanitizer.isSanitary(line)) {
                    quarantineRecord(batchId, "calendar.txt", line, "Malicious payload detected");
                    quarantined++;
                    continue;
                }
                String[] tokens = parseCsvLine(line);
                String serviceId = getVal(tokens, colIndex, "service_id");
                String monStr = getVal(tokens, colIndex, "monday");
                String tueStr = getVal(tokens, colIndex, "tuesday");
                String wedStr = getVal(tokens, colIndex, "wednesday");
                String thuStr = getVal(tokens, colIndex, "thursday");
                String friStr = getVal(tokens, colIndex, "friday");
                String satStr = getVal(tokens, colIndex, "saturday");
                String sunStr = getVal(tokens, colIndex, "sunday");
                String startStr = getVal(tokens, colIndex, "start_date");
                String endStr = getVal(tokens, colIndex, "end_date");

                if (serviceId == null || startStr == null || endStr == null) {
                    quarantineRecord(batchId, "calendar.txt", line, "Missing required calendar fields");
                    quarantined++;
                    continue;
                }

                try {
                    java.time.LocalDate startDate = parseGtfsDate(startStr);
                    java.time.LocalDate endDate = parseGtfsDate(endStr);
                    GtfsCalendar gc = GtfsCalendar.builder()
                            .gtfsServiceId(serviceId.trim())
                            .feedId(feedId)
                            .feedVersion(feedVersion)
                            .monday(parseBit(monStr))
                            .tuesday(parseBit(tueStr))
                            .wednesday(parseBit(wedStr))
                            .thursday(parseBit(thuStr))
                            .friday(parseBit(friStr))
                            .saturday(parseBit(satStr))
                            .sunday(parseBit(sunStr))
                            .startDate(startDate)
                            .endDate(endDate)
                            .batchId(batchId)
                            .build();
                    calendarBatch.add(gc);
                } catch (Exception e) {
                    quarantineRecord(batchId, "calendar.txt", line, "Invalid date format: " + e.getMessage());
                    quarantined++;
                }
            }
            if (!calendarBatch.isEmpty()) {
                txTemplate.executeWithoutResult(status -> calendarRepository.saveAll(calendarBatch));
            }
        } catch (Exception e) {
            log.error("Error processing calendar.txt: {}", e.getMessage());
        }
        return quarantined;
    }

    private java.time.LocalDate parseGtfsDate(String dateStr) {
        String clean = dateStr.trim();
        if (clean.length() == 8) {
            int y = Integer.parseInt(clean.substring(0, 4));
            int m = Integer.parseInt(clean.substring(4, 6));
            int d = Integer.parseInt(clean.substring(6, 8));
            return java.time.LocalDate.of(y, m, d);
        }
        return java.time.LocalDate.parse(clean);
    }

    private Integer parseBit(String val) {
        if (val == null) return 0;
        String c = val.trim();
        return ("1".equals(c) || "true".equalsIgnoreCase(c)) ? 1 : 0;
    }
}

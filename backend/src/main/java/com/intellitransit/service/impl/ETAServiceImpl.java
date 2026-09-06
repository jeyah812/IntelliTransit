package com.intellitransit.service.impl;

import com.intellitransit.dto.EstimatedArrivalDTO;
import com.intellitransit.dto.SimulatedVehicleDTO;
import com.intellitransit.entity.RouteStop;
import com.intellitransit.entity.Stop;
import com.intellitransit.entity.Trip;
import com.intellitransit.repository.RouteStopRepository;
import com.intellitransit.repository.StopRepository;
import com.intellitransit.repository.TripRepository;
import com.intellitransit.service.ETAService;
import com.intellitransit.service.TransitSimulationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ETAServiceImpl implements ETAService {

    private final TransitSimulationService simulationService;
    private final TripRepository tripRepository;
    private final RouteStopRepository routeStopRepository;
    private final StopRepository stopRepository;

    public ETAServiceImpl(TransitSimulationService simulationService,
                          TripRepository tripRepository,
                          RouteStopRepository routeStopRepository,
                          StopRepository stopRepository) {
        this.simulationService = simulationService;
        this.tripRepository = tripRepository;
        this.routeStopRepository = routeStopRepository;
        this.stopRepository = stopRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public EstimatedArrivalDTO calculateETA(Long tripId, Long destinationStopId) {
        if (tripId == null || destinationStopId == null) {
            throw new IllegalArgumentException("Trip ID and Destination Stop ID must not be null");
        }

        SimulatedVehicleDTO vehicle = simulationService.getSimulationStateByTripId(tripId);
        if (vehicle == null) {
            throw new IllegalArgumentException("Simulated vehicle not found for trip ID: " + tripId);
        }

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with ID: " + tripId));

        Stop destinationStop = stopRepository.findById(destinationStopId)
                .orElseThrow(() -> new IllegalArgumentException("Stop not found with ID: " + destinationStopId));

        Long routeId = vehicle.getRouteId();
        if (routeId == null && trip.getRoute() != null) {
            routeId = trip.getRoute().getId();
        }
        if (routeId == null) {
            throw new IllegalArgumentException("Route ID missing for trip ID: " + tripId);
        }

        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequenceAsc(routeId);
        if (routeStops.isEmpty()) {
            throw new IllegalArgumentException("No route stops found for route ID: " + routeId);
        }

        int targetIdx = -1;
        for (int i = 0; i < routeStops.size(); i++) {
            if (routeStops.get(i).getStop() != null && destinationStopId.equals(routeStops.get(i).getStop().getId())) {
                targetIdx = i;
                break;
            }
        }

        if (targetIdx == -1) {
            throw new IllegalArgumentException("Destination stop '" + destinationStop.getName() +
                    "' (ID: " + destinationStopId + ") does not belong to route ID: " + routeId);
        }

        LocalDateTime now = LocalDateTime.now();

        // If trip is completed
        if ("COMPLETED".equalsIgnoreCase(vehicle.getStatus())) {
            return EstimatedArrivalDTO.builder()
                    .tripId(tripId)
                    .gtfsTripId(vehicle.getGtfsTripId())
                    .busNumber(vehicle.getBusNumber())
                    .routeId(routeId)
                    .routeNumber(vehicle.getRouteNumber())
                    .routeName(vehicle.getRouteName())
                    .destinationStopId(destinationStopId)
                    .destinationStopName(destinationStop.getName())
                    .currentStopId(vehicle.getCurrentStopId())
                    .currentStopName(vehicle.getCurrentStopName())
                    .nextStopId(vehicle.getNextStopId())
                    .nextStopName(vehicle.getNextStopName())
                    .estimatedArrivalTime(now)
                    .etaSeconds(0L)
                    .currentDelaySeconds(vehicle.getDelaySeconds() != null ? vehicle.getDelaySeconds() : 0)
                    .confidence("LOW")
                    .calculatedAt(now)
                    .build();
        }

        // Determine current segment index in routeStops
        int numStops = routeStops.size();
        int numSegments = Math.max(1, numStops - 1);
        int currentSegIdx = 0;

        if (vehicle.getStopSequence() != null) {
            for (int i = 0; i < numStops; i++) {
                if (vehicle.getStopSequence().equals(routeStops.get(i).getStopSequence())) {
                    currentSegIdx = i;
                    break;
                }
            }
        }

        currentSegIdx = Math.min(numSegments - 1, Math.max(0, currentSegIdx));

        double progress = vehicle.getProgressBetweenStops() != null ? vehicle.getProgressBetweenStops() : 0.0;

        // Check if destination is behind current vehicle position
        if (targetIdx < currentSegIdx || (targetIdx == currentSegIdx && progress > 0.85)) {
            throw new IllegalArgumentException("Destination stop '" + destinationStop.getName() +
                    "' is behind vehicle's current position on trip " + tripId);
        }

        // Calculate planned segment times
        LocalDateTime schedStart = trip.getScheduledStart();
        LocalDateTime schedEnd = trip.getScheduledEnd();
        long totalPlannedSeconds = (schedStart != null && schedEnd != null)
                ? Math.max(600, ChronoUnit.SECONDS.between(schedStart, schedEnd))
                : 1800; // default 30 mins

        // Calculate segment time distribution using distances or equal splits
        double[] segmentDurations = new double[numSegments];
        double totalDist = 0.0;
        double firstDist = routeStops.get(0).getDistanceFromOriginKm() != null ? routeStops.get(0).getDistanceFromOriginKm().doubleValue() : 0.0;
        double lastDist = routeStops.get(numStops - 1).getDistanceFromOriginKm() != null ? routeStops.get(numStops - 1).getDistanceFromOriginKm().doubleValue() : 0.0;
        totalDist = lastDist - firstDist;

        boolean useDistance = totalDist > 0.1;

        for (int k = 0; k < numSegments; k++) {
            if (useDistance) {
                double d1 = routeStops.get(k).getDistanceFromOriginKm() != null ? routeStops.get(k).getDistanceFromOriginKm().doubleValue() : 0.0;
                double d2 = routeStops.get(k + 1).getDistanceFromOriginKm() != null ? routeStops.get(k + 1).getDistanceFromOriginKm().doubleValue() : d1;
                double segDist = Math.max(0.1, d2 - d1);
                segmentDurations[k] = totalPlannedSeconds * (segDist / totalDist);
            } else {
                segmentDurations[k] = (double) totalPlannedSeconds / numSegments;
            }
        }

        // Compute partial remaining time for current segment
        double remainingCurrentSegSeconds = (1.0 - progress) * segmentDurations[currentSegIdx];

        // Compute downstream segment times from (currentSegIdx + 1) to targetIdx
        double downstreamSeconds = 0.0;
        for (int k = currentSegIdx + 1; k < targetIdx; k++) {
            if (k < numSegments) {
                downstreamSeconds += segmentDurations[k];
            }
        }

        int delaySeconds = vehicle.getDelaySeconds() != null ? vehicle.getDelaySeconds() : 0;
        long totalEtaSeconds = Math.max(0, Math.round(remainingCurrentSegSeconds + downstreamSeconds + delaySeconds));
        LocalDateTime estimatedArrivalTime = now.plusSeconds(totalEtaSeconds);

        // Compute rule-based confidence
        String confidence;
        if (delaySeconds <= 60 && useDistance) {
            confidence = "HIGH";
        } else if (delaySeconds <= 180) {
            confidence = "MEDIUM";
        } else {
            confidence = "LOW";
        }

        return EstimatedArrivalDTO.builder()
                .tripId(tripId)
                .gtfsTripId(vehicle.getGtfsTripId())
                .busNumber(vehicle.getBusNumber())
                .routeId(routeId)
                .routeNumber(vehicle.getRouteNumber())
                .routeName(vehicle.getRouteName())
                .destinationStopId(destinationStopId)
                .destinationStopName(destinationStop.getName())
                .currentStopId(vehicle.getCurrentStopId())
                .currentStopName(vehicle.getCurrentStopName())
                .nextStopId(vehicle.getNextStopId())
                .nextStopName(vehicle.getNextStopName())
                .estimatedArrivalTime(estimatedArrivalTime)
                .etaSeconds(totalEtaSeconds)
                .currentDelaySeconds(delaySeconds)
                .confidence(confidence)
                .calculatedAt(now)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstimatedArrivalDTO> calculateETAsForRoute(Long routeId, Long destinationStopId) {
        if (routeId == null || destinationStopId == null) {
            return Collections.emptyList();
        }

        List<SimulatedVehicleDTO> vehicles = simulationService.getSimulationStateByRouteId(routeId);
        List<EstimatedArrivalDTO> etas = new ArrayList<>();

        for (SimulatedVehicleDTO vehicle : vehicles) {
            try {
                EstimatedArrivalDTO dto = calculateETA(vehicle.getTripId(), destinationStopId);
                if (dto != null) {
                    etas.add(dto);
                }
            } catch (IllegalArgumentException e) {
                // Ignore vehicles where destination is behind position or invalid for that trip
            }
        }

        return etas;
    }
}

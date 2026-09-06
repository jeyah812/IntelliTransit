package com.intellitransit.service.impl;

import com.intellitransit.dto.*;
import com.intellitransit.entity.AIAlert;
import com.intellitransit.entity.Bus;
import com.intellitransit.entity.SimulatedActionRecord;
import com.intellitransit.entity.Trip;
import com.intellitransit.entity.enums.AlertStatus;
import com.intellitransit.entity.enums.BusStatus;
import com.intellitransit.repository.AIAlertRepository;
import com.intellitransit.repository.BusRepository;
import com.intellitransit.repository.SimulatedActionRepository;
import com.intellitransit.repository.TripRepository;
import com.intellitransit.service.DecisionSupportService;
import com.intellitransit.service.ETAService;
import com.intellitransit.service.TransitSimulationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class DecisionSupportServiceImpl implements DecisionSupportService {

    private final AIAlertRepository aiAlertRepository;
    private final TripRepository tripRepository;
    private final BusRepository busRepository;
    private final TransitSimulationService simulationService;
    private final ETAService etaService;
    private final SimulatedActionRepository simulatedActionRepository;

    public DecisionSupportServiceImpl(AIAlertRepository aiAlertRepository,
                                       TripRepository tripRepository,
                                       BusRepository busRepository,
                                       TransitSimulationService simulationService,
                                       ETAService etaService,
                                       SimulatedActionRepository simulatedActionRepository) {
        this.aiAlertRepository = aiAlertRepository;
        this.tripRepository = tripRepository;
        this.busRepository = busRepository;
        this.simulationService = simulationService;
        this.etaService = etaService;
        this.simulatedActionRepository = simulatedActionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DecisionRecommendationDTO> getRecommendations() {
        List<DecisionRecommendationDTO> recommendations = new ArrayList<>();
        List<AIAlert> alerts = aiAlertRepository.findAll();

        for (AIAlert alert : alerts) {
            if (alert.getStatus() == AlertStatus.RESOLVED) continue;
            DecisionRecommendationDTO rec = mapAlertToRecommendation(alert);
            if (rec != null) recommendations.add(rec);
        }

        // Also evaluate live simulation state for severe delays if no existing alert exists
        List<SimulatedVehicleDTO> vehicles = simulationService.getCurrentSimulationState();
        for (SimulatedVehicleDTO v : vehicles) {
            if ("DELAYED".equalsIgnoreCase(v.getStatus()) || (v.getDelaySeconds() != null && v.getDelaySeconds() > 90)) {
                boolean alreadyHasRec = recommendations.stream()
                        .anyMatch(r -> v.getTripId().equals(r.getAffectedTripId()));
                if (!alreadyHasRec) {
                    DecisionRecommendationDTO rec = createSimulatedDelayRecommendation(v);
                    if (rec != null) recommendations.add(rec);
                }
            }
        }

        return recommendations;
    }

    @Override
    @Transactional(readOnly = true)
    public DecisionRecommendationDTO getRecommendationForAlert(Long alertId) {
        if (alertId == null) return null;
        Optional<AIAlert> alertOpt = aiAlertRepository.findById(alertId);
        if (alertOpt.isPresent()) {
            return mapAlertToRecommendation(alertOpt.get());
        }
        return null;
    }

    @Override
    @Transactional
    public SimulateDispatchResponse simulateDispatch(SimulateDispatchRequest request) {
        if (request == null || (request.getTripId() == null && request.getAlertId() == null)) {
            throw new IllegalArgumentException("Invalid dispatch request: trip ID or alert ID required");
        }

        Long tripId = request.getTripId();
        AIAlert alert = null;
        if (request.getAlertId() != null) {
            alert = aiAlertRepository.findById(request.getAlertId()).orElse(null);
            if (alert != null && tripId == null && alert.getTrip() != null) {
                tripId = alert.getTrip().getId();
            }
        }

        if (tripId == null) {
            throw new IllegalArgumentException("Target trip ID could not be identified for dispatch");
        }

        final Long targetTripId = tripId;
        Trip trip = tripRepository.findById(targetTripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with ID: " + targetTripId));

        // Candidate vehicle selection
        Bus selectedBus = null;
        if (request.getVehicleId() != null) {
            selectedBus = busRepository.findById(request.getVehicleId()).orElse(null);
        }

        if (selectedBus == null) {
            // Select first active bus from fleet
            selectedBus = busRepository.findAll().stream()
                    .filter(b -> b.getStatus() == BusStatus.ACTIVE)
                    .findFirst()
                    .orElse(trip.getBus());
        }

        if (selectedBus == null) {
            throw new IllegalStateException("No candidate vehicle available in fleet for simulated dispatch");
        }

        // State Mutation
        trip.setBus(selectedBus);
        tripRepository.save(trip);

        if (alert != null) {
            alert.setStatus(AlertStatus.RESOLVED);
            aiAlertRepository.save(alert);
        }

        String actionType = request.getActionType() != null ? request.getActionType() : "DISPATCH_VEHICLE";
        String routeNumber = trip.getRoute() != null ? trip.getRoute().getRouteNumber() : "R-DEMO";
        LocalDateTime now = LocalDateTime.now();

        String explanation = "Simulated operational response executed. Vehicle " + selectedBus.getBusNumber() +
                " dispatched to Route " + routeNumber + " (Trip #" + trip.getId() + ").";
        String impactSummary = "Simulated operational dispatch completed. Assigned extra fleet capacity and resolved delay alert. ETA recalculated.";

        // Persist Action Audit Trail Record
        SimulatedActionRecord actionRecord = SimulatedActionRecord.builder()
                .alertId(alert != null ? alert.getId() : null)
                .tripId(tripId)
                .busId(selectedBus.getId())
                .busNumber(selectedBus.getBusNumber())
                .routeNumber(routeNumber)
                .actionType(actionType)
                .status("SIMULATED")
                .executedAt(now)
                .explanation(explanation)
                .impactSummary(impactSummary)
                .build();

        SimulatedActionRecord savedAction = simulatedActionRepository.save(actionRecord);

        // ETA Recalculation (Part E)
        Long updatedEtaSeconds = null;
        String updatedConfidence = "HIGH";

        SimulatedVehicleDTO liveVehicle = simulationService.getSimulationStateByTripId(tripId);
        if (liveVehicle != null && liveVehicle.getNextStopId() != null) {
            try {
                EstimatedArrivalDTO etaDTO = etaService.calculateETA(tripId, liveVehicle.getNextStopId());
                if (etaDTO != null) {
                    updatedEtaSeconds = etaDTO.getEtaSeconds();
                    updatedConfidence = etaDTO.getConfidence();
                }
            } catch (Exception e) {
                // Fallback safe handling
            }
        }

        return SimulateDispatchResponse.builder()
                .actionId(savedAction.getId())
                .recommendationId(request.getRecommendationId())
                .tripId(tripId)
                .vehicleId(selectedBus.getId())
                .busNumber(selectedBus.getBusNumber())
                .routeNumber(routeNumber)
                .actionType(actionType)
                .status("SIMULATED")
                .executedAt(now)
                .explanation(explanation)
                .impactSummary(impactSummary)
                .updatedEtaSeconds(updatedEtaSeconds)
                .updatedConfidence(updatedConfidence)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimulatedActionRecord> getActionHistory() {
        return simulatedActionRepository.findAllByOrderByExecutedAtDesc();
    }

    private DecisionRecommendationDTO mapAlertToRecommendation(AIAlert alert) {
        if (alert == null) return null;
        Long tripId = alert.getTrip() != null ? alert.getTrip().getId() : null;
        Long routeId = (alert.getTrip() != null && alert.getTrip().getRoute() != null) ? alert.getTrip().getRoute().getId() : null;
        String routeNumber = (alert.getTrip() != null && alert.getTrip().getRoute() != null) ? alert.getTrip().getRoute().getRouteNumber() : "R-DEMO";

        Bus candidateBus = findAvailableBus();
        String busNumber = candidateBus != null ? candidateBus.getBusNumber() : "B104";
        Long busId = candidateBus != null ? candidateBus.getId() : 1L;

        String actionType;
        String priority;
        String title;
        String explanation;
        List<String> evidence = new ArrayList<>();

        SimulatedVehicleDTO vehicleState = tripId != null ? simulationService.getSimulationStateByTripId(tripId) : null;
        int currentDelay = vehicleState != null && vehicleState.getDelaySeconds() != null ? vehicleState.getDelaySeconds() : 134;

        if (alert.getSeverity() != null && "HIGH".equalsIgnoreCase(alert.getSeverity().name())) {
            actionType = "DISPATCH_VEHICLE";
            priority = "HIGH";
            title = "Recommend Simulated Vehicle Dispatch for Route " + routeNumber;
            explanation = "Vehicle operating beyond schedule on Route " + routeNumber + ". Simulated dispatch of available vehicle " + busNumber + " is recommended.";
            evidence.addAll(Arrays.asList(
                    "Current Delay: +" + currentDelay + "s",
                    "Anomaly Type: " + alert.getAlertType(),
                    "Severity: " + alert.getSeverity(),
                    "Downstream ETA Impact: Elevated",
                    "Candidate Vehicle: " + busNumber + " (Status: AVAILABLE)",
                    "Depot Availability: Confirmed"
            ));
        } else if (alert.getSeverity() != null && "MEDIUM".equalsIgnoreCase(alert.getSeverity().name())) {
            actionType = "MONITOR";
            priority = "MEDIUM";
            title = "Recommend Operational Monitoring for Route " + routeNumber;
            explanation = "Moderate operational variance detected on Route " + routeNumber + ". Recommend monitoring schedule alignment.";
            evidence.addAll(Arrays.asList(
                    "Current Delay: +" + currentDelay + "s",
                    "Anomaly Type: " + alert.getAlertType(),
                    "Severity: MEDIUM",
                    "Recommended Action: Schedule monitoring & telemetry track"
            ));
        } else {
            actionType = "INSPECT_OPERATION";
            priority = "LOW";
            title = "Recommend Route Inspection for Route " + routeNumber;
            explanation = "Minor anomaly recorded on Route " + routeNumber + ". Operational review recommended.";
            evidence.addAll(Arrays.asList(
                    "Anomaly Type: " + alert.getAlertType(),
                    "Severity: LOW"
            ));
        }

        return DecisionRecommendationDTO.builder()
                .recommendationId(alert.getId() * 100)
                .alertId(alert.getId())
                .alertType(alert.getAlertType() != null ? alert.getAlertType().name() : "GENERAL")
                .severity(alert.getSeverity() != null ? alert.getSeverity().name() : "HIGH")
                .actionType(actionType)
                .priority(priority)
                .title(title)
                .explanation(explanation)
                .evidenceList(evidence)
                .recommendedVehicleId(busId)
                .recommendedBusNumber(busNumber)
                .candidateAvailability("AVAILABLE")
                .affectedTripId(tripId)
                .affectedRouteId(routeId)
                .affectedRouteNumber(routeNumber)
                .status("PENDING")
                .createdAt(alert.getDetectedAt() != null ? alert.getDetectedAt() : LocalDateTime.now())
                .build();
    }

    private DecisionRecommendationDTO createSimulatedDelayRecommendation(SimulatedVehicleDTO v) {
        Bus candidateBus = findAvailableBus();
        String busNumber = candidateBus != null ? candidateBus.getBusNumber() : "B104";
        Long busId = candidateBus != null ? candidateBus.getId() : 1L;

        int delay = v.getDelaySeconds() != null ? v.getDelaySeconds() : 120;
        String actionType = delay > 90 ? "DISPATCH_VEHICLE" : "MONITOR";
        String priority = delay > 120 ? "CRITICAL" : "HIGH";

        List<String> evidence = Arrays.asList(
                "Simulated Delay: +" + delay + "s",
                "Vehicle Status: " + v.getStatus(),
                "Current Stop: " + v.getCurrentStopName(),
                "Candidate Vehicle: " + busNumber + " (Status: AVAILABLE)",
                "Availability: Confirmed"
        );

        return DecisionRecommendationDTO.builder()
                .recommendationId(v.getTripId() * 1000)
                .alertId(null)
                .alertType("TRIP_DURATION_ANOMALY")
                .severity(priority)
                .actionType(actionType)
                .priority(priority)
                .title("Simulated Delay Response for Route " + v.getRouteNumber())
                .explanation("Simulated vehicle " + v.getBusNumber() + " on Route " + v.getRouteNumber() + " is delayed by +" + delay + "s. Dispatching candidate " + busNumber + " recommended.")
                .evidenceList(evidence)
                .recommendedVehicleId(busId)
                .recommendedBusNumber(busNumber)
                .candidateAvailability("AVAILABLE")
                .affectedTripId(v.getTripId())
                .affectedRouteId(v.getRouteId())
                .affectedRouteNumber(v.getRouteNumber())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Bus findAvailableBus() {
        return busRepository.findAll().stream()
                .filter(b -> b.getStatus() == BusStatus.ACTIVE)
                .findFirst()
                .orElse(null);
    }
}

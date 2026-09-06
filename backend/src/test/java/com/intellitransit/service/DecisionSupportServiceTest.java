package com.intellitransit.service;

import com.intellitransit.dto.DecisionRecommendationDTO;
import com.intellitransit.dto.EstimatedArrivalDTO;
import com.intellitransit.dto.SimulateDispatchRequest;
import com.intellitransit.dto.SimulateDispatchResponse;
import com.intellitransit.dto.SimulatedVehicleDTO;
import com.intellitransit.entity.AIAlert;
import com.intellitransit.entity.Bus;
import com.intellitransit.entity.Route;
import com.intellitransit.entity.SimulatedActionRecord;
import com.intellitransit.entity.Trip;
import com.intellitransit.entity.enums.AlertSeverity;
import com.intellitransit.entity.enums.AlertStatus;
import com.intellitransit.entity.enums.AlertType;
import com.intellitransit.entity.enums.BusStatus;
import com.intellitransit.repository.AIAlertRepository;
import com.intellitransit.repository.BusRepository;
import com.intellitransit.repository.SimulatedActionRepository;
import com.intellitransit.repository.TripRepository;
import com.intellitransit.service.impl.DecisionSupportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DecisionSupportServiceTest {

    @Mock
    private AIAlertRepository aiAlertRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private TransitSimulationService simulationService;

    @Mock
    private ETAService etaService;

    @Mock
    private SimulatedActionRepository simulatedActionRepository;

    private DecisionSupportServiceImpl decisionSupportService;

    private Trip testTrip;
    private Route testRoute;
    private Bus testBusCandidate;
    private AIAlert highDelayAlert;

    @BeforeEach
    void setUp() {
        decisionSupportService = new DecisionSupportServiceImpl(
                aiAlertRepository, tripRepository, busRepository,
                simulationService, etaService, simulatedActionRepository
        );

        testRoute = Route.builder().id(10L).routeNumber("570").routeName("CMBT to Siruseri").build();
        testTrip = Trip.builder().id(100L).gtfsTripId("GTFS-100").route(testRoute).build();

        testBusCandidate = Bus.builder()
                .id(50L)
                .busNumber("B104")
                .registrationNumber("TN-01-N-9999")
                .status(BusStatus.ACTIVE)
                .build();

        highDelayAlert = AIAlert.builder()
                .id(1L)
                .trip(testTrip)
                .alertType(AlertType.TRIP_DURATION_ANOMALY)
                .severity(AlertSeverity.HIGH)
                .anomalyScore(BigDecimal.valueOf(1.75))
                .explanation("Trip duration exceeded expected threshold")
                .status(AlertStatus.NEW)
                .detectedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testHighDelayGeneratesDispatchRecommendation() {
        when(aiAlertRepository.findAll()).thenReturn(Collections.singletonList(highDelayAlert));
        when(busRepository.findAll()).thenReturn(Collections.singletonList(testBusCandidate));
        when(simulationService.getCurrentSimulationState()).thenReturn(Collections.emptyList());

        List<DecisionRecommendationDTO> recs = decisionSupportService.getRecommendations();

        assertNotNull(recs);
        assertEquals(1, recs.size());

        DecisionRecommendationDTO rec = recs.get(0);
        assertEquals("DISPATCH_VEHICLE", rec.getActionType());
        assertEquals("HIGH", rec.getPriority());
        assertEquals("B104", rec.getRecommendedBusNumber());
        assertNotNull(rec.getEvidenceList());
        assertTrue(rec.getEvidenceList().size() >= 3);
    }

    @Test
    void testMediumDelayGeneratesMonitoringRecommendation() {
        AIAlert mediumAlert = AIAlert.builder()
                .id(2L)
                .trip(testTrip)
                .alertType(AlertType.DEMAND_ANOMALY)
                .severity(AlertSeverity.MEDIUM)
                .anomalyScore(BigDecimal.valueOf(1.20))
                .status(AlertStatus.NEW)
                .build();

        when(aiAlertRepository.findAll()).thenReturn(Collections.singletonList(mediumAlert));
        when(busRepository.findAll()).thenReturn(Collections.singletonList(testBusCandidate));
        when(simulationService.getCurrentSimulationState()).thenReturn(Collections.emptyList());

        List<DecisionRecommendationDTO> recs = decisionSupportService.getRecommendations();

        assertEquals(1, recs.size());
        assertEquals("MONITOR", recs.get(0).getActionType());
        assertEquals("MEDIUM", recs.get(0).getPriority());
    }

    @Test
    void testCandidateVehicleSelectionIsDeterministic() {
        Bus bus1 = Bus.builder().id(1L).busNumber("B101").status(BusStatus.MAINTENANCE).build();
        Bus bus2 = Bus.builder().id(2L).busNumber("B104").status(BusStatus.ACTIVE).build();

        when(aiAlertRepository.findAll()).thenReturn(Collections.singletonList(highDelayAlert));
        when(busRepository.findAll()).thenReturn(Arrays.asList(bus1, bus2));
        when(simulationService.getCurrentSimulationState()).thenReturn(Collections.emptyList());

        List<DecisionRecommendationDTO> recs = decisionSupportService.getRecommendations();

        assertEquals(1, recs.size());
        assertEquals("B104", recs.get(0).getRecommendedBusNumber());
        assertEquals(2L, recs.get(0).getRecommendedVehicleId());
    }

    @Test
    void testSimulatedDispatchSucceedsAndMutatesState() {
        SimulateDispatchRequest request = new SimulateDispatchRequest(100L, 1L, 100L, 50L, "DISPATCH_VEHICLE", "Emergency dispatch");

        when(tripRepository.findById(100L)).thenReturn(Optional.of(testTrip));
        when(aiAlertRepository.findById(1L)).thenReturn(Optional.of(highDelayAlert));
        when(busRepository.findById(50L)).thenReturn(Optional.of(testBusCandidate));

        SimulatedActionRecord savedRecord = SimulatedActionRecord.builder()
                .id(500L)
                .alertId(1L)
                .tripId(100L)
                .busId(50L)
                .busNumber("B104")
                .routeNumber("570")
                .actionType("DISPATCH_VEHICLE")
                .status("SIMULATED")
                .executedAt(LocalDateTime.now())
                .explanation("Simulated dispatch executed")
                .impactSummary("Delay reduced")
                .build();

        when(simulatedActionRepository.save(any(SimulatedActionRecord.class))).thenReturn(savedRecord);

        SimulatedVehicleDTO vehicleDTO = SimulatedVehicleDTO.builder()
                .tripId(100L)
                .nextStopId(335L)
                .status("EN_ROUTE")
                .build();
        when(simulationService.getSimulationStateByTripId(100L)).thenReturn(vehicleDTO);

        EstimatedArrivalDTO etaDTO = EstimatedArrivalDTO.builder()
                .tripId(100L)
                .etaSeconds(240L)
                .confidence("HIGH")
                .build();
        when(etaService.calculateETA(100L, 335L)).thenReturn(etaDTO);

        SimulateDispatchResponse response = decisionSupportService.simulateDispatch(request);

        assertNotNull(response);
        assertEquals(500L, response.getActionId());
        assertEquals("SIMULATED", response.getStatus());
        assertEquals("B104", response.getBusNumber());
        assertEquals(240L, response.getUpdatedEtaSeconds());
        assertEquals("HIGH", response.getUpdatedConfidence());

        // Verify state mutations
        assertEquals(testBusCandidate, testTrip.getBus());
        assertEquals(AlertStatus.RESOLVED, highDelayAlert.getStatus());
        verify(simulatedActionRepository, times(1)).save(any(SimulatedActionRecord.class));
    }

    @Test
    void testInvalidDispatchThrowsException() {
        SimulateDispatchRequest invalidReq = new SimulateDispatchRequest(null, null, null, null, "DISPATCH_VEHICLE", null);

        assertThrows(IllegalArgumentException.class, () -> decisionSupportService.simulateDispatch(invalidReq));
    }

    @Test
    void testActionHistoryRetrieval() {
        SimulatedActionRecord record = SimulatedActionRecord.builder().id(10L).actionType("DISPATCH_VEHICLE").status("SIMULATED").build();
        when(simulatedActionRepository.findAllByOrderByExecutedAtDesc()).thenReturn(Collections.singletonList(record));

        List<SimulatedActionRecord> history = decisionSupportService.getActionHistory();

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals("DISPATCH_VEHICLE", history.get(0).getActionType());
    }
}

package com.intellitransit.service;

import com.intellitransit.dto.EstimatedArrivalDTO;
import com.intellitransit.dto.SimulatedVehicleDTO;
import com.intellitransit.entity.Route;
import com.intellitransit.entity.RouteStop;
import com.intellitransit.entity.Stop;
import com.intellitransit.entity.Trip;
import com.intellitransit.repository.RouteStopRepository;
import com.intellitransit.repository.StopRepository;
import com.intellitransit.repository.TripRepository;
import com.intellitransit.service.impl.ETAServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ETAServiceTest {

    @Mock
    private TransitSimulationService simulationService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private RouteStopRepository routeStopRepository;

    @Mock
    private StopRepository stopRepository;

    private ETAServiceImpl etaService;

    private Trip testTrip;
    private Route testRoute;
    private Stop stopA, stopB, stopC, stopD;
    private RouteStop rsA, rsB, rsC, rsD;

    @BeforeEach
    void setUp() {
        etaService = new ETAServiceImpl(simulationService, tripRepository, routeStopRepository, stopRepository);

        testRoute = Route.builder()
                .id(10L)
                .routeNumber("570")
                .routeName("CMBT to Siruseri")
                .active(true)
                .build();

        testTrip = Trip.builder()
                .id(100L)
                .gtfsTripId("GTFS-100")
                .route(testRoute)
                .scheduledStart(LocalDateTime.now().minusMinutes(10))
                .scheduledEnd(LocalDateTime.now().plusMinutes(20)) // 30 min total planned
                .build();

        stopA = Stop.builder().id(1L).name("Stop A").latitude(BigDecimal.valueOf(13.01)).longitude(BigDecimal.valueOf(80.20)).build();
        stopB = Stop.builder().id(2L).name("Stop B").latitude(BigDecimal.valueOf(13.02)).longitude(BigDecimal.valueOf(80.21)).build();
        stopC = Stop.builder().id(3L).name("Stop C").latitude(BigDecimal.valueOf(13.03)).longitude(BigDecimal.valueOf(80.22)).build();
        stopD = Stop.builder().id(4L).name("Stop D").latitude(BigDecimal.valueOf(13.04)).longitude(BigDecimal.valueOf(80.23)).build();

        rsA = RouteStop.builder().id(101L).route(testRoute).stop(stopA).stopSequence(1).distanceFromOriginKm(BigDecimal.valueOf(0.0)).build();
        rsB = RouteStop.builder().id(102L).route(testRoute).stop(stopB).stopSequence(2).distanceFromOriginKm(BigDecimal.valueOf(5.0)).build();
        rsC = RouteStop.builder().id(103L).route(testRoute).stop(stopC).stopSequence(3).distanceFromOriginKm(BigDecimal.valueOf(10.0)).build();
        rsD = RouteStop.builder().id(104L).route(testRoute).stop(stopD).stopSequence(4).distanceFromOriginKm(BigDecimal.valueOf(15.0)).build();
    }

    @Test
    void testETAAtFirstStop() {
        SimulatedVehicleDTO vehicle = SimulatedVehicleDTO.builder()
                .tripId(100L)
                .routeId(10L)
                .routeNumber("570")
                .currentStopId(1L)
                .currentStopName("Stop A")
                .nextStopId(2L)
                .nextStopName("Stop B")
                .stopSequence(1)
                .progressBetweenStops(0.0)
                .status("AT_STOP")
                .delaySeconds(0)
                .build();

        when(simulationService.getSimulationStateByTripId(100L)).thenReturn(vehicle);
        when(tripRepository.findById(100L)).thenReturn(Optional.of(testTrip));
        when(stopRepository.findById(4L)).thenReturn(Optional.of(stopD));
        when(routeStopRepository.findByRouteIdOrderByStopSequenceAsc(10L)).thenReturn(Arrays.asList(rsA, rsB, rsC, rsD));

        EstimatedArrivalDTO dto = etaService.calculateETA(100L, 4L);

        assertNotNull(dto);
        assertEquals(100L, dto.getTripId());
        assertEquals(4L, dto.getDestinationStopId());
        assertEquals("Stop D", dto.getDestinationStopName());
        // 30 mins total planned = 1800s for 15km (600s per 5km segment). At progress 0, ETA to Stop D (15km) = 1800s
        assertEquals(1800L, dto.getEtaSeconds());
        assertEquals("HIGH", dto.getConfidence());
    }

    @Test
    void testETABetweenStopsPartialSegment() {
        // Vehicle between Stop B (seq 2) and Stop C (seq 3), 40% complete
        SimulatedVehicleDTO vehicle = SimulatedVehicleDTO.builder()
                .tripId(100L)
                .routeId(10L)
                .routeNumber("570")
                .currentStopId(2L)
                .currentStopName("Stop B")
                .nextStopId(3L)
                .nextStopName("Stop C")
                .stopSequence(2)
                .progressBetweenStops(0.40)
                .status("EN_ROUTE")
                .delaySeconds(30)
                .build();

        when(simulationService.getSimulationStateByTripId(100L)).thenReturn(vehicle);
        when(tripRepository.findById(100L)).thenReturn(Optional.of(testTrip));
        when(stopRepository.findById(4L)).thenReturn(Optional.of(stopD));
        when(routeStopRepository.findByRouteIdOrderByStopSequenceAsc(10L)).thenReturn(Arrays.asList(rsA, rsB, rsC, rsD));

        EstimatedArrivalDTO dto = etaService.calculateETA(100L, 4L);

        assertNotNull(dto);
        // Each segment is 600s (5km out of 15km).
        // Remaining segment B->C: (1 - 0.40) * 600 = 360s.
        // Downstream segment C->D: 600s.
        // Delay: 30s.
        // Total ETA = 360 + 600 + 30 = 990s.
        assertEquals(990L, dto.getEtaSeconds());
        assertEquals("HIGH", dto.getConfidence());
    }

    @Test
    void testETADecreasesAsProgressIncreases() {
        SimulatedVehicleDTO vehicleAt30 = SimulatedVehicleDTO.builder()
                .tripId(100L)
                .routeId(10L)
                .stopSequence(2)
                .progressBetweenStops(0.30)
                .status("EN_ROUTE")
                .delaySeconds(0)
                .build();

        SimulatedVehicleDTO vehicleAt65 = SimulatedVehicleDTO.builder()
                .tripId(100L)
                .routeId(10L)
                .stopSequence(2)
                .progressBetweenStops(0.65)
                .status("EN_ROUTE")
                .delaySeconds(0)
                .build();

        when(tripRepository.findById(100L)).thenReturn(Optional.of(testTrip));
        when(stopRepository.findById(4L)).thenReturn(Optional.of(stopD));
        when(routeStopRepository.findByRouteIdOrderByStopSequenceAsc(10L)).thenReturn(Arrays.asList(rsA, rsB, rsC, rsD));

        when(simulationService.getSimulationStateByTripId(100L)).thenReturn(vehicleAt30);
        EstimatedArrivalDTO dto1 = etaService.calculateETA(100L, 4L);

        when(simulationService.getSimulationStateByTripId(100L)).thenReturn(vehicleAt65);
        EstimatedArrivalDTO dto2 = etaService.calculateETA(100L, 4L);

        assertTrue(dto2.getEtaSeconds() < dto1.getEtaSeconds(), "ETA should decrease as progress increases");
    }

    @Test
    void testETAIncludesSimulatedDelay() {
        SimulatedVehicleDTO vehicleNoDelay = SimulatedVehicleDTO.builder()
                .tripId(100L)
                .routeId(10L)
                .stopSequence(2)
                .progressBetweenStops(0.50)
                .status("EN_ROUTE")
                .delaySeconds(0)
                .build();

        SimulatedVehicleDTO vehicleWithDelay = SimulatedVehicleDTO.builder()
                .tripId(100L)
                .routeId(10L)
                .stopSequence(2)
                .progressBetweenStops(0.50)
                .status("EN_ROUTE")
                .delaySeconds(120)
                .build();

        when(tripRepository.findById(100L)).thenReturn(Optional.of(testTrip));
        when(stopRepository.findById(4L)).thenReturn(Optional.of(stopD));
        when(routeStopRepository.findByRouteIdOrderByStopSequenceAsc(10L)).thenReturn(Arrays.asList(rsA, rsB, rsC, rsD));

        when(simulationService.getSimulationStateByTripId(100L)).thenReturn(vehicleNoDelay);
        EstimatedArrivalDTO dto1 = etaService.calculateETA(100L, 4L);

        when(simulationService.getSimulationStateByTripId(100L)).thenReturn(vehicleWithDelay);
        EstimatedArrivalDTO dto2 = etaService.calculateETA(100L, 4L);

        assertEquals(dto1.getEtaSeconds() + 120L, dto2.getEtaSeconds());
        assertEquals("MEDIUM", dto2.getConfidence());
    }

    @Test
    void testDestinationBehindVehicleThrowsException() {
        // Vehicle at Stop C (seq 3), destination requested is Stop A (seq 1)
        SimulatedVehicleDTO vehicle = SimulatedVehicleDTO.builder()
                .tripId(100L)
                .routeId(10L)
                .stopSequence(3)
                .progressBetweenStops(0.20)
                .status("EN_ROUTE")
                .delaySeconds(0)
                .build();

        when(simulationService.getSimulationStateByTripId(100L)).thenReturn(vehicle);
        when(tripRepository.findById(100L)).thenReturn(Optional.of(testTrip));
        when(stopRepository.findById(1L)).thenReturn(Optional.of(stopA));
        when(routeStopRepository.findByRouteIdOrderByStopSequenceAsc(10L)).thenReturn(Arrays.asList(rsA, rsB, rsC, rsD));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> etaService.calculateETA(100L, 1L));
        assertTrue(ex.getMessage().contains("behind vehicle"));
    }

    @Test
    void testDestinationNotOnRouteThrowsException() {
        Stop stopOther = Stop.builder().id(999L).name("Other Stop").build();

        SimulatedVehicleDTO vehicle = SimulatedVehicleDTO.builder()
                .tripId(100L)
                .routeId(10L)
                .stopSequence(1)
                .progressBetweenStops(0.0)
                .status("EN_ROUTE")
                .delaySeconds(0)
                .build();

        when(simulationService.getSimulationStateByTripId(100L)).thenReturn(vehicle);
        when(tripRepository.findById(100L)).thenReturn(Optional.of(testTrip));
        when(stopRepository.findById(999L)).thenReturn(Optional.of(stopOther));
        when(routeStopRepository.findByRouteIdOrderByStopSequenceAsc(10L)).thenReturn(Arrays.asList(rsA, rsB, rsC, rsD));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> etaService.calculateETA(100L, 999L));
        assertTrue(ex.getMessage().contains("does not belong to route"));
    }

    @Test
    void testCompletedTripHandledSafely() {
        SimulatedVehicleDTO vehicle = SimulatedVehicleDTO.builder()
                .tripId(100L)
                .routeId(10L)
                .stopSequence(4)
                .progressBetweenStops(1.0)
                .status("COMPLETED")
                .delaySeconds(0)
                .build();

        when(simulationService.getSimulationStateByTripId(100L)).thenReturn(vehicle);
        when(tripRepository.findById(100L)).thenReturn(Optional.of(testTrip));
        when(stopRepository.findById(4L)).thenReturn(Optional.of(stopD));
        when(routeStopRepository.findByRouteIdOrderByStopSequenceAsc(10L)).thenReturn(Arrays.asList(rsA, rsB, rsC, rsD));

        EstimatedArrivalDTO dto = etaService.calculateETA(100L, 4L);

        assertNotNull(dto);
        assertEquals(0L, dto.getEtaSeconds());
        assertEquals("LOW", dto.getConfidence());
    }

    @Test
    void testConfidenceClassification() {
        SimulatedVehicleDTO lowConfVehicle = SimulatedVehicleDTO.builder()
                .tripId(100L)
                .routeId(10L)
                .stopSequence(1)
                .progressBetweenStops(0.0)
                .status("EN_ROUTE")
                .delaySeconds(250) // > 180s delay
                .build();

        when(simulationService.getSimulationStateByTripId(100L)).thenReturn(lowConfVehicle);
        when(tripRepository.findById(100L)).thenReturn(Optional.of(testTrip));
        when(stopRepository.findById(4L)).thenReturn(Optional.of(stopD));
        when(routeStopRepository.findByRouteIdOrderByStopSequenceAsc(10L)).thenReturn(Arrays.asList(rsA, rsB, rsC, rsD));

        EstimatedArrivalDTO dto = etaService.calculateETA(100L, 4L);
        assertEquals("LOW", dto.getConfidence());
    }
}

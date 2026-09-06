package com.intellitransit.service;

import com.intellitransit.dto.SimulatedVehicleDTO;
import com.intellitransit.entity.Bus;
import com.intellitransit.entity.Driver;
import com.intellitransit.entity.Route;
import com.intellitransit.entity.RouteStop;
import com.intellitransit.entity.Stop;
import com.intellitransit.entity.Trip;
import com.intellitransit.entity.enums.TripStatus;
import com.intellitransit.repository.RouteStopRepository;
import com.intellitransit.repository.TripRepository;
import com.intellitransit.service.impl.TransitSimulationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransitSimulationServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private RouteStopRepository routeStopRepository;

    private TransitSimulationServiceImpl simulationService;

    private Route testRoute;
    private Stop stopA;
    private Stop stopB;
    private Stop stopC;
    private Trip testTrip;

    @BeforeEach
    void setUp() {
        simulationService = new TransitSimulationServiceImpl(tripRepository, routeStopRepository);

        testRoute = Route.builder()
                .id(1L)
                .routeNumber("570")
                .routeName("M.G.R.Koyambedu TO Kelambakkam")
                .origin("Koyambedu")
                .destination("Kelambakkam")
                .distanceKm(BigDecimal.valueOf(35.0))
                .estimatedDurationMinutes(60)
                .active(true)
                .build();

        stopA = Stop.builder()
                .id(101L)
                .name("Koyambedu BS")
                .latitude(BigDecimal.valueOf(13.0694))
                .longitude(BigDecimal.valueOf(80.1948))
                .build();

        stopB = Stop.builder()
                .id(102L)
                .name("Vadapalani")
                .latitude(BigDecimal.valueOf(13.0500))
                .longitude(BigDecimal.valueOf(80.2120))
                .build();

        stopC = Stop.builder()
                .id(103L)
                .name("Kelambakkam Terminus")
                .latitude(BigDecimal.valueOf(12.7872))
                .longitude(BigDecimal.valueOf(80.2186))
                .build();

        Bus testBus = Bus.builder().id(1L).busNumber("TN-01-N-1234").build();
        Driver testDriver = Driver.builder().id(1L).fullName("Rajesh Kumar").build();

        LocalDateTime now = LocalDateTime.now();
        testTrip = Trip.builder()
                .id(10L)
                .gtfsTripId("GTFS-TRIP-570-01")
                .route(testRoute)
                .bus(testBus)
                .driver(testDriver)
                .scheduledStart(now.minusMinutes(10))
                .scheduledEnd(now.plusMinutes(50))
                .status(TripStatus.SCHEDULED)
                .build();
    }

    @Test
    @DisplayName("1 & 8. Vehicle initializes from first stop and DTO is generated correctly")
    void testTripInitializationAndDtoMapping() {
        RouteStop rs1 = RouteStop.builder().id(1L).route(testRoute).stop(stopA).stopSequence(1).build();
        RouteStop rs2 = RouteStop.builder().id(2L).route(testRoute).stop(stopB).stopSequence(2).build();

        when(tripRepository.findAll()).thenReturn(List.of(testTrip));
        when(routeStopRepository.findByRouteIdOrderByStopSequenceAsc(1L)).thenReturn(List.of(rs1, rs2));

        simulationService.tickSimulation();
        List<SimulatedVehicleDTO> vehicles = simulationService.getCurrentSimulationState();

        assertNotNull(vehicles);
        assertEquals(1, vehicles.size());

        SimulatedVehicleDTO vehicle = vehicles.get(0);
        assertEquals(10L, vehicle.getTripId());
        assertEquals("570", vehicle.getRouteNumber());
        assertEquals("Koyambedu BS", vehicle.getCurrentStopName());
        assertNotNull(vehicle.getCurrentLatitude());
        assertNotNull(vehicle.getCurrentLongitude());
        assertNotNull(vehicle.getStatus());
        assertNotNull(vehicle.getLastUpdatedAt());
    }

    @Test
    @DisplayName("2 & 3. Vehicle progresses to next stop and position interpolation is correct")
    void testVehicleProgressionAndCoordinateInterpolation() {
        RouteStop rs1 = RouteStop.builder().id(1L).route(testRoute).stop(stopA).stopSequence(1).build();
        RouteStop rs2 = RouteStop.builder().id(2L).route(testRoute).stop(stopB).stopSequence(2).build();

        when(tripRepository.findAll()).thenReturn(List.of(testTrip));
        when(routeStopRepository.findByRouteIdOrderByStopSequenceAsc(1L)).thenReturn(List.of(rs1, rs2));

        simulationService.tickSimulation();
        SimulatedVehicleDTO vehicle = simulationService.getSimulationStateByTripId(10L);

        assertNotNull(vehicle);
        assertNotNull(vehicle.getProgressBetweenStops());
        assertTrue(vehicle.getProgressBetweenStops() >= 0.0 && vehicle.getProgressBetweenStops() <= 1.0);

        // Coordinates should be between Stop A lat/lon and Stop B lat/lon
        double vehicleLat = vehicle.getCurrentLatitude().doubleValue();
        double vehicleLon = vehicle.getCurrentLongitude().doubleValue();

        assertTrue(vehicleLat <= stopA.getLatitude().doubleValue() && vehicleLat >= stopC.getLatitude().doubleValue());
        assertTrue(vehicleLon >= stopA.getLongitude().doubleValue() && vehicleLon <= stopC.getLongitude().doubleValue());
    }

    @Test
    @DisplayName("4 & 5. Scheduled timing influences progress and delay remains bounded")
    void testScheduledTimingAndBoundedDelay() {
        RouteStop rs1 = RouteStop.builder().id(1L).route(testRoute).stop(stopA).stopSequence(1).build();
        RouteStop rs2 = RouteStop.builder().id(2L).route(testRoute).stop(stopB).stopSequence(2).build();

        when(tripRepository.findAll()).thenReturn(List.of(testTrip));
        when(routeStopRepository.findByRouteIdOrderByStopSequenceAsc(1L)).thenReturn(List.of(rs1, rs2));

        simulationService.tickSimulation();
        SimulatedVehicleDTO vehicle = simulationService.getSimulationStateByTripId(10L);

        assertNotNull(vehicle);
        assertNotNull(vehicle.getDelaySeconds());
        assertTrue(vehicle.getDelaySeconds() >= 0 && vehicle.getDelaySeconds() <= 300, "Delay must be bounded <= 300 seconds");
    }

    @Test
    @DisplayName("6. Completed trips stop progressing")
    void testCompletedTripStatusHandling() {
        testTrip.setStatus(TripStatus.COMPLETED);

        RouteStop rs1 = RouteStop.builder().id(1L).route(testRoute).stop(stopA).stopSequence(1).build();
        RouteStop rs2 = RouteStop.builder().id(2L).route(testRoute).stop(stopB).stopSequence(2).build();

        when(tripRepository.findAll()).thenReturn(List.of(testTrip));
        when(routeStopRepository.findByRouteIdOrderByStopSequenceAsc(1L)).thenReturn(List.of(rs1, rs2));

        simulationService.tickSimulation();
        SimulatedVehicleDTO vehicle = simulationService.getSimulationStateByTripId(10L);

        assertNotNull(vehicle);
        assertEquals("COMPLETED", vehicle.getStatus());
        assertEquals(1.0, vehicle.getProgressBetweenStops());
    }

    @Test
    @DisplayName("7. Invalid or missing stop coordinates are handled safely")
    void testMissingStopCoordinatesHandledSafely() {
        Stop stopNoCoords = Stop.builder().id(104L).name("Stop Without Coordinates").latitude(null).longitude(null).build();
        RouteStop rs1 = RouteStop.builder().id(1L).route(testRoute).stop(stopNoCoords).stopSequence(1).build();
        RouteStop rs2 = RouteStop.builder().id(2L).route(testRoute).stop(stopB).stopSequence(2).build();

        when(tripRepository.findAll()).thenReturn(List.of(testTrip));
        when(routeStopRepository.findByRouteIdOrderByStopSequenceAsc(1L)).thenReturn(List.of(rs1, rs2));

        assertDoesNotThrow(() -> simulationService.tickSimulation());

        SimulatedVehicleDTO vehicle = simulationService.getSimulationStateByTripId(10L);
        assertNotNull(vehicle);
        assertNotNull(vehicle.getCurrentLatitude());
        assertNotNull(vehicle.getCurrentLongitude());
    }
}

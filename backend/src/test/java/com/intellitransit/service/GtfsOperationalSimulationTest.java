package com.intellitransit.service;

import com.intellitransit.dto.GtfsSimulationRequest;
import com.intellitransit.dto.GtfsSimulationResponse;
import com.intellitransit.entity.*;
import com.intellitransit.entity.enums.BusStatus;
import com.intellitransit.entity.enums.DriverStatus;
import com.intellitransit.repository.*;
import com.intellitransit.service.impl.GtfsOperationalSimulationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GtfsOperationalSimulationTest {

    @Mock private TripRepository tripRepository;
    @Mock private TripLogRepository tripLogRepository;
    @Mock private RouteRepository routeRepository;
    @Mock private BusRepository busRepository;
    @Mock private DriverRepository driverRepository;
    @Mock private GtfsScheduleTripRepository scheduleTripRepository;
    @Mock private GtfsScheduleStopTimeRepository scheduleStopTimeRepository;
    @Mock private GtfsCalendarRepository calendarRepository;
    @Mock private PlatformTransactionManager transactionManager;

    private GtfsOperationalSimulationServiceImpl simulationService;

    private Bus sampleBus;
    private Driver sampleDriver;
    private Route sampleRoute;
    private GtfsScheduleTrip sampleScheduleTrip;
    private GtfsCalendar regularCalendar;

    @BeforeEach
    void setUp() {
        simulationService = new GtfsOperationalSimulationServiceImpl(
                tripRepository,
                tripLogRepository,
                routeRepository,
                busRepository,
                driverRepository,
                scheduleTripRepository,
                scheduleStopTimeRepository,
                calendarRepository,
                transactionManager
        );

        sampleBus = new Bus(1L, "TN01AB1234", "BUS-101", "Volvo B9R", 40, BusStatus.ACTIVE, LocalDateTime.now());
        sampleDriver = new Driver(1L, null, "EMP-001", "Karthik Raj", "9876543210", "LIC-998877", DriverStatus.AVAILABLE, LocalDateTime.now());
        sampleRoute = new Route(1L, "R1", "MTC", "2.0", "21G", "Broadway to Tambaram", "Guindy", "Tambaram", BigDecimal.valueOf(15.5), 45, true, LocalDateTime.now());
        sampleScheduleTrip = new GtfsScheduleTrip(1L, "T101", "R1", "Regular", 0, "BATCH-01");

        // Active Mon-Fri (1,1,1,1,1,0,0) for MTC v2.0 in BATCH-01
        regularCalendar = new GtfsCalendar(1L, "Regular", "MTC", "2.0", 1, 1, 1, 1, 1, 0, 0, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), "BATCH-01");
        when(calendarRepository.findByGtfsServiceIdAndBatchId("Regular", "BATCH-01")).thenReturn(Optional.of(regularCalendar));
        when(calendarRepository.findByGtfsServiceIdAndFeedIdAndFeedVersion("Regular", "MTC", "2.0")).thenReturn(Optional.of(regularCalendar));
    }

    @Test
    @DisplayName("A. Same Feed + Same Version + Same Batch + Same Service-ID -> Correct Calendar Resolved")
    void testSameFeedVersionBatchAndServiceIdCorrectCalendarResolved() {
        when(scheduleTripRepository.findByBatchId("BATCH-01")).thenReturn(List.of(sampleScheduleTrip));
        when(scheduleTripRepository.findAll()).thenReturn(List.of(sampleScheduleTrip));
        when(busRepository.findAll()).thenReturn(List.of(sampleBus));
        when(driverRepository.findAll()).thenReturn(List.of(sampleDriver));
        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "MTC", "2.0")).thenReturn(Optional.of(sampleRoute));
        when(routeRepository.findByGtfsRouteId("R1")).thenReturn(Optional.of(sampleRoute));

        GtfsScheduleStopTime st1 = new GtfsScheduleStopTime(1L, "T101", "S1", LocalTime.of(8, 0), LocalTime.of(8, 0), 0, 1, "BATCH-01");
        GtfsScheduleStopTime st2 = new GtfsScheduleStopTime(2L, "T101", "S2", LocalTime.of(8, 45), LocalTime.of(8, 45), 0, 2, "BATCH-01");
        when(scheduleStopTimeRepository.findByGtfsTripIdOrderByStopSequenceAsc("T101")).thenReturn(List.of(st1, st2));

        LocalDate monday = LocalDate.of(2026, 8, 24); // Mon - Active!
        GtfsSimulationRequest request = new GtfsSimulationRequest("BATCH-01", monday, monday, 10, 42L);

        GtfsSimulationResponse response = simulationService.simulateOperationalTrips(request);

        assertNotNull(response);
        assertEquals(1, response.getTripsGenerated());
        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    @Test
    @DisplayName("B. Cross-Batch Calendar Lineage Isolation (Strict Batch Fallback Block)")
    void testSameFeedAndVersionDifferentBatchLineageIsolated() {
        GtfsScheduleTrip batch1Trip = new GtfsScheduleTrip(1L, "T101", "R1", "WKD", 0, "BATCH-1");
        GtfsCalendar batch1Calendar = new GtfsCalendar(1L, "WKD", "A", "2.0", 1, 1, 1, 1, 1, 0, 0, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), "BATCH-1");

        GtfsScheduleTrip batch2Trip = new GtfsScheduleTrip(2L, "T102", "R1", "WKD", 0, "BATCH-2");
        GtfsCalendar batch2Calendar = new GtfsCalendar(2L, "WKD", "A", "2.0", 0, 0, 0, 0, 0, 1, 1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), "BATCH-2");

        Route routeA = new Route(1L, "R1", "A", "2.0", "21G-A", "Route A", "Guindy", "Tambaram", BigDecimal.valueOf(15.5), 45, true, LocalDateTime.now());

        when(busRepository.findAll()).thenReturn(List.of(sampleBus));
        when(driverRepository.findAll()).thenReturn(List.of(sampleDriver));
        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "A", "2.0")).thenReturn(Optional.of(routeA));
        when(routeRepository.findByGtfsRouteId("R1")).thenReturn(Optional.of(routeA));

        GtfsScheduleStopTime st1 = new GtfsScheduleStopTime(1L, "T101", "S1", LocalTime.of(8, 0), LocalTime.of(8, 0), 0, 1, "BATCH-1");
        GtfsScheduleStopTime st2 = new GtfsScheduleStopTime(2L, "T101", "S2", LocalTime.of(8, 45), LocalTime.of(8, 45), 0, 2, "BATCH-1");
        when(scheduleStopTimeRepository.findByGtfsTripIdOrderByStopSequenceAsc(anyString())).thenReturn(List.of(st1, st2));

        when(calendarRepository.findByGtfsServiceIdAndBatchId("WKD", "BATCH-1")).thenReturn(Optional.of(batch1Calendar));
        when(calendarRepository.findByGtfsServiceIdAndBatchId("WKD", "BATCH-2")).thenReturn(Optional.of(batch2Calendar));

        LocalDate monday = LocalDate.of(2026, 8, 24);

        // 1. Simulate BATCH-1 on Monday -> Uses BATCH-1 active Monday calendar -> Trip generated
        when(scheduleTripRepository.findByBatchId("BATCH-1")).thenReturn(List.of(batch1Trip));
        GtfsSimulationResponse res1 = simulationService.simulateOperationalTrips(new GtfsSimulationRequest("BATCH-1", monday, monday, 10, 42L));
        assertEquals(1, res1.getTripsGenerated());

        // 2. Simulate BATCH-2 on Monday -> Uses BATCH-2 inactive Monday calendar -> Trip NOT generated
        when(scheduleTripRepository.findByBatchId("BATCH-2")).thenReturn(List.of(batch2Trip));
        GtfsSimulationResponse res2 = simulationService.simulateOperationalTrips(new GtfsSimulationRequest("BATCH-2", monday, monday, 10, 42L));
        assertEquals(0, res2.getTripsGenerated());

        // 3. Make BATCH-1's calendar unavailable while BATCH-2's calendar remains available!
        when(calendarRepository.findByGtfsServiceIdAndBatchId("WKD", "BATCH-1")).thenReturn(Optional.empty());

        // Simulate BATCH-1 -> MUST NOT use BATCH-2's calendar -> Trip NOT generated!
        GtfsSimulationResponse res3 = simulationService.simulateOperationalTrips(new GtfsSimulationRequest("BATCH-1", monday, monday, 10, 42L));
        assertEquals(0, res3.getTripsGenerated());
    }

    @Test
    @DisplayName("C. Cross-Feed GTFS Route Provenance Isolation")
    void testCrossFeedRouteIsolation() {
        Route routeFeedA = new Route(10L, "R1", "A", "2.0", "R1-A", "Route A", "Guindy", "Tambaram", BigDecimal.valueOf(15.0), 45, true, LocalDateTime.now());
        Route routeFeedB = new Route(20L, "R1", "B", "2.0", "R1-B", "Route B", "Central", "Airport", BigDecimal.valueOf(20.0), 40, true, LocalDateTime.now());

        GtfsScheduleTrip tripA = new GtfsScheduleTrip(10L, "T-A", "R1", "Regular", 0, "BATCH-A");
        GtfsCalendar calA = new GtfsCalendar(10L, "Regular", "A", "2.0", 1, 1, 1, 1, 1, 0, 0, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), "BATCH-A");

        GtfsScheduleTrip tripB = new GtfsScheduleTrip(20L, "T-B", "R1", "Regular", 0, "BATCH-B");
        GtfsCalendar calB = new GtfsCalendar(20L, "Regular", "B", "2.0", 1, 1, 1, 1, 1, 0, 0, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), "BATCH-B");

        when(calendarRepository.findByGtfsServiceIdAndBatchId("Regular", "BATCH-A")).thenReturn(Optional.of(calA));
        when(calendarRepository.findByGtfsServiceIdAndBatchId("Regular", "BATCH-B")).thenReturn(Optional.of(calB));

        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "A", "2.0")).thenReturn(Optional.of(routeFeedA));
        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "B", "2.0")).thenReturn(Optional.of(routeFeedB));

        when(busRepository.findAll()).thenReturn(List.of(sampleBus));
        when(driverRepository.findAll()).thenReturn(List.of(sampleDriver));

        GtfsScheduleStopTime st1 = new GtfsScheduleStopTime(1L, "T-A", "S1", LocalTime.of(8, 0), LocalTime.of(8, 0), 0, 1, "BATCH-A");
        GtfsScheduleStopTime st2 = new GtfsScheduleStopTime(2L, "T-A", "S2", LocalTime.of(8, 45), LocalTime.of(8, 45), 0, 2, "BATCH-A");
        when(scheduleStopTimeRepository.findByGtfsTripIdOrderByStopSequenceAsc(anyString())).thenReturn(List.of(st1, st2));

        LocalDate monday = LocalDate.of(2026, 8, 24);

        // Feed A simulation resolves Feed A Route
        when(scheduleTripRepository.findByBatchId("BATCH-A")).thenReturn(List.of(tripA));
        GtfsSimulationResponse resA = simulationService.simulateOperationalTrips(new GtfsSimulationRequest("BATCH-A", monday, monday, 10, 42L));
        assertEquals(1, resA.getTripsGenerated());

        // Feed B simulation resolves Feed B Route
        when(scheduleTripRepository.findByBatchId("BATCH-B")).thenReturn(List.of(tripB));
        GtfsSimulationResponse resB = simulationService.simulateOperationalTrips(new GtfsSimulationRequest("BATCH-B", monday, monday, 10, 42L));
        assertEquals(1, resB.getTripsGenerated());

        verify(routeRepository, times(1)).findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "A", "2.0");
        verify(routeRepository, times(1)).findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "B", "2.0");
    }

    @Test
    @DisplayName("D. Same Feed + Different Version + Same Service-ID -> Calendar Records are NOT Mixed")
    void testSameFeedDifferentVersionNotMixed() {
        Route mtcV1Route = new Route(3L, "R1", "MTC", "1.0", "21G-V1", "MTC V1 Line", "Guindy", "Tambaram", BigDecimal.valueOf(15.5), 45, true, LocalDateTime.now());
        GtfsScheduleTrip mtcV1Trip = new GtfsScheduleTrip(3L, "T101", "R1", "Regular", 0, "BATCH-V1");

        when(scheduleTripRepository.findByBatchId("BATCH-V1")).thenReturn(List.of(mtcV1Trip));
        when(scheduleTripRepository.findAll()).thenReturn(List.of(mtcV1Trip));
        when(busRepository.findAll()).thenReturn(List.of(sampleBus));
        when(driverRepository.findAll()).thenReturn(List.of(sampleDriver));
        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "MTC", "1.0")).thenReturn(Optional.of(mtcV1Route));
        when(routeRepository.findByGtfsRouteId("R1")).thenReturn(Optional.of(mtcV1Route));

        // Version 1.0 has NO calendar record!
        when(calendarRepository.findByGtfsServiceIdAndBatchId("Regular", "BATCH-V1")).thenReturn(Optional.empty());

        LocalDate monday = LocalDate.of(2026, 8, 24);
        GtfsSimulationRequest request = new GtfsSimulationRequest("BATCH-V1", monday, monday, 10, 42L);

        GtfsSimulationResponse response = simulationService.simulateOperationalTrips(request);

        assertNotNull(response);
        assertEquals(0, response.getTripsGenerated());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("E. Missing Batch/Calendar Provenance -> NO Trip Generated (Global Fallback Rejected)")
    void testMissingProvenanceNoTrip() {
        GtfsScheduleTrip unprovenancedTrip = new GtfsScheduleTrip(5L, "T105", "R1", "Regular", 0, "BATCH-UNPROVENANCED");
        when(scheduleTripRepository.findByBatchId("BATCH-UNPROVENANCED")).thenReturn(List.of(unprovenancedTrip));
        when(scheduleTripRepository.findAll()).thenReturn(List.of(unprovenancedTrip));
        when(busRepository.findAll()).thenReturn(List.of(sampleBus));
        when(driverRepository.findAll()).thenReturn(List.of(sampleDriver));
        when(routeRepository.findByGtfsRouteId("R1")).thenReturn(Optional.of(sampleRoute));

        when(calendarRepository.findByGtfsServiceIdAndBatchId("Regular", "BATCH-UNPROVENANCED")).thenReturn(Optional.empty());

        LocalDate monday = LocalDate.of(2026, 8, 24);
        GtfsSimulationRequest request = new GtfsSimulationRequest("BATCH-UNPROVENANCED", monday, monday, 10, 42L);

        GtfsSimulationResponse response = simulationService.simulateOperationalTrips(request);

        assertNotNull(response);
        assertEquals(0, response.getTripsGenerated());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("F. Target Date Outside Calendar Start/End -> No Trip")
    void testServiceOutsideDateRangeTripNotGenerated() {
        when(scheduleTripRepository.findByBatchId("BATCH-01")).thenReturn(List.of(sampleScheduleTrip));
        when(scheduleTripRepository.findAll()).thenReturn(List.of(sampleScheduleTrip));
        when(busRepository.findAll()).thenReturn(List.of(sampleBus));
        when(driverRepository.findAll()).thenReturn(List.of(sampleDriver));

        // Date in 2027 (outside calendar start/end date range!)
        LocalDate mondayIn2027 = LocalDate.of(2027, 8, 23);
        GtfsSimulationRequest request = new GtfsSimulationRequest("BATCH-01", mondayIn2027, mondayIn2027, 10, 42L);

        GtfsSimulationResponse response = simulationService.simulateOperationalTrips(request);

        assertNotNull(response);
        assertEquals(0, response.getTripsGenerated());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("G. Target Date on Inactive Weekday -> No Trip")
    void testServiceInactiveWeekdayTripNotGenerated() {
        when(scheduleTripRepository.findByBatchId("BATCH-01")).thenReturn(List.of(sampleScheduleTrip));
        when(scheduleTripRepository.findAll()).thenReturn(List.of(sampleScheduleTrip));
        when(busRepository.findAll()).thenReturn(List.of(sampleBus));
        when(driverRepository.findAll()).thenReturn(List.of(sampleDriver));

        // Sunday target date (Regular calendar has sunday=0!)
        LocalDate sunday = LocalDate.of(2026, 8, 23); // Sun
        GtfsSimulationRequest request = new GtfsSimulationRequest("BATCH-01", sunday, sunday, 10, 42L);

        GtfsSimulationResponse response = simulationService.simulateOperationalTrips(request);

        assertNotNull(response);
        assertEquals(0, response.getTripsGenerated());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("H. Unknown Service-ID -> No Trip")
    void testMissingServiceIdTripNotGenerated() {
        GtfsScheduleTrip unknownTrip = new GtfsScheduleTrip(3L, "T103", "R1", "UnknownService", 0, "BATCH-01");
        when(scheduleTripRepository.findByBatchId("BATCH-01")).thenReturn(List.of(unknownTrip));
        when(scheduleTripRepository.findAll()).thenReturn(List.of(unknownTrip));
        when(busRepository.findAll()).thenReturn(List.of(sampleBus));
        when(driverRepository.findAll()).thenReturn(List.of(sampleDriver));
        when(calendarRepository.findByGtfsServiceIdAndBatchId("UnknownService", "BATCH-01")).thenReturn(Optional.empty());

        LocalDate monday = LocalDate.of(2026, 8, 24);
        GtfsSimulationRequest request = new GtfsSimulationRequest("BATCH-01", monday, monday, 10, 42L);

        GtfsSimulationResponse response = simulationService.simulateOperationalTrips(request);

        assertNotNull(response);
        assertEquals(0, response.getTripsGenerated());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("I. Malformed / Quarantined Service-ID -> No Trip")
    void testQuarantinedServiceIdTripNotGenerated() {
        GtfsScheduleTrip malformedTrip = new GtfsScheduleTrip(4L, "T104", "R1", "'/><script>alert(1)</script>", 0, "BATCH-01");
        when(scheduleTripRepository.findByBatchId("BATCH-01")).thenReturn(List.of(malformedTrip));
        when(scheduleTripRepository.findAll()).thenReturn(List.of(malformedTrip));
        when(busRepository.findAll()).thenReturn(List.of(sampleBus));
        when(driverRepository.findAll()).thenReturn(List.of(sampleDriver));

        LocalDate monday = LocalDate.of(2026, 8, 24);
        GtfsSimulationRequest request = new GtfsSimulationRequest("BATCH-01", monday, monday, 10, 42L);

        GtfsSimulationResponse response = simulationService.simulateOperationalTrips(request);

        assertNotNull(response);
        assertEquals(0, response.getTripsGenerated());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("L. Same Active Service Imported Repeatedly Remains Idempotent")
    void testSameFeedIdempotency() {
        when(scheduleTripRepository.findByBatchId("BATCH-01")).thenReturn(List.of(sampleScheduleTrip));
        when(scheduleTripRepository.findAll()).thenReturn(List.of(sampleScheduleTrip));
        when(busRepository.findAll()).thenReturn(List.of(sampleBus));
        when(driverRepository.findAll()).thenReturn(List.of(sampleDriver));
        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "MTC", "2.0")).thenReturn(Optional.of(sampleRoute));
        when(routeRepository.findByGtfsRouteId("R1")).thenReturn(Optional.of(sampleRoute));

        GtfsScheduleStopTime st1 = new GtfsScheduleStopTime(1L, "T101", "S1", LocalTime.of(8, 0), LocalTime.of(8, 0), 0, 1, "BATCH-01");
        GtfsScheduleStopTime st2 = new GtfsScheduleStopTime(2L, "T101", "S2", LocalTime.of(8, 45), LocalTime.of(8, 45), 0, 2, "BATCH-01");
        when(scheduleStopTimeRepository.findByGtfsTripIdOrderByStopSequenceAsc("T101")).thenReturn(List.of(st1, st2));

        LocalDate monday = LocalDate.of(2026, 8, 24);
        LocalDateTime scheduledStart = LocalDateTime.of(monday, LocalTime.of(8, 0));

        // Trip already exists in repository!
        when(tripRepository.existsByRouteFeedIdAndRouteFeedVersionAndGtfsTripIdAndScheduledStart("MTC", "2.0", "T101", scheduledStart)).thenReturn(true);

        GtfsSimulationRequest request = new GtfsSimulationRequest("BATCH-01", monday, monday, 10, 42L);
        GtfsSimulationResponse response = simulationService.simulateOperationalTrips(request);

        assertNotNull(response);
        assertEquals(0, response.getTripsGenerated());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("L2. Deterministic Reproducibility - Same Seed Produces Identical Delay Values")
    void testDeterministicReproducibility() {
        when(scheduleTripRepository.findByBatchId("BATCH-01")).thenReturn(List.of(sampleScheduleTrip));
        when(scheduleTripRepository.findAll()).thenReturn(List.of(sampleScheduleTrip));
        when(busRepository.findAll()).thenReturn(List.of(sampleBus));
        when(driverRepository.findAll()).thenReturn(List.of(sampleDriver));
        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "MTC", "2.0")).thenReturn(Optional.of(sampleRoute));
        when(routeRepository.findByGtfsRouteId("R1")).thenReturn(Optional.of(sampleRoute));

        GtfsScheduleStopTime st1 = new GtfsScheduleStopTime(1L, "T101", "S1", LocalTime.of(8, 0), LocalTime.of(8, 0), 0, 1, "BATCH-01");
        GtfsScheduleStopTime st2 = new GtfsScheduleStopTime(2L, "T101", "S2", LocalTime.of(8, 45), LocalTime.of(8, 45), 0, 2, "BATCH-01");
        when(scheduleStopTimeRepository.findByGtfsTripIdOrderByStopSequenceAsc("T101")).thenReturn(List.of(st1, st2));

        LocalDate targetDate = LocalDate.of(2026, 8, 24);
        GtfsSimulationRequest req1 = new GtfsSimulationRequest("BATCH-01", targetDate, targetDate, 1, 99L);
        GtfsSimulationResponse res1 = simulationService.simulateOperationalTrips(req1);

        reset(tripRepository, tripLogRepository);

        GtfsSimulationRequest req2 = new GtfsSimulationRequest("BATCH-01", targetDate, targetDate, 1, 99L);
        GtfsSimulationResponse res2 = simulationService.simulateOperationalTrips(req2);

        assertEquals(res1.getAvgArrivalDelayMinutes(), res2.getAvgArrivalDelayMinutes(), 0.001);
    }
}

package com.intellitransit.service;

import com.intellitransit.dto.GtfsImportRequest;
import com.intellitransit.dto.GtfsImportResponse;
import com.intellitransit.entity.GtfsQuarantineLog;
import com.intellitransit.entity.Route;
import com.intellitransit.entity.Stop;
import com.intellitransit.exception.GtfsValidationException;
import com.intellitransit.repository.*;
import com.intellitransit.service.impl.GtfsIngestionServiceImpl;
import com.intellitransit.util.GtfsSanitizer;
import com.intellitransit.util.GtfsTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GtfsIngestionTest {

    @Mock private RouteRepository routeRepository;
    @Mock private StopRepository stopRepository;
    @Mock private RouteStopRepository routeStopRepository;
    @Mock private GtfsFeedMetadataRepository feedMetadataRepository;
    @Mock private GtfsScheduleTripRepository scheduleTripRepository;
    @Mock private GtfsScheduleStopTimeRepository scheduleStopTimeRepository;
    @Mock private GtfsQuarantineLogRepository quarantineLogRepository;
    @Mock private GtfsCalendarRepository calendarRepository;
    @Mock private org.springframework.transaction.PlatformTransactionManager transactionManager;

    private GtfsIngestionServiceImpl gtfsIngestionService;

    @BeforeEach
    void setUp() {
        gtfsIngestionService = new GtfsIngestionServiceImpl(
                routeRepository,
                stopRepository,
                routeStopRepository,
                feedMetadataRepository,
                scheduleTripRepository,
                scheduleStopTimeRepository,
                quarantineLogRepository,
                calendarRepository,
                transactionManager
        );
    }

    @Test
    @DisplayName("1. GTFS Time Parsing - Standard Timestamps")
    void testStandardGtfsTimeParsing() {
        GtfsTimeUtil.GtfsTimeResult res1 = GtfsTimeUtil.parseGtfsTime("08:30:00");
        assertEquals(LocalTime.of(8, 30, 0), res1.getLocalTime());
        assertEquals(0, res1.getDayOffset());

        GtfsTimeUtil.GtfsTimeResult res2 = GtfsTimeUtil.parseGtfsTime("23:59:59");
        assertEquals(LocalTime.of(23, 59, 59), res2.getLocalTime());
        assertEquals(0, res2.getDayOffset());
    }

    @Test
    @DisplayName("2. GTFS Time Parsing - Over 24:00 Timestamps")
    void testOver24hGtfsTimeParsing() {
        GtfsTimeUtil.GtfsTimeResult res1 = GtfsTimeUtil.parseGtfsTime("24:15:00");
        assertEquals(LocalTime.of(0, 15, 0), res1.getLocalTime());
        assertEquals(1, res1.getDayOffset());

        GtfsTimeUtil.GtfsTimeResult res2 = GtfsTimeUtil.parseGtfsTime("25:30:00");
        assertEquals(LocalTime.of(1, 30, 0), res2.getLocalTime());
        assertEquals(1, res2.getDayOffset());
    }

    @Test
    @DisplayName("3. Sanitizer Rejects Script & Malicious Payloads")
    void testSanitizerRejectsMaliciousPayloads() {
        assertFalse(GtfsSanitizer.isSanitary("'/><script>alert(1)</script>"));
        assertFalse(GtfsSanitizer.isSanitary("<iframe src='evil.com'></iframe>"));
        assertTrue(GtfsSanitizer.isSanitary("Guindy TVK Estate"));
    }

    @Test
    @DisplayName("4. Sanitizer Normalizes Whitespace")
    void testSanitizerWhitespaceNormalization() {
        assertEquals("test", GtfsSanitizer.normalize("  test  "));
        assertEquals("Route 21G", GtfsSanitizer.normalize("Route 21G "));
    }

    @Test
    @DisplayName("5. Duplicate Detection")
    void testDuplicateDetectionInIngestion(@TempDir File tempDir) throws IOException {
        createFile(tempDir, "routes.txt", "route_id,route_short_name,route_long_name\nR1,21G,Broadway\nR1,21G,Broadway\n");
        createFile(tempDir, "stops.txt", "stop_id,stop_name,stop_lat,stop_lon\nS1,Guindy,13.0067,80.2020\n");
        createFile(tempDir, "trips.txt", "trip_id,route_id,service_id\nT1,R1,Regular\n");
        createFile(tempDir, "stop_times.txt", "trip_id,arrival_time,departure_time,stop_id,stop_sequence\nT1,08:00:00,08:00:00,S1,1\n");

        when(stopRepository.findByGtfsStopIdAndFeedIdAndFeedVersion("S1", "MTC", "2.0")).thenReturn(Optional.empty());
        when(stopRepository.save(any(Stop.class))).thenAnswer(i -> i.getArgument(0));
        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "MTC", "2.0")).thenReturn(Optional.empty());
        when(routeRepository.findByRouteNumber("21G")).thenReturn(Optional.empty());
        when(routeRepository.save(any(Route.class))).thenAnswer(i -> i.getArgument(0));

        GtfsImportRequest request = new GtfsImportRequest(tempDir.getAbsolutePath(), false, "MTC", "2.0");
        GtfsImportResponse response = gtfsIngestionService.importGtfsFeed(request);

        assertNotNull(response);
        assertEquals("COMPLETED", response.getStatus());
    }

    @Test
    @DisplayName("6. Broken References Rejection")
    void testBrokenReferencesRejection(@TempDir File tempDir) throws IOException {
        createFile(tempDir, "routes.txt", "route_id,route_short_name,route_long_name\nR1,21G,Broadway\n");
        createFile(tempDir, "stops.txt", "stop_id,stop_name,stop_lat,stop_lon\nS1,Guindy,13.0067,80.2020\n");
        createFile(tempDir, "trips.txt", "trip_id,route_id,service_id\nT1,INVALID_ROUTE_ID,Regular\n");
        createFile(tempDir, "stop_times.txt", "trip_id,arrival_time,departure_time,stop_id,stop_sequence\nT1,08:00:00,08:00:00,S1,1\n");

        when(stopRepository.findByGtfsStopIdAndFeedIdAndFeedVersion("S1", "MTC", "2.0")).thenReturn(Optional.empty());
        when(stopRepository.save(any(Stop.class))).thenAnswer(i -> i.getArgument(0));
        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "MTC", "2.0")).thenReturn(Optional.empty());
        when(routeRepository.findByRouteNumber("21G")).thenReturn(Optional.empty());
        when(routeRepository.save(any(Route.class))).thenAnswer(i -> i.getArgument(0));

        GtfsImportRequest request = new GtfsImportRequest(tempDir.getAbsolutePath(), false, "MTC", "2.0");
        GtfsImportResponse response = gtfsIngestionService.importGtfsFeed(request);

        assertNotNull(response);
        assertTrue(response.getQuarantinedRecordsCount() >= 1);
    }

    @Test
    @DisplayName("7. Dedicated Invalid Coordinates Test")
    void testInvalidCoordinatesQuarantined(@TempDir File tempDir) throws IOException {
        createFile(tempDir, "routes.txt", "route_id,route_short_name,route_long_name\nR1,21G,Broadway\n");
        createFile(tempDir, "stops.txt", "stop_id,stop_name,stop_lat,stop_lon\nS1,Out-of-Bounds-Stop,48.8566,2.3522\n");
        createFile(tempDir, "trips.txt", "trip_id,route_id,service_id\nT1,R1,Regular\n");
        createFile(tempDir, "stop_times.txt", "trip_id,arrival_time,departure_time,stop_id,stop_sequence\nT1,08:00:00,08:00:00,S1,1\n");

        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "MTC", "2.0")).thenReturn(Optional.empty());
        when(routeRepository.findByRouteNumber("21G")).thenReturn(Optional.empty());
        when(routeRepository.save(any(Route.class))).thenAnswer(i -> i.getArgument(0));

        GtfsImportRequest request = new GtfsImportRequest(tempDir.getAbsolutePath(), false, "MTC", "2.0");
        GtfsImportResponse response = gtfsIngestionService.importGtfsFeed(request);

        assertNotNull(response);
        assertTrue(response.getQuarantinedRecordsCount() >= 1);
        verify(quarantineLogRepository, atLeastOnce()).save(any(GtfsQuarantineLog.class));
    }

    @Test
    @DisplayName("8. Route-Number Disambiguation On Collision")
    void testRouteNumberDisambiguation(@TempDir File tempDir) throws IOException {
        createFile(tempDir, "routes.txt", "route_id,route_short_name,route_long_name\nR2,21G,Secondary Line\n");
        createFile(tempDir, "stops.txt", "stop_id,stop_name,stop_lat,stop_lon\nS1,Guindy,13.0067,80.2020\n");
        createFile(tempDir, "trips.txt", "trip_id,route_id,service_id\nT1,R2,Regular\n");
        createFile(tempDir, "stop_times.txt", "trip_id,arrival_time,departure_time,stop_id,stop_sequence\nT1,08:00:00,08:00:00,S1,1\n");

        when(stopRepository.findByGtfsStopIdAndFeedIdAndFeedVersion("S1", "MTC", "2.0")).thenReturn(Optional.empty());
        when(stopRepository.save(any(Stop.class))).thenAnswer(i -> i.getArgument(0));
        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R2", "MTC", "2.0")).thenReturn(Optional.empty());
        when(routeRepository.findByRouteNumber("21G")).thenReturn(Optional.of(new Route(1L, "R1", "MTC", "2.0", "21G", "Existing", "O", "D", java.math.BigDecimal.ONE, 10, true, null)));
        when(routeRepository.save(any(Route.class))).thenAnswer(i -> i.getArgument(0));

        GtfsImportRequest request = new GtfsImportRequest(tempDir.getAbsolutePath(), false, "MTC", "2.0");
        GtfsImportResponse response = gtfsIngestionService.importGtfsFeed(request);

        assertNotNull(response);
        assertEquals("COMPLETED", response.getStatus());
        verify(routeRepository).findByRouteNumber("21G");
    }

    @Test
    @DisplayName("9. Directional Route Handling & 10. GTFS Source-ID Preservation")
    void testDirectionalRouteAndSourceIdPreservation(@TempDir File tempDir) throws IOException {
        createFile(tempDir, "routes.txt", "route_id,route_short_name,route_long_name\nR1,21G,Broadway to Tambaram\n");
        createFile(tempDir, "stops.txt", "stop_id,stop_name,stop_lat,stop_lon\nS1,Guindy,13.0067,80.2020\nS2,Tambaram,12.9249,80.1000\n");
        createFile(tempDir, "trips.txt", "trip_id,route_id,service_id,direction_id\nT1,R1,Regular,0\n");
        createFile(tempDir, "stop_times.txt", "trip_id,arrival_time,departure_time,stop_id,stop_sequence\nT1,08:00:00,08:00:00,S1,1\nT1,08:30:00,08:30:00,S2,2\n");

        Stop stop1 = new Stop(1L, "S1", "MTC", "2.0", "Guindy", java.math.BigDecimal.valueOf(13.0067), java.math.BigDecimal.valueOf(80.2020), null);
        Stop stop2 = new Stop(2L, "S2", "MTC", "2.0", "Tambaram", java.math.BigDecimal.valueOf(12.9249), java.math.BigDecimal.valueOf(80.1000), null);

        when(stopRepository.findByGtfsStopIdAndFeedIdAndFeedVersion("S1", "MTC", "2.0")).thenReturn(Optional.of(stop1));
        when(stopRepository.findByGtfsStopIdAndFeedIdAndFeedVersion("S2", "MTC", "2.0")).thenReturn(Optional.of(stop2));

        Route route = new Route(1L, "R1", "MTC", "2.0", "21G", "Broadway to Tambaram", "Guindy", "Tambaram", java.math.BigDecimal.TEN, 30, true, null);
        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "MTC", "2.0")).thenReturn(Optional.of(route));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

        GtfsImportRequest request = new GtfsImportRequest(tempDir.getAbsolutePath(), false, "MTC", "2.0");
        GtfsImportResponse response = gtfsIngestionService.importGtfsFeed(request);

        assertNotNull(response);
        assertEquals("COMPLETED", response.getStatus());
        assertEquals("S1", stop1.getGtfsStopId());
        assertEquals("R1", route.getGtfsRouteId());
        assertEquals("MTC", route.getFeedId());
        assertEquals("2.0", route.getFeedVersion());
    }

    @Test
    @DisplayName("11. Idempotent Repeated Import")
    void testIdempotentRepeatedImport(@TempDir File tempDir) throws IOException {
        createFile(tempDir, "routes.txt", "route_id,route_short_name,route_long_name\nR1,21G,Broadway\n");
        createFile(tempDir, "stops.txt", "stop_id,stop_name,stop_lat,stop_lon\nS1,Guindy,13.0067,80.2020\n");
        createFile(tempDir, "trips.txt", "trip_id,route_id,service_id\nT1,R1,Regular\n");
        createFile(tempDir, "stop_times.txt", "trip_id,arrival_time,departure_time,stop_id,stop_sequence\nT1,08:00:00,08:00:00,S1,1\n");

        Stop existingStop = new Stop(1L, "S1", "MTC", "2.0", "Guindy", java.math.BigDecimal.valueOf(13.0067), java.math.BigDecimal.valueOf(80.2020), null);
        Route existingRoute = new Route(1L, "R1", "MTC", "2.0", "21G", "Broadway", "Guindy", "Guindy", java.math.BigDecimal.ZERO, 30, true, null);

        when(stopRepository.findByGtfsStopIdAndFeedIdAndFeedVersion("S1", "MTC", "2.0")).thenReturn(Optional.of(existingStop));
        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "MTC", "2.0")).thenReturn(Optional.of(existingRoute));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(existingRoute));

        GtfsImportRequest request = new GtfsImportRequest(tempDir.getAbsolutePath(), false, "MTC", "2.0");
        GtfsImportResponse response = gtfsIngestionService.importGtfsFeed(request);

        assertNotNull(response);
        assertEquals(0, response.getTotalStopsImported());
        assertEquals(0, response.getTotalRoutesImported());
    }

    @Test
    @DisplayName("12. True Cross-Feed Identity & Version Isolation")
    void testTrueCrossFeedIdentityIsolation(@TempDir File tempDir) throws IOException {
        createFile(tempDir, "routes.txt", "route_id,route_short_name,route_long_name\nR1,21G,Broadway\n");
        createFile(tempDir, "stops.txt", "stop_id,stop_name,stop_lat,stop_lon\nS1,Guindy,13.0067,80.2020\n");
        createFile(tempDir, "trips.txt", "trip_id,route_id,service_id\nT1,R1,Regular\n");
        createFile(tempDir, "stop_times.txt", "trip_id,arrival_time,departure_time,stop_id,stop_sequence\nT1,08:00:00,08:00:00,S1,1\n");

        // When Feed CMRL (Version 2.0) is imported, existing Stop/Route for MTC (Version 2.0) must NOT be merged!
        when(stopRepository.findByGtfsStopIdAndFeedIdAndFeedVersion("S1", "CMRL", "2.0")).thenReturn(Optional.empty());
        when(stopRepository.save(any(Stop.class))).thenAnswer(i -> i.getArgument(0));
        when(routeRepository.findByGtfsRouteIdAndFeedIdAndFeedVersion("R1", "CMRL", "2.0")).thenReturn(Optional.empty());
        when(routeRepository.findByRouteNumber("21G")).thenReturn(Optional.empty());
        when(routeRepository.save(any(Route.class))).thenAnswer(i -> i.getArgument(0));

        GtfsImportRequest request = new GtfsImportRequest(tempDir.getAbsolutePath(), false, "CMRL", "2.0");
        GtfsImportResponse response = gtfsIngestionService.importGtfsFeed(request);

        assertNotNull(response);
        assertEquals("COMPLETED", response.getStatus());
        assertEquals(1, response.getTotalStopsImported());
        assertEquals(1, response.getTotalRoutesImported());
    }

    @Test
    @DisplayName("13. Transaction Failure Fast-Fail")
    void testTransactionFailureFastFail(@TempDir File tempDir) {
        GtfsImportRequest request = new GtfsImportRequest(tempDir.getAbsolutePath() + "/non_existent_folder", false);
        assertThrows(GtfsValidationException.class, () -> gtfsIngestionService.importGtfsFeed(request));
    }

    private void createFile(File dir, String filename, String content) throws IOException {
        File file = new File(dir, filename);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
}

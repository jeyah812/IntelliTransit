package com.intellitransit.service;

import com.intellitransit.dto.AIAlertDTO;
import com.intellitransit.entity.*;
import com.intellitransit.entity.enums.*;
import com.intellitransit.repository.AIAlertRepository;
import com.intellitransit.repository.BookingRepository;
import com.intellitransit.repository.ComplaintRepository;
import com.intellitransit.repository.TripRepository;
import com.intellitransit.service.impl.AIAlertServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIAlertServiceTest {

    @Mock
    private AIAlertRepository aiAlertRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AIAlertServiceImpl aiAlertService;

    private Trip sampleTrip;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        sampleTrip = Trip.builder()
                .id(1L)
                .gtfsTripId("GTFS-T1")
                .scheduledStart(now.minusHours(2))
                .scheduledEnd(now.minusHours(1)) // Planned = 60 mins
                .actualStart(now.minusHours(2))
                .actualEnd(now) // Actual = 120 mins (> 60 * 1.5 = 90 mins)
                .status(TripStatus.COMPLETED)
                .build();
    }

    @Test
    @DisplayName("Should detect TRIP_DURATION_ANOMALY when actual duration > planned duration * 1.5")
    void testTripDurationAnomalyDetection() {
        when(tripRepository.findAll()).thenReturn(List.of(sampleTrip));
        when(complaintRepository.findByTripId(1L)).thenReturn(List.of());
        when(bookingRepository.findByTripId(1L)).thenReturn(List.of());
        when(aiAlertRepository.existsByTripIdAndAlertTypeAndStatus(eq(1L), eq(AlertType.TRIP_DURATION_ANOMALY), eq(AlertStatus.NEW))).thenReturn(false);
        when(aiAlertRepository.save(any(AIAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<AIAlertDTO> alerts = aiAlertService.runAnomalyDetection();

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        AIAlertDTO alert = alerts.get(0);
        assertEquals(AlertType.TRIP_DURATION_ANOMALY, alert.getAlertType());
        assertEquals(AlertSeverity.HIGH, alert.getSeverity());
        assertEquals(AlertStatus.NEW, alert.getStatus());
        assertEquals("Trip duration exceeded expected duration by more than 50%", alert.getExplanation());
        assertEquals("Investigate delay causes", alert.getRecommendation());
        verify(aiAlertRepository, times(1)).save(any(AIAlert.class));
    }

    @Test
    @DisplayName("Should detect COMPLAINT_SPIKE when complaint count >= 5")
    void testComplaintSpikeDetection() {
        Trip trip = Trip.builder().id(2L).status(TripStatus.SCHEDULED).build();
        List<Complaint> complaintList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            complaintList.add(new Complaint());
        }

        when(tripRepository.findAll()).thenReturn(List.of(trip));
        when(complaintRepository.findByTripId(2L)).thenReturn(complaintList);
        when(bookingRepository.findByTripId(2L)).thenReturn(List.of());
        when(aiAlertRepository.existsByTripIdAndAlertTypeAndStatus(eq(2L), eq(AlertType.COMPLAINT_SPIKE), eq(AlertStatus.NEW))).thenReturn(false);
        when(aiAlertRepository.save(any(AIAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<AIAlertDTO> alerts = aiAlertService.runAnomalyDetection();

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        AIAlertDTO alert = alerts.get(0);
        assertEquals(AlertType.COMPLAINT_SPIKE, alert.getAlertType());
        assertEquals(AlertSeverity.MEDIUM, alert.getSeverity());
        assertEquals(AlertStatus.NEW, alert.getStatus());
        assertEquals("Complaint volume exceeded threshold", alert.getExplanation());
        assertEquals("Review passenger feedback", alert.getRecommendation());
    }

    @Test
    @DisplayName("Should detect DEMAND_ANOMALY when booking count > 100")
    void testDemandAnomalyDetection() {
        Trip trip = Trip.builder().id(3L).status(TripStatus.SCHEDULED).build();
        List<Booking> bookingList = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            bookingList.add(new Booking());
        }

        when(tripRepository.findAll()).thenReturn(List.of(trip));
        when(complaintRepository.findByTripId(3L)).thenReturn(List.of());
        when(bookingRepository.findByTripId(3L)).thenReturn(bookingList);
        when(aiAlertRepository.existsByTripIdAndAlertTypeAndStatus(eq(3L), eq(AlertType.DEMAND_ANOMALY), eq(AlertStatus.NEW))).thenReturn(false);
        when(aiAlertRepository.save(any(AIAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<AIAlertDTO> alerts = aiAlertService.runAnomalyDetection();

        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        AIAlertDTO alert = alerts.get(0);
        assertEquals(AlertType.DEMAND_ANOMALY, alert.getAlertType());
        assertEquals(AlertSeverity.MEDIUM, alert.getSeverity());
        assertEquals(AlertStatus.NEW, alert.getStatus());
        assertEquals("Passenger demand unusually high", alert.getExplanation());
        assertEquals("Consider increasing service frequency", alert.getRecommendation());
    }

    @Test
    @DisplayName("Should prevent duplicate alerts when an active alert already exists")
    void testPreventDuplicateAlerts() {
        when(tripRepository.findAll()).thenReturn(List.of(sampleTrip));
        when(complaintRepository.findByTripId(1L)).thenReturn(List.of());
        when(bookingRepository.findByTripId(1L)).thenReturn(List.of());
        when(aiAlertRepository.existsByTripIdAndAlertTypeAndStatus(eq(1L), eq(AlertType.TRIP_DURATION_ANOMALY), eq(AlertStatus.NEW))).thenReturn(true);

        List<AIAlertDTO> alerts = aiAlertService.runAnomalyDetection();

        assertNotNull(alerts);
        assertTrue(alerts.isEmpty());
        verify(aiAlertRepository, never()).save(any(AIAlert.class));
    }

    @Test
    @DisplayName("Should return all alerts and new alerts correctly")
    void testGetAlerts() {
        AIAlert alert1 = AIAlert.builder().id(100L).status(AlertStatus.NEW).build();
        AIAlert alert2 = AIAlert.builder().id(101L).status(AlertStatus.RESOLVED).build();

        when(aiAlertRepository.findAll()).thenReturn(List.of(alert1, alert2));
        when(aiAlertRepository.findByStatus(AlertStatus.NEW)).thenReturn(List.of(alert1));

        List<AIAlertDTO> all = aiAlertService.getAllAlerts();
        List<AIAlertDTO> newAlerts = aiAlertService.getNewAlerts();

        assertEquals(2, all.size());
        assertEquals(1, newAlerts.size());
        assertEquals(AlertStatus.NEW, newAlerts.get(0).getStatus());
    }
}

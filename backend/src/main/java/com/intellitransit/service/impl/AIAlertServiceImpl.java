package com.intellitransit.service.impl;

import com.intellitransit.entity.AIAlert;
import com.intellitransit.entity.Booking;
import com.intellitransit.entity.Complaint;
import com.intellitransit.entity.Trip;
import com.intellitransit.entity.enums.AlertSeverity;
import com.intellitransit.entity.enums.AlertStatus;
import com.intellitransit.entity.enums.AlertType;
import com.intellitransit.entity.enums.TripStatus;
import com.intellitransit.repository.AIAlertRepository;
import com.intellitransit.repository.BookingRepository;
import com.intellitransit.repository.ComplaintRepository;
import com.intellitransit.repository.TripRepository;
import com.intellitransit.service.AIAlertService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIAlertServiceImpl implements AIAlertService {

    private final AIAlertRepository aiAlertRepository;
    private final TripRepository tripRepository;
    private final ComplaintRepository complaintRepository;
    private final BookingRepository bookingRepository;

    public AIAlertServiceImpl(AIAlertRepository aiAlertRepository,
                              TripRepository tripRepository,
                              ComplaintRepository complaintRepository,
                              BookingRepository bookingRepository) {
        this.aiAlertRepository = aiAlertRepository;
        this.tripRepository = tripRepository;
        this.complaintRepository = complaintRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public List<AIAlert> runAnomalyDetection() {
        List<AIAlert> generatedAlerts = new ArrayList<>();
        List<Trip> trips = tripRepository.findAll();

        for (Trip trip : trips) {
            // 1. TRIP_DURATION_ANOMALY
            if (trip.getStatus() == TripStatus.COMPLETED
                    && trip.getScheduledStart() != null
                    && trip.getScheduledEnd() != null
                    && trip.getActualStart() != null
                    && trip.getActualEnd() != null) {

                long plannedMinutes = ChronoUnit.MINUTES.between(trip.getScheduledStart(), trip.getScheduledEnd());
                long actualMinutes = ChronoUnit.MINUTES.between(trip.getActualStart(), trip.getActualEnd());

                if (plannedMinutes > 0 && actualMinutes > (long) (plannedMinutes * 1.5)) {
                    if (!aiAlertRepository.existsByTripIdAndAlertTypeAndStatus(trip.getId(), AlertType.TRIP_DURATION_ANOMALY, AlertStatus.NEW)) {
                        double ratio = (double) actualMinutes / (double) plannedMinutes;
                        BigDecimal anomalyScore = BigDecimal.valueOf(ratio).setScale(4, RoundingMode.HALF_UP);

                        AIAlert alert = AIAlert.builder()
                                .trip(trip)
                                .alertType(AlertType.TRIP_DURATION_ANOMALY)
                                .severity(AlertSeverity.HIGH)
                                .anomalyScore(anomalyScore)
                                .explanation("Trip duration exceeded expected duration by more than 50%")
                                .recommendation("Investigate delay causes")
                                .status(AlertStatus.NEW)
                                .build();

                        AIAlert saved = aiAlertRepository.save(alert);
                        generatedAlerts.add(saved);
                    }
                }
            }

            // 2. COMPLAINT_SPIKE
            List<Complaint> complaints = complaintRepository.findByTripId(trip.getId());
            if (complaints.size() >= 5) {
                if (!aiAlertRepository.existsByTripIdAndAlertTypeAndStatus(trip.getId(), AlertType.COMPLAINT_SPIKE, AlertStatus.NEW)) {
                    BigDecimal anomalyScore = BigDecimal.valueOf((double) complaints.size() / 5.0).setScale(4, RoundingMode.HALF_UP);

                    AIAlert alert = AIAlert.builder()
                            .trip(trip)
                            .alertType(AlertType.COMPLAINT_SPIKE)
                            .severity(AlertSeverity.MEDIUM)
                            .anomalyScore(anomalyScore)
                            .explanation("Complaint volume exceeded threshold")
                            .recommendation("Review passenger feedback")
                            .status(AlertStatus.NEW)
                            .build();

                    AIAlert saved = aiAlertRepository.save(alert);
                    generatedAlerts.add(saved);
                }
            }

            // 3. DEMAND_ANOMALY
            List<Booking> bookings = bookingRepository.findByTripId(trip.getId());
            if (bookings.size() > 100) {
                if (!aiAlertRepository.existsByTripIdAndAlertTypeAndStatus(trip.getId(), AlertType.DEMAND_ANOMALY, AlertStatus.NEW)) {
                    BigDecimal anomalyScore = BigDecimal.valueOf((double) bookings.size() / 100.0).setScale(4, RoundingMode.HALF_UP);

                    AIAlert alert = AIAlert.builder()
                            .trip(trip)
                            .alertType(AlertType.DEMAND_ANOMALY)
                            .severity(AlertSeverity.MEDIUM)
                            .anomalyScore(anomalyScore)
                            .explanation("Passenger demand unusually high")
                            .recommendation("Consider increasing service frequency")
                            .status(AlertStatus.NEW)
                            .build();

                    AIAlert saved = aiAlertRepository.save(alert);
                    generatedAlerts.add(saved);
                }
            }
        }

        return generatedAlerts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AIAlert> getAllAlerts() {
        return aiAlertRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AIAlert> getNewAlerts() {
        return aiAlertRepository.findByStatus(AlertStatus.NEW);
    }
}

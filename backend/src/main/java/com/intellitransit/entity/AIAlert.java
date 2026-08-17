package com.intellitransit.entity;

import com.intellitransit.entity.enums.AlertSeverity;
import com.intellitransit.entity.enums.AlertStatus;
import com.intellitransit.entity.enums.AlertType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_alerts")
public class AIAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 30)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertSeverity severity;

    @Column(name = "anomaly_score", nullable = false, precision = 5, scale = 4)
    private BigDecimal anomalyScore;

    @Column(name = "detected_at", nullable = false, updatable = false)
    private LocalDateTime detectedAt;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertStatus status = AlertStatus.NEW;

    public AIAlert() {}

    public AIAlert(Long id, Trip trip, AlertType alertType, AlertSeverity severity, BigDecimal anomalyScore, LocalDateTime detectedAt, String explanation, String recommendation, AlertStatus status) {
        this.id = id;
        this.trip = trip;
        this.alertType = alertType;
        this.severity = severity;
        this.anomalyScore = anomalyScore;
        this.detectedAt = detectedAt;
        this.explanation = explanation;
        this.recommendation = recommendation;
        this.status = status != null ? status : AlertStatus.NEW;
    }

    @PrePersist
    protected void onCreate() {
        this.detectedAt = LocalDateTime.now();
    }

    public static AIAlertBuilder builder() {
        return new AIAlertBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public AlertType getAlertType() { return alertType; }
    public void setAlertType(AlertType alertType) { this.alertType = alertType; }

    public AlertSeverity getSeverity() { return severity; }
    public void setSeverity(AlertSeverity severity) { this.severity = severity; }

    public BigDecimal getAnomalyScore() { return anomalyScore; }
    public void setAnomalyScore(BigDecimal anomalyScore) { this.anomalyScore = anomalyScore; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public AlertStatus getStatus() { return status; }
    public void setStatus(AlertStatus status) { this.status = status; }

    public static class AIAlertBuilder {
        private Long id;
        private Trip trip;
        private AlertType alertType;
        private AlertSeverity severity;
        private BigDecimal anomalyScore;
        private LocalDateTime detectedAt;
        private String explanation;
        private String recommendation;
        private AlertStatus status = AlertStatus.NEW;

        public AIAlertBuilder id(Long id) { this.id = id; return this; }
        public AIAlertBuilder trip(Trip trip) { this.trip = trip; return this; }
        public AIAlertBuilder alertType(AlertType alertType) { this.alertType = alertType; return this; }
        public AIAlertBuilder severity(AlertSeverity severity) { this.severity = severity; return this; }
        public AIAlertBuilder anomalyScore(BigDecimal anomalyScore) { this.anomalyScore = anomalyScore; return this; }
        public AIAlertBuilder detectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; return this; }
        public AIAlertBuilder explanation(String explanation) { this.explanation = explanation; return this; }
        public AIAlertBuilder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public AIAlertBuilder status(AlertStatus status) { this.status = status; return this; }

        public AIAlert build() {
            return new AIAlert(id, trip, alertType, severity, anomalyScore, detectedAt, explanation, recommendation, status);
        }
    }
}

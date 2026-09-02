package com.intellitransit.dto;

import com.intellitransit.entity.enums.AlertSeverity;
import com.intellitransit.entity.enums.AlertStatus;
import com.intellitransit.entity.enums.AlertType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AIAlertDTO {

    private Long id;
    private Long tripId;
    private String gtfsTripId;
    private String routeNumber;
    private AlertType alertType;
    private AlertSeverity severity;
    private BigDecimal anomalyScore;
    private LocalDateTime detectedAt;
    private String explanation;
    private String recommendation;
    private AlertStatus status;

    public AIAlertDTO() {}

    public AIAlertDTO(Long id, Long tripId, String gtfsTripId, String routeNumber, AlertType alertType, AlertSeverity severity, BigDecimal anomalyScore, LocalDateTime detectedAt, String explanation, String recommendation, AlertStatus status) {
        this.id = id;
        this.tripId = tripId;
        this.gtfsTripId = gtfsTripId;
        this.routeNumber = routeNumber;
        this.alertType = alertType;
        this.severity = severity;
        this.anomalyScore = anomalyScore;
        this.detectedAt = detectedAt;
        this.explanation = explanation;
        this.recommendation = recommendation;
        this.status = status;
    }

    public static AIAlertDTOBuilder builder() {
        return new AIAlertDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getGtfsTripId() { return gtfsTripId; }
    public void setGtfsTripId(String gtfsTripId) { this.gtfsTripId = gtfsTripId; }

    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }

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

    public static class AIAlertDTOBuilder {
        private Long id;
        private Long tripId;
        private String gtfsTripId;
        private String routeNumber;
        private AlertType alertType;
        private AlertSeverity severity;
        private BigDecimal anomalyScore;
        private LocalDateTime detectedAt;
        private String explanation;
        private String recommendation;
        private AlertStatus status;

        public AIAlertDTOBuilder id(Long id) { this.id = id; return this; }
        public AIAlertDTOBuilder tripId(Long tripId) { this.tripId = tripId; return this; }
        public AIAlertDTOBuilder gtfsTripId(String gtfsTripId) { this.gtfsTripId = gtfsTripId; return this; }
        public AIAlertDTOBuilder routeNumber(String routeNumber) { this.routeNumber = routeNumber; return this; }
        public AIAlertDTOBuilder alertType(AlertType alertType) { this.alertType = alertType; return this; }
        public AIAlertDTOBuilder severity(AlertSeverity severity) { this.severity = severity; return this; }
        public AIAlertDTOBuilder anomalyScore(BigDecimal anomalyScore) { this.anomalyScore = anomalyScore; return this; }
        public AIAlertDTOBuilder detectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; return this; }
        public AIAlertDTOBuilder explanation(String explanation) { this.explanation = explanation; return this; }
        public AIAlertDTOBuilder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public AIAlertDTOBuilder status(AlertStatus status) { this.status = status; return this; }

        public AIAlertDTO build() {
            return new AIAlertDTO(id, tripId, gtfsTripId, routeNumber, alertType, severity, anomalyScore, detectedAt, explanation, recommendation, status);
        }
    }
}

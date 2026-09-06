package com.intellitransit.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DecisionRecommendationDTO {

    private Long recommendationId;
    private Long alertId;
    private String alertType;
    private String severity;
    private String actionType; // DISPATCH_VEHICLE, MONITOR, REASSIGN_VEHICLE, REVIEW_ROUTE, INSPECT_OPERATION
    private String priority;   // CRITICAL, HIGH, MEDIUM, LOW
    private String title;
    private String explanation;
    private List<String> evidenceList;
    private Long recommendedVehicleId;
    private String recommendedBusNumber;
    private String candidateAvailability;
    private Long affectedTripId;
    private Long affectedRouteId;
    private String affectedRouteNumber;
    private String status;     // PENDING, DISPATCHED, COMPLETED, REJECTED
    private LocalDateTime createdAt;

    public DecisionRecommendationDTO() {}

    public DecisionRecommendationDTO(Long recommendationId, Long alertId, String alertType, String severity,
                                   String actionType, String priority, String title, String explanation,
                                   List<String> evidenceList, Long recommendedVehicleId, String recommendedBusNumber,
                                   String candidateAvailability, Long affectedTripId, Long affectedRouteId,
                                   String affectedRouteNumber, String status, LocalDateTime createdAt) {
        this.recommendationId = recommendationId;
        this.alertId = alertId;
        this.alertType = alertType;
        this.severity = severity;
        this.actionType = actionType;
        this.priority = priority;
        this.title = title;
        this.explanation = explanation;
        this.evidenceList = evidenceList;
        this.recommendedVehicleId = recommendedVehicleId;
        this.recommendedBusNumber = recommendedBusNumber;
        this.candidateAvailability = candidateAvailability;
        this.affectedTripId = affectedTripId;
        this.affectedRouteId = affectedRouteId;
        this.affectedRouteNumber = affectedRouteNumber;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static DecisionRecommendationDTOBuilder builder() {
        return new DecisionRecommendationDTOBuilder();
    }

    public Long getRecommendationId() { return recommendationId; }
    public void setRecommendationId(Long recommendationId) { this.recommendationId = recommendationId; }

    public Long getAlertId() { return alertId; }
    public void setAlertId(Long alertId) { this.alertId = alertId; }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<String> getEvidenceList() { return evidenceList; }
    public void setEvidenceList(List<String> evidenceList) { this.evidenceList = evidenceList; }

    public Long getRecommendedVehicleId() { return recommendedVehicleId; }
    public void setRecommendedVehicleId(Long recommendedVehicleId) { this.recommendedVehicleId = recommendedVehicleId; }

    public String getRecommendedBusNumber() { return recommendedBusNumber; }
    public void setRecommendedBusNumber(String recommendedBusNumber) { this.recommendedBusNumber = recommendedBusNumber; }

    public String getCandidateAvailability() { return candidateAvailability; }
    public void setCandidateAvailability(String candidateAvailability) { this.candidateAvailability = candidateAvailability; }

    public Long getAffectedTripId() { return affectedTripId; }
    public void setAffectedTripId(Long affectedTripId) { this.affectedTripId = affectedTripId; }

    public Long getAffectedRouteId() { return affectedRouteId; }
    public void setAffectedRouteId(Long affectedRouteId) { this.affectedRouteId = affectedRouteId; }

    public String getAffectedRouteNumber() { return affectedRouteNumber; }
    public void setAffectedRouteNumber(String affectedRouteNumber) { this.affectedRouteNumber = affectedRouteNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class DecisionRecommendationDTOBuilder {
        private Long recommendationId;
        private Long alertId;
        private String alertType;
        private String severity;
        private String actionType;
        private String priority;
        private String title;
        private String explanation;
        private List<String> evidenceList;
        private Long recommendedVehicleId;
        private String recommendedBusNumber;
        private String candidateAvailability;
        private Long affectedTripId;
        private Long affectedRouteId;
        private String affectedRouteNumber;
        private String status;
        private LocalDateTime createdAt;

        public DecisionRecommendationDTOBuilder recommendationId(Long recommendationId) { this.recommendationId = recommendationId; return this; }
        public DecisionRecommendationDTOBuilder alertId(Long alertId) { this.alertId = alertId; return this; }
        public DecisionRecommendationDTOBuilder alertType(String alertType) { this.alertType = alertType; return this; }
        public DecisionRecommendationDTOBuilder severity(String severity) { this.severity = severity; return this; }
        public DecisionRecommendationDTOBuilder actionType(String actionType) { this.actionType = actionType; return this; }
        public DecisionRecommendationDTOBuilder priority(String priority) { this.priority = priority; return this; }
        public DecisionRecommendationDTOBuilder title(String title) { this.title = title; return this; }
        public DecisionRecommendationDTOBuilder explanation(String explanation) { this.explanation = explanation; return this; }
        public DecisionRecommendationDTOBuilder evidenceList(List<String> evidenceList) { this.evidenceList = evidenceList; return this; }
        public DecisionRecommendationDTOBuilder recommendedVehicleId(Long recommendedVehicleId) { this.recommendedVehicleId = recommendedVehicleId; return this; }
        public DecisionRecommendationDTOBuilder recommendedBusNumber(String recommendedBusNumber) { this.recommendedBusNumber = recommendedBusNumber; return this; }
        public DecisionRecommendationDTOBuilder candidateAvailability(String candidateAvailability) { this.candidateAvailability = candidateAvailability; return this; }
        public DecisionRecommendationDTOBuilder affectedTripId(Long affectedTripId) { this.affectedTripId = affectedTripId; return this; }
        public DecisionRecommendationDTOBuilder affectedRouteId(Long affectedRouteId) { this.affectedRouteId = affectedRouteId; return this; }
        public DecisionRecommendationDTOBuilder affectedRouteNumber(String affectedRouteNumber) { this.affectedRouteNumber = affectedRouteNumber; return this; }
        public DecisionRecommendationDTOBuilder status(String status) { this.status = status; return this; }
        public DecisionRecommendationDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public DecisionRecommendationDTO build() {
            return new DecisionRecommendationDTO(recommendationId, alertId, alertType, severity, actionType, priority,
                    title, explanation, evidenceList, recommendedVehicleId, recommendedBusNumber, candidateAvailability,
                    affectedTripId, affectedRouteId, affectedRouteNumber, status, createdAt);
        }
    }
}

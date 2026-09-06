package com.intellitransit.dto;

import java.time.LocalDateTime;

public class SimulateDispatchResponse {

    private Long actionId;
    private Long recommendationId;
    private Long tripId;
    private Long vehicleId;
    private String busNumber;
    private String routeNumber;
    private String actionType;
    private String status; // SIMULATED, COMPLETED, REJECTED
    private LocalDateTime executedAt;
    private String explanation;
    private String impactSummary;
    private Long updatedEtaSeconds;
    private String updatedConfidence;

    public SimulateDispatchResponse() {}

    public SimulateDispatchResponse(Long actionId, Long recommendationId, Long tripId, Long vehicleId, String busNumber,
                                    String routeNumber, String actionType, String status, LocalDateTime executedAt,
                                    String explanation, String impactSummary, Long updatedEtaSeconds, String updatedConfidence) {
        this.actionId = actionId;
        this.recommendationId = recommendationId;
        this.tripId = tripId;
        this.vehicleId = vehicleId;
        this.busNumber = busNumber;
        this.routeNumber = routeNumber;
        this.actionType = actionType;
        this.status = status;
        this.executedAt = executedAt;
        this.explanation = explanation;
        this.impactSummary = impactSummary;
        this.updatedEtaSeconds = updatedEtaSeconds;
        this.updatedConfidence = updatedConfidence;
    }

    public static SimulateDispatchResponseBuilder builder() {
        return new SimulateDispatchResponseBuilder();
    }

    public Long getActionId() { return actionId; }
    public void setActionId(Long actionId) { this.actionId = actionId; }

    public Long getRecommendationId() { return recommendationId; }
    public void setRecommendationId(Long recommendationId) { this.recommendationId = recommendationId; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getImpactSummary() { return impactSummary; }
    public void setImpactSummary(String impactSummary) { this.impactSummary = impactSummary; }

    public Long getUpdatedEtaSeconds() { return updatedEtaSeconds; }
    public void setUpdatedEtaSeconds(Long updatedEtaSeconds) { this.updatedEtaSeconds = updatedEtaSeconds; }

    public String getUpdatedConfidence() { return updatedConfidence; }
    public void setUpdatedConfidence(String updatedConfidence) { this.updatedConfidence = updatedConfidence; }

    public static class SimulateDispatchResponseBuilder {
        private Long actionId;
        private Long recommendationId;
        private Long tripId;
        private Long vehicleId;
        private String busNumber;
        private String routeNumber;
        private String actionType;
        private String status;
        private LocalDateTime executedAt;
        private String explanation;
        private String impactSummary;
        private Long updatedEtaSeconds;
        private String updatedConfidence;

        public SimulateDispatchResponseBuilder actionId(Long actionId) { this.actionId = actionId; return this; }
        public SimulateDispatchResponseBuilder recommendationId(Long recommendationId) { this.recommendationId = recommendationId; return this; }
        public SimulateDispatchResponseBuilder tripId(Long tripId) { this.tripId = tripId; return this; }
        public SimulateDispatchResponseBuilder vehicleId(Long vehicleId) { this.vehicleId = vehicleId; return this; }
        public SimulateDispatchResponseBuilder busNumber(String busNumber) { this.busNumber = busNumber; return this; }
        public SimulateDispatchResponseBuilder routeNumber(String routeNumber) { this.routeNumber = routeNumber; return this; }
        public SimulateDispatchResponseBuilder actionType(String actionType) { this.actionType = actionType; return this; }
        public SimulateDispatchResponseBuilder status(String status) { this.status = status; return this; }
        public SimulateDispatchResponseBuilder executedAt(LocalDateTime executedAt) { this.executedAt = executedAt; return this; }
        public SimulateDispatchResponseBuilder explanation(String explanation) { this.explanation = explanation; return this; }
        public SimulateDispatchResponseBuilder impactSummary(String impactSummary) { this.impactSummary = impactSummary; return this; }
        public SimulateDispatchResponseBuilder updatedEtaSeconds(Long updatedEtaSeconds) { this.updatedEtaSeconds = updatedEtaSeconds; return this; }
        public SimulateDispatchResponseBuilder updatedConfidence(String updatedConfidence) { this.updatedConfidence = updatedConfidence; return this; }

        public SimulateDispatchResponse build() {
            return new SimulateDispatchResponse(actionId, recommendationId, tripId, vehicleId, busNumber, routeNumber,
                    actionType, status, executedAt, explanation, impactSummary, updatedEtaSeconds, updatedConfidence);
        }
    }
}

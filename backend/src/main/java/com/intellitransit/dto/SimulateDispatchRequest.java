package com.intellitransit.dto;

public class SimulateDispatchRequest {

    private Long recommendationId;
    private Long alertId;
    private Long tripId;
    private Long vehicleId;
    private String actionType;
    private String notes;

    public SimulateDispatchRequest() {}

    public SimulateDispatchRequest(Long recommendationId, Long alertId, Long tripId, Long vehicleId, String actionType, String notes) {
        this.recommendationId = recommendationId;
        this.alertId = alertId;
        this.tripId = tripId;
        this.vehicleId = vehicleId;
        this.actionType = actionType;
        this.notes = notes;
    }

    public Long getRecommendationId() { return recommendationId; }
    public void setRecommendationId(Long recommendationId) { this.recommendationId = recommendationId; }

    public Long getAlertId() { return alertId; }
    public void setAlertId(Long alertId) { this.alertId = alertId; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

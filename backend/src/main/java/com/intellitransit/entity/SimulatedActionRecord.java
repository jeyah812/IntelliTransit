package com.intellitransit.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "simulated_actions")
public class SimulatedActionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alert_id")
    private Long alertId;

    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "bus_id")
    private Long busId;

    @Column(name = "bus_number", length = 50)
    private String busNumber;

    @Column(name = "route_number", length = 50)
    private String routeNumber;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Column(nullable = false, length = 30)
    private String status = "SIMULATED";

    @Column(name = "executed_at", nullable = false, updatable = false)
    private LocalDateTime executedAt;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "impact_summary", columnDefinition = "TEXT")
    private String impactSummary;

    public SimulatedActionRecord() {}

    public SimulatedActionRecord(Long id, Long alertId, Long tripId, Long busId, String busNumber, String routeNumber,
                                 String actionType, String status, LocalDateTime executedAt, String explanation, String impactSummary) {
        this.id = id;
        this.alertId = alertId;
        this.tripId = tripId;
        this.busId = busId;
        this.busNumber = busNumber;
        this.routeNumber = routeNumber;
        this.actionType = actionType;
        this.status = status != null ? status : "SIMULATED";
        this.executedAt = executedAt;
        this.explanation = explanation;
        this.impactSummary = impactSummary;
    }

    @PrePersist
    protected void onCreate() {
        this.executedAt = LocalDateTime.now();
    }

    public static SimulatedActionRecordBuilder builder() {
        return new SimulatedActionRecordBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAlertId() { return alertId; }
    public void setAlertId(Long alertId) { this.alertId = alertId; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

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

    public static class SimulatedActionRecordBuilder {
        private Long id;
        private Long alertId;
        private Long tripId;
        private Long busId;
        private String busNumber;
        private String routeNumber;
        private String actionType;
        private String status = "SIMULATED";
        private LocalDateTime executedAt;
        private String explanation;
        private String impactSummary;

        public SimulatedActionRecordBuilder id(Long id) { this.id = id; return this; }
        public SimulatedActionRecordBuilder alertId(Long alertId) { this.alertId = alertId; return this; }
        public SimulatedActionRecordBuilder tripId(Long tripId) { this.tripId = tripId; return this; }
        public SimulatedActionRecordBuilder busId(Long busId) { this.busId = busId; return this; }
        public SimulatedActionRecordBuilder busNumber(String busNumber) { this.busNumber = busNumber; return this; }
        public SimulatedActionRecordBuilder routeNumber(String routeNumber) { this.routeNumber = routeNumber; return this; }
        public SimulatedActionRecordBuilder actionType(String actionType) { this.actionType = actionType; return this; }
        public SimulatedActionRecordBuilder status(String status) { this.status = status; return this; }
        public SimulatedActionRecordBuilder executedAt(LocalDateTime executedAt) { this.executedAt = executedAt; return this; }
        public SimulatedActionRecordBuilder explanation(String explanation) { this.explanation = explanation; return this; }
        public SimulatedActionRecordBuilder impactSummary(String impactSummary) { this.impactSummary = impactSummary; return this; }

        public SimulatedActionRecord build() {
            return new SimulatedActionRecord(id, alertId, tripId, busId, busNumber, routeNumber, actionType, status, executedAt, explanation, impactSummary);
        }
    }
}

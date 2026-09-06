package com.intellitransit.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SimulatedVehicleDTO {

    private Long tripId;
    private String gtfsTripId;
    private Long routeId;
    private String routeNumber;
    private String routeName;
    private String busNumber;
    private String driverName;
    private Long currentStopId;
    private String currentStopName;
    private Long nextStopId;
    private String nextStopName;
    private Integer stopSequence;
    private BigDecimal currentLatitude;
    private BigDecimal currentLongitude;
    private Double progressBetweenStops; // 0.0 to 1.0
    private String status; // SCHEDULED, EN_ROUTE, AT_STOP, DELAYED, COMPLETED
    private Integer delaySeconds;
    private LocalDateTime lastUpdatedAt;

    public SimulatedVehicleDTO() {}

    public SimulatedVehicleDTO(Long tripId, String gtfsTripId, Long routeId, String routeNumber, String routeName,
                               String busNumber, String driverName, Long currentStopId, String currentStopName,
                               Long nextStopId, String nextStopName, Integer stopSequence, BigDecimal currentLatitude,
                               BigDecimal currentLongitude, Double progressBetweenStops, String status,
                               Integer delaySeconds, LocalDateTime lastUpdatedAt) {
        this.tripId = tripId;
        this.gtfsTripId = gtfsTripId;
        this.routeId = routeId;
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.busNumber = busNumber;
        this.driverName = driverName;
        this.currentStopId = currentStopId;
        this.currentStopName = currentStopName;
        this.nextStopId = nextStopId;
        this.nextStopName = nextStopName;
        this.stopSequence = stopSequence;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.progressBetweenStops = progressBetweenStops;
        this.status = status;
        this.delaySeconds = delaySeconds;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public static SimulatedVehicleDTOBuilder builder() {
        return new SimulatedVehicleDTOBuilder();
    }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getGtfsTripId() { return gtfsTripId; }
    public void setGtfsTripId(String gtfsTripId) { this.gtfsTripId = gtfsTripId; }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public Long getCurrentStopId() { return currentStopId; }
    public void setCurrentStopId(Long currentStopId) { this.currentStopId = currentStopId; }

    public String getCurrentStopName() { return currentStopName; }
    public void setCurrentStopName(String currentStopName) { this.currentStopName = currentStopName; }

    public Long getNextStopId() { return nextStopId; }
    public void setNextStopId(Long nextStopId) { this.nextStopId = nextStopId; }

    public String getNextStopName() { return nextStopName; }
    public void setNextStopName(String nextStopName) { this.nextStopName = nextStopName; }

    public Integer getStopSequence() { return stopSequence; }
    public void setStopSequence(Integer stopSequence) { this.stopSequence = stopSequence; }

    public BigDecimal getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(BigDecimal currentLatitude) { this.currentLatitude = currentLatitude; }

    public BigDecimal getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(BigDecimal currentLongitude) { this.currentLongitude = currentLongitude; }

    public Double getProgressBetweenStops() { return progressBetweenStops; }
    public void setProgressBetweenStops(Double progressBetweenStops) { this.progressBetweenStops = progressBetweenStops; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getDelaySeconds() { return delaySeconds; }
    public void setDelaySeconds(Integer delaySeconds) { this.delaySeconds = delaySeconds; }

    public LocalDateTime getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    public static class SimulatedVehicleDTOBuilder {
        private Long tripId;
        private String gtfsTripId;
        private Long routeId;
        private String routeNumber;
        private String routeName;
        private String busNumber;
        private String driverName;
        private Long currentStopId;
        private String currentStopName;
        private Long nextStopId;
        private String nextStopName;
        private Integer stopSequence;
        private BigDecimal currentLatitude;
        private BigDecimal currentLongitude;
        private Double progressBetweenStops;
        private String status;
        private Integer delaySeconds;
        private LocalDateTime lastUpdatedAt;

        public SimulatedVehicleDTOBuilder tripId(Long tripId) { this.tripId = tripId; return this; }
        public SimulatedVehicleDTOBuilder gtfsTripId(String gtfsTripId) { this.gtfsTripId = gtfsTripId; return this; }
        public SimulatedVehicleDTOBuilder routeId(Long routeId) { this.routeId = routeId; return this; }
        public SimulatedVehicleDTOBuilder routeNumber(String routeNumber) { this.routeNumber = routeNumber; return this; }
        public SimulatedVehicleDTOBuilder routeName(String routeName) { this.routeName = routeName; return this; }
        public SimulatedVehicleDTOBuilder busNumber(String busNumber) { this.busNumber = busNumber; return this; }
        public SimulatedVehicleDTOBuilder driverName(String driverName) { this.driverName = driverName; return this; }
        public SimulatedVehicleDTOBuilder currentStopId(Long currentStopId) { this.currentStopId = currentStopId; return this; }
        public SimulatedVehicleDTOBuilder currentStopName(String currentStopName) { this.currentStopName = currentStopName; return this; }
        public SimulatedVehicleDTOBuilder nextStopId(Long nextStopId) { this.nextStopId = nextStopId; return this; }
        public SimulatedVehicleDTOBuilder nextStopName(String nextStopName) { this.nextStopName = nextStopName; return this; }
        public SimulatedVehicleDTOBuilder stopSequence(Integer stopSequence) { this.stopSequence = stopSequence; return this; }
        public SimulatedVehicleDTOBuilder currentLatitude(BigDecimal currentLatitude) { this.currentLatitude = currentLatitude; return this; }
        public SimulatedVehicleDTOBuilder currentLongitude(BigDecimal currentLongitude) { this.currentLongitude = currentLongitude; return this; }
        public SimulatedVehicleDTOBuilder progressBetweenStops(Double progressBetweenStops) { this.progressBetweenStops = progressBetweenStops; return this; }
        public SimulatedVehicleDTOBuilder status(String status) { this.status = status; return this; }
        public SimulatedVehicleDTOBuilder delaySeconds(Integer delaySeconds) { this.delaySeconds = delaySeconds; return this; }
        public SimulatedVehicleDTOBuilder lastUpdatedAt(LocalDateTime lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; return this; }

        public SimulatedVehicleDTO build() {
            return new SimulatedVehicleDTO(tripId, gtfsTripId, routeId, routeNumber, routeName, busNumber, driverName,
                    currentStopId, currentStopName, nextStopId, nextStopName, stopSequence, currentLatitude,
                    currentLongitude, progressBetweenStops, status, delaySeconds, lastUpdatedAt);
        }
    }
}

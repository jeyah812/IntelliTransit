package com.intellitransit.dto;

import java.time.LocalDateTime;

public class EstimatedArrivalDTO {

    private Long tripId;
    private String gtfsTripId;
    private String busNumber;
    private Long routeId;
    private String routeNumber;
    private String routeName;
    private Long destinationStopId;
    private String destinationStopName;
    private Long currentStopId;
    private String currentStopName;
    private Long nextStopId;
    private String nextStopName;
    private LocalDateTime estimatedArrivalTime;
    private Long etaSeconds;
    private Integer currentDelaySeconds;
    private String confidence; // HIGH, MEDIUM, LOW
    private LocalDateTime calculatedAt;

    public EstimatedArrivalDTO() {}

    public EstimatedArrivalDTO(Long tripId, String gtfsTripId, String busNumber, Long routeId, String routeNumber,
                               String routeName, Long destinationStopId, String destinationStopName, Long currentStopId,
                               String currentStopName, Long nextStopId, String nextStopName, LocalDateTime estimatedArrivalTime,
                               Long etaSeconds, Integer currentDelaySeconds, String confidence, LocalDateTime calculatedAt) {
        this.tripId = tripId;
        this.gtfsTripId = gtfsTripId;
        this.busNumber = busNumber;
        this.routeId = routeId;
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.destinationStopId = destinationStopId;
        this.destinationStopName = destinationStopName;
        this.currentStopId = currentStopId;
        this.currentStopName = currentStopName;
        this.nextStopId = nextStopId;
        this.nextStopName = nextStopName;
        this.estimatedArrivalTime = estimatedArrivalTime;
        this.etaSeconds = etaSeconds;
        this.currentDelaySeconds = currentDelaySeconds;
        this.confidence = confidence;
        this.calculatedAt = calculatedAt;
    }

    public static EstimatedArrivalDTOBuilder builder() {
        return new EstimatedArrivalDTOBuilder();
    }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getGtfsTripId() { return gtfsTripId; }
    public void setGtfsTripId(String gtfsTripId) { this.gtfsTripId = gtfsTripId; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public Long getDestinationStopId() { return destinationStopId; }
    public void setDestinationStopId(Long destinationStopId) { this.destinationStopId = destinationStopId; }

    public String getDestinationStopName() { return destinationStopName; }
    public void setDestinationStopName(String destinationStopName) { this.destinationStopName = destinationStopName; }

    public Long getCurrentStopId() { return currentStopId; }
    public void setCurrentStopId(Long currentStopId) { this.currentStopId = currentStopId; }

    public String getCurrentStopName() { return currentStopName; }
    public void setCurrentStopName(String currentStopName) { this.currentStopName = currentStopName; }

    public Long getNextStopId() { return nextStopId; }
    public void setNextStopId(Long nextStopId) { this.nextStopId = nextStopId; }

    public String getNextStopName() { return nextStopName; }
    public void setNextStopName(String nextStopName) { this.nextStopName = nextStopName; }

    public LocalDateTime getEstimatedArrivalTime() { return estimatedArrivalTime; }
    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) { this.estimatedArrivalTime = estimatedArrivalTime; }

    public Long getEtaSeconds() { return etaSeconds; }
    public void setEtaSeconds(Long etaSeconds) { this.etaSeconds = etaSeconds; }

    public Integer getCurrentDelaySeconds() { return currentDelaySeconds; }
    public void setCurrentDelaySeconds(Integer currentDelaySeconds) { this.currentDelaySeconds = currentDelaySeconds; }

    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }

    public static class EstimatedArrivalDTOBuilder {
        private Long tripId;
        private String gtfsTripId;
        private String busNumber;
        private Long routeId;
        private String routeNumber;
        private String routeName;
        private Long destinationStopId;
        private String destinationStopName;
        private Long currentStopId;
        private String currentStopName;
        private Long nextStopId;
        private String nextStopName;
        private LocalDateTime estimatedArrivalTime;
        private Long etaSeconds;
        private Integer currentDelaySeconds;
        private String confidence;
        private LocalDateTime calculatedAt;

        public EstimatedArrivalDTOBuilder tripId(Long tripId) { this.tripId = tripId; return this; }
        public EstimatedArrivalDTOBuilder gtfsTripId(String gtfsTripId) { this.gtfsTripId = gtfsTripId; return this; }
        public EstimatedArrivalDTOBuilder busNumber(String busNumber) { this.busNumber = busNumber; return this; }
        public EstimatedArrivalDTOBuilder routeId(Long routeId) { this.routeId = routeId; return this; }
        public EstimatedArrivalDTOBuilder routeNumber(String routeNumber) { this.routeNumber = routeNumber; return this; }
        public EstimatedArrivalDTOBuilder routeName(String routeName) { this.routeName = routeName; return this; }
        public EstimatedArrivalDTOBuilder destinationStopId(Long destinationStopId) { this.destinationStopId = destinationStopId; return this; }
        public EstimatedArrivalDTOBuilder destinationStopName(String destinationStopName) { this.destinationStopName = destinationStopName; return this; }
        public EstimatedArrivalDTOBuilder currentStopId(Long currentStopId) { this.currentStopId = currentStopId; return this; }
        public EstimatedArrivalDTOBuilder currentStopName(String currentStopName) { this.currentStopName = currentStopName; return this; }
        public EstimatedArrivalDTOBuilder nextStopId(Long nextStopId) { this.nextStopId = nextStopId; return this; }
        public EstimatedArrivalDTOBuilder nextStopName(String nextStopName) { this.nextStopName = nextStopName; return this; }
        public EstimatedArrivalDTOBuilder estimatedArrivalTime(LocalDateTime estimatedArrivalTime) { this.estimatedArrivalTime = estimatedArrivalTime; return this; }
        public EstimatedArrivalDTOBuilder etaSeconds(Long etaSeconds) { this.etaSeconds = etaSeconds; return this; }
        public EstimatedArrivalDTOBuilder currentDelaySeconds(Integer currentDelaySeconds) { this.currentDelaySeconds = currentDelaySeconds; return this; }
        public EstimatedArrivalDTOBuilder confidence(String confidence) { this.confidence = confidence; return this; }
        public EstimatedArrivalDTOBuilder calculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; return this; }

        public EstimatedArrivalDTO build() {
            return new EstimatedArrivalDTO(tripId, gtfsTripId, busNumber, routeId, routeNumber, routeName,
                    destinationStopId, destinationStopName, currentStopId, currentStopName, nextStopId, nextStopName,
                    estimatedArrivalTime, etaSeconds, currentDelaySeconds, confidence, calculatedAt);
        }
    }
}

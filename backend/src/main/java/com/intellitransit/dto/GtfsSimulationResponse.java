package com.intellitransit.dto;

import java.time.LocalDateTime;

public class GtfsSimulationResponse {

    private String batchId;
    private long tripsGenerated;
    private long logsCreated;
    private double avgArrivalDelayMinutes;
    private String status;
    private String message;
    private LocalDateTime generatedAt;

    public GtfsSimulationResponse() {}

    public GtfsSimulationResponse(String batchId, long tripsGenerated, long logsCreated, double avgArrivalDelayMinutes, String status, String message, LocalDateTime generatedAt) {
        this.batchId = batchId;
        this.tripsGenerated = tripsGenerated;
        this.logsCreated = logsCreated;
        this.avgArrivalDelayMinutes = avgArrivalDelayMinutes;
        this.status = status;
        this.message = message;
        this.generatedAt = generatedAt;
    }

    public static GtfsSimulationResponseBuilder builder() {
        return new GtfsSimulationResponseBuilder();
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public long getTripsGenerated() { return tripsGenerated; }
    public void setTripsGenerated(long tripsGenerated) { this.tripsGenerated = tripsGenerated; }

    public long getLogsCreated() { return logsCreated; }
    public void setLogsCreated(long logsCreated) { this.logsCreated = logsCreated; }

    public double getAvgArrivalDelayMinutes() { return avgArrivalDelayMinutes; }
    public void setAvgArrivalDelayMinutes(double avgArrivalDelayMinutes) { this.avgArrivalDelayMinutes = avgArrivalDelayMinutes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public static class GtfsSimulationResponseBuilder {
        private String batchId;
        private long tripsGenerated;
        private long logsCreated;
        private double avgArrivalDelayMinutes;
        private String status;
        private String message;
        private LocalDateTime generatedAt;

        public GtfsSimulationResponseBuilder batchId(String batchId) { this.batchId = batchId; return this; }
        public GtfsSimulationResponseBuilder tripsGenerated(long tripsGenerated) { this.tripsGenerated = tripsGenerated; return this; }
        public GtfsSimulationResponseBuilder logsCreated(long logsCreated) { this.logsCreated = logsCreated; return this; }
        public GtfsSimulationResponseBuilder avgArrivalDelayMinutes(double avgArrivalDelayMinutes) { this.avgArrivalDelayMinutes = avgArrivalDelayMinutes; return this; }
        public GtfsSimulationResponseBuilder status(String status) { this.status = status; return this; }
        public GtfsSimulationResponseBuilder message(String message) { this.message = message; return this; }
        public GtfsSimulationResponseBuilder generatedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; return this; }

        public GtfsSimulationResponse build() {
            return new GtfsSimulationResponse(batchId, tripsGenerated, logsCreated, avgArrivalDelayMinutes, status, message, generatedAt);
        }
    }
}

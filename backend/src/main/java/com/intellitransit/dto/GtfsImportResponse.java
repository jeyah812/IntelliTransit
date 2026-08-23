package com.intellitransit.dto;

import java.time.LocalDateTime;

public class GtfsImportResponse {

    private String batchId;
    private LocalDateTime importedAt;
    private long totalRoutesImported;
    private long totalStopsImported;
    private long totalTripsStaged;
    private long totalStopTimesStaged;
    private long quarantinedRecordsCount;
    private String status;
    private String message;

    public GtfsImportResponse() {}

    public GtfsImportResponse(String batchId, LocalDateTime importedAt, long totalRoutesImported, long totalStopsImported, long totalTripsStaged, long totalStopTimesStaged, long quarantinedRecordsCount, String status, String message) {
        this.batchId = batchId;
        this.importedAt = importedAt;
        this.totalRoutesImported = totalRoutesImported;
        this.totalStopsImported = totalStopsImported;
        this.totalTripsStaged = totalTripsStaged;
        this.totalStopTimesStaged = totalStopTimesStaged;
        this.quarantinedRecordsCount = quarantinedRecordsCount;
        this.status = status;
        this.message = message;
    }

    public static GtfsImportResponseBuilder builder() {
        return new GtfsImportResponseBuilder();
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public LocalDateTime getImportedAt() { return importedAt; }
    public void setImportedAt(LocalDateTime importedAt) { this.importedAt = importedAt; }

    public long getTotalRoutesImported() { return totalRoutesImported; }
    public void setTotalRoutesImported(long totalRoutesImported) { this.totalRoutesImported = totalRoutesImported; }

    public long getTotalStopsImported() { return totalStopsImported; }
    public void setTotalStopsImported(long totalStopsImported) { this.totalStopsImported = totalStopsImported; }

    public long getTotalTripsStaged() { return totalTripsStaged; }
    public void setTotalTripsStaged(long totalTripsStaged) { this.totalTripsStaged = totalTripsStaged; }

    public long getTotalStopTimesStaged() { return totalStopTimesStaged; }
    public void setTotalStopTimesStaged(long totalStopTimesStaged) { this.totalStopTimesStaged = totalStopTimesStaged; }

    public long getQuarantinedRecordsCount() { return quarantinedRecordsCount; }
    public void setQuarantinedRecordsCount(long quarantinedRecordsCount) { this.quarantinedRecordsCount = quarantinedRecordsCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static class GtfsImportResponseBuilder {
        private String batchId;
        private LocalDateTime importedAt;
        private long totalRoutesImported;
        private long totalStopsImported;
        private long totalTripsStaged;
        private long totalStopTimesStaged;
        private long quarantinedRecordsCount;
        private String status;
        private String message;

        public GtfsImportResponseBuilder batchId(String batchId) { this.batchId = batchId; return this; }
        public GtfsImportResponseBuilder importedAt(LocalDateTime importedAt) { this.importedAt = importedAt; return this; }
        public GtfsImportResponseBuilder totalRoutesImported(long totalRoutesImported) { this.totalRoutesImported = totalRoutesImported; return this; }
        public GtfsImportResponseBuilder totalStopsImported(long totalStopsImported) { this.totalStopsImported = totalStopsImported; return this; }
        public GtfsImportResponseBuilder totalTripsStaged(long totalTripsStaged) { this.totalTripsStaged = totalTripsStaged; return this; }
        public GtfsImportResponseBuilder totalStopTimesStaged(long totalStopTimesStaged) { this.totalStopTimesStaged = totalStopTimesStaged; return this; }
        public GtfsImportResponseBuilder quarantinedRecordsCount(long quarantinedRecordsCount) { this.quarantinedRecordsCount = quarantinedRecordsCount; return this; }
        public GtfsImportResponseBuilder status(String status) { this.status = status; return this; }
        public GtfsImportResponseBuilder message(String message) { this.message = message; return this; }

        public GtfsImportResponse build() {
            return new GtfsImportResponse(batchId, importedAt, totalRoutesImported, totalStopsImported, totalTripsStaged, totalStopTimesStaged, quarantinedRecordsCount, status, message);
        }
    }
}

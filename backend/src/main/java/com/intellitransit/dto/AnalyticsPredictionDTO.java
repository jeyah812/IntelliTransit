package com.intellitransit.dto;

public class AnalyticsPredictionDTO {

    private long totalAlerts;
    private long openAlerts;
    private long highSeverityAlerts;
    private long completedTrips;
    private double averageTripDuration;
    private long complaintCount;
    private long bookingCount;

    public AnalyticsPredictionDTO() {}

    public AnalyticsPredictionDTO(long totalAlerts, long openAlerts, long highSeverityAlerts, long completedTrips, double averageTripDuration, long complaintCount, long bookingCount) {
        this.totalAlerts = totalAlerts;
        this.openAlerts = openAlerts;
        this.highSeverityAlerts = highSeverityAlerts;
        this.completedTrips = completedTrips;
        this.averageTripDuration = averageTripDuration;
        this.complaintCount = complaintCount;
        this.bookingCount = bookingCount;
    }

    public static AnalyticsPredictionDTOBuilder builder() {
        return new AnalyticsPredictionDTOBuilder();
    }

    public long getTotalAlerts() { return totalAlerts; }
    public void setTotalAlerts(long totalAlerts) { this.totalAlerts = totalAlerts; }

    public long getOpenAlerts() { return openAlerts; }
    public void setOpenAlerts(long openAlerts) { this.openAlerts = openAlerts; }

    public long getHighSeverityAlerts() { return highSeverityAlerts; }
    public void setHighSeverityAlerts(long highSeverityAlerts) { this.highSeverityAlerts = highSeverityAlerts; }

    public long getCompletedTrips() { return completedTrips; }
    public void setCompletedTrips(long completedTrips) { this.completedTrips = completedTrips; }

    public double getAverageTripDuration() { return averageTripDuration; }
    public void setAverageTripDuration(double averageTripDuration) { this.averageTripDuration = averageTripDuration; }

    public long getComplaintCount() { return complaintCount; }
    public void setComplaintCount(long complaintCount) { this.complaintCount = complaintCount; }

    public long getBookingCount() { return bookingCount; }
    public void setBookingCount(long bookingCount) { this.bookingCount = bookingCount; }

    public static class AnalyticsPredictionDTOBuilder {
        private long totalAlerts;
        private long openAlerts;
        private long highSeverityAlerts;
        private long completedTrips;
        private double averageTripDuration;
        private long complaintCount;
        private long bookingCount;

        public AnalyticsPredictionDTOBuilder totalAlerts(long totalAlerts) { this.totalAlerts = totalAlerts; return this; }
        public AnalyticsPredictionDTOBuilder openAlerts(long openAlerts) { this.openAlerts = openAlerts; return this; }
        public AnalyticsPredictionDTOBuilder highSeverityAlerts(long highSeverityAlerts) { this.highSeverityAlerts = highSeverityAlerts; return this; }
        public AnalyticsPredictionDTOBuilder completedTrips(long completedTrips) { this.completedTrips = completedTrips; return this; }
        public AnalyticsPredictionDTOBuilder averageTripDuration(double averageTripDuration) { this.averageTripDuration = averageTripDuration; return this; }
        public AnalyticsPredictionDTOBuilder complaintCount(long complaintCount) { this.complaintCount = complaintCount; return this; }
        public AnalyticsPredictionDTOBuilder bookingCount(long bookingCount) { this.bookingCount = bookingCount; return this; }

        public AnalyticsPredictionDTO build() {
            return new AnalyticsPredictionDTO(totalAlerts, openAlerts, highSeverityAlerts, completedTrips, averageTripDuration, complaintCount, bookingCount);
        }
    }
}

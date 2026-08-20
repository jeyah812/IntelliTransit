package com.intellitransit.dto;

public class AnalyticsDTO {

    private long totalRoutes;
    private long totalStops;
    private long totalTrips;
    private long completedTrips;
    private long scheduledTrips;
    private long totalBookings;
    private long totalTickets;
    private long activeTickets;
    private long totalDrivers;
    private long totalBuses;

    public AnalyticsDTO() {}

    public AnalyticsDTO(long totalRoutes, long totalStops, long totalTrips, long completedTrips, long scheduledTrips, long totalBookings, long totalTickets, long activeTickets, long totalDrivers, long totalBuses) {
        this.totalRoutes = totalRoutes;
        this.totalStops = totalStops;
        this.totalTrips = totalTrips;
        this.completedTrips = completedTrips;
        this.scheduledTrips = scheduledTrips;
        this.totalBookings = totalBookings;
        this.totalTickets = totalTickets;
        this.activeTickets = activeTickets;
        this.totalDrivers = totalDrivers;
        this.totalBuses = totalBuses;
    }

    public static AnalyticsDTOBuilder builder() {
        return new AnalyticsDTOBuilder();
    }

    public long getTotalRoutes() { return totalRoutes; }
    public void setTotalRoutes(long totalRoutes) { this.totalRoutes = totalRoutes; }

    public long getTotalStops() { return totalStops; }
    public void setTotalStops(long totalStops) { this.totalStops = totalStops; }

    public long getTotalTrips() { return totalTrips; }
    public void setTotalTrips(long totalTrips) { this.totalTrips = totalTrips; }

    public long getCompletedTrips() { return completedTrips; }
    public void setCompletedTrips(long completedTrips) { this.completedTrips = completedTrips; }

    public long getScheduledTrips() { return scheduledTrips; }
    public void setScheduledTrips(long scheduledTrips) { this.scheduledTrips = scheduledTrips; }

    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

    public long getTotalTickets() { return totalTickets; }
    public void setTotalTickets(long totalTickets) { this.totalTickets = totalTickets; }

    public long getActiveTickets() { return activeTickets; }
    public void setActiveTickets(long activeTickets) { this.activeTickets = activeTickets; }

    public long getTotalDrivers() { return totalDrivers; }
    public void setTotalDrivers(long totalDrivers) { this.totalDrivers = totalDrivers; }

    public long getTotalBuses() { return totalBuses; }
    public void setTotalBuses(long totalBuses) { this.totalBuses = totalBuses; }

    public static class AnalyticsDTOBuilder {
        private long totalRoutes;
        private long totalStops;
        private long totalTrips;
        private long completedTrips;
        private long scheduledTrips;
        private long totalBookings;
        private long totalTickets;
        private long activeTickets;
        private long totalDrivers;
        private long totalBuses;

        public AnalyticsDTOBuilder totalRoutes(long totalRoutes) { this.totalRoutes = totalRoutes; return this; }
        public AnalyticsDTOBuilder totalStops(long totalStops) { this.totalStops = totalStops; return this; }
        public AnalyticsDTOBuilder totalTrips(long totalTrips) { this.totalTrips = totalTrips; return this; }
        public AnalyticsDTOBuilder completedTrips(long completedTrips) { this.completedTrips = completedTrips; return this; }
        public AnalyticsDTOBuilder scheduledTrips(long scheduledTrips) { this.scheduledTrips = scheduledTrips; return this; }
        public AnalyticsDTOBuilder totalBookings(long totalBookings) { this.totalBookings = totalBookings; return this; }
        public AnalyticsDTOBuilder totalTickets(long totalTickets) { this.totalTickets = totalTickets; return this; }
        public AnalyticsDTOBuilder activeTickets(long activeTickets) { this.activeTickets = activeTickets; return this; }
        public AnalyticsDTOBuilder totalDrivers(long totalDrivers) { this.totalDrivers = totalDrivers; return this; }
        public AnalyticsDTOBuilder totalBuses(long totalBuses) { this.totalBuses = totalBuses; return this; }

        public AnalyticsDTO build() {
            return new AnalyticsDTO(totalRoutes, totalStops, totalTrips, completedTrips, scheduledTrips, totalBookings, totalTickets, activeTickets, totalDrivers, totalBuses);
        }
    }
}

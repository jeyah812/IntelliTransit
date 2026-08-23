package com.intellitransit.dto;

public class DemoDataResponse {

    private int routesGenerated;
    private int stopsGenerated;
    private int tripsGenerated;
    private int bookingsGenerated;
    private int complaintsGenerated;
    private int alertsGenerated;
    private String status;
    private String message;

    public DemoDataResponse() {}

    public DemoDataResponse(int routesGenerated, int stopsGenerated, int tripsGenerated, int bookingsGenerated, int complaintsGenerated, int alertsGenerated, String status, String message) {
        this.routesGenerated = routesGenerated;
        this.stopsGenerated = stopsGenerated;
        this.tripsGenerated = tripsGenerated;
        this.bookingsGenerated = bookingsGenerated;
        this.complaintsGenerated = complaintsGenerated;
        this.alertsGenerated = alertsGenerated;
        this.status = status;
        this.message = message;
    }

    public static DemoDataResponseBuilder builder() {
        return new DemoDataResponseBuilder();
    }

    public int getRoutesGenerated() { return routesGenerated; }
    public void setRoutesGenerated(int routesGenerated) { this.routesGenerated = routesGenerated; }

    public int getStopsGenerated() { return stopsGenerated; }
    public void setStopsGenerated(int stopsGenerated) { this.stopsGenerated = stopsGenerated; }

    public int getTripsGenerated() { return tripsGenerated; }
    public void setTripsGenerated(int tripsGenerated) { this.tripsGenerated = tripsGenerated; }

    public int getBookingsGenerated() { return bookingsGenerated; }
    public void setBookingsGenerated(int bookingsGenerated) { this.bookingsGenerated = bookingsGenerated; }

    public int getComplaintsGenerated() { return complaintsGenerated; }
    public void setComplaintsGenerated(int complaintsGenerated) { this.complaintsGenerated = complaintsGenerated; }

    public int getAlertsGenerated() { return alertsGenerated; }
    public void setAlertsGenerated(int alertsGenerated) { this.alertsGenerated = alertsGenerated; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static class DemoDataResponseBuilder {
        private int routesGenerated;
        private int stopsGenerated;
        private int tripsGenerated;
        private int bookingsGenerated;
        private int complaintsGenerated;
        private int alertsGenerated;
        private String status;
        private String message;

        public DemoDataResponseBuilder routesGenerated(int routesGenerated) { this.routesGenerated = routesGenerated; return this; }
        public DemoDataResponseBuilder stopsGenerated(int stopsGenerated) { this.stopsGenerated = stopsGenerated; return this; }
        public DemoDataResponseBuilder tripsGenerated(int tripsGenerated) { this.tripsGenerated = tripsGenerated; return this; }
        public DemoDataResponseBuilder bookingsGenerated(int bookingsGenerated) { this.bookingsGenerated = bookingsGenerated; return this; }
        public DemoDataResponseBuilder complaintsGenerated(int complaintsGenerated) { this.complaintsGenerated = complaintsGenerated; return this; }
        public DemoDataResponseBuilder alertsGenerated(int alertsGenerated) { this.alertsGenerated = alertsGenerated; return this; }
        public DemoDataResponseBuilder status(String status) { this.status = status; return this; }
        public DemoDataResponseBuilder message(String message) { this.message = message; return this; }

        public DemoDataResponse build() {
            return new DemoDataResponse(routesGenerated, stopsGenerated, tripsGenerated, bookingsGenerated, complaintsGenerated, alertsGenerated, status, message);
        }
    }
}

package com.intellitransit.dto;

import com.intellitransit.entity.enums.TripStatus;
import java.time.LocalDateTime;

public class TripDTO {

    private Long id;
    private Long routeId;
    private String routeNumber;
    private String routeName;
    private Long busId;
    private String busNumber;
    private Long driverId;
    private String driverName;
    private LocalDateTime scheduledStart;
    private LocalDateTime scheduledEnd;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private TripStatus status;

    public TripDTO() {}

    public TripDTO(Long id, Long routeId, String routeNumber, String routeName, Long busId, String busNumber, Long driverId, String driverName, LocalDateTime scheduledStart, LocalDateTime scheduledEnd, LocalDateTime actualStart, LocalDateTime actualEnd, TripStatus status) {
        this.id = id;
        this.routeId = routeId;
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.busId = busId;
        this.busNumber = busNumber;
        this.driverId = driverId;
        this.driverName = driverName;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.actualStart = actualStart;
        this.actualEnd = actualEnd;
        this.status = status;
    }

    public static TripDTOBuilder builder() {
        return new TripDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public LocalDateTime getScheduledStart() { return scheduledStart; }
    public void setScheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; }

    public LocalDateTime getScheduledEnd() { return scheduledEnd; }
    public void setScheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; }

    public LocalDateTime getActualStart() { return actualStart; }
    public void setActualStart(LocalDateTime actualStart) { this.actualStart = actualStart; }

    public LocalDateTime getActualEnd() { return actualEnd; }
    public void setActualEnd(LocalDateTime actualEnd) { this.actualEnd = actualEnd; }

    public TripStatus getStatus() { return status; }
    public void setStatus(TripStatus status) { this.status = status; }

    public static class TripDTOBuilder {
        private Long id;
        private Long routeId;
        private String routeNumber;
        private String routeName;
        private Long busId;
        private String busNumber;
        private Long driverId;
        private String driverName;
        private LocalDateTime scheduledStart;
        private LocalDateTime scheduledEnd;
        private LocalDateTime actualStart;
        private LocalDateTime actualEnd;
        private TripStatus status;

        public TripDTOBuilder id(Long id) { this.id = id; return this; }
        public TripDTOBuilder routeId(Long routeId) { this.routeId = routeId; return this; }
        public TripDTOBuilder routeNumber(String routeNumber) { this.routeNumber = routeNumber; return this; }
        public TripDTOBuilder routeName(String routeName) { this.routeName = routeName; return this; }
        public TripDTOBuilder busId(Long busId) { this.busId = busId; return this; }
        public TripDTOBuilder busNumber(String busNumber) { this.busNumber = busNumber; return this; }
        public TripDTOBuilder driverId(Long driverId) { this.driverId = driverId; return this; }
        public TripDTOBuilder driverName(String driverName) { this.driverName = driverName; return this; }
        public TripDTOBuilder scheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; return this; }
        public TripDTOBuilder scheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; return this; }
        public TripDTOBuilder actualStart(LocalDateTime actualStart) { this.actualStart = actualStart; return this; }
        public TripDTOBuilder actualEnd(LocalDateTime actualEnd) { this.actualEnd = actualEnd; return this; }
        public TripDTOBuilder status(TripStatus status) { this.status = status; return this; }

        public TripDTO build() {
            return new TripDTO(id, routeId, routeNumber, routeName, busId, busNumber, driverId, driverName, scheduledStart, scheduledEnd, actualStart, actualEnd, status);
        }
    }
}

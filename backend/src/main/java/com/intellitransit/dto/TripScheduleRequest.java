package com.intellitransit.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TripScheduleRequest {

    @NotNull(message = "Route ID is required")
    private Long routeId;

    @NotNull(message = "Bus ID is required")
    private Long busId;

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotNull(message = "Scheduled start time is required")
    private LocalDateTime scheduledStart;

    @NotNull(message = "Scheduled end time is required")
    private LocalDateTime scheduledEnd;

    public TripScheduleRequest() {}

    public TripScheduleRequest(Long routeId, Long busId, Long driverId, LocalDateTime scheduledStart, LocalDateTime scheduledEnd) {
        this.routeId = routeId;
        this.busId = busId;
        this.driverId = driverId;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
    }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public LocalDateTime getScheduledStart() { return scheduledStart; }
    public void setScheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; }

    public LocalDateTime getScheduledEnd() { return scheduledEnd; }
    public void setScheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; }
}

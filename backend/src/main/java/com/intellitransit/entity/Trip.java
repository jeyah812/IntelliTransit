package com.intellitransit.entity;

import com.intellitransit.entity.enums.TripStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gtfs_trip_id", length = 100)
    private String gtfsTripId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "scheduled_start", nullable = false)
    private LocalDateTime scheduledStart;

    @Column(name = "scheduled_end", nullable = false)
    private LocalDateTime scheduledEnd;

    @Column(name = "actual_start")
    private LocalDateTime actualStart;

    @Column(name = "actual_end")
    private LocalDateTime actualEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TripStatus status = TripStatus.SCHEDULED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Trip() {}

    public Trip(Long id, String gtfsTripId, Route route, Bus bus, Driver driver, LocalDateTime scheduledStart, LocalDateTime scheduledEnd, LocalDateTime actualStart, LocalDateTime actualEnd, TripStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.gtfsTripId = gtfsTripId;
        this.route = route;
        this.bus = bus;
        this.driver = driver;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.actualStart = actualStart;
        this.actualEnd = actualEnd;
        this.status = status != null ? status : TripStatus.SCHEDULED;
        this.createdAt = createdAt;
    }

    public Trip(Long id, Route route, Bus bus, Driver driver, LocalDateTime scheduledStart, LocalDateTime scheduledEnd, LocalDateTime actualStart, LocalDateTime actualEnd, TripStatus status, LocalDateTime createdAt) {
        this(id, null, route, bus, driver, scheduledStart, scheduledEnd, actualStart, actualEnd, status, createdAt);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static TripBuilder builder() {
        return new TripBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGtfsTripId() { return gtfsTripId; }
    public void setGtfsTripId(String gtfsTripId) { this.gtfsTripId = gtfsTripId; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Bus getBus() { return bus; }
    public void setBus(Bus bus) { this.bus = bus; }

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class TripBuilder {
        private Long id;
        private String gtfsTripId;
        private Route route;
        private Bus bus;
        private Driver driver;
        private LocalDateTime scheduledStart;
        private LocalDateTime scheduledEnd;
        private LocalDateTime actualStart;
        private LocalDateTime actualEnd;
        private TripStatus status = TripStatus.SCHEDULED;
        private LocalDateTime createdAt;

        public TripBuilder id(Long id) { this.id = id; return this; }
        public TripBuilder gtfsTripId(String gtfsTripId) { this.gtfsTripId = gtfsTripId; return this; }
        public TripBuilder route(Route route) { this.route = route; return this; }
        public TripBuilder bus(Bus bus) { this.bus = bus; return this; }
        public TripBuilder driver(Driver driver) { this.driver = driver; return this; }
        public TripBuilder scheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; return this; }
        public TripBuilder scheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; return this; }
        public TripBuilder actualStart(LocalDateTime actualStart) { this.actualStart = actualStart; return this; }
        public TripBuilder actualEnd(LocalDateTime actualEnd) { this.actualEnd = actualEnd; return this; }
        public TripBuilder status(TripStatus status) { this.status = status; return this; }
        public TripBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Trip build() {
            return new Trip(id, gtfsTripId, route, bus, driver, scheduledStart, scheduledEnd, actualStart, actualEnd, status, createdAt);
        }
    }
}

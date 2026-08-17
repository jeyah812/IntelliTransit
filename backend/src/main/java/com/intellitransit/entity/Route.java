package com.intellitransit.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_number", nullable = false, unique = true, length = 30)
    private String routeNumber;

    @Column(name = "route_name", nullable = false, length = 100)
    private String routeName;

    @Column(nullable = false, length = 100)
    private String origin;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(name = "distance_km", nullable = false, precision = 6, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "estimated_duration_minutes", nullable = false)
    private Integer estimatedDurationMinutes;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Route() {}

    public Route(Long id, String routeNumber, String routeName, String origin, String destination, BigDecimal distanceKm, Integer estimatedDurationMinutes, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.active = active;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static RouteBuilder builder() {
        return new RouteBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public BigDecimal getDistanceKm() { return distanceKm; }
    public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }

    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class RouteBuilder {
        private Long id;
        private String routeNumber;
        private String routeName;
        private String origin;
        private String destination;
        private BigDecimal distanceKm;
        private Integer estimatedDurationMinutes;
        private boolean active = true;
        private LocalDateTime createdAt;

        public RouteBuilder id(Long id) { this.id = id; return this; }
        public RouteBuilder routeNumber(String routeNumber) { this.routeNumber = routeNumber; return this; }
        public RouteBuilder routeName(String routeName) { this.routeName = routeName; return this; }
        public RouteBuilder origin(String origin) { this.origin = origin; return this; }
        public RouteBuilder destination(String destination) { this.destination = destination; return this; }
        public RouteBuilder distanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; return this; }
        public RouteBuilder estimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; return this; }
        public RouteBuilder active(boolean active) { this.active = active; return this; }
        public RouteBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Route build() {
            return new Route(id, routeNumber, routeName, origin, destination, distanceKm, estimatedDurationMinutes, active, createdAt);
        }
    }
}

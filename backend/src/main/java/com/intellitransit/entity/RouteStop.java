package com.intellitransit.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "route_stops", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"route_id", "stop_sequence"})
})
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stop_id", nullable = false)
    private Stop stop;

    @Column(name = "stop_sequence", nullable = false)
    private Integer stopSequence;

    @Column(name = "distance_from_origin_km", nullable = false, precision = 6, scale = 2)
    private BigDecimal distanceFromOriginKm;

    public RouteStop() {}

    public RouteStop(Long id, Route route, Stop stop, Integer stopSequence, BigDecimal distanceFromOriginKm) {
        this.id = id;
        this.route = route;
        this.stop = stop;
        this.stopSequence = stopSequence;
        this.distanceFromOriginKm = distanceFromOriginKm;
    }

    public static RouteStopBuilder builder() {
        return new RouteStopBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Stop getStop() { return stop; }
    public void setStop(Stop stop) { this.stop = stop; }

    public Integer getStopSequence() { return stopSequence; }
    public void setStopSequence(Integer stopSequence) { this.stopSequence = stopSequence; }

    public BigDecimal getDistanceFromOriginKm() { return distanceFromOriginKm; }
    public void setDistanceFromOriginKm(BigDecimal distanceFromOriginKm) { this.distanceFromOriginKm = distanceFromOriginKm; }

    public static class RouteStopBuilder {
        private Long id;
        private Route route;
        private Stop stop;
        private Integer stopSequence;
        private BigDecimal distanceFromOriginKm;

        public RouteStopBuilder id(Long id) { this.id = id; return this; }
        public RouteStopBuilder route(Route route) { this.route = route; return this; }
        public RouteStopBuilder stop(Stop stop) { this.stop = stop; return this; }
        public RouteStopBuilder stopSequence(Integer stopSequence) { this.stopSequence = stopSequence; return this; }
        public RouteStopBuilder distanceFromOriginKm(BigDecimal distanceFromOriginKm) { this.distanceFromOriginKm = distanceFromOriginKm; return this; }

        public RouteStop build() {
            return new RouteStop(id, route, stop, stopSequence, distanceFromOriginKm);
        }
    }
}

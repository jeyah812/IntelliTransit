package com.intellitransit.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class RouteStopDTO {

    private Long id;

    private Long routeId;

    @NotNull(message = "Stop ID is required")
    private Long stopId;

    private String stopName;

    @NotNull(message = "Stop sequence is required")
    @Min(value = 1, message = "Stop sequence must be at least 1")
    private Integer stopSequence;

    @NotNull(message = "Distance from origin is required")
    @DecimalMin(value = "0.0", message = "Distance cannot be negative")
    private BigDecimal distanceFromOriginKm;

    public RouteStopDTO() {}

    public RouteStopDTO(Long id, Long routeId, Long stopId, String stopName, Integer stopSequence, BigDecimal distanceFromOriginKm) {
        this.id = id;
        this.routeId = routeId;
        this.stopId = stopId;
        this.stopName = stopName;
        this.stopSequence = stopSequence;
        this.distanceFromOriginKm = distanceFromOriginKm;
    }

    public static RouteStopDTOBuilder builder() {
        return new RouteStopDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public Long getStopId() { return stopId; }
    public void setStopId(Long stopId) { this.stopId = stopId; }

    public String getStopName() { return stopName; }
    public void setStopName(String stopName) { this.stopName = stopName; }

    public Integer getStopSequence() { return stopSequence; }
    public void setStopSequence(Integer stopSequence) { this.stopSequence = stopSequence; }

    public BigDecimal getDistanceFromOriginKm() { return distanceFromOriginKm; }
    public void setDistanceFromOriginKm(BigDecimal distanceFromOriginKm) { this.distanceFromOriginKm = distanceFromOriginKm; }

    public static class RouteStopDTOBuilder {
        private Long id;
        private Long routeId;
        private Long stopId;
        private String stopName;
        private Integer stopSequence;
        private BigDecimal distanceFromOriginKm;

        public RouteStopDTOBuilder id(Long id) { this.id = id; return this; }
        public RouteStopDTOBuilder routeId(Long routeId) { this.routeId = routeId; return this; }
        public RouteStopDTOBuilder stopId(Long stopId) { this.stopId = stopId; return this; }
        public RouteStopDTOBuilder stopName(String stopName) { this.stopName = stopName; return this; }
        public RouteStopDTOBuilder stopSequence(Integer stopSequence) { this.stopSequence = stopSequence; return this; }
        public RouteStopDTOBuilder distanceFromOriginKm(BigDecimal distanceFromOriginKm) { this.distanceFromOriginKm = distanceFromOriginKm; return this; }

        public RouteStopDTO build() {
            return new RouteStopDTO(id, routeId, stopId, stopName, stopSequence, distanceFromOriginKm);
        }
    }
}

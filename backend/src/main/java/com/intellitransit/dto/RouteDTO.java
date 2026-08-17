package com.intellitransit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class RouteDTO {

    private Long id;

    @NotBlank(message = "Route number is required")
    private String routeNumber;

    @NotBlank(message = "Route name is required")
    private String routeName;

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Distance in KM is required")
    private BigDecimal distanceKm;

    @NotNull(message = "Estimated duration is required")
    private Integer estimatedDurationMinutes;

    private boolean active = true;
    private List<RouteStopDTO> stops;

    public RouteDTO() {}

    public RouteDTO(Long id, String routeNumber, String routeName, String origin, String destination, BigDecimal distanceKm, Integer estimatedDurationMinutes, boolean active, List<RouteStopDTO> stops) {
        this.id = id;
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.active = active;
        this.stops = stops;
    }

    public static RouteDTOBuilder builder() {
        return new RouteDTOBuilder();
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

    public List<RouteStopDTO> getStops() { return stops; }
    public void setStops(List<RouteStopDTO> stops) { this.stops = stops; }

    public static class RouteDTOBuilder {
        private Long id;
        private String routeNumber;
        private String routeName;
        private String origin;
        private String destination;
        private BigDecimal distanceKm;
        private Integer estimatedDurationMinutes;
        private boolean active = true;
        private List<RouteStopDTO> stops;

        public RouteDTOBuilder id(Long id) { this.id = id; return this; }
        public RouteDTOBuilder routeNumber(String routeNumber) { this.routeNumber = routeNumber; return this; }
        public RouteDTOBuilder routeName(String routeName) { this.routeName = routeName; return this; }
        public RouteDTOBuilder origin(String origin) { this.origin = origin; return this; }
        public RouteDTOBuilder destination(String destination) { this.destination = destination; return this; }
        public RouteDTOBuilder distanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; return this; }
        public RouteDTOBuilder estimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; return this; }
        public RouteDTOBuilder active(boolean active) { this.active = active; return this; }
        public RouteDTOBuilder stops(List<RouteStopDTO> stops) { this.stops = stops; return this; }

        public RouteDTO build() {
            return new RouteDTO(id, routeNumber, routeName, origin, destination, distanceKm, estimatedDurationMinutes, active, stops);
        }
    }
}

package com.intellitransit.dto;

import jakarta.validation.constraints.NotNull;

public class FareCalculationRequest {

    @NotNull(message = "Route ID is required")
    private Long routeId;

    @NotNull(message = "Origin stop ID is required")
    private Long originStopId;

    @NotNull(message = "Destination stop ID is required")
    private Long destinationStopId;

    private String passengerCategory = "STANDARD"; // STANDARD, STUDENT, SENIOR

    public FareCalculationRequest() {}

    public FareCalculationRequest(Long routeId, Long originStopId, Long destinationStopId, String passengerCategory) {
        this.routeId = routeId;
        this.originStopId = originStopId;
        this.destinationStopId = destinationStopId;
        this.passengerCategory = passengerCategory != null ? passengerCategory : "STANDARD";
    }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public Long getOriginStopId() { return originStopId; }
    public void setOriginStopId(Long originStopId) { this.originStopId = originStopId; }

    public Long getDestinationStopId() { return destinationStopId; }
    public void setDestinationStopId(Long destinationStopId) { this.destinationStopId = destinationStopId; }

    public String getPassengerCategory() { return passengerCategory; }
    public void setPassengerCategory(String passengerCategory) { this.passengerCategory = passengerCategory; }
}

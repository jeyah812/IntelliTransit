package com.intellitransit.dto;

import jakarta.validation.constraints.NotNull;

public class BookingRequest {

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    @NotNull(message = "Origin stop ID is required")
    private Long originStopId;

    @NotNull(message = "Destination stop ID is required")
    private Long destinationStopId;

    private String passengerCategory = "STANDARD";

    public BookingRequest() {}

    public BookingRequest(Long tripId, Long originStopId, Long destinationStopId, String passengerCategory) {
        this.tripId = tripId;
        this.originStopId = originStopId;
        this.destinationStopId = destinationStopId;
        this.passengerCategory = passengerCategory != null ? passengerCategory : "STANDARD";
    }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public Long getOriginStopId() { return originStopId; }
    public void setOriginStopId(Long originStopId) { this.originStopId = originStopId; }

    public Long getDestinationStopId() { return destinationStopId; }
    public void setDestinationStopId(Long destinationStopId) { this.destinationStopId = destinationStopId; }

    public String getPassengerCategory() { return passengerCategory; }
    public void setPassengerCategory(String passengerCategory) { this.passengerCategory = passengerCategory; }
}

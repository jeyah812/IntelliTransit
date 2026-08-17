package com.intellitransit.dto;

import jakarta.validation.constraints.NotNull;

public class TicketVerificationRequest {

    @NotNull(message = "Ticket number or QR data is required")
    private String ticketNumberOrQrData;

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    public TicketVerificationRequest() {}

    public TicketVerificationRequest(String ticketNumberOrQrData, Long tripId) {
        this.ticketNumberOrQrData = ticketNumberOrQrData;
        this.tripId = tripId;
    }

    public String getTicketNumberOrQrData() { return ticketNumberOrQrData; }
    public void setTicketNumberOrQrData(String ticketNumberOrQrData) { this.ticketNumberOrQrData = ticketNumberOrQrData; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
}

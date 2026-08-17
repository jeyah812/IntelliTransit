package com.intellitransit.dto;

import java.time.LocalDateTime;

public class TicketVerificationResponse {

    private boolean valid;
    private String message;
    private String ticketNumber;
    private String bookingReference;
    private String passengerName;
    private LocalDateTime verifiedAt;

    public TicketVerificationResponse() {}

    public TicketVerificationResponse(boolean valid, String message, String ticketNumber, String bookingReference, String passengerName, LocalDateTime verifiedAt) {
        this.valid = valid;
        this.message = message;
        this.ticketNumber = ticketNumber;
        this.bookingReference = bookingReference;
        this.passengerName = passengerName;
        this.verifiedAt = verifiedAt;
    }

    public static TicketVerificationResponseBuilder builder() {
        return new TicketVerificationResponseBuilder();
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public static class TicketVerificationResponseBuilder {
        private boolean valid;
        private String message;
        private String ticketNumber;
        private String bookingReference;
        private String passengerName;
        private LocalDateTime verifiedAt;

        public TicketVerificationResponseBuilder valid(boolean valid) { this.valid = valid; return this; }
        public TicketVerificationResponseBuilder message(String message) { this.message = message; return this; }
        public TicketVerificationResponseBuilder ticketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; return this; }
        public TicketVerificationResponseBuilder bookingReference(String bookingReference) { this.bookingReference = bookingReference; return this; }
        public TicketVerificationResponseBuilder passengerName(String passengerName) { this.passengerName = passengerName; return this; }
        public TicketVerificationResponseBuilder verifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; return this; }

        public TicketVerificationResponse build() {
            return new TicketVerificationResponse(valid, message, ticketNumber, bookingReference, passengerName, verifiedAt);
        }
    }
}

package com.intellitransit.dto;

import com.intellitransit.entity.enums.BookingStatus;
import com.intellitransit.entity.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingResponse {

    private Long bookingId;
    private String bookingReference;
    private Long passengerId;
    private String passengerName;
    private Long tripId;
    private String routeNumber;
    private String routeName;
    private BigDecimal fareAmount;
    private PaymentStatus paymentStatus;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingTime;
    private TicketDTO ticket;

    public BookingResponse() {}

    public BookingResponse(Long bookingId, String bookingReference, Long passengerId, String passengerName, Long tripId, String routeNumber, String routeName, BigDecimal fareAmount, PaymentStatus paymentStatus, BookingStatus bookingStatus, LocalDateTime bookingTime, TicketDTO ticket) {
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.tripId = tripId;
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.fareAmount = fareAmount;
        this.paymentStatus = paymentStatus;
        this.bookingStatus = bookingStatus;
        this.bookingTime = bookingTime;
        this.ticket = ticket;
    }

    public static BookingResponseBuilder builder() {
        return new BookingResponseBuilder();
    }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public BigDecimal getFareAmount() { return fareAmount; }
    public void setFareAmount(BigDecimal fareAmount) { this.fareAmount = fareAmount; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public TicketDTO getTicket() { return ticket; }
    public void setTicket(TicketDTO ticket) { this.ticket = ticket; }

    public static class BookingResponseBuilder {
        private Long bookingId;
        private String bookingReference;
        private Long passengerId;
        private String passengerName;
        private Long tripId;
        private String routeNumber;
        private String routeName;
        private BigDecimal fareAmount;
        private PaymentStatus paymentStatus;
        private BookingStatus bookingStatus;
        private LocalDateTime bookingTime;
        private TicketDTO ticket;

        public BookingResponseBuilder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public BookingResponseBuilder bookingReference(String bookingReference) { this.bookingReference = bookingReference; return this; }
        public BookingResponseBuilder passengerId(Long passengerId) { this.passengerId = passengerId; return this; }
        public BookingResponseBuilder passengerName(String passengerName) { this.passengerName = passengerName; return this; }
        public BookingResponseBuilder tripId(Long tripId) { this.tripId = tripId; return this; }
        public BookingResponseBuilder routeNumber(String routeNumber) { this.routeNumber = routeNumber; return this; }
        public BookingResponseBuilder routeName(String routeName) { this.routeName = routeName; return this; }
        public BookingResponseBuilder fareAmount(BigDecimal fareAmount) { this.fareAmount = fareAmount; return this; }
        public BookingResponseBuilder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public BookingResponseBuilder bookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; return this; }
        public BookingResponseBuilder bookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; return this; }
        public BookingResponseBuilder ticket(TicketDTO ticket) { this.ticket = ticket; return this; }

        public BookingResponse build() {
            return new BookingResponse(bookingId, bookingReference, passengerId, passengerName, tripId, routeNumber, routeName, fareAmount, paymentStatus, bookingStatus, bookingTime, ticket);
        }
    }
}

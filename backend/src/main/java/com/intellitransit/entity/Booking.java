package com.intellitransit.entity;

import com.intellitransit.entity.enums.BookingStatus;
import com.intellitransit.entity.enums.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "booking_reference", nullable = false, unique = true, length = 30)
    private String bookingReference;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;

    @Column(name = "fare_amount", nullable = false, precision = 8, scale = 2)
    private BigDecimal fareAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.CONFIRMED;

    public Booking() {}

    public Booking(Long id, Passenger passenger, Trip trip, String bookingReference, LocalDateTime bookingTime, BigDecimal fareAmount, PaymentStatus paymentStatus, BookingStatus status) {
        this.id = id;
        this.passenger = passenger;
        this.trip = trip;
        this.bookingReference = bookingReference;
        this.bookingTime = bookingTime;
        this.fareAmount = fareAmount;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;
        this.status = status != null ? status : BookingStatus.CONFIRMED;
    }

    @PrePersist
    protected void onCreate() {
        if (this.bookingTime == null) {
            this.bookingTime = LocalDateTime.now();
        }
    }

    public static BookingBuilder builder() {
        return new BookingBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public BigDecimal getFareAmount() { return fareAmount; }
    public void setFareAmount(BigDecimal fareAmount) { this.fareAmount = fareAmount; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public static class BookingBuilder {
        private Long id;
        private Passenger passenger;
        private Trip trip;
        private String bookingReference;
        private LocalDateTime bookingTime;
        private BigDecimal fareAmount;
        private PaymentStatus paymentStatus = PaymentStatus.PENDING;
        private BookingStatus status = BookingStatus.CONFIRMED;

        public BookingBuilder id(Long id) { this.id = id; return this; }
        public BookingBuilder passenger(Passenger passenger) { this.passenger = passenger; return this; }
        public BookingBuilder trip(Trip trip) { this.trip = trip; return this; }
        public BookingBuilder bookingReference(String bookingReference) { this.bookingReference = bookingReference; return this; }
        public BookingBuilder bookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; return this; }
        public BookingBuilder fareAmount(BigDecimal fareAmount) { this.fareAmount = fareAmount; return this; }
        public BookingBuilder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public BookingBuilder status(BookingStatus status) { this.status = status; return this; }

        public Booking build() {
            return new Booking(id, passenger, trip, bookingReference, bookingTime, fareAmount, paymentStatus, status);
        }
    }
}

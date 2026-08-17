package com.intellitransit.entity;

import com.intellitransit.entity.enums.TicketStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(name = "ticket_number", nullable = false, unique = true, length = 40)
    private String ticketNumber;

    @Column(name = "qr_data", nullable = false, columnDefinition = "TEXT")
    private String qrData;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status = TicketStatus.ACTIVE;

    public Ticket() {}

    public Ticket(Long id, Booking booking, String ticketNumber, String qrData, LocalDateTime issuedAt, LocalDateTime verifiedAt, TicketStatus status) {
        this.id = id;
        this.booking = booking;
        this.ticketNumber = ticketNumber;
        this.qrData = qrData;
        this.issuedAt = issuedAt;
        this.verifiedAt = verifiedAt;
        this.status = status != null ? status : TicketStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        if (this.issuedAt == null) {
            this.issuedAt = LocalDateTime.now();
        }
    }

    public static TicketBuilder builder() {
        return new TicketBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getQrData() { return qrData; }
    public void setQrData(String qrData) { this.qrData = qrData; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public static class TicketBuilder {
        private Long id;
        private Booking booking;
        private String ticketNumber;
        private String qrData;
        private LocalDateTime issuedAt;
        private LocalDateTime verifiedAt;
        private TicketStatus status = TicketStatus.ACTIVE;

        public TicketBuilder id(Long id) { this.id = id; return this; }
        public TicketBuilder booking(Booking booking) { this.booking = booking; return this; }
        public TicketBuilder ticketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; return this; }
        public TicketBuilder qrData(String qrData) { this.qrData = qrData; return this; }
        public TicketBuilder issuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; return this; }
        public TicketBuilder verifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; return this; }
        public TicketBuilder status(TicketStatus status) { this.status = status; return this; }

        public Ticket build() {
            return new Ticket(id, booking, ticketNumber, qrData, issuedAt, verifiedAt, status);
        }
    }
}

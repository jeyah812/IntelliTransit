package com.intellitransit.dto;

import com.intellitransit.entity.enums.TicketStatus;
import java.time.LocalDateTime;

public class TicketDTO {

    private Long id;
    private Long bookingId;
    private String ticketNumber;
    private String qrData;
    private LocalDateTime issuedAt;
    private LocalDateTime verifiedAt;
    private TicketStatus status;

    public TicketDTO() {}

    public TicketDTO(Long id, Long bookingId, String ticketNumber, String qrData, LocalDateTime issuedAt, LocalDateTime verifiedAt, TicketStatus status) {
        this.id = id;
        this.bookingId = bookingId;
        this.ticketNumber = ticketNumber;
        this.qrData = qrData;
        this.issuedAt = issuedAt;
        this.verifiedAt = verifiedAt;
        this.status = status;
    }

    public static TicketDTOBuilder builder() {
        return new TicketDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

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

    public static class TicketDTOBuilder {
        private Long id;
        private Long bookingId;
        private String ticketNumber;
        private String qrData;
        private LocalDateTime issuedAt;
        private LocalDateTime verifiedAt;
        private TicketStatus status;

        public TicketDTOBuilder id(Long id) { this.id = id; return this; }
        public TicketDTOBuilder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public TicketDTOBuilder ticketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; return this; }
        public TicketDTOBuilder qrData(String qrData) { this.qrData = qrData; return this; }
        public TicketDTOBuilder issuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; return this; }
        public TicketDTOBuilder verifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; return this; }
        public TicketDTOBuilder status(TicketStatus status) { this.status = status; return this; }

        public TicketDTO build() {
            return new TicketDTO(id, bookingId, ticketNumber, qrData, issuedAt, verifiedAt, status);
        }
    }
}

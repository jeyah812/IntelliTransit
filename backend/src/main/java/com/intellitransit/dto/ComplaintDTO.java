package com.intellitransit.dto;

import com.intellitransit.entity.enums.ComplaintCategory;
import com.intellitransit.entity.enums.ComplaintStatus;
import java.time.LocalDateTime;

public class ComplaintDTO {

    private Long id;
    private Long passengerId;
    private String passengerName;
    private Long tripId;
    private String subject;
    private String description;
    private ComplaintCategory category;
    private ComplaintStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public ComplaintDTO() {}

    public ComplaintDTO(Long id, Long passengerId, String passengerName, Long tripId, String subject, String description, ComplaintCategory category, ComplaintStatus status, LocalDateTime createdAt, LocalDateTime resolvedAt) {
        this.id = id;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.tripId = tripId;
        this.subject = subject;
        this.description = description;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    public static ComplaintDTOBuilder builder() {
        return new ComplaintDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ComplaintCategory getCategory() { return category; }
    public void setCategory(ComplaintCategory category) { this.category = category; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public static class ComplaintDTOBuilder {
        private Long id;
        private Long passengerId;
        private String passengerName;
        private Long tripId;
        private String subject;
        private String description;
        private ComplaintCategory category;
        private ComplaintStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime resolvedAt;

        public ComplaintDTOBuilder id(Long id) { this.id = id; return this; }
        public ComplaintDTOBuilder passengerId(Long passengerId) { this.passengerId = passengerId; return this; }
        public ComplaintDTOBuilder passengerName(String passengerName) { this.passengerName = passengerName; return this; }
        public ComplaintDTOBuilder tripId(Long tripId) { this.tripId = tripId; return this; }
        public ComplaintDTOBuilder subject(String subject) { this.subject = subject; return this; }
        public ComplaintDTOBuilder description(String description) { this.description = description; return this; }
        public ComplaintDTOBuilder category(ComplaintCategory category) { this.category = category; return this; }
        public ComplaintDTOBuilder status(ComplaintStatus status) { this.status = status; return this; }
        public ComplaintDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ComplaintDTOBuilder resolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }

        public ComplaintDTO build() {
            return new ComplaintDTO(id, passengerId, passengerName, tripId, subject, description, category, status, createdAt, resolvedAt);
        }
    }
}

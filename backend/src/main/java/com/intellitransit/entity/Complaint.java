package com.intellitransit.entity;

import com.intellitransit.entity.enums.ComplaintCategory;
import com.intellitransit.entity.enums.ComplaintStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(nullable = false, length = 150)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ComplaintStatus status = ComplaintStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public Complaint() {}

    public Complaint(Long id, Passenger passenger, Trip trip, String subject, String description, ComplaintCategory category, ComplaintStatus status, LocalDateTime createdAt, LocalDateTime resolvedAt) {
        this.id = id;
        this.passenger = passenger;
        this.trip = trip;
        this.subject = subject;
        this.description = description;
        this.category = category;
        this.status = status != null ? status : ComplaintStatus.OPEN;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static ComplaintBuilder builder() {
        return new ComplaintBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

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

    public static class ComplaintBuilder {
        private Long id;
        private Passenger passenger;
        private Trip trip;
        private String subject;
        private String description;
        private ComplaintCategory category;
        private ComplaintStatus status = ComplaintStatus.OPEN;
        private LocalDateTime createdAt;
        private LocalDateTime resolvedAt;

        public ComplaintBuilder id(Long id) { this.id = id; return this; }
        public ComplaintBuilder passenger(Passenger passenger) { this.passenger = passenger; return this; }
        public ComplaintBuilder trip(Trip trip) { this.trip = trip; return this; }
        public ComplaintBuilder subject(String subject) { this.subject = subject; return this; }
        public ComplaintBuilder description(String description) { this.description = description; return this; }
        public ComplaintBuilder category(ComplaintCategory category) { this.category = category; return this; }
        public ComplaintBuilder status(ComplaintStatus status) { this.status = status; return this; }
        public ComplaintBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ComplaintBuilder resolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }

        public Complaint build() {
            return new Complaint(id, passenger, trip, subject, description, category, status, createdAt, resolvedAt);
        }
    }
}

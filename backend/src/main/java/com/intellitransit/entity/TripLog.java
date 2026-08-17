package com.intellitransit.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip_logs")
public class TripLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "actual_start")
    private LocalDateTime actualStart;

    @Column(name = "actual_end")
    private LocalDateTime actualEnd;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "recorded_at", nullable = false, updatable = false)
    private LocalDateTime recordedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public TripLog() {}

    public TripLog(Long id, Trip trip, LocalDateTime actualStart, LocalDateTime actualEnd, Integer durationMinutes, LocalDateTime recordedAt, String notes) {
        this.id = id;
        this.trip = trip;
        this.actualStart = actualStart;
        this.actualEnd = actualEnd;
        this.durationMinutes = durationMinutes;
        this.recordedAt = recordedAt;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        this.recordedAt = LocalDateTime.now();
    }

    public static TripLogBuilder builder() {
        return new TripLogBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public LocalDateTime getActualStart() { return actualStart; }
    public void setActualStart(LocalDateTime actualStart) { this.actualStart = actualStart; }

    public LocalDateTime getActualEnd() { return actualEnd; }
    public void setActualEnd(LocalDateTime actualEnd) { this.actualEnd = actualEnd; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static class TripLogBuilder {
        private Long id;
        private Trip trip;
        private LocalDateTime actualStart;
        private LocalDateTime actualEnd;
        private Integer durationMinutes;
        private LocalDateTime recordedAt;
        private String notes;

        public TripLogBuilder id(Long id) { this.id = id; return this; }
        public TripLogBuilder trip(Trip trip) { this.trip = trip; return this; }
        public TripLogBuilder actualStart(LocalDateTime actualStart) { this.actualStart = actualStart; return this; }
        public TripLogBuilder actualEnd(LocalDateTime actualEnd) { this.actualEnd = actualEnd; return this; }
        public TripLogBuilder durationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; return this; }
        public TripLogBuilder recordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; return this; }
        public TripLogBuilder notes(String notes) { this.notes = notes; return this; }

        public TripLog build() {
            return new TripLog(id, trip, actualStart, actualEnd, durationMinutes, recordedAt, notes);
        }
    }
}

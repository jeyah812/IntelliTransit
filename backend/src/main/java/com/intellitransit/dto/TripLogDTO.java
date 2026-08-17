package com.intellitransit.dto;

import java.time.LocalDateTime;

public class TripLogDTO {

    private Long id;
    private Long tripId;
    private String routeNumber;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private Integer durationMinutes;
    private LocalDateTime recordedAt;
    private String notes;

    public TripLogDTO() {}

    public TripLogDTO(Long id, Long tripId, String routeNumber, LocalDateTime actualStart, LocalDateTime actualEnd, Integer durationMinutes, LocalDateTime recordedAt, String notes) {
        this.id = id;
        this.tripId = tripId;
        this.routeNumber = routeNumber;
        this.actualStart = actualStart;
        this.actualEnd = actualEnd;
        this.durationMinutes = durationMinutes;
        this.recordedAt = recordedAt;
        this.notes = notes;
    }

    public static TripLogDTOBuilder builder() {
        return new TripLogDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }

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

    public static class TripLogDTOBuilder {
        private Long id;
        private Long tripId;
        private String routeNumber;
        private LocalDateTime actualStart;
        private LocalDateTime actualEnd;
        private Integer durationMinutes;
        private LocalDateTime recordedAt;
        private String notes;

        public TripLogDTOBuilder id(Long id) { this.id = id; return this; }
        public TripLogDTOBuilder tripId(Long tripId) { this.tripId = tripId; return this; }
        public TripLogDTOBuilder routeNumber(String routeNumber) { this.routeNumber = routeNumber; return this; }
        public TripLogDTOBuilder actualStart(LocalDateTime actualStart) { this.actualStart = actualStart; return this; }
        public TripLogDTOBuilder actualEnd(LocalDateTime actualEnd) { this.actualEnd = actualEnd; return this; }
        public TripLogDTOBuilder durationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; return this; }
        public TripLogDTOBuilder recordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; return this; }
        public TripLogDTOBuilder notes(String notes) { this.notes = notes; return this; }

        public TripLogDTO build() {
            return new TripLogDTO(id, tripId, routeNumber, actualStart, actualEnd, durationMinutes, recordedAt, notes);
        }
    }
}

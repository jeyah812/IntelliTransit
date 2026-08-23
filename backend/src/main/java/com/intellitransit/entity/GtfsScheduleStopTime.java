package com.intellitransit.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "gtfs_schedule_stop_times", indexes = {
    @Index(name = "idx_gtfs_stop_time_trip", columnList = "gtfs_trip_id, batch_id")
})
public class GtfsScheduleStopTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gtfs_trip_id", nullable = false, length = 100)
    private String gtfsTripId;

    @Column(name = "gtfs_stop_id", nullable = false, length = 100)
    private String gtfsStopId;

    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    @Column(name = "departure_time")
    private LocalTime departureTime;

    @Column(name = "day_offset", nullable = false)
    private int dayOffset = 0;

    @Column(name = "stop_sequence", nullable = false)
    private int stopSequence;

    @Column(name = "batch_id", nullable = false, length = 64)
    private String batchId;

    public GtfsScheduleStopTime() {}

    public GtfsScheduleStopTime(Long id, String gtfsTripId, String gtfsStopId, LocalTime arrivalTime, LocalTime departureTime, int dayOffset, int stopSequence, String batchId) {
        this.id = id;
        this.gtfsTripId = gtfsTripId;
        this.gtfsStopId = gtfsStopId;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.dayOffset = dayOffset;
        this.stopSequence = stopSequence;
        this.batchId = batchId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGtfsTripId() { return gtfsTripId; }
    public void setGtfsTripId(String gtfsTripId) { this.gtfsTripId = gtfsTripId; }

    public String getGtfsStopId() { return gtfsStopId; }
    public void setGtfsStopId(String gtfsStopId) { this.gtfsStopId = gtfsStopId; }

    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }

    public int getDayOffset() { return dayOffset; }
    public void setDayOffset(int dayOffset) { this.dayOffset = dayOffset; }

    public int getStopSequence() { return stopSequence; }
    public void setStopSequence(int stopSequence) { this.stopSequence = stopSequence; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
}

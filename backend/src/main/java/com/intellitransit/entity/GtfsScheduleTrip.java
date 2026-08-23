package com.intellitransit.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "gtfs_schedule_trips", indexes = {
    @Index(name = "idx_gtfs_trip_id_batch", columnList = "gtfs_trip_id, batch_id")
})
public class GtfsScheduleTrip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gtfs_trip_id", nullable = false, length = 100)
    private String gtfsTripId;

    @Column(name = "gtfs_route_id", nullable = false, length = 100)
    private String gtfsRouteId;

    @Column(name = "gtfs_service_id", nullable = false, length = 100)
    private String gtfsServiceId;

    @Column(name = "direction_id")
    private Integer directionId;

    @Column(name = "batch_id", nullable = false, length = 64)
    private String batchId;

    public GtfsScheduleTrip() {}

    public GtfsScheduleTrip(Long id, String gtfsTripId, String gtfsRouteId, String gtfsServiceId, Integer directionId, String batchId) {
        this.id = id;
        this.gtfsTripId = gtfsTripId;
        this.gtfsRouteId = gtfsRouteId;
        this.gtfsServiceId = gtfsServiceId;
        this.directionId = directionId;
        this.batchId = batchId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGtfsTripId() { return gtfsTripId; }
    public void setGtfsTripId(String gtfsTripId) { this.gtfsTripId = gtfsTripId; }

    public String getGtfsRouteId() { return gtfsRouteId; }
    public void setGtfsRouteId(String gtfsRouteId) { this.gtfsRouteId = gtfsRouteId; }

    public String getGtfsServiceId() { return gtfsServiceId; }
    public void setGtfsServiceId(String gtfsServiceId) { this.gtfsServiceId = gtfsServiceId; }

    public Integer getDirectionId() { return directionId; }
    public void setDirectionId(Integer directionId) { this.directionId = directionId; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
}

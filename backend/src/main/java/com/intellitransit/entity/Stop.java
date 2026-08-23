package com.intellitransit.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stops")
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gtfs_stop_id", length = 100)
    private String gtfsStopId;

    @Column(name = "feed_id", length = 50)
    private String feedId;

    @Column(name = "feed_version", length = 50)
    private String feedVersion;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Stop() {}

    public Stop(Long id, String gtfsStopId, String feedId, String feedVersion, String name, BigDecimal latitude, BigDecimal longitude, LocalDateTime createdAt) {
        this.id = id;
        this.gtfsStopId = gtfsStopId;
        this.feedId = feedId;
        this.feedVersion = feedVersion;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    public Stop(Long id, String gtfsStopId, String feedVersion, String name, BigDecimal latitude, BigDecimal longitude, LocalDateTime createdAt) {
        this(id, gtfsStopId, "MTC", feedVersion, name, latitude, longitude, createdAt);
    }

    public Stop(Long id, String name, BigDecimal latitude, BigDecimal longitude, LocalDateTime createdAt) {
        this(id, null, "MTC", "2.0", name, latitude, longitude, createdAt);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static StopBuilder builder() {
        return new StopBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGtfsStopId() { return gtfsStopId; }
    public void setGtfsStopId(String gtfsStopId) { this.gtfsStopId = gtfsStopId; }

    public String getFeedId() { return feedId; }
    public void setFeedId(String feedId) { this.feedId = feedId; }

    public String getFeedVersion() { return feedVersion; }
    public void setFeedVersion(String feedVersion) { this.feedVersion = feedVersion; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class StopBuilder {
        private Long id;
        private String gtfsStopId;
        private String feedId = "MTC";
        private String feedVersion = "2.0";
        private String name;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private LocalDateTime createdAt;

        public StopBuilder id(Long id) { this.id = id; return this; }
        public StopBuilder gtfsStopId(String gtfsStopId) { this.gtfsStopId = gtfsStopId; return this; }
        public StopBuilder feedId(String feedId) { this.feedId = feedId; return this; }
        public StopBuilder feedVersion(String feedVersion) { this.feedVersion = feedVersion; return this; }
        public StopBuilder name(String name) { this.name = name; return this; }
        public StopBuilder latitude(BigDecimal latitude) { this.latitude = latitude; return this; }
        public StopBuilder longitude(BigDecimal longitude) { this.longitude = longitude; return this; }
        public StopBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Stop build() {
            return new Stop(id, gtfsStopId, feedId, feedVersion, name, latitude, longitude, createdAt);
        }
    }
}

package com.intellitransit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gtfs_feed_metadata")
public class GtfsFeedMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "feed_publisher_name", length = 100)
    private String feedPublisherName;

    @Column(name = "feed_version", length = 50)
    private String feedVersion;

    @Column(name = "batch_id", nullable = false, unique = true, length = 64)
    private String batchId;

    @Column(name = "imported_at", nullable = false)
    private LocalDateTime importedAt;

    @Column(name = "total_routes_imported")
    private long totalRoutesImported;

    @Column(name = "total_stops_imported")
    private long totalStopsImported;

    @Column(name = "quarantined_records_count")
    private long quarantinedRecordsCount;

    @Column(nullable = false, length = 20)
    private String status;

    public GtfsFeedMetadata() {}

    public GtfsFeedMetadata(Long id, String feedPublisherName, String feedVersion, String batchId, LocalDateTime importedAt, long totalRoutesImported, long totalStopsImported, long quarantinedRecordsCount, String status) {
        this.id = id;
        this.feedPublisherName = feedPublisherName;
        this.feedVersion = feedVersion;
        this.batchId = batchId;
        this.importedAt = importedAt;
        this.totalRoutesImported = totalRoutesImported;
        this.totalStopsImported = totalStopsImported;
        this.quarantinedRecordsCount = quarantinedRecordsCount;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFeedPublisherName() { return feedPublisherName; }
    public void setFeedPublisherName(String feedPublisherName) { this.feedPublisherName = feedPublisherName; }

    public String getFeedVersion() { return feedVersion; }
    public void setFeedVersion(String feedVersion) { this.feedVersion = feedVersion; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public LocalDateTime getImportedAt() { return importedAt; }
    public void setImportedAt(LocalDateTime importedAt) { this.importedAt = importedAt; }

    public long getTotalRoutesImported() { return totalRoutesImported; }
    public void setTotalRoutesImported(long totalRoutesImported) { this.totalRoutesImported = totalRoutesImported; }

    public long getTotalStopsImported() { return totalStopsImported; }
    public void setTotalStopsImported(long totalStopsImported) { this.totalStopsImported = totalStopsImported; }

    public long getQuarantinedRecordsCount() { return quarantinedRecordsCount; }
    public void setQuarantinedRecordsCount(long quarantinedRecordsCount) { this.quarantinedRecordsCount = quarantinedRecordsCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

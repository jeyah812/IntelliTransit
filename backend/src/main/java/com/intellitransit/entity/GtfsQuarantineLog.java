package com.intellitransit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gtfs_quarantine_logs")
public class GtfsQuarantineLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_id", nullable = false, length = 64)
    private String batchId;

    @Column(name = "file_name", nullable = false, length = 50)
    private String fileName;

    @Column(name = "raw_record", columnDefinition = "TEXT")
    private String rawRecord;

    @Column(name = "rejection_reason", nullable = false, length = 255)
    private String rejectionReason;

    @Column(name = "quarantined_at", nullable = false)
    private LocalDateTime quarantinedAt;

    public GtfsQuarantineLog() {}

    public GtfsQuarantineLog(Long id, String batchId, String fileName, String rawRecord, String rejectionReason, LocalDateTime quarantinedAt) {
        this.id = id;
        this.batchId = batchId;
        this.fileName = fileName;
        this.rawRecord = rawRecord;
        this.rejectionReason = rejectionReason;
        this.quarantinedAt = quarantinedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getRawRecord() { return rawRecord; }
    public void setRawRecord(String rawRecord) { this.rawRecord = rawRecord; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getQuarantinedAt() { return quarantinedAt; }
    public void setQuarantinedAt(LocalDateTime quarantinedAt) { this.quarantinedAt = quarantinedAt; }
}

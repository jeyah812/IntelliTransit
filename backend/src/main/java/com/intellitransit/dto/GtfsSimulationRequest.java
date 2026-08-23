package com.intellitransit.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class GtfsSimulationRequest {

    private String batchId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private Integer sampleSize = 100;

    private Long seed = 42L;

    public GtfsSimulationRequest() {}

    public GtfsSimulationRequest(String batchId, LocalDate startDate, LocalDate endDate, Integer sampleSize, Long seed) {
        this.batchId = batchId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sampleSize = sampleSize != null ? sampleSize : 100;
        this.seed = seed != null ? seed : 42L;
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getSampleSize() { return sampleSize; }
    public void setSampleSize(Integer sampleSize) { this.sampleSize = sampleSize; }

    public Long getSeed() { return seed; }
    public void setSeed(Long seed) { this.seed = seed; }
}

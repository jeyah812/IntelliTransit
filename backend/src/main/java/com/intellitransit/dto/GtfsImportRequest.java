package com.intellitransit.dto;

import jakarta.validation.constraints.NotBlank;

public class GtfsImportRequest {

    @NotBlank(message = "GTFS directory path is required")
    private String directoryPath;

    private boolean dryRun = false;

    private String feedId = "MTC";

    private String feedVersion = "2.0";

    public GtfsImportRequest() {}

    public GtfsImportRequest(String directoryPath, boolean dryRun) {
        this.directoryPath = directoryPath;
        this.dryRun = dryRun;
        this.feedId = "MTC";
        this.feedVersion = "2.0";
    }

    public GtfsImportRequest(String directoryPath, boolean dryRun, String feedVersion) {
        this(directoryPath, dryRun, "MTC", feedVersion);
    }

    public GtfsImportRequest(String directoryPath, boolean dryRun, String feedId, String feedVersion) {
        this.directoryPath = directoryPath;
        this.dryRun = dryRun;
        this.feedId = feedId != null ? feedId : "MTC";
        this.feedVersion = feedVersion != null ? feedVersion : "2.0";
    }

    public String getDirectoryPath() { return directoryPath; }
    public void setDirectoryPath(String directoryPath) { this.directoryPath = directoryPath; }

    public boolean isDryRun() { return dryRun; }
    public void setDryRun(boolean dryRun) { this.dryRun = dryRun; }

    public String getFeedId() { return feedId; }
    public void setFeedId(String feedId) { this.feedId = feedId; }

    public String getFeedVersion() { return feedVersion; }
    public void setFeedVersion(String feedVersion) { this.feedVersion = feedVersion; }
}

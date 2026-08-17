package com.intellitransit.dto;

import jakarta.validation.constraints.NotBlank;

public class IncidentReportRequest {

    @NotBlank(message = "Notes / description of incident is required")
    private String notes;

    public IncidentReportRequest() {}

    public IncidentReportRequest(String notes) {
        this.notes = notes;
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

package com.intellitransit.service;

import com.intellitransit.dto.GtfsImportRequest;
import com.intellitransit.dto.GtfsImportResponse;

public interface GtfsIngestionService {
    GtfsImportResponse importGtfsFeed(GtfsImportRequest request);
}

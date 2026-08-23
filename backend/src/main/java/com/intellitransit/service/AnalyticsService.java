package com.intellitransit.service;

import com.intellitransit.dto.AnalyticsDTO;
import com.intellitransit.dto.AnalyticsPredictionDTO;

public interface AnalyticsService {
    AnalyticsDTO getDashboardMetrics();
    AnalyticsPredictionDTO getSmartDashboardMetrics();
}

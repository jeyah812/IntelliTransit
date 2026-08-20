package com.intellitransit.controller;

import com.intellitransit.dto.AnalyticsDTO;
import com.intellitransit.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsDTO> getDashboardMetrics() {
        AnalyticsDTO metrics = analyticsService.getDashboardMetrics();
        return ResponseEntity.ok(metrics);
    }
}

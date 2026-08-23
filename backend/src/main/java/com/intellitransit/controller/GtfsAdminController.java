package com.intellitransit.controller;

import com.intellitransit.dto.GtfsImportRequest;
import com.intellitransit.dto.GtfsImportResponse;
import com.intellitransit.dto.GtfsSimulationRequest;
import com.intellitransit.dto.GtfsSimulationResponse;
import com.intellitransit.service.GtfsIngestionService;
import com.intellitransit.service.GtfsOperationalSimulationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/gtfs")
public class GtfsAdminController {

    private final GtfsIngestionService gtfsIngestionService;
    private final GtfsOperationalSimulationService simulationService;

    public GtfsAdminController(GtfsIngestionService gtfsIngestionService,
                                GtfsOperationalSimulationService simulationService) {
        this.gtfsIngestionService = gtfsIngestionService;
        this.simulationService = simulationService;
    }

    @PostMapping("/import")
    public ResponseEntity<GtfsImportResponse> importGtfsFeed(@Valid @RequestBody GtfsImportRequest request) {
        GtfsImportResponse response = gtfsIngestionService.importGtfsFeed(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/simulate")
    public ResponseEntity<GtfsSimulationResponse> simulateOperationalTrips(@Valid @RequestBody GtfsSimulationRequest request) {
        GtfsSimulationResponse response = simulationService.simulateOperationalTrips(request);
        return ResponseEntity.ok(response);
    }
}

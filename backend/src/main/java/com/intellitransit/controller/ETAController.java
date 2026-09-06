package com.intellitransit.controller;

import com.intellitransit.dto.EstimatedArrivalDTO;
import com.intellitransit.service.ETAService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operations/eta")
public class ETAController {

    private final ETAService etaService;

    public ETAController(ETAService etaService) {
        this.etaService = etaService;
    }

    @GetMapping("/trip/{tripId}/stop/{stopId}")
    public ResponseEntity<?> getETAForTripAndStop(@PathVariable Long tripId, @PathVariable Long stopId) {
        try {
            EstimatedArrivalDTO eta = etaService.calculateETA(tripId, stopId);
            return ResponseEntity.ok(eta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/route/{routeId}/stop/{stopId}")
    public ResponseEntity<List<EstimatedArrivalDTO>> getETAsForRouteAndStop(@PathVariable Long routeId, @PathVariable Long stopId) {
        List<EstimatedArrivalDTO> etas = etaService.calculateETAsForRoute(routeId, stopId);
        return ResponseEntity.ok(etas);
    }
}

package com.intellitransit.controller;

import com.intellitransit.dto.AIAlertDTO;
import com.intellitransit.service.AIAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/operations/alerts")
public class AIAlertController {

    private final AIAlertService aiAlertService;

    public AIAlertController(AIAlertService aiAlertService) {
        this.aiAlertService = aiAlertService;
    }

    @GetMapping
    public ResponseEntity<List<AIAlertDTO>> getAllAlerts() {
        List<AIAlertDTO> alerts = aiAlertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/new")
    public ResponseEntity<List<AIAlertDTO>> getNewAlerts() {
        List<AIAlertDTO> alerts = aiAlertService.getNewAlerts();
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/run")
    public ResponseEntity<List<AIAlertDTO>> runAnomalyDetection() {
        List<AIAlertDTO> generatedAlerts = aiAlertService.runAnomalyDetection();
        return ResponseEntity.ok(generatedAlerts);
    }
}

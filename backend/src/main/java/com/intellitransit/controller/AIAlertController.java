package com.intellitransit.controller;

import com.intellitransit.entity.AIAlert;
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
    public ResponseEntity<List<AIAlert>> getAllAlerts() {
        List<AIAlert> alerts = aiAlertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/new")
    public ResponseEntity<List<AIAlert>> getNewAlerts() {
        List<AIAlert> alerts = aiAlertService.getNewAlerts();
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/run")
    public ResponseEntity<List<AIAlert>> runAnomalyDetection() {
        List<AIAlert> generatedAlerts = aiAlertService.runAnomalyDetection();
        return ResponseEntity.ok(generatedAlerts);
    }
}

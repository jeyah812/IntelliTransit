package com.intellitransit.controller;

import com.intellitransit.dto.DecisionRecommendationDTO;
import com.intellitransit.dto.SimulateDispatchRequest;
import com.intellitransit.dto.SimulateDispatchResponse;
import com.intellitransit.entity.SimulatedActionRecord;
import com.intellitransit.service.DecisionSupportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
public class DecisionSupportController {

    private final DecisionSupportService decisionSupportService;

    public DecisionSupportController(DecisionSupportService decisionSupportService) {
        this.decisionSupportService = decisionSupportService;
    }

    @GetMapping("/decision-support/recommendations")
    public ResponseEntity<List<DecisionRecommendationDTO>> getRecommendations() {
        List<DecisionRecommendationDTO> recommendations = decisionSupportService.getRecommendations();
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/decision-support/recommendation/{alertId}")
    public ResponseEntity<DecisionRecommendationDTO> getRecommendationForAlert(@PathVariable Long alertId) {
        DecisionRecommendationDTO recommendation = decisionSupportService.getRecommendationForAlert(alertId);
        if (recommendation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recommendation);
    }

    @PostMapping("/dispatch/simulate")
    public ResponseEntity<?> simulateDispatch(@RequestBody SimulateDispatchRequest request) {
        try {
            SimulateDispatchResponse response = decisionSupportService.simulateDispatch(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/dispatch/history")
    public ResponseEntity<List<SimulatedActionRecord>> getActionHistory() {
        List<SimulatedActionRecord> history = decisionSupportService.getActionHistory();
        return ResponseEntity.ok(history);
    }
}

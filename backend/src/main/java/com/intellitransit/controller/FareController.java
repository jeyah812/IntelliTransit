package com.intellitransit.controller;

import com.intellitransit.dto.FareCalculationRequest;
import com.intellitransit.dto.FareCalculationResponse;
import com.intellitransit.dto.FareRuleDTO;
import com.intellitransit.service.FareCalculationService;
import com.intellitransit.service.FareRuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FareController {

    private final FareCalculationService fareCalculationService;
    private final FareRuleService fareRuleService;

    public FareController(FareCalculationService fareCalculationService, FareRuleService fareRuleService) {
        this.fareCalculationService = fareCalculationService;
        this.fareRuleService = fareRuleService;
    }

    @PostMapping("/fares/calculate")
    public ResponseEntity<FareCalculationResponse> calculateFare(@Valid @RequestBody FareCalculationRequest request) {
        FareCalculationResponse response = fareCalculationService.calculateFare(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/operations/fare-rules")
    public ResponseEntity<FareRuleDTO> createFareRule(@Valid @RequestBody FareRuleDTO fareRuleDTO) {
        FareRuleDTO response = fareRuleService.createFareRule(fareRuleDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

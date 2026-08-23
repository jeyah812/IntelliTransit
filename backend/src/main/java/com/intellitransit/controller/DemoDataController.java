package com.intellitransit.controller;

import com.intellitransit.dto.DemoDataResponse;
import com.intellitransit.service.DemoDataGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class DemoDataController {

    private final DemoDataGeneratorService demoDataGeneratorService;

    public DemoDataController(DemoDataGeneratorService demoDataGeneratorService) {
        this.demoDataGeneratorService = demoDataGeneratorService;
    }

    @PostMapping("/generate-demo-data")
    public ResponseEntity<DemoDataResponse> generateDemoData() {
        DemoDataResponse response = demoDataGeneratorService.generateDemoData();
        return ResponseEntity.ok(response);
    }
}

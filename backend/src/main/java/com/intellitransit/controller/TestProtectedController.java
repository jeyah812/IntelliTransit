package com.intellitransit.controller;

import com.intellitransit.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestProtectedController {

    @GetMapping("/passenger/me")
    public ResponseEntity<Map<String, Object>> getPassengerResource(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(Map.of(
                "message", "Welcome to Passenger Portal",
                "username", userPrincipal.getUsername(),
                "role", userPrincipal.getRole().name()
        ));
    }

    @GetMapping("/driver/trips")
    public ResponseEntity<Map<String, Object>> getDriverResource(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(Map.of(
                "message", "Welcome to Driver Trip Portal",
                "username", userPrincipal.getUsername(),
                "role", userPrincipal.getRole().name()
        ));
    }

    @GetMapping("/operations/dashboard")
    public ResponseEntity<Map<String, Object>> getOperationsDashboard(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(Map.of(
                "message", "Welcome to Operations Manager Dark Enterprise Dashboard",
                "username", userPrincipal.getUsername(),
                "role", userPrincipal.getRole().name()
        ));
    }
}

package com.intellitransit.controller;

import com.intellitransit.dto.IncidentReportRequest;
import com.intellitransit.dto.TripDTO;
import com.intellitransit.dto.TripLogDTO;
import com.intellitransit.dto.TripScheduleRequest;
import com.intellitransit.security.UserPrincipal;
import com.intellitransit.service.TripOperationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TripOperationController {

    private final TripOperationService tripOperationService;

    public TripOperationController(TripOperationService tripOperationService) {
        this.tripOperationService = tripOperationService;
    }

    @PostMapping("/operations/trips/schedule")
    public ResponseEntity<TripDTO> scheduleTrip(@Valid @RequestBody TripScheduleRequest request) {
        TripDTO tripDTO = tripOperationService.scheduleTrip(request);
        return new ResponseEntity<>(tripDTO, HttpStatus.CREATED);
    }

    @PostMapping("/driver/trips/{id}/start")
    public ResponseEntity<TripDTO> startTrip(@AuthenticationPrincipal UserPrincipal currentUser,
                                              @PathVariable Long id) {
        TripDTO tripDTO = tripOperationService.startTrip(currentUser.getId(), id);
        return ResponseEntity.ok(tripDTO);
    }

    @PostMapping("/driver/trips/{id}/end")
    public ResponseEntity<TripDTO> endTrip(@AuthenticationPrincipal UserPrincipal currentUser,
                                            @PathVariable Long id,
                                            @RequestBody(required = false) IncidentReportRequest incidentRequest) {
        String notes = incidentRequest != null ? incidentRequest.getNotes() : null;
        TripDTO tripDTO = tripOperationService.endTrip(currentUser.getId(), id, notes);
        return ResponseEntity.ok(tripDTO);
    }

    @GetMapping("/driver/trips/assigned")
    public ResponseEntity<List<TripDTO>> getAssignedTrips(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<TripDTO> trips = tripOperationService.getDriverAssignedTrips(currentUser.getId());
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/operations/trips")
    public ResponseEntity<List<TripDTO>> getAllTrips() {
        List<TripDTO> trips = tripOperationService.getAllTrips();
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/operations/trip-logs")
    public ResponseEntity<List<TripLogDTO>> getAllTripLogs() {
        List<TripLogDTO> logs = tripOperationService.getAllTripLogs();
        return ResponseEntity.ok(logs);
    }
}

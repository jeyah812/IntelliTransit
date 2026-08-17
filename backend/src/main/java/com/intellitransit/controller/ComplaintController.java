package com.intellitransit.controller;

import com.intellitransit.dto.ComplaintDTO;
import com.intellitransit.dto.ComplaintRequest;
import com.intellitransit.security.UserPrincipal;
import com.intellitransit.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping("/passenger/complaints")
    public ResponseEntity<ComplaintDTO> fileComplaint(@AuthenticationPrincipal UserPrincipal currentUser,
                                                       @Valid @RequestBody ComplaintRequest request) {
        ComplaintDTO response = complaintService.fileComplaint(currentUser.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/passenger/complaints/my")
    public ResponseEntity<List<ComplaintDTO>> getMyComplaints(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<ComplaintDTO> complaints = complaintService.getPassengerComplaints(currentUser.getId());
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/operations/complaints")
    public ResponseEntity<List<ComplaintDTO>> getAllComplaints() {
        List<ComplaintDTO> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }
}

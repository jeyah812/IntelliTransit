package com.intellitransit.controller;

import com.intellitransit.dto.TicketVerificationRequest;
import com.intellitransit.dto.TicketVerificationResponse;
import com.intellitransit.entity.Ticket;
import com.intellitransit.exception.ResourceNotFoundException;
import com.intellitransit.repository.TicketRepository;
import com.intellitransit.security.UserPrincipal;
import com.intellitransit.service.QrTicketService;
import com.intellitransit.service.TripOperationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final QrTicketService qrTicketService;
    private final TripOperationService tripOperationService;

    public TicketController(TicketRepository ticketRepository,
                            QrTicketService qrTicketService,
                            TripOperationService tripOperationService) {
        this.ticketRepository = ticketRepository;
        this.qrTicketService = qrTicketService;
        this.tripOperationService = tripOperationService;
    }

    @GetMapping(value = "/tickets/{ticketId}/qr-image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrCodeImage(@PathVariable Long ticketId,
                                                 @RequestParam(defaultValue = "250") int size) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        byte[] pngData = qrTicketService.generateQrCodeImagePng(ticket.getQrData(), size, size);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(pngData, headers, HttpStatus.OK);
    }

    @PostMapping("/driver/tickets/verify")
    public ResponseEntity<TicketVerificationResponse> verifyTicket(@AuthenticationPrincipal UserPrincipal currentUser,
                                                                   @Valid @RequestBody TicketVerificationRequest request) {
        TicketVerificationResponse response = tripOperationService.verifyTicket(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }
}

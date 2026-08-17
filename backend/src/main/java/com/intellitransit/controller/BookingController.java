package com.intellitransit.controller;

import com.intellitransit.dto.BookingRequest;
import com.intellitransit.dto.BookingResponse;
import com.intellitransit.security.UserPrincipal;
import com.intellitransit.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passenger/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@AuthenticationPrincipal UserPrincipal currentUser,
                                                          @Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(currentUser.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<BookingResponse> responses = bookingService.getPassengerBookings(currentUser.getId());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@AuthenticationPrincipal UserPrincipal currentUser,
                                                           @PathVariable Long id) {
        BookingResponse response = bookingService.getBookingById(currentUser.getId(), id);
        return ResponseEntity.ok(response);
    }
}

package com.intellitransit.service;

import com.intellitransit.dto.BookingRequest;
import com.intellitransit.dto.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(Long userId, BookingRequest request);
    List<BookingResponse> getPassengerBookings(Long userId);
    BookingResponse getBookingById(Long userId, Long bookingId);
}

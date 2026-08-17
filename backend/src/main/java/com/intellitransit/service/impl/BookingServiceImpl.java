package com.intellitransit.service.impl;

import com.intellitransit.dto.*;
import com.intellitransit.entity.*;
import com.intellitransit.entity.enums.BookingStatus;
import com.intellitransit.entity.enums.PaymentStatus;
import com.intellitransit.entity.enums.TicketStatus;
import com.intellitransit.entity.enums.TripStatus;
import com.intellitransit.exception.ResourceNotFoundException;
import com.intellitransit.repository.*;
import com.intellitransit.service.BookingService;
import com.intellitransit.service.FareCalculationService;
import com.intellitransit.service.QrTicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final PassengerRepository passengerRepository;
    private final TripRepository tripRepository;
    private final FareCalculationService fareCalculationService;
    private final QrTicketService qrTicketService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              TicketRepository ticketRepository,
                              PassengerRepository passengerRepository,
                              TripRepository tripRepository,
                              FareCalculationService fareCalculationService,
                              QrTicketService qrTicketService) {
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.passengerRepository = passengerRepository;
        this.tripRepository = tripRepository;
        this.fareCalculationService = fareCalculationService;
        this.qrTicketService = qrTicketService;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(Long userId, BookingRequest request) {
        Passenger passenger = passengerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger profile not found for user ID: " + userId));

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + request.getTripId()));

        if (trip.getStatus() == TripStatus.CANCELLED || trip.getStatus() == TripStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot book ticket for a trip that is " + trip.getStatus());
        }

        FareCalculationRequest fareRequest = new FareCalculationRequest(
                trip.getRoute().getId(),
                request.getOriginStopId(),
                request.getDestinationStopId(),
                request.getPassengerCategory()
        );
        FareCalculationResponse fareResponse = fareCalculationService.calculateFare(fareRequest);

        String bookingRef = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Booking booking = Booking.builder()
                .passenger(passenger)
                .trip(trip)
                .bookingReference(bookingRef)
                .bookingTime(LocalDateTime.now())
                .fareAmount(fareResponse.getFinalFare())
                .paymentStatus(PaymentStatus.PAID)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        String ticketNumber = "TK-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        String qrPayload = qrTicketService.generateQrPayload(ticketNumber, bookingRef, passenger.getId(), trip.getId());

        Ticket ticket = Ticket.builder()
                .booking(savedBooking)
                .ticketNumber(ticketNumber)
                .qrData(qrPayload)
                .issuedAt(LocalDateTime.now())
                .status(TicketStatus.ACTIVE)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        TicketDTO ticketDTO = TicketDTO.builder()
                .id(savedTicket.getId())
                .bookingId(savedBooking.getId())
                .ticketNumber(savedTicket.getTicketNumber())
                .qrData(savedTicket.getQrData())
                .issuedAt(savedTicket.getIssuedAt())
                .verifiedAt(savedTicket.getVerifiedAt())
                .status(savedTicket.getStatus())
                .build();

        return BookingResponse.builder()
                .bookingId(savedBooking.getId())
                .bookingReference(savedBooking.getBookingReference())
                .passengerId(passenger.getId())
                .passengerName(passenger.getFullName())
                .tripId(trip.getId())
                .routeNumber(trip.getRoute().getRouteNumber())
                .routeName(trip.getRoute().getRouteName())
                .fareAmount(savedBooking.getFareAmount())
                .paymentStatus(savedBooking.getPaymentStatus())
                .bookingStatus(savedBooking.getStatus())
                .bookingTime(savedBooking.getBookingTime())
                .ticket(ticketDTO)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getPassengerBookings(Long userId) {
        Passenger passenger = passengerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger profile not found for user ID: " + userId));

        List<Booking> bookings = bookingRepository.findByPassengerId(passenger.getId());
        return bookings.stream().map(this::mapToBookingResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        return mapToBookingResponse(booking);
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        Ticket ticket = ticketRepository.findByBookingId(booking.getId()).orElse(null);
        TicketDTO ticketDTO = null;
        if (ticket != null) {
            ticketDTO = TicketDTO.builder()
                    .id(ticket.getId())
                    .bookingId(booking.getId())
                    .ticketNumber(ticket.getTicketNumber())
                    .qrData(ticket.getQrData())
                    .issuedAt(ticket.getIssuedAt())
                    .verifiedAt(ticket.getVerifiedAt())
                    .status(ticket.getStatus())
                    .build();
        }

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .bookingReference(booking.getBookingReference())
                .passengerId(booking.getPassenger().getId())
                .passengerName(booking.getPassenger().getFullName())
                .tripId(booking.getTrip().getId())
                .routeNumber(booking.getTrip().getRoute().getRouteNumber())
                .routeName(booking.getTrip().getRoute().getRouteName())
                .fareAmount(booking.getFareAmount())
                .paymentStatus(booking.getPaymentStatus())
                .bookingStatus(booking.getStatus())
                .bookingTime(booking.getBookingTime())
                .ticket(ticketDTO)
                .build();
    }
}

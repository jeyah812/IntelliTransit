package com.intellitransit.service.impl;

import com.intellitransit.dto.*;
import com.intellitransit.entity.*;
import com.intellitransit.entity.enums.DriverStatus;
import com.intellitransit.entity.enums.TicketStatus;
import com.intellitransit.entity.enums.TripStatus;
import com.intellitransit.exception.ResourceNotFoundException;
import com.intellitransit.repository.*;
import com.intellitransit.service.TripOperationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TripOperationServiceImpl implements TripOperationService {

    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final TicketRepository ticketRepository;
    private final TripLogRepository tripLogRepository;

    public TripOperationServiceImpl(TripRepository tripRepository,
                                  RouteRepository routeRepository,
                                  BusRepository busRepository,
                                  DriverRepository driverRepository,
                                  TicketRepository ticketRepository,
                                  TripLogRepository tripLogRepository) {
        this.tripRepository = tripRepository;
        this.routeRepository = routeRepository;
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.ticketRepository = ticketRepository;
        this.tripLogRepository = tripLogRepository;
    }

    @Override
    @Transactional
    public TripDTO scheduleTrip(TripScheduleRequest request) {
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with ID: " + request.getRouteId()));

        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + request.getBusId()));

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + request.getDriverId()));

        Trip trip = Trip.builder()
                .route(route)
                .bus(bus)
                .driver(driver)
                .scheduledStart(request.getScheduledStart())
                .scheduledEnd(request.getScheduledEnd())
                .status(TripStatus.SCHEDULED)
                .build();

        Trip savedTrip = tripRepository.save(trip);
        return mapToTripDTO(savedTrip);
    }

    @Override
    @Transactional
    public TripDTO startTrip(Long driverUserId, Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

        Driver driver = driverRepository.findByUserId(driverUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found for user ID: " + driverUserId));

        if (!trip.getDriver().getId().equals(driver.getId())) {
            throw new IllegalArgumentException("Driver is not assigned to this trip");
        }

        if (trip.getStatus() != TripStatus.SCHEDULED) {
            throw new IllegalStateException("Cannot start trip. Current status is " + trip.getStatus() + ". Only SCHEDULED trips can be started.");
        }

        trip.setStatus(TripStatus.IN_PROGRESS);
        trip.setActualStart(LocalDateTime.now());
        Trip updatedTrip = tripRepository.save(trip);

        driver.setStatus(DriverStatus.ON_TRIP);
        driverRepository.save(driver);

        return mapToTripDTO(updatedTrip);
    }

    @Override
    @Transactional
    public TripDTO endTrip(Long driverUserId, Long tripId, String notes) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

        Driver driver = driverRepository.findByUserId(driverUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found for user ID: " + driverUserId));

        if (!trip.getDriver().getId().equals(driver.getId())) {
            throw new IllegalArgumentException("Driver is not assigned to this trip");
        }

        if (trip.getStatus() != TripStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot end trip. Current status is " + trip.getStatus() + ". Only IN_PROGRESS trips can be completed.");
        }

        LocalDateTime actualEnd = LocalDateTime.now();
        trip.setStatus(TripStatus.COMPLETED);
        trip.setActualEnd(actualEnd);
        Trip updatedTrip = tripRepository.save(trip);

        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);

        LocalDateTime actualStart = trip.getActualStart() != null ? trip.getActualStart() : trip.getScheduledStart();
        long minutes = Duration.between(actualStart, actualEnd).toMinutes();

        TripLog tripLog = TripLog.builder()
                .trip(updatedTrip)
                .actualStart(actualStart)
                .actualEnd(actualEnd)
                .durationMinutes((int) minutes)
                .notes(notes != null ? notes : "Trip completed normally")
                .build();
        tripLogRepository.save(tripLog);

        return mapToTripDTO(updatedTrip);
    }

    @Override
    @Transactional
    public TicketVerificationResponse verifyTicket(Long driverUserId, TicketVerificationRequest request) {
        Driver driver = driverRepository.findByUserId(driverUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found for user ID: " + driverUserId));

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + request.getTripId()));

        if (!trip.getDriver().getId().equals(driver.getId())) {
            return TicketVerificationResponse.builder()
                    .valid(false)
                    .message("Driver is not authorized to verify tickets for this trip")
                    .build();
        }

        Optional<Ticket> ticketOpt = ticketRepository.findByTicketNumber(request.getTicketNumberOrQrData());
        if (ticketOpt.isEmpty()) {
            ticketOpt = ticketRepository.findByQrData(request.getTicketNumberOrQrData());
        }

        if (ticketOpt.isEmpty()) {
            return TicketVerificationResponse.builder()
                    .valid(false)
                    .message("Invalid ticket: Ticket not found")
                    .build();
        }

        Ticket ticket = ticketOpt.get();

        if (!ticket.getBooking().getTrip().getId().equals(request.getTripId())) {
            return TicketVerificationResponse.builder()
                    .valid(false)
                    .message("Invalid ticket: Ticket is for a different trip (Trip ID: " + ticket.getBooking().getTrip().getId() + ")")
                    .ticketNumber(ticket.getTicketNumber())
                    .bookingReference(ticket.getBooking().getBookingReference())
                    .passengerName(ticket.getBooking().getPassenger().getFullName())
                    .build();
        }

        if (ticket.getStatus() == TicketStatus.USED) {
            return TicketVerificationResponse.builder()
                    .valid(false)
                    .message("Invalid ticket: Ticket has ALREADY BEEN USED")
                    .ticketNumber(ticket.getTicketNumber())
                    .bookingReference(ticket.getBooking().getBookingReference())
                    .passengerName(ticket.getBooking().getPassenger().getFullName())
                    .build();
        }

        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            return TicketVerificationResponse.builder()
                    .valid(false)
                    .message("Invalid ticket: Ticket status is " + ticket.getStatus())
                    .ticketNumber(ticket.getTicketNumber())
                    .bookingReference(ticket.getBooking().getBookingReference())
                    .passengerName(ticket.getBooking().getPassenger().getFullName())
                    .build();
        }

        ticket.setStatus(TicketStatus.USED);
        ticket.setVerifiedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        return TicketVerificationResponse.builder()
                .valid(true)
                .message("Ticket verified successfully")
                .ticketNumber(ticket.getTicketNumber())
                .bookingReference(ticket.getBooking().getBookingReference())
                .passengerName(ticket.getBooking().getPassenger().getFullName())
                .verifiedAt(ticket.getVerifiedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> getDriverAssignedTrips(Long driverUserId) {
        Driver driver = driverRepository.findByUserId(driverUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found for user ID: " + driverUserId));

        return tripRepository.findByDriverId(driver.getId()).stream()
                .map(this::mapToTripDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(this::mapToTripDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripLogDTO> getAllTripLogs() {
        return tripLogRepository.findAll().stream()
                .map(log -> TripLogDTO.builder()
                        .id(log.getId())
                        .tripId(log.getTrip().getId())
                        .routeNumber(log.getTrip().getRoute().getRouteNumber())
                        .actualStart(log.getActualStart())
                        .actualEnd(log.getActualEnd())
                        .durationMinutes(log.getDurationMinutes())
                        .recordedAt(log.getRecordedAt())
                        .notes(log.getNotes())
                        .build())
                .collect(Collectors.toList());
    }

    private TripDTO mapToTripDTO(Trip trip) {
        return TripDTO.builder()
                .id(trip.getId())
                .routeId(trip.getRoute().getId())
                .routeNumber(trip.getRoute().getRouteNumber())
                .routeName(trip.getRoute().getRouteName())
                .busId(trip.getBus().getId())
                .busNumber(trip.getBus().getBusNumber())
                .driverId(trip.getDriver().getId())
                .driverName(trip.getDriver().getFullName())
                .scheduledStart(trip.getScheduledStart())
                .scheduledEnd(trip.getScheduledEnd())
                .actualStart(trip.getActualStart())
                .actualEnd(trip.getActualEnd())
                .status(trip.getStatus())
                .build();
    }
}

package com.intellitransit.service;

import com.intellitransit.dto.*;
import com.intellitransit.entity.*;
import com.intellitransit.entity.enums.DriverStatus;
import com.intellitransit.entity.enums.TicketStatus;
import com.intellitransit.entity.enums.TripStatus;
import com.intellitransit.repository.*;
import com.intellitransit.service.impl.TripOperationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripOperationServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TripLogRepository tripLogRepository;

    @InjectMocks
    private TripOperationServiceImpl tripOperationService;

    private User driverUser;
    private Driver driver;
    private Route route;
    private Bus bus;
    private Trip scheduledTrip;
    private Trip inProgressTrip;
    private Trip completedTrip;

    @BeforeEach
    void setUp() {
        driverUser = User.builder().id(20L).username("driver_user").build();
        driver = Driver.builder().id(2L).user(driverUser).fullName("John Driver").status(DriverStatus.AVAILABLE).build();

        route = Route.builder().id(1L).routeNumber("R10").routeName("City Route").build();
        bus = Bus.builder().id(1L).busNumber("B99").build();

        scheduledTrip = Trip.builder()
                .id(100L)
                .route(route)
                .bus(bus)
                .driver(driver)
                .scheduledStart(LocalDateTime.now().plusMinutes(10))
                .scheduledEnd(LocalDateTime.now().plusHours(1))
                .status(TripStatus.SCHEDULED)
                .build();

        inProgressTrip = Trip.builder()
                .id(101L)
                .route(route)
                .bus(bus)
                .driver(driver)
                .scheduledStart(LocalDateTime.now().minusMinutes(30))
                .scheduledEnd(LocalDateTime.now().plusMinutes(30))
                .actualStart(LocalDateTime.now().minusMinutes(30))
                .status(TripStatus.IN_PROGRESS)
                .build();

        completedTrip = Trip.builder()
                .id(102L)
                .route(route)
                .bus(bus)
                .driver(driver)
                .scheduledStart(LocalDateTime.now().minusHours(2))
                .scheduledEnd(LocalDateTime.now().minusHours(1))
                .actualStart(LocalDateTime.now().minusHours(2))
                .actualEnd(LocalDateTime.now().minusHours(1))
                .status(TripStatus.COMPLETED)
                .build();
    }

    @Test
    @DisplayName("Should successfully start trip and transition driver status from AVAILABLE to ON_TRIP")
    void testStartTripSuccess() {
        when(tripRepository.findById(100L)).thenReturn(Optional.of(scheduledTrip));
        when(driverRepository.findByUserId(20L)).thenReturn(Optional.of(driver));
        when(tripRepository.save(any(Trip.class))).thenAnswer(i -> i.getArgument(0));

        TripDTO result = tripOperationService.startTrip(20L, 100L);

        assertNotNull(result);
        assertEquals(TripStatus.IN_PROGRESS, result.getStatus());
        assertEquals(DriverStatus.ON_TRIP, driver.getStatus());
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when starting an already completed trip")
    void testStartTripInvalidTransition() {
        when(tripRepository.findById(102L)).thenReturn(Optional.of(completedTrip));
        when(driverRepository.findByUserId(20L)).thenReturn(Optional.of(driver));

        assertThrows(IllegalStateException.class, () -> tripOperationService.startTrip(20L, 102L));
    }

    @Test
    @DisplayName("Should end trip, transition driver status to AVAILABLE, and create TripLog")
    void testEndTripSuccess() {
        driver.setStatus(DriverStatus.ON_TRIP);
        when(tripRepository.findById(101L)).thenReturn(Optional.of(inProgressTrip));
        when(driverRepository.findByUserId(20L)).thenReturn(Optional.of(driver));
        when(tripRepository.save(any(Trip.class))).thenAnswer(i -> i.getArgument(0));

        TripDTO result = tripOperationService.endTrip(20L, 101L, "Heavy traffic on main st");

        assertNotNull(result);
        assertEquals(TripStatus.COMPLETED, result.getStatus());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());

        verify(driverRepository, times(1)).save(driver);
        verify(tripLogRepository, times(1)).save(any(TripLog.class));
    }

    @Test
    @DisplayName("Should verify active ticket successfully and transition status to USED")
    void testVerifyTicketSuccess() {
        Passenger passenger = Passenger.builder().id(1L).fullName("Bob Passenger").build();
        Booking booking = Booking.builder().id(10L).trip(inProgressTrip).passenger(passenger).bookingReference("BK-100").build();
        Ticket ticket = Ticket.builder().id(55L).booking(booking).ticketNumber("TK-55").status(TicketStatus.ACTIVE).build();

        when(driverRepository.findByUserId(20L)).thenReturn(Optional.of(driver));
        when(tripRepository.findById(101L)).thenReturn(Optional.of(inProgressTrip));
        when(ticketRepository.findByTicketNumber("TK-55")).thenReturn(Optional.of(ticket));

        TicketVerificationRequest request = new TicketVerificationRequest("TK-55", 101L);
        TicketVerificationResponse response = tripOperationService.verifyTicket(20L, request);

        assertTrue(response.isValid());
        assertEquals("Ticket verified successfully", response.getMessage());
        assertEquals(TicketStatus.USED, ticket.getStatus());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Should reject verification when ticket has already been used")
    void testVerifyTicketAlreadyUsed() {
        Passenger passenger = Passenger.builder().id(1L).fullName("Bob Passenger").build();
        Booking booking = Booking.builder().id(10L).trip(inProgressTrip).passenger(passenger).bookingReference("BK-100").build();
        Ticket ticket = Ticket.builder().id(55L).booking(booking).ticketNumber("TK-55").status(TicketStatus.USED).build();

        when(driverRepository.findByUserId(20L)).thenReturn(Optional.of(driver));
        when(tripRepository.findById(101L)).thenReturn(Optional.of(inProgressTrip));
        when(ticketRepository.findByTicketNumber("TK-55")).thenReturn(Optional.of(ticket));

        TicketVerificationRequest request = new TicketVerificationRequest("TK-55", 101L);
        TicketVerificationResponse response = tripOperationService.verifyTicket(20L, request);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("ALREADY BEEN USED"));
    }

    @Test
    @DisplayName("Should reject verification when ticket belongs to a different trip")
    void testVerifyTicketWrongTrip() {
        Trip differentTrip = Trip.builder().id(999L).build();
        Passenger passenger = Passenger.builder().id(1L).fullName("Bob Passenger").build();
        Booking booking = Booking.builder().id(10L).trip(differentTrip).passenger(passenger).bookingReference("BK-100").build();
        Ticket ticket = Ticket.builder().id(55L).booking(booking).ticketNumber("TK-55").status(TicketStatus.ACTIVE).build();

        when(driverRepository.findByUserId(20L)).thenReturn(Optional.of(driver));
        when(tripRepository.findById(101L)).thenReturn(Optional.of(inProgressTrip));
        when(ticketRepository.findByTicketNumber("TK-55")).thenReturn(Optional.of(ticket));

        TicketVerificationRequest request = new TicketVerificationRequest("TK-55", 101L);
        TicketVerificationResponse response = tripOperationService.verifyTicket(20L, request);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("different trip"));
    }

    @Test
    @DisplayName("Should reject verification when driver is not authorized for the trip")
    void testVerifyTicketUnauthorizedDriver() {
        Driver otherDriver = Driver.builder().id(99L).build();
        Trip otherDriverTrip = Trip.builder().id(101L).driver(otherDriver).build();

        when(driverRepository.findByUserId(20L)).thenReturn(Optional.of(driver));
        when(tripRepository.findById(101L)).thenReturn(Optional.of(otherDriverTrip));

        TicketVerificationRequest request = new TicketVerificationRequest("TK-55", 101L);
        TicketVerificationResponse response = tripOperationService.verifyTicket(20L, request);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("not authorized"));
    }
}

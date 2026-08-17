package com.intellitransit.service;

import com.intellitransit.dto.*;
import com.intellitransit.entity.*;
import com.intellitransit.entity.enums.BookingStatus;
import com.intellitransit.entity.enums.PaymentStatus;
import com.intellitransit.entity.enums.TicketStatus;
import com.intellitransit.entity.enums.TripStatus;
import com.intellitransit.repository.*;
import com.intellitransit.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private FareCalculationService fareCalculationService;

    @Mock
    private QrTicketService qrTicketService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User passengerUser;
    private Passenger passenger;
    private Route route;
    private Bus bus;
    private Driver driver;
    private Trip trip;

    @BeforeEach
    void setUp() {
        passengerUser = User.builder().id(10L).username("passenger1").build();
        passenger = Passenger.builder().id(1L).user(passengerUser).fullName("Jane Passenger").build();
        route = Route.builder().id(100L).routeNumber("R50").routeName("Express Way").build();
        bus = Bus.builder().id(5L).busNumber("B101").build();
        driver = Driver.builder().id(8L).fullName("Driver Bob").build();

        trip = Trip.builder()
                .id(50L)
                .route(route)
                .bus(bus)
                .driver(driver)
                .scheduledStart(LocalDateTime.now().plusHours(1))
                .scheduledEnd(LocalDateTime.now().plusHours(2))
                .status(TripStatus.SCHEDULED)
                .build();
    }

    @Test
    @DisplayName("Should create booking and ticket atomically in one transaction")
    void testCreateBookingSuccess() {
        BookingRequest request = new BookingRequest(50L, 101L, 102L, "STANDARD");

        FareCalculationResponse fareResponse = FareCalculationResponse.builder()
                .routeId(100L)
                .originStopName("Stop A")
                .destinationStopName("Stop B")
                .distanceKm(new BigDecimal("8.0"))
                .baseFare(new BigDecimal("30.00"))
                .discountPercentage(BigDecimal.ZERO)
                .finalFare(new BigDecimal("30.00"))
                .passengerCategory("STANDARD")
                .build();

        when(passengerRepository.findByUserId(10L)).thenReturn(Optional.of(passenger));
        when(tripRepository.findById(50L)).thenReturn(Optional.of(trip));
        when(fareCalculationService.calculateFare(any())).thenReturn(fareResponse);
        when(qrTicketService.generateQrPayload(anyString(), anyString(), anyLong(), anyLong())).thenReturn("{\"mock\":\"payload\"}");

        Booking savedBooking = Booking.builder()
                .id(500L)
                .passenger(passenger)
                .trip(trip)
                .bookingReference("BK-TEST12")
                .bookingTime(LocalDateTime.now())
                .fareAmount(new BigDecimal("30.00"))
                .paymentStatus(PaymentStatus.PAID)
                .status(BookingStatus.CONFIRMED)
                .build();

        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        Ticket savedTicket = Ticket.builder()
                .id(900L)
                .booking(savedBooking)
                .ticketNumber("TK-TEST99")
                .qrData("{\"mock\":\"payload\"}")
                .issuedAt(LocalDateTime.now())
                .status(TicketStatus.ACTIVE)
                .build();

        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        BookingResponse response = bookingService.createBooking(10L, request);

        assertNotNull(response);
        assertEquals(500L, response.getBookingId());
        assertEquals("BK-TEST12", response.getBookingReference());
        assertEquals(new BigDecimal("30.00"), response.getFareAmount());
        assertNotNull(response.getTicket());
        assertEquals("TK-TEST99", response.getTicket().getTicketNumber());

        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }
}

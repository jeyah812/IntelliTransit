package com.intellitransit.service;

import com.intellitransit.dto.DemoDataResponse;
import com.intellitransit.entity.*;
import com.intellitransit.entity.enums.*;
import com.intellitransit.repository.*;
import com.intellitransit.service.impl.DemoDataGeneratorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemoDataGeneratorServiceTest {

    @Mock private RouteRepository routeRepository;
    @Mock private StopRepository stopRepository;
    @Mock private BusRepository busRepository;
    @Mock private DriverRepository driverRepository;
    @Mock private TripRepository tripRepository;
    @Mock private TripLogRepository tripLogRepository;
    @Mock private UserRepository userRepository;
    @Mock private PassengerRepository passengerRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private ComplaintRepository complaintRepository;
    @Mock private AIAlertRepository aiAlertRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DemoDataGeneratorServiceImpl demoDataGeneratorService;

    @BeforeEach
    void setUp() {
        Bus bus = new Bus(1L, "TN-01-N-1001", "BUS-1", "Volvo B9R", 50, BusStatus.ACTIVE, LocalDateTime.now());
        User user = new User(1L, "p1", "p1@test.com", "pass", UserRole.PASSENGER, true, LocalDateTime.now(), LocalDateTime.now());
        Driver driver = new Driver(1L, user, "EMP-1001", "Driver 1", "+919876543210", "DL-1001", DriverStatus.AVAILABLE, LocalDateTime.now());
        Passenger passenger = new Passenger(1L, user, "Passenger 1", "+919800000010", LocalDateTime.now());

        when(busRepository.findAll()).thenReturn(List.of(bus));
        when(driverRepository.findAll()).thenReturn(List.of(driver));
        when(passengerRepository.findAll()).thenReturn(List.of(passenger));

        when(stopRepository.save(any(Stop.class))).thenAnswer(i -> i.getArgument(0));
        when(routeRepository.save(any(Route.class))).thenAnswer(i -> i.getArgument(0));
        when(tripRepository.save(any(Trip.class))).thenAnswer(i -> i.getArgument(0));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));
        when(aiAlertRepository.save(any(AIAlert.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    @DisplayName("Should generate demo data: 50 routes, 100 stops, 200 trips, 1000 bookings, 100 complaints, 50 AI alerts")
    void testGenerateDemoDataSuccess() {
        DemoDataResponse response = demoDataGeneratorService.generateDemoData();

        assertNotNull(response);
        assertEquals(50, response.getRoutesGenerated());
        assertEquals(100, response.getStopsGenerated());
        assertEquals(200, response.getTripsGenerated());
        assertEquals(1000, response.getBookingsGenerated());
        assertEquals(100, response.getComplaintsGenerated());
        assertEquals(50, response.getAlertsGenerated());
        assertEquals("COMPLETED", response.getStatus());

        verify(stopRepository, times(100)).save(any(Stop.class));
        verify(routeRepository, times(50)).save(any(Route.class));
        verify(tripRepository, times(200)).save(any(Trip.class));
        verify(bookingRepository, times(1000)).save(any(Booking.class));
        verify(complaintRepository, times(100)).save(any(Complaint.class));
        verify(aiAlertRepository, times(50)).save(any(AIAlert.class));
    }
}

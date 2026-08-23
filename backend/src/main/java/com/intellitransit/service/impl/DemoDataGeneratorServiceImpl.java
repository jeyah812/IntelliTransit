package com.intellitransit.service.impl;

import com.intellitransit.dto.DemoDataResponse;
import com.intellitransit.entity.*;
import com.intellitransit.entity.enums.*;
import com.intellitransit.repository.*;
import com.intellitransit.service.DemoDataGeneratorService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DemoDataGeneratorServiceImpl implements DemoDataGeneratorService {

    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;
    private final TripLogRepository tripLogRepository;
    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    private final ComplaintRepository complaintRepository;
    private final AIAlertRepository aiAlertRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataGeneratorServiceImpl(RouteRepository routeRepository,
                                         StopRepository stopRepository,
                                         BusRepository busRepository,
                                         DriverRepository driverRepository,
                                         TripRepository tripRepository,
                                         TripLogRepository tripLogRepository,
                                         UserRepository userRepository,
                                         PassengerRepository passengerRepository,
                                         BookingRepository bookingRepository,
                                         ComplaintRepository complaintRepository,
                                         AIAlertRepository aiAlertRepository,
                                         PasswordEncoder passwordEncoder) {
        this.routeRepository = routeRepository;
        this.stopRepository = stopRepository;
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.tripRepository = tripRepository;
        this.tripLogRepository = tripLogRepository;
        this.userRepository = userRepository;
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.complaintRepository = complaintRepository;
        this.aiAlertRepository = aiAlertRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public DemoDataResponse generateDemoData() {
        LocalDateTime now = LocalDateTime.now();

        // 0. Ensure Fleet & Passengers exist
        List<Bus> buses = busRepository.findAll();
        if (buses.isEmpty()) {
            buses = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                Bus bus = new Bus(null, "TN-01-N-" + (1000 + i), "BUS-DEMO-" + i, "Volvo B9R", 50, BusStatus.ACTIVE, now);
                buses.add(busRepository.save(bus));
            }
        }

        List<Driver> drivers = driverRepository.findAll();
        if (drivers.isEmpty()) {
            drivers = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                User driverUser = new User(null, "demodriver" + i, "demodriver" + i + "@intellitransit.com", passwordEncoder.encode("password123"), UserRole.DRIVER, true, now, now);
                User savedDriverUser = userRepository.save(driverUser);
                Driver driver = new Driver(null, savedDriverUser, "EMP-DEMO-" + (1000 + i), "Demo Driver " + i, "+9198765432" + (10 + i), "DL-DEMO-" + (1000 + i), DriverStatus.AVAILABLE, now);
                drivers.add(driverRepository.save(driver));
            }
        }

        List<Passenger> passengers = passengerRepository.findAll();
        if (passengers.isEmpty()) {
            passengers = new ArrayList<>();
            String encodedPassword = passwordEncoder.encode("password123");
            for (int i = 1; i <= 20; i++) {
                User user = new User(null, "demopassenger" + i, "demopassenger" + i + "@intellitransit.com", encodedPassword, UserRole.PASSENGER, true, now, now);
                User savedUser = userRepository.save(user);
                Passenger passenger = new Passenger(null, savedUser, "Demo Passenger " + i, "+9198000000" + (10 + i), now);
                passengers.add(passengerRepository.save(passenger));
            }
        }

        // 1. Generate 100 Stops
        List<Stop> generatedStops = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            String gtfsStopId = "DEMO-STOP-" + i;
            BigDecimal lat = BigDecimal.valueOf(12.8500 + (i * 0.003)).setScale(8, RoundingMode.HALF_UP);
            BigDecimal lon = BigDecimal.valueOf(80.0500 + (i * 0.002)).setScale(8, RoundingMode.HALF_UP);
            Stop stop = new Stop(null, gtfsStopId, "MTC", "2.0", "Chennai Station Stop " + i, lat, lon, now);
            generatedStops.add(stopRepository.save(stop));
        }

        // 2. Generate 50 Routes
        List<Route> generatedRoutes = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            Stop origin = generatedStops.get((i - 1) % 100);
            Stop destination = generatedStops.get((i + 10) % 100);
            Route route = Route.builder()
                    .gtfsRouteId("DEMO-ROUTE-" + i)
                    .feedId("MTC")
                    .feedVersion("2.0")
                    .routeNumber("DEMO-R" + i)
                    .routeName("Route " + i + " (" + origin.getName() + " to " + destination.getName() + ")")
                    .origin(origin.getName())
                    .destination(destination.getName())
                    .distanceKm(BigDecimal.valueOf(6.0 + (i % 20) * 1.5).setScale(2, RoundingMode.HALF_UP))
                    .estimatedDurationMinutes(25 + (i % 35))
                    .active(true)
                    .createdAt(now)
                    .build();
            generatedRoutes.add(routeRepository.save(route));
        }

        // 3. Generate 200 Trips
        List<Trip> generatedTrips = new ArrayList<>();
        TripStatus[] statuses = {TripStatus.COMPLETED, TripStatus.COMPLETED, TripStatus.COMPLETED, TripStatus.SCHEDULED, TripStatus.IN_PROGRESS};
        for (int i = 1; i <= 200; i++) {
            Route route = generatedRoutes.get((i - 1) % generatedRoutes.size());
            Bus bus = buses.get((i - 1) % buses.size());
            Driver driver = drivers.get((i - 1) % drivers.size());
            TripStatus status = statuses[(i - 1) % statuses.length];

            LocalDateTime scheduledStart = now.minusDays(5 - (i % 6)).plusHours((i * 2) % 24);
            LocalDateTime scheduledEnd = scheduledStart.plusMinutes(route.getEstimatedDurationMinutes());

            LocalDateTime actualStart = null;
            LocalDateTime actualEnd = null;

            if (status == TripStatus.COMPLETED) {
                actualStart = scheduledStart.plusMinutes(-2 + (i % 10));
                actualEnd = actualStart.plusMinutes(route.getEstimatedDurationMinutes() + (-4 + (i % 25)));
            } else if (status == TripStatus.IN_PROGRESS) {
                actualStart = scheduledStart.plusMinutes(-1);
            }

            Trip trip = Trip.builder()
                    .gtfsTripId("DEMO-TRIP-" + i)
                    .route(route)
                    .bus(bus)
                    .driver(driver)
                    .scheduledStart(scheduledStart)
                    .scheduledEnd(scheduledEnd)
                    .actualStart(actualStart)
                    .actualEnd(actualEnd)
                    .status(status)
                    .build();

            Trip savedTrip = tripRepository.save(trip);
            generatedTrips.add(savedTrip);

            if (status == TripStatus.COMPLETED && actualStart != null && actualEnd != null) {
                long durationMinutes = java.time.temporal.ChronoUnit.MINUTES.between(actualStart, actualEnd);
                TripLog log = TripLog.builder()
                        .trip(savedTrip)
                        .actualStart(actualStart)
                        .actualEnd(actualEnd)
                        .durationMinutes((int) durationMinutes)
                        .notes("Demo trip execution completed successfully.")
                        .build();
                tripLogRepository.save(log);
            }
        }

        // 4. Generate 1000 Bookings
        for (int i = 1; i <= 1000; i++) {
            Trip trip = generatedTrips.get((i - 1) % generatedTrips.size());
            Passenger passenger = passengers.get((i - 1) % passengers.size());
            String bookingRef = "BK-DEMO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            Booking booking = Booking.builder()
                    .bookingReference(bookingRef)
                    .passenger(passenger)
                    .trip(trip)
                    .bookingTime(trip.getScheduledStart().minusHours(2 + (i % 12)))
                    .fareAmount(BigDecimal.valueOf(20.00 + (i % 40)).setScale(2, RoundingMode.HALF_UP))
                    .status(BookingStatus.CONFIRMED)
                    .paymentStatus(PaymentStatus.PAID)
                    .build();

            bookingRepository.save(booking);
        }

        // 5. Generate 100 Complaints
        ComplaintCategory[] categories = ComplaintCategory.values();
        ComplaintStatus[] cStatuses = ComplaintStatus.values();
        for (int i = 1; i <= 100; i++) {
            Trip trip = generatedTrips.get((i - 1) % generatedTrips.size());
            Passenger passenger = passengers.get((i - 1) % passengers.size());

            Complaint complaint = Complaint.builder()
                    .passenger(passenger)
                    .trip(trip)
                    .subject("Demo Complaint #" + i + " - " + categories[(i - 1) % categories.length])
                    .description("Passenger raised issue regarding trip schedule and bus condition.")
                    .category(categories[(i - 1) % categories.length])
                    .status(cStatuses[(i - 1) % cStatuses.length])
                    .build();

            complaintRepository.save(complaint);
        }

        // 6. Generate 50 AI Alerts
        AlertType[] alertTypes = AlertType.values();
        AlertSeverity[] severities = AlertSeverity.values();
        AlertStatus[] aStatuses = AlertStatus.values();

        for (int i = 1; i <= 50; i++) {
            Trip trip = generatedTrips.get((i - 1) % generatedTrips.size());
            AlertType type = alertTypes[(i - 1) % alertTypes.length];
            AlertSeverity severity = severities[(i - 1) % severities.length];
            AlertStatus status = aStatuses[(i - 1) % aStatuses.length];

            AIAlert alert = AIAlert.builder()
                    .trip(trip)
                    .alertType(type)
                    .severity(severity)
                    .anomalyScore(BigDecimal.valueOf(1.25 + (i % 10) * 0.15).setScale(4, RoundingMode.HALF_UP))
                    .explanation("Generated demo anomaly for " + type)
                    .recommendation("Review operational parameters and driver logs")
                    .status(status)
                    .build();

            aiAlertRepository.save(alert);
        }

        return DemoDataResponse.builder()
                .routesGenerated(50)
                .stopsGenerated(100)
                .tripsGenerated(200)
                .bookingsGenerated(1000)
                .complaintsGenerated(100)
                .alertsGenerated(50)
                .status("COMPLETED")
                .message("Successfully generated 50 routes, 100 stops, 200 trips, 1000 bookings, 100 complaints, and 50 AI alerts.")
                .build();
    }
}

package com.intellitransit.config;

import com.intellitransit.entity.Driver;
import com.intellitransit.entity.Passenger;
import com.intellitransit.entity.User;
import com.intellitransit.entity.enums.DriverStatus;
import com.intellitransit.entity.enums.UserRole;
import com.intellitransit.repository.DriverRepository;
import com.intellitransit.repository.PassengerRepository;
import com.intellitransit.repository.RouteRepository;
import com.intellitransit.repository.UserRepository;
import com.intellitransit.service.DemoDataGeneratorService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final RouteRepository routeRepository;
    private final DemoDataGeneratorService demoDataGeneratorService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PassengerRepository passengerRepository,
                           DriverRepository driverRepository,
                           RouteRepository routeRepository,
                           DemoDataGeneratorService demoDataGeneratorService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
        this.routeRepository = routeRepository;
        this.demoDataGeneratorService = demoDataGeneratorService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        LocalDateTime now = LocalDateTime.now();
        String defaultPassword = passwordEncoder.encode("password123");

        // 1. Ensure Operations Manager exists
        if (!userRepository.existsByUsername("manager1")) {
            User manager = User.builder()
                    .username("manager1")
                    .email("manager1@intellitransit.com")
                    .password(defaultPassword)
                    .role(UserRole.OPERATIONS_MANAGER)
                    .enabled(true)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            userRepository.save(manager);
        }

        // 2. Ensure Demo Passenger exists
        if (!userRepository.existsByUsername("demopassenger1")) {
            User passengerUser = User.builder()
                    .username("demopassenger1")
                    .email("demopassenger1@intellitransit.com")
                    .password(defaultPassword)
                    .role(UserRole.PASSENGER)
                    .enabled(true)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            User savedPassengerUser = userRepository.save(passengerUser);

            Passenger passenger = Passenger.builder()
                    .user(savedPassengerUser)
                    .fullName("Demo Passenger 1")
                    .phone("+919800000011")
                    .createdAt(now)
                    .build();
            passengerRepository.save(passenger);
        }

        // 3. Ensure Demo Driver exists
        if (!userRepository.existsByUsername("demodriver1")) {
            User driverUser = User.builder()
                    .username("demodriver1")
                    .email("demodriver1@intellitransit.com")
                    .password(defaultPassword)
                    .role(UserRole.DRIVER)
                    .enabled(true)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            User savedDriverUser = userRepository.save(driverUser);

            Driver driver = Driver.builder()
                    .user(savedDriverUser)
                    .employeeId("EMP-DEMO-1001")
                    .fullName("Demo Driver 1")
                    .phone("+919876543211")
                    .licenseNumber("DL-DEMO-1001")
                    .status(DriverStatus.AVAILABLE)
                    .createdAt(now)
                    .build();
            driverRepository.save(driver);
        }

        // 4. Ensure Demo Data and RouteStop associations are populated and repaired
        demoDataGeneratorService.generateDemoData();
    }
}

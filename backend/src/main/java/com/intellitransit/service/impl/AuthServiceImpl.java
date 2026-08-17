package com.intellitransit.service.impl;

import com.intellitransit.dto.AuthResponse;
import com.intellitransit.dto.LoginRequest;
import com.intellitransit.dto.RegisterRequest;
import com.intellitransit.entity.Driver;
import com.intellitransit.entity.Passenger;
import com.intellitransit.entity.User;
import com.intellitransit.entity.enums.UserRole;
import com.intellitransit.exception.DuplicateResourceException;
import com.intellitransit.exception.InvalidCredentialsException;
import com.intellitransit.repository.DriverRepository;
import com.intellitransit.repository.PassengerRepository;
import com.intellitransit.repository.UserRepository;
import com.intellitransit.security.JwtTokenProvider;
import com.intellitransit.security.UserPrincipal;
import com.intellitransit.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           PassengerRepository passengerRepository,
                           DriverRepository driverRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already registered");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already registered");
        }

        if (request.getRole() == UserRole.DRIVER) {
            if (request.getEmployeeId() == null || request.getEmployeeId().isBlank()) {
                throw new IllegalArgumentException("Employee ID is required for Driver role");
            }
            if (request.getLicenseNumber() == null || request.getLicenseNumber().isBlank()) {
                throw new IllegalArgumentException("License number is required for Driver role");
            }
            if (driverRepository.findByEmployeeId(request.getEmployeeId()).isPresent()) {
                throw new DuplicateResourceException("Employee ID '" + request.getEmployeeId() + "' is already registered");
            }
            if (driverRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
                throw new DuplicateResourceException("License number '" + request.getLicenseNumber() + "' is already registered");
            }
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        if (request.getRole() == UserRole.PASSENGER) {
            Passenger passenger = Passenger.builder()
                    .user(savedUser)
                    .fullName(request.getFullName())
                    .phone(request.getPhone())
                    .build();
            passengerRepository.save(passenger);
        } else if (request.getRole() == UserRole.DRIVER) {
            Driver driver = Driver.builder()
                    .user(savedUser)
                    .employeeId(request.getEmployeeId())
                    .fullName(request.getFullName())
                    .phone(request.getPhone())
                    .licenseNumber(request.getLicenseNumber())
                    .build();
            driverRepository.save(driver);
        }

        String token = tokenProvider.generateTokenFromUsername(savedUser.getUsername(), savedUser.getId(), savedUser.getRole().name());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresInMs(tokenProvider.getExpirationMs())
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .fullName(request.getFullName())
                .role(savedUser.getRole())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String token = tokenProvider.generateToken(authentication);

            String fullName = userPrincipal.getUsername();
            if (userPrincipal.getRole() == UserRole.PASSENGER) {
                fullName = passengerRepository.findByUserId(userPrincipal.getId())
                        .map(Passenger::getFullName).orElse(userPrincipal.getUsername());
            } else if (userPrincipal.getRole() == UserRole.DRIVER) {
                fullName = driverRepository.findByUserId(userPrincipal.getId())
                        .map(Driver::getFullName).orElse(userPrincipal.getUsername());
            }

            return AuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresInMs(tokenProvider.getExpirationMs())
                    .userId(userPrincipal.getId())
                    .username(userPrincipal.getUsername())
                    .email(userPrincipal.getEmail())
                    .fullName(fullName)
                    .role(userPrincipal.getRole())
                    .build();
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid username/email or password");
        }
    }
}

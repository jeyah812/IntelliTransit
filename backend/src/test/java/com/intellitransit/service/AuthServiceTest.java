package com.intellitransit.service;

import com.intellitransit.dto.AuthResponse;
import com.intellitransit.dto.LoginRequest;
import com.intellitransit.dto.RegisterRequest;
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
import com.intellitransit.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    private JwtTokenProvider tokenProvider;
    private AuthServiceImpl authService;

    private RegisterRequest passengerRegisterRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250655368566D5971");
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", 86400000L);

        authService = new AuthServiceImpl(
                userRepository,
                passengerRepository,
                driverRepository,
                passwordEncoder,
                authenticationManager,
                tokenProvider
        );

        passengerRegisterRequest = RegisterRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("password123")
                .fullName("John Doe")
                .phone("1234567890")
                .role(UserRole.PASSENGER)
                .build();

        savedUser = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .password("encoded_password")
                .role(UserRole.PASSENGER)
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("Should successfully register a passenger user")
    void testRegisterPassengerSuccess() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponse response = authService.register(passengerRegisterRequest);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertEquals("john_doe", response.getUsername());
        assertEquals(UserRole.PASSENGER, response.getRole());

        verify(passengerRepository, times(1)).save(any(Passenger.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when username exists")
    void testRegisterDuplicateUsername() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(passengerRegisterRequest));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email exists")
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(passengerRegisterRequest));
    }

    @Test
    @DisplayName("Should successfully login user with valid credentials")
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest("john_doe", "password123");
        UserPrincipal principal = UserPrincipal.create(savedUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(passengerRepository.findByUserId(1L)).thenReturn(Optional.of(Passenger.builder().fullName("John Doe").build()));

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertEquals("john_doe", response.getUsername());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when login fails")
    void testLoginFailure() {
        LoginRequest loginRequest = new LoginRequest("john_doe", "wrongpass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
    }
}

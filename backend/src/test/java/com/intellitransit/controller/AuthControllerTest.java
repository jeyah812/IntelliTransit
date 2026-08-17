package com.intellitransit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellitransit.dto.AuthResponse;
import com.intellitransit.dto.LoginRequest;
import com.intellitransit.dto.RegisterRequest;
import com.intellitransit.entity.enums.UserRole;
import com.intellitransit.exception.DuplicateResourceException;
import com.intellitransit.exception.InvalidCredentialsException;
import com.intellitransit.security.CustomUserDetailsService;
import com.intellitransit.security.JwtTokenProvider;
import com.intellitransit.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("POST /api/auth/register should return 201 Created on valid request")
    void testRegisterEndpointSuccess() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("jane_doe")
                .email("jane@example.com")
                .password("password123")
                .fullName("Jane Doe")
                .role(UserRole.PASSENGER)
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("mock.jwt.token")
                .tokenType("Bearer")
                .userId(1L)
                .username("jane_doe")
                .email("jane@example.com")
                .role(UserRole.PASSENGER)
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("mock.jwt.token"))
                .andExpect(jsonPath("$.username").value("jane_doe"))
                .andExpect(jsonPath("$.role").value("PASSENGER"));
    }

    @Test
    @DisplayName("POST /api/auth/register should return 409 Conflict on duplicate user")
    void testRegisterEndpointDuplicate() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("jane_doe")
                .email("jane@example.com")
                .password("password123")
                .fullName("Jane Doe")
                .role(UserRole.PASSENGER)
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException("Username 'jane_doe' is already registered"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    @DisplayName("POST /api/auth/login should return 200 OK on valid credentials")
    void testLoginEndpointSuccess() throws Exception {
        LoginRequest request = new LoginRequest("jane_doe", "password123");
        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("mock.jwt.token")
                .tokenType("Bearer")
                .userId(1L)
                .username("jane_doe")
                .role(UserRole.PASSENGER)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock.jwt.token"));
    }

    @Test
    @DisplayName("POST /api/auth/login should return 401 Unauthorized on invalid credentials")
    void testLoginEndpointInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("jane_doe", "wrongpassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid username/email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET protected endpoint without token should return 401 Unauthorized")
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/passenger/bookings/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET operations buses endpoint with PASSENGER role should return 403 Forbidden")
    @WithMockUser(username = "passenger_user", roles = {"PASSENGER"})
    void testOperationsEndpointForbiddenForPassenger() throws Exception {
        mockMvc.perform(get("/api/operations/buses"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET operations buses endpoint with OPERATIONS_MANAGER role should return 200 OK")
    @WithMockUser(username = "manager_user", roles = {"OPERATIONS_MANAGER"})
    void testOperationsEndpointAllowedForManager() throws Exception {
        mockMvc.perform(get("/api/operations/buses"))
                .andExpect(status().isOk());
    }
}

package com.intellitransit.controller;

import com.intellitransit.dto.AnalyticsDTO;
import com.intellitransit.dto.AnalyticsPredictionDTO;
import com.intellitransit.security.CustomUserDetailsService;
import com.intellitransit.security.JwtTokenProvider;
import com.intellitransit.service.AnalyticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "OPERATIONS_MANAGER")
    @DisplayName("GET /api/analytics/dashboard should return standard dashboard metrics")
    void testGetDashboardMetrics() throws Exception {
        AnalyticsDTO dto = AnalyticsDTO.builder()
                .totalRoutes(5L)
                .totalStops(20L)
                .totalTrips(15L)
                .completedTrips(8L)
                .scheduledTrips(7L)
                .totalBookings(45L)
                .totalTickets(45L)
                .activeTickets(30L)
                .totalDrivers(10L)
                .totalBuses(12L)
                .build();

        when(analyticsService.getDashboardMetrics()).thenReturn(dto);

        mockMvc.perform(get("/api/analytics/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRoutes").value(5))
                .andExpect(jsonPath("$.completedTrips").value(8));
    }

    @Test
    @WithMockUser(roles = "OPERATIONS_MANAGER")
    @DisplayName("GET /api/analytics/smart-dashboard should return Smart Dashboard metrics in a single DTO")
    void testGetSmartDashboardMetrics() throws Exception {
        AnalyticsPredictionDTO dto = AnalyticsPredictionDTO.builder()
                .totalAlerts(10L)
                .openAlerts(3L)
                .highSeverityAlerts(1L)
                .completedTrips(20L)
                .averageTripDuration(45.5)
                .complaintCount(5L)
                .bookingCount(120L)
                .build();

        when(analyticsService.getSmartDashboardMetrics()).thenReturn(dto);

        mockMvc.perform(get("/api/analytics/smart-dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAlerts").value(10))
                .andExpect(jsonPath("$.openAlerts").value(3))
                .andExpect(jsonPath("$.highSeverityAlerts").value(1))
                .andExpect(jsonPath("$.completedTrips").value(20))
                .andExpect(jsonPath("$.averageTripDuration").value(45.5))
                .andExpect(jsonPath("$.complaintCount").value(5))
                .andExpect(jsonPath("$.bookingCount").value(120));
    }
}

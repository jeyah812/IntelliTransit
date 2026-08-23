package com.intellitransit.controller;

import com.intellitransit.entity.AIAlert;
import com.intellitransit.entity.enums.AlertSeverity;
import com.intellitransit.entity.enums.AlertStatus;
import com.intellitransit.entity.enums.AlertType;
import com.intellitransit.security.CustomUserDetailsService;
import com.intellitransit.security.JwtTokenProvider;
import com.intellitransit.service.AIAlertService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AIAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIAlertService aiAlertService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "OPERATIONS_MANAGER")
    @DisplayName("GET /api/operations/alerts should return all alerts for OPERATIONS_MANAGER")
    void testGetAllAlerts() throws Exception {
        AIAlert alert1 = AIAlert.builder()
                .id(1L)
                .alertType(AlertType.TRIP_DURATION_ANOMALY)
                .severity(AlertSeverity.HIGH)
                .anomalyScore(BigDecimal.valueOf(1.85))
                .explanation("Trip duration exceeded expected duration by more than 50%")
                .recommendation("Investigate delay causes")
                .status(AlertStatus.NEW)
                .build();

        when(aiAlertService.getAllAlerts()).thenReturn(List.of(alert1));

        mockMvc.perform(get("/api/operations/alerts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].alertType").value("TRIP_DURATION_ANOMALY"))
                .andExpect(jsonPath("$[0].severity").value("HIGH"));
    }

    @Test
    @WithMockUser(roles = "OPERATIONS_MANAGER")
    @DisplayName("GET /api/operations/alerts/new should return new alerts")
    void testGetNewAlerts() throws Exception {
        AIAlert alert = AIAlert.builder()
                .id(2L)
                .alertType(AlertType.COMPLAINT_SPIKE)
                .severity(AlertSeverity.MEDIUM)
                .anomalyScore(BigDecimal.valueOf(1.2))
                .explanation("Complaint volume exceeded threshold")
                .recommendation("Review passenger feedback")
                .status(AlertStatus.NEW)
                .build();

        when(aiAlertService.getNewAlerts()).thenReturn(List.of(alert));

        mockMvc.perform(get("/api/operations/alerts/new")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].alertType").value("COMPLAINT_SPIKE"))
                .andExpect(jsonPath("$[0].status").value("NEW"));
    }

    @Test
    @WithMockUser(roles = "OPERATIONS_MANAGER")
    @DisplayName("POST /api/operations/alerts/run should execute anomaly detection and return generated alerts")
    void testRunAnomalyDetection() throws Exception {
        AIAlert alert = AIAlert.builder()
                .id(3L)
                .alertType(AlertType.DEMAND_ANOMALY)
                .severity(AlertSeverity.MEDIUM)
                .anomalyScore(BigDecimal.valueOf(1.05))
                .explanation("Passenger demand unusually high")
                .recommendation("Consider increasing service frequency")
                .status(AlertStatus.NEW)
                .build();

        when(aiAlertService.runAnomalyDetection()).thenReturn(List.of(alert));

        mockMvc.perform(post("/api/operations/alerts/run")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].alertType").value("DEMAND_ANOMALY"));
    }
}

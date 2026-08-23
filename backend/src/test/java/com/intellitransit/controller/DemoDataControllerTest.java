package com.intellitransit.controller;

import com.intellitransit.dto.DemoDataResponse;
import com.intellitransit.security.CustomUserDetailsService;
import com.intellitransit.security.JwtTokenProvider;
import com.intellitransit.service.DemoDataGeneratorService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DemoDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DemoDataGeneratorService demoDataGeneratorService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "OPERATIONS_MANAGER")
    @DisplayName("POST /api/admin/generate-demo-data should generate demo data and return 200 OK")
    void testGenerateDemoData() throws Exception {
        DemoDataResponse response = DemoDataResponse.builder()
                .routesGenerated(50)
                .stopsGenerated(100)
                .tripsGenerated(200)
                .bookingsGenerated(1000)
                .complaintsGenerated(100)
                .alertsGenerated(50)
                .status("COMPLETED")
                .message("Successfully generated demo data.")
                .build();

        when(demoDataGeneratorService.generateDemoData()).thenReturn(response);

        mockMvc.perform(post("/api/admin/generate-demo-data")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routesGenerated").value(50))
                .andExpect(jsonPath("$.stopsGenerated").value(100))
                .andExpect(jsonPath("$.tripsGenerated").value(200))
                .andExpect(jsonPath("$.bookingsGenerated").value(1000))
                .andExpect(jsonPath("$.complaintsGenerated").value(100))
                .andExpect(jsonPath("$.alertsGenerated").value(50))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}

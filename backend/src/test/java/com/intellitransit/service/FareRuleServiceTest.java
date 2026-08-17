package com.intellitransit.service;

import com.intellitransit.dto.FareRuleDTO;
import com.intellitransit.entity.FareRule;
import com.intellitransit.entity.Route;
import com.intellitransit.exception.ResourceNotFoundException;
import com.intellitransit.repository.FareRuleRepository;
import com.intellitransit.repository.RouteRepository;
import com.intellitransit.service.impl.FareRuleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareRuleServiceTest {

    @Mock
    private FareRuleRepository fareRuleRepository;

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private FareRuleServiceImpl fareRuleService;

    private Route route;

    @BeforeEach
    void setUp() {
        route = Route.builder()
                .id(1L)
                .routeNumber("R101")
                .routeName("Chennai Central - Tambaram")
                .origin("Chennai Central")
                .destination("Tambaram")
                .distanceKm(new BigDecimal("25.00"))
                .estimatedDurationMinutes(60)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should successfully create a valid fare rule")
    void testCreateFareRuleSuccess() {
        FareRuleDTO request = FareRuleDTO.builder()
                .routeId(1L)
                .minimumDistanceKm(new BigDecimal("0.00"))
                .maximumDistanceKm(new BigDecimal("25.00"))
                .baseFare(new BigDecimal("30.00"))
                .studentDiscountPercentage(new BigDecimal("20.00"))
                .seniorDiscountPercentage(new BigDecimal("30.00"))
                .effectiveFrom(LocalDate.of(2026, 8, 17))
                .active(true)
                .build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(fareRuleRepository.findByRouteIdAndActive(1L, true)).thenReturn(Collections.emptyList());

        FareRule savedFareRule = FareRule.builder()
                .id(100L)
                .route(route)
                .minimumDistanceKm(new BigDecimal("0.00"))
                .maximumDistanceKm(new BigDecimal("25.00"))
                .baseFare(new BigDecimal("30.00"))
                .studentDiscountPercentage(new BigDecimal("20.00"))
                .seniorDiscountPercentage(new BigDecimal("30.00"))
                .effectiveFrom(LocalDate.of(2026, 8, 17))
                .active(true)
                .build();

        when(fareRuleRepository.save(any(FareRule.class))).thenReturn(savedFareRule);

        FareRuleDTO response = fareRuleService.createFareRule(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getRouteId());
        assertEquals(new BigDecimal("0.00"), response.getMinimumDistanceKm());
        assertEquals(new BigDecimal("25.00"), response.getMaximumDistanceKm());
        assertEquals(new BigDecimal("30.00"), response.getBaseFare());
        assertEquals(new BigDecimal("20.00"), response.getStudentDiscountPercentage());
        assertEquals(new BigDecimal("30.00"), response.getSeniorDiscountPercentage());
        assertTrue(response.isActive());

        verify(fareRuleRepository, times(1)).save(any(FareRule.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when route does not exist")
    void testCreateFareRuleNonexistentRoute() {
        FareRuleDTO request = FareRuleDTO.builder()
                .routeId(999L)
                .minimumDistanceKm(new BigDecimal("0.00"))
                .maximumDistanceKm(new BigDecimal("25.00"))
                .baseFare(new BigDecimal("30.00"))
                .effectiveFrom(LocalDate.of(2026, 8, 17))
                .build();

        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fareRuleService.createFareRule(request));
        verify(fareRuleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when maximum distance is less than minimum distance")
    void testCreateFareRuleInvalidDistanceRange() {
        FareRuleDTO request = FareRuleDTO.builder()
                .routeId(1L)
                .minimumDistanceKm(new BigDecimal("30.00"))
                .maximumDistanceKm(new BigDecimal("10.00"))
                .baseFare(new BigDecimal("30.00"))
                .effectiveFrom(LocalDate.of(2026, 8, 17))
                .build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fareRuleService.createFareRule(request));

        assertTrue(exception.getMessage().contains("Maximum distance must be greater than or equal to minimum distance"));
        verify(fareRuleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when active fare rule overlaps in distance range")
    void testCreateFareRuleOverlappingRange() {
        FareRule existingRule = FareRule.builder()
                .id(50L)
                .route(route)
                .minimumDistanceKm(new BigDecimal("0.00"))
                .maximumDistanceKm(new BigDecimal("20.00"))
                .baseFare(new BigDecimal("20.00"))
                .active(true)
                .build();

        FareRuleDTO request = FareRuleDTO.builder()
                .routeId(1L)
                .minimumDistanceKm(new BigDecimal("15.00"))
                .maximumDistanceKm(new BigDecimal("30.00"))
                .baseFare(new BigDecimal("35.00"))
                .effectiveFrom(LocalDate.of(2026, 8, 17))
                .active(true)
                .build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(fareRuleRepository.findByRouteIdAndActive(1L, true)).thenReturn(List.of(existingRule));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fareRuleService.createFareRule(request));

        assertTrue(exception.getMessage().contains("Overlapping active fare rule exists"));
        verify(fareRuleRepository, never()).save(any());
    }
}

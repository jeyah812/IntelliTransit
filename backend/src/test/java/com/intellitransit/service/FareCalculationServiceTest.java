package com.intellitransit.service;

import com.intellitransit.dto.FareCalculationRequest;
import com.intellitransit.dto.FareCalculationResponse;
import com.intellitransit.entity.FareRule;
import com.intellitransit.entity.Route;
import com.intellitransit.entity.RouteStop;
import com.intellitransit.entity.Stop;
import com.intellitransit.exception.ResourceNotFoundException;
import com.intellitransit.repository.FareRuleRepository;
import com.intellitransit.repository.RouteStopRepository;
import com.intellitransit.service.impl.FareCalculationServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareCalculationServiceTest {

    @Mock
    private RouteStopRepository routeStopRepository;

    @Mock
    private FareRuleRepository fareRuleRepository;

    @InjectMocks
    private FareCalculationServiceImpl fareCalculationService;

    private Route testRoute;
    private Stop originStop;
    private Stop destStop;
    private RouteStop originRouteStop;
    private RouteStop destRouteStop;
    private FareRule fareRule1;

    @BeforeEach
    void setUp() {
        testRoute = Route.builder()
                .id(1L)
                .routeNumber("R101")
                .routeName("Central Loop")
                .build();

        originStop = Stop.builder().id(10L).name("Central Station").build();
        destStop = Stop.builder().id(20L).name("Airport Terminal").build();

        originRouteStop = RouteStop.builder()
                .id(100L)
                .route(testRoute)
                .stop(originStop)
                .stopSequence(1)
                .distanceFromOriginKm(new BigDecimal("0.0"))
                .build();

        destRouteStop = RouteStop.builder()
                .id(101L)
                .route(testRoute)
                .stop(destStop)
                .stopSequence(3)
                .distanceFromOriginKm(new BigDecimal("12.5"))
                .build();

        fareRule1 = FareRule.builder()
                .id(1L)
                .route(testRoute)
                .minimumDistanceKm(new BigDecimal("10.0"))
                .maximumDistanceKm(new BigDecimal("20.0"))
                .baseFare(new BigDecimal("50.00"))
                .studentDiscountPercentage(new BigDecimal("20.00"))
                .seniorDiscountPercentage(new BigDecimal("30.00"))
                .effectiveFrom(LocalDate.now().minusDays(10))
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should correctly calculate standard fare by slab matching and distance")
    void testCalculateFareStandard() {
        when(routeStopRepository.findByRouteIdAndStopId(1L, 10L)).thenReturn(Optional.of(originRouteStop));
        when(routeStopRepository.findByRouteIdAndStopId(1L, 20L)).thenReturn(Optional.of(destRouteStop));
        when(fareRuleRepository.findByRouteIdAndActive(1L, true)).thenReturn(List.of(fareRule1));

        FareCalculationRequest request = new FareCalculationRequest(1L, 10L, 20L, "STANDARD");
        FareCalculationResponse response = fareCalculationService.calculateFare(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("12.5"), response.getDistanceKm());
        assertEquals(new BigDecimal("50.00"), response.getBaseFare());
        assertEquals(new BigDecimal("0"), response.getDiscountPercentage());
        assertEquals(new BigDecimal("50.00"), response.getFinalFare());
    }

    @Test
    @DisplayName("Should apply student discount percentage correctly")
    void testCalculateFareStudentDiscount() {
        when(routeStopRepository.findByRouteIdAndStopId(1L, 10L)).thenReturn(Optional.of(originRouteStop));
        when(routeStopRepository.findByRouteIdAndStopId(1L, 20L)).thenReturn(Optional.of(destRouteStop));
        when(fareRuleRepository.findByRouteIdAndActive(1L, true)).thenReturn(List.of(fareRule1));

        FareCalculationRequest request = new FareCalculationRequest(1L, 10L, 20L, "STUDENT");
        FareCalculationResponse response = fareCalculationService.calculateFare(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("20.00"), response.getDiscountPercentage());
        assertEquals(new BigDecimal("40.00"), response.getFinalFare());
    }

    @Test
    @DisplayName("Should apply senior discount percentage correctly")
    void testCalculateFareSeniorDiscount() {
        when(routeStopRepository.findByRouteIdAndStopId(1L, 10L)).thenReturn(Optional.of(originRouteStop));
        when(routeStopRepository.findByRouteIdAndStopId(1L, 20L)).thenReturn(Optional.of(destRouteStop));
        when(fareRuleRepository.findByRouteIdAndActive(1L, true)).thenReturn(List.of(fareRule1));

        FareCalculationRequest request = new FareCalculationRequest(1L, 10L, 20L, "SENIOR");
        FareCalculationResponse response = fareCalculationService.calculateFare(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("30.00"), response.getDiscountPercentage());
        assertEquals(new BigDecimal("35.00"), response.getFinalFare());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when distance is out of slab range")
    void testCalculateFareInvalidDistanceRange() {
        destRouteStop.setDistanceFromOriginKm(new BigDecimal("25.0")); // 25km > max 20km

        when(routeStopRepository.findByRouteIdAndStopId(1L, 10L)).thenReturn(Optional.of(originRouteStop));
        when(routeStopRepository.findByRouteIdAndStopId(1L, 20L)).thenReturn(Optional.of(destRouteStop));
        when(fareRuleRepository.findByRouteIdAndActive(1L, true)).thenReturn(List.of(fareRule1));

        FareCalculationRequest request = new FareCalculationRequest(1L, 10L, 20L, "STANDARD");

        assertThrows(ResourceNotFoundException.class, () -> fareCalculationService.calculateFare(request));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when destination sequence is before origin sequence")
    void testCalculateFareInvalidSequence() {
        destRouteStop.setStopSequence(1);
        originRouteStop.setStopSequence(2);

        when(routeStopRepository.findByRouteIdAndStopId(1L, 10L)).thenReturn(Optional.of(originRouteStop));
        when(routeStopRepository.findByRouteIdAndStopId(1L, 20L)).thenReturn(Optional.of(destRouteStop));

        FareCalculationRequest request = new FareCalculationRequest(1L, 10L, 20L, "STANDARD");

        assertThrows(IllegalArgumentException.class, () -> fareCalculationService.calculateFare(request));
    }
}

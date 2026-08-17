package com.intellitransit.service;

import com.intellitransit.dto.RouteStopDTO;
import com.intellitransit.entity.Route;
import com.intellitransit.entity.RouteStop;
import com.intellitransit.entity.Stop;
import com.intellitransit.exception.DuplicateResourceException;
import com.intellitransit.exception.ResourceNotFoundException;
import com.intellitransit.repository.RouteRepository;
import com.intellitransit.repository.RouteStopRepository;
import com.intellitransit.repository.StopRepository;
import com.intellitransit.service.impl.RouteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private StopRepository stopRepository;

    @Mock
    private RouteStopRepository routeStopRepository;

    @InjectMocks
    private RouteServiceImpl routeService;

    private Route route;
    private Stop stop;

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

        stop = Stop.builder()
                .id(10L)
                .name("Guindy Junction")
                .latitude(new BigDecimal("13.0067"))
                .longitude(new BigDecimal("80.2020"))
                .build();
    }

    @Test
    @DisplayName("Should successfully attach stop to route")
    void testAddStopToRouteSuccess() {
        RouteStopDTO request = RouteStopDTO.builder()
                .stopId(10L)
                .stopSequence(1)
                .distanceFromOriginKm(new BigDecimal("10.50"))
                .build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(stopRepository.findById(10L)).thenReturn(Optional.of(stop));
        when(routeStopRepository.existsByRouteIdAndStopSequence(1L, 1)).thenReturn(false);
        when(routeStopRepository.existsByRouteIdAndStopId(1L, 10L)).thenReturn(false);

        RouteStop savedRouteStop = RouteStop.builder()
                .id(100L)
                .route(route)
                .stop(stop)
                .stopSequence(1)
                .distanceFromOriginKm(new BigDecimal("10.50"))
                .build();

        when(routeStopRepository.save(any(RouteStop.class))).thenReturn(savedRouteStop);

        RouteStopDTO response = routeService.addStopToRoute(1L, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getRouteId());
        assertEquals(10L, response.getStopId());
        assertEquals("Guindy Junction", response.getStopName());
        assertEquals(1, response.getStopSequence());
        assertEquals(new BigDecimal("10.50"), response.getDistanceFromOriginKm());

        verify(routeStopRepository, times(1)).save(any(RouteStop.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when route does not exist")
    void testAddStopToRouteNonexistentRoute() {
        RouteStopDTO request = RouteStopDTO.builder()
                .stopId(10L)
                .stopSequence(1)
                .distanceFromOriginKm(new BigDecimal("5.00"))
                .build();

        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> routeService.addStopToRoute(999L, request));
        verify(routeStopRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when stop does not exist")
    void testAddStopToRouteNonexistentStop() {
        RouteStopDTO request = RouteStopDTO.builder()
                .stopId(999L)
                .stopSequence(1)
                .distanceFromOriginKm(new BigDecimal("5.00"))
                .build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(stopRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> routeService.addStopToRoute(1L, request));
        verify(routeStopRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when stop sequence already exists for route")
    void testAddStopToRouteDuplicateSequence() {
        RouteStopDTO request = RouteStopDTO.builder()
                .stopId(10L)
                .stopSequence(1)
                .distanceFromOriginKm(new BigDecimal("10.50"))
                .build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(stopRepository.findById(10L)).thenReturn(Optional.of(stop));
        when(routeStopRepository.existsByRouteIdAndStopSequence(1L, 1)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> routeService.addStopToRoute(1L, request));
        verify(routeStopRepository, never()).save(any());
    }
}

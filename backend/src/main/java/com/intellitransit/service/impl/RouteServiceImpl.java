package com.intellitransit.service.impl;

import com.intellitransit.dto.RouteDTO;
import com.intellitransit.dto.RouteStopDTO;
import com.intellitransit.dto.StopDTO;
import com.intellitransit.entity.Route;
import com.intellitransit.entity.RouteStop;
import com.intellitransit.entity.Stop;
import com.intellitransit.exception.DuplicateResourceException;
import com.intellitransit.exception.ResourceNotFoundException;
import com.intellitransit.repository.RouteRepository;
import com.intellitransit.repository.RouteStopRepository;
import com.intellitransit.repository.StopRepository;
import com.intellitransit.service.RouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final RouteStopRepository routeStopRepository;

    public RouteServiceImpl(RouteRepository routeRepository, StopRepository stopRepository, RouteStopRepository routeStopRepository) {
        this.routeRepository = routeRepository;
        this.stopRepository = stopRepository;
        this.routeStopRepository = routeStopRepository;
    }

    @Override
    @Transactional
    public RouteDTO createRoute(RouteDTO routeDTO) {
        if (routeRepository.findByRouteNumber(routeDTO.getRouteNumber()).isPresent()) {
            throw new DuplicateResourceException("Route number '" + routeDTO.getRouteNumber() + "' already exists");
        }

        Route route = Route.builder()
                .routeNumber(routeDTO.getRouteNumber())
                .routeName(routeDTO.getRouteName())
                .origin(routeDTO.getOrigin())
                .destination(routeDTO.getDestination())
                .distanceKm(routeDTO.getDistanceKm())
                .estimatedDurationMinutes(routeDTO.getEstimatedDurationMinutes())
                .active(routeDTO.isActive())
                .build();

        Route saved = routeRepository.save(route);
        return mapToRouteDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RouteDTO getRouteById(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with ID: " + routeId));
        return mapToRouteDTO(route);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteDTO> getAllRoutes() {
        return routeRepository.findAll().stream().map(this::mapToRouteDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StopDTO createStop(StopDTO stopDTO) {
        Stop stop = Stop.builder()
                .name(stopDTO.getName())
                .latitude(stopDTO.getLatitude())
                .longitude(stopDTO.getLongitude())
                .build();
        Stop saved = stopRepository.save(stop);
        return mapToStopDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StopDTO> getAllStops() {
        return stopRepository.findAll().stream().map(this::mapToStopDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RouteStopDTO addStopToRoute(Long routeId, RouteStopDTO routeStopDTO) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with ID: " + routeId));

        Stop stop = stopRepository.findById(routeStopDTO.getStopId())
                .orElseThrow(() -> new ResourceNotFoundException("Stop not found with ID: " + routeStopDTO.getStopId()));

        if (routeStopRepository.existsByRouteIdAndStopSequence(routeId, routeStopDTO.getStopSequence())) {
            throw new DuplicateResourceException("Stop sequence " + routeStopDTO.getStopSequence() + " already exists for route ID: " + routeId);
        }

        if (routeStopRepository.existsByRouteIdAndStopId(routeId, routeStopDTO.getStopId())) {
            throw new DuplicateResourceException("Stop with ID " + routeStopDTO.getStopId() + " is already attached to route ID: " + routeId);
        }

        RouteStop routeStop = RouteStop.builder()
                .route(route)
                .stop(stop)
                .stopSequence(routeStopDTO.getStopSequence())
                .distanceFromOriginKm(routeStopDTO.getDistanceFromOriginKm())
                .build();

        RouteStop saved = routeStopRepository.save(routeStop);

        return RouteStopDTO.builder()
                .id(saved.getId())
                .routeId(saved.getRoute().getId())
                .stopId(saved.getStop().getId())
                .stopName(saved.getStop().getName())
                .stopSequence(saved.getStopSequence())
                .distanceFromOriginKm(saved.getDistanceFromOriginKm())
                .build();
    }

    private RouteDTO mapToRouteDTO(Route route) {
        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequenceAsc(route.getId());
        List<RouteStopDTO> stopDTOs = routeStops.stream().map(rs -> RouteStopDTO.builder()
                .id(rs.getId())
                .routeId(rs.getRoute().getId())
                .stopId(rs.getStop().getId())
                .stopName(rs.getStop().getName())
                .stopSequence(rs.getStopSequence())
                .distanceFromOriginKm(rs.getDistanceFromOriginKm())
                .build()).collect(Collectors.toList());

        return RouteDTO.builder()
                .id(route.getId())
                .routeNumber(route.getRouteNumber())
                .routeName(route.getRouteName())
                .origin(route.getOrigin())
                .destination(route.getDestination())
                .distanceKm(route.getDistanceKm())
                .estimatedDurationMinutes(route.getEstimatedDurationMinutes())
                .active(route.isActive())
                .stops(stopDTOs)
                .build();
    }

    private StopDTO mapToStopDTO(Stop stop) {
        return StopDTO.builder()
                .id(stop.getId())
                .name(stop.getName())
                .latitude(stop.getLatitude())
                .longitude(stop.getLongitude())
                .build();
    }
}

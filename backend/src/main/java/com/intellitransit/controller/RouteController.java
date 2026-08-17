package com.intellitransit.controller;

import com.intellitransit.dto.RouteDTO;
import com.intellitransit.dto.RouteStopDTO;
import com.intellitransit.dto.StopDTO;
import com.intellitransit.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/operations/routes")
    public ResponseEntity<RouteDTO> createRoute(@Valid @RequestBody RouteDTO routeDTO) {
        RouteDTO response = routeService.createRoute(routeDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/routes")
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        List<RouteDTO> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/routes/{id}")
    public ResponseEntity<RouteDTO> getRouteById(@PathVariable Long id) {
        RouteDTO route = routeService.getRouteById(id);
        return ResponseEntity.ok(route);
    }

    @PostMapping("/operations/stops")
    public ResponseEntity<StopDTO> createStop(@Valid @RequestBody StopDTO stopDTO) {
        StopDTO response = routeService.createStop(stopDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/stops")
    public ResponseEntity<List<StopDTO>> getAllStops() {
        List<StopDTO> stops = routeService.getAllStops();
        return ResponseEntity.ok(stops);
    }

    @PostMapping("/operations/routes/{routeId}/stops")
    public ResponseEntity<RouteStopDTO> addStopToRoute(@PathVariable Long routeId,
                                                       @Valid @RequestBody RouteStopDTO routeStopDTO) {
        RouteStopDTO response = routeService.addStopToRoute(routeId, routeStopDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

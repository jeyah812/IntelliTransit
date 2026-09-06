package com.intellitransit.controller;

import com.intellitransit.dto.SimulatedVehicleDTO;
import com.intellitransit.service.TransitSimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operations/simulation")
public class TransitSimulationController {

    private final TransitSimulationService simulationService;

    public TransitSimulationController(TransitSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping("/state")
    public ResponseEntity<List<SimulatedVehicleDTO>> getCurrentSimulationState() {
        List<SimulatedVehicleDTO> vehicles = simulationService.getCurrentSimulationState();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/state/route/{routeId}")
    public ResponseEntity<List<SimulatedVehicleDTO>> getSimulationStateByRoute(@PathVariable Long routeId) {
        List<SimulatedVehicleDTO> vehicles = simulationService.getSimulationStateByRouteId(routeId);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/state/trip/{tripId}")
    public ResponseEntity<SimulatedVehicleDTO> getSimulationStateByTrip(@PathVariable Long tripId) {
        SimulatedVehicleDTO vehicle = simulationService.getSimulationStateByTripId(tripId);
        if (vehicle == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehicle);
    }
}

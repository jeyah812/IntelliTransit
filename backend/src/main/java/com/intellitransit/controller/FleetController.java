package com.intellitransit.controller;

import com.intellitransit.dto.BusDTO;
import com.intellitransit.dto.DriverDTO;
import com.intellitransit.service.FleetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
public class FleetController {

    private final FleetService fleetService;

    public FleetController(FleetService fleetService) {
        this.fleetService = fleetService;
    }

    @PostMapping("/buses")
    public ResponseEntity<BusDTO> createBus(@Valid @RequestBody BusDTO busDTO) {
        BusDTO response = fleetService.createBus(busDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/buses")
    public ResponseEntity<List<BusDTO>> getAllBuses() {
        List<BusDTO> buses = fleetService.getAllBuses();
        return ResponseEntity.ok(buses);
    }

    @GetMapping("/buses/{id}")
    public ResponseEntity<BusDTO> getBusById(@PathVariable Long id) {
        BusDTO bus = fleetService.getBusById(id);
        return ResponseEntity.ok(bus);
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        List<DriverDTO> drivers = fleetService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/drivers/{id}")
    public ResponseEntity<DriverDTO> getDriverById(@PathVariable Long id) {
        DriverDTO driver = fleetService.getDriverById(id);
        return ResponseEntity.ok(driver);
    }
}

package com.intellitransit.service;

import com.intellitransit.dto.SimulatedVehicleDTO;

import java.util.List;

public interface TransitSimulationService {

    /**
     * Retrieves the current simulation state for all active vehicles/trips.
     */
    List<SimulatedVehicleDTO> getCurrentSimulationState();

    /**
     * Retrieves simulation state for vehicles on a specific route.
     */
    List<SimulatedVehicleDTO> getSimulationStateByRouteId(Long routeId);

    /**
     * Retrieves simulation state for a specific trip ID.
     */
    SimulatedVehicleDTO getSimulationStateByTripId(Long tripId);

    /**
     * Advances simulation state by one tick interval.
     */
    void tickSimulation();
}

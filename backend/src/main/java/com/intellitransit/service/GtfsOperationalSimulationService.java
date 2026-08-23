package com.intellitransit.service;

import com.intellitransit.dto.GtfsSimulationRequest;
import com.intellitransit.dto.GtfsSimulationResponse;

public interface GtfsOperationalSimulationService {
    GtfsSimulationResponse simulateOperationalTrips(GtfsSimulationRequest request);
}

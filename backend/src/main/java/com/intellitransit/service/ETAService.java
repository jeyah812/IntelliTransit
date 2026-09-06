package com.intellitransit.service;

import com.intellitransit.dto.EstimatedArrivalDTO;

import java.util.List;

public interface ETAService {

    /**
     * Calculates the estimated arrival time for a simulated trip to reach a downstream destination stop.
     *
     * @param tripId the trip ID
     * @param destinationStopId the target downstream stop ID
     * @return EstimatedArrivalDTO with ETA seconds, estimated time, and confidence
     */
    EstimatedArrivalDTO calculateETA(Long tripId, Long destinationStopId);

    /**
     * Calculates estimated arrival times for all active trips on a route to reach a target destination stop.
     *
     * @param routeId the route ID
     * @param destinationStopId the target downstream stop ID
     * @return List of EstimatedArrivalDTOs
     */
    List<EstimatedArrivalDTO> calculateETAsForRoute(Long routeId, Long destinationStopId);
}

package com.intellitransit.service;

import com.intellitransit.dto.RouteDTO;
import com.intellitransit.dto.RouteStopDTO;
import com.intellitransit.dto.StopDTO;
import java.util.List;

public interface RouteService {
    RouteDTO createRoute(RouteDTO routeDTO);
    RouteDTO getRouteById(Long routeId);
    List<RouteDTO> getAllRoutes();
    StopDTO createStop(StopDTO stopDTO);
    List<StopDTO> getAllStops();
    RouteStopDTO addStopToRoute(Long routeId, RouteStopDTO routeStopDTO);
}

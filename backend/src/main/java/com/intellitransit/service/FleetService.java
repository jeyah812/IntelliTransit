package com.intellitransit.service;

import com.intellitransit.dto.BusDTO;
import com.intellitransit.dto.DriverDTO;
import java.util.List;

public interface FleetService {
    BusDTO createBus(BusDTO busDTO);
    BusDTO getBusById(Long busId);
    List<BusDTO> getAllBuses();
    List<DriverDTO> getAllDrivers();
    DriverDTO getDriverById(Long driverId);
}

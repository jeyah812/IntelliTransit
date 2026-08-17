package com.intellitransit.service.impl;

import com.intellitransit.dto.BusDTO;
import com.intellitransit.dto.DriverDTO;
import com.intellitransit.entity.Bus;
import com.intellitransit.entity.Driver;
import com.intellitransit.exception.DuplicateResourceException;
import com.intellitransit.exception.ResourceNotFoundException;
import com.intellitransit.repository.BusRepository;
import com.intellitransit.repository.DriverRepository;
import com.intellitransit.service.FleetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FleetServiceImpl implements FleetService {

    private final BusRepository busRepository;
    private final DriverRepository driverRepository;

    public FleetServiceImpl(BusRepository busRepository, DriverRepository driverRepository) {
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    @Transactional
    public BusDTO createBus(BusDTO busDTO) {
        if (busRepository.findByRegistrationNumber(busDTO.getRegistrationNumber()).isPresent()) {
            throw new DuplicateResourceException("Bus registration number '" + busDTO.getRegistrationNumber() + "' already exists");
        }

        Bus bus = Bus.builder()
                .registrationNumber(busDTO.getRegistrationNumber())
                .busNumber(busDTO.getBusNumber())
                .model(busDTO.getModel())
                .capacity(busDTO.getCapacity())
                .status(busDTO.getStatus())
                .build();

        Bus saved = busRepository.save(bus);
        return mapToBusDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BusDTO getBusById(Long busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + busId));
        return mapToBusDTO(bus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusDTO> getAllBuses() {
        return busRepository.findAll().stream().map(this::mapToBusDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverDTO> getAllDrivers() {
        return driverRepository.findAll().stream().map(this::mapToDriverDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DriverDTO getDriverById(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
        return mapToDriverDTO(driver);
    }

    private BusDTO mapToBusDTO(Bus bus) {
        return BusDTO.builder()
                .id(bus.getId())
                .registrationNumber(bus.getRegistrationNumber())
                .busNumber(bus.getBusNumber())
                .model(bus.getModel())
                .capacity(bus.getCapacity())
                .status(bus.getStatus())
                .build();
    }

    private DriverDTO mapToDriverDTO(Driver driver) {
        return DriverDTO.builder()
                .id(driver.getId())
                .userId(driver.getUser().getId())
                .employeeId(driver.getEmployeeId())
                .fullName(driver.getFullName())
                .phone(driver.getPhone())
                .licenseNumber(driver.getLicenseNumber())
                .status(driver.getStatus())
                .build();
    }
}

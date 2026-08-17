package com.intellitransit.repository;

import com.intellitransit.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByUserId(Long userId);
    Optional<Driver> findByEmployeeId(String employeeId);
    Optional<Driver> findByLicenseNumber(String licenseNumber);
}

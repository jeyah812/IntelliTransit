package com.intellitransit.repository;

import com.intellitransit.entity.Bus;
import com.intellitransit.entity.enums.BusStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    Optional<Bus> findByRegistrationNumber(String registrationNumber);
    List<Bus> findByStatus(BusStatus status);
}

package com.intellitransit.repository;

import com.intellitransit.entity.SimulatedActionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulatedActionRepository extends JpaRepository<SimulatedActionRecord, Long> {

    List<SimulatedActionRecord> findAllByOrderByExecutedAtDesc();

    List<SimulatedActionRecord> findByTripIdOrderByExecutedAtDesc(Long tripId);
}

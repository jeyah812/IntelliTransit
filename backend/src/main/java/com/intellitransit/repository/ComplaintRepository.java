package com.intellitransit.repository;

import com.intellitransit.entity.Complaint;
import com.intellitransit.entity.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByPassengerId(Long passengerId);
    List<Complaint> findByTripId(Long tripId);
    List<Complaint> findByStatus(ComplaintStatus status);
}

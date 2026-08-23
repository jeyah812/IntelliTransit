package com.intellitransit.repository;

import com.intellitransit.entity.AIAlert;
import com.intellitransit.entity.enums.AlertSeverity;
import com.intellitransit.entity.enums.AlertStatus;
import com.intellitransit.entity.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIAlertRepository extends JpaRepository<AIAlert, Long> {
    List<AIAlert> findByTripId(Long tripId);
    List<AIAlert> findByStatus(AlertStatus status);
    List<AIAlert> findByAlertType(AlertType alertType);
    boolean existsByTripIdAndAlertTypeAndStatus(Long tripId, AlertType alertType, AlertStatus status);
    long countByStatus(AlertStatus status);
    long countBySeverity(AlertSeverity severity);
}

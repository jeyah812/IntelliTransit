package com.intellitransit.repository;

import com.intellitransit.entity.GtfsQuarantineLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GtfsQuarantineLogRepository extends JpaRepository<GtfsQuarantineLog, Long> {
    List<GtfsQuarantineLog> findByBatchId(String batchId);
}

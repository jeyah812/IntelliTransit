package com.intellitransit.repository;

import com.intellitransit.entity.GtfsScheduleStopTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GtfsScheduleStopTimeRepository extends JpaRepository<GtfsScheduleStopTime, Long> {
    List<GtfsScheduleStopTime> findByGtfsTripIdAndBatchIdOrderByStopSequenceAsc(String gtfsTripId, String batchId);
    List<GtfsScheduleStopTime> findByGtfsTripIdOrderByStopSequenceAsc(String gtfsTripId);
}

package com.intellitransit.repository;

import com.intellitransit.entity.GtfsScheduleTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GtfsScheduleTripRepository extends JpaRepository<GtfsScheduleTrip, Long> {
    List<GtfsScheduleTrip> findByBatchId(String batchId);
    List<GtfsScheduleTrip> findByGtfsRouteId(String gtfsRouteId);
}

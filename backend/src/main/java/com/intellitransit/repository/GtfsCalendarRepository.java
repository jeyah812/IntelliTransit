package com.intellitransit.repository;

import com.intellitransit.entity.GtfsCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GtfsCalendarRepository extends JpaRepository<GtfsCalendar, Long> {
    Optional<GtfsCalendar> findByGtfsServiceId(String gtfsServiceId);
    Optional<GtfsCalendar> findByGtfsServiceIdAndBatchId(String gtfsServiceId, String batchId);
    Optional<GtfsCalendar> findByGtfsServiceIdAndFeedIdAndFeedVersion(String gtfsServiceId, String feedId, String feedVersion);
    List<GtfsCalendar> findByBatchId(String batchId);
}

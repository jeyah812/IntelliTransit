package com.intellitransit.repository;

import com.intellitransit.entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {
    Optional<Stop> findByName(String name);
    Optional<Stop> findByGtfsStopId(String gtfsStopId);
    Optional<Stop> findByGtfsStopIdAndFeedVersion(String gtfsStopId, String feedVersion);
    Optional<Stop> findByGtfsStopIdAndFeedIdAndFeedVersion(String gtfsStopId, String feedId, String feedVersion);
}

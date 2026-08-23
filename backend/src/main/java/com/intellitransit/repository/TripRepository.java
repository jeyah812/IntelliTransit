package com.intellitransit.repository;

import com.intellitransit.entity.Trip;
import com.intellitransit.entity.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByRouteId(Long routeId);
    List<Trip> findByDriverId(Long driverId);
    List<Trip> findByBusId(Long busId);
    List<Trip> findByStatus(TripStatus status);
    long countByStatus(TripStatus status);

    boolean existsByRouteFeedIdAndRouteFeedVersionAndGtfsTripIdAndScheduledStart(
            String feedId,
            String feedVersion,
            String gtfsTripId,
            LocalDateTime scheduledStart
    );

    Optional<Trip> findByRouteFeedIdAndRouteFeedVersionAndGtfsTripIdAndScheduledStart(
            String feedId,
            String feedVersion,
            String gtfsTripId,
            LocalDateTime scheduledStart
    );

    long countByGtfsTripIdIsNotNull();
}

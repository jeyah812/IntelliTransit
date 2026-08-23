package com.intellitransit.repository;

import com.intellitransit.entity.TripLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripLogRepository extends JpaRepository<TripLog, Long> {
    List<TripLog> findByTripId(Long tripId);

    @Query("SELECT AVG(tl.durationMinutes) FROM TripLog tl WHERE tl.durationMinutes IS NOT NULL")
    Double findAverageTripDurationMinutes();
}

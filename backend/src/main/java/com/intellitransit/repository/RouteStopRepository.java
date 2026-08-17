package com.intellitransit.repository;

import com.intellitransit.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {
    List<RouteStop> findByRouteIdOrderByStopSequenceAsc(Long routeId);
    List<RouteStop> findByStopId(Long stopId);
    Optional<RouteStop> findByRouteIdAndStopId(Long routeId, Long stopId);
    boolean existsByRouteIdAndStopSequence(Long routeId, Integer stopSequence);
    boolean existsByRouteIdAndStopId(Long routeId, Long stopId);
}

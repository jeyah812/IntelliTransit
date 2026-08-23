package com.intellitransit.repository;

import com.intellitransit.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByRouteNumber(String routeNumber);
    Optional<Route> findByGtfsRouteId(String gtfsRouteId);
    Optional<Route> findByGtfsRouteIdAndFeedVersion(String gtfsRouteId, String feedVersion);
    Optional<Route> findByGtfsRouteIdAndFeedIdAndFeedVersion(String gtfsRouteId, String feedId, String feedVersion);
    List<Route> findByActive(boolean active);
}

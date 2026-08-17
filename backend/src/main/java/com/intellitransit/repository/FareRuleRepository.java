package com.intellitransit.repository;

import com.intellitransit.entity.FareRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FareRuleRepository extends JpaRepository<FareRule, Long> {
    List<FareRule> findByRouteIdAndActive(Long routeId, boolean active);
}

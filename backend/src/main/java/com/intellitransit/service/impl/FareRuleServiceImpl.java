package com.intellitransit.service.impl;

import com.intellitransit.dto.FareRuleDTO;
import com.intellitransit.entity.FareRule;
import com.intellitransit.entity.Route;
import com.intellitransit.exception.ResourceNotFoundException;
import com.intellitransit.repository.FareRuleRepository;
import com.intellitransit.repository.RouteRepository;
import com.intellitransit.service.FareRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FareRuleServiceImpl implements FareRuleService {

    private final FareRuleRepository fareRuleRepository;
    private final RouteRepository routeRepository;

    public FareRuleServiceImpl(FareRuleRepository fareRuleRepository, RouteRepository routeRepository) {
        this.fareRuleRepository = fareRuleRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    @Transactional
    public FareRuleDTO createFareRule(FareRuleDTO fareRuleDTO) {
        Route route = routeRepository.findById(fareRuleDTO.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with ID: " + fareRuleDTO.getRouteId()));

        if (fareRuleDTO.getMaximumDistanceKm().compareTo(fareRuleDTO.getMinimumDistanceKm()) < 0) {
            throw new IllegalArgumentException("Maximum distance must be greater than or equal to minimum distance");
        }

        if (fareRuleDTO.isActive()) {
            List<FareRule> existingActiveRules = fareRuleRepository.findByRouteIdAndActive(fareRuleDTO.getRouteId(), true);
            boolean isOverlapping = existingActiveRules.stream().anyMatch(rule ->
                    fareRuleDTO.getMinimumDistanceKm().compareTo(rule.getMaximumDistanceKm()) <= 0 &&
                    fareRuleDTO.getMaximumDistanceKm().compareTo(rule.getMinimumDistanceKm()) >= 0
            );

            if (isOverlapping) {
                throw new IllegalArgumentException("Overlapping active fare rule exists for route ID " + fareRuleDTO.getRouteId() + " in range " + fareRuleDTO.getMinimumDistanceKm() + " - " + fareRuleDTO.getMaximumDistanceKm() + " km");
            }
        }

        FareRule fareRule = FareRule.builder()
                .route(route)
                .minimumDistanceKm(fareRuleDTO.getMinimumDistanceKm())
                .maximumDistanceKm(fareRuleDTO.getMaximumDistanceKm())
                .baseFare(fareRuleDTO.getBaseFare())
                .studentDiscountPercentage(fareRuleDTO.getStudentDiscountPercentage())
                .seniorDiscountPercentage(fareRuleDTO.getSeniorDiscountPercentage())
                .effectiveFrom(fareRuleDTO.getEffectiveFrom())
                .effectiveTo(fareRuleDTO.getEffectiveTo())
                .active(fareRuleDTO.isActive())
                .build();

        FareRule saved = fareRuleRepository.save(fareRule);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FareRuleDTO> getFareRulesByRouteId(Long routeId) {
        return fareRuleRepository.findByRouteIdAndActive(routeId, true)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private FareRuleDTO mapToDTO(FareRule fareRule) {
        return FareRuleDTO.builder()
                .id(fareRule.getId())
                .routeId(fareRule.getRoute().getId())
                .minimumDistanceKm(fareRule.getMinimumDistanceKm())
                .maximumDistanceKm(fareRule.getMaximumDistanceKm())
                .baseFare(fareRule.getBaseFare())
                .studentDiscountPercentage(fareRule.getStudentDiscountPercentage())
                .seniorDiscountPercentage(fareRule.getSeniorDiscountPercentage())
                .effectiveFrom(fareRule.getEffectiveFrom())
                .effectiveTo(fareRule.getEffectiveTo())
                .active(fareRule.isActive())
                .build();
    }
}

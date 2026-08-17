package com.intellitransit.service.impl;

import com.intellitransit.dto.FareCalculationRequest;
import com.intellitransit.dto.FareCalculationResponse;
import com.intellitransit.entity.FareRule;
import com.intellitransit.entity.RouteStop;
import com.intellitransit.exception.ResourceNotFoundException;
import com.intellitransit.repository.FareRuleRepository;
import com.intellitransit.repository.RouteStopRepository;
import com.intellitransit.service.FareCalculationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class FareCalculationServiceImpl implements FareCalculationService {

    private final RouteStopRepository routeStopRepository;
    private final FareRuleRepository fareRuleRepository;

    public FareCalculationServiceImpl(RouteStopRepository routeStopRepository, FareRuleRepository fareRuleRepository) {
        this.routeStopRepository = routeStopRepository;
        this.fareRuleRepository = fareRuleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public FareCalculationResponse calculateFare(FareCalculationRequest request) {
        RouteStop originStop = routeStopRepository.findByRouteIdAndStopId(request.getRouteId(), request.getOriginStopId())
                .orElseThrow(() -> new ResourceNotFoundException("Origin stop not found on route " + request.getRouteId()));

        RouteStop destStop = routeStopRepository.findByRouteIdAndStopId(request.getRouteId(), request.getDestinationStopId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination stop not found on route " + request.getRouteId()));

        if (destStop.getStopSequence() <= originStop.getStopSequence()) {
            throw new IllegalArgumentException("Destination stop must be after origin stop in route sequence");
        }

        BigDecimal distanceKm = destStop.getDistanceFromOriginKm().subtract(originStop.getDistanceFromOriginKm()).abs();

        List<FareRule> fareRules = fareRuleRepository.findByRouteIdAndActive(request.getRouteId(), true);

        FareRule matchingRule = fareRules.stream()
                .filter(rule -> distanceKm.compareTo(rule.getMinimumDistanceKm()) >= 0 &&
                                distanceKm.compareTo(rule.getMaximumDistanceKm()) <= 0)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No active fare rule found for distance: " + distanceKm + " km on route " + request.getRouteId()));

        BigDecimal baseFare = matchingRule.getBaseFare();
        BigDecimal discountPercentage = BigDecimal.ZERO;

        String category = request.getPassengerCategory() != null ? request.getPassengerCategory().toUpperCase() : "STANDARD";
        if ("STUDENT".equals(category) && matchingRule.getStudentDiscountPercentage() != null) {
            discountPercentage = matchingRule.getStudentDiscountPercentage();
        } else if ("SENIOR".equals(category) && matchingRule.getSeniorDiscountPercentage() != null) {
            discountPercentage = matchingRule.getSeniorDiscountPercentage();
        }

        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        BigDecimal finalFare = baseFare.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);

        return FareCalculationResponse.builder()
                .routeId(request.getRouteId())
                .originStopName(originStop.getStop().getName())
                .destinationStopName(destStop.getStop().getName())
                .distanceKm(distanceKm)
                .baseFare(baseFare)
                .discountPercentage(discountPercentage)
                .finalFare(finalFare)
                .passengerCategory(category)
                .build();
    }
}

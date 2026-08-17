package com.intellitransit.service;

import com.intellitransit.dto.FareCalculationRequest;
import com.intellitransit.dto.FareCalculationResponse;

public interface FareCalculationService {
    FareCalculationResponse calculateFare(FareCalculationRequest request);
}

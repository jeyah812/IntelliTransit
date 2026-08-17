package com.intellitransit.service;

import com.intellitransit.dto.FareRuleDTO;
import java.util.List;

public interface FareRuleService {
    FareRuleDTO createFareRule(FareRuleDTO fareRuleDTO);
    List<FareRuleDTO> getFareRulesByRouteId(Long routeId);
}

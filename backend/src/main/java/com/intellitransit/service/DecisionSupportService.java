package com.intellitransit.service;

import com.intellitransit.dto.DecisionRecommendationDTO;
import com.intellitransit.dto.SimulateDispatchRequest;
import com.intellitransit.dto.SimulateDispatchResponse;
import com.intellitransit.entity.SimulatedActionRecord;

import java.util.List;

public interface DecisionSupportService {

    /**
     * Retrieves all active explainable decision recommendations for network anomalies.
     */
    List<DecisionRecommendationDTO> getRecommendations();

    /**
     * Retrieves decision recommendation for a specific alert ID.
     */
    DecisionRecommendationDTO getRecommendationForAlert(Long alertId);

    /**
     * Executes a simulated operational dispatch action, mutating operational state and updating ETA.
     */
    SimulateDispatchResponse simulateDispatch(SimulateDispatchRequest request);

    /**
     * Retrieves audit trail history of recent simulated operational actions.
     */
    List<SimulatedActionRecord> getActionHistory();
}

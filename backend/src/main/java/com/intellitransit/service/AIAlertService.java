package com.intellitransit.service;

import com.intellitransit.dto.AIAlertDTO;

import java.util.List;

public interface AIAlertService {
    List<AIAlertDTO> runAnomalyDetection();
    List<AIAlertDTO> getAllAlerts();
    List<AIAlertDTO> getNewAlerts();
}

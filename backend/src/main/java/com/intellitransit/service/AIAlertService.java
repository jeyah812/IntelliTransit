package com.intellitransit.service;

import com.intellitransit.entity.AIAlert;

import java.util.List;

public interface AIAlertService {
    List<AIAlert> runAnomalyDetection();
    List<AIAlert> getAllAlerts();
    List<AIAlert> getNewAlerts();
}

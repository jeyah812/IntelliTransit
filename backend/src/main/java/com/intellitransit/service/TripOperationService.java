package com.intellitransit.service;

import com.intellitransit.dto.*;
import java.util.List;

public interface TripOperationService {
    TripDTO scheduleTrip(TripScheduleRequest request);
    TripDTO startTrip(Long driverUserId, Long tripId);
    TripDTO endTrip(Long driverUserId, Long tripId, String notes);
    TicketVerificationResponse verifyTicket(Long driverUserId, TicketVerificationRequest request);
    List<TripDTO> getDriverAssignedTrips(Long driverUserId);
    List<TripDTO> getAllTrips();
    List<TripLogDTO> getAllTripLogs();
}

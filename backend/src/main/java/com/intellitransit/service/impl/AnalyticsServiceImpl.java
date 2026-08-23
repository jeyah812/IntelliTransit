package com.intellitransit.service.impl;

import com.intellitransit.dto.AnalyticsDTO;
import com.intellitransit.dto.AnalyticsPredictionDTO;
import com.intellitransit.entity.enums.AlertSeverity;
import com.intellitransit.entity.enums.AlertStatus;
import com.intellitransit.entity.enums.TicketStatus;
import com.intellitransit.entity.enums.TripStatus;
import com.intellitransit.repository.*;
import com.intellitransit.service.AnalyticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final DriverRepository driverRepository;
    private final BusRepository busRepository;
    private final AIAlertRepository aiAlertRepository;
    private final ComplaintRepository complaintRepository;
    private final TripLogRepository tripLogRepository;

    public AnalyticsServiceImpl(RouteRepository routeRepository,
                                StopRepository stopRepository,
                                TripRepository tripRepository,
                                BookingRepository bookingRepository,
                                TicketRepository ticketRepository,
                                DriverRepository driverRepository,
                                BusRepository busRepository,
                                AIAlertRepository aiAlertRepository,
                                ComplaintRepository complaintRepository,
                                TripLogRepository tripLogRepository) {
        this.routeRepository = routeRepository;
        this.stopRepository = stopRepository;
        this.tripRepository = tripRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.driverRepository = driverRepository;
        this.busRepository = busRepository;
        this.aiAlertRepository = aiAlertRepository;
        this.complaintRepository = complaintRepository;
        this.tripLogRepository = tripLogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDTO getDashboardMetrics() {
        long totalRoutes = routeRepository.count();
        long totalStops = stopRepository.count();
        long totalTrips = tripRepository.count();
        long completedTrips = tripRepository.countByStatus(TripStatus.COMPLETED);
        long scheduledTrips = tripRepository.countByStatus(TripStatus.SCHEDULED);
        long totalBookings = bookingRepository.count();
        long totalTickets = ticketRepository.count();
        long activeTickets = ticketRepository.countByStatus(TicketStatus.ACTIVE);
        long totalDrivers = driverRepository.count();
        long totalBuses = busRepository.count();

        return new AnalyticsDTO(
                totalRoutes,
                totalStops,
                totalTrips,
                completedTrips,
                scheduledTrips,
                totalBookings,
                totalTickets,
                activeTickets,
                totalDrivers,
                totalBuses
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsPredictionDTO getSmartDashboardMetrics() {
        long totalAlerts = aiAlertRepository.count();
        long openAlerts = aiAlertRepository.countByStatus(AlertStatus.NEW);
        long highSeverityAlerts = aiAlertRepository.countBySeverity(AlertSeverity.HIGH);
        long completedTrips = tripRepository.countByStatus(TripStatus.COMPLETED);
        
        Double rawAvgDuration = tripLogRepository.findAverageTripDurationMinutes();
        double averageTripDuration = (rawAvgDuration != null) ? Math.round(rawAvgDuration * 100.0) / 100.0 : 0.0;
        
        long complaintCount = complaintRepository.count();
        long bookingCount = bookingRepository.count();

        return AnalyticsPredictionDTO.builder()
                .totalAlerts(totalAlerts)
                .openAlerts(openAlerts)
                .highSeverityAlerts(highSeverityAlerts)
                .completedTrips(completedTrips)
                .averageTripDuration(averageTripDuration)
                .complaintCount(complaintCount)
                .bookingCount(bookingCount)
                .build();
    }
}

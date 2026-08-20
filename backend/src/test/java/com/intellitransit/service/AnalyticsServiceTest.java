package com.intellitransit.service;

import com.intellitransit.dto.AnalyticsDTO;
import com.intellitransit.entity.enums.TicketStatus;
import com.intellitransit.entity.enums.TripStatus;
import com.intellitransit.repository.*;
import com.intellitransit.service.impl.AnalyticsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private StopRepository stopRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    @Test
    @DisplayName("Should return aggregated dashboard metrics correctly from repository counts")
    void testGetDashboardMetricsSuccess() {
        when(routeRepository.count()).thenReturn(5L);
        when(stopRepository.count()).thenReturn(20L);
        when(tripRepository.count()).thenReturn(15L);
        when(tripRepository.countByStatus(TripStatus.COMPLETED)).thenReturn(8L);
        when(tripRepository.countByStatus(TripStatus.SCHEDULED)).thenReturn(7L);
        when(bookingRepository.count()).thenReturn(45L);
        when(ticketRepository.count()).thenReturn(45L);
        when(ticketRepository.countByStatus(TicketStatus.ACTIVE)).thenReturn(30L);
        when(driverRepository.count()).thenReturn(10L);
        when(busRepository.count()).thenReturn(12L);

        AnalyticsDTO metrics = analyticsService.getDashboardMetrics();

        assertNotNull(metrics);
        assertEquals(5L, metrics.getTotalRoutes());
        assertEquals(20L, metrics.getTotalStops());
        assertEquals(15L, metrics.getTotalTrips());
        assertEquals(8L, metrics.getCompletedTrips());
        assertEquals(7L, metrics.getScheduledTrips());
        assertEquals(45L, metrics.getTotalBookings());
        assertEquals(45L, metrics.getTotalTickets());
        assertEquals(30L, metrics.getActiveTickets());
        assertEquals(10L, metrics.getTotalDrivers());
        assertEquals(12L, metrics.getTotalBuses());
    }
}

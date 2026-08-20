package com.intellitransit.repository;

import com.intellitransit.entity.Ticket;
import com.intellitransit.entity.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    Optional<Ticket> findByBookingId(Long bookingId);
    Optional<Ticket> findByQrData(String qrData);
    long countByStatus(TicketStatus status);
}

package com.intellitransit.service.impl;

import com.intellitransit.dto.ComplaintDTO;
import com.intellitransit.dto.ComplaintRequest;
import com.intellitransit.entity.Complaint;
import com.intellitransit.entity.Passenger;
import com.intellitransit.entity.Trip;
import com.intellitransit.entity.enums.ComplaintStatus;
import com.intellitransit.exception.ResourceNotFoundException;
import com.intellitransit.repository.ComplaintRepository;
import com.intellitransit.repository.PassengerRepository;
import com.intellitransit.repository.TripRepository;
import com.intellitransit.service.ComplaintService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final PassengerRepository passengerRepository;
    private final TripRepository tripRepository;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository, PassengerRepository passengerRepository, TripRepository tripRepository) {
        this.complaintRepository = complaintRepository;
        this.passengerRepository = passengerRepository;
        this.tripRepository = tripRepository;
    }

    @Override
    @Transactional
    public ComplaintDTO fileComplaint(Long userId, ComplaintRequest request) {
        Passenger passenger = passengerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger profile not found for user ID: " + userId));

        Trip trip = null;
        if (request.getTripId() != null) {
            trip = tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + request.getTripId()));
        }

        Complaint complaint = Complaint.builder()
                .passenger(passenger)
                .trip(trip)
                .subject(request.getSubject())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(ComplaintStatus.OPEN)
                .build();

        Complaint saved = complaintRepository.save(complaint);
        return mapToComplaintDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDTO> getPassengerComplaints(Long userId) {
        Passenger passenger = passengerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger profile not found for user ID: " + userId));

        return complaintRepository.findByPassengerId(passenger.getId()).stream()
                .map(this::mapToComplaintDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDTO> getAllComplaints() {
        return complaintRepository.findAll().stream()
                .map(this::mapToComplaintDTO)
                .collect(Collectors.toList());
    }

    private ComplaintDTO mapToComplaintDTO(Complaint complaint) {
        return ComplaintDTO.builder()
                .id(complaint.getId())
                .passengerId(complaint.getPassenger().getId())
                .passengerName(complaint.getPassenger().getFullName())
                .tripId(complaint.getTrip() != null ? complaint.getTrip().getId() : null)
                .subject(complaint.getSubject())
                .description(complaint.getDescription())
                .category(complaint.getCategory())
                .status(complaint.getStatus())
                .createdAt(complaint.getCreatedAt())
                .resolvedAt(complaint.getResolvedAt())
                .build();
    }
}

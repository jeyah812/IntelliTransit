package com.intellitransit.service;

import com.intellitransit.dto.ComplaintDTO;
import com.intellitransit.dto.ComplaintRequest;
import java.util.List;

public interface ComplaintService {
    ComplaintDTO fileComplaint(Long userId, ComplaintRequest request);
    List<ComplaintDTO> getPassengerComplaints(Long userId);
    List<ComplaintDTO> getAllComplaints();
}

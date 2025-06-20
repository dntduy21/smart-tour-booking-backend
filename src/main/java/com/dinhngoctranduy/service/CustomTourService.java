package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.dto.CustomTourResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomTourService {
    CustomTourResponse createCustomTour(com.dinhngoctranduy.model.dto.CustomTourRequest request);

    List<CustomTourResponse> getAllCustomTours(Pageable pageable);

    CustomTourResponse getCustomTourById(Long id, Boolean isDeleted);

    void sendReplyToCustomer(Long tourId, String message);

    void softDeleteCustomTour(Long id);

    CustomTourResponse updateCustomTourStatus(Long id, boolean newStatus);
}

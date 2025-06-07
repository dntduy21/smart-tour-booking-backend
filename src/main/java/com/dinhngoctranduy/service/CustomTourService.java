package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.dto.CustomTourResponse;

import java.util.List;

public interface CustomTourService {
    CustomTourResponse createCustomTour(com.dinhngoctranduy.model.dto.CustomTourRequest request);

    List<CustomTourResponse> getAllCustomTours();

    CustomTourResponse getCustomTourById(Long id, Boolean isDeleted);

    void sendReplyToCustomer(Long tourId, String message);
}

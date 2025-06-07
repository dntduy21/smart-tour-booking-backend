package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.CustomTour;
import com.dinhngoctranduy.model.dto.CustomTourResponse;

import java.util.List;
import java.util.stream.Collectors;

public interface CustomTourService {
    CustomTourResponse createCustomTour(com.dinhngoctranduy.model.dto.CustomTourRequest request);

    List<CustomTourResponse> getAllCustomTours();

    CustomTourResponse getCustomTourById(Long id, Boolean isDeleted);

    void sendReplyToCustomer(Long tourId, String message);
}

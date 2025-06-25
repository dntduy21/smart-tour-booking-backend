package com.dinhngoctranduy.service;


import com.dinhngoctranduy.model.Tour;
import com.dinhngoctranduy.model.request.TourRequestDTO;
import com.dinhngoctranduy.model.response.TourResponseDTO;
import com.dinhngoctranduy.model.request.TourSearchRequest;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface TourService {
    TourResponseDTO createTour(TourRequestDTO dto);

    TourResponseDTO updateTour(Long id, TourRequestDTO dto);

    void deleteTour(Long id);

    TourResponseDTO getById(Long id, Boolean isDeleted);

    List<TourResponseDTO> getAll(Boolean isDeleted);

    Page<TourResponseDTO> getAllTours(int page, int size, Boolean isDeleted);

    byte[] exportToursToPdf(int page, int size);

    ByteArrayInputStream exportToursToExcel(int page, int size) throws IOException;

    List<TourResponseDTO> searchTours(TourSearchRequest request, Boolean isDeleted);

    Tour updateAvailability(Long tourId, boolean available);
}

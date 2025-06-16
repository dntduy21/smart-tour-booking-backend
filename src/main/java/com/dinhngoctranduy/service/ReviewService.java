package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.dto.ReviewDTO;
import com.dinhngoctranduy.model.dto.TourReviewsResponseDTO;

import java.util.List;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO dto);

    ReviewDTO updateReview(Long id, ReviewDTO dto);

    void deleteReview(Long id);

    TourReviewsResponseDTO getReviewsByTourId(Long tourId);

    List<ReviewDTO> getReviewsByUserId(Long userId);
}

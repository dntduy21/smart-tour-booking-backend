package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO dto);
    ReviewDTO updateReview(Long id, ReviewDTO dto);
    void deleteReview(Long id);
    List<ReviewDTO> getReviewsByTourId(Long tourId);
    List<ReviewDTO> getReviewsByUserId(Long userId);
}

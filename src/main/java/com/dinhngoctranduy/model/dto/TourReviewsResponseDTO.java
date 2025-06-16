package com.dinhngoctranduy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourReviewsResponseDTO {
    private double averageRating;
    private int totalReviews;
    private List<ReviewDTO> reviews;
}
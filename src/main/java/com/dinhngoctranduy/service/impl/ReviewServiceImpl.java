package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.exceptions.InvalidDataException;
import com.dinhngoctranduy.model.Booking;
import com.dinhngoctranduy.model.Review;
import com.dinhngoctranduy.model.Tour;
import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.dto.ReviewDTO;
import com.dinhngoctranduy.repository.ReviewRepository;
import com.dinhngoctranduy.repository.TourRepository;
import com.dinhngoctranduy.repository.UserRepository;
import com.dinhngoctranduy.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final TourRepository tourRepo;

    @Override
    public ReviewDTO createReview(ReviewDTO dto) {
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setReviewedAt(Instant.now());

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new InvalidDataException("Rating must be between 1 and 5");
        }

        // Validate comment length
        if (review.getComment() != null && review.getComment().length() > 2000) {
            throw new InvalidDataException("Comment length must be less than 2000 characters");
        }

        // Validate tour exists
        Tour tour = tourRepo.findById(dto.getTourId())
                .orElseThrow(() -> new InvalidDataException("Tour not found"));

        // Validate user exists
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new InvalidDataException("User not found"));

        boolean isCompleted = user.getBookings().stream()
                        .map(Booking::getTour)
                                .anyMatch(t -> t.getId().equals(tour.getId()));

        if(!isCompleted) {
            throw new InvalidDataException("User has not completed the tour");
        }
        review.setTour(tour);
        review.setUser(user);

        review = reviewRepo.save(review);

        dto.setId(review.getId());
        return dto;
    }

    @Override
    public ReviewDTO updateReview(Long id, ReviewDTO dto) {
        Review review = reviewRepo.findById(id).orElseThrow();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setReviewedAt(Instant.now());

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new InvalidDataException("Rating must be between 1 and 5");
        }

        // Validate comment length
        if (review.getComment() != null && review.getComment().length() > 2000) {
            throw new InvalidDataException("Comment length must be less than 2000 characters");
        }

        return toDto(reviewRepo.save(review));
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepo.deleteById(id);
    }

    @Override
    public List<ReviewDTO> getReviewsByTourId(Long tourId) {
        return reviewRepo.findByTourId(tourId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO> getReviewsByUserId(Long userId) {
        return reviewRepo.findByUserId(userId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    private ReviewDTO toDto(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .tourId(review.getTour().getId())
                .userId(review.getUser().getId())
                .build();
    }
}

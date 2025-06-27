package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.model.dto.ReviewTourDTO;
import com.dinhngoctranduy.util.error.InvalidDataException;
import com.dinhngoctranduy.model.Booking;
import com.dinhngoctranduy.model.Review;
import com.dinhngoctranduy.model.Tour;
import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.dto.ReviewDTO;
import com.dinhngoctranduy.model.response.TourReviewsResponseDTO;
import com.dinhngoctranduy.repository.ReviewRepository;
import com.dinhngoctranduy.repository.TourRepository;
import com.dinhngoctranduy.repository.UserRepository;
import com.dinhngoctranduy.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
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

        if (!isCompleted) {
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
    public TourReviewsResponseDTO getReviewsByTourId(Long tourId) {
        // 1. Lấy danh sách các review chi tiết
        List<Review> reviews = reviewRepo.findByTourId(tourId);
        List<ReviewTourDTO> reviewDTOs = reviews.stream()
                .map(this::toReviewTourDTO)
                .collect(Collectors.toList());

        // 2. Lấy điểm rating trung bình từ repository
        double avgRating = reviewRepo.findAverageRatingByTourId(tourId);

        // 3. Xây dựng và trả về đối tượng response mới
        return TourReviewsResponseDTO.builder()
                .averageRating(avgRating)
                .totalReviews(reviews.size())
                .reviews(reviewDTOs)
                .build();
    }


    @Override
    public List<ReviewDTO> getReviewsByUserId(Long userId, Pageable pageable) {
        return reviewRepo.findByUserId(userId, pageable).getContent()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
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

    private ReviewTourDTO toReviewTourDTO(Review review) {
        return ReviewTourDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .tourId(review.getTour().getId())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .fullName(review.getUser().getFullName())
                .build();
    }
}

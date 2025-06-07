package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTourId(Long tourId);
    List<Review> findByUserId(Long userId);
}

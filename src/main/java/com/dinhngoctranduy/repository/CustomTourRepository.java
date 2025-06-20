package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.CustomTour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomTourRepository extends JpaRepository<CustomTour, Long> {
    List<CustomTour> findByDeletedFalseOrderByStartDateDesc();

    Optional<CustomTour> findByIdAndDeletedFalse(Long id);

    Page<CustomTour> findByDeletedFalse(Pageable pageable);
}

package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.TourCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourCategoryRepository extends JpaRepository<TourCategory, Long> {
    List<TourCategory> findByNameContainingIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<TourCategory> findByActiveTrue();

    List<TourCategory> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}

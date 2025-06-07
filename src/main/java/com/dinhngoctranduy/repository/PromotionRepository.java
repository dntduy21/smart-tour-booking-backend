package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByIdAndActiveTrue(Long id);

    Optional<Promotion> findByCodeAndActiveTrue(String code);

    boolean existsByCode(String code);

    List<Promotion> findByDescriptionContainingIgnoreCase(String keyword);

}

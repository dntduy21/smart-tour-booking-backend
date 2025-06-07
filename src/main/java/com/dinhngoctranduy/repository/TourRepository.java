package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {
    boolean existsByCode(String code);
    List<Tour> findAllByDeletedFalse();

    // Phân trang các tour chưa bị xóa
    Page<Tour> findAllByDeletedFalse(Pageable pageable);

    // Tìm tour theo ID nếu chưa bị xóa
    Optional<Tour> findByIdAndDeletedFalse(Long id);
}

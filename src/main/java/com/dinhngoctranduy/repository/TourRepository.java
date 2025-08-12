package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    //  Lấy các tour sắp khởi hành (startDate trong tương lai)
    @Query("SELECT t FROM Tour t WHERE t.deleted = false AND t.startDate > :currentTime")
    Page<Tour> findUpcomingTours(@Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    //  Lấy các tour đang diễn ra (currentTime nằm giữa startDate và endDate)
    @Query("SELECT t FROM Tour t WHERE t.deleted = false AND t.startDate <= :currentTime AND t.endDate >= :currentTime")
    Page<Tour> findOngoingTours(@Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    //  Lấy các tour đã kết thúc (endDate trong quá khứ)
    @Query("SELECT t FROM Tour t WHERE t.deleted = false AND t.endDate < :currentTime")
    Page<Tour> findFinishedTours(@Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    Page<Tour> findByCategoryIdAndDeletedFalse(Long categoryId, Pageable pageable);

    @Query(value = """
            SELECT * FROM tours 
            ORDER BY 
                CASE WHEN end_date >= :now THEN 0 ELSE 1 END,
                start_date ASC
            """,
            countQuery = "SELECT count(*) FROM tours",
            nativeQuery = true)
    Page<Tour> findAllOrderByUpcomingFirst(@Param("now") LocalDateTime now, Pageable pageable);

    @Query(value = """
            SELECT * FROM tours t
            WHERE t.deleted = false
            ORDER BY 
                CASE 
                    WHEN t.start_date > :now THEN 1
                    WHEN t.start_date <= :now AND t.end_date >= :now THEN 2
                    ELSE 3
                END,
                t.start_date ASC
            """,
            countQuery = "SELECT COUNT(*) FROM tours t WHERE t.deleted = false",
            nativeQuery = true)
    Page<Tour> findAllByDeletedFalseOrderByUpcoming(@Param("now") LocalDateTime now, Pageable pageable);
}

package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.Booking;
import com.dinhngoctranduy.model.dto.statistic.RevenueByDate;
import com.dinhngoctranduy.model.dto.statistic.RevenueByMonth;
import com.dinhngoctranduy.model.dto.statistic.RevenueByYear;
import com.dinhngoctranduy.model.dto.statistic.TourStat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    List<Booking> findByUserId(Long userId);


    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status <> 'CANCELLED'")
    Long countActiveBookings();

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.status IN ('CONFIRMED', 'COMPLETED')")
    Double sumTotalRevenue();

    @Query("SELECT COUNT(t) FROM Tour t WHERE t.available = true AND t.deleted = false")
    Long countActiveTours();

    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false")
    Long countActiveUsers();

    // Doanh thu theo ngày
    @Query("SELECT new com.dinhngoctranduy.model.dto.statistic.RevenueByDate(" +
            "FUNCTION('DATE', b.bookingDate), SUM(b.totalPrice)) " +
            "FROM Booking b WHERE b.status IN ('CONFIRMED', 'COMPLETED') " +
            "GROUP BY FUNCTION('DATE', b.bookingDate) " +
            "ORDER BY FUNCTION('DATE', b.bookingDate)")
    List<RevenueByDate> revenueByDate();

    // Doanh thu theo tháng
    @Query("SELECT new com.dinhngoctranduy.model.dto.statistic.RevenueByMonth(" +
            "YEAR(b.bookingDate), MONTH(b.bookingDate), SUM(b.totalPrice)) " +
            "FROM Booking b WHERE b.status IN ('CONFIRMED', 'COMPLETED') " +
            "GROUP BY YEAR(b.bookingDate), MONTH(b.bookingDate) " +
            "ORDER BY YEAR(b.bookingDate), MONTH(b.bookingDate)")
    List<RevenueByMonth> revenueByMonth();

    // Doanh thu theo năm
    @Query("SELECT new com.dinhngoctranduy.model.dto.statistic.RevenueByYear(" +
            "YEAR(b.bookingDate), SUM(b.totalPrice)) " +
            "FROM Booking b WHERE b.status IN ('CONFIRMED', 'COMPLETED') " +
            "GROUP BY YEAR(b.bookingDate) " +
            "ORDER BY YEAR(b.bookingDate)")
    List<RevenueByYear> revenueByYear();

    // Top tour theo lượt đặt
    @Query("SELECT new com.dinhngoctranduy.model.dto.statistic.TourStat(" +
            "b.tour.id, b.tour.title, COUNT(b)) " +
            "FROM Booking b WHERE b.status = 'CONFIRMED' OR b.status = 'COMPLETED' " +
            "GROUP BY b.tour.id, b.tour.title ORDER BY COUNT(b) DESC")
    List<TourStat> topBookedTours(Pageable pageable);

    // Top tour theo lượt huỷ
    @Query("SELECT new com.dinhngoctranduy.model.dto.statistic.TourStat(" +
            "b.tour.id, b.tour.title, COUNT(b)) " +
            "FROM Booking b WHERE b.status = 'CANCELLED' " +
            "GROUP BY b.tour.id, b.tour.title ORDER BY COUNT(b) DESC")
    List<TourStat> topCancelledTours(Pageable pageable);

    // Lấy booking kèm user và tour (để tránh lazy loading khi gửi email)
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.user LEFT JOIN FETCH b.tour WHERE b.id = :id")
    Optional<Booking> findFullById(@org.springframework.data.repository.query.Param("id") Long id);
}

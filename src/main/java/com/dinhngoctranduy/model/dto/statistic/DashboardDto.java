package com.dinhngoctranduy.model.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private Long totalBookings;        // Tổng số đơn đặt
    private Double totalRevenue;       // Tổng doanh thu
    private Long activeTours;          // Số tour đang hoạt động (available = true)
    private Long totalUsers;           // Tổng số người dùng

    private List<RevenueByDate> revenueByDate;  // Doanh thu theo ngày
    private List<RevenueByMonth> revenueByMonth;// Doanh thu theo tháng
    private List<RevenueByYear> revenueByYear;  // Doanh thu theo năm

    private List<TourStat> topBookedTours;      // Top tour lượt đặt cao
    private List<TourStat> topCancelledTours;   // Top tour bị huỷ nhiều

}

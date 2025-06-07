package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.model.dto.statistic.DashboardDto;
import com.dinhngoctranduy.repository.BookingRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DashboardService {

    private final BookingRepository bookingRepo;

    public DashboardDto getDashboardOverview() {
        DashboardDto dto = new DashboardDto();

        dto.setTotalBookings(bookingRepo.countActiveBookings());
        dto.setTotalRevenue(bookingRepo.sumTotalRevenue());
        dto.setActiveTours(bookingRepo.countActiveTours());
        dto.setTotalUsers(bookingRepo.countActiveUsers());

        dto.setRevenueByDate(bookingRepo.revenueByDate());
        dto.setRevenueByMonth(bookingRepo.revenueByMonth());
        dto.setRevenueByYear(bookingRepo.revenueByYear());

        dto.setTopBookedTours(bookingRepo.topBookedTours(PageRequest.of(0, 5)));
        dto.setTopCancelledTours(bookingRepo.topCancelledTours(PageRequest.of(0, 5)));

        return dto;
    }
}

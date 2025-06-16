package com.dinhngoctranduy.model.dto;// TourSearchRequest.java

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TourSearchRequest {
    private String keyword;         // Từ khóa tìm kiếm (tên tour, mô tả, địa điểm)
    private LocalDate startDate;    // Ngày khởi hành (từ)
    private LocalDate endDate;      // Ngày khởi hành (đến)
    private String location;        // Miền Bắc / Miền Trung / Miền Nam / Quốc tế
    private Long minPrice;          // Giá từ
    private Long maxPrice;          // Giá đến
    private Integer minRating;
    private Integer maxRating;
}

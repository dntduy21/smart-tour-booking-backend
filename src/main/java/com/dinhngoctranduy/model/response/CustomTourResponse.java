package com.dinhngoctranduy.model.response;

import com.dinhngoctranduy.util.constant.TourRegion;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomTourResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String destination;
    private int capacity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private long durationDays;
    private long durationNights;
    private String description;
    private TourRegion region;
    private int adultsCapacity;
    private int childrenCapacity;
    private boolean status;
}

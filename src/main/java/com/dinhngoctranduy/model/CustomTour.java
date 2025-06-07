package com.dinhngoctranduy.model;

import com.dinhngoctranduy.util.constant.TourCategory;
import com.dinhngoctranduy.util.constant.TourRegion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "custom_tours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomTour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String description;

    private int capacity;
    private int adultsCapacity;
    private int childrenCapacity;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String destination;

    @Enumerated(EnumType.STRING)
    private TourRegion region;

    private String email;
    private String phone;
    private String name;

    // 6. Thời gian (có thể tính tự động)
    @Transient
    public long getDurationDays() {
        return ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
    }

    @Transient
    public long getDurationNights() {
        return getDurationDays() - 1;
    }

    private boolean deleted = false;
}
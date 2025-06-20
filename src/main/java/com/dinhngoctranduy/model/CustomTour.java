package com.dinhngoctranduy.model;

import com.dinhngoctranduy.util.constant.TourRegion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    private boolean status = false;

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

    // Người dùng đăng nhập (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
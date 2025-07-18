package com.dinhngoctranduy.model;

import com.dinhngoctranduy.util.StringListConverter;
import com.dinhngoctranduy.util.constant.TourRegion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String description;

    private int capacity;
    private double priceAdults;
    private double priceChildren;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String destination;

    @Enumerated(EnumType.STRING)
    private TourRegion region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private TourCategory category;

    // 3. Hãng hàng không
    private String airline;

    // 4. Code tour
    @Column(unique = true, nullable = false)
    private String code;

    // 6. Thời gian (có thể tính tự động)
    @Transient
    public long getDurationDays() {
        return ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
    }

    @Transient
    public long getDurationNights() {
        return getDurationDays() - 1;
    }

    // Availability flag
    private boolean available;

    private boolean deleted = false;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "LONGTEXT")
    private List<String> itinerary = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "tour", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();
}
package com.dinhngoctranduy.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Column(length = 1000)
    private String description;
    private double discountPercent;

    private Instant startAt;
    private Instant endAt;
    private int usageLimit;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

}
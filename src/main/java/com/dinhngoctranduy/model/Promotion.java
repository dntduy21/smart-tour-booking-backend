package com.dinhngoctranduy.model;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private double discount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int quantity;
    @OneToMany(mappedBy = "promotion")
    private List<Booking> bookings = new ArrayList<>();
}


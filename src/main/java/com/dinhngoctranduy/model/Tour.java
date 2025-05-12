package com.dinhngoctranduy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String description;
    private int quantity;
    private double priceAdults;
    private double priceChildren;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String destination;
    private boolean availability;
    private List<String> itinerary;
    private String tourType;
    private String guideInfo;
    private boolean deleted;

    @OneToMany(mappedBy = "tour")
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "tour")
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "tour")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "tour")
    private List<History> histories = new ArrayList<>();
}


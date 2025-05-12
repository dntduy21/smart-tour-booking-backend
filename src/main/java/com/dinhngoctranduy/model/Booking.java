package com.dinhngoctranduy.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;
    private int numAdults;
    private int numChildren;
    private double totalPrice;
    private String paymentStatus;
    private String bookingStatus;
    private boolean deleted;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Invoice invoice;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Checkout checkout;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;
}

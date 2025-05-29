package com.dinhngoctranduy.model;

import com.dinhngoctranduy.util.constant.BookingStatus;
import com.dinhngoctranduy.util.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant bookingDate;
    private int adults;
    private int children;
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    // Guest booking info
    private String guestName;
    private String guestEmail;
    private String guestPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Invoice invoice;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Checkout checkout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    private List<History> histories = new ArrayList<>();
}
package com.dinhngoctranduy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "checkouts")
public class Checkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentMethod;
    private LocalDateTime paymentDate;
    private double amount;
    private String paymentStatus;
    private String transactionId;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}


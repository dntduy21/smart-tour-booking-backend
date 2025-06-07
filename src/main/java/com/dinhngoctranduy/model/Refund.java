package com.dinhngoctranduy.model;

import com.dinhngoctranduy.util.constant.RefundStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refund")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant createdAt;
    private Instant refundAt;

    private double refundPercent;

    private double refundAmount;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}
package com.dinhngoctranduy.model;

import com.dinhngoctranduy.util.constant.PaymentGateway;
import com.dinhngoctranduy.util.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "checkouts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentGateway method;     // VNPAY

    private Instant paidAt;
    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;      // UNPAID, PAID, FAILED

    @Column(unique = true)
    private String transactionId;      // mã giao dịch PSP

    // Chi tiết PSP trả về
    private String responseCode;
    private String errorMessage;
    private String bankCode;

    @Lob
    private String callbackData;       // toàn bộ payload

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}
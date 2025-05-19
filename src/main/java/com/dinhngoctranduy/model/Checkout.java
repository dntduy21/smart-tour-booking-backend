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
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentGateway method;     // VNPAY, MOMO, BANK_TRANSFER,…

    private String orderInfo;          // ghi chú đơn

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
    private String qrCodeUrl;          // nếu là MoMo QR

    @Lob
    private String callbackData;       // toàn bộ payload

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}
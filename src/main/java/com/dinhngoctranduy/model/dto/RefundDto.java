package com.dinhngoctranduy.model.dto;

import com.dinhngoctranduy.model.Booking;
import com.dinhngoctranduy.model.Refund;
import com.dinhngoctranduy.util.constant.RefundStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundDto {
    private Long id;
    private Instant createdAt;
    private Instant refundAt;
    private double refundPercent;
    private double refundAmount;
    private RefundStatus status;
    private Long bookingId;

    public static RefundDto mapToDto(Refund refund) {
        if (refund == null) return null;

        return RefundDto.builder()
                .id(refund.getId())
                .createdAt(refund.getCreatedAt())
                .refundAt(refund.getRefundAt())
                .refundPercent(refund.getRefundPercent())
                .refundAmount(refund.getRefundAmount())
                .status(refund.getStatus())
                .bookingId(refund.getBooking() != null ? refund.getBooking().getId() : null)
                .build();
    }

    public static Refund mapToEntity(RefundDto dto, Booking booking) {
        if (dto == null || booking == null) return null;

        return Refund.builder()
                .id(dto.getId())
                .createdAt(dto.getCreatedAt())
                .refundAt(dto.getRefundAt())
                .refundPercent(dto.getRefundPercent())
                .refundAmount(dto.getRefundAmount())
                .status(dto.getStatus())
                .booking(booking)
                .build();
    }
}

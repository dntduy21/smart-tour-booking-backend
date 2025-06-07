package com.dinhngoctranduy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CancelResponse {
    private Long bookingId;
    private double originalAmount;
    private double penaltyPercent;
    private double refundAmount;
}

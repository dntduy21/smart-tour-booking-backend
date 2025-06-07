package com.dinhngoctranduy.model.dto;
import lombok.*;

import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionResponse {
    private Long id;
    private String code;
    private String description;
    private double discountPercent;
    private Instant startAt;
    private Instant endAt;
    private int usageLimit;
    private boolean active;
}
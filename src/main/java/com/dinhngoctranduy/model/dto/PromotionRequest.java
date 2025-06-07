package com.dinhngoctranduy.model.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionRequest {
    @NotBlank
    private String code;

    private String description;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private double discountPercent;

    private Instant startAt;
    private Instant endAt;

    @Min(0)
    private int usageLimit;
}
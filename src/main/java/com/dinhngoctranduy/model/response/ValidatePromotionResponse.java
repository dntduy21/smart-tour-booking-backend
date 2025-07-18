package com.dinhngoctranduy.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidatePromotionResponse {
    private boolean valid;
    private String message;
    private double discountPercent;
}

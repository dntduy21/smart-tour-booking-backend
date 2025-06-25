package com.dinhngoctranduy.model.dto;

import com.dinhngoctranduy.model.Promotion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionDTO {
    private Long id;
    private String code;
    private double discountPercent;
    private Instant startAt;
    private Instant endAt;

    public static PromotionDTO fromDomain(Promotion promotion) {
        if(promotion == null) {
            return null;
        }
        return PromotionDTO.builder()
                .id(promotion.getId())
                .code(promotion.getCode())
                .discountPercent(promotion.getDiscountPercent())
                .startAt(promotion.getStartAt())
                .endAt(promotion.getEndAt())
                .build();
    }
}

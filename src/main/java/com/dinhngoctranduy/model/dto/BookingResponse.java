package com.dinhngoctranduy.model.dto;

import com.dinhngoctranduy.model.Participant;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private TourDto tour;
    private double totalPrice;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Instant bookingAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Instant cancelAt;

    private PromotionDto promotionDto;
    private List<Participant> participants;
    private RefundDto refund;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TourDto {
        private Long tourId;
        private String title;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private double price;
    }
}

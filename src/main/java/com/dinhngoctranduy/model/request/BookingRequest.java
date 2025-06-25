package com.dinhngoctranduy.model.request;

import com.dinhngoctranduy.model.Participant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {
    private Long tourId;
    private int adults;
    private int children;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private List<Participant> participants;
    private String promoCode;
    private Long userId;
    private String promotionCode;
    private Boolean isCashPayment = false;
}


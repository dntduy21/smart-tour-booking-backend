package com.dinhngoctranduy.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;
    private int rating;
    private String comment;
    private Long tourId;
    private Long userId;
}

package com.dinhngoctranduy.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewTourDTO {
    private Long id;
    private int rating;
    private String comment;
    private Long tourId;
    private Long userId;
    private String username;
    private String fullName;
}
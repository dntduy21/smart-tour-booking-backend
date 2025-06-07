package com.dinhngoctranduy.model.dto;

import com.dinhngoctranduy.util.constant.TourRegion;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomTourRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String destination;

    @Min(1)
    private int capacity;

    private int adultsCapacity;
    private int childrenCapacity;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    private String description;

    @NotNull
    private TourRegion region;
}

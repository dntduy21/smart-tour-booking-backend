package com.dinhngoctranduy.model.dto;

import com.dinhngoctranduy.util.constant.TourCategory;
import com.dinhngoctranduy.util.constant.TourRegion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
public class TourRequestDTO {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Integer capacity;

    @NotNull
    private Double priceAdults;

    @NotNull
    private Double priceChildren;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @NotBlank
    private String destination;

    @NotNull
    private TourRegion region;

    @NotNull
    private TourCategory category;

    private String airline;

    @NotBlank
    private String code;

    private Boolean available;

    private List<String> itinerary = new ArrayList<>();

    private List<String> images = new ArrayList<>();
}

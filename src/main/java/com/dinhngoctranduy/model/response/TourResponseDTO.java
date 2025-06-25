package com.dinhngoctranduy.model.response;

import com.dinhngoctranduy.model.dto.ImageDTO;
import com.dinhngoctranduy.util.constant.TourCategory;
import com.dinhngoctranduy.util.constant.TourRegion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Setter
public class TourResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Integer capacity;
    private Double priceAdults;
    private Double priceChildren;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String destination;
    private TourRegion region;
    private TourCategory category;
    private String airline;
    private String code;
    private Boolean available;
    private List<String> itinerary;
    private long durationDays;
    private long durationNights;
    private List<ImageDTO> images;
}

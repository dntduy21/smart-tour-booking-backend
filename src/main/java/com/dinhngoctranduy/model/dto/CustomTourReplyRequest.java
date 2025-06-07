package com.dinhngoctranduy.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomTourReplyRequest {
    @NotBlank
    private String message;
}

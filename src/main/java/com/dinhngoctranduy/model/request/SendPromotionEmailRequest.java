package com.dinhngoctranduy.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendPromotionEmailRequest {

    @NotEmpty(message = "Danh sách email không được trống")
    private List<@Email String> emails;

    @NotBlank(message = "Mã khuyến mãi không được để trống")
    private String promotionCode;
}

package com.dinhngoctranduy.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostRequestDTO {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    // URL hình ảnh sẽ được gửi từ client sau khi upload
    @NotBlank(message = "URL hình ảnh không được để trống")
    private String imageUrl;

    @NotNull(message = "ID tác giả không được để trống")
    private Long authorId;
}

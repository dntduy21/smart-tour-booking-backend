package com.dinhngoctranduy.model.dto;

import com.dinhngoctranduy.model.Image;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDTO {
    private Long id;
    private String url;
    private Long uploadedAt;

    public static ImageDTO fromDomain(Image image) {
        return ImageDTO.builder()
                .id(image.getId())
                .url(image.getUrl())
                .uploadedAt(image.getUploadedAt().toEpochMilli())
                .build();
    }

    public Image toDomain() {
        return Image.builder()
                .id(id)
                .url(url)
                .uploadedAt(uploadedAt == null || uploadedAt == 0 ? Instant.now() : Instant.ofEpochMilli(uploadedAt))
                .build();
    }
}
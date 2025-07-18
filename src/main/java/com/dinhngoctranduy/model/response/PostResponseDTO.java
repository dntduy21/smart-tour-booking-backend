package com.dinhngoctranduy.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AuthorDTO author;

    @Data
    @Builder
    public static class AuthorDTO {
        private Long id;
        private String username;
    }
}

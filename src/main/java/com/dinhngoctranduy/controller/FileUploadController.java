package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.dto.FileUploadResponseDTO;
import com.dinhngoctranduy.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/files") // Đặt một đường dẫn chung cho các API liên quan đến file
public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<FileUploadResponseDTO>> uploadFiles(@RequestParam("images") MultipartFile[] images) {
        // Dùng Stream để xử lý từng file trong mảng
        List<FileUploadResponseDTO> responses = Arrays.stream(images)
                .map(image -> {
                    // Gọi service để lưu từng file một
                    String imageUrl = fileStorageService.storeFile(image);
                    // Tạo DTO response cho file đó
                    return new FileUploadResponseDTO(imageUrl);
                })
                .collect(Collectors.toList()); // Thu thập kết quả vào một List

        return ResponseEntity.ok(responses);
    }
}
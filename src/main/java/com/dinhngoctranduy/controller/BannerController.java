package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.response.FileUploadResponseDTO;
import com.dinhngoctranduy.service.BannerStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/banners") // Endpoint riêng cho banner
public class BannerController {

    private final BannerStorageService bannerStorageService;

    public BannerController(BannerStorageService bannerStorageService) {
        this.bannerStorageService = bannerStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<FileUploadResponseDTO>> uploadBanners(@RequestParam("banners") MultipartFile[] banners) {
        List<FileUploadResponseDTO> responses = Arrays.stream(banners)
                .map(banner -> {
                    String bannerUrl = bannerStorageService.storeFile(banner);
                    // Bạn có thể dùng lại FileUploadResponseDTO hoặc tạo DTO mới nếu cần
                    return new FileUploadResponseDTO(bannerUrl);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<FileUploadResponseDTO>> getAllBanners() {
        List<String> bannerUrls = bannerStorageService.loadAll();
        List<FileUploadResponseDTO> responses = bannerUrls.stream()
                .map(FileUploadResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{filename:.+}")
    public ResponseEntity<String> deleteBanner(@PathVariable String filename) {
        boolean deleted = bannerStorageService.deleteFile(filename);
        if (deleted) {
            return ResponseEntity.ok("Đã xóa file thành công: " + filename);
        } else {
            return ResponseEntity.status(404).body("Không tìm thấy file để xóa: " + filename);
        }
    }
}

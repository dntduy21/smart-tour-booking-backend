package com.dinhngoctranduy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${upload-file.base-path}") String basePath) {
        // SỬA DÒNG NÀY: Dùng regex linh hoạt hơn để xóa "file:" và các dấu "/" theo sau
        String actualPath = basePath.replaceFirst("^file:/*", "");

        this.fileStorageLocation = Paths.get(actualPath).toAbsolutePath().normalize();
    }

    public String storeFile(MultipartFile file) {
        // ... (phần còn lại của class giữ nguyên)
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        try {
            if (uniqueFileName.contains("..")) {
                throw new RuntimeException("Tên file chứa ký tự không hợp lệ: " + uniqueFileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/storage/")
                    .path(uniqueFileName)
                    .toUriString();

        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file " + uniqueFileName + ". Vui lòng thử lại!", ex);
        }
    }
}
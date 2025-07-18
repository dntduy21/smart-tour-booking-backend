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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BannerStorageService {

    private final Path bannerStorageLocation;

    // Sử dụng giá trị từ 'upload-banner.base-path'
    public BannerStorageService(@Value("${upload-banner.base-path}") String basePath) {
        String actualPath = basePath.replaceFirst("^file:/*", "");
        this.bannerStorageLocation = Paths.get(actualPath).toAbsolutePath().normalize();
    }

    public String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        try {
            if (uniqueFileName.contains("..")) {
                throw new RuntimeException("Tên banner chứa ký tự không hợp lệ: " + uniqueFileName);
            }

            Path targetLocation = this.bannerStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Xây dựng URL để truy cập banner
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/banners/") // Đường dẫn web cho banner
                    .path(uniqueFileName)
                    .toUriString();

        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu banner " + uniqueFileName + ". Vui lòng thử lại!", ex);
        }
    }

    public List<String> loadAll() {
        try (Stream<Path> paths = Files.walk(this.bannerStorageLocation, 1)) {
            return paths
                    .filter(path -> !path.equals(this.bannerStorageLocation)) // Bỏ qua thư mục gốc
                    .filter(path -> !Files.isDirectory(path)) // Chỉ lấy file, không lấy thư mục con
                    .map(path -> {
                        // Xây dựng URL để truy cập file
                        return ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/banners/")
                                .path(path.getFileName().toString())
                                .toUriString();
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc danh sách file", e);
        }
    }

    public boolean deleteFile(String filename) {
        try {
            Path file = this.bannerStorageLocation.resolve(filename).normalize();
            // Thêm kiểm tra để đảm bảo file nằm trong thư mục banner
            if (!file.getParent().equals(this.bannerStorageLocation)) {
                throw new SecurityException("Không được phép xóa file ngoài thư mục banner!");
            }
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xóa file: " + filename, e);
        }
    }
}

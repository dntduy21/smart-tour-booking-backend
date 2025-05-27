package com.dinhngoctranduy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class StaticResourcesWebConfiguration implements WebMvcConfigurer {

    @Value("${upload-file.base-path}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Kiểm tra và tạo thư mục nếu chưa tồn tại
        createDirectoryIfNotExists(basePath);

        // Cấu hình resource handler
        registry.addResourceHandler("/storage/**")
                .addResourceLocations(basePath);
    }

    private void createDirectoryIfNotExists(String path) {
        try {
            // Nếu là "file:/C:/upload" → ta cần loại bỏ "file:" để lấy path thực
            String directoryPath = path.replaceFirst("^file:(//)?", "");

            File dir = new File(directoryPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (created) {
                    System.out.println("Created upload directory: " + dir.getAbsolutePath());
                } else {
                    System.err.println("Failed to create upload directory: " + dir.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("Error when checking/creating upload directory: " + e.getMessage());
        }
    }
}

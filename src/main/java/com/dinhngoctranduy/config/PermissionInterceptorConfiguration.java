package com.dinhngoctranduy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/api/v1/login",
                "/api/v1/register",
                "/api/v1/verify",
                "/storage/**",
                "/api/v1/ping",
//                "/api/v1/**",
                "/api/v1/categories",
                "/api/v1/posts/**",
                "/api/v1/public/contact/primary",
                "/api/v1/forgot-password",
                "/api/v1/tours/upcoming",
                "/api/v1/tours/finished",
                "/api/v1/tours/ongoing",
                "/api/v1/categories/{id}/tours",
                "/api/v1/banners"
        };
        registry.addInterceptor(permissionInterceptor)
                .excludePathPatterns(whiteList);
    }
}

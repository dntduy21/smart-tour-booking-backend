package com.dinhngoctranduy.config;

import com.dinhngoctranduy.model.Permission;
import com.dinhngoctranduy.model.Role;
import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.service.UserService;
import com.dinhngoctranduy.util.SecurityUtil;
import com.dinhngoctranduy.util.error.IdInValidException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

@Component
public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        String username = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (username != null && !username.isEmpty()) {
            User user = this.userService.handleGetUserByUsername(username);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream().anyMatch(item -> item.getApiPath().equals(path)
                            && item.getMethod().equals(httpMethod));

                    if (isAllow == false) {
                        throw new IdInValidException("Bạn không có quyền truy cập endpoint này");
                    }
                } else {
                    throw new IdInValidException("Bạn không có quyền truy cập endpoint này");
                }
            }
        }
        return true;
    }
}

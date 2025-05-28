package com.dinhngoctranduy.config;

import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("userDetailsService")
public class UserDetailCustom implements UserDetailsService {
    private final UserService userService;

    public UserDetailCustom(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userService.handleGetUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Tài khoản không tồn tại: " + username);
        }
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Email chưa được xác thực.");
        }
        if (user.isDeleted()) {
            throw new RuntimeException("Tài khoản của bạn đã xoá");
        }
        if (user.isBlocked()) {
            throw new RuntimeException("Tài khoản của bạn đã bị khoá vui lòng liên hệ với quản trị viên để biết thêm thông tin");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}

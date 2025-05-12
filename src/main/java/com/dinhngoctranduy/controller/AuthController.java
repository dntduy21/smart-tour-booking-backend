package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.VerificationToken;
import com.dinhngoctranduy.model.dto.LoginDTO;
import com.dinhngoctranduy.model.response.ResCreateUserDTO;
import com.dinhngoctranduy.model.response.ResLoginDTO;
import com.dinhngoctranduy.service.EmailService;
import com.dinhngoctranduy.service.UserService;
import com.dinhngoctranduy.service.VerificationTokenService;
import com.dinhngoctranduy.util.SecurityUtil;
import com.dinhngoctranduy.util.error.IdInValidException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
                          SecurityUtil securityUtil,
                          UserService userService,
                          PasswordEncoder passwordEncoder,
                          VerificationTokenService verificationTokenService,
                          EmailService emailService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@RequestBody LoginDTO loginDTO) {
        //Nạp input username/password vào security
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        //Xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        //Tạo token
        String access_token = this.securityUtil.createToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO res = new ResLoginDTO();
        User userCurrentDB = this.userService.handleGetUserByUsername(loginDTO.getUsername());
        if (userCurrentDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(userCurrentDB.getUserId(),
                    userCurrentDB.getEmail(), userCurrentDB.getUsername());
            res.setUser(userLogin);
        }
        res.setAccessToken(access_token);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/register")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User user) throws IdInValidException {
        // Kiểm tra username đã tồn tại
        if (userService.isUsernameExists(user.getUsername())) {
            throw new IdInValidException("Username đã tồn tại");
        }

        // Mã hóa mật khẩu và gán emailVerified = false
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(false);
        User newUser = userService.handleCreateUser(user); // Lưu vào DB

        // Tạo token xác thực
        VerificationToken token = verificationTokenService.createVerificationToken(newUser);

        // Gửi email xác thực
        emailService.sendVerificationEmail(newUser, token.getToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.resCreateUserDTO(newUser));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        Optional<VerificationToken> optionalToken = verificationTokenService.findByToken(token);
        if (optionalToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Token không hợp lệ hoặc đã hết hạn.");
        }

        VerificationToken verificationToken = optionalToken.get();
        User user = verificationToken.getUser();
        if (user == null) {
            return ResponseEntity.badRequest().body("Token không hợp lệ.");
        }

        user.setEmailVerified(true);
        userService.handleUpdateUser(user);
        return ResponseEntity.ok("Email đã được xác thực, bạn có thể đăng nhập.");
    }
}

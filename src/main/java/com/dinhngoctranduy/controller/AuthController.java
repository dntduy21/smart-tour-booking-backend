package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.Role;
import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.VerificationToken;
import com.dinhngoctranduy.model.dto.AccountDTO;
import com.dinhngoctranduy.model.dto.LoginDTO;
import com.dinhngoctranduy.model.response.ResCreateUserDTO;
import com.dinhngoctranduy.model.response.ResLoginDTO;
import com.dinhngoctranduy.model.response.ResUserDTO;
import com.dinhngoctranduy.service.*;
import com.dinhngoctranduy.service.impl.PromotionServiceImpl;
import com.dinhngoctranduy.util.SecurityUtil;
import com.dinhngoctranduy.util.error.IdInValidException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final RoleService roleService;
    private final PromotionServiceImpl promotionService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
                          SecurityUtil securityUtil,
                          UserService userService,
                          VerificationTokenService verificationTokenService,
                          EmailService emailService,
                          RoleService roleService,
                          PromotionServiceImpl promotionService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
        this.roleService = roleService;
        this.promotionService = promotionService;
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
        Role role = userCurrentDB.getRole();
        ResLoginDTO.RoleDTO roleDTO = new ResLoginDTO.RoleDTO(role.getId(), role.getName());
        if (userCurrentDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(userCurrentDB.getId(),
                    userCurrentDB.getEmail(), userCurrentDB.getUsername(), roleDTO);
            res.setUser(userLogin);
        }
        res.setAccessToken(access_token);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/register")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User user) throws IdInValidException, MessagingException {
        if (userService.isUsernameExists(user.getUsername())) {
            throw new IdInValidException("Username đã tồn tại");
        }

        if (userService.isEmailExists(user.getEmail())) {
            throw new IdInValidException("Email đã được sử dụng");
        }

        Role defaultRole = this.roleService.fetchById(1);
        user.setRole(defaultRole);

        User newUser = this.userService.handleCreateUser(user);

        VerificationToken token = verificationTokenService.createVerificationToken(newUser);
        emailService.sendVerificationEmail(newUser, token.getToken());

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.resCreateUserDTO(newUser));
    }


    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        Optional<VerificationToken> optionalToken = verificationTokenService.findByToken(token);

        if (optionalToken.isEmpty()) {
            String htmlBody = generateStaticPageHtml(
                    "Xác thực Thất Bại",
                    "Token không hợp lệ hoặc đã hết hạn.",
                    "Vui lòng đóng trang này và thử lại.",
                    "#dc3545"
            );
            return createHtmlResponse(htmlBody, 400);
        }

        VerificationToken verificationToken = optionalToken.get();
        User user = verificationToken.getUser();

        if (user == null) {
            String htmlBody = generateStaticPageHtml(
                    "Xác thực Thất Bại",
                    "Token không hợp lệ.",
                    "Liên kết này không được liên kết với bất kỳ người dùng nào.",
                    "#dc3545"
            );
            return createHtmlResponse(htmlBody, 400);
        }

        user.setEmailVerified(true);
        userService.handleUpdateUser(user);
        promotionService.createAndSendWelcomePromotion(user);

        String htmlBody = generateStaticPageHtml(
                "Xác thực Thành Công!",
                "Email đã được xác thực thành công.",
                "Bây giờ bạn có thể đóng trang này và đăng nhập vào ứng dụng.",
                "#28a745"
        );
        return createHtmlResponse(htmlBody, 200);
    }

    private ResponseEntity<String> createHtmlResponse(String htmlBody, int statusCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<>(htmlBody, headers, statusCode);
    }

    private String generateStaticPageHtml(String title, String message, String details, String headerColor) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f0f2f5; display: flex; justify-content: center; align-items: center; height: 100vh; }
                        .container { background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); text-align: center; max-width: 450px; overflow: hidden; }
                        .header { background-color: %s; color: white; padding: 30px 20px; }
                        .header h1 { margin: 0; font-size: 28px; }
                        .content { padding: 30px 40px; color: #333; }
                        .content p { font-size: 16px; color: #666; line-height: 1.6; }
                        .content .message { font-size: 18px; font-weight: 500; color: #333; margin-bottom: 10px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>%s</h1>
                        </div>
                        <div class="content">
                            <p class="message">%s</p>
                            <p>%s</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(title, headerColor, title, message, details);
    }

    @GetMapping("/account")
    public ResponseEntity<AccountDTO> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUserDB = this.userService.handleGetUserByUsername(email);
        AccountDTO userLogin = new AccountDTO();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setFullName(currentUserDB.getFullName());
            userLogin.setPhone(currentUserDB.getPhone());
            userLogin.setAddress(currentUserDB.getAddress());
            userLogin.setBirthDate(currentUserDB.getBirthDate());
            userLogin.setGender(currentUserDB.getGender());
            Role role = currentUserDB.getRole();
            ResUserDTO.RoleUser roleDTO = new ResUserDTO.RoleUser(role.getId(), role.getName());
            userLogin.setRole(roleDTO);
            userLogin.setPassword(currentUserDB.getPassword());
        }

        return ResponseEntity.ok().body(userLogin);
    }
}

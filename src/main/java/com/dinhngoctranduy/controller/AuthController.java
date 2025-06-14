package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.Role;
import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.VerificationToken;
import com.dinhngoctranduy.model.dto.LoginDTO;
import com.dinhngoctranduy.model.response.ResCreateUserDTO;
import com.dinhngoctranduy.model.response.ResLoginDTO;
import com.dinhngoctranduy.model.response.ResUserDTO;
import com.dinhngoctranduy.service.*;
import com.dinhngoctranduy.service.impl.PromotionServiceImpl;
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
    private final RoleService roleService;
    private final PromotionServiceImpl promotionService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
                          SecurityUtil securityUtil,
                          UserService userService,
                          PasswordEncoder passwordEncoder,
                          VerificationTokenService verificationTokenService,
                          EmailService emailService,
                          RoleService roleService,
                          PromotionServiceImpl promotionService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User user) throws IdInValidException {
        if (userService.isUsernameExists(user.getUsername())) {
            throw new IdInValidException("Username đã tồn tại");
        }

        if (userService.isEmailExists(user.getEmail())) {
            throw new IdInValidException("Email đã được sử dụng");
        }

        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);

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
            return ResponseEntity.badRequest().body("Token không hợp lệ hoặc đã hết hạn.");
        }

        VerificationToken verificationToken = optionalToken.get();
        User user = verificationToken.getUser();
        if (user == null) {
            return ResponseEntity.badRequest().body("Token không hợp lệ.");
        }

        user.setEmailVerified(true);
        userService.handleUpdateUser(user);
        promotionService.createAndSendWelcomePromotion(user);
        return ResponseEntity.ok("Email đã được xác thực, bạn có thể đăng nhập.");
    }

    @GetMapping("/account")
    public ResponseEntity<ResUserDTO> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUserDB = this.userService.handleGetUserByUsername(email);
        ResUserDTO userLogin = new ResUserDTO();

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
        }

        return ResponseEntity.ok().body(userLogin);
    }
}

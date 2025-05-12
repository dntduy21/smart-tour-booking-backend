package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user, String token) {
        String subject = "Xác thực tài khoản của bạn";
        String confirmationUrl = "http://localhost:8080/api/v1/verify?token=" + token;
        String message = "Click vào link sau để xác thực email: " + confirmationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }
}
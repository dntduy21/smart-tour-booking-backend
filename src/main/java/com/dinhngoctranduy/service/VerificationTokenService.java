package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.VerificationToken;
import com.dinhngoctranduy.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationTokenService {
    private final VerificationTokenRepository tokenRepo;

    public VerificationTokenService(VerificationTokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    public VerificationToken createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1)); // hết hạn sau 1 ngày
        return tokenRepo.save(verificationToken);
    }

    public Optional<VerificationToken> findByToken(String token) {
        return tokenRepo.findByToken(token);
    }
}

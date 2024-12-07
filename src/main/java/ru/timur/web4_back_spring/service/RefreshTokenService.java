package ru.timur.web4_back_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.timur.web4_back_spring.dao.RefreshTokenRepository;
import ru.timur.web4_back_spring.entity.RefreshToken;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${auth.refreshTokenValidity}")
    private Duration refreshTokenValidity;

    private final RefreshTokenRepository refreshTokenRepository;

    public UUID generateRefreshToken() {
        RefreshToken refreshToken = RefreshToken.builder()
                .issuedAt(Date.from(Instant.now()))
                .expiresAt(Date.from(Instant.now().plus(refreshTokenValidity)))
                .build();
        return refreshTokenRepository
                .save(refreshToken)
                .getId();

    }

    public RefreshToken getRefreshToken(UUID token) {
        return refreshTokenRepository.findById(token).orElse(null);
    }

    public boolean isTokenValid(RefreshToken refreshToken) {
        if (refreshToken == null) return false;
        return !refreshToken.getExpiresAt().before(Date.from(Instant.now()));
    }

    public void removeToken(RefreshToken refreshToken) {
        refreshTokenRepository.deleteById(refreshToken.getId());
    }
}

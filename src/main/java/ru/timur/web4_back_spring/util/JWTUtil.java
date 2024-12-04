package ru.timur.web4_back_spring.util;

import java.time.Instant;

public interface JWTUtil {
    String generateToken(Long userId, Instant expiresAt);
    Long getUserId(String token);
    boolean isExpired(String token);
}

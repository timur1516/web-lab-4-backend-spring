package ru.timur.web4_back_spring.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtUtilImpl implements JWTUtil {
    private Key key;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @PostConstruct
    private void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            log.error("SecretKey property was not found in application properties");
            return;
        }
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
        log.info("JWT secret key was loaded successfully");
    }

    @Override
    public String generateToken(Long userId, Instant expiresAt) {
        return Jwts
                .builder()
                .claim("userId", userId)
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }

    @Override
    public Long getUserId(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("userId", Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isExpired(String token) {
        try {
            Date expirationTime = Jwts
                    .parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            if(expirationTime == null) return true;
            return expirationTime.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}

package ru.timur.web4_back_spring.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;


@Service
public class AccessTokenService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${auth.accessTokenValidity}")
    private Duration accessTokenValidity;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(secretKey);
    }

    public void verifyToken(String token) throws JWTVerificationException {
        JWT.require(algorithm).build().verify(token);
    }

    public String extractUsername(String token) {
        return JWT
                .decode(token)
                .getSubject();
    }

    public String generateAccessToken(UserDetails user) {
        return JWT
                .create()
                .withIssuer("jwtService")
                .withSubject(user.getUsername())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(accessTokenValidity.toSeconds()))
                .sign(algorithm);
    }
}

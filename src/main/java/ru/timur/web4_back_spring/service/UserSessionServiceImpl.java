package ru.timur.web4_back_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timur.web4_back_spring.dao.UserSessionDAO;
import ru.timur.web4_back_spring.dto.CredentialsDTO;
import ru.timur.web4_back_spring.dto.TokenDTO;
import ru.timur.web4_back_spring.entity.UserEntity;
import ru.timur.web4_back_spring.entity.UserSessionEntity;
import ru.timur.web4_back_spring.exception.SessionNotFoundException;
import ru.timur.web4_back_spring.exception.SessionTimeoutException;
import ru.timur.web4_back_spring.util.JWTUtil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class UserSessionServiceImpl implements UserSessionService {
    private final UserSessionDAO userSessionDAO;
    private final JWTUtil jwtUtil;

    @Autowired
    public UserSessionServiceImpl(UserSessionDAO userSessionDAO,
                              JWTUtil jwtUtil) {
        this.userSessionDAO = userSessionDAO;
        this.jwtUtil = jwtUtil;
    }

    private final long ACCESS_TOKEN_VALIDITY_MINUTES = 30;
    private final long REFRESH_TOKEN_VALIDITY_HOURS = 5;

    @Override
    public CredentialsDTO startSession(UserEntity user) {
        String accessToken = jwtUtil.generateToken(user.getId(), Instant.now().plus(ACCESS_TOKEN_VALIDITY_MINUTES, ChronoUnit.MINUTES));
        String refreshToken = jwtUtil.generateToken(user.getId(), Instant.now().plus(REFRESH_TOKEN_VALIDITY_HOURS, ChronoUnit.HOURS));
        UserSessionEntity userSessionEntity = UserSessionEntity
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        userSessionDAO.save(userSessionEntity);

        return new CredentialsDTO(accessToken, refreshToken);
    }

    @Override
    public void endSession(String token) {
        userSessionDAO.deleteByAccessToken(token);
    }

    @Override
    public void validateToken(String token) throws SessionNotFoundException, SessionTimeoutException {
        if (jwtUtil.isExpired(token))
            throw new SessionTimeoutException("Session with token " + token + " is expired");
        if (userSessionDAO.findByAccessToken(token).isEmpty())
            throw new SessionNotFoundException("Session with token " + token + " does not exists");
    }

    @Override
    public TokenDTO refreshToken(String token) throws SessionNotFoundException, SessionTimeoutException {
        if (jwtUtil.isExpired(token)) {
            userSessionDAO.deleteByRefreshToken(token);
            throw new SessionTimeoutException("Session with token " + token + " is expired");
        }
        UserSessionEntity userSession = userSessionDAO
                .findByRefreshToken(token)
                .orElseThrow(() -> new SessionNotFoundException("Session with token " + token + " does not exists"));

        String newAccessToken = jwtUtil.generateToken(jwtUtil.getUserId(token), Instant.now().plus(ACCESS_TOKEN_VALIDITY_MINUTES, ChronoUnit.MINUTES));
        userSessionDAO.updateAccessToken(newAccessToken, userSession.getId());
        return new TokenDTO(newAccessToken);
    }
}

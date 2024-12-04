package ru.timur.web4_back_spring.service;

import ru.timur.web4_back_spring.dto.CredentialsDTO;
import ru.timur.web4_back_spring.dto.TokenDTO;
import ru.timur.web4_back_spring.entity.UserEntity;
import ru.timur.web4_back_spring.exception.SessionNotFoundException;
import ru.timur.web4_back_spring.exception.SessionTimeoutException;

public interface UserSessionService {
    CredentialsDTO startSession(UserEntity user);

    void endSession(String token);

    void validateToken(String token) throws SessionNotFoundException, SessionTimeoutException;

    TokenDTO refreshToken(String token) throws SessionNotFoundException, SessionTimeoutException;
}

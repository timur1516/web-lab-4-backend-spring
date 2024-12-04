package ru.timur.web4_back_spring.service;

import ru.timur.web4_back_spring.dto.CredentialsDTO;
import ru.timur.web4_back_spring.dto.TokenDTO;
import ru.timur.web4_back_spring.exception.*;

public interface AuthorizationService {
    CredentialsDTO registerUser(String username, String password) throws UsernameExistsException, InvalidAuthorizationDataException;

    CredentialsDTO authenticateUser(String username, String password) throws InvalidAuthorizationDataException, UserNotFoundException, AuthenticationException;

    CredentialsDTO findGoodOrNew(Long userId);

    void logout(String token);

    TokenDTO getRefreshedToken(TokenDTO token) throws SessionTimeoutException, SessionNotFoundException;
}

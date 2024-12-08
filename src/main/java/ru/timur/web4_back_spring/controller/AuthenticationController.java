package ru.timur.web4_back_spring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.timur.web4_back_spring.dto.*;
import ru.timur.web4_back_spring.entity.User;
import ru.timur.web4_back_spring.exception.*;
import ru.timur.web4_back_spring.service.AuthenticationService;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<CredentialsDTO> register(@Validated @RequestBody RegistrationDTO registrationDTO) throws UsernameExistsException {
        CredentialsDTO credentialsDTO = authenticationService.register(registrationDTO);
        log.info("Successfully registered user with username {}", registrationDTO.getUsername());
        return ResponseEntity.ok(credentialsDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<CredentialsDTO> logIn(@Validated @RequestBody AuthenticationDTO authenticationDTO) throws AuthenticationException {
        CredentialsDTO credentialsDTO = authenticationService.logIn(authenticationDTO);
        log.info("Successfully authenticated user with username {}", authenticationDTO.getUsername());
        return ResponseEntity.ok(credentialsDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logOut(@Validated @RequestBody RefreshTokenDTO refreshTokenDTO) {
        authenticationService.logout(refreshTokenDTO);
        log.info("Successfully logged out user with token {}", refreshTokenDTO.getToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("update-password")
    public ResponseEntity<?> updatePassword(@Validated @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        authenticationService.updatePassword(passwordUpdateDTO);
        log.info("Successfully updated password for user with username {}", passwordUpdateDTO.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AccessTokenDTO> refreshToken(@Validated @RequestBody RefreshTokenDTO refreshTokenDTO) throws RefreshTokenNotFoundException, RefreshTokenTimeoutException {
        AccessTokenDTO newToken = authenticationService.getRefreshedToken(refreshTokenDTO);
        log.info("Successfully refreshed token: {}\nNew token: {}", refreshTokenDTO.getToken(), newToken.getToken());
        return ResponseEntity.ok(newToken);
    }
}

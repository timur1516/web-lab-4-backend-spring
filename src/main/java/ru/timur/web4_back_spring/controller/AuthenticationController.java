package ru.timur.web4_back_spring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.timur.web4_back_spring.dto.CredentialsDTO;
import ru.timur.web4_back_spring.dto.AccessTokenDTO;
import ru.timur.web4_back_spring.dto.RefreshTokenDTO;
import ru.timur.web4_back_spring.dto.UserDTO;
import ru.timur.web4_back_spring.entity.User;
import ru.timur.web4_back_spring.exception.*;
import ru.timur.web4_back_spring.service.AuthenticationService;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> singUp(@Valid @RequestBody UserDTO user) throws UsernameExistsException {
        log.info("Registering user: {}", user.getUsername());
        CredentialsDTO credentialsDTO = authenticationService.register(user);
        log.info("Registered user {}", user.getUsername());
        return ResponseEntity.ok(credentialsDTO);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<CredentialsDTO> logIn(@Valid @RequestBody UserDTO user) {
        log.info("Authenticating user: {}", user.getUsername());
        CredentialsDTO credentialsDTO = authenticationService.authenticate(user);
        log.info("Authenticating user {}", user);
        return ResponseEntity.ok(credentialsDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logOut(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        log.info(refreshTokenDTO.getToken().toString());
        authenticationService.logout(refreshTokenDTO);
        return ResponseEntity.ok("Logout successfully");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AccessTokenDTO> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) throws SessionTimeoutException {
        log.info("Refreshing token: {}", refreshTokenDTO.getToken());
        AccessTokenDTO newToken = authenticationService.getRefreshedToken(refreshTokenDTO);
        log.info("Successfully refreshed token: {} -> {}", refreshTokenDTO.getToken(), newToken.getToken());
        return ResponseEntity.ok(newToken);
    }
}

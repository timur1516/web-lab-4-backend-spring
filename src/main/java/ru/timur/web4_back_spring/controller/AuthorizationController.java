package ru.timur.web4_back_spring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.timur.web4_back_spring.dto.CredentialsDTO;
import ru.timur.web4_back_spring.dto.TokenDTO;
import ru.timur.web4_back_spring.dto.UserDTO;
import ru.timur.web4_back_spring.exception.*;
import ru.timur.web4_back_spring.service.AuthorizationService;
import jakarta.validation.Valid;
import ru.timur.web4_back_spring.util.UserPrincipals;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    private final AuthorizationService authorizationService;

    @Autowired
    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> singUp(@Valid @RequestBody UserDTO user) {
        try {
            CredentialsDTO token = authorizationService.registerUser(user.getUsername(), user.getPassword());
            log.info("Successfully registered user: {}", user.getUsername());
            return ResponseEntity.ok(token);
        } catch (UsernameExistsException | InvalidAuthorizationDataException e) {
            log.info("Registration failed for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> logIn(@Valid @RequestBody UserDTO user) {
        try {
            CredentialsDTO token = authorizationService.authenticateUser(user.getUsername(), user.getPassword());
            log.info("Successfully authenticated user: {}", user.getUsername());
            return ResponseEntity.ok(token);
        } catch (InvalidAuthorizationDataException e) {
            log.info("Authentication failed for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UserNotFoundException e) {
            log.info("Authentication failed for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AuthenticationException e) {
            log.info("Authentication failed for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logOut() {
        UserPrincipals userPrincipals = (UserPrincipals) SecurityContextHolder.getContext().getAuthentication();
        authorizationService.logout(userPrincipals.getToken());
        log.info("Successfully logged out user with id: {}", userPrincipals.getUserId());
        return ResponseEntity.ok("Logout successfully");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenDTO token) {
        try {
            TokenDTO newToken = authorizationService.getRefreshedToken(token);
            log.info("Successfully refreshed token: {} -> {}", token.getToken(), newToken.getToken());
            return ResponseEntity.ok(newToken);
        } catch (SessionTimeoutException | SessionNotFoundException e) {
            log.info("Unable to refresh token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/change-user")
    public ResponseEntity<?> changeUser() {
        UserPrincipals userPrincipals = (UserPrincipals) SecurityContextHolder.getContext().getAuthentication();
        CredentialsDTO token = authorizationService.findGoodOrNew(userPrincipals.getUserId());
        log.info("Successfully logged out user with id: {}", userPrincipals.getUserId());
        return ResponseEntity.ok(token);
    }
}

package ru.timur.web4_back_spring.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.timur.web4_back_spring.dao.UserRepository;
import ru.timur.web4_back_spring.dto.CredentialsDTO;
import ru.timur.web4_back_spring.dto.AccessTokenDTO;
import ru.timur.web4_back_spring.dto.RefreshTokenDTO;
import ru.timur.web4_back_spring.dto.UserDTO;
import ru.timur.web4_back_spring.entity.RefreshToken;
import ru.timur.web4_back_spring.entity.Role;
import ru.timur.web4_back_spring.entity.User;
import ru.timur.web4_back_spring.exception.SessionTimeoutException;
import ru.timur.web4_back_spring.exception.UsernameExistsException;
import ru.timur.web4_back_spring.util.RandomStringGenerator;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenService accessTokenService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final RandomStringGenerator randomStringGenerator;

    public CredentialsDTO register(@Valid UserDTO userDTO) throws UsernameExistsException {
        if (userRepository.findByLogin(userDTO.getUsername()).isPresent())
            throw new UsernameExistsException("User with username: " + userDTO.getUsername() + " already exists");
        User user = User.builder()
                .login(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return generateCredentials(user);
    }

    public CredentialsDTO authenticate(@Valid UserDTO userDTO) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(userDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(userDTO.getUsername()));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getId(),
                        userDTO.getPassword()
                )
        );
        return generateCredentials(user);
    }

    public CredentialsDTO findGoodOrNew(User user) {
        User newUser = userRepository
                .getRandomUserWithDifferentId(user.getId())
                .orElse(null);
        if (newUser == null) {
            while (true) {
                String username = randomStringGenerator.generate(10);
                String password = randomStringGenerator.generate(20);
                try {
                    return register(new UserDTO(username, password));
                } catch (Exception ignored) {
                }
            }
        }
        return generateCredentials(newUser);
    }

    public void logout(RefreshTokenDTO refreshTokenDTO) {
        log.info("Token: {}", refreshTokenDTO.getToken());
        RefreshToken refreshToken = refreshTokenService.getRefreshToken(refreshTokenDTO.getToken());
        if (refreshTokenService.isTokenValid(refreshToken)) {
            refreshTokenService.removeToken(refreshToken);
        }
    }

    public AccessTokenDTO getRefreshedToken(RefreshTokenDTO refreshTokenDTO) throws SessionTimeoutException {
        RefreshToken refreshToken = refreshTokenService.getRefreshToken(refreshTokenDTO.getToken());
        if (refreshTokenService.isTokenValid(refreshToken)) {
            User user = refreshToken.getUser();
            String accessToken = accessTokenService.generateAccessToken(user);
            return new AccessTokenDTO(accessToken);
        }
        throw new SessionTimeoutException("Session expired");
    }

    private CredentialsDTO generateCredentials(User user) {
        String accessToken = accessTokenService.generateAccessToken(user);
        UUID refreshToken = refreshTokenService.generateRefreshToken();
        return CredentialsDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

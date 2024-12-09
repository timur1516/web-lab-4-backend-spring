package ru.timur.web4_back_spring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.timur.web4_back_spring.repository.UserRepository;
import ru.timur.web4_back_spring.dto.*;
import ru.timur.web4_back_spring.entity.Avatar;
import ru.timur.web4_back_spring.entity.RefreshToken;
import ru.timur.web4_back_spring.entity.Role;
import ru.timur.web4_back_spring.entity.User;
import ru.timur.web4_back_spring.exception.RefreshTokenNotFoundException;
import ru.timur.web4_back_spring.exception.RefreshTokenTimeoutException;
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

    public CredentialsDTO register(RegistrationDTO registrationDTO) throws UsernameExistsException {
        if (userRepository.findByUsername(registrationDTO.getUsername()).isPresent())
            throw new UsernameExistsException("User with username: " + registrationDTO.getUsername() + " already exists");
        User user = User.builder()
                .username(registrationDTO.getUsername())
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .firstName(registrationDTO.getFirstName())
                .lastName(registrationDTO.getLastName())
                .role(Role.USER)
                .build();
        Avatar avatar = Avatar.builder().user(user).build();
        user.setAvatar(avatar);
        userRepository.save(user);
        return generateCredentials(user);
    }

    public CredentialsDTO logIn(AuthenticationDTO authenticationDTO) throws AuthenticationException {
        authenticate(authenticationDTO.getUsername(), authenticationDTO.getPassword());
        User user = userRepository.findByUsername(authenticationDTO.getUsername()).orElse(null);
        return generateCredentials(user);
    }

    public void authenticate(String username, String password) throws AuthenticationException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }

    public void updatePassword(PasswordUpdateDTO passwordUpdateDTO) throws AuthenticationException {
        authenticate(passwordUpdateDTO.getUsername(), passwordUpdateDTO.getOldPassword());
        userRepository.updatePasswordByUsername(
                passwordEncoder.encode(passwordUpdateDTO.getNewPassword()),
                passwordUpdateDTO.getUsername());
    }

    public CredentialsDTO findGoodOrNew(User user) {
        User newUser = userRepository
                .getRandomUserWithDifferentId(user.getId())
                .orElse(null);
        if (newUser == null) {
            while (true) {
                String username = randomStringGenerator.generate(10);
                String password = randomStringGenerator.generate(20);
                String firstName = randomStringGenerator.generate(10);
                String lastName = randomStringGenerator.generate(10);
                try {
                    return register(new RegistrationDTO(username, password, firstName, lastName));
                } catch (Exception ignored) {
                }
            }
        }
        return generateCredentials(newUser);
    }

    public void logout(RefreshTokenDTO refreshTokenDTO) {
        refreshTokenService.removeToken(refreshTokenDTO.getToken());
    }

    public AccessTokenDTO getRefreshedToken(RefreshTokenDTO refreshTokenDTO) throws RefreshTokenTimeoutException, RefreshTokenNotFoundException {
        RefreshToken refreshToken = refreshTokenService.getRefreshToken(refreshTokenDTO.getToken());
        if (!refreshTokenService.isExpired(refreshToken)) {
            User user = refreshToken.getUser();
            String accessToken = accessTokenService.generateAccessToken(user);
            return new AccessTokenDTO(accessToken);
        }
        refreshTokenService.removeToken(refreshTokenDTO.getToken());
        throw new RefreshTokenTimeoutException("Token " + refreshTokenDTO.getToken().toString() + " is expired");
    }

    private CredentialsDTO generateCredentials(User user) {
        String accessToken = accessTokenService.generateAccessToken(user);
        UUID refreshToken = refreshTokenService.generateRefreshToken(user);
        return CredentialsDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

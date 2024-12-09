package ru.timur.web4_back_spring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.timur.web4_back_spring.dto.*;
import ru.timur.web4_back_spring.entity.User;
import ru.timur.web4_back_spring.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/get-points")
    public ResponseEntity<List<PointResponseDTO>> getPoints(@AuthenticationPrincipal User user) {
        List<PointResponseDTO> points = userService.getAllPoints(user);
        log.info("Successfully loaded points for user with username {}", user.getUsername());
        return ResponseEntity.ok(points);
    }

    @PostMapping("/check-point")
    public ResponseEntity<?> checkpoint(@Validated @RequestBody PointRequestDTO point, @AuthenticationPrincipal User user) {
        PointResponseDTO response = userService.addPoint(point, user);
        log.info("Successfully checked point for user with username {}", user.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@Validated @RequestBody ImageDTO imageDTO, @AuthenticationPrincipal User user) throws IllegalArgumentException {
        userService.updateAvatar(imageDTO, user);
        log.info("Successfully uploaded avatar for user with username {}", user.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("get-user-profile-data")
    public ResponseEntity<?> getUserProfileData(@AuthenticationPrincipal User user) {
        UserProfileDataDTO userProfileDataDTO = userService.getUserProfileData(user);
        log.info("Successfully got user profile data for user with username {}", user.getUsername());
        return ResponseEntity.ok(userProfileDataDTO);

    }

    @PostMapping("update-user-details")
    public ResponseEntity<?> updateUsername(@Validated @RequestBody UserDetailsDTO userDetailsDTO, @AuthenticationPrincipal User user) {
        userService.updateDetails(userDetailsDTO, user);
        log.info("Successfully updated user details for user with username {}", user.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove-points")
    public ResponseEntity<?> removePoints(@AuthenticationPrincipal User user) {
        userService.removePoints(user);
        log.info("Successfully removed points for user with username {}", user.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-user")
    public ResponseEntity<CredentialsDTO> changeUser(@AuthenticationPrincipal User user) {
        CredentialsDTO token = userService.findGoodOrNew(user);
        log.info("Successfully changed user for user with username: {}", user.getUsername());
        return ResponseEntity.ok(token);
    }
}

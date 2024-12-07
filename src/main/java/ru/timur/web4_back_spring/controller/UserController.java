package ru.timur.web4_back_spring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<List<PointResponseDTO>> getPoints() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Get points for {}", user.getLogin());
        List<PointResponseDTO> points = userService.getAllPoints(user);
        log.info("Got points for {}", user.getLogin());
        return ResponseEntity.ok(points);
    }

    @PostMapping("/check-point")
    public ResponseEntity<?> checkpoint(@Valid @RequestBody PointRequestDTO point) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Check point for {}", user.getLogin());
        PointResponseDTO response = userService.addPoint(point, user);
        log.info("Checked point for {}", user.getLogin());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@Valid @RequestBody ImageDTO imageDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Upload avatar for {}", user.getLogin());
        userService.updateAvatar(imageDTO, user);
        log.info("Uploaded avatar for {}", user.getLogin());
        return ResponseEntity.ok().build();
    }

    @GetMapping("get-user-profile-data")
    public ResponseEntity<?> getUserProfileData() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Get user profile data for {}", user.getLogin());
        UserProfileDataDTO userProfileDataDTO = userService.getUserProfileData(user);
        log.info("Got user profile data for {}", user.getLogin());
        return ResponseEntity.ok(userProfileDataDTO);

    }

    @PostMapping("update-username")
    public ResponseEntity<?> updateUsername(@Valid @RequestBody UsernameDTO usernameDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Update username for {}", user.getLogin());
        userService.updateUsername(usernameDTO.getUsername(), user);
        log.info("Updated username for {}", user.getLogin());
        return ResponseEntity.ok().build();
    }

    @PostMapping("update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Update password for {}", user.getLogin());
        userService.updatePassword(passwordDTO.getPassword(), user);
        log.info("Updated password for {}", user.getLogin());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove-points")
    public ResponseEntity<?> removePoints() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Remove points for {}", user.getLogin());
        userService.removePoints(user);
        log.info("Removed points for {}", user.getLogin());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-user")
    public ResponseEntity<CredentialsDTO> changeUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Changing user: {}", user);
        CredentialsDTO token = userService.findGoodOrNew(user);
        log.info("Successfully changed user with id: {}", user.getId());
        return ResponseEntity.ok(token);
    }
}

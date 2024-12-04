package ru.timur.web4_back_spring.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.timur.web4_back_spring.dto.*;
import ru.timur.web4_back_spring.exception.UserNotFoundException;
import ru.timur.web4_back_spring.service.UserService;
import ru.timur.web4_back_spring.util.UserPrincipals;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/main")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get-points")
    public ResponseEntity<List<PointResponseDTO>> getPoints() {
        UserPrincipals userPrincipals = (UserPrincipals) SecurityContextHolder.getContext().getAuthentication();
        List<PointResponseDTO> points = userService.getAllPoints(userPrincipals.getUserId());
        return ResponseEntity.ok(points);
    }

    @PostMapping("/check-point")
    public ResponseEntity<?> checkpoint(@Valid @RequestBody PointRequestDTO point) {
        UserPrincipals userPrincipals = (UserPrincipals) SecurityContextHolder.getContext().getAuthentication();
        try {
            PointResponseDTO response = userService.addPoint(point, userPrincipals.getUserId());
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@Valid @RequestBody ImageDTO imageDTO) {
        UserPrincipals userPrincipals = (UserPrincipals) SecurityContextHolder.getContext().getAuthentication();
        try {
            userService.updateAvatar(imageDTO, userPrincipals.getUserId());
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("get-user-profile-data")
    public ResponseEntity<?> getUserProfileData() {
        UserPrincipals userPrincipals = (UserPrincipals) SecurityContextHolder.getContext().getAuthentication();
        try {
            UserProfileDataDTO userProfileDataDTO = userService.getUserProfileData(userPrincipals.getUserId());
            return ResponseEntity.ok(userProfileDataDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("update-username")
    public ResponseEntity<?> updateUsername(@Valid @RequestBody UsernameDTO usernameDTO) {
        UserPrincipals userPrincipals = (UserPrincipals) SecurityContextHolder.getContext().getAuthentication();
        try {
            userService.updateUsername(usernameDTO.getUsername(), userPrincipals.getUserId());
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        UserPrincipals userPrincipals = (UserPrincipals) SecurityContextHolder.getContext().getAuthentication();
        try {
            userService.updatePassword(passwordDTO.getPassword(), userPrincipals.getUserId());
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/remove-points")
    public ResponseEntity<?> removePoints() {
        UserPrincipals userPrincipals = (UserPrincipals) SecurityContextHolder.getContext().getAuthentication();
        userService.removePoints(userPrincipals.getUserId());
        return ResponseEntity.ok().build();
    }
}

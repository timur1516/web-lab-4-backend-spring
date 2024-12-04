package ru.timur.web4_back_spring.service;

import ru.timur.web4_back_spring.dto.ImageDTO;
import ru.timur.web4_back_spring.dto.PointRequestDTO;
import ru.timur.web4_back_spring.dto.PointResponseDTO;
import ru.timur.web4_back_spring.dto.UserProfileDataDTO;
import ru.timur.web4_back_spring.exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    List<PointResponseDTO> getAllPoints(Long userId);

    PointResponseDTO addPoint(PointRequestDTO point, Long userId) throws UserNotFoundException;

    void updateAvatar(ImageDTO avatar, Long userId) throws UserNotFoundException;

    void updateUsername(String username, Long userId) throws UserNotFoundException;

    void updatePassword(String password, Long userId) throws UserNotFoundException;

    void removePoints(Long userId);

    UserProfileDataDTO getUserProfileData(Long userId) throws UserNotFoundException;
}

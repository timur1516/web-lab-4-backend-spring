package ru.timur.web4_back_spring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.timur.web4_back_spring.dao.AvatarRepository;
import ru.timur.web4_back_spring.dao.PointRepository;
import ru.timur.web4_back_spring.dao.UserRepository;
import ru.timur.web4_back_spring.dto.*;
import ru.timur.web4_back_spring.entity.Point;
import ru.timur.web4_back_spring.entity.User;

import java.util.Base64;
import java.util.List;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class UserService {
    private final PointRepository pointRepository;
    private final UserRepository userRepository;
    private final AvatarRepository avatarRepository;
    private final AuthenticationService authenticationService;
    private final AreaCheckService areaCheckService;

    public List<PointResponseDTO> getAllPoints(User user) {
        return pointRepository.findByUserId(user.getId())
                .stream()
                .map(PointResponseDTO::new)
                .toList();
    }

    public PointResponseDTO addPoint(PointRequestDTO point, User user) {
        PointResponseDTO response = areaCheckService.checkPoint(point);
        Point pointEntity = Point.builder()
                .x(response.getX())
                .y(response.getY())
                .r(response.getR())
                .hit(response.isHit())
                .reqTime(response.getReqTime())
                .procTime(response.getProcTime())
                .user(user)
                .build();
        this.pointRepository.save(pointEntity);
        return response;
    }

    public void updateAvatar(ImageDTO avatar, User user) {
        avatarRepository.updateAvatarByUserId(
                Base64.getDecoder().decode(avatar.getBase64()),
                avatar.getType(),
                user.getId());
    }

    public void removePoints(User user) {
        pointRepository.deleteByUserId(user.getId());
    }

    public UserProfileDataDTO getUserProfileData(User user) {
        ImageDTO avatar = null;
        if (user.getAvatar() != null && user.getAvatar().getBase64() != null) {
            avatar = ImageDTO.builder()
                    .base64(Base64.getEncoder().encodeToString(user.getAvatar().getBase64()))
                    .type(user.getAvatar().getImageType())
                    .build();
        }
        return UserProfileDataDTO
                .builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(avatar)
                .build();
    }

    public CredentialsDTO findGoodOrNew(User user) {
        return authenticationService.findGoodOrNew(user);
    }

    public void updateDetails(UserDetailsDTO userDetailsDTO, User user) {
        userRepository.updateDetailsById(userDetailsDTO.getFirstName(), userDetailsDTO.getLastName(), user.getId());
    }
}

package ru.timur.web4_back_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.timur.web4_back_spring.dao.PointRepository;
import ru.timur.web4_back_spring.dao.UserRepository;
import ru.timur.web4_back_spring.dto.*;
import ru.timur.web4_back_spring.entity.PointEntity;
import ru.timur.web4_back_spring.entity.User;
import ru.timur.web4_back_spring.util.AreaChecker;

import java.util.Base64;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {
    private final PointRepository pointRepository;
    private final AreaChecker areaChecker;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public List<PointResponseDTO> getAllPoints(User user) {
        return pointRepository.findByUserId(user.getId())
                .stream()
                .map(PointResponseDTO::new)
                .toList();
    }

    public PointResponseDTO addPoint(PointRequestDTO point, User user) {
        PointResponseDTO response = areaChecker.checkPoint(point);
        PointEntity pointEntity = PointEntity
                .builder()
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
        userRepository.updateAvatar(
                Base64.getDecoder().decode(avatar.getBase64()),
                avatar.getType(),
                user.getId());
    }

    public void updateUsername(String username, User user) {
        userRepository.updateUsername(username, user.getId());
    }

    public void updatePassword(String password, User user) {
        userRepository.updatePassword(password, user.getId());
    }

    public void removePoints(User user) {
        pointRepository.deleteByUserId(user.getId());
    }

    public UserProfileDataDTO getUserProfileData(User user) {
        return UserProfileDataDTO
                .builder()
                .username(user.getLogin())
                .avatar(user.getAvatar() != null ? Base64.getEncoder().encodeToString(user.getAvatar()) : null)
                .avatarType(user.getAvatarType())
                .build();
    }

    public CredentialsDTO findGoodOrNew(User user) {
        return authenticationService.findGoodOrNew(user);
    }
}

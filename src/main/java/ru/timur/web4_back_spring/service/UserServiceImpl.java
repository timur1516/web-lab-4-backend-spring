package ru.timur.web4_back_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.timur.web4_back_spring.dao.PointDAO;
import ru.timur.web4_back_spring.dao.UserDAO;
import ru.timur.web4_back_spring.dto.ImageDTO;
import ru.timur.web4_back_spring.dto.PointRequestDTO;
import ru.timur.web4_back_spring.dto.PointResponseDTO;
import ru.timur.web4_back_spring.dto.UserProfileDataDTO;
import ru.timur.web4_back_spring.entity.PointEntity;
import ru.timur.web4_back_spring.entity.UserEntity;
import ru.timur.web4_back_spring.exception.UserNotFoundException;
import ru.timur.web4_back_spring.util.AreaChecker;

import java.util.Base64;
import java.util.List;

@Transactional
@Service
public class UserServiceImpl implements UserService {
    private final PointDAO pointDAO;
    private final AreaChecker areaChecker;
    private final UserDAO userDAO;

    @Autowired
    public UserServiceImpl(PointDAO pointDAO, AreaChecker areaChecker, UserDAO userDAO) {
        this.pointDAO = pointDAO;
        this.areaChecker = areaChecker;
        this.userDAO = userDAO;
    }

    @Override
    public List<PointResponseDTO> getAllPoints(Long userId) {
        return pointDAO.findByUserId(userId)
                .stream()
                .map(PointResponseDTO::new)
                .toList();
    }

    @Override
    public PointResponseDTO addPoint(PointRequestDTO point, Long userId) throws UserNotFoundException {
        UserEntity user = userDAO
                .getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + "does not exists"));
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
        pointDAO.save(pointEntity);
        return response;
    }

    @Override
    public void updateAvatar(ImageDTO avatar, Long userId) {
        userDAO.updateAvatar(
                Base64.getDecoder().decode(avatar.getBase64()),
                avatar.getType(),
                userId);
    }

    @Override
    public void updateUsername(String username, Long userId) {
        userDAO.updateUsername(username, userId);
    }

    @Override
    public void updatePassword(String password, Long userId) {
        userDAO.updatePassword(password, userId);
    }

    @Override
    public void removePoints(Long userId) {
        pointDAO.deleteByUserId(userId);
    }

    @Override
    public UserProfileDataDTO getUserProfileData(Long userId) throws UserNotFoundException {
        UserEntity user = userDAO
                .getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + "does not exists"));
        return UserProfileDataDTO
                .builder()
                .username(user.getUsername())
                .avatar(user.getAvatar() != null ? Base64.getEncoder().encodeToString(user.getAvatar()) : null)
                .avatarType(user.getAvatarType())
                .build();
    }
}

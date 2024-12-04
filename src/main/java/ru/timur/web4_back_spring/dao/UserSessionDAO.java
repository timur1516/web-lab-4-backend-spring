package ru.timur.web4_back_spring.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.timur.web4_back_spring.entity.UserSessionEntity;

import java.util.Optional;

public interface UserSessionDAO extends JpaRepository<UserSessionEntity, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE UserSessionEntity u SET u.accessToken = :accessToken WHERE u.id = :id")
    void updateAccessToken(@Param("accessToken") String accessToken, @Param("id") Long id);

    void deleteByAccessToken(String accessToken);

    void deleteByRefreshToken(String refreshToken);

    Optional<UserSessionEntity> findByAccessToken(String accessToken);

    Optional<UserSessionEntity> findByRefreshToken(String refreshToken);
}

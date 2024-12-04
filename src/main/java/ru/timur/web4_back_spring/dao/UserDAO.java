package ru.timur.web4_back_spring.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.timur.web4_back_spring.entity.UserEntity;

import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<UserEntity, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.username = :username WHERE u.id = :id")
    void updateUsername(@Param("username") String username, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.password = :password WHERE u.id = :id")
    void updatePassword(@Param("password") String password, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.avatar = :avatar, u.avatarType = :avatarType WHERE u.id = :id")
    void updateAvatar(@Param("avatar") byte[] avatar, @Param("avatarType") String avatarType, @Param("id") Long id);

    @Transactional
    @Query("SELECT u FROM UserEntity u WHERE u.id != :id ORDER BY RANDOM() LIMIT 1")
    Optional<UserEntity> getRandomUserWithDifferentId(@Param("id") Long id);

    Optional<UserEntity> getUserById(Long userId);

    Optional<UserEntity> getUserByUsername(String username);
}

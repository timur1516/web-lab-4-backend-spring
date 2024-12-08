package ru.timur.web4_back_spring.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.timur.web4_back_spring.entity.Avatar;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Avatar a SET a.base64 = :base64, a.imageType = :imageType WHERE a.user.id = :userId")
    void updateAvatarByUserId(@Param("base64") byte[] base64, @Param("imageType") String imageType, @Param("userId") Long userId);
}

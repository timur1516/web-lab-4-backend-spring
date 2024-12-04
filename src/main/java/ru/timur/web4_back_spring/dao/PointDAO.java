package ru.timur.web4_back_spring.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.timur.web4_back_spring.entity.PointEntity;

import java.util.List;

@Repository
public interface PointDAO extends JpaRepository<PointEntity, Long> {
    List<PointEntity> findByUserId(Long userId);

    Long deleteByUserId(Long userId);
}

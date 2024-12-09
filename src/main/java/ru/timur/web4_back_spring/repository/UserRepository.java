package ru.timur.web4_back_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.timur.web4_back_spring.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.username = :username")
    void updatePasswordByUsername(@Param("password") String password, @Param("username") String username);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.firstName = :firstName, u.lastName = :lastName WHERE u.id = :id")
    void updateDetailsById(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("id") Long id);

    @Transactional
    @Query("SELECT u FROM User u WHERE u.id != :id ORDER BY RANDOM() LIMIT 1")
    Optional<User> getRandomUserWithDifferentId(@Param("id") Long id);

    @Transactional
    Optional<User> findByUsername(String username);
}

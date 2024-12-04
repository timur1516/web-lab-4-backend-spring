package ru.timur.web4_back_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "user_data")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(unique = true, length = 50)
    private String username;

    @NotNull
    @Column
    private String password;

    @NotNull
    @Column
    private String salt;

    @Lob
    @Column
    private byte[] avatar;

    @Column
    private String avatarType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<PointEntity> points;
}

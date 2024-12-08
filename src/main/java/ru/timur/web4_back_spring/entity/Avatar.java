package ru.timur.web4_back_spring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "avatar")
public class Avatar {
    @Id
    @GeneratedValue
    private Long id;

    @Lob
    @Column
    private byte[] base64;

    @Column
    private String imageType;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}

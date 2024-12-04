package ru.timur.web4_back_spring.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDataDTO implements Serializable {
    private String username;
    private String avatar;
    private String avatarType;
}

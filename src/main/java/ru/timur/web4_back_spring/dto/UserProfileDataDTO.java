package ru.timur.web4_back_spring.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDataDTO {
    private String username;
    private String firstName;
    private String lastName;
    private ImageDTO avatar;
}

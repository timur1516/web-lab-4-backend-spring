package ru.timur.web4_back_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CredentialsDTO implements Serializable {
    @NotNull(message = "Access token cannot be null")
    @NotBlank(message = "Access token cannot be empty")
    private String accessToken;

    @NotNull(message = "Refresh token cannot be null")
    @NotBlank(message = "Refresh token cannot be empty")
    private String refreshToken;
}

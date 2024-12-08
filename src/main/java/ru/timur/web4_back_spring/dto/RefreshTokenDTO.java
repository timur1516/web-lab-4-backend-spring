package ru.timur.web4_back_spring.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenDTO {
    @NotNull(message = "Refresh token cannot be null")
    private UUID token;
}

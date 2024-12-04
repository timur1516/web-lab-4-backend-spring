package ru.timur.web4_back_spring.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointRequestDTO implements Serializable {
    @NotNull(message = "X cannot be null")
    private double x;

    @NotNull(message = "Y cannot be null")
    private double y;

    @NotNull(message = "R cannot be null")
    @DecimalMin(value = "1", message = "R must be greater than or equal to 1")
    private double r;
}

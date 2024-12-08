package ru.timur.web4_back_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailsDTO {
    @NotNull(message = "FirstName cannot be null")
    @NotBlank(message = "FirstName cannot be empty")
    @Size(max = 45, message = "FirstName max size is 45 symbols")
    private String firstName;

    @NotNull(message = "LastName cannot be null")
    @NotBlank(message = "LastName cannot be empty")
    @Size(max = 45, message = "LastName max size is 45 symbols")
    private String lastName;
}

package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String email;
}
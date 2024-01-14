package com.example.pw_odwsi_project.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public record UserPasswordResetDTO(

        @NotNull
        @Size(max = 255, message = "Email must not have more than 255 characters")
        @NotBlank(message = "Email cannot be blank")
        String email

) {
}

package com.example.pw_odwsi_project.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public record UserPasswordChangeDTO(

        @NotNull
        @Size(max = 255, message = "Password must not have more than 255 characters")
        @NotBlank(message = "Password cannot be blank")
        String password,
        @NotNull
        @Size(max = 255, message = "PasswordToConfirm must not have more than 255 characters")
        @NotBlank(message = "PasswordToConfirm cannot be blank")
        String passwordToConfirm,
        @NotNull
        @Size(max = 255, message = "Token must not have more than 255 characters")
        @NotBlank(message = "Token cannot be blank")
        String token


) {
}

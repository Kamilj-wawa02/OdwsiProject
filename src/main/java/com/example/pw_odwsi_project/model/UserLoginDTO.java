package com.example.pw_odwsi_project.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public record UserLoginDTO(

        @NotNull
        @Size(max = 255, message = "Username must not have more than 255 characters")
        @NotBlank(message = "Username cannot be blank")
        String username,

        @NotNull
        @Size(max = 255, message = "Password must not have more than 255 characters")
        @NotBlank(message = "Password cannot be blank")
        String password,

        @NotNull
        @Size(max = 255, message = "Verification code must not have more than 255 characters")
        @NotBlank(message = "Verification code cannot be blank")
        String verificationCode

) {
}

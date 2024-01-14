package com.example.pw_odwsi_project.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public record UserRegistrationDTO(

        @NotNull
        @Size(max = 255)
        String username,

        @NotNull
        @Size(max = 255)
        String email,

        @NotNull
        @Size(max = 255)
        String password,

        @NotNull
        @Size(max = 255)
        String passwordToConfirm

) {
}

package com.example.pw_odwsi_project.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public record NotePasswordDTO(

        @NotBlank(message = "Password cannot be blank")
        @Size(max = 255, message = "Password must not have more than 255 characters")
        String password

) {
}

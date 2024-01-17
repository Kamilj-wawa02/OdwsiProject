package com.example.pw_odwsi_project.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoteDTO(

        @NotNull
        Long id,

        @NotBlank
        @Size(max = 255)
        String title,

        @Size(max = 10000)
        String content,

        @NotBlank
        @Size(max = 255)
        String authorUsername,

        @NotNull
        Boolean isEncrypted,

        @NotNull
        Boolean isPublic

) {
}

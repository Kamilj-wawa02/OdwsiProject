package com.example.pw_odwsi_project.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public record NoteEditorDTO(

        @Size(max = 255, message = "Title must not have more than 255 characters")
        @NotBlank(message = "Title cannot be blank")
        String title,

        @Size(max = 10000, message = "Content must not have more than 10000 characters")
        String content,

        @NotNull
        Boolean isEncrypted,

        @NotNull
        Boolean isPublic,

        @Size(max = 255, message = "Password must not have more than 255 characters")
        String password

) {
}

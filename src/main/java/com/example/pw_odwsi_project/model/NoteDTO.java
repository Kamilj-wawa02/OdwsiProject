package com.example.pw_odwsi_project.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public record NoteDTO(

        @NotNull
        Long id,

        @NotNull
        @Size(max = 255)
        String title,

        @Size(max = 10000)
        String content,

        @NotNull
        String authorUsername,

        @NotNull
        Boolean isEncrypted,

        @NotNull
        Boolean isPublic

) {
}

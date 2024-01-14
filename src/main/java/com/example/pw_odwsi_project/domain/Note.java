package com.example.pw_odwsi_project.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;


@Entity
@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED)
public class Note {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 10000)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private String content;

    @ManyToOne(optional = false)
    @EqualsAndHashCode.Exclude
    private User author;

    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Boolean isEncrypted;

    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Boolean isPublic;

    public Note(@NonNull User author, @NonNull String title, @NonNull String content, @NonNull Boolean isPublic, @NonNull Boolean isEncrypted) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.isEncrypted = isEncrypted;
    }

}

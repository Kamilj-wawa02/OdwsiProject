package com.example.pw_odwsi_project.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {

    private static final int EXPIRATION_MINS = 10;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false, length = 10000)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private LocalDateTime expirationDate;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    public boolean hasExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }

    public Token(@NonNull User user) {
        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.expirationDate = LocalDateTime.now().plusMinutes(EXPIRATION_MINS);
    }

}

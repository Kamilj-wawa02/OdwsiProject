package com.example.pw_odwsi_project.service;

import com.example.pw_odwsi_project.domain.Token;
import com.example.pw_odwsi_project.domain.User;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PasswordCheckService {

    public static final int PASSWORD_MIN_ENTROPY = 80;
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final Boolean PASSWORD_MUST_HAVE_SMALL_LETTER = true;
    public static final Boolean PASSWORD_MUST_HAVE_CAPITAL_LETTER = true;
    public static final Boolean PASSWORD_MUST_HAVE_SPECIAL_CHARACTER = true;

    private final EntropyService entropyService;

    public void checkPassword(@NotNull String password) {
        checkPasswordLength(password);
        if (PASSWORD_MUST_HAVE_SMALL_LETTER) {
            checkPasswordHasSmallLetter(password);
        }
        if (PASSWORD_MUST_HAVE_CAPITAL_LETTER) {
            checkPasswordHasCapitalLetter(password);
        }
        if (PASSWORD_MUST_HAVE_SPECIAL_CHARACTER) {
            checkPasswordHasSpecialCharacter(password);
        }
        checkPasswordEntropy(password);
    }

    public void checkPasswords(@NotNull String password1, @NotNull String password2) {
        if (!password1.equals(password2)) {
            throw new IllegalStateException("Passwords do not match.");
        }
        checkPassword(password1);
    }

    private void checkPasswordLength(@NotNull String password) {
        if (password.length() < PASSWORD_MIN_LENGTH) {
            throw new IllegalStateException("Password must be at least " + PASSWORD_MIN_LENGTH +" characters long.");
        }
    }

    private void checkPasswordEntropy(String password) {
        if (entropyService.calculatePasswordEntropy(password) < PASSWORD_MIN_ENTROPY) {
            throw new IllegalStateException("Provided password is too weak.");
        }
    }

    private void checkPasswordHasSmallLetter(String password) {
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalStateException("Provided password must have at least one small letter.");
        }
    }

    private void checkPasswordHasCapitalLetter(String password) {
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalStateException("Provided password must have at least one capital letter.");
        }
    }

    private void checkPasswordHasSpecialCharacter(String password) {
        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalStateException("Provided password must have at least one special character.");
        }
    }

}

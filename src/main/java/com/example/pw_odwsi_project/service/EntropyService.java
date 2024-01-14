package com.example.pw_odwsi_project.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class EntropyService {

    private final Pattern[] charSetPatterns = {
            Pattern.compile("[a-z]"),
            Pattern.compile("[A-Z]"),
            Pattern.compile("[0-9]"),
            Pattern.compile("[^a-zA-Z0-9]")
    };

    private final int[] charSetSizes = {26, 26, 10, 33};

    public int calculatePasswordEntropy(String password) {
        double entropy = 0;

        for (int i = 0; i < charSetPatterns.length; i++) {
            if (charSetPatterns[i].matcher(password).find()) {
                entropy += Math.log(charSetSizes[i]) / Math.log(2);
            }
        }

        return (int) Math.round(entropy * password.length());
    }

}

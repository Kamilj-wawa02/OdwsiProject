package com.example.pw_odwsi_project.service;

import com.example.pw_odwsi_project.domain.Token;
import com.example.pw_odwsi_project.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendLoggedInFromAnotherIp(User user, String newIp) {
//        System.out.println(">>> Email being sent, because logged from another device to user " + user.getEmail());

        final String currentTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
        final SimpleMailMessage email = buildEmail(user, "NotesManager - Logged in from another IP address",
                "Logged in from a different IP address (" + newIp + ") at " + currentTime +
                        ". If this wasn't you, please change your password immediately.");
        mailSender.send(email);
    }

    public void sendPasswordResetEmail(User user, Token token) {
//        System.out.println(">>> Email being sent with token " + token.getToken() + " to user " + user.getEmail());

        final var appBaseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        final var linkUrl = appBaseUrl + "/change-password?token=" + token.getToken();
        final SimpleMailMessage email = buildEmail(user, "NotesManager - Reset Password", "Reset your password via link: " + linkUrl);

        mailSender.send(email);
    }

    private SimpleMailMessage buildEmail(User user, String subject, String message) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("noreply@notesmanager.com");
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);
        return email;
    }

}

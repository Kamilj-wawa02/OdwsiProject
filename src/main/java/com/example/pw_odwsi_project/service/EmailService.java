package com.example.pw_odwsi_project.service;

import com.example.pw_odwsi_project.domain.Token;
import com.example.pw_odwsi_project.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(User user, Token token) {
        System.out.println(">>> Email sent with token: " + token.getToken() + " to user: " + user.getEmail());

        final var appBaseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        final var linkUrl = appBaseUrl + "/change-password?token=" + token.getToken();
        final SimpleMailMessage email = buildEmail(user, "Reset your password via link: " + linkUrl);

        mailSender.send(email);

        System.out.println(">>> Email has been sent!");
    }

    private SimpleMailMessage buildEmail(User user, String message) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("noreply@notesmanager.com");
        email.setTo(user.getEmail());
        email.setSubject("NotesManager - Reset Password");
        email.setText(message);
        return email;
    }

}

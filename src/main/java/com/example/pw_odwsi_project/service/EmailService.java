package com.example.pw_odwsi_project.service;

import com.example.pw_odwsi_project.domain.Token;
import com.example.pw_odwsi_project.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    public void sendPasswordResetEmail(User user, Token token) {
        System.out.println(">>> Email sent with token: " + token.getToken() + " to user: " + user.getEmail());
    }

}

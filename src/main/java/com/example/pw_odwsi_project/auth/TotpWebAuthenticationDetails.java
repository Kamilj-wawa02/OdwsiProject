package com.example.pw_odwsi_project.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Getter
public class TotpWebAuthenticationDetails extends WebAuthenticationDetails {

    private final String verificationCode;

    public TotpWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        verificationCode = request.getParameter("code");
    }

}
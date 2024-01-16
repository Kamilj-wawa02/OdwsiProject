package com.example.pw_odwsi_project.auth;

import com.example.pw_odwsi_project.auth.TotpWebAuthenticationDetails;
import com.example.pw_odwsi_project.domain.User;
import com.example.pw_odwsi_project.repos.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.github.benmanes.caffeine.cache.Cache;

@Component
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private static final int LOGIN_DELAY = 500;
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    public static  final String MAX_LOGIN_ATTEMPTS_MSG = "Reached maximum number of login attempts. Try again later.";

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final Cache<String, Integer> unsuccessfulLoginAttemptsCache;
    private final HttpServletRequest httpServletRequest;

    @PostConstruct
    private void initialize() {
        setPasswordEncoder(passwordEncoder);
        setUserDetailsService(userDetailsService);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("Login attempt #" + (getCurrentLoginAttempts() + 1) + " of " + authentication.getName());
        checkLoginAttempts();

        try {
            Thread.sleep(LOGIN_DELAY + (long) (Math.random() * 500));
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }

        try {
            return super.authenticate(authentication);
        } catch (AuthenticationException exception) {
            noteUnsuccessfulLoginAttempt();
            throw exception;
        }
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        final String ipAddress = httpServletRequest.getRemoteAddr();
        unsuccessfulLoginAttemptsCache.invalidate(ipAddress);

        return super.createSuccessAuthentication(principal, authentication, user);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        super.additionalAuthenticationChecks(userDetails, authentication);

        final String verificationCode = ((TotpWebAuthenticationDetails) authentication.getDetails()).getVerificationCode();
        final User user = userRepository.findByEmailIgnoreCase(authentication.getName());
        if (user == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        final Totp totp = new Totp(user.getSecret());
        if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
//            throw new BadCredentialsException("Invalid verfication code");
        }
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private int getCurrentLoginAttempts() {
        return unsuccessfulLoginAttemptsCache.get(httpServletRequest.getRemoteAddr(), key -> 0);
    }

    private void checkLoginAttempts() {
        final String ipAddress = httpServletRequest.getRemoteAddr();
        final int unsuccessfulLoginAttempts = unsuccessfulLoginAttemptsCache.get(ipAddress, key -> 0);
        if (unsuccessfulLoginAttempts >= MAX_LOGIN_ATTEMPTS) {
            log.warn("User at IP " + ipAddress + " performed login attempt that exceeds the maximum tries (" + MAX_LOGIN_ATTEMPTS + ").");
            throw new BadCredentialsException(MAX_LOGIN_ATTEMPTS_MSG);
        }
    }

    private void noteUnsuccessfulLoginAttempt() {
        unsuccessfulLoginAttemptsCache.put(httpServletRequest.getRemoteAddr(), getCurrentLoginAttempts() + 1);
    }


}
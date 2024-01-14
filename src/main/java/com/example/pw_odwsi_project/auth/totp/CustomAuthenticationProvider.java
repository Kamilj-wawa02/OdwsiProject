package com.example.pw_odwsi_project.auth.totp;

import com.example.pw_odwsi_project.domain.User;
import com.example.pw_odwsi_project.repos.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @PostConstruct
    private void init() {
        setPasswordEncoder(passwordEncoder);
        setUserDetailsService(userDetailsService);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // max attempts
        // delay

        try {
            return super.authenticate(authentication);
        } catch (AuthenticationException exception) {
            //incrementUnsuccessfulLoginAttempts();
            System.out.println("User did not authenticate: " + exception.getMessage());
            throw exception;
        }
//        Authentication result = super.authenticate(auth);
//        return new UsernamePasswordAuthenticationToken(
//                user, result.getCredentials(), result.getAuthorities());
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
//        clearUnsuccessfulLoginAttempts();
//        addLastSuccessfulLogin(user);
        return super.createSuccessAuthentication(principal, authentication, user);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        super.additionalAuthenticationChecks(userDetails, authentication);

        final String verificationCode = ((CustomWebAuthenticationDetails) authentication.getDetails()).getVerificationCode();
        final User user = userRepository.findByEmailIgnoreCase(authentication.getName());
        if (user == null) {
            System.out.println("User '" + authentication.getName() + "' not found");
            throw new BadCredentialsException("Invalid username or password");
        }

        final Totp totp = new Totp(user.getSecret());
        if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
            System.out.println("User did not provide a valid verification code");
//            throw new BadCredentialsException("Invalid verfication code");
        }

        System.out.println("User authenticated!");
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
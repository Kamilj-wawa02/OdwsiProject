package com.example.pw_odwsi_project.service;

import com.example.pw_odwsi_project.domain.Token;
import com.example.pw_odwsi_project.domain.User;
import com.example.pw_odwsi_project.model.UserPasswordResetDTO;
import com.example.pw_odwsi_project.model.UserRegistrationDTO;
import com.example.pw_odwsi_project.model.UserPasswordChangeDTO;
import com.example.pw_odwsi_project.repos.TokenRepository;
import com.example.pw_odwsi_project.repos.UserRepository;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    public static final String APP_NAME = "NotesManager";
    public static final String QR_URL_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static final String INVALID_TOKEN = "Token is invalid.";
    public static final String DIFFERENT_PASSWORDS = "Provided password is too weak.";
    public static final String TOO_WEAK_PASSWORD = "Provided password is too weak.";
    private final int PASSWORD_MIN_ENTROPY = 80;


    private final UserRepository userRepository;
    private final EntropyService entropyService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String email) {
        final User user = userRepository.findByEmailIgnoreCase(email);
        if (user == null) {
            throw new UsernameNotFoundException("User " + email + " not found.");
        }
        return user;
    }

    @Transactional
    public User createUser(final UserRegistrationDTO userRegistrationDTO) {
        if (userRepository.existsByUsernameIgnoreCase(userRegistrationDTO.username()) ||
                userRepository.existsByEmailIgnoreCase(userRegistrationDTO.email())) {
            throw new IllegalStateException("This username or email has already been used.");
        }
        if (!userRegistrationDTO.password().equals(userRegistrationDTO.passwordToConfirm())) {
            throw new IllegalStateException(DIFFERENT_PASSWORDS);
        }
        if (entropyService.calculatePasswordEntropy(userRegistrationDTO.password()) < PASSWORD_MIN_ENTROPY) {
            throw new IllegalStateException(TOO_WEAK_PASSWORD);
        }

        final String password = passwordEncoder.encode(userRegistrationDTO.password());
        final User user = new User(userRegistrationDTO.username(), userRegistrationDTO.email(), password);
        userRepository.save(user);

        return user;
    }

    public String generateUserQRUrl(User user) throws IOException, WriterException {
        final GoogleAuthenticatorKey key =
            new GoogleAuthenticatorKey.Builder(user.getSecret()).build();

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(APP_NAME, user.getUsername(), key);

        BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeBase64String(bytes);
    }

    @Transactional
    public void sendResetPasswordEmail(String email) {
        final User user = userRepository.findByEmailIgnoreCase(email);
        if (user == null) {
            throw new UsernameNotFoundException("Can't find user with this email (" + email + ").");
        }

        if (tokenRepository.existsByUserEmailIgnoreCase(email)) {
            final Token token = tokenRepository.findByUserEmail(email);
            emailService.sendPasswordResetEmail(user, token);
        } else {
            final Token token = new Token(user);
            emailService.sendPasswordResetEmail(user, token);
            tokenRepository.save(token);
        }
    }

    @Transactional
    public Boolean validateResetPasswordToken(String tokenIdentifier) {
        if (tokenIdentifier == null) {
            return false;
        }

        final Optional<Token> returnedToken = tokenRepository.findByToken(tokenIdentifier);
        if (returnedToken.isEmpty()) {
            return false;
        }

        final Token token = returnedToken.get();
        if (token.hasExpired()) {
            tokenRepository.delete(token);
            return false;
        }

        return true;
    }

    @Transactional
    public void changePassword(UserPasswordChangeDTO userPasswordChangeDTO) {
        if (validateResetPasswordToken(userPasswordChangeDTO.token())) {
            throw new IllegalStateException(INVALID_TOKEN);
        }
        final Token token = tokenRepository.findByToken(userPasswordChangeDTO.token())
                .orElseThrow(() -> new IllegalStateException(INVALID_TOKEN));

        if (!userPasswordChangeDTO.password().equals(userPasswordChangeDTO.passwordToConfirm())) {
            throw new IllegalStateException(DIFFERENT_PASSWORDS);
        }
        if (entropyService.calculatePasswordEntropy(userPasswordChangeDTO.password()) < PASSWORD_MIN_ENTROPY) {
            throw new IllegalStateException(TOO_WEAK_PASSWORD);
        }

        final User user = token.getUser();
        final String newPassword = passwordEncoder.encode(userPasswordChangeDTO.password());

        if (newPassword.equals(user.getPassword())) {
            throw new IllegalStateException("New password cannot be the same as the old one.");
        }

        user.setPassword(passwordEncoder.encode(userPasswordChangeDTO.password()));
        userRepository.save(user);
    }

}

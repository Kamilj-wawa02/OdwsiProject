package com.example.pw_odwsi_project.service;

import com.example.pw_odwsi_project.domain.User;
import com.example.pw_odwsi_project.model.UserRegistrationDTO;
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

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    public final String APP_NAME = "NotesManager";
    public final String QR_URL_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    private final int PASSWORD_MIN_ENTROPY = 80;


    private final UserRepository userRepository;
    private final EntropyService entropyService;
    private final PasswordEncoder passwordEncoder;
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
            throw new IllegalStateException("Provided passwords do not match.");
        }
        if (entropyService.calculatePasswordEntropy(userRegistrationDTO.password()) < PASSWORD_MIN_ENTROPY) {
            throw new IllegalStateException("Provided password is too weak.");
        }

        final String password = passwordEncoder.encode(userRegistrationDTO.password());
        final User user = new User(userRegistrationDTO.username(), userRegistrationDTO.email(), password);
        userRepository.save(user);

        return user;
    }


    private final GoogleAuthenticator gAuth;

    public String generateUserQRUrl(User user) throws IOException, WriterException {
        final GoogleAuthenticatorKey key = //gAuth.createCredentials(user.getEmail());
            new GoogleAuthenticatorKey.Builder(user.getSecret()).build();

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(APP_NAME, user.getUsername(), key);

        BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeBase64String(bytes);


        //I've decided to generate QRCode on backend site
        //QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // src="https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=
        // otpauth://totp/NotesManager:test1@test1.com?secret=FXDBTENTBMDVDDKH&issuer=NotesManager&algorithm=SHA1&digits=6&period=30"

        //return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(APP_NAME, user.getEmail(), key);

//        return QR_URL_PREFIX + URLEncoder.encode(String.format(
//                        "otpauth://totp/%s:%s?secret=%s&issuer=%s",
//                        "security", user.getEmail(), user.getSecret(), "security"),
//                StandardCharsets.UTF_8);
    }

}

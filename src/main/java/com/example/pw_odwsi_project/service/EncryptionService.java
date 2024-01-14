package com.example.pw_odwsi_project.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.PBEKeySpec;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class EncryptionService {

    private final SecureRandom secureRandom = new SecureRandom();
    private static final String ENCRYPTION_ALGORITHM_CONFIGURATION = "AES/CBC/PKCS5Padding";
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String KEY_GENERATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 16;
    private static final int KEY_ITERATION_COUNT = 1000;
    private static final int KEY_LENGTH = 256;

    public String encrypt(String content, String password) {
        try {
            final byte[] salt = generateBytes(SALT_LENGTH);
            final byte[] iv = generateBytes(IV_LENGTH);
            final Cipher cipher = initializeCipher(password, salt, iv, Cipher.ENCRYPT_MODE);

            final byte[] encryptedBytes = cipher.doFinal(content.getBytes());
            final byte[] encryptedBytesAddedSaltAndIV = new byte[encryptedBytes.length + SALT_LENGTH + IV_LENGTH];
            System.arraycopy(salt, 0, encryptedBytesAddedSaltAndIV, 0, SALT_LENGTH);
            System.arraycopy(iv, 0, encryptedBytesAddedSaltAndIV, SALT_LENGTH, IV_LENGTH);
            System.arraycopy(encryptedBytes, 0, encryptedBytesAddedSaltAndIV, SALT_LENGTH + IV_LENGTH, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(encryptedBytesAddedSaltAndIV);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalStateException("Encryption failed.", e);
        }
    }

    public String decrypt(String content, String password) {
        try {
            final byte[] encryptedBytesWithSaltAndIV = Base64.getDecoder().decode(content);
            final byte[] salt = readBytesFromString(encryptedBytesWithSaltAndIV, 0, SALT_LENGTH);
            final byte[] iv = readBytesFromString(encryptedBytesWithSaltAndIV, SALT_LENGTH, IV_LENGTH);
            final Cipher cipher = initializeCipher(password, salt, iv, Cipher.DECRYPT_MODE);

            final byte[] encryptedBytesRemovedSaltAndIV = new byte[encryptedBytesWithSaltAndIV.length - SALT_LENGTH - IV_LENGTH];
            System.arraycopy(encryptedBytesWithSaltAndIV, SALT_LENGTH + IV_LENGTH, encryptedBytesRemovedSaltAndIV, 0, encryptedBytesRemovedSaltAndIV.length);
            final var decryptedBytes = cipher.doFinal(encryptedBytesRemovedSaltAndIV);
            return new String(decryptedBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalStateException("Decryption failed.", e);
        }
    }

    private byte[] generateBytes(int length) {
        final byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

    private byte[] readBytesFromString(byte[] encryptedBytes, int start, int length) {
        final byte[] outputBytes = new byte[length];
        System.arraycopy(encryptedBytes, start, outputBytes, 0, length);
        return outputBytes;
    }

    private Cipher initializeCipher(String password, byte[] salt, byte[] iv, int mode) {
        try {
            final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_GENERATION_ALGORITHM);
            final PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, KEY_ITERATION_COUNT, KEY_LENGTH);
            final SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
            final var secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), ENCRYPTION_ALGORITHM);
            final Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM_CONFIGURATION);
            cipher.init(mode, secretKeySpec, new IvParameterSpec(iv));
            return cipher;
        } catch (Exception e) {
            throw new IllegalStateException("Cipher initialization failed.", e);
        }
    }

}

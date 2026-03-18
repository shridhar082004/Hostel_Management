package com.hostel.security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {

    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    public static String hash(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        return ITERATIONS + ":" + base64(salt) + ":" + base64(hash);
    }

    public static boolean verify(String password, String stored) {
        if (password == null || stored == null || stored.isBlank()) {
            return false;
        }
        String[] parts = stored.split(":");
        if (parts.length != 3) {
            return false;
        }
        int iterations;
        try {
            iterations = Integer.parseInt(parts[0]);
        } catch (NumberFormatException ex) {
            return false;
        }
        byte[] salt = fromBase64(parts[1]);
        byte[] expected = fromBase64(parts[2]);
        byte[] actual = pbkdf2(password.toCharArray(), salt, iterations, expected.length * 8);
        return MessageDigest.isEqual(expected, actual);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (Exception ex) {
            throw new IllegalStateException("Password hashing failed.", ex);
        }
    }

    private static String base64(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    private static byte[] fromBase64(String value) {
        return Base64.getDecoder().decode(value);
    }
}

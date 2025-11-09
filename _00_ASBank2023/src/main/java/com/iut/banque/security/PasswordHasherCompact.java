package com.iut.banque.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PasswordHasherCompact {

    private static final Logger logger = LoggerFactory.getLogger(PasswordHasherCompact.class);

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

    public static final int SALT_LENGTH = 6;
    public static final int ITERATIONS = 20000;
    public static final int KEY_LENGTH = 160;

    private PasswordHasherCompact() {}

    private static byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        sr.nextBytes(salt);
        return salt;
    }

    public static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        try {
            return skf.generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }

    public static String createHashString(char[] password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = generateSalt();
        byte[] hash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH);

        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hashB64 = Base64.getEncoder().encodeToString(hash);

        Arrays.fill(salt, (byte)0);
        Arrays.fill(hash, (byte)0);

        return String.format("%d:%s:%s", ITERATIONS, saltB64, hashB64);
    }

    public static boolean verifyPassword(char[] attemptedPassword, String stored)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (stored == null || stored.isEmpty()) return false;
        String[] parts = stored.split(":");
        if (parts.length != 3) return false;

        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] storedHash = Base64.getDecoder().decode(parts[2]);

        byte[] attemptedHash = pbkdf2(attemptedPassword, salt, iterations, storedHash.length * 8);

        boolean matches = MessageDigest.isEqual(storedHash, attemptedHash);

        Arrays.fill(salt, (byte)0);
        Arrays.fill(storedHash, (byte)0);
        Arrays.fill(attemptedHash, (byte)0);

        return matches;
    }

    // Petit utilitaire de test local (optionnel)
    public static void main(String[] args) throws Exception {
        char[] pw = "MonMotDePasse123!".toCharArray();
        String stored = createHashString(pw);
        logger.info("Stored ({} chars): {}", stored.length(), stored);
        Arrays.fill(pw, '\0');

        char[] attempt = "MonMotDePasse123!".toCharArray();
        logger.info("Verify: {}", verifyPassword(attempt, stored));
        Arrays.fill(attempt, '\0');
    }
}

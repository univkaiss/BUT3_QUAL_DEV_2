package com.iut.banque.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;

public final class PasswordHasherCompact {

    // Utilisation PBKDF2WithHmacSHA1 pour garder un hash compact
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

    // Paramètres choisis pour tenir dans VARCHAR(45)
    // - SALT_LENGTH = 6 bytes -> base64 8 chars
    // - KEY_LENGTH = 160 bits -> base64 28 chars
    // - ITERATIONS = 20000 (5 digits)
    public static final int SALT_LENGTH = 6;          // bytes
    public static final int ITERATIONS = 20000;
    public static final int KEY_LENGTH = 160;         // bits

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

    /**
     * Crée la chaîne compacte : iterations:salt_base64:hash_base64
     * Longueur typique (avec paramètres ci-dessus) ≈ 43 caractères.
     */
    public static String createHashString(char[] password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = generateSalt();
        byte[] hash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH);

        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hashB64 = Base64.getEncoder().encodeToString(hash);

        // Effacer données sensibles en mémoire
        Arrays.fill(salt, (byte)0);
        Arrays.fill(hash, (byte)0);

        return String.format("%d:%s:%s", ITERATIONS, saltB64, hashB64);
    }

    /**
     * Vérifie le mot de passe tenté contre la chaîne stockée.
     */
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

        // Effacer données sensibles
        Arrays.fill(salt, (byte)0);
        Arrays.fill(storedHash, (byte)0);
        Arrays.fill(attemptedHash, (byte)0);

        return matches;
    }

    // Petit utilitaire de test local (optionnel)
    public static void main(String[] args) throws Exception {
        char[] pw = "MonMotDePasse123!".toCharArray();
        String stored = createHashString(pw);
        System.out.println("Stored (" + stored.length() + " chars): " + stored);
        Arrays.fill(pw, '\0');

        char[] attempt = "MonMotDePasse123!".toCharArray();
        System.out.println("Verify: " + verifyPassword(attempt, stored));
        Arrays.fill(attempt, '\0');
    }
}

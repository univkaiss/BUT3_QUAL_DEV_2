package com.iut.banque.test.security;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.iut.banque.security.PasswordHasherCompact;

/**
 * Tests unitaires de la classe PasswordHasherCompact.
 */
@RunWith(Parameterized.class)
public class PasswordHasherCompactTest {

    private final String password;
    private final boolean shouldSucceed;

    public PasswordHasherCompactTest(String password, boolean shouldSucceed) {
        this.password = password;
        this.shouldSucceed = shouldSucceed;
    }

    /**
     * Données paramétrées pour les tests de hachage de mots de passe
     */
    @Parameters(name = "{index}: password=''{0}'' shouldSucceed={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // --- Tests avec différents types de mots de passe ---
                { "password123", true },
                { "MotDePasse!@#", true },
                { "SimplePass", true },
                { "12345678", true },
                { "a", true },
                { "UnMotDePasseTresLongAvecBeaucoupDeCaracteres123456789!@#$%", true },
                { "", true },
                { "Mot de passe avec espaces", true },
                { "パスワード", true }, // Caractères japonais
                { "🔐🔑🗝️", true } // Emojis
        });
    }

    // --- Tests paramétrés de création et vérification de hash ---
    @Test
    public void testCreateHashStringEtVerifyPassword() {
        try {
            char[] passwordChars = password.toCharArray();
            String hash = PasswordHasherCompact.createHashString(passwordChars);

            if (shouldSucceed) {
                assertNotNull("Le hash ne devrait pas être null", hash);
                assertFalse("Le hash ne devrait pas être vide", hash.isEmpty());

                // Vérifier que le mot de passe correspond
                char[] attemptChars = password.toCharArray();
                boolean verified = PasswordHasherCompact.verifyPassword(attemptChars, hash);
                assertTrue("La vérification devrait réussir pour le même mot de passe", verified);

                // Nettoyer les tableaux
                Arrays.fill(passwordChars, '\0');
                Arrays.fill(attemptChars, '\0');
            }

        } catch (Exception e) {
            if (shouldSucceed) {
                fail("Exception inattendue pour password='" + password + "' -> " + e.getMessage());
            }
        }
    }

    // --- Tests statiques (non paramétrés) ---

    @Test
    public void testCreateHashStringFormatCorrect() {
        try {
            char[] pwd = "TestPassword123".toCharArray();
            String hash = PasswordHasherCompact.createHashString(pwd);

            // Vérifier le format : iterations:salt:hash
            String[] parts = hash.split(":");
            assertEquals("Le hash devrait avoir 3 parties séparées par ':'", 3, parts.length);

            // Vérifier que la première partie est un nombre (iterations)
            int iterations = Integer.parseInt(parts[0]);
            assertEquals("Le nombre d'itérations devrait être " + PasswordHasherCompact.ITERATIONS,
                    PasswordHasherCompact.ITERATIONS, iterations);

            // Vérifier que les parties 2 et 3 sont en Base64
            Base64.getDecoder().decode(parts[1]); // salt
            Base64.getDecoder().decode(parts[2]); // hash

            Arrays.fill(pwd, '\0');

        } catch (NumberFormatException e) {
            fail("La première partie du hash devrait être un nombre");
        } catch (IllegalArgumentException e) {
            fail("Les parties salt et hash devraient être en Base64 valide");
        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testHashesDifferentsPourMemeMotDePasse() {
        try {
            char[] pwd = "SamePassword".toCharArray();

            String hash1 = PasswordHasherCompact.createHashString(pwd);
            String hash2 = PasswordHasherCompact.createHashString(pwd);

            assertNotEquals("Deux hashes du même mot de passe devraient être différents (salt différent)",
                    hash1, hash2);

            // Mais les deux devraient vérifier le même mot de passe
            assertTrue("hash1 devrait vérifier le mot de passe",
                    PasswordHasherCompact.verifyPassword(pwd, hash1));
            assertTrue("hash2 devrait vérifier le mot de passe",
                    PasswordHasherCompact.verifyPassword(pwd, hash2));

            Arrays.fill(pwd, '\0');

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testVerifyPasswordAvecMauvaisMotDePasse() {
        try {
            char[] correctPassword = "CorrectPassword123".toCharArray();
            String hash = PasswordHasherCompact.createHashString(correctPassword);

            char[] wrongPassword = "WrongPassword456".toCharArray();
            boolean verified = PasswordHasherCompact.verifyPassword(wrongPassword, hash);

            assertFalse("La vérification devrait échouer avec un mauvais mot de passe", verified);

            Arrays.fill(correctPassword, '\0');
            Arrays.fill(wrongPassword, '\0');

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }



    @Test
    public void testVerifyPasswordAvecHashVide() {
        try {
            char[] pwd = "TestPassword".toCharArray();
            boolean verified = PasswordHasherCompact.verifyPassword(pwd, "");

            assertFalse("La vérification devrait échouer avec un hash vide", verified);

            Arrays.fill(pwd, '\0');

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testVerifyPasswordAvecHashMalFormate() {
        try {
            char[] pwd = "TestPassword".toCharArray();

            // Hash avec seulement 2 parties au lieu de 3
            boolean verified1 = PasswordHasherCompact.verifyPassword(pwd, "20000:saltBase64");
            assertFalse("La vérification devrait échouer avec un hash mal formaté", verified1);

            // Hash avec 4 parties
            boolean verified2 = PasswordHasherCompact.verifyPassword(pwd, "20000:salt:hash:extra");
            assertFalse("La vérification devrait échouer avec trop de parties", verified2);

            Arrays.fill(pwd, '\0');

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testVerifyPasswordAvecIterationsInvalides() {
        try {
            char[] pwd = "TestPassword".toCharArray();

            // Hash avec iterations non numériques
            boolean verified = PasswordHasherCompact.verifyPassword(pwd, "invalid:c2FsdA==:aGFzaA==");
            assertFalse("La vérification devrait échouer avec des itérations invalides", verified);

            Arrays.fill(pwd, '\0');

        } catch (Exception e) {
            // Une exception NumberFormatException peut être levée, c'est acceptable
        }
    }

    @Test
    public void testVerifyPasswordAvecBase64Invalide() {
        try {
            char[] pwd = "TestPassword".toCharArray();

            // Hash avec salt Base64 invalide
            boolean verified = PasswordHasherCompact.verifyPassword(pwd, "20000:invalid!@#:aGFzaA==");
            assertFalse("La vérification devrait échouer avec un salt Base64 invalide", verified);

            Arrays.fill(pwd, '\0');

        } catch (Exception e) {
            // Une exception IllegalArgumentException peut être levée, c'est acceptable
        }
    }

    @Test
    public void testSaltLengthCorrect() {
        try {
            char[] pwd = "TestPassword".toCharArray();
            String hash = PasswordHasherCompact.createHashString(pwd);

            String[] parts = hash.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[1]);

            assertEquals("La longueur du salt devrait être " + PasswordHasherCompact.SALT_LENGTH,
                    PasswordHasherCompact.SALT_LENGTH, salt.length);

            Arrays.fill(pwd, '\0');
            Arrays.fill(salt, (byte)0);

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }



    @Test
    public void testPbkdf2Deterministe() {
        try {
            char[] pwd = "DeterministicTest".toCharArray();
            byte[] salt = new byte[] { 1, 2, 3, 4, 5, 6 };
            int iterations = 1000;
            int keyLength = 160;

            byte[] hash1 = PasswordHasherCompact.pbkdf2(pwd, salt, iterations, keyLength);
            byte[] hash2 = PasswordHasherCompact.pbkdf2(pwd, salt, iterations, keyLength);

            assertArrayEquals("Deux appels avec les mêmes paramètres devraient produire le même hash",
                    hash1, hash2);

            Arrays.fill(pwd, '\0');
            Arrays.fill(salt, (byte)0);
            Arrays.fill(hash1, (byte)0);
            Arrays.fill(hash2, (byte)0);

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testMotDePasseCaseSensitive() {
        try {
            char[] password1 = "Password".toCharArray();
            char[] password2 = "password".toCharArray();

            String hash = PasswordHasherCompact.createHashString(password1);

            boolean verified1 = PasswordHasherCompact.verifyPassword(password1, hash);
            boolean verified2 = PasswordHasherCompact.verifyPassword(password2, hash);

            assertTrue("Le mot de passe exact devrait être vérifié", verified1);
            assertFalse("Un mot de passe avec une casse différente ne devrait pas être vérifié", verified2);

            Arrays.fill(password1, '\0');
            Arrays.fill(password2, '\0');

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testMotDePasseAvecCaracteresSpeciaux() {
        try {
            char[] pwd = "P@ssw0rd!#$%&*()".toCharArray();
            String hash = PasswordHasherCompact.createHashString(pwd);

            boolean verified = PasswordHasherCompact.verifyPassword(pwd, hash);
            assertTrue("La vérification devrait réussir avec des caractères spéciaux", verified);

            Arrays.fill(pwd, '\0');

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testMotDePasseTresLong() {
        try {
            // Créer un mot de passe de 1000 caractères
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                sb.append("a");
            }
            char[] pwd = sb.toString().toCharArray();

            String hash = PasswordHasherCompact.createHashString(pwd);
            boolean verified = PasswordHasherCompact.verifyPassword(pwd, hash);

            assertTrue("La vérification devrait réussir avec un mot de passe très long", verified);

            Arrays.fill(pwd, '\0');

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testVerifyPasswordAvecModificationDuHash() {
        try {
            char[] pwd = "TestPassword".toCharArray();
            String hash = PasswordHasherCompact.createHashString(pwd);

            // Modifier légèrement le hash
            String modifiedHash = hash.substring(0, hash.length() - 1) + "X";

            boolean verified = PasswordHasherCompact.verifyPassword(pwd, modifiedHash);
            assertFalse("La vérification devrait échouer avec un hash modifié", verified);

            Arrays.fill(pwd, '\0');

        } catch (Exception e) {
            // Une exception peut être levée, c'est acceptable
        }
    }
}
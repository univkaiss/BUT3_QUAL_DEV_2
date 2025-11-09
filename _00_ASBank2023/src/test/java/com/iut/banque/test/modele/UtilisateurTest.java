package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.modele.Utilisateur;

/**
 * Tests unitaires de la classe Utilisateur.
 * Comme Utilisateur est abstraite, on utilise une classe concrète pour les tests.
 */
@RunWith(Parameterized.class)
public class UtilisateurTest {

    /**
     * Classe concrète pour tester Utilisateur (classe abstraite).
     */
    private static class UtilisateurConcret extends Utilisateur {
        public UtilisateurConcret(String nom, String prenom, String adresse, boolean male, String userId, String userPwd) {
            super(nom, prenom, adresse, male, userId, userPwd);
        }

        public UtilisateurConcret() {
            super();
        }

        @Override
        public String getIdentity() {
            return getUserId();
        }
    }

    // --- Paramètres pour les tests ---
    protected final String nom;
    protected final String prenom;
    protected final String adresse;
    protected final boolean male;
    protected final String userId;
    protected final String userPwd;

    public UtilisateurTest(String nom, String prenom, String adresse, boolean male, String userId, String userPwd) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.male = male;
        this.userId = userId;
        this.userPwd = userPwd;
    }

    /**
     * Données paramétrées pour les tests de création d'Utilisateur
     */
    @Parameters(name = "{index}: Utilisateur({0}, {1}, male={3}, userId=''{4}'')")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // --- Tests avec différentes combinaisons de paramètres ---
                { "Dupont", "Jean", "10 rue de la Paix", true, "j.dupont1", "password123" },
                { "Martin", "Marie", "25 avenue des Fleurs", false, "m.martin2", "pass456" },
                { "Durand", "Pierre", "5 boulevard Victor Hugo", true, "p.durand1", "pwd789" },
                { "Lefebvre", "Sophie", "30 rue du Commerce", false, "s.lefebvre1", "password" },
                { "Bernard", "Luc", "12 place de l'Église", true, "l.bernard1", "secure123" },
                { "Petit", "Anne", "8 chemin des Roses", false, "a.petit1", "mypass" },
                { "", "", "", true, "user1", "pwd" },
                { "Nom", "Prenom", "Adresse", false, "n.prenom1", "" }
        });
    }

    @Test
    public void testCreationUtilisateur() {
        try {
            Utilisateur u = new UtilisateurConcret(nom, prenom, adresse, male, userId, userPwd);

            // Vérifications des attributs
            assertNotNull("L'utilisateur ne devrait pas être null", u);
            assertEquals("Le nom ne correspond pas", nom, u.getNom());
            assertEquals("Le prénom ne correspond pas", prenom, u.getPrenom());
            assertEquals("L'adresse ne correspond pas", adresse, u.getAdresse());
            assertEquals("Le genre ne correspond pas", male, u.isMale());
            assertEquals("Le userId ne correspond pas", userId, u.getUserId());
            assertEquals("Le mot de passe ne correspond pas", userPwd, u.getUserPwd());

        } catch (Exception e) {
            fail("Exception inattendue : " + e.getMessage());
        }
    }

    // --- Tests des setters ---
    @Test
    public void testSetNom() {
        try {
            Utilisateur u = new UtilisateurConcret("Ancien", "Prenom", "Adresse", true, "a.prenom1", "pwd");
            u.setNom("Nouveau");
            assertEquals("Le nom devrait être modifié", "Nouveau", u.getNom());
        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testSetPrenom() {
        try {
            Utilisateur u = new UtilisateurConcret("Nom", "Ancien", "Adresse", true, "n.ancien1", "pwd");
            u.setPrenom("Nouveau");
            assertEquals("Le prénom devrait être modifié", "Nouveau", u.getPrenom());
        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testSetAdresse() {
        try {
            Utilisateur u = new UtilisateurConcret("Nom", "Prenom", "Ancienne adresse", true, "n.prenom1", "pwd");
            u.setAdresse("Nouvelle adresse");
            assertEquals("L'adresse devrait être modifiée", "Nouvelle adresse", u.getAdresse());
        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testSetMale() {
        try {
            Utilisateur u = new UtilisateurConcret("Nom", "Prenom", "Adresse", true, "n.prenom1", "pwd");
            u.setMale(false);
            assertFalse("Le genre devrait être modifié à false", u.isMale());

            u.setMale(true);
            assertTrue("Le genre devrait être modifié à true", u.isMale());
        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testSetUserPwd() {
        try {
            Utilisateur u = new UtilisateurConcret("Nom", "Prenom", "Adresse", true, "n.prenom1", "ancien");
            u.setUserPwd("nouveau");
            assertEquals("Le mot de passe devrait être modifié", "nouveau", u.getUserPwd());
        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    // --- Tests de setUserId ---
    @Test
    public void testSetUserIdAvecValeurValide() {
        try {
            Utilisateur u = new UtilisateurConcret("Nom", "Prenom", "Adresse", true, "n.prenom1", "pwd");
            u.setUserId("n.nouveau1");
            assertEquals("Le userId devrait être modifié", "n.nouveau1", u.getUserId());
        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testSetUserIdAvecValeurNull() {
        try {
            Utilisateur u = new UtilisateurConcret("Nom", "Prenom", "Adresse", true, "n.prenom1", "pwd");
            u.setUserId(null);
            fail("Une exception IllegalFormatException aurait dû être levée pour userId null");
        } catch (IllegalFormatException e) {
            // Exception attendue
            assertEquals("Le message d'erreur ne correspond pas", "UserId invalide", e.getMessage());
        } catch (Exception e) {
            fail("Exception inattendue de type " + e.getClass().getName() + " : " + e.getMessage());
        }
    }

    // --- Tests du constructeur sans paramètre ---
    @Test
    public void testConstructeurSansParametre() {
        try {
            Utilisateur u = new UtilisateurConcret();
            assertNotNull("L'utilisateur créé avec le constructeur par défaut ne devrait pas être null", u);

            // Vérifier que les attributs sont à leur valeur par défaut
            assertNull("Le nom devrait être null", u.getNom());
            assertNull("Le prénom devrait être null", u.getPrenom());
            assertNull("L'adresse devrait être null", u.getAdresse());
            assertNull("Le userId devrait être null", u.getUserId());
            assertNull("Le mot de passe devrait être null", u.getUserPwd());
            assertFalse("Le genre devrait être false par défaut", u.isMale());
        } catch (Exception e) {
            fail("Le constructeur sans paramètre ne devrait pas lever d'exception -> " + e.getMessage());
        }
    }

    // --- Tests de la méthode toString ---
    @Test
    public void testMethodeToString() {
        try {
            Utilisateur u = new UtilisateurConcret("Fournier", "Claire", "18 rue Principale", false, "c.fournier1", "secret");
            String result = u.toString();

            assertNotNull("toString() ne devrait pas retourner null", result);

            // Vérifier que la chaîne contient les informations importantes
            if (!result.contains("Utilisateur")) {
                fail("toString() devrait contenir 'Utilisateur'");
            }
            if (!result.contains("Fournier")) {
                fail("toString() devrait contenir le nom");
            }
            if (!result.contains("Claire")) {
                fail("toString() devrait contenir le prénom");
            }
            if (!result.contains("c.fournier1")) {
                fail("toString() devrait contenir le userId");
            }
            if (!result.contains("18 rue Principale")) {
                fail("toString() devrait contenir l'adresse");
            }
        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testMethodeToStringAvecValeursVides() {
        try {
            Utilisateur u = new UtilisateurConcret("", "", "", true, "user1", "");
            String result = u.toString();
            assertNotNull("toString() ne devrait pas retourner null même avec des valeurs vides", result);
        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    // --- Tests de la méthode abstraite getIdentity ---
    @Test
    public void testMethodeGetIdentity() {
        try {
            Utilisateur u = new UtilisateurConcret("Moreau", "Julie", "7 impasse du Parc", false, "j.moreau1", "pwd");
            assertEquals("getIdentity() devrait retourner le userId", "j.moreau1", u.getIdentity());
        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    // --- Tests de vérification de la cohérence des getters et setters ---
    @Test
    public void testCoherenceGettersSetters() {
        try {
            Utilisateur u = new UtilisateurConcret();

            // Test nom
            u.setNom("TestNom");
            assertEquals("Incohérence entre setter et getter pour le nom", "TestNom", u.getNom());

            // Test prenom
            u.setPrenom("TestPrenom");
            assertEquals("Incohérence entre setter et getter pour le prénom", "TestPrenom", u.getPrenom());

            // Test adresse
            u.setAdresse("TestAdresse");
            assertEquals("Incohérence entre setter et getter pour l'adresse", "TestAdresse", u.getAdresse());

            // Test male
            u.setMale(true);
            assertTrue("Incohérence entre setter et getter pour male", u.isMale());

            // Test userId
            u.setUserId("test.user1");
            assertEquals("Incohérence entre setter et getter pour userId", "test.user1", u.getUserId());

            // Test userPwd
            u.setUserPwd("TestPassword");
            assertEquals("Incohérence entre setter et getter pour userPwd", "TestPassword", u.getUserPwd());

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    // --- Tests avec valeurs limites ---
    @Test
    public void testAvecChainesVides() {
        try {
            Utilisateur u = new UtilisateurConcret("", "", "", true, "u1", "");

            assertEquals("Le nom vide devrait être accepté", "", u.getNom());
            assertEquals("Le prénom vide devrait être accepté", "", u.getPrenom());
            assertEquals("L'adresse vide devrait être acceptée", "", u.getAdresse());
            assertEquals("Le mot de passe vide devrait être accepté", "", u.getUserPwd());

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testAvecChainesLongues() {
        try {
            // Créer une chaîne longue compatible Java 8
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 500; i++) {
                sb.append("a");
            }
            String longString = sb.toString();

            Utilisateur u = new UtilisateurConcret(longString, longString, longString, false, "u.test1", longString);

            assertEquals("Le nom long devrait être accepté", longString, u.getNom());
            assertEquals("Le prénom long devrait être accepté", longString, u.getPrenom());
            assertEquals("L'adresse longue devrait être acceptée", longString, u.getAdresse());
            assertEquals("Le mot de passe long devrait être accepté", longString, u.getUserPwd());

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }
}
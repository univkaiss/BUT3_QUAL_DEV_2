
package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.iut.banque.modele.Gestionnaire;

/**
 * Tests unitaires de la classe Gestionnaire.
 */
@RunWith(Parameterized.class)
public class GestionnaireTest {

    // --- Paramètres pour les tests ---
    protected String nom;
    protected String prenom;
    protected String adresse;
    protected boolean homme;
    protected String userId;
    protected String userPwd;
    protected boolean shouldThrowException;

    public GestionnaireTest(String nom, String prenom, String adresse, boolean homme,
                             String userId, String userPwd, boolean shouldThrowException) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.homme = homme;
        this.userId = userId;
        this.userPwd = userPwd;
        this.shouldThrowException = shouldThrowException;
    }

    /**
     * Données paramétrées pour les tests de création de Gestionnaire
     */
    @Parameters(name = "{index}: Gestionnaire({0}, {1}, userId=''{4}'') exception={6}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // --- Tests avec userId valide ---
                { "Dupont", "Jean", "10 rue de la Paix", true, "j.dupont1", "password123", false },
                { "Martin", "Marie", "25 avenue des Fleurs", false, "m.martin2", "pass456", false },
                { "Durand", "Pierre", "5 boulevard Victor Hugo", true, "p.durand1", "pwd789", false },

                // --- Tests avec userId vide (doit lever une exception) ---
                { "Lefebvre", "Sophie", "30 rue du Commerce", false, "", "password", true },

                // --- Tests avec autres valeurs valides ---
                { "Bernard", "Luc", "12 place de l'Église", true, "l.bernard1", "secure123", false },
                { "Petit", "Anne", "8 chemin des Roses", false, "a.petit1", "mypass", false }
        });
    }

    @Test
    public void testCreationGestionnaire() {
        try {
            Gestionnaire g = new Gestionnaire(nom, prenom, adresse, homme, userId, userPwd);

            if (shouldThrowException) {
                fail("Une exception IllegalArgumentException aurait dû être levée pour userId vide");
            }

            // Vérifications des attributs
            assertNotNull("Le gestionnaire ne devrait pas être null", g);
            assertEquals("Le nom ne correspond pas", nom, g.getNom());
            assertEquals("Le prénom ne correspond pas", prenom, g.getPrenom());
            assertEquals("L'adresse ne correspond pas", adresse, g.getAdresse());
            assertEquals("Le genre ne correspond pas", homme, g.isMale());
            assertEquals("Le userId ne correspond pas", userId, g.getUserId());
            assertEquals("Le mot de passe ne correspond pas", userPwd, g.getUserPwd());

        } catch (IllegalArgumentException e) {
            if (!shouldThrowException) {
                fail("Exception inattendue : " + e.getMessage());
            }
            // Exception attendue pour userId vide
            assertEquals("Le message d'erreur ne correspond pas",
                    "L'identifiant ne peut être vide.", e.getMessage());
        } catch (Exception e) {
            fail("Exception inattendue de type " + e.getClass().getName() + " : " + e.getMessage());
        }
    }

    // --- Tests de la méthode getIdentity ---
    @Test
    public void testMethodeGetIdentityRetourneUserId() {
        try {
            Gestionnaire g = new Gestionnaire("Dubois", "Marc", "15 rue Neuve", true, "m.dubois1", "pass");
            assertEquals("getIdentity() devrait retourner le userId", "m.dubois1", g.getIdentity());
        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testMethodeGetIdentityAvecDifferentsUserIds() {
        try {
            Gestionnaire g1 = new Gestionnaire("Moreau", "Julie", "7 impasse du Parc", false, "j.moreau1", "pwd1");
            Gestionnaire g2 = new Gestionnaire("Laurent", "Thomas", "33 avenue de la Gare", true, "t.laurent99", "pwd2");

            assertEquals("getIdentity() devrait retourner le userId du premier gestionnaire",
                    "j.moreau1", g1.getIdentity());
            assertEquals("getIdentity() devrait retourner le userId du second gestionnaire",
                    "t.laurent99", g2.getIdentity());
        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    // --- Tests du constructeur sans paramètre ---
    @Test
    public void testConstructeurSansParametre() {
        try {
            Gestionnaire g = new Gestionnaire();
            assertNotNull("Le gestionnaire créé avec le constructeur par défaut ne devrait pas être null", g);
        } catch (Exception e) {
            fail("Le constructeur sans paramètre ne devrait pas lever d'exception -> " + e.getMessage());
        }
    }

    // --- Tests de la méthode toString ---
    @Test
    public void testMethodeToString() {
        try {
            Gestionnaire g = new Gestionnaire("Fournier", "Claire", "18 rue Principale", false, "c.fournier1", "secret");
            String result = g.toString();

            assertNotNull("toString() ne devrait pas retourner null", result);

            // Vérifier que la chaîne contient les informations importantes
            if (!result.contains("Gestionnaire")) {
                fail("toString() devrait contenir 'Gestionnaire'");
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
        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }
}
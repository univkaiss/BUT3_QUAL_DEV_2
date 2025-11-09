package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.modele.Banque;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Gestionnaire;

/**
 * Tests unitaires de la classe Banque.
 */
public class BanqueTest {

    private Banque banque;
    private Client client1;
    private Client client2;
    private Gestionnaire gestionnaire1;
    private Gestionnaire gestionnaire2;
    private Compte compte1;
    private Compte compte2;

    @Before
    public void setUp() throws Exception {
        banque = new Banque();

        // Création de clients de test
        client1 = new Client("Dupont", "Jean", "10 rue de la Paix", true, "j.dupont1", "pass123", "1234567890");
        client2 = new Client("Martin", "Marie", "25 avenue des Fleurs", false, "m.martin1", "pass456", "0987654321");

        // Création de gestionnaires de test
        gestionnaire1 = new Gestionnaire("Durand", "Pierre", "5 boulevard Hugo", true, "p.durand1", "pwd789");
        gestionnaire2 = new Gestionnaire("Bernard", "Sophie", "30 rue Commerce", false, "s.bernard1", "pwd012");

        // Création de comptes de test
        compte1 = new CompteSansDecouvert("FR1234567890", 1000.0, client1);
        compte2 = new CompteAvecDecouvert("FR0987654321", 500.0, 200.0, client2);
    }

    // --- Tests du constructeur ---
    @Test
    public void testConstructeurParDefaut() {
        try {
            Banque b = new Banque();
            assertNotNull("La banque ne devrait pas être null", b);
        } catch (Exception e) {
            fail("Le constructeur ne devrait pas lever d'exception -> " + e.getMessage());
        }
    }

    // --- Tests des getters et setters pour clients ---
    @Test
    public void testSetEtGetClients() {
        try {
            Map<String, Client> clients = new HashMap<>();
            clients.put(client1.getUserId(), client1);
            clients.put(client2.getUserId(), client2);

            banque.setClients(clients);

            assertNotNull("La map de clients ne devrait pas être null", banque.getClients());
            assertEquals("La map devrait contenir 2 clients", 2, banque.getClients().size());
            assertTrue("La map devrait contenir client1", banque.getClients().containsKey("j.dupont1"));
            assertTrue("La map devrait contenir client2", banque.getClients().containsKey("m.martin1"));
            assertEquals("Le client récupéré devrait être client1", client1, banque.getClients().get("j.dupont1"));

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testSetClientsAvecMapVide() {
        try {
            Map<String, Client> clients = new HashMap<>();
            banque.setClients(clients);

            assertNotNull("La map de clients ne devrait pas être null", banque.getClients());
            assertEquals("La map devrait être vide", 0, banque.getClients().size());

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    // --- Tests des getters et setters pour gestionnaires ---
    @Test
    public void testSetEtGetGestionnaires() {
        try {
            Map<String, Gestionnaire> gestionnaires = new HashMap<>();
            gestionnaires.put(gestionnaire1.getUserId(), gestionnaire1);
            gestionnaires.put(gestionnaire2.getUserId(), gestionnaire2);

            banque.setGestionnaires(gestionnaires);

            assertNotNull("La map de gestionnaires ne devrait pas être null", banque.getGestionnaires());
            assertEquals("La map devrait contenir 2 gestionnaires", 2, banque.getGestionnaires().size());
            assertTrue("La map devrait contenir gestionnaire1", banque.getGestionnaires().containsKey("p.durand1"));
            assertTrue("La map devrait contenir gestionnaire2", banque.getGestionnaires().containsKey("s.bernard1"));

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testSetGestionnairesAvecMapVide() {
        try {
            Map<String, Gestionnaire> gestionnaires = new HashMap<>();
            banque.setGestionnaires(gestionnaires);

            assertNotNull("La map de gestionnaires ne devrait pas être null", banque.getGestionnaires());
            assertEquals("La map devrait être vide", 0, banque.getGestionnaires().size());

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    // --- Tests des getters et setters pour comptes ---
    @Test
    public void testSetEtGetAccounts() {
        try {
            Map<String, Compte> accounts = new HashMap<>();
            accounts.put(compte1.getNumeroCompte(), compte1);
            accounts.put(compte2.getNumeroCompte(), compte2);

            banque.setAccounts(accounts);

            assertNotNull("La map de comptes ne devrait pas être null", banque.getAccounts());
            assertEquals("La map devrait contenir 2 comptes", 2, banque.getAccounts().size());
            assertTrue("La map devrait contenir compte1", banque.getAccounts().containsKey("FR1234567890"));
            assertTrue("La map devrait contenir compte2", banque.getAccounts().containsKey("FR0987654321"));

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testSetAccountsAvecMapVide() {
        try {
            Map<String, Compte> accounts = new HashMap<>();
            banque.setAccounts(accounts);

            assertNotNull("La map de comptes ne devrait pas être null", banque.getAccounts());
            assertEquals("La map devrait être vide", 0, banque.getAccounts().size());

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    // --- Tests de la méthode debiter ---
    @Test
    public void testDebiterAvecMontantValide() throws IllegalFormatException, InsufficientFundsException {
        CompteSansDecouvert compte = new CompteSansDecouvert("FR1111111111", 1000.0, client1);
        banque.debiter(compte, 300.0);

        assertEquals("Le solde devrait être de 700.0", 700.0, compte.getSolde(), 0.01);
    }

    @Test(expected = IllegalFormatException.class)
    public void testDebiterAvecMontantNegatif() throws IllegalFormatException, InsufficientFundsException {
        CompteSansDecouvert compte = new CompteSansDecouvert("FR1111111111", 1000.0, client1);
        banque.debiter(compte, -100.0);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testDebiterAvecSoldeInsuffisant() throws IllegalFormatException, InsufficientFundsException {
        CompteSansDecouvert compte = new CompteSansDecouvert("FR1111111111", 1000.0, client1);
        banque.debiter(compte, 1500.0);
    }

    @Test
    public void testDebiterCompteAvecDecouvert() throws IllegalFormatException, InsufficientFundsException, IllegalOperationException {
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR2222222222", 500.0, 200.0, client1);
        banque.debiter(compte, 600.0);

        assertEquals("Le solde devrait être de -100.0", -100.0, compte.getSolde(), 0.01);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testDebiterCompteAvecDecouvertDepassantLaLimite() throws IllegalFormatException, InsufficientFundsException, IllegalOperationException {
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR2222222222", 500.0, 200.0, client1);
        banque.debiter(compte, 800.0);
    }

    // --- Tests de la méthode crediter ---
    @Test
    public void testCrediterAvecMontantValide() throws IllegalFormatException {
        CompteSansDecouvert compte = new CompteSansDecouvert("FR1111111111", 1000.0, client1);
        banque.crediter(compte, 500.0);

        assertEquals("Le solde devrait être de 1500.0", 1500.0, compte.getSolde(), 0.01);
    }

    @Test(expected = IllegalFormatException.class)
    public void testCrediterAvecMontantNegatif() throws IllegalFormatException {
        CompteSansDecouvert compte = new CompteSansDecouvert("FR1111111111", 1000.0, client1);
        banque.crediter(compte, -100.0);
    }

    @Test
    public void testCrediterCompteAvecDecouvert() throws IllegalFormatException, IllegalOperationException {
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR2222222222", -100.0, 200.0, client1);
        banque.crediter(compte, 150.0);

        assertEquals("Le solde devrait être de 50.0", 50.0, compte.getSolde(), 0.01);
    }

    // --- Tests de la méthode deleteUser ---
    @Test
    public void testDeleteUserExistant() {
        try {
            Map<String, Client> clients = new HashMap<>();
            clients.put(client1.getUserId(), client1);
            clients.put(client2.getUserId(), client2);
            banque.setClients(clients);

            assertEquals("La map devrait contenir 2 clients", 2, banque.getClients().size());

            banque.deleteUser("j.dupont1");

            assertEquals("La map devrait contenir 1 client après suppression", 1, banque.getClients().size());
            assertFalse("Le client ne devrait plus être dans la map", banque.getClients().containsKey("j.dupont1"));
            assertTrue("Le client2 devrait toujours être dans la map", banque.getClients().containsKey("m.martin1"));

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testDeleteUserNonExistant() {
        try {
            Map<String, Client> clients = new HashMap<>();
            clients.put(client1.getUserId(), client1);
            banque.setClients(clients);

            assertEquals("La map devrait contenir 1 client", 1, banque.getClients().size());

            banque.deleteUser("utilisateur.inexistant1");

            assertEquals("La map devrait toujours contenir 1 client", 1, banque.getClients().size());

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    @Test
    public void testDeleteUserDansMapVide() {
        try {
            Map<String, Client> clients = new HashMap<>();
            banque.setClients(clients);

            banque.deleteUser("j.dupont1");

            assertEquals("La map devrait rester vide", 0, banque.getClients().size());

        } catch (Exception e) {
            fail("Exception récupérée -> " + e.getMessage());
        }
    }

    // --- Tests de la méthode changeDecouvert ---
    @Test
    public void testChangeDecouvertAvecValeurValide() throws IllegalFormatException, IllegalOperationException {
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR3333333333", 500.0, 200.0, client1);

        banque.changeDecouvert(compte, 500.0);

        assertEquals("Le découvert autorisé devrait être de 500.0", 500.0, compte.getDecouvertAutorise(), 0.01);
    }

    @Test(expected = IllegalFormatException.class)
    public void testChangeDecouvertAvecValeurNegative() throws IllegalFormatException, IllegalOperationException {
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR3333333333", 500.0, 200.0, client1);
        banque.changeDecouvert(compte, -100.0);
    }

    @Test(expected = IllegalOperationException.class)
    public void testChangeDecouvertAvecSoldeNegatifSuperieurAuNouveauDecouvert() throws IllegalFormatException, IllegalOperationException {
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR3333333333", -150.0, 200.0, client1);
        banque.changeDecouvert(compte, 100.0);
    }

    @Test
    public void testChangeDecouvertAvecSoldeNegatifInferieurAuNouveauDecouvert() throws IllegalFormatException, IllegalOperationException {
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR3333333333", -50.0, 200.0, client1);

        banque.changeDecouvert(compte, 100.0);

        assertEquals("Le découvert autorisé devrait être modifié à 100.0", 100.0, compte.getDecouvertAutorise(), 0.01);
    }

    @Test
    public void testChangeDecouvertAvecSoldePositif() throws IllegalFormatException, IllegalOperationException {
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR3333333333", 500.0, 200.0, client1);

        banque.changeDecouvert(compte, 50.0);

        assertEquals("Le découvert autorisé devrait être modifié à 50.0", 50.0, compte.getDecouvertAutorise(), 0.01);
    }

    @Test
    public void testChangeDecouvertAZero() throws IllegalFormatException, IllegalOperationException {
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR3333333333", 100.0, 200.0, client1);

        banque.changeDecouvert(compte, 0.0);

        assertEquals("Le découvert autorisé devrait être 0.0", 0.0, compte.getDecouvertAutorise(), 0.01);
    }
}
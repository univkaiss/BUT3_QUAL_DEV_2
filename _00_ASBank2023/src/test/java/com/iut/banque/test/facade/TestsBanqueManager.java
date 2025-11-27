package com.iut.banque.test.facade;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.facade.BanqueManager;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Gestionnaire;

@RunWith(MockitoJUnitRunner.class)
public class TestsBanqueManager {

    @Mock
    private IDao dao;

    @InjectMocks
    private BanqueManager bm;

    @Before
    public void setUp() {
        // Pas besoin d'initialisation complexe, @InjectMocks s'occupe d'injecter la DAO dans le BanqueManager
    }

    @Test
    public void TestCreationDunClient() {
        try {
            // On simule une liste vide de clients pour que la vérification d'unicité passe
            when(dao.getAllClients()).thenReturn(new HashMap<>());

            // On charge la liste (simulée) dans le modèle interne de la banque
            bm.loadAllClients();

            bm.createClient("t.test1", "password", "test1nom", "test1prenom", "test town", true, "4242424242");

            // CORRECTION : L'ordre des types correspond maintenant à IDao.createUser
            // (String nom, String prenom, String adresse, boolean male, String usrId, String usrPwd, boolean manager, String numClient)
            verify(dao).createUser(
                    anyString(), // nom
                    anyString(), // prenom
                    anyString(), // adresse
                    anyBoolean(), // male (CORRIGÉ: c'était anyString())
                    anyString(), // usrId
                    anyString(), // usrPwd (CORRIGÉ: c'était anyBoolean())
                    eq(false),   // manager
                    anyString()  // numClient
            );
        } catch (Exception e) {
            fail("Aucune exception ne devrait être levée : " + e.getMessage());
        }
    }

    @Test(expected = IllegalOperationException.class)
    public void TestCreationDunClientAvecDeuxNumerosDeCompteIdentiques() throws Exception {
        // On prépare un client existant avec le numéro "0101010101"
        Map<String, Client> clients = new HashMap<>();
        Client existing = mock(Client.class);
        when(existing.getNumeroClient()).thenReturn("0101010101");
        clients.put("id1", existing);

        when(dao.getAllClients()).thenReturn(clients);

        bm.loadAllClients();

        // Ceci doit échouer car le numéro de client existe déjà
        bm.createClient("t.test1", "password", "test1nom", "test1prenom", "test town", true, "0101010101");
    }

    @Test
    public void TestSuppressionDunCompteAvecDecouvertAvecSoldeZero() {
        try {
            CompteAvecDecouvert compte = mock(CompteAvecDecouvert.class);
            when(compte.getSolde()).thenReturn(0.0);
            when(dao.getAccountById("CADV000000")).thenReturn(compte);

            bm.deleteAccount(bm.getAccountById("CADV000000"));

            verify(dao).deleteAccount(compte);
        } catch (Exception e) {
            fail("Aucune exception ne devrait être levée : " + e.getMessage());
        }
    }

    @Test(expected = IllegalOperationException.class)
    public void TestSuppressionDunCompteAvecDecouvertAvecSoldeDifferentDeZero() throws Exception {
        CompteAvecDecouvert compte = mock(CompteAvecDecouvert.class);
        when(compte.getSolde()).thenReturn(10.0); // Solde non nul
        when(dao.getAccountById("CADNV00000")).thenReturn(compte);

        bm.deleteAccount(bm.getAccountById("CADNV00000"));
    }

    @Test
    public void TestSuppressionDunCompteSansDecouvertAvecSoldeZero() {
        try {
            CompteSansDecouvert compte = mock(CompteSansDecouvert.class);
            when(compte.getSolde()).thenReturn(0.0);
            when(dao.getAccountById("CSDV000000")).thenReturn(compte);

            bm.deleteAccount(bm.getAccountById("CSDV000000"));

            verify(dao).deleteAccount(compte);
        } catch (Exception e) {
            fail("Aucune exception ne devrait être levée");
        }
    }

    @Test(expected = IllegalOperationException.class)
    public void TestSuppressionDunCompteSansDecouvertAvecSoldeDifferentDeZero() throws Exception {
        CompteSansDecouvert compte = mock(CompteSansDecouvert.class);
        when(compte.getSolde()).thenReturn(50.0);
        when(dao.getAccountById("CSDNV00000")).thenReturn(compte);

        bm.deleteAccount(bm.getAccountById("CSDNV00000"));
    }

    @Test
    public void TestSuppressionDunUtilisateurSansCompte() {
        try {
            Client client = mock(Client.class);
            when(client.getUserId()).thenReturn("g.pasdecompte");
            // Client sans compte (map vide)
            when(client.getAccounts()).thenReturn(new HashMap<>());

            when(dao.getUserById("g.pasdecompte")).thenReturn(client);
            // Simuler le chargement des clients
            when(dao.getAllClients()).thenReturn(new HashMap<>());
            bm.loadAllClients();

            bm.deleteUser(bm.getUserById("g.pasdecompte"));

            verify(dao).deleteUser(client);
        } catch (Exception e) {
            fail("Aucune exception ne devrait être levée : " + e.getMessage());
        }
    }

    @Test(expected = IllegalOperationException.class)
    public void TestSuppressionDuDernierManagerDeLaBaseDeDonnees() throws Exception {
        // On simule qu'il n'y a qu'un seul gestionnaire
        Map<String, Gestionnaire> managers = new HashMap<>();
        Gestionnaire admin = mock(Gestionnaire.class);
        when(admin.getUserId()).thenReturn("admin");
        managers.put("admin", admin);

        when(dao.getAllGestionnaires()).thenReturn(managers);
        when(dao.getUserById("admin")).thenReturn(admin);

        bm.loadAllGestionnaires();

        bm.deleteUser(bm.getUserById("admin"));
    }

    @Test
    public void TestSuppressionDunClientAvecComptesDeSoldeZero() {
        try {
            Client client = mock(Client.class);
            when(client.getUserId()).thenReturn("client1");

            // Créer des comptes à solde 0
            CompteSansDecouvert c1 = mock(CompteSansDecouvert.class);
            when(c1.getSolde()).thenReturn(0.0);
            CompteAvecDecouvert c2 = mock(CompteAvecDecouvert.class);
            when(c2.getSolde()).thenReturn(0.0);

            Map<String, com.iut.banque.modele.Compte> comptes = new HashMap<>();
            comptes.put("C1", c1);
            comptes.put("C2", c2);

            when(client.getAccounts()).thenReturn(comptes);
            when(dao.getUserById("client1")).thenReturn(client);
            when(dao.getAllClients()).thenReturn(new HashMap<>());

            bm.loadAllClients();
            bm.deleteUser(bm.getUserById("client1"));

            // Vérifie qu'on supprime bien les comptes PUIS l'utilisateur
            verify(dao).deleteAccount(c1);
            verify(dao).deleteAccount(c2);
            verify(dao).deleteUser(client);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Aucune exception ne devrait être levée");
        }
    }

    @Test(expected = IllegalOperationException.class)
    public void TestSuppressionDunClientAvecUnCompteDeSoldePositif() throws Exception {
        Client client = mock(Client.class);
        CompteSansDecouvert c1 = mock(CompteSansDecouvert.class);
        when(c1.getSolde()).thenReturn(100.0); // Solde positif

        Map<String, com.iut.banque.modele.Compte> comptes = new HashMap<>();
        comptes.put("C1", c1);
        when(client.getAccounts()).thenReturn(comptes);

        when(dao.getUserById("client1")).thenReturn(client);

        bm.deleteUser(bm.getUserById("client1"));
    }

    @Test(expected = IllegalOperationException.class)
    public void TestSuppressionDunClientAvecUnCompteAvecDecouvertDeSoldeNegatif() throws Exception {
        Client client = mock(Client.class);
        CompteAvecDecouvert c1 = mock(CompteAvecDecouvert.class);
        when(c1.getSolde()).thenReturn(-50.0); // Solde négatif

        Map<String, com.iut.banque.modele.Compte> comptes = new HashMap<>();
        comptes.put("C1", c1);
        when(client.getAccounts()).thenReturn(comptes);

        when(dao.getUserById("client1")).thenReturn(client);

        bm.deleteUser(bm.getUserById("client1"));
    }
}
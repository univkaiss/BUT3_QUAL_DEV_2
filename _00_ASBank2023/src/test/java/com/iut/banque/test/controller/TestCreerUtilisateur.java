package com.iut.banque.test.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;

public class TestCreerUtilisateur {

    @Mock
    private BanqueFacade banqueFacade;

    private AutoCloseable mocks;
    private TestableCreerUtilisateur action;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        action = new TestableCreerUtilisateur(banqueFacade);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void creationUtilisateur_client_success() throws Exception {
        action.setUserId("user1");
        action.setNom("Nom");
        action.setPrenom("Prenom");
        action.setAdresse("Addr");
        action.setUserPwd("pwd");
        action.setClient(true);
        action.setMale(true);
        action.setNumClient("NUM123");

        String res = action.creationUtilisateur();

        assertEquals("SUCCESS", res);
        assertEquals("L'utilisateur 'user1' a bien été créé.", action.getMessage());
        verify(banqueFacade).createClient(anyString(), anyString(), eq("Nom"), eq("Prenom"), eq("Addr"), eq(true), eq("NUM123"));
    }

    @Test
    public void creationUtilisateur_manager_success() throws Exception {
        action.setUserId("mgr1");
        action.setNom("MNom");
        action.setPrenom("MPrenom");
        action.setAdresse("MAddr");
        action.setUserPwd("pwd2");
        action.setClient(false);
        action.setMale(false);

        String res = action.creationUtilisateur();

        assertEquals("SUCCESS", res);
        assertEquals("L'utilisateur 'mgr1' a bien été créé.", action.getMessage());
        verify(banqueFacade).createManager(anyString(), anyString(), eq("MNom"), eq("MPrenom"), eq("MAddr"), eq(false));
    }

    @Test
    public void creationUtilisateur_whenIllegalOperationException_setsMessageAndError() throws Exception {
        doThrow(new IllegalOperationException("id exists")).when(banqueFacade).createClient(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());

        action.setUserId("dup");
        action.setUserPwd("pwd");
        action.setClient(true);
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(true);
        action.setNumClient("C");

        String res = action.creationUtilisateur();

        assertEquals("ERROR", res);
        assertEquals("L'identifiant a déjà été assigné à un autre utilisateur de la banque.", action.getMessage());
    }

    @Test
    public void creationUtilisateur_whenTechnicalException_setsMessageAndError() throws Exception {
        doThrow(new TechnicalException("num client dup")).when(banqueFacade).createClient(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());

        action.setUserId("u");
        action.setUserPwd("pwd");
        action.setClient(true);
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(true);
        action.setNumClient("NUM");

        String res = action.creationUtilisateur();

        assertEquals("ERROR", res);
        assertEquals("Le numéro de client est déjà assigné à un autre client.", action.getMessage());
    }

    @Test
    public void creationUtilisateur_whenIllegalArgument_setsMessageAndError() throws Exception {
        doThrow(new IllegalArgumentException("bad id")).when(banqueFacade).createManager(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean());

        action.setUserId("bad!");
        action.setUserPwd("pwd");
        action.setClient(false);
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(false);

        String res = action.creationUtilisateur();

        assertEquals("ERROR", res);
        assertEquals("Le format de l'identifiant est incorrect.", action.getMessage());
    }

    @Test
    public void creationUtilisateur_whenIllegalFormat_setsMessageAndError() throws Exception {
        doThrow(new IllegalFormatException("bad format")).when(banqueFacade).createClient(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());

        action.setUserId("u2");
        action.setUserPwd("pwd");
        action.setClient(true);
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(true);
        action.setNumClient("BAD_FMT");

        String res = action.creationUtilisateur();

        assertEquals("ERROR", res);
        assertEquals("Format du numéro de client incorrect.", action.getMessage());
    }

    private static class TestableCreerUtilisateur {
        private final BanqueFacade banqueFacade;
        private String userId;
        private String userPwd;
        private boolean client;
        private String nom;
        private String prenom;
        private String adresse;
        private boolean male;
        private String numClient;
        private String message;

        public TestableCreerUtilisateur(BanqueFacade banqueFacade) {
            this.banqueFacade = banqueFacade;
        }

        public String creationUtilisateur() {
            try {
                if (client) {
                    banqueFacade.createClient(userId, userPwd, nom, prenom, adresse, male, numClient);
                } else {
                    banqueFacade.createManager(userId, userPwd, nom, prenom, adresse, male);
                }
                message = "L'utilisateur '" + userId + "' a bien été créé.";
                return "SUCCESS";
            } catch (IllegalOperationException e) {
                message = "L'identifiant a déjà été assigné à un autre utilisateur de la banque.";
                return "ERROR";
            } catch (TechnicalException e) {
                message = "Le numéro de client est déjà assigné à un autre client.";
                return "ERROR";
            } catch (IllegalArgumentException e) {
                message = "Le format de l'identifiant est incorrect.";
                return "ERROR";
            } catch (IllegalFormatException e) {
                message = "Format du numéro de client incorrect.";
                return "ERROR";
            }
        }

        public void setUserId(String userId) { this.userId = userId; }
        public void setUserPwd(String userPwd) { this.userPwd = userPwd; }
        public void setClient(boolean client) { this.client = client; }
        public void setNom(String nom) { this.nom = nom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }
        public void setAdresse(String adresse) { this.adresse = adresse; }
        public void setMale(boolean male) { this.male = male; }
        public void setNumClient(String numClient) { this.numClient = numClient; }
        public String getMessage() { return message; }
    }
}

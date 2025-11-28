package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.controller.CreerUtilisateur;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;

public class TestCreerUtilisateur {

    @Mock
    private BanqueFacade banqueFacade;

    private AutoCloseable mocks;
    // Utilisation de la vraie classe
    private CreerUtilisateur action;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        // Injection via le constructeur de test
        action = new CreerUtilisateur(banqueFacade);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    // ========== Tests Getters/Setters ==========

    @Test
    public void gettersAndSetters_allFields_shouldWorkCorrectly() {
        action.setUserId("testUser");
        action.setUserPwd("testPwd");
        action.setClient(true);
        action.setNom("TestNom");
        action.setPrenom("TestPrenom");
        action.setAdresse("TestAddr");
        action.setMale(true);
        action.setNumClient("C999");

        assertEquals("testUser", action.getUserId());
        assertEquals("testPwd", action.getUserPwd());
        assertTrue(action.isClient());
        assertEquals("TestNom", action.getNom());
        assertEquals("TestPrenom", action.getPrenom());
        assertEquals("TestAddr", action.getAdresse());
        assertTrue(action.isMale());
        assertEquals("C999", action.getNumClient());
    }

    @Test
    public void setUserId_nullValue_shouldStoreNull() {
        action.setUserId(null);
        assertNull(action.getUserId());
    }

    @Test
    public void setUserPwd_emptyString_shouldStoreEmpty() {
        action.setUserPwd("");
        assertEquals("", action.getUserPwd());
    }

    @Test
    public void setClient_false_shouldStoreFalse() {
        action.setClient(false);
        assertFalse(action.isClient());
    }

    @Test
    public void setMale_false_shouldStoreFalse() {
        action.setMale(false);
        assertFalse(action.isMale());
    }

    @Test
    public void getMessage_initiallyNull() {
        assertNull(action.getMessage());
    }

    // ========== Tests création CLIENT - SUCCESS ==========

    @Test
    public void creationUtilisateur_client_success_male() throws Exception {
        action.setUserId("user1");
        action.setNom("Nom");
        action.setPrenom("Prenom");
        action.setAdresse("Addr");
        action.setUserPwd("pwd");
        action.setClient(true);
        action.setMale(true);
        action.setNumClient("NUM123");

        String res = action.execute();

        assertEquals("SUCCESS", res);
        assertEquals("L'utilisateur 'user1' a bien été créé.", action.getMessage());
        verify(banqueFacade).createClient("user1", "pwd", "Nom", "Prenom", "Addr", true, "NUM123");
    }

    @Test
    public void creationUtilisateur_client_success_female() throws Exception {
        action.setUserId("user2");
        action.setNom("Martin");
        action.setPrenom("Sophie");
        action.setAdresse("Rue B");
        action.setUserPwd("pass123");
        action.setClient(true);
        action.setMale(false);
        action.setNumClient("C456");

        String res = action.execute();

        assertEquals("SUCCESS", res);
        assertEquals("L'utilisateur 'user2' a bien été créé.", action.getMessage());
        verify(banqueFacade).createClient("user2", "pass123", "Martin", "Sophie", "Rue B", false, "C456");
    }

    @Test
    public void creationUtilisateur_client_withEmptyFields() throws Exception {
        action.setUserId("");
        action.setNom("");
        action.setPrenom("");
        action.setAdresse("");
        action.setUserPwd("");
        action.setClient(true);
        action.setMale(false);
        action.setNumClient("");

        String res = action.execute();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).createClient("", "", "", "", "", false, "");
    }

    @Test
    public void creationUtilisateur_client_withSpecialCharacters() throws Exception {
        action.setUserId("user@123");
        action.setNom("Nom-Composé");
        action.setPrenom("Jean-Pierre");
        action.setAdresse("12 Rue d'Été");
        action.setUserPwd("p@ssw0rd!");
        action.setClient(true);
        action.setMale(true);
        action.setNumClient("C-001");

        String res = action.execute();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).createClient("user@123", "p@ssw0rd!", "Nom-Composé", "Jean-Pierre", "12 Rue d'Été", true, "C-001");
    }

    // ========== Tests création MANAGER - SUCCESS ==========

    @Test
    public void creationUtilisateur_manager_success_male() throws Exception {
        action.setUserId("mgr1");
        action.setNom("MNom");
        action.setPrenom("MPrenom");
        action.setAdresse("MAddr");
        action.setUserPwd("pwd2");
        action.setClient(false);
        action.setMale(true);

        String res = action.execute();

        assertEquals("SUCCESS", res);
        assertEquals("L'utilisateur 'mgr1' a bien été créé.", action.getMessage());
        verify(banqueFacade).createManager("mgr1", "pwd2", "MNom", "MPrenom", "MAddr", true);
        verify(banqueFacade, never()).createClient(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());
    }

    @Test
    public void creationUtilisateur_manager_success_female() throws Exception {
        action.setUserId("admin2");
        action.setNom("Durand");
        action.setPrenom("Marie");
        action.setAdresse("Avenue X");
        action.setUserPwd("secure");
        action.setClient(false);
        action.setMale(false);

        String res = action.execute();

        assertEquals("SUCCESS", res);
        assertEquals("L'utilisateur 'admin2' a bien été créé.", action.getMessage());
        verify(banqueFacade).createManager("admin2", "secure", "Durand", "Marie", "Avenue X", false);
    }

    @Test
    public void creationUtilisateur_manager_withEmptyFields() throws Exception {
        action.setUserId("");
        action.setNom("");
        action.setPrenom("");
        action.setAdresse("");
        action.setUserPwd("");
        action.setClient(false);
        action.setMale(true);

        String res = action.execute();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).createManager("", "", "", "", "", true);
    }

    // ========== Tests exceptions CLIENT - IllegalOperationException ==========

    @Test
    public void creationUtilisateur_client_whenIllegalOperation_shouldSetErrorMessage() throws Exception {
        doThrow(new IllegalOperationException("id exists")).when(banqueFacade)
                .createClient(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());

        action.setUserId("dup");
        action.setUserPwd("pwd");
        action.setClient(true);
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(true);
        action.setNumClient("C");

        String res = action.execute();

        assertEquals("ERROR", res);
        assertEquals("L'identifiant a déjà été assigné à un autre utilisateur de la banque.", action.getMessage());
        verify(banqueFacade).createClient("dup", "pwd", "n", "p", "a", true, "C");
    }

    // ========== Tests exceptions CLIENT - TechnicalException ==========

    @Test
    public void creationUtilisateur_client_whenTechnicalException_shouldSetErrorMessage() throws Exception {
        doThrow(new TechnicalException("num client dup")).when(banqueFacade)
                .createClient(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());

        action.setUserId("u");
        action.setUserPwd("pwd");
        action.setClient(true);
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(true);
        action.setNumClient("NUM");

        String res = action.execute();

        assertEquals("ERROR", res);
        assertEquals("Le numéro de client est déjà assigné à un autre client.", action.getMessage());
    }

    // ========== Tests exceptions CLIENT - IllegalArgumentException ==========

    @Test
    public void creationUtilisateur_client_whenIllegalArgument_shouldSetErrorMessage() throws Exception {
        doThrow(new IllegalArgumentException("bad id")).when(banqueFacade)
                .createClient(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());

        action.setUserId("bad!");
        action.setUserPwd("pwd");
        action.setClient(true);
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(true);
        action.setNumClient("C");

        String res = action.execute();

        assertEquals("ERROR", res);
        assertEquals("Le format de l'identifiant est incorrect.", action.getMessage());
    }

    // ========== Tests exceptions CLIENT - IllegalFormatException ==========

    @Test
    public void creationUtilisateur_client_whenIllegalFormat_shouldSetErrorMessage() throws Exception {
        doThrow(new IllegalFormatException("bad format")).when(banqueFacade)
                .createClient(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());

        action.setUserId("u2");
        action.setUserPwd("pwd");
        action.setClient(true);
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(true);
        action.setNumClient("BAD_FMT");

        String res = action.execute();

        assertEquals("ERROR", res);
        assertEquals("Format du numéro de client incorrect.", action.getMessage());
    }

    // ========== Tests exceptions MANAGER - IllegalOperationException ==========

    @Test
    public void creationUtilisateur_manager_whenIllegalOperation_shouldSetErrorMessage() throws Exception {
        doAnswer(invocation -> {
            throw new IllegalOperationException("id exists");
        }).when(banqueFacade).createManager(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean());

        action.setUserId("dupMgr");
        action.setUserPwd("pwd");
        action.setClient(false);
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(false);

        String res = action.execute();

        assertEquals("ERROR", res);
        assertEquals("L'identifiant a déjà été assigné à un autre utilisateur de la banque.", action.getMessage());
    }

    // ========== Tests exceptions MANAGER - TechnicalException ==========

    @Test
    public void creationUtilisateur_manager_whenTechnicalException_shouldSetErrorMessage() throws Exception {
        doThrow(new TechnicalException("tech error")).when(banqueFacade)
                .createManager(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean());

        action.setUserId("techMgr");
        action.setUserPwd("pwd");
        action.setClient(false);
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(true);

        String res = action.execute();

        assertEquals("ERROR", res);
        assertEquals("Le numéro de client est déjà assigné à un autre client.", action.getMessage());
    }

    // ========== Tests exceptions MANAGER - IllegalArgumentException ==========

    @Test
    public void creationUtilisateur_manager_whenIllegalArgument_shouldSetErrorMessage() throws Exception {
        doThrow(new IllegalArgumentException("bad id")).when(banqueFacade)
                .createManager(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean());

        action.setUserId("bad!");
        action.setUserPwd("pwd");
        action.setClient(false);
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(false);

        String res = action.execute();

        assertEquals("ERROR", res);
        assertEquals("Le format de l'identifiant est incorrect.", action.getMessage());
    }

    // ========== Tests exceptions MANAGER - IllegalFormatException ==========

    @Test
    public void creationUtilisateur_manager_whenIllegalFormat_shouldSetErrorMessage() throws Exception {
        doThrow(new IllegalFormatException("format error")).when(banqueFacade)
                .createManager(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean());

        action.setUserId("fmtMgr");
        action.setUserPwd("pwd");
        action.setClient(false);
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(false);

        String res = action.execute();

        assertEquals("ERROR", res);
        assertEquals("Format du numéro de client incorrect.", action.getMessage());
    }

    // ========== Tests cas limites ==========

    @Test
    public void creationUtilisateur_client_withNullUserId_shouldCallFacade() throws Exception {
        action.setUserId(null);
        action.setClient(true);
        action.setUserPwd("pwd");
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(true);
        action.setNumClient("C");

        String res = action.execute();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).createClient(null, "pwd", "n", "p", "a", true, "C");
    }

    @Test
    public void creationUtilisateur_manager_withNullUserId_shouldCallFacade() throws Exception {
        action.setUserId(null);
        action.setClient(false);
        action.setUserPwd("pwd");
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(false);

        String res = action.execute();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).createManager(null, "pwd", "n", "p", "a", false);
    }

    @Test
    public void creationUtilisateur_multipleCalls_shouldPreserveLastMessage() {
        action.setUserId("user1");
        action.setClient(true);
        action.setUserPwd("pwd");
        action.setNom("n");
        action.setPrenom("p");
        action.setAdresse("a");
        action.setMale(true);
        action.setNumClient("C1");
        action.execute();

        action.setUserId("user2");
        action.setNumClient("C2");
        action.execute();

        assertEquals("L'utilisateur 'user2' a bien été créé.", action.getMessage());
    }
}
package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.iut.banque.controller.ReinitialiserMotDePasse;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.opensymphony.xwork2.ActionSupport;

@RunWith(MockitoJUnitRunner.class)
public class TestReinitialiserMotDePasse {

    @Mock
    private BanqueFacade banqueFacade;

    private AutoCloseable mocks;
    private ReinitialiserMotDePasse action;

    @Before
    public void setUp() {
        // Initialisation de la vraie classe avec le mock
        action = new ReinitialiserMotDePasse(banqueFacade);
    }

    // ========== Test méthode input() ==========

    @Test
    public void input_shouldReturnInput() {
        String result = action.input();
        assertEquals(ActionSupport.INPUT, result);
    }

    // ========== Tests méthode submit() - Validation ==========

    @Test
    public void submit_missingUserId_shouldReturnInput_andAddActionError() throws Exception {
        action.setUserId(null);
        action.setNewPassword("password123");

        String result = action.submit();

        assertEquals(ActionSupport.INPUT, result);
        assertTrue("Devrait avoir des erreurs", action.hasActionErrors());
        assertTrue("Message d'erreur attendu", action.getActionErrors().contains("Identifiant et nouveau mot de passe requis."));
        verify(banqueFacade, never()).updatePassword(anyString(), anyString());
    }

    @Test
    public void submit_emptyUserId_shouldReturnInput() throws Exception {
        action.setUserId("   ");
        action.setNewPassword("password123");

        String result = action.submit();

        assertEquals(ActionSupport.INPUT, result);
        assertTrue(action.hasActionErrors());
    }

    @Test
    public void submit_missingPassword_shouldReturnInput() throws Exception {
        action.setUserId("user1");
        action.setNewPassword(null);

        String result = action.submit();

        assertEquals(ActionSupport.INPUT, result);
        assertTrue(action.hasActionErrors());
    }

    // ========== Tests méthode submit() - Succès ==========

    @Test
    public void submit_validCredentials_shouldReturnSuccess() throws Exception {
        action.setUserId("user1");
        action.setNewPassword("newPass123");

        // Mock de la mise à jour réussie
        doNothing().when(banqueFacade).updatePassword(eq("user1"), anyString());

        String result = action.submit();

        assertEquals(ActionSupport.SUCCESS, result);
        assertTrue("Devrait avoir un message de succès", action.hasActionMessages());
        assertTrue(action.getActionMessages().contains("Mot de passe mis à jour. Connectez-vous avec le nouveau mot de passe."));

        // Vérifie que la méthode de façade a bien été appelée avec le userId et un hash (non vide)
        verify(banqueFacade).updatePassword(eq("user1"), anyString());
    }

    // ========== Tests méthode submit() - Exceptions ==========

    @Test
    public void submit_userNotFound_shouldReturnInput_andAddActionError() throws Exception {
        action.setUserId("unknownUser");
        action.setNewPassword("pass");

        // Simulation de l'exception métier (Utilisateur inconnu)
        doThrow(new IllegalArgumentException("User not found"))
                .when(banqueFacade).updatePassword(eq("unknownUser"), anyString());

        String result = action.submit();

        assertEquals(ActionSupport.INPUT, result);
        assertTrue(action.hasActionErrors());
        assertTrue(action.getActionErrors().contains("Utilisateur introuvable."));
    }

    @Test
    public void submit_technicalError_shouldReturnError() throws Exception {
        action.setUserId("user1");
        action.setNewPassword("pass");

        // Simulation d'une erreur technique inattendue
        doThrow(new TechnicalException("DB Error"))
                .when(banqueFacade).updatePassword(eq("user1"), anyString());

        String result = action.submit();

        assertEquals(ActionSupport.ERROR, result);
        assertTrue(action.hasActionErrors());
        assertTrue(action.getActionErrors().contains("Erreur interne, réessayer plus tard."));
    }
}
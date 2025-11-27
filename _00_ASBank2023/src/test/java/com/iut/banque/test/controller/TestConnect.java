package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.controller.Connect; // Utilisation de la vraie classe
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.Utilisateur;

@RunWith(MockitoJUnitRunner.class)
public class TestConnect {

    private Connect connect; // Vraie classe

    @Mock
    private BanqueFacade banqueFacade;

    @Mock
    private Client client;

    @Mock
    private Utilisateur utilisateur;

    @Before
    public void setUp() {
        // Injection de la façade via le nouveau constructeur
        connect = new Connect(banqueFacade);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        connect.setUserCde("user123");
        connect.setUserPwd("password");
        when(banqueFacade.tryLogin("user123", "password")).thenReturn(LoginConstants.USER_IS_CONNECTED);

        String result = connect.login();

        assertEquals("SUCCESS", result);
        verify(banqueFacade).tryLogin("user123", "password");
    }

    @Test
    public void testLoginSuccessManager() throws Exception {
        connect.setUserCde("manager123");
        connect.setUserPwd("managerPass");
        when(banqueFacade.tryLogin("manager123", "managerPass")).thenReturn(LoginConstants.MANAGER_IS_CONNECTED);

        String result = connect.login();

        assertEquals("SUCCESSMANAGER", result);
        verify(banqueFacade).tryLogin("manager123", "managerPass");
    }

    @Test
    public void testLoginFailedInvalidCredentials() throws Exception {
        connect.setUserCde("user123");
        connect.setUserPwd("wrongPassword");
        when(banqueFacade.tryLogin("user123", "wrongPassword")).thenReturn(LoginConstants.LOGIN_FAILED);

        String result = connect.login();

        assertEquals("ERROR", result);
        verify(banqueFacade).tryLogin("user123", "wrongPassword");
    }

    @Test
    public void testLoginWithNullUserCode() {
        connect.setUserCde(null);
        connect.setUserPwd("password");

        String result = connect.login();

        assertEquals("ERROR", result);
        verify(banqueFacade, never()).tryLogin(anyString(), anyString());
    }

    @Test
    public void testLoginWithNullPassword() {
        connect.setUserCde("user123");
        connect.setUserPwd(null);

        String result = connect.login();

        assertEquals("ERROR", result);
        verify(banqueFacade, never()).tryLogin(anyString(), anyString());
    }

    @Test
    public void testLoginWithWhitespaceUserCode() throws Exception {
        connect.setUserCde("  user123  ");
        connect.setUserPwd("password");
        when(banqueFacade.tryLogin("user123", "password")).thenReturn(LoginConstants.USER_IS_CONNECTED);

        String result = connect.login();

        assertEquals("SUCCESS", result);
        verify(banqueFacade).tryLogin("user123", "password");
    }

    @Test
    public void testLoginWithException() throws Exception {
        connect.setUserCde("user123");
        connect.setUserPwd("password");
        when(banqueFacade.tryLogin("user123", "password")).thenThrow(new RuntimeException("Database error"));

        String result = connect.login();

        assertEquals("ERROR", result);
        verify(banqueFacade).tryLogin("user123", "password");
    }

    @Test
    public void testLoginWithUnknownResult() throws Exception {
        connect.setUserCde("user123");
        connect.setUserPwd("password");
        when(banqueFacade.tryLogin("user123", "password")).thenReturn(999);

        String result = connect.login();

        assertEquals("ERROR", result);
        verify(banqueFacade).tryLogin("user123", "password");
    }

    @Test
    public void testGetConnectedUser() {
        when(banqueFacade.getConnectedUser()).thenReturn(utilisateur);

        Utilisateur result = connect.getConnectedUser();

        assertNotNull(result);
        assertEquals(utilisateur, result);
        verify(banqueFacade).getConnectedUser();
    }

    @Test
    public void testGetAccounts() {
        Map<String, Compte> accounts = new HashMap<String, Compte>();
        when(banqueFacade.getConnectedUser()).thenReturn(client);
        when(client.getAccounts()).thenReturn(accounts);

        Map<String, Compte> result = connect.getAccounts();

        assertNotNull(result);
        assertEquals(accounts, result);
        verify(banqueFacade).getConnectedUser();
        verify(client).getAccounts();
    }

    @Test
    public void testLogout() {
        String result = connect.logout();

        assertEquals("SUCCESS", result);
        verify(banqueFacade).logout();
    }

    @Test
    public void testGetUserCde() {
        connect.setUserCde("testUser");
        assertEquals("testUser", connect.getUserCde());
    }

    @Test
    public void testGetUserPwd() {
        connect.setUserPwd("testPassword");
        assertEquals("testPassword", connect.getUserPwd());
    }
}
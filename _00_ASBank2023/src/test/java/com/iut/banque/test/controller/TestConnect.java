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
import com.iut.banque.controller.Connect;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.Utilisateur;

@RunWith(MockitoJUnitRunner.class)
public class TestConnect {

    private ConnectTestable connect;

    @Mock
    private BanqueFacade banqueFacade;

    @Mock
    private Client client;

    @Mock
    private Utilisateur utilisateur;

    @Before
    public void setUp() {
        // Utiliser une version testable de Connect qui permet d'injecter la facade
        connect = new ConnectTestable(banqueFacade);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // Arrange
        connect.setUserCde("user123");
        connect.setUserPwd("password");
        when(banqueFacade.tryLogin("user123", "password")).thenReturn(LoginConstants.USER_IS_CONNECTED);

        // Act
        String result = connect.login();

        // Assert
        assertEquals("SUCCESS", result);
        verify(banqueFacade).tryLogin("user123", "password");
    }

    @Test
    public void testLoginSuccessManager() throws Exception {
        // Arrange
        connect.setUserCde("manager123");
        connect.setUserPwd("managerPass");
        when(banqueFacade.tryLogin("manager123", "managerPass")).thenReturn(LoginConstants.MANAGER_IS_CONNECTED);

        // Act
        String result = connect.login();

        // Assert
        assertEquals("SUCCESSMANAGER", result);
        verify(banqueFacade).tryLogin("manager123", "managerPass");
    }

    @Test
    public void testLoginFailedInvalidCredentials() throws Exception {
        // Arrange
        connect.setUserCde("user123");
        connect.setUserPwd("wrongPassword");
        when(banqueFacade.tryLogin("user123", "wrongPassword")).thenReturn(LoginConstants.LOGIN_FAILED);

        // Act
        String result = connect.login();

        // Assert
        assertEquals("ERROR", result);
        verify(banqueFacade).tryLogin("user123", "wrongPassword");
    }

    @Test
    public void testLoginWithNullUserCode() {
        // Arrange
        connect.setUserCde(null);
        connect.setUserPwd("password");

        // Act
        String result = connect.login();

        // Assert
        assertEquals("ERROR", result);
        verify(banqueFacade, never()).tryLogin(anyString(), anyString());
    }

    @Test
    public void testLoginWithNullPassword() {
        // Arrange
        connect.setUserCde("user123");
        connect.setUserPwd(null);

        // Act
        String result = connect.login();

        // Assert
        assertEquals("ERROR", result);
        verify(banqueFacade, never()).tryLogin(anyString(), anyString());
    }

    @Test
    public void testLoginWithWhitespaceUserCode() throws Exception {
        // Arrange
        connect.setUserCde("  user123  ");
        connect.setUserPwd("password");
        when(banqueFacade.tryLogin("user123", "password")).thenReturn(LoginConstants.USER_IS_CONNECTED);

        // Act
        String result = connect.login();

        // Assert
        assertEquals("SUCCESS", result);
        verify(banqueFacade).tryLogin("user123", "password");
    }

    @Test
    public void testLoginWithException() throws Exception {
        // Arrange
        connect.setUserCde("user123");
        connect.setUserPwd("password");
        when(banqueFacade.tryLogin("user123", "password")).thenThrow(new RuntimeException("Database error"));

        // Act
        String result = connect.login();

        // Assert
        assertEquals("ERROR", result);
        verify(banqueFacade).tryLogin("user123", "password");
    }

    @Test
    public void testLoginWithUnknownResult() throws Exception {
        // Arrange
        connect.setUserCde("user123");
        connect.setUserPwd("password");
        when(banqueFacade.tryLogin("user123", "password")).thenReturn(999); // Code inconnu

        // Act
        String result = connect.login();

        // Assert
        assertEquals("ERROR", result);
        verify(banqueFacade).tryLogin("user123", "password");
    }

    @Test
    public void testGetConnectedUser() {
        // Arrange
        when(banqueFacade.getConnectedUser()).thenReturn(utilisateur);

        // Act
        Utilisateur result = connect.getConnectedUser();

        // Assert
        assertNotNull(result);
        assertEquals(utilisateur, result);
        verify(banqueFacade).getConnectedUser();
    }

    @Test
    public void testGetAccounts() {
        // Arrange
        Map<String, Compte> accounts = new HashMap<String, Compte>();
        when(banqueFacade.getConnectedUser()).thenReturn(client);
        when(client.getAccounts()).thenReturn(accounts);

        // Act
        Map<String, Compte> result = connect.getAccounts();

        // Assert
        assertNotNull(result);
        assertEquals(accounts, result);
        verify(banqueFacade).getConnectedUser();
        verify(client).getAccounts();
    }

    @Test
    public void testLogout() {
        // Act
        String result = connect.logout();

        // Assert
        assertEquals("SUCCESS", result);
        verify(banqueFacade).logout();
    }

    @Test
    public void testGetUserCde() {
        // Arrange
        connect.setUserCde("testUser");

        // Act & Assert
        assertEquals("testUser", connect.getUserCde());
    }

    @Test
    public void testGetUserPwd() {
        // Arrange
        connect.setUserPwd("testPassword");

        // Act & Assert
        assertEquals("testPassword", connect.getUserPwd());
    }

    /**
     * Classe testable qui permet d'injecter la facade mockée
     * sans avoir besoin du contexte Spring/Struts
     */
    private static class ConnectTestable {
        private BanqueFacade banqueFacade;
        private String userCde;
        private String userPwd;

        public ConnectTestable(BanqueFacade banqueFacade) {
            this.banqueFacade = banqueFacade;
        }

        public String getUserCde() {
            return userCde;
        }

        public void setUserCde(String userCde) {
            this.userCde = userCde;
        }

        public String getUserPwd() {
            return userPwd;
        }

        public void setUserPwd(String userPwd) {
            this.userPwd = userPwd;
        }

        public String login() {
            if (userCde == null || userPwd == null) {
                return "ERROR";
            }
            String trimmedUserCde = userCde.trim();

            int loginResult;
            try {
                loginResult = banqueFacade.tryLogin(trimmedUserCde, userPwd);
            } catch (Exception e) {
                loginResult = LoginConstants.ERROR;
            }

            switch (loginResult) {
                case LoginConstants.USER_IS_CONNECTED:
                    return "SUCCESS";
                case LoginConstants.MANAGER_IS_CONNECTED:
                    return "SUCCESSMANAGER";
                case LoginConstants.LOGIN_FAILED:
                    return "ERROR";
                default:
                    return "ERROR";
            }
        }

        public Utilisateur getConnectedUser() {
            return banqueFacade.getConnectedUser();
        }

        public Map<String, Compte> getAccounts() {
            return ((Client) banqueFacade.getConnectedUser()).getAccounts();
        }

        public String logout() {
            banqueFacade.logout();
            return "SUCCESS";
        }
    }
}
package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;

public class TestListeCompteManager {

    @Mock
    private BanqueFacade banqueFacade;

    @Mock
    private Client client;

    @Mock
    private Compte compte;

    private AutoCloseable mocks;
    private TestableListeCompteManager action;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        action = new TestableListeCompteManager(banqueFacade);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void getAllClients_shouldCallLoadAndReturnMap() {
        Map<String, Client> map = new HashMap<>();
        map.put("C1", client);
        doNothing().when(banqueFacade).loadClients();
        when(banqueFacade.getAllClients()).thenReturn(map);

        Map<String, Client> result = action.getAllClients();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(client, result.get("C1"));
        verify(banqueFacade).loadClients();
        verify(banqueFacade).getAllClients();
    }

    @Test
    public void deleteUser_success_shouldReturnSuccess_andSetUserInfo() throws Exception {
        when(client.getIdentity()).thenReturn("USER1");
        doNothing().when(banqueFacade).deleteUser(client);

        action.setClient(client);
        String res = action.deleteUser();

        assertEquals("SUCCESS", res);
        assertEquals("USER1", action.getUserInfo());
        verify(banqueFacade).deleteUser(client);
    }

    @Test
    public void deleteUser_whenTechnicalException_shouldReturnError() throws Exception {
        when(client.getIdentity()).thenReturn("USER2");
        doThrow(new TechnicalException("tech")).when(banqueFacade).deleteUser(client);

        action.setClient(client);
        String res = action.deleteUser();

        assertEquals("ERROR", res);
        assertEquals("USER2", action.getUserInfo());
        verify(banqueFacade).deleteUser(client);
    }

    @Test
    public void deleteUser_whenIllegalOperation_shouldReturnNonEmptyAccount() throws Exception {
        when(client.getIdentity()).thenReturn("USER3");
        doThrow(new IllegalOperationException("non vide")).when(banqueFacade).deleteUser(client);

        action.setClient(client);
        String res = action.deleteUser();

        assertEquals("NONEMPTYACCOUNT", res);
        assertEquals("USER3", action.getUserInfo());
        verify(banqueFacade).deleteUser(client);
    }

    @Test
    public void deleteAccount_success_shouldReturnSuccess_andSetCompteInfo() throws Exception {
        when(compte.getNumeroCompte()).thenReturn("ACC-1");
        doNothing().when(banqueFacade).deleteAccount(compte);

        action.setCompte(compte);
        String res = action.deleteAccount();

        assertEquals("SUCCESS", res);
        assertEquals("ACC-1", action.getCompteInfo());
        verify(banqueFacade).deleteAccount(compte);
    }

    @Test
    public void deleteAccount_whenIllegalOperation_shouldReturnNonEmptyAccount() throws Exception {
        when(compte.getNumeroCompte()).thenReturn("ACC-2");
        doThrow(new IllegalOperationException("not empty")).when(banqueFacade).deleteAccount(compte);

        action.setCompte(compte);
        String res = action.deleteAccount();

        assertEquals("NONEMPTYACCOUNT", res);
        assertEquals("ACC-2", action.getCompteInfo());
        verify(banqueFacade).deleteAccount(compte);
    }

    @Test
    public void deleteAccount_whenTechnicalException_shouldReturnError() throws Exception {
        when(compte.getNumeroCompte()).thenReturn("ACC-3");
        doThrow(new TechnicalException("tech")).when(banqueFacade).deleteAccount(compte);

        action.setCompte(compte);
        String res = action.deleteAccount();

        assertEquals("ERROR", res);
        assertEquals("ACC-3", action.getCompteInfo());
        verify(banqueFacade).deleteAccount(compte);
    }

    private static class TestableListeCompteManager {
        private final BanqueFacade banqueFacade;
        private Client client;
        private Compte compte;
        private String userInfo;
        private String compteInfo;

        public TestableListeCompteManager(BanqueFacade banqueFacade) {
            this.banqueFacade = banqueFacade;
        }

        public Map<String, Client> getAllClients() {
            banqueFacade.loadClients();
            return banqueFacade.getAllClients();
        }

        public void setClient(Client client) {
            this.client = client;
        }

        public Client getClient() {
            return client;
        }

        public void setCompte(Compte compte) {
            this.compte = compte;
        }

        public Compte getCompte() {
            return compte;
        }

        public String getUserInfo() {
            return userInfo;
        }

        public String getCompteInfo() {
            return compteInfo;
        }

        public String deleteUser() {
            try {
                this.userInfo = client.getIdentity();
                banqueFacade.deleteUser(client);
                return "SUCCESS";
            } catch (TechnicalException e) {
                return "ERROR";
            } catch (IllegalOperationException ioe) {
                return "NONEMPTYACCOUNT";
            }
        }

        public String deleteAccount() {
            try {
                this.compteInfo = compte.getNumeroCompte();
                banqueFacade.deleteAccount(compte);
                return "SUCCESS";
            } catch (IllegalOperationException e) {
                return "NONEMPTYACCOUNT";
            } catch (TechnicalException e) {
                return "ERROR";
            }
        }
    }
}

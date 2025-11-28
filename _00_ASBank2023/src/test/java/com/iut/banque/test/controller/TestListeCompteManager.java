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

import com.iut.banque.controller.ListeCompteManager;
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
    // Utilisation de la vraie classe
    private ListeCompteManager action;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        // Injection via le nouveau constructeur
        action = new ListeCompteManager(banqueFacade);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    // ========== Tests getAllClients ==========

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
    public void getAllClients_emptyMap_shouldReturnEmpty() {
        Map<String, Client> emptyMap = new HashMap<>();
        when(banqueFacade.getAllClients()).thenReturn(emptyMap);

        Map<String, Client> result = action.getAllClients();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(banqueFacade).loadClients();
    }

    @Test
    public void getAllClients_multipleClients_shouldReturnAll() {
        Client client2 = mock(Client.class);
        Client client3 = mock(Client.class);
        Map<String, Client> map = new HashMap<>();
        map.put("C1", client);
        map.put("C2", client2);
        map.put("C3", client3);
        when(banqueFacade.getAllClients()).thenReturn(map);

        Map<String, Client> result = action.getAllClients();

        assertEquals(3, result.size());
        verify(banqueFacade).loadClients();
    }

    // ========== Tests deleteUser - SUCCESS ==========

    @Test
    public void deleteUser_success_shouldReturnSuccess_andSetUserInfo() throws Exception {
        when(client.getIdentity()).thenReturn("USER1");
        doNothing().when(banqueFacade).deleteUser(client);

        action.setClient(client);
        String res = action.deleteUser();

        assertEquals("SUCCESS", res);
        assertEquals("USER1", action.getUserInfo());
        verify(banqueFacade).deleteUser(client);
        verify(client).getIdentity();
    }

    @Test
    public void deleteUser_success_withEmptyIdentity() throws Exception {
        when(client.getIdentity()).thenReturn("");
        doNothing().when(banqueFacade).deleteUser(client);

        action.setClient(client);
        String res = action.deleteUser();

        assertEquals("SUCCESS", res);
        assertEquals("", action.getUserInfo());
    }

    @Test
    public void deleteUser_success_withNullIdentity() throws Exception {
        when(client.getIdentity()).thenReturn(null);
        doNothing().when(banqueFacade).deleteUser(client);

        action.setClient(client);
        String res = action.deleteUser();

        assertEquals("SUCCESS", res);
        assertNull(action.getUserInfo());
    }

    // ========== Tests deleteUser - TechnicalException ==========

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
    public void deleteUser_whenTechnicalException_withSpecificMessage() throws Exception {
        when(client.getIdentity()).thenReturn("TECH_USER");
        doThrow(new TechnicalException("Database connection failed")).when(banqueFacade).deleteUser(client);

        action.setClient(client);
        String res = action.deleteUser();

        assertEquals("ERROR", res);
        assertEquals("TECH_USER", action.getUserInfo());
    }

    // ========== Tests deleteUser - IllegalOperationException ==========

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
    public void deleteUser_whenIllegalOperation_clientHasAccounts() throws Exception {
        when(client.getIdentity()).thenReturn("RICH_CLIENT");
        doThrow(new IllegalOperationException("Client has active accounts")).when(banqueFacade).deleteUser(client);

        action.setClient(client);
        String res = action.deleteUser();

        assertEquals("NONEMPTYACCOUNT", res);
        assertEquals("RICH_CLIENT", action.getUserInfo());
    }

    // ========== Tests deleteAccount - SUCCESS ==========

    @Test
    public void deleteAccount_success_shouldReturnSuccess_andSetCompteInfo() throws Exception {
        when(compte.getNumeroCompte()).thenReturn("ACC-1");
        doNothing().when(banqueFacade).deleteAccount(compte);

        action.setCompte(compte);
        String res = action.deleteAccount();

        assertEquals("SUCCESS", res);
        assertEquals("ACC-1", action.getCompteInfo());
        verify(banqueFacade).deleteAccount(compte);
        verify(compte).getNumeroCompte();
    }

    @Test
    public void deleteAccount_success_withEmptyNumero() throws Exception {
        when(compte.getNumeroCompte()).thenReturn("");
        doNothing().when(banqueFacade).deleteAccount(compte);

        action.setCompte(compte);
        String res = action.deleteAccount();

        assertEquals("SUCCESS", res);
        assertEquals("", action.getCompteInfo());
    }

    @Test
    public void deleteAccount_success_withNullNumero() throws Exception {
        when(compte.getNumeroCompte()).thenReturn(null);
        doNothing().when(banqueFacade).deleteAccount(compte);

        action.setCompte(compte);
        String res = action.deleteAccount();

        assertEquals("SUCCESS", res);
        assertNull(action.getCompteInfo());
    }

    // ========== Tests deleteAccount - IllegalOperationException ==========

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
    public void deleteAccount_whenIllegalOperation_positiveBalance() throws Exception {
        when(compte.getNumeroCompte()).thenReturn("ACC-SOLDE");
        doThrow(new IllegalOperationException("Balance is positive")).when(banqueFacade).deleteAccount(compte);

        action.setCompte(compte);
        String res = action.deleteAccount();

        assertEquals("NONEMPTYACCOUNT", res);
        assertEquals("ACC-SOLDE", action.getCompteInfo());
    }

    // ========== Tests deleteAccount - TechnicalException ==========

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

    @Test
    public void deleteAccount_whenTechnicalException_databaseError() throws Exception {
        when(compte.getNumeroCompte()).thenReturn("ACC-DB");
        doThrow(new TechnicalException("DAO exception")).when(banqueFacade).deleteAccount(compte);

        action.setCompte(compte);
        String res = action.deleteAccount();

        assertEquals("ERROR", res);
        assertEquals("ACC-DB", action.getCompteInfo());
    }

    // ========== Tests Getters/Setters ==========

    @Test
    public void setClient_shouldStoreClient() {
        action.setClient(client);
        assertSame(client, action.getClient());
    }

    @Test
    public void setClient_null_shouldStoreNull() {
        action.setClient(null);
        assertNull(action.getClient());
    }

    @Test
    public void setCompte_shouldStoreCompte() {
        action.setCompte(compte);
        assertSame(compte, action.getCompte());
    }

    @Test
    public void setCompte_null_shouldStoreNull() {
        action.setCompte(null);
        assertNull(action.getCompte());
    }

    @Test
    public void getUserInfo_initiallyNull() {
        assertNull(action.getUserInfo());
    }

    @Test
    public void getCompteInfo_initiallyNull() {
        assertNull(action.getCompteInfo());
    }

    // ========== Tests ordres d'appels multiples ==========

    @Test
    public void deleteUser_multipleCalls_shouldUpdateUserInfo()  {
        Client client2 = mock(Client.class);
        when(client.getIdentity()).thenReturn("USER_A");
        when(client2.getIdentity()).thenReturn("USER_B");

        action.setClient(client);
        action.deleteUser();
        assertEquals("USER_A", action.getUserInfo());

        action.setClient(client2);
        action.deleteUser();
        assertEquals("USER_B", action.getUserInfo());
    }

    @Test
    public void deleteAccount_multipleCalls_shouldUpdateCompteInfo()  {
        Compte compte2 = mock(Compte.class);
        when(compte.getNumeroCompte()).thenReturn("ACC-X");
        when(compte2.getNumeroCompte()).thenReturn("ACC-Y");

        action.setCompte(compte);
        action.deleteAccount();
        assertEquals("ACC-X", action.getCompteInfo());

        action.setCompte(compte2);
        action.deleteAccount();
        assertEquals("ACC-Y", action.getCompteInfo());
    }
}
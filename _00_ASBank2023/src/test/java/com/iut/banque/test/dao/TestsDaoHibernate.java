package com.iut.banque.test.dao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.iut.banque.dao.DaoHibernate;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;

@RunWith(MockitoJUnitRunner.class)
public class TestsDaoHibernate {

    private DaoHibernate daoHibernate;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Criteria criteria;

    @Before
    public void setUp() {
        // On crée la DAO manuellement (pas de Spring)
        daoHibernate = new DaoHibernate();
        // On injecte le mock de SessionFactory
        daoHibernate.setSessionFactory(sessionFactory);

        // Comportement standard : sessionFactory.getCurrentSession() renvoie notre mock session
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    // --- Tests récupération Compte ---

    @Test
    public void testGetAccountByIdExist() {
        Compte compteMock = mock(Compte.class);
        when(session.get(Compte.class, "CPT001")).thenReturn(compteMock);

        Compte result = daoHibernate.getAccountById("CPT001");

        assertNotNull(result);
        assertSame(compteMock, result);
        verify(session).get(Compte.class, "CPT001");
    }

    @Test
    public void testGetAccountByIdDoesntExist() {
        when(session.get(Compte.class, "INCONNU")).thenReturn(null);

        Compte result = daoHibernate.getAccountById("INCONNU");

        assertNull(result);
    }

    // --- Tests Création Compte ---

    @Test
    public void testCreateCompteAvecDecouvert_Success() throws Exception {
        Client client = new Client();
        String id = "NW1010010001"; // Format valide

        // Simuler que le compte n'existe pas encore
        when(session.get(CompteAvecDecouvert.class, id)).thenReturn(null);

        Compte res = daoHibernate.createCompteAvecDecouvert(0, id, 100, client);

        assertNotNull(res);
        assertTrue(res instanceof CompteAvecDecouvert);
        assertEquals(id, res.getNumeroCompte());
        // Vérifier que save() a bien été appelé
        verify(session).save(any(CompteAvecDecouvert.class));
    }

    @Test(expected = TechnicalException.class)
    public void testCreateCompteAvecDecouvert_ExistingId() throws Exception {
        String id = "EXIST";
        // Simuler que le compte existe DÉJÀ
        when(session.get(CompteAvecDecouvert.class, id)).thenReturn(mock(CompteAvecDecouvert.class));

        daoHibernate.createCompteAvecDecouvert(0, id, 100, new Client());
    }

    @Test
    public void testCreateCompteSansDecouvert_Success() throws Exception {
        Client client = new Client();
        String id = "CSD001";
        when(session.get(CompteSansDecouvert.class, id)).thenReturn(null);

        Compte res = daoHibernate.createCompteSansDecouvert(100, id, client);

        assertNotNull(res);
        assertTrue(res instanceof CompteSansDecouvert);
        verify(session).save(any(CompteSansDecouvert.class));
    }

    // --- Tests Suppression Compte ---

    @Test
    public void testDeleteAccount_Success() throws Exception {
        Compte compte = mock(Compte.class);
        daoHibernate.deleteAccount(compte);
        verify(session).delete(compte);
    }

    @Test(expected = TechnicalException.class)
    public void testDeleteAccount_Null() throws Exception {
        daoHibernate.deleteAccount(null);
    }

    // --- Tests Utilisateur ---

    @Test
    public void testGetUserById() {
        Utilisateur user = mock(Utilisateur.class);
        when(session.get(Utilisateur.class, "u1")).thenReturn(user);

        Utilisateur result = daoHibernate.getUserById("u1");
        assertSame(user, result);
    }

    @Test
    public void testCreateUser_Client_Success() throws Exception {
        String userId = "newClient";
        when(session.get(Utilisateur.class, userId)).thenReturn(null);

        Utilisateur res = daoHibernate.createUser("Nom", "Prenom", "Adr", true, userId, "pass", false, "1234567890");

        assertTrue(res instanceof Client);
        verify(session).save(any(Client.class));
    }

    @Test
    public void testCreateUser_Manager_Success() throws Exception {
        String userId = "newMgr";
        when(session.get(Utilisateur.class, userId)).thenReturn(null);

        Utilisateur res = daoHibernate.createUser("Nom", "Prenom", "Adr", true, userId, "pass", true, null);

        assertTrue(res instanceof Gestionnaire);
        verify(session).save(any(Gestionnaire.class));
    }

    @Test(expected = TechnicalException.class)
    public void testCreateUser_Existing() throws Exception {
        String userId = "exist";
        when(session.get(Utilisateur.class, userId)).thenReturn(mock(Utilisateur.class));
        daoHibernate.createUser("N", "P", "A", true, userId, "pwd", false, "000");
    }

    // --- Tests Login (isUserAllowed) ---

    @Test
    public void testIsUserAllowed_Success() {
        Utilisateur user = mock(Utilisateur.class);
        when(user.getUserPwd()).thenReturn("secret");

        when(session.get(Utilisateur.class, "login")).thenReturn(user);

        boolean allowed = daoHibernate.isUserAllowed("login", "secret");
        assertTrue(allowed);
    }

    @Test
    public void testIsUserAllowed_WrongPassword() {
        Utilisateur user = mock(Utilisateur.class);
        when(user.getUserPwd()).thenReturn("secret");
        when(session.get(Utilisateur.class, "login")).thenReturn(user);

        boolean allowed = daoHibernate.isUserAllowed("login", "wrong");
        assertFalse(allowed);
    }

    @Test
    public void testIsUserAllowed_UserNotFound() {
        when(session.get(Utilisateur.class, "unknown")).thenReturn(null);
        boolean allowed = daoHibernate.isUserAllowed("unknown", "pass");
        assertFalse(allowed);
    }

    @Test
    public void testIsUserAllowed_NullParams() {
        assertFalse(daoHibernate.isUserAllowed(null, "pass"));
        assertFalse(daoHibernate.isUserAllowed("user", null));
    }

    // --- Tests Listes (Clients/Gestionnaires) ---

    @Test
    public void testGetAllClients() {
        when(session.createCriteria(Client.class)).thenReturn(criteria);

        List<Object> list = new ArrayList<>();
        Client c1 = new Client();
        try {
            // CORRECTION IMPORTANTE : Utiliser un format d'ID valide pour un Client
            // Regex: ^[a-z]\\.[a-z]+[1-9]\\d*$ (ex: c.client1)
            c1.setUserId("c.client1");
        } catch(Exception e){
            e.printStackTrace();
        }
        list.add(c1);

        when(criteria.list()).thenReturn(list);

        Map<String, Client> res = daoHibernate.getAllClients();

        assertEquals(1, res.size());
        // On vérifie avec la clé valide
        assertTrue(res.containsKey("c.client1"));
    }

    @Test
    public void testGetAllGestionnaires() {
        when(session.createCriteria(Gestionnaire.class)).thenReturn(criteria);

        List<Object> list = new ArrayList<>();
        Gestionnaire g1 = new Gestionnaire();
        try { g1.setUserId("g1"); } catch(Exception e){}
        list.add(g1);

        when(criteria.list()).thenReturn(list);

        Map<String, Gestionnaire> res = daoHibernate.getAllGestionnaires();

        assertEquals(1, res.size());
        assertTrue(res.containsKey("g1"));
    }

    @Test
    public void testDisconnect() {
        daoHibernate.disconnect();
    }

    @Test
    public void testGetAccountsByClientId() {
        Client client = mock(Client.class);
        Map<String, Compte> accounts = new HashMap<>();
        when(client.getAccounts()).thenReturn(accounts);

        when(session.get(Client.class, "c1")).thenReturn(client);

        Map<String, Compte> res = daoHibernate.getAccountsByClientId("c1");
        assertSame(accounts, res);
    }
}
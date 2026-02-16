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

import com.iut.banque.controller.DetailCompte;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.Gestionnaire;

public class TestDetailCompte {

    @Mock
    private BanqueFacade banqueFacade;

    @Mock
    private Client client;

    @Mock
    private Gestionnaire gestionnaire;

    @Mock
    private Compte compte;

    private AutoCloseable mocks;
    // Utilisation de la vraie classe
    private DetailCompte action;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        // Injection via le constructeur de test
        action = new DetailCompte(banqueFacade);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    // ========== Tests getError ==========

    @Test
    public void getError_technical_shouldReturnMappedMessage() {
        action.setError("TECHNICAL");
        assertEquals("Erreur interne. Verifiez votre saisie puis réessayer. Contactez votre conseiller si le problème persiste.", action.getError());
    }

    @Test
    public void getError_business_shouldReturnMappedMessage() {
        action.setError("BUSINESS");
        assertEquals("Fonds insuffisants.", action.getError());
    }

    @Test
    public void getError_negativeAmount_shouldReturnMappedMessage() {
        action.setError("NEGATIVEAMOUNT");
        assertEquals("Veuillez rentrer un montant positif.", action.getError());
    }

    @Test
    public void getError_negativeOverdraft_shouldReturnMappedMessage() {
        action.setError("NEGATIVEOVERDRAFT");
        assertEquals("Veuillez rentrer un découvert positif.", action.getError());
    }

    @Test
    public void getError_incompatibleOverdraft_shouldReturnMappedMessage() {
        action.setError("INCOMPATIBLEOVERDRAFT");
        assertEquals("Le nouveau découvert est incompatible avec le solde actuel.", action.getError());
    }

    @Test
    public void getError_null_shouldReturnEmptyString() {
        action.setError(null);
        // Note: setError(null) met "EMPTY", et getError() avec "EMPTY" renvoie "" via default
        assertEquals("", action.getError());
    }

    @Test
    public void getError_unknownCode_shouldReturnEmptyString() {
        action.setError("UNKNOWN");
        assertEquals("", action.getError());
    }

    // ========== Tests getCompte ==========

    @Test
    public void getCompte_asGestionnaire_shouldReturnCompte() {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);

        Compte result = action.getCompte();

        assertSame(compte, result);
    }

    @Test
    public void getCompte_asClientWithAccount_shouldReturnCompte() {
        Map<String, Compte> accounts = new HashMap<>();
        when(compte.getNumeroCompte()).thenReturn("C123");
        accounts.put("C123", compte);
        when(client.getAccounts()).thenReturn(accounts);
        when(banqueFacade.getConnectedUser()).thenReturn(client);
        action.setCompte(compte);

        Compte result = action.getCompte();

        assertSame(compte, result);
    }

    @Test
    public void getCompte_asClientWithoutAccount_shouldReturnNull() {
        Map<String, Compte> accounts = new HashMap<>();
        when(compte.getNumeroCompte()).thenReturn("C999");
        when(client.getAccounts()).thenReturn(accounts);
        when(banqueFacade.getConnectedUser()).thenReturn(client);
        action.setCompte(compte);

        Compte result = action.getCompte();

        assertNull(result);
    }

    @Test
    public void getCompte_asClientWithNullCompte_shouldReturnNull() {
        when(banqueFacade.getConnectedUser()).thenReturn(client);
        action.setCompte((Compte)null);

        Compte result = action.getCompte();

        assertNull(result);
    }

    @Test
    public void getCompte_asGestionnaireWithNullCompte_shouldReturnNull() {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte((Compte)null);
        // Si compte est null, getCompte() renvoie null (ou crasherait s'il n'était pas protégé, mais on teste)
        Compte result = action.getCompte();

        // Note: Dans l'implémentation actuelle, si c'est un gestionnaire, il retourne 'compte' directement.
        // Si 'compte' est null, il retourne null.
        assertNull(result);
    }

    // ========== Tests debit - SUCCESS ==========

    @Test
    public void debit_success_shouldCallDebiterAndReturnSuccess() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("100.0");
        doNothing().when(banqueFacade).debiter(compte, 100.0);

        String res = action.debit();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).debiter(compte, 100.0);
    }

    @Test
    public void debit_success_withDecimalAmount() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("50.75");

        String res = action.debit();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).debiter(compte, 50.75);
    }

    @Test
    public void debit_success_withZeroAmount() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("0");

        String res = action.debit();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).debiter(compte, 0.0);
    }

    // ========== Tests debit - ERROR (format) ==========

    @Test
    public void debit_shouldReturnError_whenInputIsInvalid() throws Exception {
        // Liste des cas à tester : lettres, null, vide, caractères spéciaux
        String[] invalidInputs = {"abc", null, "", "100$"};

        for (String input : invalidInputs) {
            // IMPORTANT : On efface l'historique des appels précédents pour que le verify(never) soit juste
            clearInvocations(banqueFacade);

            when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
            action.setCompte(compte);
            action.setMontant(input);

            String res = action.debit();

            assertEquals("Devrait retourner ERROR pour l'entrée : " + input, "ERROR", res);
            verify(banqueFacade, never()).debiter(any(), anyDouble());
        }
    }


    // ========== Tests debit - Exceptions ==========

    @Test
    public void debit_insufficientFunds_shouldReturnNotEnoughFunds() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("1000.0");
        doThrow(new InsufficientFundsException("not enough")).when(banqueFacade).debiter(compte, 1000.0);

        String res = action.debit();

        assertEquals("NOTENOUGHFUNDS", res);
        verify(banqueFacade).debiter(compte, 1000.0);
    }

    @Test
    public void debit_negativeAmount_shouldReturnNegativeAmountConstant() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("-50.0");
        doThrow(new IllegalFormatException("negative")).when(banqueFacade).debiter(compte, -50.0);

        String res = action.debit();

        assertEquals("NEGATIVEAMOUNT", res);
        verify(banqueFacade).debiter(compte, -50.0);
    }

    // ========== Tests credit - SUCCESS ==========

    @Test
    public void credit_success_shouldCallCrediterAndReturnSuccess() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("200.0");
        doNothing().when(banqueFacade).crediter(compte, 200.0);

        String res = action.credit();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).crediter(compte, 200.0);
    }

    @Test
    public void credit_success_withDecimalAmount() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("99.99");

        String res = action.credit();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).crediter(compte, 99.99);
    }

    @Test
    public void credit_success_withZeroAmount() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("0.0");

        String res = action.credit();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).crediter(compte, 0.0);
    }

    // ========== Tests credit - ERROR (format) ==========

    @Test
    public void credit_shouldReturnError_whenInputIsInvalid() throws Exception {
        // Liste des cas à tester
        String[] invalidInputs = {"xyz", null, ""};

        for (String input : invalidInputs) {
            // On nettoie l'historique du mock
            clearInvocations(banqueFacade);

            when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
            action.setCompte(compte);
            action.setMontant(input);

            String res = action.credit();

            assertEquals("Devrait retourner ERROR pour l'entrée : " + input, "ERROR", res);
            verify(banqueFacade, never()).crediter(any(), anyDouble());
        }
    }

    // ========== Tests credit - Exceptions ==========

    @Test
    public void credit_negativeAmount_shouldReturnNegativeAmountConstant() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("-100.0");
        doThrow(new IllegalFormatException("negative")).when(banqueFacade).crediter(compte, -100.0);

        String res = action.credit();

        assertEquals("NEGATIVEAMOUNT", res);
        verify(banqueFacade).crediter(compte, -100.0);
    }

    // ========== Tests Getters/Setters ==========

    @Test
    public void setMontant_shouldStoreMontant() {
        action.setMontant("123.45");
        assertEquals("123.45", action.getMontant());
    }

    @Test
    public void setCompte_shouldStoreCompte() {
        action.setCompte(compte);
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        assertSame(compte, action.getCompte());
    }
}
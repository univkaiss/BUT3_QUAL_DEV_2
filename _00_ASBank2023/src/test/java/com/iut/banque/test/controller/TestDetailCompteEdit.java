package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.controller.DetailCompteEdit;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Gestionnaire;

public class TestDetailCompteEdit {

    @Mock
    private BanqueFacade banqueFacade;

    @Mock
    private Gestionnaire gestionnaire;

    @Mock
    private Compte compte;

    @Mock
    private CompteAvecDecouvert compteAvecDecouvert;

    private AutoCloseable mocks;
    // Utilisation de la vraie classe
    private DetailCompteEdit action;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        // Injection via le constructeur de test
        action = new DetailCompteEdit(banqueFacade);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    // ========== Tests Getters/Setters ==========

    @Test
    public void gettersAndSetters_shouldWorkCorrectly() {
        action.setDecouvertAutorise("500.0");
        assertEquals("500.0", action.getDecouvertAutorise());
    }

    @Test
    public void setDecouvertAutorise_null_shouldStoreNull() {
        action.setDecouvertAutorise(null);
        assertNull(action.getDecouvertAutorise());
    }

    @Test
    public void setDecouvertAutorise_emptyString_shouldStoreEmpty() {
        action.setDecouvertAutorise("");
        assertEquals("", action.getDecouvertAutorise());
    }

    // ========== Tests changementDecouvert - SUCCESS ==========

    @Test
    public void changementDecouvert_success_withPositiveDecouvert() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        when(compteAvecDecouvert.getSolde()).thenReturn(100.0);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("200.0");
        doNothing().when(banqueFacade).changeDecouvert(compteAvecDecouvert, 200.0);

        String res = action.changementDecouvert();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).changeDecouvert(compteAvecDecouvert, 200.0);
    }

    @Test
    public void changementDecouvert_success_withZeroDecouvert() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        when(compteAvecDecouvert.getSolde()).thenReturn(50.0);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("0");

        String res = action.changementDecouvert();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).changeDecouvert(compteAvecDecouvert, 0.0);
    }

    @Test
    public void changementDecouvert_success_withLargeDecouvert() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        when(compteAvecDecouvert.getSolde()).thenReturn(1000.0);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("9999.99");

        String res = action.changementDecouvert();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).changeDecouvert(compteAvecDecouvert, 9999.99);
    }

    // ========== Tests changementDecouvert - ERROR (type compte) ==========

    @Test
    public void changementDecouvert_compteSansDecouvert_shouldReturnError() throws Exception {
        CompteSansDecouvert compteSansDecouvert = mock(CompteSansDecouvert.class);
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compteSansDecouvert);
        action.setDecouvertAutorise("100");

        String res = action.changementDecouvert();

        assertEquals("ERROR", res);
        verify(banqueFacade, never()).changeDecouvert(any(), anyDouble());
    }

    @Test
    public void changementDecouvert_compteNull_shouldReturnError() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(null);
        action.setDecouvertAutorise("100");

        String res = action.changementDecouvert();

        assertEquals("ERROR", res);
        verify(banqueFacade, never()).changeDecouvert(any(), anyDouble());
    }

    @Test
    public void changementDecouvert_compteGenericNotAvecDecouvert_shouldReturnError() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setDecouvertAutorise("100");

        String res = action.changementDecouvert();

        assertEquals("ERROR", res);
        verify(banqueFacade, never()).changeDecouvert(any(), anyDouble());
    }

    // ========== Tests changementDecouvert - ERROR (format invalide) ==========

    @Test
    public void changementDecouvert_invalidNumberFormat_shouldReturnError() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("abc");

        String res = action.changementDecouvert();

        assertEquals("ERROR", res);
        verify(banqueFacade, never()).changeDecouvert(any(), anyDouble());
    }

    @Test
    public void changementDecouvert_emptyString_shouldReturnError() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("");

        String res = action.changementDecouvert();

        assertEquals("ERROR", res);
        verify(banqueFacade, never()).changeDecouvert(any(), anyDouble());
    }

    @Test
    public void changementDecouvert_null_shouldReturnError() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise(null);

        String res = action.changementDecouvert();

        assertEquals("ERROR", res);
        verify(banqueFacade, never()).changeDecouvert(any(), anyDouble());
    }

    @Test
    public void changementDecouvert_specialCharacters_shouldReturnError() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("100$");

        String res = action.changementDecouvert();

        assertEquals("ERROR", res);
        verify(banqueFacade, never()).changeDecouvert(any(), anyDouble());
    }

    @Test
    public void changementDecouvert_multipleDecimalPoints_shouldReturnError() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("100.50.25");

        String res = action.changementDecouvert();

        assertEquals("ERROR", res);
        verify(banqueFacade, never()).changeDecouvert(any(), anyDouble());
    }

    // ========== Tests changementDecouvert - NEGATIVEOVERDRAFT ==========

    @Test
    public void changementDecouvert_negativeOverdraft_shouldReturnNegativeOverdraft() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        when(compteAvecDecouvert.getSolde()).thenReturn(100.0);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("-50.0");
        doThrow(new IllegalFormatException("negative")).when(banqueFacade).changeDecouvert(compteAvecDecouvert, -50.0);

        String res = action.changementDecouvert();

        assertEquals("NEGATIVEOVERDRAFT", res);
        verify(banqueFacade).changeDecouvert(compteAvecDecouvert, -50.0);
    }

    @Test
    public void changementDecouvert_veryNegativeOverdraft_shouldReturnNegativeOverdraft() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        when(compteAvecDecouvert.getSolde()).thenReturn(100.0);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("-9999.99");
        doThrow(new IllegalFormatException("negative")).when(banqueFacade).changeDecouvert(compteAvecDecouvert, -9999.99);

        String res = action.changementDecouvert();

        assertEquals("NEGATIVEOVERDRAFT", res);
    }

    // ========== Tests changementDecouvert - INCOMPATIBLEOVERDRAFT ==========

    @Test
    public void changementDecouvert_incompatibleOverdraft_shouldReturnIncompatible() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        when(compteAvecDecouvert.getSolde()).thenReturn(-200.0);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("100.0");
        doThrow(new IllegalOperationException("incompatible")).when(banqueFacade).changeDecouvert(compteAvecDecouvert, 100.0);

        String res = action.changementDecouvert();

        assertEquals("INCOMPATIBLEOVERDRAFT", res);
        verify(banqueFacade).changeDecouvert(compteAvecDecouvert, 100.0);
    }

    @Test
    public void changementDecouvert_incompatibleWithBalance_shouldReturnIncompatible() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        when(compteAvecDecouvert.getSolde()).thenReturn(-500.0);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("300.0");
        doThrow(new IllegalOperationException("balance too low")).when(banqueFacade).changeDecouvert(compteAvecDecouvert, 300.0);

        String res = action.changementDecouvert();

        assertEquals("INCOMPATIBLEOVERDRAFT", res);
    }
}
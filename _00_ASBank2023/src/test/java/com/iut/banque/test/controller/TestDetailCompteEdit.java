package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
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
    private TestableDetailCompteEdit action;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        action = new TestableDetailCompteEdit(banqueFacade);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void changementDecouvert_success_shouldReturnSuccess() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("150.5");

        String res = action.changementDecouvert();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).changeDecouvert(compteAvecDecouvert, 150.5);
    }

    @Test
    public void changementDecouvert_notCompteAvecDecouvert_shouldReturnError() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setDecouvertAutorise("100");

        String res = action.changementDecouvert();

        assertEquals("ERROR", res);
        verify(banqueFacade, never()).changeDecouvert(any(CompteAvecDecouvert.class), anyDouble());
    }

    @Test
    public void changementDecouvert_invalidNumber_shouldReturnError() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("abc");

        String res = action.changementDecouvert();

        assertEquals("ERROR", res);
        verify(banqueFacade, never()).changeDecouvert(any(CompteAvecDecouvert.class), anyDouble());
    }

    @Test
    public void changementDecouvert_negativeOverdraft_shouldReturnNegativeOverdraft() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("-50");

        doThrow(new IllegalFormatException("neg")).when(banqueFacade).changeDecouvert(eq(compteAvecDecouvert), eq(-50.0));

        String res = action.changementDecouvert();

        assertEquals("NEGATIVEOVERDRAFT", res);
        verify(banqueFacade).changeDecouvert(compteAvecDecouvert, -50.0);
    }

    @Test
    public void changementDecouvert_incompatibleOverdraft_shouldReturnIncompatible() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("200");

        doThrow(new IllegalOperationException("incompatible")).when(banqueFacade).changeDecouvert(eq(compteAvecDecouvert), eq(200.0));

        String res = action.changementDecouvert();

        assertEquals("INCOMPATIBLEOVERDRAFT", res);
        verify(banqueFacade).changeDecouvert(compteAvecDecouvert, 200.0);
    }

    private static class TestableDetailCompteEdit {
        private final BanqueFacade banqueFacade;
        private Compte compteField;
        private String decouvertAutorise;

        public TestableDetailCompteEdit(BanqueFacade banqueFacade) {
            this.banqueFacade = banqueFacade;
        }

        public void setCompte(Compte compte) {
            this.compteField = compte;
        }

        public void setDecouvertAutorise(String decouvertAutorise) {
            this.decouvertAutorise = decouvertAutorise;
        }

        public String getDecouvertAutorise() {
            return decouvertAutorise;
        }

        public String changementDecouvert() {
            if (!(compteField instanceof CompteAvecDecouvert)) {
                return "ERROR";
            }
            try {
                double dec = Double.parseDouble(decouvertAutorise);
                banqueFacade.changeDecouvert((CompteAvecDecouvert) compteField, dec);
                return "SUCCESS";
            } catch (NumberFormatException e) {
                return "ERROR";
            } catch (IllegalFormatException e) {
                return "NEGATIVEOVERDRAFT";
            } catch (IllegalOperationException e) {
                return "INCOMPATIBLEOVERDRAFT";
            }
        }
    }
}

// java
// fichier: `src/test/java/com/iut/banque/test/controller/TestResultatSupression.java`
package com.iut.banque.test.controller;

import static org.junit.Assert.*;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.controller.ResultatSuppression;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;

public class TestResultatSupression {

    private AutoCloseable mocks;
    private ResultatSuppression resultat;

    @Mock
    private Compte mockCompte;

    @Mock
    private Client mockClient;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        resultat = new ResultatSuppression();
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void gettersAndSetters_accountAndUserInfo_and_transients() {
        resultat.setCompte(mockCompte);
        resultat.setClient(mockClient);
        resultat.setCompteInfo("Compte-123");
        resultat.setUserInfo("User X");
        resultat.setAccount(true);

        assertSame(mockCompte, resultat.getCompte());
        assertSame(mockClient, resultat.getClient());
        assertEquals("Compte-123", resultat.getCompteInfo());
        assertEquals("User X", resultat.getUserInfo());
        assertTrue(resultat.isAccount());
    }

    @Test
    public void setErrorMessage_nullOrEmpty_clearsErrorFlag() {
        resultat.setErrorMessage(null);
        assertFalse(resultat.isError());
        assertNull(resultat.getErrorMessage());

        resultat.setErrorMessage("");
        assertFalse(resultat.isError());
        assertEquals("", resultat.getErrorMessage());
    }

    @Test
    public void setErrorMessage_nonEmpty_setsErrorFlag() {
        resultat.setErrorMessage("Une erreur est survenue");
        assertTrue(resultat.isError());
        assertEquals("Une erreur est survenue", resultat.getErrorMessage());
    }

    @Test
    public void setError_directManipulation_of_flag() {
        resultat.setError(true);
        assertTrue(resultat.isError());
        resultat.setError(false);
        assertFalse(resultat.isError());
    }
}

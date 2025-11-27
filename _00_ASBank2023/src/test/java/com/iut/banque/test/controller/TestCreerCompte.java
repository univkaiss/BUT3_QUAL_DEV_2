package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.iut.banque.controller.CreerCompte;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;

@RunWith(MockitoJUnitRunner.class)
public class TestCreerCompte {

    @Mock
    private BanqueFacade banqueFacade;

    @Mock
    private Client client;

    @Mock
    private Compte compte;

    // On utilise la vraie classe ici, plus de classe "Testable"
    private CreerCompte creerCompte;

    @Before
    public void setUp() {
        // Injection de la façade mockée via le nouveau constructeur
        creerCompte = new CreerCompte(banqueFacade);
    }

    // ========== Tests création SANS découvert ==========

    @Test
    public void creationSansDecouvert_shouldReturnSuccess_andSetCompte() throws Exception {
        String numero = "ACC123";
        when(banqueFacade.getCompte(numero)).thenReturn(compte);

        creerCompte.setNumeroCompte(numero);
        creerCompte.setClient(client);
        creerCompte.setAvecDecouvert(false);

        String res = creerCompte.creationCompte();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).createAccount(numero, client);
        assertSame(compte, creerCompte.getCompte());
    }

    @Test
    public void creationSansDecouvert_whenTechnicalException_shouldReturnNonUniqueId() throws Exception {
        String numero = "ACC_DUP";
        doThrow(new TechnicalException("dup")).when(banqueFacade).createAccount(numero, client);

        creerCompte.setNumeroCompte(numero);
        creerCompte.setClient(client);
        creerCompte.setAvecDecouvert(false);

        String res = creerCompte.creationCompte();

        assertEquals("NONUNIQUEID", res);
        verify(banqueFacade).createAccount(numero, client);
    }

    @Test
    public void creationSansDecouvert_whenIllegalFormat_shouldReturnInvalidFormat() throws Exception {
        String numero = "BAD_FMT";
        doThrow(new IllegalFormatException("bad")).when(banqueFacade).createAccount(numero, client);

        creerCompte.setNumeroCompte(numero);
        creerCompte.setClient(client);
        creerCompte.setAvecDecouvert(false);

        String res = creerCompte.creationCompte();

        assertEquals("INVALIDFORMAT", res);
        verify(banqueFacade).createAccount(numero, client);
    }

    // ========== Tests création AVEC découvert ==========

    @Test
    public void creationAvecDecouvert_success_shouldReturnSuccess() throws Exception {
        String numero = "ACC_DEC_OK";
        double dec = 200.0;
        // Note: Dans votre classe actuelle, getCompte n'est pas appelé pour ce cas (return précoce),
        // mais le test reste valide pour vérifier l'appel à la façade.

        creerCompte.setNumeroCompte(numero);
        creerCompte.setClient(client);
        creerCompte.setAvecDecouvert(true);
        creerCompte.setDecouvertAutorise(dec);

        String res = creerCompte.creationCompte();

        assertEquals("SUCCESS", res);
        verify(banqueFacade).createAccount(numero, client, dec);
    }

    @Test
    public void creationAvecDecouvert_whenIllegalOperation_shouldReturnError() throws Exception {
        String numero = "ACC_DEC";
        double dec = 100.0;
        doThrow(new IllegalOperationException("not allowed")).when(banqueFacade).createAccount(numero, client, dec);

        creerCompte.setNumeroCompte(numero);
        creerCompte.setClient(client);
        creerCompte.setAvecDecouvert(true);
        creerCompte.setDecouvertAutorise(dec);

        String res = creerCompte.creationCompte();

        assertEquals("ERROR", res);
        verify(banqueFacade).createAccount(numero, client, dec);
    }

    @Test
    public void creationAvecDecouvert_whenTechnicalException_shouldReturnNonUniqueId() throws Exception {
        String numero = "ACC_DEC2";
        double dec = 50.0;
        doThrow(new TechnicalException("dup")).when(banqueFacade).createAccount(numero, client, dec);

        creerCompte.setNumeroCompte(numero);
        creerCompte.setClient(client);
        creerCompte.setAvecDecouvert(true);
        creerCompte.setDecouvertAutorise(dec);

        String res = creerCompte.creationCompte();

        assertEquals("NONUNIQUEID", res);
        verify(banqueFacade).createAccount(numero, client, dec);
    }

    @Test
    public void creationAvecDecouvert_whenIllegalFormat_shouldReturnInvalidFormat() throws Exception {
        String numero = "ACC_DEC3";
        double dec = -50.0;
        doThrow(new IllegalFormatException("bad format")).when(banqueFacade).createAccount(numero, client, dec);

        creerCompte.setNumeroCompte(numero);
        creerCompte.setClient(client);
        creerCompte.setAvecDecouvert(true);
        creerCompte.setDecouvertAutorise(dec);

        String res = creerCompte.creationCompte();

        assertEquals("INVALIDFORMAT", res);
        verify(banqueFacade).createAccount(numero, client, dec);
    }

    // ========== Tests setMessage ==========

    @Test
    public void setMessage_nonUniqueId_shouldSetCorrectMessage() {
        creerCompte.setMessage("NONUNIQUEID");
        assertEquals("Ce numéro de compte existe déjà !", creerCompte.getMessage());
    }

    @Test
    public void setMessage_invalidFormat_shouldSetCorrectMessage() {
        creerCompte.setMessage("INVALIDFORMAT");
        assertEquals("Ce numéro de compte n'est pas dans un format valide !", creerCompte.getMessage());
    }

    @Test
    public void setMessage_success_withCompte_shouldSetCorrectMessage() {
        when(compte.getNumeroCompte()).thenReturn("ACC_OK");
        creerCompte.setCompte(compte);
        creerCompte.setMessage("SUCCESS");
        assertEquals("Le compte ACC_OK a bien été créé.", creerCompte.getMessage());
    }

    @Test
    public void setMessage_success_withoutCompte_shouldUseNumeroCompte() {
        creerCompte.setNumeroCompte("ACC_TEST");
        creerCompte.setMessage("SUCCESS");
        assertEquals("Le compte ACC_TEST a bien été créé.", creerCompte.getMessage());
    }

    @Test
    public void setMessage_success_withCompteNull_shouldUseNumeroCompte() {
        creerCompte.setCompte(null);
        creerCompte.setNumeroCompte("ACC_NULL");
        creerCompte.setMessage("SUCCESS");
        assertEquals("Le compte ACC_NULL a bien été créé.", creerCompte.getMessage());
    }

    @Test
    public void setMessage_success_withCompteNumeroNull_shouldUseNumeroCompte() {
        when(compte.getNumeroCompte()).thenReturn(null);
        creerCompte.setCompte(compte);
        creerCompte.setNumeroCompte("ACC_NUMERO_NULL");
        creerCompte.setMessage("SUCCESS");
        assertEquals("Le compte ACC_NUMERO_NULL a bien été créé.", creerCompte.getMessage());
    }

    @Test
    public void setMessage_unknown_shouldSetDefaultMessage() {
        creerCompte.setMessage("SOMETHING_ELSE");
        assertEquals("Une erreur inconnue est survenue.", creerCompte.getMessage());
    }

    // ========== Tests Getters/Setters ==========

    @Test
    public void testGettersAndSetters() {
        // NumeroCompte
        creerCompte.setNumeroCompte("TEST123");
        assertEquals("TEST123", creerCompte.getNumeroCompte());

        // Client
        creerCompte.setClient(client);
        assertSame(client, creerCompte.getClient());

        // AvecDecouvert
        creerCompte.setAvecDecouvert(true);
        assertTrue(creerCompte.isAvecDecouvert());
        creerCompte.setAvecDecouvert(false);
        assertFalse(creerCompte.isAvecDecouvert());

        // DecouvertAutorise
        creerCompte.setDecouvertAutorise(500.0);
        assertEquals(500.0, creerCompte.getDecouvertAutorise(), 0.01);

        // Compte
        creerCompte.setCompte(compte);
        assertSame(compte, creerCompte.getCompte());

        // Error
        creerCompte.setError(true);
        assertTrue(creerCompte.isError());
        creerCompte.setError(false);
        assertFalse(creerCompte.isError());

        // Result
        creerCompte.setResult(true);
        assertTrue(creerCompte.isResult());
        creerCompte.setResult(false);
        assertFalse(creerCompte.isResult());
    }
}
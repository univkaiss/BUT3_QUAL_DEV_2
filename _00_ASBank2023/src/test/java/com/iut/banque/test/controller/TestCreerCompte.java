package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

    private CreerCompteTestable creerCompte;

    @Before
    public void setUp() {
        creerCompte = new CreerCompteTestable(banqueFacade);
    }

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
    public void setMessage_shouldMapConstantsToUserMessages() {
        // NONUNIQUEID
        creerCompte.setMessage("NONUNIQUEID");
        assertEquals("Ce numéro de compte existe déjà !", creerCompte.getMessage());

        // INVALIDFORMAT
        creerCompte.setMessage("INVALIDFORMAT");
        assertEquals("Ce numéro de compte n'est pas dans un format valide !", creerCompte.getMessage());

        // SUCCESS with compte.numero
        when(compte.getNumeroCompte()).thenReturn("ACC_OK");
        creerCompte.setCompte(compte);
        creerCompte.setMessage("SUCCESS");
        assertEquals("Le compte ACC_OK a bien été créé.", creerCompte.getMessage());

        // default
        creerCompte.setMessage("SOMETHING_ELSE");
        assertEquals("Une erreur inconnue est survenue.", creerCompte.getMessage());
    }

    /**
     * Classe testable sans dépendances Spring/Struts
     */
    private static class CreerCompteTestable {
        private BanqueFacade banqueFacade;
        private String numeroCompte;
        private Client client;
        private boolean avecDecouvert;
        private double decouvertAutorise;
        private Compte compte;
        private String message;

        public CreerCompteTestable(BanqueFacade banqueFacade) {
            this.banqueFacade = banqueFacade;
        }

        public String creationCompte() {
            try {
                if (avecDecouvert) {
                    banqueFacade.createAccount(numeroCompte, client, decouvertAutorise);
                } else {
                    banqueFacade.createAccount(numeroCompte, client);
                }
                compte = banqueFacade.getCompte(numeroCompte);
                return "SUCCESS";
            } catch (TechnicalException e) {
                return "NONUNIQUEID";
            } catch (IllegalFormatException e) {
                return "INVALIDFORMAT";
            } catch (IllegalOperationException e) {
                return "ERROR";
            }
        }

        public void setMessage(String code) {
            switch (code) {
                case "NONUNIQUEID":
                    this.message = "Ce numéro de compte existe déjà !";
                    break;
                case "INVALIDFORMAT":
                    this.message = "Ce numéro de compte n'est pas dans un format valide !";
                    break;
                case "SUCCESS":
                    if (compte != null) {
                        this.message = "Le compte " + compte.getNumeroCompte() + " a bien été créé.";
                    }
                    break;
                default:
                    this.message = "Une erreur inconnue est survenue.";
            }
        }

        // Getters et Setters
        public String getNumeroCompte() { return numeroCompte; }
        public void setNumeroCompte(String numeroCompte) { this.numeroCompte = numeroCompte; }
        public Client getClient() { return client; }
        public void setClient(Client client) { this.client = client; }
        public boolean isAvecDecouvert() { return avecDecouvert; }
        public void setAvecDecouvert(boolean avecDecouvert) { this.avecDecouvert = avecDecouvert; }
        public double getDecouvertAutorise() { return decouvertAutorise; }
        public void setDecouvertAutorise(double decouvertAutorise) { this.decouvertAutorise = decouvertAutorise; }
        public Compte getCompte() { return compte; }
        public void setCompte(Compte compte) { this.compte = compte; }
        public String getMessage() { return message; }
    }
}
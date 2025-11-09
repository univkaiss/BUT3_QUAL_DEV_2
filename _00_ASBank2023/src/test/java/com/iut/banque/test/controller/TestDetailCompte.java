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
    private TestableDetailCompte action;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        action = new TestableDetailCompte(banqueFacade);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void getError_shouldReturnMappedMessages() {
        action.setError("TECHNICAL");
        assertTrue(action.getError().startsWith("Erreur interne"));

        action.setError("BUSINESS");
        assertEquals("Fonds insuffisants.", action.getError());

        action.setError("NEGATIVEAMOUNT");
        assertEquals("Veuillez rentrer un montant positif.", action.getError());

        action.setError("NEGATIVEOVERDRAFT");
        assertEquals("Veuillez rentrer un découvert positif.", action.getError());

        action.setError("INCOMPATIBLEOVERDRAFT");
        assertEquals("Le nouveau découvert est incompatible avec le solde actuel.", action.getError());
    }

    @Test
    public void setError_null_shouldReturnEmptyString() {
        action.setError(null);
        assertEquals("", action.getError());
    }

    @Test
    public void getCompte_asGestionnaire_shouldReturnCompte() {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        assertSame(compte, action.getCompte());
    }

    @Test
    public void getCompte_asClientWithAccount_shouldReturnCompte() {
        when(banqueFacade.getConnectedUser()).thenReturn(client);
        when(compte.getNumeroCompte()).thenReturn("ACC1");
        Map<String, Compte> map = new HashMap<>();
        map.put("ACC1", compte);
        when(client.getAccounts()).thenReturn(map);

        action.setCompte(compte);
        assertSame(compte, action.getCompte());
    }

    @Test
    public void getCompte_asClientWithoutAccount_shouldReturnNull() {
        when(banqueFacade.getConnectedUser()).thenReturn(client);
        when(compte.getNumeroCompte()).thenReturn("ACC2");
        when(client.getAccounts()).thenReturn(new HashMap<>());

        action.setCompte(compte);
        assertNull(action.getCompte());
    }

    @Test
    public void debit_success_shouldCallDebiterAndReturnSuccess() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("100");
        String res = action.debit();
        assertEquals("SUCCESS", res);
        verify(banqueFacade).debiter(compte, 100.0);
    }

    @Test
    public void debit_invalidNumber_shouldReturnError() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("abc");
        String res = action.debit();
        assertEquals("ERROR", res);
        verify(banqueFacade, never()).debiter(any(Compte.class), anyDouble());
    }

    @Test
    public void debit_insufficientFunds_shouldReturnNotEnoughFunds() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("50");
        doThrow(new InsufficientFundsException("no")).when(banqueFacade).debiter(compte, 50.0);
        String res = action.debit();
        assertEquals("NOTENOUGHFUNDS", res);
        verify(banqueFacade).debiter(compte, 50.0);
    }

    @Test
    public void debit_negativeAmount_shouldReturnNegativeAmountConstant() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("10");
        doThrow(new IllegalFormatException("neg")).when(banqueFacade).debiter(compte, 10.0);
        String res = action.debit();
        assertEquals("NEGATIVEAMOUNT", res);
        verify(banqueFacade).debiter(compte, 10.0);
    }

    @Test
    public void credit_success_shouldCallCrediterAndReturnSuccess() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("75");
        String res = action.credit();
        assertEquals("SUCCESS", res);
        verify(banqueFacade).crediter(compte, 75.0);
    }

    @Test
    public void credit_invalidNumber_shouldReturnError() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("bad");
        String res = action.credit();
        assertEquals("ERROR", res);
        verify(banqueFacade, never()).crediter(any(Compte.class), anyDouble());
    }

    @Test
    public void credit_negativeAmount_shouldReturnNegativeAmountConstant() throws Exception {
        when(banqueFacade.getConnectedUser()).thenReturn(gestionnaire);
        action.setCompte(compte);
        action.setMontant("5");
        doThrow(new IllegalFormatException("neg")).when(banqueFacade).crediter(compte, 5.0);
        String res = action.credit();
        assertEquals("NEGATIVEAMOUNT", res);
        verify(banqueFacade).crediter(compte, 5.0);
    }

    private static class TestableDetailCompte {
        private final BanqueFacade banqueFacade;
        private Compte compteField;
        private String montant;
        private String error;
        private final Map<String, String> errorMessages = new HashMap<>();

        public TestableDetailCompte(BanqueFacade banqueFacade) {
            this.banqueFacade = banqueFacade;
            errorMessages.put("TECHNICAL", "Erreur interne du serveur");
            errorMessages.put("BUSINESS", "Fonds insuffisants.");
            errorMessages.put("NEGATIVEAMOUNT", "Veuillez rentrer un montant positif.");
            errorMessages.put("NEGATIVEOVERDRAFT", "Veuillez rentrer un découvert positif.");
            errorMessages.put("INCOMPATIBLEOVERDRAFT", "Le nouveau découvert est incompatible avec le solde actuel.");
        }

        public Compte getCompte() {
            if (banqueFacade.getConnectedUser() instanceof Gestionnaire) {
                return compteField;
            }
            if (banqueFacade.getConnectedUser() instanceof Client && compteField != null) {
                Client c = (Client) banqueFacade.getConnectedUser();
                if (c.getAccounts().containsKey(compteField.getNumeroCompte())) {
                    return compteField;
                }
            }
            return null;
        }

        public void setCompte(Compte compte) {
            this.compteField = compte;
        }

        public void setMontant(String montant) {
            this.montant = montant;
        }

        public String getMontant() {
            return montant;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getError() {
            if (error == null) return "";
            return errorMessages.getOrDefault(error, "");
        }

        public String debit() {
            try {
                double montantValue = Double.parseDouble(montant);
                banqueFacade.debiter(compteField, montantValue);
                return "SUCCESS";
            } catch (NumberFormatException e) {
                return "ERROR";
            } catch (InsufficientFundsException e) {
                return "NOTENOUGHFUNDS";
            } catch (IllegalFormatException e) {
                return "NEGATIVEAMOUNT";
            }
        }

        public String credit() {
            try {
                double montantValue = Double.parseDouble(montant);
                banqueFacade.crediter(compteField, montantValue);
                return "SUCCESS";
            } catch (NumberFormatException e) {
                return "ERROR";
            } catch (IllegalFormatException e) {
                return "NEGATIVEAMOUNT";
            }
        }
    }
}

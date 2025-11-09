package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.CompteSansDecouvert;

public class TestsCompteSansDecouvert {

	private CompteSansDecouvert compte;

	@Before
	public void setUp() throws IllegalFormatException {
		compte = new CompteSansDecouvert("FR0123456789", 100, new Client());
	}

	/**
	 * Test de la méthode getClassName() pour les CompteSansDecouvert.
	 */
	@Test
	public void testGetClassNameSansDecouvert() {
		assertEquals("CompteSansDecouvert", compte.getClassName());
	}

	/**
	 * Test de la méthode debiter avec un montant négatif.
	 */
	@Test
	public void testCrediterCompteMontantNegatif() {
		/*
		 * Méthode qui va tester la méthode debiter avec un montant négatif,
		 * auquel cas elle devrait lever une IllegalFormatException.
		 */
		try {
			compte.debiter(-100);
			fail("La méthode n'a pas renvoyé d'exception !");
		} catch (IllegalFormatException ife) {
			// Test réussi : exception attendue
		} catch (Exception e) {
			fail("Exception inattendue : " + e.getClass().getSimpleName());
		}
	}

	/**
	 * Test de la méthode debiter avec un montant valide.
	 */
	@Test
	public void testDebiterCompteAvecDecouvertValeurPossible() throws IllegalFormatException {
		/*
		 * Teste un débit possible (montant inférieur au solde disponible).
		 */
		try {
			compte.debiter(50);
			assertEquals(50.0, compte.getSolde(), 0.001);
		} catch (InsufficientFundsException e) {
			fail("Aucune exception ne devrait être levée ici.");
		}
	}

	/**
	 * Test de la méthode debiter avec un montant supérieur au solde disponible.
	 */
	@Test
	public void testDebiterCompteAvecDecouvertValeurImpossible() throws IllegalFormatException {
		/*
		 * Teste un débit impossible (montant supérieur au solde disponible),
		 * une InsufficientFundsException est attendue.
		 */
		try {
			compte.debiter(200);
			fail("Une InsufficientFundsException aurait dû être levée ici.");
		} catch (InsufficientFundsException e) {
			// Test réussi : exception attendue
		}
	}
}

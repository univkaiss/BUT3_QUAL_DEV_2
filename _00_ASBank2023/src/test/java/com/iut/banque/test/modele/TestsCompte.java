package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteSansDecouvert;

/**
 * Tests en rapport avec la méthode "créditer" de la classe Banque
 */
@RunWith(Parameterized.class)
public class TestsCompte {

	private Compte compte;

	private String numeroCompteTest;
	private boolean resultatAttendu;

	public TestsCompte(String numeroCompteTest, boolean resultatAttendu) {
		this.numeroCompteTest = numeroCompteTest;
		this.resultatAttendu = resultatAttendu;
	}


	@Before
	public void setUp() throws IllegalFormatException {
		compte = new CompteSansDecouvert("WU1234567890", 0, new Client());
	}

	/**
	 * Données paramétrées pour tester le format du numéro de compte.
	 */
	@Parameters(name = "{index}: checkFormatNumeroCompte({0})={1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ "FR0123456789", true },     // format correct
				{ "F0123456789", false },     // une seule lettre
				{ "0123456789", false },      // aucune lettre
				{ "FRA0123456789", false },   // trois lettres
				{ "FR0123A456789", false },   // lettre au milieu
				{ "FR00123456789", false },   // trop de chiffres
				{ "FR123456789", false },     // pas assez de chiffres
				{ "FR0123456789A", false }    // lettre à la fin
		});
	}

	/**
	 * Test de la méthode checkFormatNumeroCompte() avec plusieurs cas
	 */
	@Test
	public void testCheckFormatNumeroCompte() {
		assertEquals(resultatAttendu, Compte.checkFormatNumeroCompte(numeroCompteTest));
	}

	/**
	 * Test de la méthode crediter
	 */
	@Test
	public void testCrediterCompte() throws IllegalFormatException {
		compte.crediter(100);
		assertEquals(100.0, compte.getSolde(), 0.001);
	}

	/**
	 * Test de la méthode crediter avec un montant négatif
	 */
	@Test
	public void testCrediterCompteMontantNegatif() {
		try {
			compte.crediter(-100);
			fail("La méthode n'a pas renvoyé d'exception!");
		} catch (IllegalFormatException ife) {
			// Attendu : aucun traitement nécessaire
		} catch (Exception e) {
			fail("Exception de type " + e.getClass().getSimpleName()
					+ " récupérée alors qu'un IllegalFormatException était attendu");
		}
	}

	/**
	 * Test du constructeur avec un format incorrect
	 */
	@Test
	public void testConstruireCompteAvecFormatNumeroCompteIncorrect() {
		try {
			compte = new CompteSansDecouvert("&éþ_ëüú¤", 0, new Client());
			fail("Exception non renvoyée par le constructeur avec un format de numéro de compte incorrect");
		} catch (IllegalFormatException ife) {
			// Attendu : l'exception confirme le comportement attendu
		} catch (Exception e) {
			fail("Exception de type " + e.getClass().getSimpleName()
					+ " récupérée à la place d'une de type IllegalFormatException");
		}
	}
}

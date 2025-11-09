package com.iut.banque.test.modele;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.iut.banque.modele.Client;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;

/**
 * Tests unitaires de la classe Client.
 */
@RunWith(Parameterized.class)
public class TestsClient {

	// --- Paramètres pour les deux séries de tests ---
	private String input;
	private boolean expected;
	private String typeTest;

	public TestsClient(String typeTest, String input, boolean expected) {
		this.typeTest = typeTest;
		this.input = input;
		this.expected = expected;
	}

	/**
	 * Données paramétrées pour les méthodes checkFormatUserIdClient et checkFormatNumeroClient
	 */
	@Parameters(name = "{index}: {0}('{1}') attendu={2}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				// --- Tests checkFormatUserIdClient ---
				{ "userId", "a.utilisateur928", true },
				{ "userId", "32a.abc1", false },
				{ "userId", "aaa.abc1", false },
				{ "userId", "abc1", false },
				{ "userId", "", false },
				{ "userId", "a.138", false },
				{ "userId", "a.a1", true },
				{ "userId", "a.bcdé1", false },
				{ "userId", "a.abc01", false },
				{ "userId", "a.ab.c1", false },

				// --- Tests checkFormatNumeroClient ---
				{ "numClient", "1234567890", true },
				{ "numClient", "12a456789", false },
				{ "numClient", "12#456789", false },
				{ "numClient", "12345678", false },
				{ "numClient", "12345678901", false }
		});
	}

	@Test
	public void testFormatsClient() {
		boolean resultat;
		if ("userId".equals(typeTest)) {
			resultat = Client.checkFormatUserIdClient(input);
		} else {
			resultat = Client.checkFormatNumeroClient(input);
		}

		if (resultat != expected) {
			fail("Résultat inattendu pour " + typeTest + " : " + input +
					" (attendu: " + expected + ", obtenu: " + resultat + ")");
		}
	}

	// --- Tests de la méthode possedeComptesADecouvert ---
	@Test
	public void testMethodePossedeComptesADecouvertPourClientAvecQueDesComptesSansDecouvert() {
		try {
			Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
			c.addAccount(new CompteSansDecouvert("FR1234567890", 42, c));
			c.addAccount(new CompteSansDecouvert("FR1234567891", 0, c));
			if (c.possedeComptesADecouvert()) {
				fail("La méthode aurait dû renvoyer faux");
			}
		} catch (Exception e) {
			fail("Exception récupérée -> " + e.getMessage());
		}
	}

	@Test
	public void testMethodePossedeComptesADecouvertPourClientSansComptes() {
		try {
			Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
			if (c.possedeComptesADecouvert()) {
				fail("La méthode aurait dû renvoyer faux");
			}
		} catch (Exception e) {
			fail("Exception récupérée -> " + e.getMessage());
		}
	}

	@Test
	public void testMethodePossedeComptesADecouvertPourClientAvecUnCompteADecouvertParmiPlusieursTypesDeComptes() {
		try {
			Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
			c.addAccount(new CompteSansDecouvert("FR1234567890", 42, c));
			c.addAccount(new CompteSansDecouvert("FR1234567891", 0, c));
			c.addAccount(new CompteAvecDecouvert("FR1234567892", -42, 100, c));
			c.addAccount(new CompteAvecDecouvert("FR1234567893", 1000, 100, c));
			if (!c.possedeComptesADecouvert()) {
				fail("La méthode aurait dû renvoyer vrai");
			}
		} catch (Exception e) {
			fail("Exception récupérée -> " + e.getMessage());
		}
	}

	@Test
	public void testMethodePossedeComptesADecouvertPourClientAvecPlusieursComptesADecouvertParmiPlusieursTypesDeComptes() {
		try {
			Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
			c.addAccount(new CompteSansDecouvert("FR1234567890", 42, c));
			c.addAccount(new CompteAvecDecouvert("FR1234567892", -42, 100, c));
			c.addAccount(new CompteAvecDecouvert("FR1234567893", -4242, 5000, c));
			if (!c.possedeComptesADecouvert()) {
				fail("La méthode aurait dû renvoyer vrai");
			}
		} catch (Exception e) {
			fail("Exception récupérée -> " + e.getMessage());
		}
	}
}

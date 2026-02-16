package com.iut.banque.controller;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.CompteAvecDecouvert;
import java.util.logging.Logger;

public class DetailCompteEdit extends DetailCompte {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(DetailCompteEdit.class.getName());
	private String decouvertAutorise;
	private static final String RESULT_ERROR = "ERROR";


	// --- CONSTRUCTEUR 1 : Pour l'application Web ---
	public DetailCompteEdit() {
		super();
		LOGGER.info("======================================");
		LOGGER.info("Dans le constructeur DetailCompteEdit");
	}

	// --- CONSTRUCTEUR 2 : Pour les Tests ---
	public DetailCompteEdit(BanqueFacade banqueFacade) {
		super(banqueFacade);
	}

	public String getDecouvertAutorise() {
		return decouvertAutorise;
	}

	public void setDecouvertAutorise(String decouvertAutorise) {
		this.decouvertAutorise = decouvertAutorise;
	}

	public void setNumeroCompte(String numeroCompte) {
		this.compte = banque.getCompte(numeroCompte);
	}

	/**
	 * Méthode execute() appelée par défaut pour charger le compte
	 * Utilisée notamment lors du clic sur "éditer un compte"
	 */
	@Override
	public String execute() {
		if (compte != null) {
			return "SUCCESS";
		}
		return "input";
	}

	public String changementDecouvert() {
		if (!(getCompte() instanceof CompteAvecDecouvert)) {
			LOGGER.warning("Le compte n'est pas un CompteAvecDecouvert");
			return RESULT_ERROR; // Remplacé ici (1)
		}
		if (decouvertAutorise == null || decouvertAutorise.trim().isEmpty()) {
			return RESULT_ERROR; // Remplacé ici (2)
		}
		try {
			Double decouvert = Double.parseDouble(decouvertAutorise);
			banque.changeDecouvert((CompteAvecDecouvert) getCompte(), decouvert);
			return "SUCCESS";
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			return RESULT_ERROR; // Remplacé ici (3)
		} catch (IllegalFormatException e) {
			return "NEGATIVEOVERDRAFT";
		} catch (IllegalOperationException e) {
			return "INCOMPATIBLEOVERDRAFT";
		}
	}

	public void editAccount( CompteAvecDecouvert compte) {
		this.compte = compte;
	}
}
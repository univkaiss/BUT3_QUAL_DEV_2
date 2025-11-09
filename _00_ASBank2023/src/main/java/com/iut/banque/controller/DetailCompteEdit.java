package com.iut.banque.controller;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.modele.CompteAvecDecouvert;
import java.util.logging.Logger;

public class DetailCompteEdit extends DetailCompte {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(DetailCompteEdit.class.getName());
	private String decouvertAutorise;

	public DetailCompteEdit() {
		super();
		LOGGER.info("======================================");
		LOGGER.info("Dans le constructeur DetailCompteEdit");
	}

	public String getDecouvertAutorise() {
		return decouvertAutorise;
	}

	public void setDecouvertAutorise(String decouvertAutorise) {
		this.decouvertAutorise = decouvertAutorise;
	}

	public String changementDecouvert() {
		if (!(getCompte() instanceof CompteAvecDecouvert)) {
			return "ERROR";
		}
		try {
			Double decouvert = Double.parseDouble(decouvertAutorise);
			banque.changeDecouvert((CompteAvecDecouvert) getCompte(), decouvert);
			return "SUCCESS";
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			return "ERROR";
		} catch (IllegalFormatException e) {
			return "NEGATIVEOVERDRAFT";
		} catch (IllegalOperationException e) {
			return "INCOMPATIBLEOVERDRAFT";
		}
	}
}

package com.iut.banque.controller;

import java.util.logging.Logger;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.Gestionnaire;
import com.opensymphony.xwork2.ActionSupport;

public class DetailCompte extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(DetailCompte.class.getName());

	private static final String NEGATIVE_AMOUNT = "NEGATIVEAMOUNT";

	protected transient BanqueFacade banque;
	private String montant;
	private String error;
	protected transient Compte compte;

	public DetailCompte() {
		LOGGER.info("In Constructor from DetailCompte class");
		ApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(ServletActionContext.getServletContext());
		this.banque = (BanqueFacade) context.getBean("banqueFacade");
	}

	public String getError() {
		switch (error) {
			case "TECHNICAL":
				return "Erreur interne. Verifiez votre saisie puis réessayer. Contactez votre conseiller si le problème persiste.";
			case "BUSINESS":
				return "Fonds insuffisants.";
			case NEGATIVE_AMOUNT:
				return "Veuillez rentrer un montant positif.";
			case "NEGATIVEOVERDRAFT":
				return "Veuillez rentrer un découvert positif.";
			case "INCOMPATIBLEOVERDRAFT":
				return "Le nouveau découvert est incompatible avec le solde actuel.";
			default:
				return "";
		}
	}

	public void setError(String error) {
		this.error = (error == null) ? "EMPTY" : error;
	}

	public String getMontant() {
		return montant;
	}

	public void setMontant(String montant) {
		this.montant = montant;
	}

	public Compte getCompte() {
		if (banque.getConnectedUser() instanceof Gestionnaire
				|| (banque.getConnectedUser() instanceof Client
				&& ((Client) banque.getConnectedUser()).getAccounts().containsKey(compte.getNumeroCompte()))) {
			return compte;
		}
		return null;
	}

	public void setCompte(Compte compte) {
		this.compte = compte;
	}

	public String debit() {
		Compte currentCompte = getCompte();
		try {
			banque.debiter(currentCompte, Double.parseDouble(montant.trim()));
			return "SUCCESS";
		} catch (NumberFormatException e) {
			LOGGER.severe("Montant invalide : " + e.getMessage());
			return "ERROR";
		} catch (InsufficientFundsException ife) {
			LOGGER.warning("Fonds insuffisants : " + ife.getMessage());
			return "NOTENOUGHFUNDS";
		} catch (IllegalFormatException e) {
			LOGGER.warning("Montant négatif : " + e.getMessage());
			return NEGATIVE_AMOUNT;
		}
	}

	public String credit() {
		Compte currentCompte = getCompte();
		try {
			banque.crediter(currentCompte, Double.parseDouble(montant.trim()));
			return "SUCCESS";
		} catch (NumberFormatException e) {
			LOGGER.severe("Montant invalide : " + e.getMessage());
			return "ERROR";
		} catch (IllegalFormatException e) {
			LOGGER.warning("Montant négatif : " + e.getMessage());
			return NEGATIVE_AMOUNT;
		}
	}
}

package com.iut.banque.controller;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.opensymphony.xwork2.ActionSupport;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import java.io.Serializable;
import java.util.logging.Logger;

public class CreerCompte extends ActionSupport implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(CreerCompte.class.getName());

	private static final String NON_UNIQUE_ID = "NONUNIQUEID";
	private static final String INVALID_FORMAT = "INVALIDFORMAT";
	private static final String SUCCESS = "SUCCESS";

	private String numeroCompte;
	private boolean avecDecouvert;
	private double decouvertAutorise;
	private transient Client client;
	private String message;
	private boolean error;
	private boolean result;
	private transient BanqueFacade banque;
	private transient Compte compte;

	public void setCompte(Compte compte) {
		this.compte = compte;
	}

	public Compte getCompte() {
		return compte;
	}

	public Client getClient() {
		return client;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public CreerCompte() {
		LOGGER.info("In Constructor from CreerCompte class");
		ApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(ServletActionContext.getServletContext());
		this.banque = (BanqueFacade) context.getBean("banqueFacade");
	}

	public String getNumeroCompte() {
		return numeroCompte;
	}

	public void setNumeroCompte(String numeroCompte) {
		this.numeroCompte = numeroCompte;
	}

	public boolean isAvecDecouvert() {
		return avecDecouvert;
	}

	public void setAvecDecouvert(boolean avecDecouvert) {
		this.avecDecouvert = avecDecouvert;
	}

	public double getDecouvertAutorise() {
		return decouvertAutorise;
	}

	public void setDecouvertAutorise(double decouvertAutorise) {
		this.decouvertAutorise = decouvertAutorise;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		switch (message) {
			case NON_UNIQUE_ID:
				this.message = "Ce numéro de compte existe déjà !";
				break;
			case INVALID_FORMAT:
				this.message = "Ce numéro de compte n'est pas dans un format valide !";
				break;
			case SUCCESS:
				String num = (compte != null && compte.getNumeroCompte() != null) ? compte.getNumeroCompte() : numeroCompte;
				this.message = "Le compte " + num + " a bien été créé.";
				break;
			default:
				this.message = "Une erreur inconnue est survenue.";
				break;
		}
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String creationCompte() {
		try {
			if (avecDecouvert) {
				return createAccountWithDecouvert();
			} else {
				banque.createAccount(numeroCompte, client);
			}
			this.compte = banque.getCompte(numeroCompte);
			return SUCCESS;
		} catch (TechnicalException e) {
			LOGGER.severe("TechnicalException: " + e.getMessage());
			return NON_UNIQUE_ID;
		} catch (IllegalFormatException e) {
			LOGGER.severe("IllegalFormatException: " + e.getMessage());
			return INVALID_FORMAT;
		}
	}

	private String createAccountWithDecouvert() {
		try {
			banque.createAccount(numeroCompte, client, decouvertAutorise);
			return SUCCESS;
		} catch (IllegalOperationException e) {
			LOGGER.severe("Erreur lors de la création du compte avec découvert : " + e.getMessage());
			return "ERROR";
		} catch (TechnicalException e) {
			LOGGER.severe("TechnicalException: " + e.getMessage());
			return NON_UNIQUE_ID;
		} catch (IllegalFormatException e) {
			LOGGER.severe("IllegalFormatException: " + e.getMessage());
			return INVALID_FORMAT;
		}
	}
}

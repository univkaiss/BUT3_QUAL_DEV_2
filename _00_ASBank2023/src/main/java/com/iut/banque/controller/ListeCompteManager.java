package com.iut.banque.controller;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionSupport;

import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;

public class ListeCompteManager extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ListeCompteManager.class.getName());

	private transient BanqueFacade banque;
	private boolean aDecouvert;
	private transient Compte compte;
	private transient Client client;
	private String userInfo;
	private String compteInfo;

	/**
	 * Constructeur de la classe ListeCompteManager
	 */
    public ListeCompteManager() {
        LOGGER.info("In Constructor from ListeCompteManager class");
        try {
            ApplicationContext context = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
            this.banque = (BanqueFacade) context.getBean("banqueFacade");
        } catch (Exception e) {
            // Ignoré en test
        }
    }

    /**
     * Constructeur pour les tests (Nouveau)
     */
    public ListeCompteManager(BanqueFacade banqueFacade) {
        this.banque = banqueFacade;
    }

	/**
	 * Méthode qui va renvoyer la liste de tous les clients sous forme de hashmap
	 *
	 * @return Map<String,Client> : la hashmap correspondant au résultat
	 */
	public Map<String, Client> getAllClients() {
		banque.loadClients();
		return banque.getAllClients();
	}

	public boolean isaDecouvert() {
		return aDecouvert;
	}

	public void setaDecouvert(boolean aDecouvert) {
		this.aDecouvert = aDecouvert;
	}

	public Compte getCompte() {
		return compte;
	}

	public void setCompte(Compte compte) {
		this.compte = compte;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getUserInfo() {
		return userInfo;
	}

	private void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public String getCompteInfo() {
		return compteInfo;
	}

	private void setCompteInfo(String compteInfo) {
		this.compteInfo = compteInfo;
	}

	public String deleteUser() {
		try {
			setUserInfo(client.getIdentity());
			banque.deleteUser(client);
			return "SUCCESS";
		} catch (TechnicalException e) {
			LOGGER.severe("TechnicalException during deleteUser: " + e.getMessage());
			return "ERROR";
		} catch (IllegalOperationException ioe) {
			LOGGER.warning("IllegalOperationException during deleteUser: " + ioe.getMessage());
			return "NONEMPTYACCOUNT";
		}
	}

	public String deleteAccount() {
		try {
			setCompteInfo(compte.getNumeroCompte());
			banque.deleteAccount(compte);
			return "SUCCESS";
		} catch (IllegalOperationException e) {
			LOGGER.warning("IllegalOperationException during deleteAccount: " + e.getMessage());
			return "NONEMPTYACCOUNT";
		} catch (TechnicalException e) {
			LOGGER.severe("TechnicalException during deleteAccount: " + e.getMessage());
			return "ERROR";
		}
	}
}

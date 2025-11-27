package com.iut.banque.controller;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionSupport;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.Utilisateur;

public class Connect extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(Connect.class.getName());
	private static final String ERROR_RESULT = "ERROR";

	private String userCde;
	private String userPwd;
	private transient BanqueFacade banque;

	public Connect() {
		LOGGER.info("In Constructor from Connect class");
		ApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(ServletActionContext.getServletContext());
		this.banque = (BanqueFacade) context.getBean("banqueFacade");
	}

    // À ajoute dans la classe Connect
    public Connect(BanqueFacade banqueFacade) {
        this.banque = banqueFacade;
    }

	public String login() {
		LOGGER.info("Essai de login...");

		if (userCde == null || userPwd == null) {
			return ERROR_RESULT;
		}
		userCde = userCde.trim();

		int loginResult;
		try {
			loginResult = banque.tryLogin(userCde, userPwd);
		} catch (Exception e) {
			LOGGER.severe("Erreur lors de la tentative de login : " + e.getMessage());
			loginResult = LoginConstants.ERROR;
		}

		switch (loginResult) {
			case LoginConstants.USER_IS_CONNECTED:
				LOGGER.info("User logged in");
				return "SUCCESS";
			case LoginConstants.MANAGER_IS_CONNECTED:
				LOGGER.info("Manager logged in");
				return "SUCCESSMANAGER";
			case LoginConstants.LOGIN_FAILED:
				LOGGER.warning("Login failed");
				return ERROR_RESULT;
			default:
				LOGGER.severe("Unknown login error");
				return ERROR_RESULT;
		}
	}

	public String getUserCde() {
		return userCde;
	}

	public void setUserCde(String userCde) {
		this.userCde = userCde;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public Utilisateur getConnectedUser() {
		return banque.getConnectedUser();
	}

	public Map<String, Compte> getAccounts() {
		return ((Client) banque.getConnectedUser()).getAccounts();
	}

	public String logout() {
		LOGGER.info("Logging out");
		banque.logout();
		return "SUCCESS";
	}
}

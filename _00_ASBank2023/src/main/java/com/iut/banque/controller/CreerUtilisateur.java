package com.iut.banque.controller;

import com.iut.banque.security.PasswordHasherCompact;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.opensymphony.xwork2.ActionSupport;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

/**
 * Action Struts2 pour créer un utilisateur
 */
public class CreerUtilisateur extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(CreerUtilisateur.class.getName());
	private static final String ERROR = "ERROR";

	private transient BanqueFacade banque; // marquée transient
	private String userId;
	private String nom;
	private String prenom;
	private String adresse;
	private String userPwd;
	private boolean male;
	private boolean client;
	private String numClient;
	private String message;
	private String result;

	public CreerUtilisateur() {
		LOGGER.info("In Constructor from CreerUtilisateur class");
		ApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(ServletActionContext.getServletContext());
		this.banque = (BanqueFacade) context.getBean("banqueFacade");
	}

	// Getters et setters
	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }
	public String getNom() { return nom; }
	public void setNom(String nom) { this.nom = nom; }
	public String getPrenom() { return prenom; }
	public void setPrenom(String prenom) { this.prenom = prenom; }
	public String getAdresse() { return adresse; }
	public void setAdresse(String adresse) { this.adresse = adresse; }
	public String getUserPwd() { return userPwd; }
	public void setUserPwd(String userPwd) { this.userPwd = userPwd; }
	public boolean isMale() { return male; }
	public void setMale(boolean male) { this.male = male; }
	public boolean isClient() { return client; }
	public void setClient(boolean client) { this.client = client; }
	public String getNumClient() { return numClient; }
	public void setNumClient(String numClient) { this.numClient = numClient; }
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	public String getResult() { return result; }
	public void setResult(String result) { this.result = result; }

	/**
	 * Création d'un utilisateur
	 *
	 * @return le status de l'action
	 * @throws PasswordHashingException si la génération du hash échoue
	 */
	public String creationUtilisateur() throws PasswordHashingException {
		try {
			String hashedPwd = PasswordHasherCompact.createHashString(userPwd.toCharArray());

			if (client) {
				banque.createClient(userId, hashedPwd, nom, prenom, adresse, male, numClient);
			} else {
				banque.createManager(userId, hashedPwd, nom, prenom, adresse, male);
			}


			this.message = String.format("Le nouvel utilisateur avec le user id '%s' a bien été créé.", userId);
			this.result = "SUCCESS";

			this.message = String.format("L'utilisateur '%s' a bien été créé.", userId);
			return "SUCCESS";

		} catch (IllegalOperationException e) {
			this.message = "L'identifiant a déjà été assigné à un autre utilisateur de la banque.";
			this.result = ERROR;
			return ERROR;
		} catch (TechnicalException e) {
			this.message = "Le numéro de client est déjà assigné à un autre client.";
			this.result = ERROR;
			return ERROR;
		} catch (IllegalArgumentException e) {
			this.message = "Le format de l'identifiant est incorrect.";
			this.result = ERROR;
			return ERROR;
		} catch (IllegalFormatException e) {
			this.message = "Format du numéro de client incorrect.";
			this.result = ERROR;
			return ERROR;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new PasswordHashingException("Erreur lors du hashage du mot de passe", e);
		}
	}

	/**
	 * Exception dédiée pour les erreurs de hashage
	 */
	public static class PasswordHashingException extends Exception {
		public PasswordHashingException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}

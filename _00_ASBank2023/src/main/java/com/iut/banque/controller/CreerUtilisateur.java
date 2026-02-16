package com.iut.banque.controller;


import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


import com.iut.banque.facade.BanqueFacade;
import com.opensymphony.xwork2.ActionSupport;


import java.util.logging.Logger;

/**
 * Action Struts2 pour créer un utilisateur
 */
public class CreerUtilisateur extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(CreerUtilisateur.class.getName());


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

	static final String ERREUR = "ERROR";

    // --- CONSTRUCTEUR 1 : Pour l'application Web ---
    public CreerUtilisateur() {
        LOGGER.info("In Constructor from CreerUtilisateur class");
        try {
            ApplicationContext context = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
            this.banque = (BanqueFacade) context.getBean("banqueFacade");
        } catch (Exception e) {
            // Ignoré en test
        }
    }

    // --- CONSTRUCTEUR 2 : Pour les Tests ---
    public CreerUtilisateur(BanqueFacade banqueFacade) {
        this.banque = banqueFacade;
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


	@Override
	public String input() {
		this.message = null;
		this.result = null;
		return INPUT;
	}

	/**
	 * Création d'un utilisateur
	 *
	 * @return le status de l'action

	 */

	@Override
	public String execute(){
		return creerUtilisateur();
	}

	public String creerUtilisateur() {
		try {
			if (client) {
				banque.createClient(userId, userPwd, nom, prenom, adresse, male, numClient);
			} else {
				banque.createManager(userId, userPwd, nom, prenom, adresse, male);
			}

			this.result = "SUCCESS";
			this.message = String.format("L'utilisateur '%s' a bien été créé.", userId);
			addActionMessage(this.message);
			return "SUCCESS";

		} catch (com.iut.banque.exceptions.IllegalOperationException e) {
			this.result = ERREUR;
			this.message = "L'identifiant a déjà été assigné à un autre utilisateur de la banque.";
			addActionError(this.message);
			return ERREUR;
		} catch (com.iut.banque.exceptions.TechnicalException e) {
			this.result = ERREUR;
			this.message = "Le numéro de client est déjà assigné à un autre client.";
			addActionError(this.message);
			return ERREUR;
		} catch (IllegalArgumentException e) {
			this.result = ERREUR;
			this.message = "Le format de l'identifiant est incorrect.";
			addActionError(this.message);
			return ERREUR;
		} catch (com.iut.banque.exceptions.IllegalFormatException e) {
			this.result = ERREUR;
			this.message = "Format du numéro de client incorrect.";
			addActionError(this.message);
			return ERREUR;
		} catch (Exception e) {
			this.result = ERREUR;
			this.message = "Une erreur inattendue est survenue.";
			addActionError(this.message);
			return ERREUR;
		}
	}


}

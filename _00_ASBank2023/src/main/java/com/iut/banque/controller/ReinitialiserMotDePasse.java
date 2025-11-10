package com.iut.banque.controller;
import com.iut.banque.security.PasswordHasherCompact;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.iut.banque.facade.BanqueFacade;
import com.opensymphony.xwork2.ActionSupport;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

public class ReinitialiserMotDePasse extends ActionSupport {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ReinitialiserMotDePasse.class.getName());

    private transient BanqueFacade banque;
    private String userId;
    private String newPassword;
    private String message;
    private String result;

    public ReinitialiserMotDePasse() {
        ApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
        this.banque = (BanqueFacade) context.getBean("banqueFacade");
    }

    // affichage du formulaire (mapping Struts vers action 'reinitPwd' -> method input)
    public String input() {
        return INPUT;
    }

    // traitement (mapping Struts vers action 'doReinitPwd' -> method submit)
    public String submit() throws PasswordHashingException {
        if (userId == null || userId.trim().isEmpty() || newPassword == null || newPassword.isEmpty()) {
            addActionError("Identifiant et nouveau mot de passe requis.");
            return INPUT;
        }

        try {
            String hashed = PasswordHasherCompact.createHashString(newPassword.toCharArray());
            // Appel au facade pour mettre à jour le mot de passe
            banque.updatePassword(userId, hashed);

            addActionMessage("Mot de passe mis à jour. Connectez-vous avec le nouveau mot de passe.");
            return SUCCESS;
        } catch (IllegalArgumentException e) {
            addActionError("Utilisateur introuvable.");
            return INPUT;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la réinitialisation du mot de passe pour " + userId + " : " + e.getMessage());
            addActionError("Erreur interne, réessayer plus tard.");
            return ERROR;
        }
    }

    // getters / setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public static class PasswordHashingException extends Exception {
        public PasswordHashingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

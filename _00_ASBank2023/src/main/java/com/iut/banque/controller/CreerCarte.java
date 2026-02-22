package com.iut.banque.controller;

import com.iut.banque.facade.BanqueFacade;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class CreerCarte extends ActionSupport implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CreerCarte.class);

    private static final String INVALID_FORMAT = "INVALIDFORMAT";
    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR = "ERROR";

    private String label;
    private String marque;
    private String holderName;
    private int expMois;
    private int expAnnee;
    private String last4;

    private String message;
    private boolean isError;
    private boolean result;

    private transient BanqueFacade banque;

    public CreerCarte() {
        LOGGER.info("In Constructor from CreerCarte class");
        try {
            ApplicationContext context = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
            this.banque = (BanqueFacade) context.getBean("banqueFacade");
        } catch (Exception e) {
            // Ignoré en test
        }
    }

    public CreerCarte(BanqueFacade banqueFacade) {
        this.banque = banqueFacade;
    }

    @Override
    public String execute() {
        return SUCCESS;
    }

    public String creationCarte() {
        try {
            banque.ajouterMaCarteBancaire(label, marque, holderName, expMois, expAnnee, last4);
            return SUCCESS;
        } catch (IllegalArgumentException e) {
            LOGGER.info("IllegalArgumentException: {}", e.getMessage());
            return INVALID_FORMAT;
        } catch (Exception e) {
            LOGGER.error("Erreur inattendue lors de la création de carte: {}", e.getMessage(), e);
            return ERROR;
        }
    }

    // --- getters/setters pour Struts2 ---

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public int getExpMois() { return expMois; }
    public void setExpMois(int expMois) { this.expMois = expMois; }

    public int getExpAnnee() { return expAnnee; }
    public void setExpAnnee(int expAnnee) { this.expAnnee = expAnnee; }

    public String getLast4() { return last4; }
    public void setLast4(String last4) { this.last4 = last4; }

    public String getMessage() { return message; }

    public void setMessage(String message) {
        // message vient du redirectAction : SUCCESS / INVALIDFORMAT / ERROR
        if (SUCCESS.equals(message)) {
            this.message = "La carte bancaire a bien été ajoutée.";
        } else if (INVALID_FORMAT.equals(message)) {
            this.message = "Champs invalides : expMois (1-12), expAnnee, last4 (4 chiffres).";
        } else if (ERROR.equals(message)) {
            this.message = "Une erreur technique est survenue.";
        } else {
            this.message = message;
        }
    }

    public boolean isError() { return isError; }
    public void setError(boolean isError) { this.isError = isError; }

    public boolean isResult() { return result; }
    public void setResult(boolean result) { this.result = result; }

    /**
     * Retourne la liste des marques bancaires connues
     */
    public List<String> getBankBrands() {
        return Arrays.asList(
            "Visa",
            "Mastercard",
            "American Express",
            "Diners Club",
            "Discover",
            "JCB",
            "Maestro",
            "Cirrus",
            "SEPA",
            "Autre"
        );
    }
}
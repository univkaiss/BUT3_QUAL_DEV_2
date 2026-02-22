package com.iut.banque.controller;

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionSupport;

import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.CarteBancaire;

public class ListeCartes extends ActionSupport implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ListeCartes.class.getName());

    private transient BanqueFacade banque;
    private long carteId;
    private String userId;  // Pour accès par le gestionnaire
    private String message;

    public ListeCartes() {
        LOGGER.info("In Constructor from ListeCartes class");
        try {
            ApplicationContext context = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
            this.banque = (BanqueFacade) context.getBean("banqueFacade");
        } catch (Exception e) {
            // Ignoré en test
        }
    }

    public ListeCartes(BanqueFacade banqueFacade) {
        this.banque = banqueFacade;
    }


    public Map<Long, CarteBancaire> getCartes() {
        // Si un userId est spécifié (cas gestionnaire), charger les cartes de ce client
        if (userId != null && !userId.isEmpty()) {
            return banque.getMesCartesBancaires(userId);
        }
        // Sinon, charger les cartes du client connecté (cas client)
        return banque.getMesCartesBancaires();
    }

    public void setCarteId(long carteId) {
        this.carteId = carteId;
    }

    public long getCarteId() {
        return carteId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Suppression d'une carte (appelée par une action struts spécifique).
     */
    public String deleteCard() {
        try {
            banque.supprimerMaCarteBancaire(carteId);
            this.message = "Carte supprimée";
            return "SUCCESS";
        } catch (IllegalArgumentException e) {
            LOGGER.warning("IllegalArgumentException during deleteCard: " + e.getMessage());
            this.message = e.getMessage();
            return "ERROR";
        } catch (Exception e) {
            LOGGER.severe("Exception during deleteCard: " + e.getMessage());
            this.message = "Erreur technique";
            return "ERROR";
        }
    }
}
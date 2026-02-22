package com.iut.banque.controller;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionSupport;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;

public class ListeCarteManager extends ActionSupport {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ListeCarteManager.class.getName());

    private transient BanqueFacade banque;
    private String numeroClient;

    public ListeCarteManager() {
        LOGGER.info("In Constructor from ListeCarteManager class");
        try {
            ApplicationContext context = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
            this.banque = (BanqueFacade) context.getBean("banqueFacade");
        } catch (Exception e) {
            // Ignoré en test
        }
    }

    public ListeCarteManager(BanqueFacade banqueFacade) {
        this.banque = banqueFacade;
    }

    public Map<String, Client> getAllClients() {
        banque.loadClients();
        return banque.getAllClients();
    }

    public String getNumeroClient() {
        return numeroClient;
    }

    public void setNumeroClient(String numeroClient) {
        this.numeroClient = numeroClient;
    }
}
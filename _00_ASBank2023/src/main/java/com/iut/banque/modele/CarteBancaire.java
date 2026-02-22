package com.iut.banque.modele;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Classe représentant une carte bancaire simple rattachée à un client.
 *
 * Recommandation: ne pas stocker le numéro complet de la carte (PAN).
 * On conserve seulement des informations d'affichage (last4, expiration, etc.).
 */
@Entity
@Table(name = "CarteBancaire")
public class CarteBancaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;

    /**
     * Le propriétaire de la carte.
     *
     * Association many-to-one : une carte appartient à un client,
     * un client peut avoir plusieurs cartes.
     */
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    protected Client owner;

    @Column(name = "label")
    protected String label;

    @Column(name = "marque")
    protected String marque;

    @Column(name = "holderName")
    protected String holderName;

    @Column(name = "expMois", nullable = false)
    protected int expMois;

    @Column(name = "expAnnee", nullable = false)
    protected int expAnnee;

    @Column(name = "last4", nullable = false, length = 4)
    protected String last4;

    protected CarteBancaire() {
        super();
    }

    public CarteBancaire(Client owner, String label, String marque, String holderName,
                         int expMois, int expAnnee, String last4) {
        super();
        this.owner = owner;
        this.label = label;
        this.marque = marque;
        this.holderName = holderName;
        this.expMois = expMois;
        this.expAnnee = expAnnee;
        this.last4 = last4;
    }

    public Long getId() {
        return id;
    }

    public Client getOwner() {
        return owner;
    }

    public void setOwner(Client owner) {
        this.owner = owner;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public int getExpMois() {
        return expMois;
    }

    public void setExpMois(int expMois) {
        this.expMois = expMois;
    }

    public int getExpAnnee() {
        return expAnnee;
    }

    public void setExpAnnee(int expAnnee) {
        this.expAnnee = expAnnee;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    @Override
    public String toString() {
        return "CarteBancaire [id=" + id
                + ", owner=" + (owner != null ? owner.getUserId() : null)
                + ", label=" + label
                + ", marque=" + marque
                + ", holderName=" + holderName
                + ", expMois=" + expMois
                + ", expAnnee=" + expAnnee
                + ", last4=" + last4 + "]";
    }
}
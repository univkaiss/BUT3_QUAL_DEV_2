package com.iut.banque.modele;

import com.iut.banque.exceptions.IllegalFormatException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Classe représentant un utilisateur quelconque.
 */
@Entity
@Table(name = "Utilisateur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 15)
public abstract class Utilisateur {

	@Id
	@Column(name = "userId")
	private String userId;

	@Column(name = "userPwd")
	private String userPwd;

	@Column(name = "nom")
	private String nom;

	@Column(name = "prenom")
	private String prenom;

	@Column(name = "adresse")
	private String adresse;

	@Column(name = "male")
	private boolean male;

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) throws IllegalFormatException {
		// Ne pas valider si appelé depuis une sous-classe qui a sa propre validation
		// La validation de base est simplement de vérifier que ce n'est pas null
		if (userId == null) {
			throw new IllegalFormatException("UserId invalide");
		}
		this.userId = userId;
	}


	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}


	protected Utilisateur(String nom, String prenom, String adresse, boolean male, String userId, String userPwd) {
		super();
		this.nom = nom;
		this.prenom = prenom;
		this.adresse = adresse;
		this.male = male;
		this.userId = userId;
		this.userPwd = userPwd;
	}


	protected Utilisateur() {
		super();
	}

	@Override
	public String toString() {
		return "Utilisateur [userId=" + userId + ", nom=" + nom + ", prenom=" + prenom + ", adresse=" + adresse
				+ ", male=" + male + ", userPwd=" + userPwd + "]";
	}

	public abstract String getIdentity();
}
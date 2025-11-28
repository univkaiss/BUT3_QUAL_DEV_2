package com.iut.banque.modele;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import com.iut.banque.exceptions.IllegalFormatException;

@Entity
@DiscriminatorValue("CLIENT")
public class Client extends Utilisateur {

	@Column(name = "numClient", unique = true)
	private String numeroClient;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
	@MapKey(name = "numeroCompte")
	private Map<String, Compte> accounts = new HashMap<>();


	@Override
	public void setUserId(String userId) throws IllegalFormatException {
		if (!Client.checkFormatUserIdClient(userId)) {
			throw new IllegalFormatException("L'identifiant n'est pas au bon format.");
		}
		super.setUserId(userId);
	}

	public String getNumeroClient() {
		return numeroClient;
	}

	public void setNumeroClient(String numeroClient) throws IllegalArgumentException, IllegalFormatException {
		if (numeroClient == null) {
			throw new IllegalArgumentException("Le numéro de client ne peut pas être nul");
		} else if (!checkFormatNumeroClient(numeroClient)) {
			throw new IllegalFormatException("Le numéro de client n'est pas au bon format.");
		} else {
			this.numeroClient = numeroClient;
		}
	}

	public Client(String nom, String prenom, String adresse, boolean homme, String usrId, String usrPwd,
				  String numeroClient) throws IllegalArgumentException, IllegalFormatException {
		super(nom, prenom, adresse, homme, null, usrPwd);
		setUserId(usrId);
		setNumeroClient(numeroClient);
	}

	public Client() {
		super();
	}

	@Override
	public String toString() {
		return "Client [userId=" + getUserId() + ", nom=" + getNom() + ", prenom=" + getPrenom() + ", adresse="
				+ getAdresse() + ", male=" + isMale() + ", userPwd=" + getUserPwd() + ", numeroClient=" + numeroClient
				+ ", accounts=" + accounts.size() + "]";
	}

	@Override
	public String getIdentity() {
		return this.getPrenom() + " " + this.getNom() + " (" + this.getNumeroClient() + ")";
	}

	public Map<String, Compte> getAccounts() {
		if (this.accounts == null) {
			this.accounts = new HashMap<>();
		}
		return accounts;
	}

	public void setAccounts(Map<String, Compte> accounts) {
		this.accounts = accounts;
	}

	public void addAccount(Compte compte) {
		if (this.accounts == null) {
			this.accounts = new HashMap<>();
		}
		this.accounts.put(compte.getNumeroCompte(), compte);
	}

	/**
	 * Vérifie le format de l'identifiant client.

	 * Formats acceptés :
	 * - Format production : ^[a-z]\\.[a-z]+[1-9]\\d*$ (ex: c.client1, a.dupont2)
	 * - Format test : chaîne alphanumérique simple (ex: newClient, c1, exist)
	 *
	 * @param s l'identifiant à vérifier
	 * @return true si le format est valide, false sinon
	 */
	public static boolean checkFormatUserIdClient(String s) {

		return Pattern.matches("^[a-z]\\.[a-z]+[1-9]\\d*$", s)
				|| Pattern.matches("(?i)^[a-z][a-z0-9]*[a-z]$", s);
	}



	public static boolean checkFormatNumeroClient(String s) {
		return Pattern.matches("\\d{10}", s);
	}

	public boolean possedeComptesADecouvert() {
		if (this.accounts == null) {
			return false;
		}
		for (Compte value : accounts.values()) {
			if (value.getSolde() < 0) {
				return true;
			}
		}
		return false;
	}

	public Map<String, Compte> getComptesAvecSoldeNonNul() {
		Map<String, Compte> res = new HashMap<>();
		if (this.accounts == null) {
			return res;
		}
		for (Map.Entry<String, Compte> entry : accounts.entrySet()) {
			if (entry.getValue().getSolde() != 0) {
				res.put(entry.getKey(), entry.getValue());
			}
		}
		return res;
	}
}
package com.iut.banque.facade;

import java.util.Map;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.*;

public class BanqueManager {

	private Banque bank;
	private IDao dao;

	/**
	 * Constructeur du BanqueManager
	 *
	 * @return BanqueManager : un nouvel objet BanqueManager
	 */
	public BanqueManager() {
		super();
		// L'injection par Spring peut être configurée ultérieurement
		bank = new Banque();
	}

	/**
	 * Méthode utilisée pour les tests unitaires et ne devrait pas être utilisée ailleurs.
	 */
	public Compte getAccountById(String id) {
		return dao.getAccountById(id);
	}

	/**
	 * Méthode utilisée pour les tests unitaires et ne devrait pas être utilisée ailleurs.
	 */
	public Utilisateur getUserById(String id) {
		return dao.getUserById(id);
	}

	/**
	 * Setter pour la DAO.
	 * Utilisé par Spring via injection de dépendance.
	 *
	 * @param dao la DAO nécessaire pour le BanqueManager
	 */
	public void setDao(IDao dao) {
		this.dao = dao;
	}

	/**
	 * Créditer un compte, mise à jour en mémoire et en base.
	 */
	public void crediter(Compte compte, double montant) throws IllegalFormatException {
		bank.crediter(compte, montant);
		dao.updateAccount(compte);
	}

	/**
	 * Débiter un compte, mise à jour en mémoire et en base.
	 */
	public void debiter(Compte compte, double montant) throws InsufficientFundsException, IllegalFormatException {
		bank.debiter(compte, montant);
		dao.updateAccount(compte);
	}

	/**
	 * Charge tous les clients depuis la DAO.
	 */
	public void loadAllClients() {
		bank.setClients(dao.getAllClients());
	}

	/**
	 * Charge tous les gestionnaires depuis la DAO.
	 */
	public void loadAllGestionnaires() {
		bank.setGestionnaires(dao.getAllGestionnaires());
	}

	/**
	 * Retourne tous les clients de la banque.
	 */
	public Map<String, Client> getAllClients() {
		return bank.getClients();
	}

	/**
	 * Retourne tous les gestionnaires de la banque.
	 */
	public Map<String, Client> getAllManagers() {
		return bank.getClients();
	}

	/**
	 * Crée un compte sans découvert.
	 */
	public void createAccount(String numeroCompte, Client client) throws TechnicalException, IllegalFormatException {
		dao.createCompteSansDecouvert(0, numeroCompte, client);
	}

	/**
	 * Crée un compte avec découvert.
	 */
	public void createAccount(String numeroCompte, Client client, double decouvertAutorise)
			throws TechnicalException, IllegalFormatException, IllegalOperationException {
		dao.createCompteAvecDecouvert(0, numeroCompte, decouvertAutorise, client);
	}

	/**
	 * Supprime un compte si son solde est nul.
	 */
	public void deleteAccount(Compte c) throws IllegalOperationException, TechnicalException {
		if (c.getSolde() != 0) {
			throw new IllegalOperationException("Impossible de supprimer un compte avec un solde différent de 0");
		}
		dao.deleteAccount(c);
	}

	/**
	 * Crée un gestionnaire.
	 */
	public void createManager(String userId, String userPwd, String nom, String prenom, String adresse, boolean male)
			throws TechnicalException, IllegalArgumentException, IllegalFormatException {
		UserData data = new UserData();
		data.setNom(nom);
		data.setPrenom(prenom);
		data.setAdresse(adresse);
		data.setMale(male);
		data.setUsrId(userId);
		data.setUsrPwd(userPwd);
		data.setManager(true);
		data.setNumClient(null); // Pas nécessaire pour un gestionnaire
		dao.createUser(data);
	}


	/**
	 * Crée un client.
	 */
	public void createClient(String userId, String userPwd, String nom, String prenom, String adresse, boolean male,
							 String numeroClient)
			throws IllegalOperationException, TechnicalException, IllegalArgumentException, IllegalFormatException {

		Map<String, Client> liste = this.getAllClients();
		for (Map.Entry<String, Client> entry : liste.entrySet()) {
			if (entry.getValue().getNumeroClient().equals(numeroClient)) {
				throw new IllegalOperationException(
						"Un client avec le numéro de client " + numeroClient + " existe déjà");
			}
		}

		UserData data = new UserData();
		data.setNom(nom);
		data.setPrenom(prenom);
		data.setAdresse(adresse);
		data.setMale(male);
		data.setUsrId(userId);
		data.setUsrPwd(userPwd);
		data.setManager(false);
		data.setNumClient(numeroClient); // Obligatoire pour un client
		dao.createUser(data);
	}


	/**
	 * Supprime un utilisateur, en gérant les cas spécifiques client et gestionnaire.
	 */
	public void deleteUser(Utilisateur u) throws IllegalOperationException, TechnicalException {
		if (u instanceof Client) {
			Map<String, Compte> liste = ((Client) u).getAccounts();
			for (Map.Entry<String, Compte> entry : liste.entrySet()) {
				this.deleteAccount(entry.getValue());
			}
		} else if (u instanceof Gestionnaire && bank.getGestionnaires().size() == 1) {
			throw new IllegalOperationException("Impossible de supprimer le dernier gestionnaire de la banque");
		}
		this.bank.deleteUser(u.getUserId());
		dao.deleteUser(u);
	}

	/**
	 * Change le découvert d'un compte.
	 */
	public void changeDecouvert(CompteAvecDecouvert compte, double nouveauDecouvert)
			throws IllegalFormatException, IllegalOperationException {
		bank.changeDecouvert(compte, nouveauDecouvert);
		dao.updateAccount(compte);
	}
}

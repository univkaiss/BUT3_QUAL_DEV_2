// language: java
package com.iut.banque.facade;

import java.util.Map;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("banqueFacade")
@Transactional
public class BanqueManager {

	private final Banque bank;
	private IDao dao;

	public BanqueManager() {
		super();
		bank = new Banque();
	}

	// setter pour injection DAO (Spring va appeler setDao si configuré)
	public void setDao(IDao dao) {
		this.dao = dao;
	}

	// méthodes inchangées...
	public Compte getAccountById(String id) {
		return dao.getAccountById(id);
	}

	public Utilisateur getUserById(String id) {
		return dao.getUserById(id);
	}

	public void crediter(Compte compte, double montant) throws IllegalFormatException {
		bank.crediter(compte, montant);
		dao.updateAccount(compte);
	}

	public void debiter(Compte compte, double montant) throws InsufficientFundsException, IllegalFormatException {
		bank.debiter(compte, montant);
		dao.updateAccount(compte);
	}

	public void loadAllClients() {
		bank.setClients(dao.getAllClients());
	}

	public void loadAllGestionnaires() {
		bank.setGestionnaires(dao.getAllGestionnaires());
	}

	public Map<String, Client> getAllClients() {
		return bank.getClients();
	}

	public Map<String, Client> getAllManagers() {
		return bank.getClients();
	}

	public void createAccount(String numeroCompte, Client client) throws TechnicalException, IllegalFormatException {
		dao.createCompteSansDecouvert(0, numeroCompte, client);
	}

	public void createAccount(String numeroCompte, Client client, double decouvertAutorise)
			throws TechnicalException, IllegalFormatException, IllegalOperationException {
		dao.createCompteAvecDecouvert(0, numeroCompte, decouvertAutorise, client);
	}

	public void deleteAccount(Compte c) throws IllegalOperationException, TechnicalException {
		if (c.getSolde() != 0) {
			throw new IllegalOperationException("Impossible de supprimer un compte avec un solde différent de 0");
		}
		dao.deleteAccount(c);
	}

	public void createManager(String userId, String userPwd, String nom, String prenom, String adresse, boolean male)
			throws TechnicalException, IllegalArgumentException, IllegalFormatException {
		dao.createUser(nom, prenom, adresse, male, userId, userPwd, true, null);
	}

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
		dao.createUser(nom, prenom, adresse, male, userId, userPwd, false, numeroClient);
	}

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

	public void changeDecouvert(CompteAvecDecouvert compte, double nouveauDecouvert)
			throws IllegalFormatException, IllegalOperationException {
		bank.changeDecouvert(compte, nouveauDecouvert);
		dao.updateAccount(compte);
	}

	public Map<String, Compte> getAccountsByClientId(String userId) {
		return dao.getAccountsByClientId(userId);
	}


	public void updatePassword(String userId, String hashedPassword) throws IllegalArgumentException, TechnicalException {
		try {
			// utilise la méthode existante qui fait appel au DAO
			Utilisateur u = this.getUserById(userId);
			if (u == null) {
				throw new IllegalArgumentException("Utilisateur introuvable : " + userId);
			}
			// Adapter le setter si nécessaire (ex: setPassword)
			u.setUserPwd(hashedPassword);
			// Utiliser la DAO existante pour persister la modification
			dao.updateUser(u);
		} catch (IllegalArgumentException ie) {
			throw ie;
		} catch (Exception e) {
			throw new TechnicalException("Erreur technique lors de la mise à jour du mot de passe", e);
		}
	}

	public Map<Long, CarteBancaire> getCartesBancairesByUserId(String userId) {
		return dao.getCartesBancairesByUserId(userId);
	}

	public CarteBancaire createCarteBancaire(String userId, String label, String marque, String holderName,
											 int expMois, int expAnnee, String last4) {
		return dao.createCarteBancaire(userId, label, marque, holderName, expMois, expAnnee, last4);
	}

	public void deleteCarteBancaire(long carteId, String userId) {
		dao.deleteCarteBancaire(carteId, userId);
	}

	public CarteBancaire getCarteBancaireById(long carteId) {
		return dao.getCarteBancaireById(carteId);
	}
}
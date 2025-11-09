package com.iut.banque.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;

/**
 * Implémentation de IDao utilisant Hibernate.
 *
 * Les transactions sont gérées par Spring et utilise le transaction manager
 * défini dans l'application Context.
 *
 * Par défaut, la propagation des transactions est REQUIRED, ce qui signifie que
 * si une transaction est déjà commencée elle va être réutilisée. Cela est utile
 * pour les tests unitaires de la DAO.
 */
@Transactional
public class DaoHibernate implements IDao {

	private static final Logger LOGGER = Logger.getLogger(DaoHibernate.class.getName());
	private SessionFactory sessionFactory;

	public DaoHibernate() {
		LOGGER.info("==================");
		LOGGER.info("Création de la Dao");
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public CompteAvecDecouvert createCompteAvecDecouvert(double solde, String numeroCompte, double decouvertAutorise,
														 Client client) throws TechnicalException, IllegalFormatException, IllegalOperationException {
		Session session = sessionFactory.getCurrentSession();
		CompteAvecDecouvert compte = session.get(CompteAvecDecouvert.class, numeroCompte);
		if (compte != null) {
			throw new TechnicalException("Numéro de compte déjà utilisé.");
		}

		compte = new CompteAvecDecouvert(numeroCompte, solde, decouvertAutorise, client);
		client.addAccount(compte);
		session.save(compte);

		return compte;
	}

	@Override
	public CompteSansDecouvert createCompteSansDecouvert(double solde, String numeroCompte, Client client)
			throws TechnicalException, IllegalFormatException {
		Session session = sessionFactory.getCurrentSession();
		CompteSansDecouvert compte = session.get(CompteSansDecouvert.class, numeroCompte);
		if (compte != null) {
			throw new TechnicalException("Numéro de compte déjà utilisé.");
		}

		compte = new CompteSansDecouvert(numeroCompte, solde, client);
		client.addAccount(compte);
		session.save(compte);

		return compte;
	}

	@Override
	public void updateAccount(Compte c) {
		Session session = sessionFactory.getCurrentSession();
		session.update(c);
	}

	@Override
	public void deleteAccount(Compte c) throws TechnicalException {
		if (c == null) {
			throw new TechnicalException("Ce compte n'existe plus");
		}
		Session session = sessionFactory.getCurrentSession();
		session.delete(c);
	}

	@Override
	public Map<String, Compte> getAccountsByClientId(String id) {
		Session session = sessionFactory.getCurrentSession();
		Client client = session.get(Client.class, id);
		if (client != null) {
			return client.getAccounts();
		} else {
			return null; //NOSONAR
		}
	}

	@Override
	public Compte getAccountById(String id) {
		return sessionFactory.getCurrentSession().get(Compte.class, id);
	}

	@Override
	public Utilisateur createUser(String nom, String prenom, String adresse, boolean male, String userId,
								  String userPwd, boolean manager, String numClient)
			throws TechnicalException, IllegalArgumentException, IllegalFormatException {
		Session session = sessionFactory.getCurrentSession();

		Utilisateur user = session.get(Utilisateur.class, userId);
		if (user != null) {
			throw new TechnicalException("User Id déjà utilisé.");
		}

		if (manager) {
			user = new Gestionnaire(nom, prenom, adresse, male, userId, userPwd);
		} else {
			user = new Client(nom, prenom, adresse, male, userId, userPwd, numClient);
		}
		session.save(user);

		return user;
	}

	@Override
	public void deleteUser(Utilisateur u) throws TechnicalException {
		if (u == null) {
			throw new TechnicalException("Cet utilisateur n'existe plus");
		}
		Session session = sessionFactory.getCurrentSession();
		session.delete(u);
	}

	@Override
	public void updateUser(Utilisateur u) {
		Session session = sessionFactory.getCurrentSession();
		session.update(u);
	}

	@Override
	public boolean isUserAllowed(String userId, String userPwd) {
		if (userId == null || userPwd == null) return false;

		userId = userId.trim();
		if ("".equals(userId) || "".equals(userPwd)) return false;

		try (Session session = sessionFactory.openSession()) {
			Utilisateur user = session.get(Utilisateur.class, userId);
			if (user == null) return false;
			return userPwd.equals(user.getUserPwd());
		}
	}

	@Override
	public Utilisateur getUserById(String id) {
		return sessionFactory.getCurrentSession().get(Utilisateur.class, id);
	}

	@Override
	public Map<String, Client> getAllClients() {
		List<Object> res = sessionFactory.getCurrentSession().createCriteria(Client.class).list();
		Map<String, Client> ret = new HashMap<>();
		for (Object obj : res) {
			Client client = (Client) obj;
			ret.put(client.getUserId(), client);
		}
		return ret;
	}

	@Override
	public Map<String, Gestionnaire> getAllGestionnaires() {
		List<Object> res = sessionFactory.getCurrentSession().createCriteria(Gestionnaire.class).list();
		Map<String, Gestionnaire> ret = new HashMap<>();
		for (Object obj : res) {
			Gestionnaire gestionnaire = (Gestionnaire) obj;
			ret.put(gestionnaire.getUserId(), gestionnaire);
		}
		return ret;
	}


	@Override
	public void disconnect() {
		LOGGER.info("Déconnexion de la DAO (aucune fermeture manuelle de session, Spring gère cela).");
	}

}

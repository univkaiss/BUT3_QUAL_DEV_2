package com.iut.banque.interfaces;

import java.util.Map;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.UserData;
import com.iut.banque.modele.Utilisateur;

public interface IDao {

	/**
	 * Crée un compte avec découvert dans la base de données.
	 *
	 * @param solde : le solde initial du compte
	 * @param numeroCompte : le numéro du compte
	 * @param decouvertAutorise : le découvert autorisé
	 * @param client : le client propriétaire du compte
	 * @return le compte créé
	 * @throws TechnicalException : si le numéro de compte existe déjà
	 * @throws IllegalFormatException : si les données du compte sont invalides
	 * @throws IllegalOperationException : si l'opération est interdite
	 */
	CompteAvecDecouvert createCompteAvecDecouvert(double solde,
												  String numeroCompte,
												  double decouvertAutorise,
												  Client client)
			throws TechnicalException, IllegalFormatException, IllegalOperationException;

	/**
	 * Crée un compte sans découvert dans la base de données.
	 *
	 * @param solde : le solde initial du compte
	 * @param numeroCompte : le numéro du compte
	 * @param client : le client propriétaire du compte
	 * @return le compte créé
	 * @throws TechnicalException : si le numéro de compte existe déjà
	 * @throws IllegalFormatException : si les données du compte sont invalides
	 */
	CompteSansDecouvert createCompteSansDecouvert(double solde,
												  String numeroCompte,
												  Client client)
			throws TechnicalException, IllegalFormatException;

	/**
	 * Met à jour un compte existant dans la base de données.
	 *
	 * @param c : le compte à mettre à jour
	 */
	void updateAccount(Compte c);

	/**
	 * Supprime un compte existant dans la base de données.
	 *
	 * @param c : le compte à supprimer
	 * @throws TechnicalException : si le compte est null ou non persistant
	 */
	void deleteAccount(Compte c) throws TechnicalException;

	/**
	 * Récupère tous les comptes d'un client.
	 *
	 * @param id : l'identifiant du client
	 * @return la liste des comptes du client, null si l'id est invalide
	 */
	Map<String, Compte> getAccountsByClientId(String id);

	/**
	 * Récupère un compte par son identifiant.
	 *
	 * @param id : l'identifiant du compte
	 * @return le compte correspondant, null si inexistant
	 */
	Compte getAccountById(String id);

	/**
	 * Crée un utilisateur (Client ou Gestionnaire) dans la base.
	 *
	 * @param data : objet UserData contenant toutes les informations nécessaires
	 * @return l'utilisateur créé
	 * @throws TechnicalException : si l'identifiant est déjà utilisé
	 * @throws IllegalFormatException : si les données sont invalides
	 * @throws IllegalArgumentException : si un argument est incorrect
	 */
	Utilisateur createUser(UserData data)
			throws TechnicalException, IllegalArgumentException, IllegalFormatException;

	/**
	 * Supprime un utilisateur (Client ou Gestionnaire) de la base.
	 *
	 * @param u : l'utilisateur à supprimer
	 * @throws TechnicalException : si l'utilisateur est null ou non persistant
	 */
	void deleteUser(Utilisateur u) throws TechnicalException;

	/**
	 * Met à jour un utilisateur existant.
	 *
	 * @param u : l'utilisateur à mettre à jour
	 */
	void updateUser(Utilisateur u);

	/**
	 * Vérifie les identifiants d'un utilisateur.
	 *
	 * @param usrId : l'identifiant de l'utilisateur
	 * @param usrPwd : le mot de passe à comparer
	 * @return true si le mot de passe correspond, false sinon
	 */
	boolean isUserAllowed(String usrId, String usrPwd);

	/**
	 * Récupère un utilisateur par son identifiant.
	 *
	 * @param id : l'identifiant de l'utilisateur
	 * @return l'utilisateur correspondant, null si inexistant
	 */
	Utilisateur getUserById(String id);

	/**
	 * Récupère tous les clients de la banque.
	 *
	 * @return une map avec clé = ID client et valeur = Client
	 */
	Map<String, Client> getAllClients();

	/**
	 * Récupère tous les gestionnaires de la banque.
	 *
	 * @return une map avec clé = ID gestionnaire et valeur = Gestionnaire
	 */
	Map<String, Gestionnaire> getAllGestionnaires();

	/**
	 * Termine la session de l'utilisateur courant.
	 */
	void disconnect();
}

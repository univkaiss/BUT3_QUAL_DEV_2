package com.iut.banque.converter;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.struts2.util.StrutsTypeConverter;
import com.opensymphony.xwork2.conversion.TypeConversionException;

import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Compte;

/**
 * Cette classe contient des méthodes permettant de convertir un compte en
 * string et vice-versa. Elle est déclarée dans
 * «src/main/webapp/WEB-INF/classes/xwork-conversion.properties».
 *
 * Grâce à cette classe il est possible de passer en paramètre d'une action
 * Struts le numéro d'un compte (une string) et le contrôleur qui va
 * recevoir le paramètre n'a besoin que d'un setter prenant un objet de type
 * Compte.
 */
public class AccountConverter extends StrutsTypeConverter {

	private static final Logger LOGGER = Logger.getLogger(AccountConverter.class.getName());

	/**
	 * DAO utilisée pour récupérer les objets correspondants à l'id passé en
	 * paramètre de convertFromString.
	 */
	private final IDao dao;

	/**
	 * Constructeur avec paramètre pour le AccountConverter.
	 * Utilisé pour l'injection de dépendance.
	 *
	 * @param dao l'IDao injecté
	 */
	public AccountConverter(IDao dao) {
		LOGGER.info("Création du convertisseur de compte avec DAO injectée");
		this.dao = dao;
	}

	/**
	 * Constructeur sans paramètre pour le AccountConverter
	 */
	public AccountConverter() {
		LOGGER.info("Création du convertisseur de compte sans DAO injectée");
		this.dao = null; // DAO à injecter par setter si nécessaire
	}

	/**
	 * Permet la conversion automatique par Struts d'un tableau de chaine vers
	 * un Compte
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object convertFromString(Map context, String[] values, Class classe) {
		if (dao == null) {
			throw new TypeConversionException("DAO non initialisée dans AccountConverter");
		}

		Compte compte = dao.getAccountById(values[0]);
		if (compte == null) {
			throw new TypeConversionException("Impossible de convertir la chaîne suivante : " + values[0]);
		}
		return compte;
	}

	/**
	 * Permet la conversion automatique par Struts d'un compte vers une chaine.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String convertToString(Map context, Object value) {
		if (!(value instanceof Compte)) {
			return null;
		}
		Compte compte = (Compte) value;
		return compte.getNumeroCompte();
	}
}

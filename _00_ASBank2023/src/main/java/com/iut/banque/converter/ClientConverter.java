package com.iut.banque.converter;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.struts2.util.StrutsTypeConverter;
import com.opensymphony.xwork2.conversion.TypeConversionException;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Client;

public class ClientConverter extends StrutsTypeConverter {

	private static final Logger LOGGER = Logger.getLogger(ClientConverter.class.getName());

	private final IDao dao;

	public ClientConverter(IDao dao) {
		LOGGER.info("=========================");
		LOGGER.info("Création du convertisseur de client");
		this.dao = dao;
	}

	public ClientConverter() {
		LOGGER.info("=========================");
		LOGGER.info("Création du convertisseur de client (sans DAO injectée)");
		this.dao = null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object convertFromString(Map context, String[] values, Class classe) {
		if (dao == null) {
			throw new IllegalStateException("DAO non initialisée dans ClientConverter");
		}
		Client client = (Client) dao.getUserById(values[0]);
		if (client == null) {
			throw new TypeConversionException("Impossible de convertir la chaine suivante : " + values[0]);
		}
		return client;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String convertToString(Map context, Object value) {
		Client client = (Client) value;
		return client == null ? null : client.getIdentity();
	}
}

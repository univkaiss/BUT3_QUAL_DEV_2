<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<html lang="fr">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Tableau de bord</title>
	<link rel="stylesheet" href="../style/style.css?v=3">
</head>
<body>
<div class="btnLogout">
	<s:form name="myForm" action="logout" method="POST">
		<s:submit name="Retour" value="Déconnexion" />
	</s:form>
</div>
<h1>Tableau de bord</h1>
<p>
	Bienvenue <b><s:property value="connectedUser.prenom" /> <s:property
		value="connectedUser.nom" /></b> !
</p>
<p>Voici l'état de vos comptes :</p>
<table>
	<th>
		Compte Bancaire
	</th>
	<tr>
		<td><b>Numéro de compte</b></td>
		<td><b>Type de compte</b></td>
		<td><b>Solde actuel</b></td>
	</tr>
	<s:iterator value="accounts">
		<tr>
			<td><s:url action="urlDetail" var="urlDetail">
				<s:param name="compte"><s:property value="key" /></s:param>
			</s:url> <s:a href="%{urlDetail}">
				<s:property value="key" />
			</s:a></td>
			<s:if test="%{value.className == \"CompteAvecDecouvert\"}">
				<td>Découvert possible</td>
			</s:if>
			<s:else>
				<td>Simple</td>
			</s:else>
			<s:if test="%{value.solde >= 0}">
				<td><s:property value="value.solde" /></td>
			</s:if>
			<s:else>
				<td class="soldeNegatif"><s:property value="value.solde" /></td>
			</s:else>
		</tr>
	</s:iterator>
</table>

<!-- ===================== -->
<!-- Cartes bancaires -->
<!-- ===================== -->
<h2 style="margin-top: 20px; text-align: center">Mes cartes bancaires</h2>

<s:if test="%{cartes == null || cartes.size() == 0}">
	<div class="failure">Aucune carte enregistrée.</div>
</s:if>
<s:else>
	<table>
		<thead>
		<tr>
			<th>Libellé</th>
			<th>Marque</th>
			<th>Titulaire</th>
			<th>Expiration</th>
			<th>Last4</th>
			<th>Action</th>
		</tr>
		</thead>
		<tbody>
		<s:iterator value="cartes" var="entry">
			<tr>
				<td><s:property value="#entry.value.label"/></td>
				<td><s:property value="#entry.value.marque"/></td>
				<td><s:property value="#entry.value.holderName"/></td>
				<td><s:property value="#entry.value.expMois"/> / <s:property value="#entry.value.expAnnee"/></td>
				<td><s:property value="#entry.value.last4"/></td>
				<td>
					<s:url var="deleteUrl" action="deleteCard">
						<s:param name="carteId" value="%{#entry.key}"/>
					</s:url>
					<s:a href="%{#deleteUrl}" onclick="return confirm('Supprimer cette carte ?');">Supprimer</s:a>
				</td>
			</tr>
		</s:iterator>
		</tbody>
	</table>
</s:else>

<div style="margin-top: 10px;">
	<s:form action="urlAddCard" method="POST" style="display: inline-block;">
		<s:submit value="Ajouter une carte" />
	</s:form>
</div>

</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>
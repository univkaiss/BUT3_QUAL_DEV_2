<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<html lang="fr">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Liste des comptes de la banque</title>
	<link rel="stylesheet" href="../style/style.css">
</head>
<body>
<div class="btnLogout">
	<s:form name="myForm" action="logout" method="POST">
		<s:submit name="Retour" value="Déconnexion" />
	</s:form>
</div>
<s:if test="aDecouvert">
	<h1>Liste des comptes à découvert de la banque</h1>
</s:if>
<s:else>
	<h1>Liste des comptes de la banque</h1>
</s:else>
<s:form name="myForm" action="retourTableauDeBordManager" method="POST">
	<s:submit name="Retour" value="Retour" />
</s:form>

<s:if test="aDecouvert">
	<p>Voici les comptes à découvert de la banque :</p>
</s:if>
<s:else>
	<p>Voici l'état des comptes de la banque :</p>
</s:else>

<table>
	<s:iterator value="allClients">
		<s:if test="(value.possedeComptesADecouvert() || !aDecouvert)">
			<tr>
				<td colspan="5"><b>Client :</b> <s:property value="value.prenom" /> <s:property value="value.nom" /> (n°<s:property value="value.numeroClient" />)</td>
			</tr>
			<tr>
				<th scope="col">Numéro de compte</th>
				<th scope="col">Type</th>
				<th scope="col">Solde</th>
				<s:if test="(!aDecouvert)">
					<th scope="col">Éditer</th>
					<th scope="col">Supprimer</th>
				</s:if>
			</tr>

			<s:iterator value="value.accounts">
				<s:if test="(value.solde < 0 || !aDecouvert)">
					<tr>
						<td><s:property value="key" /></td>
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
						<s:if test="(!aDecouvert)">
							<td>
								<s:url action="editAccount" var="editAccount">
									<s:param name="numeroCompte">
										<s:property value="value.numeroCompte" />
									</s:param>
								</s:url>
								<s:a href="%{editAccount}">
									<img src="../images/pen.png"
										 style="width: 20px; height: 20px"
										 alt="Editer ce compte"
										 title="Editer ce compte" />
								</s:a>
							</td>
							<td>
								<s:url action="deleteAccount" var="deleteAccount">
									<s:param name="numeroCompte">
										<s:property value="value.numeroCompte" />
									</s:param>
									<s:param name="numeroClient">
										<s:property value="value.owner.userId" />
									</s:param>
								</s:url>
								<s:a href="%{deleteAccount}" onclick="return confirm('Voulez-vous vraiment supprimer ce compte ?')">
									<img src="https://cdn2.iconfinder.com/data/icons/windows-8-metro-style/512/trash-.png"
										 style="width: 20px; height: 20px"
										 alt="Supprimer ce compte"
										 title="Supprimer ce compte" />
								</s:a>
							</td>
						</s:if>
					</tr>
				</s:if>
			</s:iterator>
			<s:if test="(!aDecouvert)">
				<tr>
					<td colspan="5">
						<s:url action="urlAddAccount" var="urlAddAccount">
							<s:param name="numeroClient">
								<s:property value="key" />
							</s:param>
						</s:url>
						<s:a href="%{urlAddAccount}">[ + Ajouter un compte ]</s:a>
					</td>
				</tr>
			</s:if>

		</s:if>
	</s:iterator>
</table>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>
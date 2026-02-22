<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<html lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Gestion des cartes bancaires</title>
    <link rel="stylesheet" href="../style/style.css">
</head>
<body>

<div class="btnLogout">
    <s:form name="myForm" action="logout" method="POST">
        <s:submit name="Retour" value="Déconnexion" />
    </s:form>
</div>

<h1>Gestion des cartes bancaires</h1>

<s:form name="myForm" action="retourTableauDeBordManager" method="POST">
    <s:submit name="Retour" value="Retour" />
</s:form>

<p>Sélectionnez un client pour consulter / supprimer ses cartes :</p>

<table>
    <tr>
        <th scope="col">Client</th>
        <th scope="col">Numéro client</th>
        <th scope="col">Actions</th>
    </tr>

    <s:iterator value="allClients">
        <tr>
            <td>
                <s:property value="value.prenom" />
                <s:property value="value.nom" />
                (<s:property value="value.userId" />)
            </td>
            <td><s:property value="value.numeroClient" /></td>

            <td>
                <s:url action="listeCartes" var="urlListeCartes">
                    <s:param name="userId">
                        <s:property value="value.userId" />
                    </s:param>
                </s:url>
                <s:a href="%{urlListeCartes}"> Voir les cartes </s:a>
            </td>
        </tr>
    </s:iterator>
</table>

</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>
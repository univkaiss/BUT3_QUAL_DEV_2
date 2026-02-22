<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html lang="fr">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Mes cartes bancaires</title>
    <link rel="stylesheet" href="../style/style.css?v=3">
    <script src="../js/jquery.js"></script>
</head>

<body>
<h1>Mes cartes bancaires</h1>

<p>
    <a href="<s:url action='urlAddCard'/>">Ajouter une carte</a>
</p>

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
                    <s:url action="deleteCard" var="deleteCard">
                        <s:param name="carteId" value="%{#entry.key}"/>
                    </s:url>
                    <a href="<s:property value='%{deleteCard}'/>" onclick="return confirm('Supprimer cette carte ?');">Supprimer</a>
                </td>
            </tr>
        </s:iterator>
        </tbody>
    </table>
</s:else>

<s:if test="%{userId != null && !userId.isEmpty()}">
    <!-- Retour vers ListeCarteManager pour le gestionnaire -->
    <s:form name="myForm" action="listeCarteManager" method="POST">
        <s:submit name="Retour" value="Retour" />
    </s:form>
</s:if>
<s:else>
    <!-- Retour vers tableau de bord client -->
    <s:form name="myForm" action="retourTableauDeBordClient" method="POST">
        <s:submit name="Retour" value="Retour" />
    </s:form>
</s:else>


</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>
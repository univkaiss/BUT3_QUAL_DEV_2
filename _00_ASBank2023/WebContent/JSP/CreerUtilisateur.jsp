<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html lang="fr">

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Formulaire de création d'utilisateur</title>
	<link rel="stylesheet" href="../style/style.css?v=3">
	<script src="../js/jquery.js"></script>
	<script src="../js/jsCreerUtilisateur.js"></script>
</head>
<body>

<div class="btnLogout">
	<s:form name="myForm" action="logout" method="POST">
		<s:submit name="Retour" value="Déconnexion" />
	</s:form>
</div>
<h1>Créer un nouvel utilisateur</h1>
<s:form id="myForm" name="myForm" action="ajoutUtilisateur"
		method="POST">
	<s:textfield label="Code utilisateur" name="userId" />
	<s:textfield label="Nom" name="nom" />
	<s:textfield label="Prenom" name="prenom" />
	<s:textfield label="Adresse" name="adresse" />
	<s:password label="Password" name="userPwd" />
	<s:select label="Sexe" name="male" list="#{'true':'Homme','false':'Femme'}" listValue="value" listKey="key"
			 value="true" />
	<s:select label="Type" name="client"
			 list="#{'true':'Client','false':'Manager'}" listValue="value" listKey="key" value="true" />
	<s:textfield label="Numéro de client" name="numClient" />

	<s:submit value="submit"/>
</s:form>
<s:form name="myForm" action="retourTableauDeBordManager" method="POST">
	<s:submit name="Retour" value="Retour" />
</s:form>


			<s:actionmessage cssClass="success" />
			<s:actionerror cssClass="error" />




</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>
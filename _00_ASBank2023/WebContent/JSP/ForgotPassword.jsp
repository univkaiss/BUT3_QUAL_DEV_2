<!-- WebContent/JSP/ForgotPassword.jsp -->
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8"/>
    <title>Mot de passe oublié</title>
    <link rel="stylesheet" href="../style/style.css?v=3">
</head>
<body>
<h1>Réinitialiser le mot de passe</h1>

<s:form action="doReinitPwd" method="POST">
    <s:textfield name="userId" label="Identifiant (user id)" />
    <s:password name="newPassword" label="Nouveau mot de passe" />
    <s:submit value="Appliquer" />
</s:form>

<form action="controller.Connect.login.action" method="get">
    <input type="submit" value="Retour connexion" />
</form>
</body>
</html>

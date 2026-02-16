<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="/struts-tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr">
<script type="text/javascript">
	function DisplayMessage() {
		alert('Ce TD a été donné pour les AS dans le cadre du cours de CO Avancé (Promotion 2025-2026)');
	}
</script>
<link rel="stylesheet" href="../style/style.css?v=3">
<link href="../style/favicon.ico" rel="icon"
	type="image/x-icon" />
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Application IUT Bank</title>
</head>
<body>
	<div class="accueil-container">
		<h1>Bienvenue sur l'application IUT Bank 2026</h1>

		<img src="<c:url value='../images/LogoIUT.png' />" alt="logo" />

		<div>
			<input type="button" value="Information" name="info"
				onClick="DisplayMessage()" />
		</div>

		<div>
			<s:url value="/redirectionLogin.action" var="goLogin" />
			<a href="${goLogin}">Page de Login</a>
		</div>
	</div>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>
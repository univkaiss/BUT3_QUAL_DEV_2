<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html lang="fr">

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Ajouter une carte bancaire</title>
  <link rel="stylesheet" href="../style/style.css?v=3">
  <script src="../js/jquery.js"></script>
  <script src="../js/jsCreerCarte.js"></script>
</head>

<body>
<h1>Ajouter une carte bancaire</h1>

<s:form id="myForm" name="myForm" action="addCard" method="POST" onsubmit="return validateCreerCarteForm();">
  <s:textfield label="Libellé" name="label" id="label" />
  <s:select label="Marque" name="marque" id="marque" list="bankBrands" />
  <s:textfield label="Titulaire" name="holderName" id="holderName" />
  <s:textfield label="Mois d'expiration" name="expMois" id="expMois" />
  <s:textfield label="Année d'expiration" name="expAnnee" id="expAnnee" />
  <s:textfield label="4 derniers chiffres" name="last4" id="last4" maxlength="4" />

  <div id="clientError" class="failure" style="margin-top:10px;"></div>

  <s:submit name="submit" value="Submit"/>
</s:form>

<s:form name="myForm" action="listeCartes" method="POST">
  <s:submit name="Retour" value="Retour" />
</s:form>

<s:if test="result">
  <s:if test="!error">
    <div class="success">
      <s:property value="message" />
    </div>
  </s:if>
  <s:else>
    <div class="failure">
      <s:property value="message" />
    </div>
  </s:else>
</s:if>

</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>
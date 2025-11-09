@echo off
:: Nettoyage et analyse SonarQube
:: Le token d'accès doit être défini dans une variable d'environnement SONAR_TOKEN
:: Exemple : setx SONAR_TOKEN "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"

mvn clean verify sonar:sonar ^
  -Dsonar.projectKey=MON_PROJET ^
  -Dsonar.host.url=http://localhost:9000 ^
  -Dsonar.login=%SONAR_TOKEN%

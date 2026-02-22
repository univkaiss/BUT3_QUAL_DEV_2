# ASBank2023 - Application Bancaire Web

## 📌 Description du projet

**ASBank2023** est une application web bancaire développée en Java avec Struts2, Spring et Hibernate. L'application permet la gestion de comptes bancaires, de cartes bancaires et de transactions pour les clients et les gestionnaires.

## 📌 Description du projet

**ASBank2023** est une application web bancaire développée en Java avec Struts2, Spring et Hibernate. L'application permet la gestion de comptes bancaires, de cartes bancaires et de transactions pour les clients et les gestionnaires.

---

## ⚡ Commandes rapides (essentielles)

### Démarrer l'application

```bash
# 1. Démarrer MySQL
brew services start mysql

# 2. Ouvrir IntelliJ et compiler
mvn clean install -DskipTests

# 3. Lancer Tomcat depuis IntelliJ
# Sélectionner "Tomcat 9.0.109" en haut à droite → Run

# 4. Accéder à l'application
# http://localhost:8080/_00_ASBank2023/
```

### Lancer les tests

```bash
# Tests unitaires seulement
mvn test

# Tests spécifiques (exemple)
mvn test -Dtest=TestCreerUtilisateur

# Tests avec rapport de couverture
mvn clean test jacoco:report
```

### Arrêter l'application

```bash
# Arrêter MySQL
brew services stop mysql

# Arrêter Tomcat depuis IntelliJ : cliquer le carré rouge
```

---

### Fonctionnalités principales

- 🔐 **Authentification** : Connexion sécurisée pour clients et gestionnaires
- 💳 **Gestion des comptes** : Création, modification et suppression de comptes bancaires
- 💰 **Transactions** : Dépôts, retraits et virements entre comptes
- 🏧 **Gestion des cartes bancaires** : Ajout et suppression de cartes ( uniquement l'ajout de fonctionnel, la gestion global de cette fonctionnalité n'est pas encore au point)
- 👥 **Gestion des utilisateurs** : Création de clients et de gestionnaires
- 🔑 **Sécurité** : Hachage des mots de passe avec PBKDF2

### Technologies utilisées

| Technologie | Version | Rôle |
|-------------|---------|------|
| Java | JDK 8 (Zulu) | Langage de programmation |
| Maven | 3.6+ | Gestion des dépendances et build |
| Apache Struts | 2.5+ | Framework web MVC |
| Spring | 4.3+ | Injection de dépendances |
| Hibernate | 5.x | ORM pour la persistance |
| MySQL | 5.7+ | Base de données |
| Tomcat | 9.0+ | Serveur d'application |
| JUnit | 4.x | Tests unitaires |

---

## 📋 Pré-requis

- **OS** : MacBook (ARM ou Intel) ou Linux/Windows avec adaptations
- **Homebrew** : Installé sur MacBook
- **IntelliJ IDEA** : Ultimate (licence étudiante gratuite)
- **Espace disque** : 3 GB minimum
- **Mémoire RAM** : 8 GB minimum

---

## 🚀 Installation et Configuration

### 1️⃣ Installation de Java JDK 8 (Zulu)

Le projet nécessite **JDK 8** pour la compatibilité.

```bash
brew install --cask zulu8
```

Vérifier l'installation :

```bash
/usr/libexec/java_home -V
```

Configurer les variables d'environnement :

```bash
echo 'export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc
```

Vérifier :

```bash
java -version
javac -version
```

---

### 2️⃣ Installation de Maven

Maven gère les dépendances et le build du projet.

```bash
brew install maven
```

Vérifier :

```bash
mvn -v
```

---

### 3️⃣ Installation de Tomcat 9

Tomcat 9.0.109 est le serveur d'application utilisé.

```bash
# Télécharger Tomcat 9
wget https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.109/bin/apache-tomcat-9.0.109.tar.gz

# Extraire
tar -xzf apache-tomcat-9.0.109.tar.gz

# Déplacer vers /usr/local
sudo mv apache-tomcat-9.0.109 /usr/local/tomcat9
```

Configurer les variables d'environnement :

```bash
echo 'export CATALINA_HOME=/usr/local/tomcat9' >> ~/.zshrc
echo 'export PATH=$CATALINA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc
```

Tester le démarrage :

```bash
$CATALINA_HOME/bin/startup.sh
```

Accéder à [http://localhost:8080](http://localhost:8080). Vous devriez voir la page d'accueil de Tomcat.

Arrêter :

```bash
$CATALINA_HOME/bin/shutdown.sh
```

---

### 4️⃣ Installation d'IntelliJ IDEA Ultimate

IntelliJ est l'IDE recommandé pour le développement.

1. Aller sur [JetBrains Student License](https://www.jetbrains.com/student/)
2. Télécharger **IntelliJ IDEA Ultimate**
3. Installer l'application
4. Lancer IntelliJ IDEA
5. Activer la licence étudiante

---

### 5️⃣ Configuration du projet dans IntelliJ

#### Importer le projet

1. Ouvrir IntelliJ IDEA
2. `File → Open → /Users/len/Documents/Informatique/3A/Qualdev/Projet/_00_ASBank2023`
3. Sélectionner `Open as Project`

#### Configurer le SDK

1. `File → Project Structure → Project`
2. Sélectionner **Project SDK** : `zulu-8`
3. Sélectionner **Project language level** : `8`
4. Cliquer `Apply → OK`

#### Configurer le compilateur

1. `IntelliJ IDEA → Preferences → Build, Execution, Deployment → Compiler → Java Compiler`
2. Vérifier **Target bytecode version** : `8`

#### Marquer les dossiers sources

1. Clique droit sur `src/main/java` → `Mark Directory as → Sources Root`
2. Clique droit sur `src/test/java` → `Mark Directory as → Test Sources Root`
3. Clique droit sur `src/main/resources` → `Mark Directory as → Resources Root`

---

### 6️⃣ Configuration de la base de données MySQL

#### Installation MySQL

```bash
brew install mysql
brew services start mysql
```

#### Sécuriser MySQL

```bash
mysql_secure_installation
```

Répondre aux questions de sécurité (recommandé : mot de passe `root`, supprimer utilisateurs anonymes).

#### Créer la base de données

```bash
mysql -u root -p
```

```sql
CREATE DATABASE allard19u_coa_banque;
EXIT;
```

#### Importer les données

```bash
cd /Users/len/Documents/Informatique/3A/Qualdev/Projet/_00_ASBank2023

# Importer les données de production
mysql -u root -p allard19u_coa_banque < script/dumpSQL.sql

# Importer les données de test
mysql -u root -p allard19u_coa_banque < script/dumpSQL_JUnitTest.sql
```

#### Vérifier les données

```bash
mysql -u root -p
USE allard19u_coa_banque;
SHOW TABLES;
SELECT COUNT(*) FROM utilisateur;
EXIT;
```

---

### 7️⃣ Configuration de Tomcat dans IntelliJ

#### Créer une Run Configuration Tomcat

1. `Run → Edit Configurations → + (New Configuration)`
2. Sélectionner `Tomcat Server → Local`
3. Configurer :
   - **Name** : `Tomcat 9.0.109`
   - **Tomcat Home** : `/usr/local/tomcat9`
   - **URL** : `http://localhost:8080/_00_ASBank2023/`
   - **On 'Update' action** : `Redeploy`
   - **On frame deactivation** : `Update resources`

#### Ajouter l'artifact

1. Dans la tab `Deployment`
2. Cliquer `+`
3. Sélectionner `Artifact → _00_ASBank2023:war exploded`
4. **Application context** : `/_00_ASBank2023`
5. Cliquer `Apply → OK`

---

### 8️⃣ Build du projet avec Maven

#### Nettoyer et compiler

```bash
cd /Users/len/Documents/Informatique/3A/Qualdev/Projet/_00_ASBank2023

# Clean et install
mvn clean install -DskipTests

# Ou avec tests (plus long)
mvn clean install
```

#### Via IntelliJ

1. Ouvrir le panel **Maven** (View → Tool Windows → Maven)
2. Cliquer `_00_ASBank2023 → Lifecycle → clean`
3. Cliquer `_00_ASBank2023 → Lifecycle → install`

Le WAR sera généré dans `target/_00_ASBank2023.war`.

---

## 🎯 Lancer l'application

### Étapes complètes

1. **Démarrer MySQL**
   ```bash
   brew services start mysql
   ```

2. **Démarrer IntelliJ IDEA**
   ```bash
   open -a "IntelliJ IDEA"
   ```

3. **Ouvrir le projet** et attendre l'indexation

4. **Compiler le projet**
   - Maven : `mvn clean install`
   - Ou via IntelliJ : `Build → Rebuild Project`

5. **Lancer Tomcat**
   - Sélectionner configuration `Tomcat 9.0.109` en haut à droite
   - Cliquer le bouton vert `Run`
   - Attendre le message : `Tomcat Server is running`

6. **Accéder à l'application**
   - Ouvrir le navigateur : [http://localhost:8080/_00_ASBank2023/](http://localhost:8080/_00_ASBank2023/)

### Identifiants de test

**Gestionnaire** :
- Identifiant : `admin`
- Mot de passe : `adminpass`


**Client** :
- Identifiant : `client1`
- Mot de passe : `clientpass1`

---

## ✅ Vérification de l'installation

### Checklist complète

- [ ] Java JDK 8 installé : `java -version`
- [ ] Maven installé : `mvn -v`
- [ ] IntelliJ IDEA ouvert et configuré
- [ ] SDK Zulu 8 sélectionné
- [ ] Tomcat 9 installé et démarrable
- [ ] MySQL démarré : `brew services list`
- [ ] Base de données créée : `mysql -u root -p allard19u_coa_banque`
- [ ] Données importées : `SELECT COUNT(*) FROM utilisateur;`
- [ ] Maven build réussi : `mvn clean install`
- [ ] Application accessible : [http://localhost:8080/_00_ASBank2023/](http://localhost:8080/_00_ASBank2023/)
- [ ] Page de login affichée
- [ ] Connexion avec credentials de test réussie

### Commandes de vérification

```bash
# Vérifier Java
java -version
echo $JAVA_HOME

# Vérifier Maven
mvn -version

# Vérifier Tomcat
curl http://localhost:8080

# Vérifier MySQL
mysql -u root -p -e "SHOW DATABASES; USE allard19u_coa_banque; SELECT COUNT(*) AS nb_utilisateurs FROM utilisateur;"
```

---

## 🛠️ Résolution des problèmes courants

### ❌ JDK non reconnu

**Problème** : `Could not find java`

**Solution** :
```bash
/usr/libexec/java_home -V
export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home
```

Redémarrer IntelliJ IDEA.

---

### ❌ Port 8080 déjà utilisé

**Problème** : `Address already in use`

**Solution** :
```bash
sudo lsof -ti:8080 | xargs kill -9
```

Ou changer le port dans Tomcat : `$CATALINA_HOME/conf/server.xml`

---

### ❌ Erreur de déploiement Tomcat

**Problème** : WAR ne se déploie pas

**Solutions** :
- `File → Invalidate Caches and Restart`
- `Build → Rebuild Project`
- Vérifier l'artifact dans `File → Project Structure → Artifacts`

---

### ❌ Connexion à la base de données échouée

**Problème** : `Cannot connect to database`

**Solutions** :
```bash
# Vérifier MySQL
brew services list
brew services restart mysql

# Vérifier les credentials dans applicationContext.xml
mysql -u root -p -e "USE allard19u_coa_banque; SELECT 1;"
```

---

### ❌ Maven build échoue

**Problème** : Erreur lors de `mvn clean install`

**Solutions** :
```bash
# Nettoyer le cache Maven
rm -rf ~/.m2/repository

# Réinstaller
mvn clean install -DskipTests
```

---

### ❌ Tests unitaires échouent

**Pour sauter les tests lors du build** :
```bash
mvn clean install -DskipTests
```

**Pour exécuter les tests** :
```bash
mvn test
```

---

## 📚 Structure du projet

```
_00_ASBank2023/
├── src/
│   ├── main/
│   │   ├── java/com/iut/banque/
│   │   │   ├── controller/          # Actions Struts2
│   │   │   ├── facade/              # Façades métier
│   │   │   ├── modele/              # Entités Hibernate
│   │   │   ├── dao/                 # Accès aux données
│   │   │   ├── exceptions/          # Exceptions métier
│   │   │   ├── security/            # Sécurité (hachage mots de passe)
│   │   │   └── interfaces/          # Contrats DAO
│   │   ├── resources/
│   │   │   └── struts.xml           # Configuration Struts2
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── applicationContext.xml  # Configuration Spring
│   │       │   ├── web.xml                 # Configuration servlet
│   │       │   └── classes/                # Resources
│   │       └── JSP/                        # Pages web
│   └── test/
│       └── java/com/iut/banque/test/     # Tests unitaires JUnit
├── pom.xml                          # Configuration Maven
├── Dockerfile                       # Configuration Docker (optionnel)
└── script/
    ├── dumpSQL.sql                 # Données de production
    └── dumpSQL_JUnitTest.sql       # Données de test
```

---

## 🚀 Démarrage rapide (après la première installation)

### Commande unique pour démarrer

```bash
# Terminal 1 : Démarrer MySQL
brew services start mysql

# Terminal 2 : Ouvrir IntelliJ et lancer Tomcat
open -a "IntelliJ IDEA"
# Dans IntelliJ : Run → Tomcat 9.0.109
```

L'application sera accessible sur [http://localhost:8080/_00_ASBank2023/](http://localhost:8080/_00_ASBank2023/) en 30 secondes.

---

## 📖 Documentation additionnelle

- **Architecture** : Voir `Diagramme_architecure.pdf`
- **Modèle de données** : Voir `Diagramme_UML_classe.png`
- **Sprint 2** : Métriques et indicateurs dans `Sptrint2/`

---

## 🧪 Exécuter les tests

```bash
# Tests unitaires uniquement
mvn test

# Tests spécifiques
mvn test -Dtest=TestCreerUtilisateur

# Tests avec rapport de couverture
mvn clean test jacoco:report
# Rapport : target/site/jacoco/index.html
```

---

## 🔒 Configuration de sécurité

### Hachage des mots de passe

Les mots de passe sont hachés avec **PBKDF2-SHA1** avec 20 000 itérations et un salt de 6 bytes.

Classe responsable : `com.iut.banque.security.PasswordHasherCompact`

### Authentification

- Gérée par la classe `com.iut.banque.facade.LoginManager`
- Distingue les rôles : Client vs Gestionnaire (Manager)
- Session sécurisée avec Struts2

---

## 📞 Support et aide

En cas de problème :

1. Consulter la section **Résolution des problèmes courants**
2. Vérifier les logs IntelliJ IDEA (View → Tool Windows → Run)
3. Vérifier les logs Tomcat : `$CATALINA_HOME/logs/catalina.out`
4. Vérifier les erreurs MySQL : `mysql -u root -p -e "SHOW WARNINGS;"`

---

## 📄 Licence et auteurs

Projet académique - Université (3A)
Année : 2023-2024

---

## ✨ Fonctionnalités détaillées

### Pour les Clients

- ✅ Créer un compte (avec ou sans découvert)
- ✅ Voir l'état de ses comptes
- ✅ Faire des virements
- ✅ Gérer ses cartes bancaires
- ✅ Modifier son mot de passe

### Pour les Gestionnaires (Managers)

- ✅ Créer/modifier/supprimer des clients
- ✅ Créer/modifier/supprimer des comptes clients
- ✅ Consulter tous les comptes
- ✅ Consulter les comptes à découvert
- ✅ Gérer d'autres gestionnaires
- ✅ Voir les transactions



---

## 📋 Pré-requis

- MacBook ou autre ordinateur (ARM ou Intel)
- Homebrew installé
- IntelliJ IDEA Ultimate (licence étudiante)

---

## 1️⃣ Installation de Java JDK 8 (Zulu)

### Installation

```bash
brew install --cask zulu8
```

### Vérification

```bash
/usr/libexec/java_home -V
```

### Configuration des variables d'environnement

```bash
echo 'export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc
```

### Vérification finale

```bash
java -version
javac -version
```

---

## 2️⃣ Installation de Maven

### Installation via Homebrew

```bash
brew install maven
```

### Vérification

```bash
mvn -v
```

---

## 3️⃣ Installation de Tomcat 9

### Téléchargement et installation

```bash
wget https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.68/bin/apache-tomcat-9.0.68.tar.gz
tar -xzf apache-tomcat-9.0.68.tar.gz
sudo mv apache-tomcat-9.0.68 /usr/local/tomcat9
```

### Configuration environnement

```bash
echo 'export CATALINA_HOME=/usr/local/tomcat9' >> ~/.zshrc
echo 'export PATH=$CATALINA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc
```

### Test de démarrage

```bash
$CATALINA_HOME/bin/startup.sh
```

Accéder à [http://localhost:8080](http://localhost:8080)

### Arrêt de Tomcat

```bash
$CATALINA_HOME/bin/shutdown.sh
```

---

## 4️⃣ Installation d'IntelliJ IDEA Ultimate

- Télécharger depuis JetBrains Student License
- Installer l'application
- Lancer IntelliJ IDEA
- Sélectionner New Project → Maven
- Ajouter JDK 8 : `/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home`

---

## 5️⃣ Configuration du projet dans IntelliJ

- Importer le projet : `File → Open → 00_ASBank2023 → Open as Project`
- Configurer le SDK : `File → Project Structure → Project SDK : zulu-8, Project language level : 8`
- Configurer le compiler : `File → Settings → Build, Execution, Deployment → Compiler → Java Compiler → Target bytecode version 8`

---

## 6️⃣ Configuration de Tomcat dans IntelliJ

- Ajouter serveur : `Run → Edit Configurations → + → Tomcat Server → Local`
- Configurer serveur : `Tomcat Home : /usr/local/tomcat9, URL : http://localhost:8080/_00_ASBank2023/`
- Ajouter déploiement : `Deployment tab → + → Artifact → 00_ASBank2023:war exploded, Application context : /_00_ASBank2023`
- Run configuration prête

---

## 7️⃣ Configuration Maven dans IntelliJ

- Vérifier goals : `Maven panel → Lifecycle → clean → install -DskipTests`
- Configurer run Maven : `Run → Edit Configurations → + → Maven → Command line : clean install -DskipTests`
- Exécution via terminal IntelliJ : `mvn clean install -DskipTests`

---

## 8️⃣ Configuration de la base de données

### Installation MySQL

```bash
brew install mysql
brew services start mysql
```

### Sécurisation

```bash
mysql_secure_installation
```

### Configuration applicationContext.xml

```xml
<bean id="dataSource" scope="singleton" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="com.mysql.jdbc.Driver" />
    <property name="url" value="jdbc:mysql://localhost:3306/banklut?useSSL=false" />
    <property name="username" value="root" />
    <property name="password" value="root" />
</bean>
```

### Import des données

```bash
mysql -u root -p < script/dumpSQL.sql
mysql -u root -p < script/dumpSQL_UnitTest.sql
```

---

## 9️⃣ Démarrage complet de l'application

1. Build Maven : `mvn clean install -DskipTests`
2. Démarrer Tomcat via IntelliJ : sélectionner configuration Tomcat 9.0.68 → Run
3. Accès à l'application : http://localhost:8080/_00_ASBank2023/

---

## 🔧 Résolution des problèmes courants

- **JDK non reconnu** : vérifier `/usr/libexec/java_home -V` et redémarrer IntelliJ
- **Port 8080 déjà utilisé** : `sudo lsof -ti:8080 | xargs kill -9`
- **Erreur de déploiement Tomcat** : `File → Invalidate Caches and Restart`, `Build → Rebuild Project`
- **Connexion base de données** : vérifier MySQL, `brew services restart mysql`
- **Artifact non trouvé** : `File → Project Structure → Artifacts → + → Web Application: Exploded → From Modules`

---

## 📝 Vérification finale

- JDK 8 configuré et reconnu
- Maven build successful
- Tomcat démarre sans erreur
- Application accessible sur http://localhost:8080/_00_ASBank2023/
- Page de login s'affiche
- Connexion base de données fonctionnelle

### Commandes de vérification

```bash
java -version

# Vérifier Maven
mvn -v

# Vérifier Tomcat
curl http://localhost:8080

# Vérifier MySQL
mysql -u root -p -e "SHOW DATABASES;"
```

---

## 🚀 Démarrage rapide après configuration

### Démarrer MySQL

```bash
brew services start mysql
```

### Lancer l'application

- Lancer IntelliJ et exécuter la configuration Tomcat 9.0.68
- Accéder à : http://localhost:8080/_00_ASBank2023/

---

## 📞 Support

En cas de problème, vérifier la console IntelliJ pour les messages d'erreur détaillés.

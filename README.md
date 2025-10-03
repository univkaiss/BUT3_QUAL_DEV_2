# ASBank2023 - Installation et Configuration sur MacBook

Cette documentation explique pas à pas comment installer et configurer Maven, JDK 8 (Zulu), Tomcat 9 et IntelliJ IDEA Ultimate sur un MacBook pour le projet **ASBank2023**.

---

## 📋 Pré-requis

- MacBook (ARM ou Intel)
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

### Configuration des variables d’environnement

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

### Téléchargement

- Aller sur JetBrains Student License
- Télécharger IntelliJ IDEA Ultimate
- Installer l'application

### Configuration initiale

- Lancer IntelliJ IDEA
- Sélectionner : New Project
- Choisir : Maven
- Configurer le JDK 8 :
  - Cliquer sur "Add JDK..."
  - Naviguer vers : `/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home`

---

## 5️⃣ Configuration du projet dans IntelliJ

### Import du projet

- File → Open
- Sélectionner le dossier `00_ASBank2023`
- Choisir "Open as Project"

### Configuration du SDK

- File → Project Structure (⌘+;)
- Project Settings → Project
- Project SDK : Sélectionner "zulu-8"
- Project language level : 8

### Configuration du compiler

- File → Settings (⌘+,)
- Build, Execution, Deployment → Compiler → Java Compiler
- Vérifier que Target bytecode version est "8"

---

## 6️⃣ Configuration de Tomcat dans IntelliJ

### Ajout du serveur Tomcat

- Run → Edit Configurations (⌥+⇧+F10)
- + → Tomcat Server → Local

### Configuration du serveur

```yaml
Name: Tomcat 9.0.68
Application server: Configure → Tomcat Home: /usr/local/tomcat9
URL: http://localhost:8080/_00_ASBank2023/
```

### Configuration du déploiement

- Deployment tab → + → Artifact
- Sélectionner 00_ASBank2023:war exploded
- Application context: /_00_ASBank2023

### Configuration complète de la Run Configuration

```xml
<configuration name="Tomcat 9.0.68" type="SpringBootApplicationType">
  <module name="00_ASBank2023" />
  <option name="SPRING_BOOT_MAIN_CLASS" />
  <server-settings>
    <option name="NAME" value="Tomcat 9.0.68" />
    <option name="SERVER_PORT" value="8080" />
    <option name="JMX_PORT" value="1099" />
  </server-settings>
  <deployment>
    <artifact name="00_ASBank2023:war exploded">
      <settings>
        <option name="CONTEXT_PATH" value="/_00_ASBank2023" />
      </settings>
    </artifact>
  </deployment>
  <method v="2">
    <option name="Make" enabled="true" />
    <option name="BuildArtifacts" enabled="true">
      <artifact name="00_ASBank2023:war exploded" />
    </option>
  </method>
</configuration>
```

---

## 7️⃣ Configuration Maven dans IntelliJ

### Vérification des goals Maven

- Ouvrir le panneau Maven (habituellement à droite)
- Dérouler le projet → Lifecycle
- Exécuter :
  - clean
  - install -DskipTests

### Configuration du run Maven

- Run → Edit Configurations
- + → Maven

#### Exemple de configuration

```yaml
Name: Maven Build
Working directory: $ProjectFileDir$
Command line: clean install -DskipTests
```

### Exécution via terminal IntelliJ

```bash
mvn clean install -DskipTests
```

---

## 8️⃣ Configuration de la base de données

### Installation de MySQL

```bash
brew install mysql
brew services start mysql
```

### Sécurisation de MySQL

```bash
mysql_secure_installation
```

### Configuration dans applicationContext.xml

Le fichier se trouve dans : `WebContent/WEB-INF/applicationContext.xml`

```xml
<!-- La source de données utilisée en production. Contient les infos de base de la connection -->
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

### Étape 1 : Build Maven

```bash
mvn clean install -DskipTests
```

### Étape 2 : Démarrer Tomcat via IntelliJ

- Sélectionner la configuration "Tomcat 9.0.68"
- Cliquer sur Run (▶️) ou Debug (🐞)

### Étape 3 : Accès à l'application

- URL : http://localhost:8080/_00_ASBank2023/
- Login de test : Utiliser les identifiants fournis dans la documentation

---

## 🔧 Résolution des problèmes courants

### Problème : JDK non reconnu

**Solution :**

```bash
# Vérifier l'installation
/usr/libexec/java_home -V

# Redémarrer IntelliJ
```

### Problème : Port 8080 déjà utilisé

**Solution :**

```bash
sudo lsof -ti:8080 | xargs kill -9
```

### Problème : Erreur de déploiement Tomcat

**Solution :**

- File → Invalidate Caches and Restart
- Build → Rebuild Project

### Problème : Connexion base de données

**Solution :**

```bash
# Vérifier que MySQL tourne
brew services list

# Redémarrer MySQL
brew services restart mysql
```

### Problème : Artifact non trouvé

**Solution :**

- File → Project Structure (⌘+;)
- Artifacts → + → Web Application: Exploded → From Modules
- Sélectionner le module principal

---

## 📝 Vérification finale

### Checklist de validation

- JDK 8 configuré et reconnu
- Maven build successful
- Tomcat démarre sans erreur
- Application accessible sur http://localhost:8080/_00_ASBank2023/
- Page de login s'affiche
- Connexion base de données fonctionnelle

### Commandes de vérification

```bash
# Vérifier Java
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

Une fois tout configuré, pour lancer l'application :

1. **Démarrer MySQL :**

```bash
brew services start mysql
```

2. **Lancer IntelliJ et exécuter :**

- Sélectionner la configuration "Tomcat 9.0.68"
- Cliquer sur Run (▶️)
- Accéder à : http://localhost:8080/_00_ASBank2023/

---

## 📞 Support

En cas de problème, vérifier la console IntelliJ pour les messages d'erreur détaillés.

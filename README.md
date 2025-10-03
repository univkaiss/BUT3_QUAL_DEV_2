# ASBank2023 - Installation et Configuration sur MacBook

Cette documentation explique pas à pas comment installer et configurer Maven, JDK 8 (Zulu), Tomcat 9 et IntelliJ IDEA Ultimate sur un MacBook pour le projet ASBank2023.

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

Accéder à http://localhost:8080

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
mvn -v
curl http://localhost:8080
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

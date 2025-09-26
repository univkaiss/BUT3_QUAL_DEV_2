# Installation et configuration Maven, JDK 8, Tomcat et IntelliJ sur MacBook

Cette documentation explique pas à pas comment installer et configurer **Maven**, **JDK 8 (Zulu)**, **Tomcat** et **IntelliJ IDEA** sur MacBook, y compris la configuration des runs Maven et la résolution des problèmes fréquents.

---

## 1️⃣ Installation de Java JDK 8

Installer Zulu 8 via Homebrew :

⚠️ Sur Mac ARM, `openjdk@8` n’est pas disponible via Homebrew. **Zulu 8 est compatible.**

```bash
brew install --cask zulu8
```

Vérifier l’installation :

```bash
/usr/libexec/java_home -V
```

Exemple de sortie :
```
1.8.0_372 (arm64) "Zulu 8" /Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home
```

Configurer la variable d’environnement :

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

---

## 2️⃣ Installation de Maven

Installer Maven :

```bash
brew install maven
```

Vérifier la version :

```bash
mvn -v
```

Exemple :
```
Apache Maven 3.9.11
Java version: 1.8.0_372, vendor: Azul Systems, Inc.
```

---

## 3️⃣ Préparer le projet Maven

Vérifier que ton projet a un `pom.xml` configuré pour Java 8 :

```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </plugin>
        <plugin>
            <artifactId>maven-war-plugin</artifactId>
            <version>2.6</version>
            <configuration>
                <warSourceDirectory>WebContent</warSourceDirectory>
                <failOnMissingWebXml>false</failOnMissingWebXml>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Ajouter les dépendances nécessaires : **Spring, Struts, Hibernate, MySQL, etc.**

---

## 4️⃣ Installer et configurer Tomcat

Télécharger **Tomcat 9** depuis Apache Tomcat et décompresser dans :

```
~/Documents/apache-tomcat-9.0.109
```

Donner les permissions d’exécution aux scripts :

```bash
cd ~/Documents/apache-tomcat-9.0.109/bin
chmod +x *.sh
```

Tester Tomcat :

```bash
./startup.sh
```

Accéder à [http://localhost:8080](http://localhost:8080).

---

## 5️⃣ Configurer IntelliJ IDEA

### 5.1 Configurer le JDK
- **File → Project Structure → Project → Project SDK → Add JDK → Zulu 8**

### 5.2 Configurer Tomcat
- **Run → Edit Configurations… → + → Tomcat Server → Local**
- Configurer :
  - **Tomcat Home** : `~/Documents/apache-tomcat-9.0.109`
  - **Deployment → Artifact** : WAR du projet
  - **JDK** : 1.8 (Zulu 8)
- **Apply → OK**

---

## 6️⃣ Configurer un Run Maven dans IntelliJ

- **Run → Edit Configurations… → + → Maven**
- Remplir :
  - **Name** : `Maven Clean & Compile`
  - **Working directory** : racine du projet
  - **Command line** : `clean compile` (ou `clean package`, `tomcat7:deploy`, etc.)
  - **JDK** : 1.8 (Zulu 8)
- **Apply → OK**

---

## 📌 Commandes Maven utiles

| Nom configuration  | Command line Maven   | Description                         |
|--------------------|----------------------|-------------------------------------|
| Clean & Compile    | `clean compile`     | Nettoie et compile le projet        |
| Build WAR          | `clean package`     | Compile et génère le WAR            |
| Deploy Tomcat      | `tomcat7:deploy`    | Déploie sur Tomcat                  |
| Run tests          | `test`              | Exécute les tests unitaires         |

---

## ⚠️ Problèmes fréquents & Solutions

| Problème                             | Solution                                                                 |
|--------------------------------------|---------------------------------------------------------------------------|
| Permission denied sur catalina.sh    | `chmod +x catalina.sh`                                                   |
| Maven compile avec warnings Java 8   | `<source>1.8</source> <target>1.8</target>` ou `<release>8</release>`    |
| JDK incorrect / options obsolètes    | Vérifier `JAVA_HOME` vers **Zulu 8**                                      |
| MySQL Connector déplacé              | Utiliser `com.mysql:mysql-connector-j`                                    |
| Tomcat Maven Plugin introuvable      | Utiliser `tomcat7-maven-plugin:2.2` ou plugin compatible Tomcat 8+        |
| Warnings JAXB                        | Remplacer `com.sun.xml.bind` par `javax.xml.bind:jaxb-api` si possible    |

---

## 8️⃣ Vérifications finales

- Run **Maven Clean & Compile** → pas d’erreurs  
- Run **Maven Build WAR** → fichier WAR généré dans `target/`  
- Run **Tomcat via IntelliJ** → application accessible sur [http://localhost:8080/ASBank-2023](http://localhost:8080/ASBank-2023)

---

## ✅ Conclusion

Documentation complète pour un MacBook, avec **JDK 8, Maven, Tomcat, IntelliJ, runs Maven intégrés et gestion des warnings/erreurs classiques**.

---

## 🔍 Dépannage rapide (check-list)

- [ ] Vérifier que `JAVA_HOME` pointe vers **Zulu 8** (`echo $JAVA_HOME`)  
- [ ] Vérifier que Tomcat a les bons droits (`chmod +x *.sh`)  
- [ ] Vérifier la version Maven (`mvn -v`)  
- [ ] Vérifier que le WAR est bien généré (`target/*.war`)  
- [ ] Vérifier les logs IntelliJ/Tomcat en cas d’erreur (`catalina.out`)  

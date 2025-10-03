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

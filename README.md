# Projekt-Installationsanleitung

Dieses Dokument beschreibt, wie das Projekt lokal auf dem eigenen Rechner gestartet werden kann. Entwickelt und getestet wurde mit IntelliJ IDEA.

## Voraussetzungen

- Java 17 oder höher
- Gradle
- Git
- IntelliJ IDEA (Community oder Ultimate Edition)

## Projekt herunterladen und starten

1. Repository klonen:

   ```bash
   git clone <REPOSITORY_LINK>
   cd <PROJEKT_ORDNER>
   ```

2. Projekt in IntelliJ öffnen:

    - IntelliJ starten.
    - "Open" auswählen und den geklonten Projektordner öffnen.
    - IntelliJ sollte das Projekt automatisch als Gradle-Projekt erkennen.
    - Falls nicht, im Projektfenster rechtsklick → "Add Framework Support..." → "Gradle" auswählen.

3. Abhängigkeiten herunterladen:

    - Gradle lädt normalerweise automatisch alle benötigten Abhängigkeiten.
    - Falls das nicht passiert, im Gradle-Tab auf "Reload All Gradle Projects" klicken.

4. Anwendung starten:

    - In IntelliJ in der Hauptklasse (mit `@SpringBootApplication`) Rechtsklick → "Run" auswählen.

   oder alternativ im Terminal:

   ```bash
   ./gradlew bootRun
   ```

## Zugriff auf die Anwendung

Nach dem Start ist die Anwendung unter folgender Adresse erreichbar:

```
http://localhost:8080
```

Wenn ein anderer Port verwendet wird, kann dieser in der Datei `application.properties` oder `application.yml` angepasst werden.

## Datenbank (H2-Konsole)

Das Projekt verwendet eine H2 In-Memory-Datenbank.

Die Konsole ist erreichbar unter:

```
http://localhost:8080/h2-console
```

Login-Daten:

- JDBC URL: `jdbc:h2:mem:testdb`
- Benutzername: `sa`
- Passwort: password

Wenn die Konsole nicht erreichbar ist, prüfen ob `spring.h2.console.enabled=true` in `application.properties` gesetzt ist.
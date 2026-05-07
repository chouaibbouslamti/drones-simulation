# Simulateur de Flotte de Drones

Projet Java/N-Tiers pour la simulation de déplacements d'une flotte de drones sur une carte avec obstacles.

## Objectif Pédagogique

Ce projet permet de se familiariser avec :
- Le langage Java
- La programmation concurrente (Threads)
- La communication réseau (Sockets)

## Description du Projet

Une entreprise de livraison utilise une flotte de drones pour réaliser ses livraisons à domicile. La carte de la ville est représentée par une forme rectangulaire contenant des obstacles. Les drones reçoivent des chemins codifiés pour effectuer leurs livraisons.

## Architecture du Projet

### Structure des Packages

```
com.drone.simulator/
├── Drone.java                    # Classe métier du drone
├── SimulatorMap.java            # Carte avec obstacles
├── ConfigurationReader.java      # Lecture des fichiers de config
├── SimulatorBasic.java         # Version 1 : Séquentielle
├── DroneSimulatorMain.java     # Menu principal
├── concurrent/
│   ├── DroneWorker.java        # Thread pour drone
│   ├── DisplayWorker.java     # Thread d'affichage
│   └── SimulatorConcurrent.java # Version 2 : Concurrente
├── server/
│   ├── DroneServer.java       # Serveur de simulation
│   └── ServerConnectionHandler.java # Gestion des clients
└── client/
    └── DroneClient.java       # Client de simulation
```

## Les 3 Versions du Simulateur

### Version 1 : Mode Basique (Séquentiel)

Les drones se déplacent l'un après l'autre sans synchronisation.

**Caractéristiques :**
- Lecture depuis fichier de configuration
- Exécution séquentielle des commandes
- Affichage continu des positions
- Gestion des obstacles et wrapping

**Fichier de configuration :**
```
20 20                    # Largeur Hauteur
0 5 N LMLMLMLMM         # x y orientation commandes
5 0 S MMRMMRMRRM
```

### Version 2 : Mode Concurrent (avec Threads)

Les drones se déplacent simultanément avec des threads séparés.

**Caractéristiques :**
- Chaque drone exécute dans un Thread séparé
- Thread d'affichage dédié avec rafraîchissement
- Synchronisation avec ReentrantLock et Condition
- Affichage en temps réel de la carte

### Version 3 : Mode Communicant (Client-Serveur)

Communication réseau entre client et serveur via Sockets TCP.

**Caractéristiques :**
- Serveur écoute sur un port (défaut: 5000)
- Client envoie configuration au serveur
- Exécution concurrente côté serveur
- Envoi des résultats au client

## Format des Commandes

- `L` : Tourner à gauche (90°)
- `R` : Tourner à droite (90°)
- `M` : Avancer d'une case
- `B` : Reculer d'une case

## Orientations

- `N` : Nord (↑)
- `S` : Sud (↓)
- `E` : Est (→)
- `O` : Ouest (←)

## Compilation et Exécution

### Prérequis

- Java 11 ou supérieur
- Maven 3.6 ou supérieur

### Compilation

```bash
mvn clean compile
```

### Exécution

#### Menu Interactif

```bash
java -cp target/classes com.drone.simulator.DroneSimulatorMain
# ou avec le JAR
java -jar target/drone-simulator-1.0-SNAPSHOT.jar
```

#### Ligne de Commande

**Mode Basique :**
```bash
java -cp target/classes com.drone.simulator.DroneSimulatorMain basic config.txt
```

**Mode Concurrent :**
```bash
java -cp target/classes com.drone.simulator.DroneSimulatorMain concurrent config.txt
```

**Mode Serveur :**
```bash
java -cp target/classes com.drone.simulator.DroneSimulatorMain server [port]
```

**Mode Client :**
```bash
java -cp target/classes com.drone.simulator.DroneSimulatorMain client config.txt [hôte] [port]
```

### Création du JAR

```bash
mvn clean package
java -jar target/drone-simulator-1.0-SNAPSHOT.jar
```

## Tests

### Exécution des Tests

```bash
mvn test
```

### Tests Unitaires

- Tests des classes de base (Drone, SimulatorMap, ConfigurationReader)
- Tests de la Version 1 (SimulatorBasic)
- Tests de la Version 2 (SimulatorConcurrent)
- Tests de la Version 3 (Serveur et Client)

## Caractéristiques Techniques

### Gestion des Obstacles

- Les drones ne traversent pas les obstacles
- Si un mouvement est bloqué, le drone reste sur place
- Les obstacles sont générés aléatoirement (15% par défaut)

### Wrapping

- Quand un drone sort de la carte, il continue dans la direction opposée
- Géré mathématiquement avec modulo

### Synchronisation (Version 2)

- Utilisation de ReentrantLock pour l'accès à la carte
- Condition pour la synchronisation entre threads
- Collections thread-safe pour la concurrence

### Communication Réseau (Version 3)

- Sockets TCP pour la communication client-serveur
- Protocole texte simple et robuste
- Gestion des erreurs et timeouts

## Exemples d'Utilisation

### Fichier de Configuration Exemple

```
# drones_test.txt
20 20
0 5 N LMLMLMLMM
5 0 S MMRMMRMRRM
```

### Session de Simulation

**Mode Basique :**
```
Initialisation de la simulation basique...
Carte initialisée (20x20)
Nombre de drones: 2
Obstacles générés automatiquement

Début de la simulation séquentielle...
========================================

Drone 0 : Position initiale : 0 5 N | Commandes : LMLMLMLMM
===========================================
Position du drone 0 : 0 5 O (après commande 1 : L)
Position du drone 0 : 19 5 O (après commande 2 : M)
...

Positions finales des drones:
Drone 0 : 0 6 N
Drone 1 : 3 18 S
```

## Dépannage

### Problèmes Communs

1. **Port déjà utilisé** : Changer le port du serveur
2. **Fichier introuvable** : Vérifier le chemin du fichier de configuration
3. **Connexion refusée** : Vérifier que le serveur est démarré

### Logs

- Messages d'erreur sur stderr
- Informations de simulation sur stdout
- État des connexions réseau en mode serveur

## Évaluation

### Critères de Notation

- **Fonctionnalité** : Le programme répond-il aux exigences ?
- **Qualité du code** : Lisibilité, commentaires, structure
- **Gestion des erreurs** : Exceptions traitées correctement
- **Synchronisation** : Pas de race conditions
- **Respect des consignes** : Suivre la structure proposée

### Remise

- Format : Archive .zip contenant le projet Maven complet
- Documentation : README.md expliquant compilation et exécution
- Tests : Fichiers de test pour valider chaque version

## Auteur

Projet réalisé dans le cadre du M1 RSI 2025-2026
Sous la direction de Pr A. Marzouk
FSTS - Faculté des Sciences et Techniques
# drones-simulation

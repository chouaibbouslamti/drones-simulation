package com.drone.simulator.concurrent;

import com.drone.simulator.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Version concurrente du simulateur où les drones se déplacent simultanément
 */
public class SimulatorConcurrent {
    
    private SimulatorMap map;
    private List<Drone> drones;
    private ConfigurationReader configReader;
    private List<DroneWorker> droneWorkers;
    private DisplayWorker displayWorker;
    private ReentrantLock displayLock;
    private java.util.concurrent.locks.Condition displayCondition;
    private CountDownLatch completionLatch;
    
    public SimulatorConcurrent() {
        this.configReader = new ConfigurationReader();
        this.droneWorkers = new ArrayList<>();
        this.displayLock = new ReentrantLock();
        this.displayCondition = displayLock.newCondition();
    }
    
    /**
     * Initialise la simulation à partir d'un fichier de configuration
     * @param filename Chemin du fichier de configuration
     * @throws Exception En cas d'erreur d'initialisation
     */
    public void initializeSimulation(String filename) throws Exception {
        System.out.println("Initialisation de la simulation concurrente...");
        
        // Lecture du fichier de configuration
        ConfigurationReader.Configuration config = configReader.readConfiguration(filename);
        
        // Création de la carte
        map = new SimulatorMap(config.getMapWidth(), config.getMapHeight());
        
        // Ajout des obstacles (densité de 15% comme exemple)
        map.addObstacles(0.15);
        
        // Récupération des drones
        drones = config.getDrones();
        
        // Placer les drones sur la carte (enlever les obstacles à leurs positions initiales)
        for (Drone drone : drones) {
            map.removeObstacle(drone.getX(), drone.getY());
        }
        
        // Initialiser le CountDownLatch pour attendre la fin de tous les threads
        completionLatch = new CountDownLatch(drones.size());
        
        System.out.println("Carte initialisée (" + config.getMapWidth() + "x" + config.getMapHeight() + ")");
        System.out.println("Nombre de drones: " + drones.size());
        System.out.println("Obstacles générés automatiquement");
    }
    
    /**
     * Initialise la simulation avec des paramètres fournis manuellement
     * @param width Largeur de la carte
     * @param height Hauteur de la carte
     * @param drones Liste des drones
     * @param map Carte pré-initialisée
     */
    public void initializeSimulation(int width, int height, List<Drone> drones, SimulatorMap map) {
        System.out.println("Initialisation de la simulation concurrente (mode serveur)...");
        
        this.map = map;
        this.drones = drones;
        
        // Initialiser le CountDownLatch pour attendre la fin de tous les threads
        completionLatch = new CountDownLatch(drones.size());
        
        System.out.println("Carte initialisée (" + width + "x" + height + ")");
        System.out.println("Nombre de drones: " + drones.size());
        System.out.println("Obstacles générés automatiquement");
    }
    
    /**
     * Initialise et démarre tous les threads
     */
    public void initializeThreads() {
        System.out.println("\nDémarrage des threads de simulation...");
        
        // Créer les threads pour chaque drone
        for (Drone drone : drones) {
            DroneWorker worker = new DroneWorker(drone, map, displayLock, displayCondition);
            droneWorkers.add(worker);
        }
        
        // Créer le thread d'affichage
        displayWorker = new DisplayWorker(map, drones, displayLock, displayCondition);
    }
    
    /**
     * Démarre la simulation concurrente
     */
    public void startSimulation() {
        System.out.println("Début de la simulation concurrente...");
        System.out.println("=========================================");
        
        // Démarrer le thread d'affichage
        displayWorker.start();
        
        // Démarrer tous les threads de drones
        for (DroneWorker worker : droneWorkers) {
            worker.start();
        }
    }
    
    /**
     * Attend que tous les threads drones aient terminé leur exécution
     */
    public void waitForCompletion() {
        try {
            // Attendre que tous les threads de drones terminent
            for (DroneWorker worker : droneWorkers) {
                worker.join();
            }
            
            // Arrêter le thread d'affichage
            displayWorker.stopDisplay();
            displayWorker.join();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Simulation interrompue: " + e.getMessage());
        }
    }
    
    /**
     * Affiche les résultats finaux de la simulation
     */
    public void displayResults() {
        System.out.println("\n=========================================");
        System.out.println("Simulation concurrente terminée !");
        System.out.println("=========================================");
        
        System.out.println("\nPositions finales des drones:");
        for (int i = 0; i < drones.size(); i++) {
            Drone drone = drones.get(i);
            System.out.println("Drone " + i + " : " + drone.getPosition());
        }
    }
    
    /**
     * Arrête proprement tous les threads
     */
    public void stopSimulation() {
        System.out.println("Arrêt de la simulation...");
        
        // Arrêter tous les threads de drones
        for (DroneWorker worker : droneWorkers) {
            worker.stopWorker();
        }
        
        // Arrêter le thread d'affichage
        if (displayWorker != null) {
            displayWorker.stopDisplay();
        }
    }
    
    /**
     * Méthode principale pour exécuter la simulation concurrente
     * @param filename Fichier de configuration
     * @throws Exception En cas d'erreur
     */
    public void runSimulation(String filename) throws Exception {
        try {
            initializeSimulation(filename);
            initializeThreads();
            startSimulation();
            waitForCompletion();
            displayResults();
        } catch (Exception e) {
            System.err.println("Erreur lors de la simulation: " + e.getMessage());
            stopSimulation();
            throw e;
        }
    }
    
    /**
     * Méthode main pour tester la Version 2
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java SimulatorConcurrent <fichier_configuration>");
            System.err.println("Exemple: java SimulatorConcurrent src/main/resources/drones_test.txt");
            System.exit(1);
        }
        
        try {
            SimulatorConcurrent simulator = new SimulatorConcurrent();
            simulator.runSimulation(args[0]);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la simulation: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    // Getters pour les tests
    public SimulatorMap getMap() {
        return map;
    }
    
    public List<Drone> getDrones() {
        return drones;
    }
    
    public List<DroneWorker> getDroneWorkers() {
        return droneWorkers;
    }
}

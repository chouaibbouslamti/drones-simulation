package com.drone.simulator;

import java.util.List;

/**
 * Version basique du simulateur où les drones se déplacent séquentiellement
 */
public class SimulatorBasic {
    
    private SimulatorMap map;
    private List<Drone> drones;
    private ConfigurationReader fileReader;
    
    public SimulatorBasic() {
        this.fileReader = new ConfigurationReader();
    }
    
    /**
     * Initialise la simulation à partir d'un fichier de configuration
     * @param filename Chemin du fichier de configuration
     * @throws Exception En cas d'erreur d'initialisation
     */
    public void initializeSimulation(String filename) throws Exception {
        System.out.println("Initialisation de la simulation basique...");
        
        // Lecture du fichier de configuration
        ConfigurationReader.Configuration config = fileReader.readConfiguration(filename);
        
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
        
        System.out.println("Carte initialisée (" + config.getMapWidth() + "x" + config.getMapHeight() + ")");
        System.out.println("Nombre de drones: " + drones.size());
        System.out.println("Obstacles générés automatiquement");
    }
    
    /**
     * Exécute la simulation séquentielle
     */
    public void executeSimulation() {
        System.out.println("\nDébut de la simulation séquentielle...");
        System.out.println("========================================");
        
        // Afficher la carte initiale
        System.out.println("\nCarte initiale:");
        map.displayMap(drones);
        
        // Exécuter chaque drone séquentiellement
        for (int i = 0; i < drones.size(); i++) {
            Drone drone = drones.get(i);
            System.out.println("\nDrone " + i + " : Position initiale : " + drone.getPosition() + 
                             " | Commandes : " + drone.getCommands());
            System.out.println("===========================================");
            
            // Exécuter chaque commande du drone
            char[] commands = drone.getCommands().toCharArray();
            for (int j = 0; j < commands.length; j++) {
                char command = commands[j];
                
                // Exécuter la commande
                drone.executeCommand(command, map);
                
                // Afficher la position après chaque commande
                System.out.println("Position du drone " + i + " : " + drone.getPosition() + 
                                 " (après commande " + (j+1) + " : " + command + ")");
            }
        }
    }
    
    /**
     * Affiche les résultats finaux de la simulation
     */
    public void displayResults() {
        System.out.println("\n========================================");
        System.out.println("Simulation terminée !");
        System.out.println("========================================");
        
        System.out.println("\nCarte finale:");
        map.displayMap(drones);
        
        System.out.println("\nPositions finales des drones:");
        for (int i = 0; i < drones.size(); i++) {
            Drone drone = drones.get(i);
            System.out.println("Drone " + i + " : " + drone.getPosition());
        }
    }
    
    /**
     * Méthode principale pour exécuter la simulation basique
     * @param filename Fichier de configuration
     * @throws Exception En cas d'erreur
     */
    public void runSimulation(String filename) throws Exception {
        try {
            initializeSimulation(filename);
            executeSimulation();
            displayResults();
        } catch (Exception e) {
            System.err.println("Erreur lors de la simulation: " + e.getMessage());
            throw e;
        }
    }
    
    // Getters pour les tests
    public SimulatorMap getMap() {
        return map;
    }
    
    public List<Drone> getDrones() {
        return drones;
    }
    
    /**
     * Méthode main pour tester la Version 1
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java SimulatorBasic <fichier_configuration>");
            System.err.println("Exemple: java SimulatorBasic src/main/resources/drones_test.txt");
            System.exit(1);
        }
        
        try {
            SimulatorBasic simulator = new SimulatorBasic();
            simulator.runSimulation(args[0]);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la simulation: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

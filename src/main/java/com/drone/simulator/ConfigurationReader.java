package com.drone.simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour la lecture des fichiers de configuration
 */
public class ConfigurationReader {
    
    /**
     * Lit le fichier de configuration et retourne les informations
     * @param filename Chemin du fichier de configuration
     * @return Objet Configuration contenant toutes les informations
     * @throws IOException En cas d'erreur de lecture
     */
    public Configuration readConfiguration(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(filename))) {
            // Lire la première ligne : dimensions de la carte
            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new IOException("Fichier vide");
            }
            
            String[] dimensions = firstLine.split(" ");
            if (dimensions.length != 2) {
                throw new IOException("Format incorrect pour les dimensions de la carte");
            }
            
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);
            
            // Lire les lignes suivantes : informations des drones
            List<Drone> drones = new ArrayList<>();
            String line;
            int droneIndex = 0;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Drone drone = parseDroneData(line, droneIndex);
                    drones.add(drone);
                    droneIndex++;
                }
            }
            
            return new Configuration(width, height, drones);
        }
    }
    
    /**
     * Parse les données d'un drone à partir d'une ligne
     * @param line Ligne contenant les données du drone
     * @param droneIndex Index du drone pour le logging
     * @return Objet Drone initialisé
     */
    private Drone parseDroneData(String line, int droneIndex) {
        String[] parts = line.split(" ");
        
        if (parts.length < 4) {
            throw new IllegalArgumentException("Format incorrect pour les données du drone " + droneIndex + 
                                             ": " + line + " (attendu: x y orientation commandes)");
        }
        
        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            char orientation = parts[2].charAt(0);
            
            // Valider l'orientation
            if (orientation != 'N' && orientation != 'S' && orientation != 'E' && orientation != 'O') {
                throw new IllegalArgumentException("Orientation invalide pour le drone " + droneIndex + 
                                                 ": " + orientation + " (attendu: N, S, E, O)");
            }
            
            // Concaténer le reste des parties pour les commandes
            StringBuilder commands = new StringBuilder();
            for (int i = 3; i < parts.length; i++) {
                commands.append(parts[i]);
            }
            
            // Valider les commandes
            String commandStr = commands.toString();
            for (char cmd : commandStr.toCharArray()) {
                if (cmd != 'L' && cmd != 'R' && cmd != 'M' && cmd != 'B') {
                    throw new IllegalArgumentException("Commande invalide '" + cmd + 
                                                     "' pour le drone " + droneIndex + 
                                                     " (attendu: L, R, M, B)");
                }
            }
            
            return new Drone(x, y, orientation, commandStr);
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format numérique incorrect pour le drone " + droneIndex + 
                                             ": " + line);
        }
    }
    
    /**
     * Parse uniquement les dimensions de la carte
     * @param filename Chemin du fichier
     * @return Tableau [width, height]
     * @throws IOException En cas d'erreur
     */
    public int[] parseMapDimensions(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(filename))) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new IOException("Fichier vide");
            }
            
            String[] dimensions = firstLine.split(" ");
            if (dimensions.length != 2) {
                throw new IOException("Format incorrect pour les dimensions de la carte");
            }
            
            return new int[]{Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1])};
        }
    }
    
    /**
     * Parse uniquement les données des drones
     * @param filename Chemin du fichier
     * @return Liste des drones
     * @throws IOException En cas d'erreur
     */
    public List<Drone> parseDroneData(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(filename))) {
            // Sauter la première ligne (dimensions)
            reader.readLine();
            
            List<Drone> drones = new ArrayList<>();
            String line;
            int droneIndex = 0;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Drone drone = parseDroneData(line, droneIndex);
                    drones.add(drone);
                    droneIndex++;
                }
            }
            
            return drones;
        }
    }
    
    /**
     * Classe interne pour stocker la configuration complète
     */
    public static class Configuration {
        private final int mapWidth;
        private final int mapHeight;
        private final List<Drone> drones;
        
        public Configuration(int mapWidth, int mapHeight, List<Drone> drones) {
            this.mapWidth = mapWidth;
            this.mapHeight = mapHeight;
            this.drones = drones;
        }
        
        public int getMapWidth() {
            return mapWidth;
        }
        
        public int getMapHeight() {
            return mapHeight;
        }
        
        public List<Drone> getDrones() {
            return drones;
        }
    }
}

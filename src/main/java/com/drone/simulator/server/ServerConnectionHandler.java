package com.drone.simulator.server;

import com.drone.simulator.*;
import com.drone.simulator.concurrent.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère la connexion avec un client et exécute la simulation
 */
public class ServerConnectionHandler implements Runnable {
    private Socket socket;
    
    public ServerConnectionHandler(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {
            
            // Recevoir la configuration du client
            String mapDimensions = input.readLine();
            if (mapDimensions == null) {
                System.err.println("Client déconnecté prématurément");
                return;
            }
            
            // Parser les dimensions de la carte
            String[] dims = mapDimensions.split(" ");
            if (dims.length != 2) {
                sendError(output, "Format incorrect pour les dimensions. Attendu: 'width height'");
                return;
            }
            
            int width, height;
            try {
                width = Integer.parseInt(dims[0]);
                height = Integer.parseInt(dims[1]);
            } catch (NumberFormatException e) {
                sendError(output, "Dimensions numériques invalides");
                return;
            }
            
            // Recevoir le nombre de drones
            String numDronesStr = input.readLine();
            if (numDronesStr == null) {
                sendError(output, "Nombre de drones non spécifié");
                return;
            }
            
            int numDrones;
            try {
                numDrones = Integer.parseInt(numDronesStr);
            } catch (NumberFormatException e) {
                sendError(output, "Nombre de drones invalide");
                return;
            }
            
            // Recevoir les données de chaque drone
            List<Drone> drones = new ArrayList<>();
            for (int i = 0; i < numDrones; i++) {
                String droneLine = input.readLine();
                if (droneLine == null) {
                    sendError(output, "Données du drone " + i + " manquantes");
                    return;
                }
                
                try {
                    Drone drone = parseDroneData(droneLine, i);
                    drones.add(drone);
                } catch (Exception e) {
                    sendError(output, "Erreur dans les données du drone " + i + ": " + e.getMessage());
                    return;
                }
            }
            
            // Valider que tous les drones ont été reçus
            if (drones.size() != numDrones) {
                sendError(output, "Nombre de drones reçu (" + drones.size() + 
                          ") différent de celui attendu (" + numDrones + ")");
                return;
            }
            
            // Exécuter la simulation concurrente
            executeSimulation(width, height, drones, output);
            
        } catch (IOException e) {
            System.err.println("Erreur de communication avec le client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
    
    /**
     * Parse les données d'un drone à partir d'une ligne
     */
    private Drone parseDroneData(String line, int droneIndex) {
        String[] parts = line.split(" ");
        
        if (parts.length < 4) {
            throw new IllegalArgumentException("Format incorrect. Attendu: 'x y orientation commandes'");
        }
        
        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            char orientation = parts[2].charAt(0);
            
            // Valider l'orientation
            if (orientation != 'N' && orientation != 'S' && orientation != 'E' && orientation != 'O') {
                throw new IllegalArgumentException("Orientation invalide: " + orientation);
            }
            
            // Concaténer les commandes
            StringBuilder commands = new StringBuilder();
            for (int i = 3; i < parts.length; i++) {
                commands.append(parts[i]);
            }
            
            // Valider les commandes
            String commandStr = commands.toString();
            for (char cmd : commandStr.toCharArray()) {
                if (cmd != 'L' && cmd != 'R' && cmd != 'M' && cmd != 'B') {
                    throw new IllegalArgumentException("Commande invalide: " + cmd);
                }
            }
            
            return new Drone(x, y, orientation, commandStr);
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format numérique incorrect: " + e.getMessage());
        }
    }
    
    /**
     * Exécute la simulation et envoie les résultats au client
     */
    private void executeSimulation(int width, int height, List<Drone> drones, PrintWriter output) {
        try {
            // Créer la carte
            SimulatorMap map = new SimulatorMap(width, height);
            map.addObstacles(0.15); // 15% d'obstacles
            
            // Enlever les obstacles aux positions initiales des drones
            for (Drone drone : drones) {
                map.removeObstacle(drone.getX(), drone.getY());
            }
            
            // Créer et exécuter la simulation concurrente
            SimulatorConcurrent simulator = new SimulatorConcurrent();
            
            // Initialiser manuellement la simulation (sans fichier)
            simulator.initializeSimulation(width, height, drones, map);
            simulator.initializeThreads();
            
            // Envoyer SUCCESS au client
            output.println("SUCCESS");
            output.flush();
            
            // Démarrer la simulation
            simulator.startSimulation();
            
            // Attendre la fin
            simulator.waitForCompletion();
            
            // Envoyer les positions finales
            for (Drone drone : drones) {
                output.println(drone.getX() + " " + drone.getY() + " " + drone.getOrientation());
            }
            output.flush();
            
            System.out.println("Simulation terminée pour le client " + socket.getInetAddress());
            
        } catch (Exception e) {
            sendError(output, "Erreur lors de l'exécution de la simulation: " + e.getMessage());
        }
    }
    
    /**
     * Envoie un message d'erreur au client
     */
    private void sendError(PrintWriter output, String message) {
        output.println("ERROR: " + message);
        output.flush();
        System.err.println("Erreur envoyée au client: " + message);
    }
}

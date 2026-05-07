package com.drone.simulator.client;

import com.drone.simulator.*;
import java.io.*;
import java.net.*;
import java.util.List;

/**
 * Client pour la simulation communicante de drones
 */
public class DroneClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;
    
    private String host;
    private int port;
    private ConfigurationReader configReader;
    
    public DroneClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.configReader = new ConfigurationReader();
    }
    
    public DroneClient() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }
    
    /**
     * Se connecte au serveur et envoie la configuration
     * @param filename Fichier de configuration
     * @throws Exception En cas d'erreur
     */
    public void sendConfiguration(String filename) throws Exception {
        try (Socket socket = new Socket(host, port);
             PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            System.out.println("Connecté au serveur " + host + ":" + port);
            
            // Lire la configuration depuis le fichier
            ConfigurationReader.Configuration config = configReader.readConfiguration(filename);
            
            // Envoyer les dimensions de la carte
            output.println(config.getMapWidth() + " " + config.getMapHeight());
            System.out.println("Envoyé dimensions: " + config.getMapWidth() + "x" + config.getMapHeight());
            
            // Envoyer le nombre de drones
            List<Drone> drones = config.getDrones();
            output.println(drones.size());
            System.out.println("Envoyé nombre de drones: " + drones.size());
            
            // Envoyer les données de chaque drone
            for (int i = 0; i < drones.size(); i++) {
                Drone drone = drones.get(i);
                String droneData = drone.getX() + " " + drone.getY() + " " + 
                                 drone.getOrientation() + " " + drone.getCommands();
                output.println(droneData);
                System.out.println("Envoyé drone " + i + ": " + droneData);
            }
            output.flush();
            
            // Recevoir et afficher les résultats
            receiveResults(input, drones);
            
        } catch (UnknownHostException e) {
            throw new Exception("Hôte inconnu: " + host);
        } catch (ConnectException e) {
            throw new Exception("Connexion refusée au serveur " + host + ":" + port + 
                              ". Le serveur est-il démarré ?");
        } catch (IOException e) {
            throw new Exception("Erreur de communication avec le serveur: " + e.getMessage());
        }
    }
    
    /**
     * Reçoit et affiche les résultats du serveur
     */
    private void receiveResults(BufferedReader input, List<Drone> originalDrones) throws IOException {
        System.out.println("\nEn attente des résultats du serveur...");
        
        String status = input.readLine();
        if (status == null) {
            System.err.println("Le serveur a fermé la connexion");
            return;
        }
        
        if (status.startsWith("ERROR")) {
            System.err.println("Erreur du serveur: " + status.substring(6));
            return;
        }
        
        if ("SUCCESS".equals(status)) {
            System.out.println("Simulation réussie !");
            
            // Recevoir les positions finales
            System.out.println("\nPositions finales des drones:");
            for (int i = 0; i < originalDrones.size(); i++) {
                String resultLine = input.readLine();
                if (resultLine != null) {
                    String[] parts = resultLine.split(" ");
                    if (parts.length == 3) {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        char orientation = parts[2].charAt(0);
                        
                        Drone original = originalDrones.get(i);
                        System.out.println("Drone " + i + " : " + x + " " + y + " " + orientation + 
                                         " (initial: " + original.getPosition() + ")");
                    }
                }
            }
        } else {
            System.err.println("Réponse inattendue du serveur: " + status);
        }
    }
    
    /**
     * Méthode principale pour le client
     */
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            System.err.println("Usage: java DroneClient <fichier_configuration> [hôte] [port]");
            System.err.println("Exemples:");
            System.err.println("  java DroneClient config.txt");
            System.err.println("  java DroneClient config.txt localhost 5000");
            System.err.println("  java DroneClient config.txt 192.168.1.100 5000");
            System.exit(1);
        }
        
        String filename = args[0];
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        
        // Parser les arguments optionnels
        if (args.length >= 2) {
            host = args[1];
        }
        if (args.length >= 3) {
            try {
                port = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("Port invalide: " + args[2] + ". Utilisation du port par défaut: " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }
        
        DroneClient client = new DroneClient(host, port);
        
        try {
            client.sendConfiguration(filename);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du client: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

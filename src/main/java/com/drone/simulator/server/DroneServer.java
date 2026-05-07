package com.drone.simulator.server;

import com.drone.simulator.*;
import com.drone.simulator.concurrent.*;
import java.io.*;
import java.net.*;

/**
 * Serveur pour la simulation communicante de drones
 */
public class DroneServer {
    private static final int DEFAULT_PORT = 5000;
    private int port;
    private ServerSocket serverSocket;
    private volatile boolean running = true;
    
    public DroneServer(int port) {
        this.port = port;
    }
    
    public DroneServer() {
        this(DEFAULT_PORT);
    }
    
    /**
     * Démarre le serveur et attend les connexions clientes
     */
    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Serveur démarré sur le port " + port);
        System.out.println("En attente de connexions clientes...");
        
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connecté : " + clientSocket.getInetAddress());
                
                // Créer un thread pour gérer ce client
                ServerConnectionHandler handler = new ServerConnectionHandler(clientSocket);
                new Thread(handler).start();
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("Erreur lors de l'acceptation de connexion: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Arrête proprement le serveur
     */
    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'arrêt du serveur: " + e.getMessage());
        }
    }
    
    /**
     * Méthode principale pour démarrer le serveur
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        
        // Parser les arguments
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Port invalide: " + args[0] + ". Utilisation du port par défaut: " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }
        
        DroneServer server = new DroneServer(port);
        
        // Gérer l'arrêt propre avec Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nArrêt du serveur...");
            server.stopServer();
        }));
        
        try {
            server.startServer();
        } catch (IOException e) {
            System.err.println("Impossible de démarrer le serveur: " + e.getMessage());
            System.exit(1);
        }
    }
}

package com.drone.simulator;

import com.drone.simulator.concurrent.SimulatorConcurrent;
import com.drone.simulator.server.DroneServer;
import com.drone.simulator.client.DroneClient;
import java.util.Scanner;

/**
 * Classe principale avec menu pour choisir le mode d'exécution du simulateur
 */
public class DroneSimulatorMain {
    
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("    SIMULATEUR DE FLOTTE DE DRONES");
        System.out.println("=========================================");
        System.out.println();
        
        // Si des arguments sont fournis, exécuter directement
        if (args.length > 0) {
            executeFromArgs(args);
            return;
        }
        
        // Sinon, afficher le menu
        showMainMenu();
    }
    
    /**
     * Exécute le simulateur à partir des arguments en ligne de commande
     */
    private static void executeFromArgs(String[] args) {
        if (args.length == 2 && "basic".equals(args[0])) {
            // Mode basique avec fichier
            runBasicMode(args[1]);
        } else if (args.length == 2 && "concurrent".equals(args[0])) {
            // Mode concurrent avec fichier
            runConcurrentMode(args[1]);
        } else if (args.length == 1 && "server".equals(args[0])) {
            // Mode serveur
            runServerMode();
        } else if (args.length >= 2 && "client".equals(args[0])) {
            // Mode client
            String filename = args[1];
            String host = "localhost";
            int port = 5000;
            
            if (args.length >= 3) {
                host = args[2];
            }
            if (args.length >= 4) {
                try {
                    port = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    System.err.println("Port invalide: " + args[3]);
                    return;
                }
            }
            
            runClientMode(filename, host, port);
        } else {
            showUsage();
        }
    }
    
    /**
     * Affiche le menu principal interactif
     */
    private static void showMainMenu() {
        while (true) {
            System.out.println("Choisissez le mode d'exécution:");
            System.out.println("1. Mode Basique (séquentiel)");
            System.out.println("2. Mode Concurrent (avec threads)");
            System.out.println("3. Mode Serveur (communicant)");
            System.out.println("4. Mode Client (communicant)");
            System.out.println("5. Quitter");
            System.out.print("Votre choix (1-5): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    runBasicModeInteractive();
                    break;
                case "2":
                    runConcurrentModeInteractive();
                    break;
                case "3":
                    runServerMode();
                    break;
                case "4":
                    runClientModeInteractive();
                    break;
                case "5":
                    System.out.println("Au revoir !");
                    return;
                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
                    break;
            }
            
            System.out.println();
        }
    }
    
    /**
     * Exécute le mode basique de manière interactive
     */
    private static void runBasicModeInteractive() {
        System.out.print("Entrez le chemin du fichier de configuration: ");
        String filename = scanner.nextLine().trim();
        runBasicMode(filename);
    }
    
    /**
     * Exécute le mode concurrent de manière interactive
     */
    private static void runConcurrentModeInteractive() {
        System.out.print("Entrez le chemin du fichier de configuration: ");
        String filename = scanner.nextLine().trim();
        runConcurrentMode(filename);
    }
    
    /**
     * Exécute le mode client de manière interactive
     */
    private static void runClientModeInteractive() {
        System.out.print("Entrez le chemin du fichier de configuration: ");
        String filename = scanner.nextLine().trim();
        
        System.out.print("Entrez l'hôte du serveur (localhost): ");
        String host = scanner.nextLine().trim();
        if (host.isEmpty()) {
            host = "localhost";
        }
        
        System.out.print("Entrez le port du serveur (5000): ");
        String portStr = scanner.nextLine().trim();
        int port = 5000;
        if (!portStr.isEmpty()) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                System.out.println("Port invalide, utilisation du port par défaut 5000");
                port = 5000;
            }
        }
        
        runClientMode(filename, host, port);
    }
    
    /**
     * Exécute le mode basique
     */
    private static void runBasicMode(String filename) {
        System.out.println("\n=== DÉMARRAGE MODE BASIQUE ===");
        try {
            SimulatorBasic simulator = new SimulatorBasic();
            simulator.runSimulation(filename);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du mode basique: " + e.getMessage());
        }
        System.out.println("=== MODE BASIQUE TERMINÉ ===\n");
    }
    
    /**
     * Exécute le mode concurrent
     */
    private static void runConcurrentMode(String filename) {
        System.out.println("\n=== DÉMARRAGE MODE CONCURRENT ===");
        try {
            SimulatorConcurrent simulator = new SimulatorConcurrent();
            simulator.runSimulation(filename);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du mode concurrent: " + e.getMessage());
        }
        System.out.println("=== MODE CONCURRENT TERMINÉ ===\n");
    }
    
    /**
     * Exécute le mode serveur
     */
    private static void runServerMode() {
        System.out.print("Entrez le port d'écoute (5000): ");
        String portStr = scanner.nextLine().trim();
        int port = 5000;
        if (!portStr.isEmpty()) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                System.out.println("Port invalide, utilisation du port par défaut 5000");
                port = 5000;
            }
        }
        
        System.out.println("\n=== DÉMARRAGE MODE SERVEUR ===");
        try {
            DroneServer server = new DroneServer(port);
            
            // Gérer l'arrêt propre avec Ctrl+C
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nArrêt du serveur...");
                server.stopServer();
            }));
            
            server.startServer();
        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage du serveur: " + e.getMessage());
        }
        System.out.println("=== MODE SERVEUR TERMINÉ ===\n");
    }
    
    /**
     * Exécute le mode client
     */
    private static void runClientMode(String filename, String host, int port) {
        System.out.println("\n=== DÉMARRAGE MODE CLIENT ===");
        try {
            DroneClient client = new DroneClient(host, port);
            client.sendConfiguration(filename);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du client: " + e.getMessage());
        }
        System.out.println("=== MODE CLIENT TERMINÉ ===\n");
    }
    
    /**
     * Affiche l'aide pour l'utilisation en ligne de commande
     */
    private static void showUsage() {
        System.out.println("Usage:");
        System.out.println("  java -jar drone-simulator.jar");
        System.out.println("      Lance le menu interactif");
        System.out.println();
        System.out.println("  java -jar drone-simulator.jar basic <fichier>");
        System.out.println("      Exécute le mode basique avec le fichier spécifié");
        System.out.println();
        System.out.println("  java -jar drone-simulator.jar concurrent <fichier>");
        System.out.println("      Exécute le mode concurrent avec le fichier spécifié");
        System.out.println();
        System.out.println("  java -jar drone-simulator.jar server [port]");
        System.out.println("      Démarre le serveur sur le port spécifié (défaut: 5000)");
        System.out.println();
        System.out.println("  java -jar drone-simulator.jar client <fichier> [hôte] [port]");
        System.out.println("      Exécute le client avec le fichier spécifié");
        System.out.println("      hôte: défaut 'localhost'");
        System.out.println("      port: défaut 5000");
        System.out.println();
        System.out.println("Exemples:");
        System.out.println("  java -jar drone-simulator.jar basic config.txt");
        System.out.println("  java -jar drone-simulator.jar concurrent config.txt");
        System.out.println("  java -jar drone-simulator.jar server 5000");
        System.out.println("  java -jar drone-simulator.jar client config.txt 192.168.1.100 5000");
    }
}

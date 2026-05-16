package com.drone.simulator.client;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe de test pour le client de la Version 3
 */
public class DroneClientTest {
    
    private Thread mockServerThread;
    private ServerSocket mockServerSocket;
    private static final int MOCK_PORT = 5002;
    
    @Before
    public void setUp() throws IOException {
        // Créer un serveur mock pour les tests
        mockServerSocket = new ServerSocket(MOCK_PORT);
        
        mockServerThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    try (Socket clientSocket = mockServerSocket.accept();
                         PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                         BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        
                        // Lire la configuration
                        String configLine = input.readLine();
                        String numDronesStr = input.readLine();
                        int numDrones = Integer.parseInt(numDronesStr);
                        
                        // Lire les données des drones
                        for (int i = 0; i < numDrones; i++) {
                            String droneData = input.readLine();
                            System.out.println("Mock server received drone " + i + ": " + droneData);
                        }
                        
                        // Envoyer SUCCESS et résultats factices
                        output.println("SUCCESS");
                        for (int i = 0; i < numDrones; i++) {
                            output.println("0 0 N"); // Position factice
                        }
                        
                    } catch (Exception e) {
                        break;
                    }
                }
            } catch (Exception e) {
                // Erreur attendue lors de l'arrêt
            }
        });
        mockServerThread.start();
        
        // Attendre que le serveur mock démarre
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @After
    public void tearDown() {
        if (mockServerSocket != null && !mockServerSocket.isClosed()) {
            try {
                mockServerSocket.close();
            } catch (IOException e) {
                // Ignorer
            }
        }
        if (mockServerThread != null) {
            mockServerThread.interrupt();
            try {
                mockServerThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Test
    public void testClientConnection() throws Exception {
        createTestFile();
        
        DroneClient client = new DroneClient("localhost", MOCK_PORT);
        
        // Le test ne devrait pas lancer d'exception
        try {
            client.sendConfiguration("src/test/resources/client_test.txt");
        } catch (Exception e) {
            fail("Le client devrait se connecter sans erreur: " + e.getMessage());
        }
    }
    
    @Test
    public void testClientConnectionFailure() {
        DroneClient client = new DroneClient("localhost", 9999); // Port invalide
        
        try {
            client.sendConfiguration("src/test/resources/client_test.txt");
            fail("Devrait lancer une exception de connexion");
        } catch (Exception e) {
            assertTrue("Devrait être une erreur de connexion", 
                      e.getMessage().contains("Connexion refusée") ||
                      e.getMessage().contains("Connection refused"));
        }
    }
    
    @Test
    public void testClientWithInvalidHost() {
        DroneClient client = new DroneClient("invalid_host_12345", MOCK_PORT);
        
        try {
            client.sendConfiguration("src/test/resources/client_test.txt");
            fail("Devrait lancer une exception d'hôte inconnu");
        } catch (Exception e) {
            assertTrue("Devrait être une erreur d'hôte inconnu", 
                      e.getMessage().contains("Hôte inconnu"));
        }
    }
    
    @Test
    public void testClientDefaultConstructor() {
        DroneClient client = new DroneClient();
        // Le client devrait utiliser les valeurs par défaut
        assertNotNull("Le client devrait être créé", client);
    }
    
    /**
     * Crée un fichier de test pour le client
     */
    private void createTestFile() throws Exception {
        java.io.File testDir = new java.io.File("src/test/resources");
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
        
        try (java.io.PrintWriter writer = new java.io.PrintWriter("src/test/resources/client_test.txt")) {
            writer.println("10 10");
            writer.println("0 0 N MM");
            writer.println("5 5 S RR");
        }
    }
}

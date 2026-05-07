package com.drone.simulator.server;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Classe de test pour le serveur de la Version 3
 */
public class DroneServerTest {
    
    private DroneServer server;
    private Thread serverThread;
    private static final int TEST_PORT = 5001; // Port différent pour les tests
    
    @Before
    public void setUp() throws IOException {
        server = new DroneServer(TEST_PORT);
        
        // Démarrer le serveur dans un thread séparé
        serverThread = new Thread(() -> {
            try {
                server.startServer();
            } catch (IOException e) {
                // Erreur attendue lors de l'arrêt
            }
        });
        serverThread.start();
        
        // Attendre que le serveur démarre
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @After
    public void tearDown() {
        if (server != null) {
            server.stopServer();
        }
        if (serverThread != null) {
            serverThread.interrupt();
            try {
                serverThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Test
    public void testServerStartup() throws IOException {
        // Vérifier que le serveur accepte les connexions
        try (Socket socket = new Socket("localhost", TEST_PORT)) {
            assertTrue("Le serveur devrait accepter les connexions", socket.isConnected());
        }
    }
    
    @Test
    public void testMultipleConnections() throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        
        // Créer plusieurs connexions simultanées
        for (int i = 0; i < 3; i++) {
            final int clientId = i;
            new Thread(() -> {
                try {
                    try (Socket socket = new Socket("localhost", TEST_PORT)) {
                        assertTrue("Connexion " + clientId + " réussie", socket.isConnected());
                        latch.countDown();
                    }
                } catch (IOException e) {
                    // Erreur de connexion possible
                }
            }).start();
        }
        
        // Attendre que toutes les connexions soient établies
        assertTrue("Les connexions devraient être établies", 
                  latch.await(2, TimeUnit.SECONDS));
    }
    
    @Test
    public void testServerStop() {
        // Arrêter le serveur
        server.stopServer();
        
        // Essayer de se connecter - devrait échouer
        try {
            Socket socket = new Socket("localhost", TEST_PORT);
            socket.close();
            fail("La connexion devrait échouer après arrêt du serveur");
        } catch (IOException e) {
            // Erreur attendue
            assertTrue("Erreur de connexion attendue", 
                      e.getMessage().contains("Connection refused") || 
                      e.getMessage().contains("Connexion refusée"));
        }
    }
}

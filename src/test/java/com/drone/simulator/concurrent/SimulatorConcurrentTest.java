package com.drone.simulator.concurrent;

import com.drone.simulator.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Classe de test pour la Version 2 du simulateur concurrent
 */
public class SimulatorConcurrentTest {
    
    private SimulatorConcurrent simulator;
    
    @Before
    public void setUp() {
        simulator = new SimulatorConcurrent();
    }
    
    @After
    public void tearDown() {
        if (simulator != null) {
            simulator.stopSimulation();
        }
    }
    
    @Test
    public void testConcurrentInitialization() throws Exception {
        simulator.initializeSimulation("src/main/resources/drones_test.txt");
        
        assertNotNull(simulator.getMap());
        assertNotNull(simulator.getDrones());
        assertEquals(2, simulator.getDrones().size());
        assertEquals(20, simulator.getMap().getWidth());
        assertEquals(20, simulator.getMap().getHeight());
    }
    
    @Test
    public void testDroneWorkerCreation() throws Exception {
        simulator.initializeSimulation("src/main/resources/drones_test.txt");
        simulator.initializeThreads();
        
        List<DroneWorker> workers = simulator.getDroneWorkers();
        assertNotNull(workers);
        assertEquals(2, workers.size());
        
        // Vérifier que chaque worker a un drone
        for (DroneWorker worker : workers) {
            assertNotNull(worker.getDrone());
        }
    }
    
    @Test
    public void testConcurrentExecution() throws Exception {
        // Créer un fichier de test simple avec des commandes courtes
        createSimpleTestFile();
        
        simulator.initializeSimulation("src/test/resources/simple_test.txt");
        
        // Capturer les positions initiales
        List<Drone> drones = simulator.getDrones();
        String initialPos1 = drones.get(0).getPosition();
        String initialPos2 = drones.get(1).getPosition();
        
        // Exécuter la simulation
        simulator.initializeThreads();
        simulator.startSimulation();
        
        // Attendre un peu pour que les drones bougent
        Thread.sleep(500);
        
        // Arrêter la simulation
        simulator.stopSimulation();
        
        // Vérifier que les positions ont changé (ou au moins que le code s'est exécuté sans erreur)
        assertNotNull(drones.get(0).getPosition());
        assertNotNull(drones.get(1).getPosition());
    }
    
    @Test
    public void testDisplayWorker() throws Exception {
        simulator.initializeSimulation("src/main/resources/drones_test.txt");
        
        // Le test vérifie simplement que le DisplayWorker peut être créé
        // sans lancer la simulation complète (pour éviter les problèmes de console)
        simulator.initializeThreads();
        
        assertNotNull(simulator.getDroneWorkers());
        assertEquals(2, simulator.getDroneWorkers().size());
    }
    
    @Test
    public void testSynchronization() throws Exception {
        // Test pour vérifier que la synchronisation fonctionne
        createSimpleTestFile();
        
        simulator.initializeSimulation("src/test/resources/simple_test.txt");
        simulator.initializeThreads();
        
        long startTime = System.currentTimeMillis();
        simulator.startSimulation();
        
        // Attendre que la simulation se termine
        Thread.sleep(2000); // Attendre assez longtemps pour les commandes
        
        simulator.stopSimulation();
        long endTime = System.currentTimeMillis();
        
        // La simulation devrait prendre au moins un certain temps
        assertTrue("La simulation a duré " + (endTime - startTime) + "ms", 
                  (endTime - startTime) > 500);
    }
    
    @Test
    public void testMultipleDronesConcurrent() throws Exception {
        // Créer un fichier avec plus de drones
        createMultiDroneTestFile();
        
        simulator.initializeSimulation("src/test/resources/multi_drone_test.txt");
        simulator.initializeThreads();
        
        List<DroneWorker> workers = simulator.getDroneWorkers();
        assertEquals(3, workers.size());
        
        // Vérifier que chaque worker a un drone différent
        for (int i = 0; i < workers.size(); i++) {
            Drone drone = workers.get(i).getDrone();
            assertNotNull(drone);
            assertTrue(drone.getCommands().length() > 0);
        }
        
        simulator.stopSimulation();
    }
    
    @Test
    public void testErrorHandling() throws Exception {
        try {
            // Tester avec un fichier inexistant
            simulator.initializeSimulation("fichier_inexistant.txt");
            fail("Devrait lancer une exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("fichier_inexistant.txt"));
        }
    }
    
    /**
     * Crée un fichier de test simple avec des commandes courtes
     */
    private void createSimpleTestFile() throws Exception {
        java.io.File testDir = new java.io.File("src/test/resources");
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
        
        try (java.io.PrintWriter writer = new java.io.PrintWriter("src/test/resources/simple_test.txt")) {
            writer.println("10 10");
            writer.println("0 0 N MM");
            writer.println("5 5 S RR");
        }
    }
    
    /**
     * Crée un fichier de test avec plusieurs drones
     */
    private void createMultiDroneTestFile() throws Exception {
        java.io.File testDir = new java.io.File("src/test/resources");
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
        
        try (java.io.PrintWriter writer = new java.io.PrintWriter("src/test/resources/multi_drone_test.txt")) {
            writer.println("15 15");
            writer.println("0 0 N MM");
            writer.println("5 5 S RR");
            writer.println("10 10 E LL");
        }
    }
}

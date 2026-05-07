package com.drone.simulator.concurrent;

import com.drone.simulator.Drone;
import com.drone.simulator.SimulatorMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread dédié à l'affichage en continu de la carte avec les drones et les obstacles
 */
public class DisplayWorker extends Thread {
    private SimulatorMap map;
    private List<Drone> drones;
    private volatile boolean running = true;
    private ReentrantLock displayLock;
    private java.util.concurrent.locks.Condition displayCondition;
    
    public DisplayWorker(SimulatorMap map, List<Drone> drones, 
                      ReentrantLock displayLock, java.util.concurrent.locks.Condition displayCondition) {
        this.map = map;
        this.drones = drones;
        this.displayLock = displayLock;
        this.displayCondition = displayCondition;
    }
    
    @Override
    public void run() {
        while (running) {
            displayLock.lock();
            try {
                // Attendre un signal des threads drone ou timeout
                displayCondition.await(100, java.util.concurrent.TimeUnit.MILLISECONDS);
                
                if (running) {
                    clearScreen();
                    map.displayMap(drones);
                    
                    // Afficher les informations des drones
                    System.out.println("\nInformations des drones:");
                    for (int i = 0; i < drones.size(); i++) {
                        Drone drone = drones.get(i);
                        System.out.println("Drone " + i + " : " + drone.getPosition() + 
                                         " | Commandes restantes : " + getRemainingCommands(drone));
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                displayLock.unlock();
            }
        }
    }
    
    /**
     * Efface l'écran console
     */
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    /**
     * Calcule le nombre de commandes restantes pour un drone
     */
    private String getRemainingCommands(Drone drone) {
        // Cette méthode est une approximation car nous ne suivons pas l'index exact
        // Dans une implémentation plus complète, nous pourrions suivre l'état exact
        return drone.getCommands().length() + " commandes";
    }
    
    /**
     * Arrête proprement le thread d'affichage
     */
    public void stopDisplay() {
        running = false;
        this.interrupt();
        
        // Affichage final
        displayLock.lock();
        try {
            clearScreen();
            System.out.println("=== Simulation terminée ===");
            map.displayMap(drones);
            
            System.out.println("\nPositions finales des drones:");
            for (int i = 0; i < drones.size(); i++) {
                Drone drone = drones.get(i);
                System.out.println("Drone " + i + " : " + drone.getPosition());
            }
        } finally {
            displayLock.unlock();
        }
    }
}

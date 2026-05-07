package com.drone.simulator.concurrent;

import com.drone.simulator.Drone;
import com.drone.simulator.SimulatorMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread qui gère l'exécution des commandes d'un drone de manière concurrente
 */
public class DroneWorker extends Thread {
    private Drone drone;
    private SimulatorMap map;
    private ReentrantLock displayLock;
    private java.util.concurrent.locks.Condition displayCondition;
    private volatile boolean running = true;
    
    public DroneWorker(Drone drone, SimulatorMap map, ReentrantLock displayLock, 
                     java.util.concurrent.locks.Condition displayCondition) {
        this.drone = drone;
        this.map = map;
        this.displayLock = displayLock;
        this.displayCondition = displayCondition;
    }
    
    @Override
    public void run() {
        try {
            char[] commands = drone.getCommands().toCharArray();
            
            for (char command : commands) {
                if (!running) {
                    break;
                }
                
                // Exécuter la commande
                drone.executeCommand(command, map);
                
                // Synchronisation avec le thread d'affichage
                displayLock.lock();
                try {
                    displayCondition.signalAll(); // Réveiller le thread d'affichage
                } finally {
                    displayLock.unlock();
                }
                
                // Pause pour simuler le temps de déplacement
                Thread.sleep(200); // 200ms par mouvement
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("DroneWorker interrompu: " + drone.getPosition());
        }
    }
    
    /**
     * Arrête proprement le thread
     */
    public void stopWorker() {
        running = false;
        this.interrupt();
    }
    
    /**
     * Retourne le drone géré par ce worker
     */
    public Drone getDrone() {
        return drone;
    }
}

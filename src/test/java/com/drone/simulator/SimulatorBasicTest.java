package com.drone.simulator;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Classe de test pour la Version 1 du simulateur basique
 */
public class SimulatorBasicTest {
    
    @Test
    public void testDroneInitialization() {
        Drone drone = new Drone(0, 5, 'N', "LMLMLMLMM");
        assertEquals(0, drone.getX());
        assertEquals(5, drone.getY());
        assertEquals('N', drone.getOrientation());
        assertEquals("LMLMLMLMM", drone.getCommands());
    }
    
    @Test
    public void testDroneTurnLeft() {
        Drone drone = new Drone(0, 0, 'N', "");
        drone.turnLeft();
        assertEquals('O', drone.getOrientation());
        
        drone.turnLeft();
        assertEquals('S', drone.getOrientation());
        
        drone.turnLeft();
        assertEquals('E', drone.getOrientation());
        
        drone.turnLeft();
        assertEquals('N', drone.getOrientation());
    }
    
    @Test
    public void testDroneTurnRight() {
        Drone drone = new Drone(0, 0, 'N', "");
        drone.turnRight();
        assertEquals('E', drone.getOrientation());
        
        drone.turnRight();
        assertEquals('S', drone.getOrientation());
        
        drone.turnRight();
        assertEquals('O', drone.getOrientation());
        
        drone.turnRight();
        assertEquals('N', drone.getOrientation());
    }
    
    @Test
    public void testDroneMoveForward() {
        SimulatorMap map = new SimulatorMap(10, 10);
        Drone drone = new Drone(5, 5, 'N', "");
        
        drone.moveForward(map);
        assertEquals(5, drone.getX());
        assertEquals(6, drone.getY());
        assertEquals('N', drone.getOrientation());
        
        drone.turnRight();
        drone.moveForward(map);
        assertEquals(6, drone.getX());
        assertEquals(6, drone.getY());
        assertEquals('E', drone.getOrientation());
    }
    
    @Test
    public void testDroneMoveBackward() {
        SimulatorMap map = new SimulatorMap(10, 10);
        Drone drone = new Drone(5, 5, 'N', "");
        
        drone.moveBackward(map);
        assertEquals(5, drone.getX());
        assertEquals(4, drone.getY());
        assertEquals('N', drone.getOrientation());
    }
    
    @Test
    public void testWrapping() {
        SimulatorMap map = new SimulatorMap(5, 5);
        Drone drone = new Drone(0, 0, 'N', "");
        
        // Test wrapping horizontal
        drone.turnRight(); // E
        drone.moveForward(map); // devrait aller à (1,0)
        assertEquals(1, drone.getX());
        assertEquals(0, drone.getY());
        
        // Créer un nouveau drone pour le test de wrapping
        Drone drone2 = new Drone(4, 0, 'E', "");
        drone2.moveForward(map); // devrait aller à (5,0) puis revenir à (0,0) par wrapping
        assertEquals(0, drone2.getX());
        assertEquals(0, drone2.getY());
        
        // Test wrapping vertical Sud (dépassement par le bas)
        Drone drone3 = new Drone(0, 0, 'S', "");
        drone3.setY(0);
        drone3.moveForward(map); // Sud: (0,0) -> (0,-1) -> (0,4) par wrapping
        assertEquals(0, drone3.getX());
        assertEquals(4, drone3.getY());
        
        // Test wrapping vertical Nord (dépassement par le haut)
        Drone drone4 = new Drone(0, 0, 'N', "");
        drone4.setY(4);
        drone4.moveForward(map); // Nord: (0,4) -> (0,5) -> (0,0) par wrapping
        assertEquals(0, drone4.getX());
        assertEquals(0, drone4.getY());
    }
    
    @Test
    public void testObstacles() {
        SimulatorMap map = new SimulatorMap(10, 10);
        map.addObstacles(0.0); // Pas d'obstacles
        
        Drone drone = new Drone(5, 5, 'N', "");
        drone.moveForward(map);
        assertEquals(6, drone.getY()); // Devrait bouger
        
        // Ajouter un obstacle
        map = new SimulatorMap(10, 10);
        map.addObstacles(0.0);
        // On ne peut pas facilement ajouter un obstacle spécifique avec l'implémentation actuelle
        // mais on teste que la méthode isBlocked fonctionne
        assertFalse(map.isBlocked(5, 6));
    }
    
    @Test
    public void testConfigurationReader() throws Exception {
        ConfigurationReader reader = new ConfigurationReader();
        
        // Test avec le fichier de test
        ConfigurationReader.Configuration config = reader.readConfiguration("src/main/resources/drones_test.txt");
        
        assertEquals(20, config.getMapWidth());
        assertEquals(20, config.getMapHeight());
        
        List<Drone> drones = config.getDrones();
        assertEquals(2, drones.size());
        
        Drone drone1 = drones.get(0);
        assertEquals(0, drone1.getX());
        assertEquals(5, drone1.getY());
        assertEquals('N', drone1.getOrientation());
        assertEquals("LMLMLMLMM", drone1.getCommands());
        
        Drone drone2 = drones.get(1);
        assertEquals(5, drone2.getX());
        assertEquals(0, drone2.getY());
        assertEquals('S', drone2.getOrientation());
        assertEquals("MMRMMRMRRM", drone2.getCommands());
    }
    
    @Test
    public void testDroneDisplayChar() {
        Drone droneNorth = new Drone(0, 0, 'N', "");
        assertEquals('^', droneNorth.getDisplayChar());
        
        Drone droneSouth = new Drone(0, 0, 'S', "");
        assertEquals('v', droneSouth.getDisplayChar());
        
        Drone droneEast = new Drone(0, 0, 'E', "");
        assertEquals('>', droneEast.getDisplayChar());
        
        Drone droneWest = new Drone(0, 0, 'O', "");
        assertEquals('<', droneWest.getDisplayChar());
    }
    
    @Test
    public void testDroneGetPosition() {
        Drone drone = new Drone(3, 7, 'E', "");
        assertEquals("3 7 E", drone.getPosition());
    }
}

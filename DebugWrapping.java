import com.drone.simulator.Drone;
import com.drone.simulator.SimulatorMap;

public class DebugWrapping {
    public static void main(String[] args) {
        SimulatorMap map = new SimulatorMap(5, 5);
        
        // Test horizontal wrapping
        Drone drone2 = new Drone(4, 0, 'E', "");
        System.out.println("Avant mouvement: " + drone2.getPosition());
        drone2.moveForward(map);
        System.out.println("Après mouvement: " + drone2.getPosition());
        
        // Test vertical wrapping
        Drone drone3 = new Drone(0, 4, 'S', "");
        System.out.println("Avant mouvement: " + drone3.getPosition());
        drone3.moveForward(map);
        System.out.println("Après mouvement: " + drone3.getPosition());
    }
}

package com.drone.simulator.visual;

import com.drone.simulator.*;
import com.drone.simulator.concurrent.SimulatorConcurrent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Visualisation simple pour soutenance avec Swing
 */
public class DroneVisualizer extends JFrame {
    
    private SimulatorMap map;
    private List<Drone> drones;
    private JPanel mapPanel;
    private JPanel controlPanel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JLabel statusLabel;
    private Timer animationTimer;
    private boolean isRunning = false;
    private int cellSize = 30;
    
    public DroneVisualizer() {
        setTitle("Simulateur de Flotte de Drones - Visualisation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        initializeComponents();
        setupDemoScenario();
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Panneau de contrôle
        controlPanel = new JPanel(new FlowLayout());
        startButton = new JButton("▶️ Démarrer");
        pauseButton = new JButton("⏸️ Pause");
        resetButton = new JButton("🔄 Réinitialiser");
        statusLabel = new JLabel("🟢 Prêt");
        
        startButton.addActionListener(e -> startAnimation());
        pauseButton.addActionListener(e -> pauseAnimation());
        resetButton.addActionListener(e -> resetSimulation());
        
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(resetButton);
        controlPanel.add(statusLabel);
        
        // Panneau de carte
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }
        };
        
        add(controlPanel, BorderLayout.NORTH);
        add(mapPanel, BorderLayout.CENTER);
        
        // Timer pour animation
        animationTimer = new Timer(500, e -> {
            updateSimulation();
            repaint();
        });
    }
    
    private void setupDemoScenario() {
        // Scénario de démonstration pour soutenance
        map = new SimulatorMap(15, 10);
        map.addObstacles(0.1); // 10% d'obstacles pour visibilité
        
        drones = List.of(
            new Drone(1, 1, 'E', "MMMRMM"),
            new Drone(10, 5, 'W', "MMMLMM"),
            new Drone(5, 8, 'N', "MMRMMM")
        );
        
        // Nettoyer les positions initiales
        for (Drone drone : drones) {
            map.removeObstacle(drone.getX(), drone.getY());
        }
        
        updatePanelSize();
    }
    
    private void updatePanelSize() {
        int width = map.getWidth() * cellSize;
        int height = map.getHeight() * cellSize;
        mapPanel.setPreferredSize(new Dimension(width, height));
        pack();
    }
    
    private void drawMap(Graphics g) {
        if (map == null || drones == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dessiner la grille
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                int px = x * cellSize;
                int py = y * cellSize;
                
                // Fond
                g2d.setColor(Color.WHITE);
                g2d.fillRect(px, py, cellSize, cellSize);
                
                // Grille
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawRect(px, py, cellSize, cellSize);
                
                // Obstacles
                if (map.isObstacle(x, y)) {
                    g2d.setColor(Color.RED);
                    g2d.fillRect(px + 2, py + 2, cellSize - 4, cellSize - 4);
                }
            }
        }
        
        // Dessiner les drones
        for (int i = 0; i < drones.size(); i++) {
            Drone drone = drones.get(i);
            drawDrone(g2d, drone, i);
        }
        
        // Légende
        drawLegend(g2d);
    }
    
    private void drawDrone(Graphics2D g2d, Drone drone, int index) {
        int px = drone.getX() * cellSize + cellSize / 2;
        int py = drone.getY() * cellSize + cellSize / 2;
        
        // Couleur différente par drone
        Color[] colors = {Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA};
        Color droneColor = colors[index % colors.length];
        
        // Cercle du drone
        g2d.setColor(droneColor);
        g2d.fillOval(px - 8, py - 8, 16, 16);
        
        // Orientation
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        int[] dx = {0, 10, 0, -10}; // E, S, W, N
        int[] dy = {10, 0, -10, 0}; // E, S, W, N
        int dirIndex = "ESWN".indexOf(drone.getOrientation());
        if (dirIndex >= 0) {
            g2d.drawLine(px, py, px + dx[dirIndex], py + dy[dirIndex]);
        }
        
        // Numéro du drone
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString(String.valueOf(index), px - 3, py + 3);
    }
    
    private void drawLegend(Graphics2D g2d) {
        int legendX = map.getWidth() * cellSize + 20;
        int legendY = 20;
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("LÉGENDE", legendX, legendY);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.drawString("🔴 Obstacles", legendX, legendY + 20);
        g2d.drawString("🔵 Drone 0", legendX, legendY + 40);
        g2d.drawString("🟢 Drone 1", legendX, legendY + 60);
        g2d.drawString("🟠 Drone 2", legendX, legendY + 80);
        
        // Informations drones
        legendY += 120;
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("DRONES", legendX, legendY);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int i = 0; i < drones.size(); i++) {
            Drone drone = drones.get(i);
            String info = String.format("D%d: (%d,%d) %c [%s]", 
                i, drone.getX(), drone.getY(), 
                drone.getOrientation(), drone.getRemainingCommands());
            g2d.drawString(info, legendX, legendY + 20 + i * 15);
        }
    }
    
    private void startAnimation() {
        if (!isRunning) {
            isRunning = true;
            animationTimer.start();
            statusLabel.setText("🟡 Simulation en cours...");
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
        }
    }
    
    private void pauseAnimation() {
        if (isRunning) {
            isRunning = false;
            animationTimer.stop();
            statusLabel.setText("🟠 En pause");
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
        }
    }
    
    private void resetSimulation() {
        pauseAnimation();
        setupDemoScenario();
        statusLabel.setText("🟢 Réinitialisé");
        repaint();
    }
    
    private void updateSimulation() {
        if (!isRunning) return;
        
        boolean allFinished = true;
        for (Drone drone : drones) {
            if (drone.hasRemainingCommands()) {
                drone.executeNextCommand(map);
                allFinished = false;
            }
        }
        
        if (allFinished) {
            pauseAnimation();
            statusLabel.setText("✅ Simulation terminée!");
        }
    }
    
    /**
     * Point d'entrée pour la visualisation
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DroneVisualizer();
        });
    }
}

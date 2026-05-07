package com.drone.simulator.visual;

import com.drone.simulator.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Visualisation interactive et explicative pour la soutenance
 */
public class InteractiveDroneVisualizer extends JFrame {
    
    private SimulatorMap map;
    private List<Drone> drones;
    private List<DroneTrail> trails;
    private JPanel mapPanel;
    private JPanel infoPanel;
    private JPanel controlPanel;
    private JButton startButton, pauseButton, resetButton, stepButton;
    private JButton addObstacleButton, clearObstaclesButton;
    private JButton speedUpButton, speedDownButton;
    private JLabel statusLabel, speedLabel, stepLabel;
    private Timer animationTimer;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private int cellSize = 40;
    private int animationSpeed = 1000; // ms par étape
    private int currentStep = 0;
    private int totalSteps = 0;
    private Color[] droneColors = {Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA};
    private String[] droneNames = {"Alpha", "Beta", "Gamma", "Delta"};
    
    public InteractiveDroneVisualizer() {
        super("🚁 Simulateur de Flotte de Drones - Mode Interactif");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        trails = new ArrayList<>();
        initializeComponents();
        setupInteractiveScenario();
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Panneau de contrôle supérieur
        controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("🎮 Contrôles de Simulation"));
        
        startButton = new JButton("▶️ Démarrer");
        pauseButton = new JButton("⏸️ Pause");
        resetButton = new JButton("🔄 Réinitialiser");
        stepButton = new JButton("⏭️ Étape par étape");
        
        startButton.addActionListener(e -> startSimulation());
        pauseButton.addActionListener(e -> pauseSimulation());
        resetButton.addActionListener(e -> resetSimulation());
        stepButton.addActionListener(e -> stepByStep());
        
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(resetButton);
        controlPanel.add(stepButton);
        
        // Panneau d'outils
        JPanel toolsPanel = new JPanel(new FlowLayout());
        toolsPanel.setBorder(BorderFactory.createTitledBorder("🛠️ Outils Interactifs"));
        
        addObstacleButton = new JButton("🧱 Ajouter Obstacle");
        clearObstaclesButton = new JButton("🧹 Nettoyer Obstacles");
        speedUpButton = new JButton("⚡ Accélérer");
        speedDownButton = new JButton("🐌 Ralentir");
        
        addObstacleButton.addActionListener(e -> enableObstacleMode());
        clearObstaclesButton.addActionListener(e -> clearAllObstacles());
        speedUpButton.addActionListener(e -> increaseSpeed());
        speedDownButton.addActionListener(e -> decreaseSpeed());
        
        toolsPanel.add(addObstacleButton);
        toolsPanel.add(clearObstaclesButton);
        toolsPanel.add(speedUpButton);
        toolsPanel.add(speedDownButton);
        
        // Panneau d'information
        infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setBorder(BorderFactory.createTitledBorder("📊 Informations en Temps Réel"));
        
        statusLabel = new JLabel("🟢 Prêt - Cliquez sur Démarrer");
        speedLabel = new JLabel("⚡ Vitesse: 1.0x");
        stepLabel = new JLabel("📍 Étape: 0/0");
        
        infoPanel.add(statusLabel);
        infoPanel.add(speedLabel);
        infoPanel.add(stepLabel);
        infoPanel.add(new JLabel("🖱️ Cliquez sur la carte pour interagir"));
        
        // Panneau de droite pour les informations détaillées
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(infoPanel, BorderLayout.NORTH);
        rightPanel.add(toolsPanel, BorderLayout.CENTER);
        
        // Panneau de carte avec écouteur de clics
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawEnhancedMap(g);
            }
        };
        
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMapClick(e);
            }
        });
        
        // Assemblage
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(infoPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(mapPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        
        // Timer pour animation
        animationTimer = new Timer(animationSpeed, e -> {
            if (!isPaused) {
                executeNextStep();
            }
        });
    }
    
    private void setupInteractiveScenario() {
        // Scénario pédagogique pour démonstration
        map = new SimulatorMap(12, 8);
        map.addObstacles(0.15); // 15% d'obstacles
        
        drones = List.of(
            new Drone(1, 1, 'E', "MMMRMM"), // Drone Alpha - va vers la droite
            new Drone(8, 3, 'W', "MMMLMM"), // Drone Beta - va vers la gauche
            new Drone(4, 6, 'N', "MMRMMM")  // Drone Gamma - va vers le haut
        );
        
        // Calculer le nombre total d'étapes
        totalSteps = drones.stream()
            .mapToInt(d -> d.getCommands().length())
            .max()
            .orElse(0);
        
        // Nettoyer les positions initiales
        for (Drone drone : drones) {
            map.removeObstacle(drone.getX(), drone.getY());
        }
        
        // Initialiser les trails
        trails.clear();
        for (int i = 0; i < drones.size(); i++) {
            trails.add(new DroneTrail(droneColors[i]));
        }
        
        updatePanelSize();
        updateStepLabel();
    }
    
    private void updatePanelSize() {
        int width = map.getWidth() * cellSize;
        int height = map.getHeight() * cellSize;
        mapPanel.setPreferredSize(new Dimension(width, height));
        pack();
    }
    
    private void drawEnhancedMap(Graphics g) {
        if (map == null || drones == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Fond dégradé
        GradientPaint background = new GradientPaint(0, 0, new Color(240, 248, 255), 
                                               0, map.getHeight() * cellSize, new Color(200, 230, 255));
        g2d.setPaint(background);
        g2d.fillRect(0, 0, map.getWidth() * cellSize, map.getHeight() * cellSize);
        
        // Dessiner les trails (trajectoires)
        for (int i = 0; i < trails.size(); i++) {
            trails.get(i).drawTrail(g2d, cellSize);
        }
        
        // Dessiner la grille et les obstacles
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                int px = x * cellSize;
                int py = y * cellSize;
                
                // Grille subtile
                g2d.setColor(new Color(220, 220, 220));
                g2d.drawRect(px, py, cellSize, cellSize);
                
                // Obstacles avec effet 3D
                if (map.isObstacle(x, y)) {
                    drawObstacle3D(g2d, px, py);
                }
            }
        }
        
        // Dessiner les zones spéciales
        drawSpecialZones(g2d);
        
        // Dessiner les drones avec effets
        for (int i = 0; i < drones.size(); i++) {
            drawEnhancedDrone(g2d, drones.get(i), i);
        }
        
        // Légende avancée
        drawAdvancedLegend(g2d);
        
        // Instructions interactives
        drawInteractiveInstructions(g2d);
    }
    
    private void drawObstacle3D(Graphics2D g2d, int x, int y) {
        // Effet 3D pour les obstacles
        g2d.setColor(new Color(150, 50, 50));
        g2d.fillRect(x + 3, y + 3, cellSize - 6, cellSize - 6);
        
        g2d.setColor(new Color(200, 50, 50));
        g2d.fillRect(x + 2, y + 2, cellSize - 4, cellSize - 4);
        
        g2d.setColor(new Color(220, 100, 100));
        g2d.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
    }
    
    private void drawEnhancedDrone(Graphics2D g2d, Drone drone, int index) {
        int px = drone.getX() * cellSize + cellSize / 2;
        int py = drone.getY() * cellSize + cellSize / 2;
        Color droneColor = droneColors[index % droneColors.length];
        
        // Ombre du drone
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillOval(px - 10, py - 8, 20, 16);
        
        // Cercle principal avec dégradé
        GradientPaint droneGradient = new GradientPaint(px - 8, py - 8, droneColor.brighter(),
                                                   px + 8, py + 8, droneColor.darker());
        g2d.setPaint(droneGradient);
        g2d.fillOval(px - 8, py - 8, 16, 16);
        
        // Bordure du drone
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(px - 8, py - 8, 16, 16);
        
        // Flèche de direction animée
        drawDirectionArrow(g2d, px, py, drone.getOrientation(), droneColor);
        
        // Numéro et nom du drone
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString(String.valueOf(index), px - 3, py + 3);
        
        // Ajouter à la trail
        trails.get(index).addPosition(drone.getX(), drone.getY());
    }
    
    private void drawDirectionArrow(Graphics2D g2d, int x, int y, char orientation, Color color) {
        int[][] arrows = {
            {0, -12, 0, -8, -4, -8, 0, -12}, // N (↑)
            {0, 12, 0, 8, 4, 8, 0, 12},   // S (↓)
            {12, 0, 8, 0, 8, -4, 12, 0},   // E (→)
            {-12, 0, -8, 0, -8, 4, -12, 0}   // W (←)
        };
        
        int dirIndex = "NSEW".indexOf(orientation);
        if (dirIndex >= 0) {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(3));
            int[] arrow = arrows[dirIndex];
            g2d.drawLine(x + arrow[0], y + arrow[1], x + arrow[2], y + arrow[3]);
            g2d.drawLine(x + arrow[2], y + arrow[3], x + arrow[4], y + arrow[5]);
            g2d.drawLine(x + arrow[4], y + arrow[5], x + arrow[6], y + arrow[7]);
        }
    }
    
    private void drawSpecialZones(Graphics2D g2d) {
        // Zone de départ
        g2d.setColor(new Color(100, 255, 100, 50));
        g2d.fillRect(cellSize, cellSize, cellSize, cellSize);
        
        // Zone d'arrivée (si applicable)
        g2d.setColor(new Color(255, 100, 100, 50));
        g2d.fillRect((map.getWidth() - 2) * cellSize, (map.getHeight() - 2) * cellSize, cellSize, cellSize);
    }
    
    private void drawAdvancedLegend(Graphics2D g2d) {
        int legendX = map.getWidth() * cellSize + 20;
        int legendY = 20;
        
        // Titre
        g2d.setColor(new Color(50, 50, 150));
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("📋 LÉGENDE COMPLÈTE", legendX, legendY);
        
        legendY += 30;
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        
        // Symboles
        g2d.drawString("🧱 Obstacles (clic pour ajouter)", legendX, legendY);
        legendY += 20;
        
        for (int i = 0; i < drones.size(); i++) {
            g2d.setColor(droneColors[i]);
            g2d.fillOval(legendX, legendY - 8, 12, 12);
            g2d.setColor(Color.BLACK);
            g2d.drawString(" Drone " + droneNames[i] + " (" + i + ")", legendX + 20, legendY);
            legendY += 25;
        }
        
        // Informations drones
        legendY += 20;
        g2d.setColor(new Color(50, 50, 150));
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("📊 ÉTAT DES DRONES", legendX, legendY);
        
        legendY += 25;
        g2d.setFont(new Font("Courier", Font.PLAIN, 10));
        for (int i = 0; i < drones.size(); i++) {
            Drone drone = drones.get(i);
            String info = String.format("%s: (%d,%d) %c [%s restantes]", 
                droneNames[i], drone.getX(), drone.getY(), 
                drone.getOrientation(), drone.getRemainingCommands());
            g2d.drawString(info, legendX, legendY);
            legendY += 18;
        }
        
        // Statistiques
        legendY += 20;
        g2d.setColor(new Color(150, 50, 150));
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("📈 STATISTIQUES", legendX, legendY);
        
        legendY += 25;
        g2d.setFont(new Font("Courier", Font.PLAIN, 10));
        g2d.drawString("Étape: " + currentStep + "/" + totalSteps, legendX, legendY);
        legendY += 18;
        g2d.drawString("Vitesse: " + (1000/animationSpeed) + "x", legendX, legendY);
        legendY += 18;
        g2d.drawString("Obstacles: " + countObstacles(), legendX, legendY);
    }
    
    private void drawInteractiveInstructions(Graphics2D g2d) {
        int y = map.getHeight() * cellSize - 60;
        
        g2d.setColor(new Color(255, 255, 200, 200));
        g2d.fillRect(10, y, 300, 50);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.drawString("💡 CONSEILS INTERACTIFS:", 20, y + 15);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("• Clic gauche: Ajouter obstacle", 20, y + 30);
        g2d.drawString("• Clic droit: Supprimer obstacle", 20, y + 42);
    }
    
    private int countObstacles() {
        int count = 0;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                if (map.isObstacle(x, y)) count++;
            }
        }
        return count;
    }
    
    private void handleMapClick(MouseEvent e) {
        int x = e.getX() / cellSize;
        int y = e.getY() / cellSize;
        
        if (x >= 0 && x < map.getWidth() && y >= 0 && y < map.getHeight()) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                // Ajouter obstacle
                if (!isDronePosition(x, y)) {
                    map.addObstacle(x, y);
                    showInfoMessage("🧱 Obstacle ajouté en (" + x + "," + y + ")");
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
                // Supprimer obstacle
                if (map.isObstacle(x, y)) {
                    map.removeObstacle(x, y);
                    showInfoMessage("🧹 Obstacle supprimé en (" + x + "," + y + ")");
                }
            }
            repaint();
        }
    }
    
    private boolean isDronePosition(int x, int y) {
        for (Drone drone : drones) {
            if (drone.getX() == x && drone.getY() == y) {
                return true;
            }
        }
        return false;
    }
    
    private void startSimulation() {
        if (!isRunning) {
            isRunning = true;
            isPaused = false;
            animationTimer.start();
            statusLabel.setText("🟡 Simulation en cours...");
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            showInfoMessage("▶️ Simulation démarrée - Les drones bougent !");
        }
    }
    
    private void pauseSimulation() {
        if (isRunning) {
            isPaused = !isPaused;
            if (isPaused) {
                statusLabel.setText("🟠 En pause");
                pauseButton.setText("▶️ Reprendre");
                showInfoMessage("⏸️ Simulation en pause - Cliquez pour continuer");
            } else {
                statusLabel.setText("🟡 Simulation en cours...");
                pauseButton.setText("⏸️ Pause");
                showInfoMessage("▶️ Simulation reprise");
            }
        }
    }
    
    private void resetSimulation() {
        isRunning = false;
        isPaused = false;
        animationTimer.stop();
        currentStep = 0;
        setupInteractiveScenario();
        statusLabel.setText("🟢 Réinitialisé - Prêt à démarrer");
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        pauseButton.setText("⏸️ Pause");
        showInfoMessage("🔄 Simulation réinitialisée");
    }
    
    private void stepByStep() {
        if (!isRunning) {
            isRunning = true;
            isPaused = true;
            executeNextStep();
            showInfoMessage("⏭️ Mode étape par étape - Cliquez encore pour avancer");
        }
    }
    
    private void executeNextStep() {
        boolean allFinished = true;
        for (Drone drone : drones) {
            if (drone.hasRemainingCommands()) {
                drone.executeNextCommand(map);
                allFinished = false;
            }
        }
        
        currentStep++;
        updateStepLabel();
        
        if (allFinished) {
            isRunning = false;
            animationTimer.stop();
            statusLabel.setText("✅ Simulation terminée !");
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            showInfoMessage("🎉 Tous les drones ont terminé leurs commandes !");
        }
        
        repaint();
    }
    
    private void enableObstacleMode() {
        showInfoMessage("🧱 Mode obstacle activé - Cliquez sur la carte");
    }
    
    private void clearAllObstacles() {
        map.clearObstacles();
        map.addObstacles(0.15);
        repaint();
        showInfoMessage("🧹 Tous les obstacles ont été supprimés");
    }
    
    private void increaseSpeed() {
        animationSpeed = Math.max(100, animationSpeed / 2);
        animationTimer.setDelay(animationSpeed);
        speedLabel.setText("⚡ Vitesse: " + (1000/animationSpeed) + "x");
        showInfoMessage("⚡ Vitesse augmentée à " + (1000/animationSpeed) + "x");
    }
    
    private void decreaseSpeed() {
        animationSpeed = Math.min(2000, animationSpeed * 2);
        animationTimer.setDelay(animationSpeed);
        speedLabel.setText("⚡ Vitesse: " + (1000/animationSpeed) + "x");
        showInfoMessage("🐌 Vitesse réduite à " + (1000/animationSpeed) + "x");
    }
    
    private void updateStepLabel() {
        stepLabel.setText("📍 Étape: " + currentStep + "/" + totalSteps);
    }
    
    private void showInfoMessage(String message) {
        statusLabel.setText(message);
        Timer messageTimer = new Timer(3000, e -> {
            if (isRunning && !isPaused) {
                statusLabel.setText("🟡 Simulation en cours...");
            } else if (!isRunning) {
                statusLabel.setText("🟢 Prêt - Cliquez sur Démarrer");
            } else if (isPaused) {
                statusLabel.setText("🟠 En pause");
            }
        });
        messageTimer.setRepeats(false);
        messageTimer.start();
    }
    
    /**
     * Point d'entrée pour la visualisation interactive
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InteractiveDroneVisualizer();
        });
    }
    
    /**
     * Classe interne pour gérer les trails des drones
     */
    private static class DroneTrail {
        private List<int[]> positions;
        private Color color;
        
        public DroneTrail(Color color) {
            this.color = color;
            this.positions = new ArrayList<>();
        }
        
        public void addPosition(int x, int y) {
            positions.add(new int[]{x, y});
            // Garder seulement les 20 dernières positions
            if (positions.size() > 20) {
                positions.remove(0);
            }
        }
        
        public void drawTrail(Graphics2D g2d, int cellSize) {
            if (positions.size() < 2) return;
            
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
            g2d.setStroke(new BasicStroke(2));
            
            for (int i = 1; i < positions.size(); i++) {
                int[] prev = positions.get(i - 1);
                int[] curr = positions.get(i);
                
                int x1 = prev[0] * cellSize + cellSize / 2;
                int y1 = prev[1] * cellSize + cellSize / 2;
                int x2 = curr[0] * cellSize + cellSize / 2;
                int y2 = curr[1] * cellSize + cellSize / 2;
                
                // Opacité progressive
                float alpha = (float) i / positions.size();
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 150)));
                g2d.drawLine(x1, y1, x2, y2);
            }
        }
    }
}

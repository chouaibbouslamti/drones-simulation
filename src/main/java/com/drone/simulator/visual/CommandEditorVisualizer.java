package com.drone.simulator.visual;

import com.drone.simulator.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Visualisation avec éditeur de commandes interactif
 * Permet de définir les commandes des drones et de voir le résultat en temps réel
 */
public class CommandEditorVisualizer extends JFrame {
    
    private SimulatorMap map;
    private List<Drone> drones;
    private List<JTextField> commandFields;
    private List<JSpinner> xSpinners, ySpinners;
    private List<JComboBox<Character>> orientationBoxes;
    private JPanel mapPanel;
    private JPanel editorPanel;
    private JButton addDroneButton, removeDroneButton;
    private JButton startButton, pauseButton, resetButton;
    private JButton generateRandomButton, clearAllButton;
    private JLabel statusLabel, infoLabel;
    private Timer animationTimer;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private int cellSize = 35;
    private int animationSpeed = 800;
    private Color[] droneColors = {Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN};
    
    public CommandEditorVisualizer() {
        super("🚁 Éditeur de Commandes de Drones - Mode Interactif");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        commandFields = new ArrayList<>();
        xSpinners = new ArrayList<>();
        ySpinners = new ArrayList<>();
        orientationBoxes = new ArrayList<>();
        
        initializeComponents();
        setupDefaultScenario();
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Panneau d'édition des drones
        editorPanel = new JPanel();
        editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));
        editorPanel.setBorder(BorderFactory.createTitledBorder("✏️ Éditeur de Commandes"));
        
        // Panneau de contrôle
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        addDroneButton = new JButton("➕ Ajouter Drone");
        removeDroneButton = new JButton("➖ Supprimer Drone");
        generateRandomButton = new JButton("🎲 Aléatoire");
        clearAllButton = new JButton("🗑️ Tout Vider");
        
        addDroneButton.addActionListener(e -> addDroneEditor());
        removeDroneButton.addActionListener(e -> removeDroneEditor());
        generateRandomButton.addActionListener(e -> generateRandomScenario());
        clearAllButton.addActionListener(e -> clearAllDrones());
        
        controlPanel.add(addDroneButton);
        controlPanel.add(removeDroneButton);
        controlPanel.add(generateRandomButton);
        controlPanel.add(clearAllButton);
        
        // Panneau de simulation
        JPanel simulationPanel = new JPanel(new FlowLayout());
        
        startButton = new JButton("▶️ Démarrer");
        pauseButton = new JButton("⏸️ Pause");
        resetButton = new JButton("🔄 Réinitialiser");
        
        startButton.addActionListener(e -> startSimulation());
        pauseButton.addActionListener(e -> pauseSimulation());
        resetButton.addActionListener(e -> resetSimulation());
        
        simulationPanel.add(startButton);
        simulationPanel.add(pauseButton);
        simulationPanel.add(resetButton);
        
        // Panneau d'information
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setBorder(BorderFactory.createTitledBorder("📊 Informations"));
        
        statusLabel = new JLabel("🟢 Prêt - Configurez vos drones");
        infoLabel = new JLabel("💡 Cliquez sur 'Ajouter Drone' pour commencer");
        
        infoPanel.add(statusLabel);
        infoPanel.add(infoLabel);
        
        // Panneau de gauche (éditeur)
        JPanel leftPanel = new JPanel(new BorderLayout());
        JScrollPane editorScrollPane = new JScrollPane(editorPanel);
        editorScrollPane.setPreferredSize(new Dimension(400, 500));
        leftPanel.add(editorScrollPane, BorderLayout.CENTER);
        leftPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Panneau de droite (carte + contrôles)
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        // Panneau de carte
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawInteractiveMap(g);
            }
        };
        
        rightPanel.add(mapPanel, BorderLayout.CENTER);
        rightPanel.add(simulationPanel, BorderLayout.NORTH);
        rightPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // Assemblage principal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Timer pour animation
        animationTimer = new Timer(animationSpeed, e -> {
            if (!isPaused) {
                executeNextStep();
            }
        });
    }
    
    private void setupDefaultScenario() {
        map = new SimulatorMap(15, 10);
        map.addObstacles(0.1); // 10% d'obstacles
        
        drones = new ArrayList<>();
        // Ajouter 2 drones par défaut
        addDroneEditor();
        addDroneEditor();
        
        updateMap();
    }
    
    private void addDroneEditor() {
        if (drones.size() >= 5) {
            showInfoMessage("⚠️ Maximum 5 drones atteints !");
            return;
        }
        
        JPanel dronePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Numéro du drone
        int droneIndex = drones.size();
        JLabel droneLabel = new JLabel("🚁 Drone " + droneIndex + ":");
        droneLabel.setFont(new Font("Arial", Font.BOLD, 12));
        droneLabel.setForeground(droneColors[droneIndex % droneColors.length]);
        
        // Position X
        JLabel xLabel = new JLabel("X:");
        JSpinner xSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 14, 1));
        xSpinner.setPreferredSize(new Dimension(60, 25));
        
        // Position Y
        JLabel yLabel = new JLabel("Y:");
        JSpinner ySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9, 1));
        ySpinner.setPreferredSize(new Dimension(60, 25));
        
        // Orientation
        JLabel orientationLabel = new JLabel("Orientation:");
        Character[] orientations = {'N', 'S', 'E', 'O'};
        JComboBox<Character> orientationBox = new JComboBox<>(orientations);
        orientationBox.setRenderer(new OrientationRenderer());
        orientationBox.setPreferredSize(new Dimension(80, 25));
        
        // Commandes
        JLabel commandsLabel = new JLabel("Commandes:");
        JTextField commandsField = new JTextField("MMRMM", 15);
        commandsField.setToolTipText("L: Gauche | R: Droite | M: Avancer | B: Reculer");
        
        // Bouton de suppression
        JButton removeButton = new JButton("❌");
        removeButton.addActionListener(e -> removeSpecificDrone(droneIndex));
        removeButton.setPreferredSize(new Dimension(30, 25));
        
        // Assemblage du panneau
        gbc.gridx = 0; gbc.gridy = 0;
        dronePanel.add(droneLabel, gbc);
        
        gbc.gridx = 1;
        dronePanel.add(xLabel, gbc);
        gbc.gridx = 2;
        dronePanel.add(xSpinner, gbc);
        
        gbc.gridx = 3;
        dronePanel.add(yLabel, gbc);
        gbc.gridx = 4;
        dronePanel.add(ySpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dronePanel.add(orientationLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        dronePanel.add(orientationBox, gbc);
        
        gbc.gridx = 3; gbc.gridwidth = 1;
        dronePanel.add(removeButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 5;
        dronePanel.add(commandsLabel, gbc);
        
        gbc.gridy = 3;
        dronePanel.add(commandsField, gbc);
        
        // Séparateur
        dronePanel.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        
        // Ajouter à l'éditeur
        editorPanel.add(dronePanel);
        
        // Stocker les références
        xSpinners.add(xSpinner);
        ySpinners.add(ySpinner);
        orientationBoxes.add(orientationBox);
        commandFields.add(commandsField);
        
        // Créer le drone correspondant
        Drone drone = new Drone(
            (Integer) xSpinner.getValue(),
            (Integer) ySpinner.getValue(),
            (Character) orientationBox.getSelectedItem(),
            commandsField.getText()
        );
        drones.add(drone);
        
        // Mettre à jour la carte pour afficher tous les drones
        updateMap();
        
        showInfoMessage("✅ Drone " + droneIndex + " ajouté - Configurez ses commandes");
    }
    
    private void removeDroneEditor() {
        if (drones.size() > 0) {
            int lastIndex = drones.size() - 1;
            drones.remove(lastIndex);
            
            // Supprimer les composants de l'éditeur
            Component[] components = editorPanel.getComponents();
            for (int i = components.length - 1; i >= 0; i--) {
                Component comp = components[i];
                if (comp instanceof JPanel) {
                    editorPanel.remove(comp);
                    break;
                }
            }
            
            // Nettoyer les listes
            if (!xSpinners.isEmpty()) xSpinners.remove(xSpinners.size() - 1);
            if (!ySpinners.isEmpty()) ySpinners.remove(ySpinners.size() - 1);
            if (!orientationBoxes.isEmpty()) orientationBoxes.remove(orientationBoxes.size() - 1);
            if (!commandFields.isEmpty()) commandFields.remove(commandFields.size() - 1);
            
            updateMap();
            showInfoMessage("➖ Drone supprimé");
        }
    }
    
    private void removeSpecificDrone(int index) {
        if (index < drones.size()) {
            drones.remove(index);
            rebuildEditorPanel();
            updateMap();
            showInfoMessage("➖ Drone " + index + " supprimé");
        }
    }
    
    private void rebuildEditorPanel() {
        editorPanel.removeAll();
        xSpinners.clear();
        ySpinners.clear();
        orientationBoxes.clear();
        commandFields.clear();
        
        List<Drone> tempDrones = new ArrayList<>(drones);
        drones.clear();
        
        for (Drone drone : tempDrones) {
            addDroneEditor();
        }
    }
    
    private void generateRandomScenario() {
        clearAllDrones();
        
        int numDrones = 2 + (int)(Math.random() * 3); // 2-4 drones
        for (int i = 0; i < numDrones; i++) {
            addDroneEditor();
            
            // Générer des valeurs aléatoires
            int x = (int)(Math.random() * map.getWidth());
            int y = (int)(Math.random() * map.getHeight());
            char[] orientations = {'N', 'S', 'E', 'O'};
            char orientation = orientations[(int)(Math.random() * 4)];
            
            // Générer des commandes aléatoires
            StringBuilder commands = new StringBuilder();
            int commandLength = 3 + (int)(Math.random() * 5); // 3-7 commandes
            for (int j = 0; j < commandLength; j++) {
                char[] possibleCommands = {'L', 'R', 'M', 'B'};
                commands.append(possibleCommands[(int)(Math.random() * 4)]);
            }
            
            // Mettre à jour les champs
            if (i < xSpinners.size()) {
                xSpinners.get(i).setValue(x);
                ySpinners.get(i).setValue(y);
                orientationBoxes.get(i).setSelectedItem(orientation);
                commandFields.get(i).setText(commands.toString());
                
                // Mettre à jour le drone
                Drone drone = drones.get(i);
                drone.setX(x);
                drone.setY(y);
                drone.setOrientation(orientation);
                drone.setCommands(commands.toString());
            }
        }
        
        updateMap();
        showInfoMessage("🎲 Scénario aléatoire généré avec " + numDrones + " drones");
    }
    
    private void clearAllDrones() {
        drones.clear();
        editorPanel.removeAll();
        xSpinners.clear();
        ySpinners.clear();
        orientationBoxes.clear();
        commandFields.clear();
        updateMap();
        showInfoMessage("🗑️ Tous les drones supprimés");
    }
    
    private void updateMap() {
        // Nettoyer les positions initiales des drones
        map.clearObstacles();
        map.addObstacles(0.1);
        
        for (Drone drone : drones) {
            map.removeObstacle(drone.getX(), drone.getY());
        }
        
        updatePanelSize();
        repaint();
    }
    
    private void updatePanelSize() {
        int width = map.getWidth() * cellSize;
        int height = map.getHeight() * cellSize;
        mapPanel.setPreferredSize(new Dimension(width, height));
    }
    
    private void drawInteractiveMap(Graphics g) {
        if (map == null || drones == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fond
        g2d.setColor(new Color(245, 250, 255));
        g2d.fillRect(0, 0, map.getWidth() * cellSize, map.getHeight() * cellSize);
        
        // Grille
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                int px = x * cellSize;
                int py = y * cellSize;
                
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawRect(px, py, cellSize, cellSize);
                
                // Obstacles
                if (map.isObstacle(x, y)) {
                    drawObstacle(g2d, px, py);
                }
            }
        }
        
        // Drones
        for (int i = 0; i < drones.size(); i++) {
            drawDrone(g2d, drones.get(i), i);
        }
        
        // Légende
        drawLegend(g2d);
    }
    
    private void drawObstacle(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(220, 100, 100));
        g2d.fillRect(x + 2, y + 2, cellSize - 4, cellSize - 4);
        
        g2d.setColor(new Color(180, 50, 50));
        g2d.fillRect(x + 4, y + 4, cellSize - 8, cellSize - 8);
    }
    
    private void drawDrone(Graphics2D g2d, Drone drone, int index) {
        int px = drone.getX() * cellSize + cellSize / 2;
        int py = drone.getY() * cellSize + cellSize / 2;
        Color droneColor = droneColors[index % droneColors.length];
        
        // Cercle du drone
        g2d.setColor(droneColor);
        g2d.fillOval(px - 10, py - 10, 20, 20);
        
        // Bordure
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(px - 10, py - 10, 20, 20);
        
        // Flèche de direction
        drawArrow(g2d, px, py, drone.getOrientation(), droneColor);
        
        // Numéro
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(String.valueOf(index), px - 4, py + 4);
    }
    
    private void drawArrow(Graphics2D g2d, int x, int y, char orientation, Color color) {
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
    
    private void drawLegend(Graphics2D g2d) {
        int legendX = map.getWidth() * cellSize + 20;
        int legendY = 20;
        
        g2d.setColor(new Color(50, 50, 150));
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("📋 LÉGENDE", legendX, legendY);
        
        legendY += 30;
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        
        g2d.drawString("🧱 Obstacles", legendX, legendY);
        legendY += 20;
        
        for (int i = 0; i < drones.size(); i++) {
            g2d.setColor(droneColors[i % droneColors.length]);
            g2d.fillOval(legendX, legendY - 8, 12, 12);
            g2d.setColor(Color.BLACK);
            g2d.drawString(" Drone " + i, legendX + 20, legendY);
            legendY += 25;
        }
        
        // Commandes actuelles
        legendY += 20;
        g2d.setColor(new Color(50, 50, 150));
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("📝 COMMANDES ACTUELLES", legendX, legendY);
        
        legendY += 25;
        g2d.setFont(new Font("Courier", Font.PLAIN, 10));
        for (int i = 0; i < drones.size(); i++) {
            if (i < commandFields.size()) {
                Drone drone = drones.get(i);
                String info = String.format("D%d: (%d,%d) %c [%s]", 
                    i, drone.getX(), drone.getY(), 
                    drone.getOrientation(), commandFields.get(i).getText());
                g2d.drawString(info, legendX, legendY);
                legendY += 18;
            }
        }
    }
    
    private void startSimulation() {
        if (!isRunning && !drones.isEmpty()) {
            // Mettre à jour les drones avec les valeurs des champs
            for (int i = 0; i < drones.size(); i++) {
                Drone drone = drones.get(i);
                drone.setX((Integer) xSpinners.get(i).getValue());
                drone.setY((Integer) ySpinners.get(i).getValue());
                drone.setOrientation((Character) orientationBoxes.get(i).getSelectedItem());
                drone.setCommands(commandFields.get(i).getText());
                drone.resetCommandIndex();
            }
            
            updateMap();
            
            isRunning = true;
            isPaused = false;
            animationTimer.start();
            statusLabel.setText("🟡 Simulation en cours...");
            startButton.setEnabled(false);
            showInfoMessage("▶️ Simulation démarrée - Les drones exécutent vos commandes !");
        }
    }
    
    private void pauseSimulation() {
        if (isRunning) {
            isPaused = !isPaused;
            if (isPaused) {
                statusLabel.setText("🟠 En pause");
                pauseButton.setText("▶️ Reprendre");
                showInfoMessage("⏸️ Simulation en pause - Modifiez les commandes si besoin");
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
        updateMap();
        statusLabel.setText("🟢 Prêt - Configurez vos drones");
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        pauseButton.setText("⏸️ Pause");
        showInfoMessage("🔄 Simulation réinitialisée - Modifiez les commandes");
    }
    
    private void executeNextStep() {
        boolean allFinished = true;
        for (Drone drone : drones) {
            if (drone.hasRemainingCommands()) {
                drone.executeNextCommand(map);
                allFinished = false;
            }
        }
        
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
    
    private void showInfoMessage(String message) {
        infoLabel.setText(message);
        Timer messageTimer = new Timer(3000, e -> {
            if (isRunning && !isPaused) {
                infoLabel.setText("💡 Simulation en cours - Modifiez les commandes dans l'éditeur");
            } else if (!isRunning) {
                infoLabel.setText("💡 Configurez vos drones et cliquez Démarrer");
            } else if (isPaused) {
                infoLabel.setText("💡 Simulation en pause - Modifiez les commandes");
            }
        });
        messageTimer.setRepeats(false);
        messageTimer.start();
    }
    
    /**
     * Renderer personnalisé pour les orientations
     */
    private static class OrientationRenderer extends JLabel implements ListCellRenderer<Character> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Character> list, Character value, int index, 
                                                  boolean isSelected, boolean cellHasFocus) {
            
            if (value != null) {
                String[] symbols = {"N↑", "S↓", "E→", "O←"};
                int symbolIndex = "NSEO".indexOf(value);
                if (symbolIndex >= 0) {
                    setText(symbols[symbolIndex]);
                }
            }
            
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            return this;
        }
    }
    
    /**
     * Point d'entrée principal
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CommandEditorVisualizer();
        });
    }
}

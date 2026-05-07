package com.drone.simulator;

import java.util.Random;

/**
 * Classe représentant la carte de simulation avec les obstacles
 */
public class SimulatorMap {
    private char[][] grid;
    private int width;
    private int height;
    private static final char OBSTACLE = '#';
    private static final char EMPTY = '.';
    
    public SimulatorMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new char[height][width];
        initializeGrid();
    }
    
    /**
     * Initialise la grille avec des cases vides
     */
    private void initializeGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = EMPTY;
            }
        }
    }
    
    /**
     * Génère aléatoirement des obstacles sur la carte
     * @param obstacleDensity Densité d'obstacles (0.0 à 1.0)
     */
    public void addObstacles(double obstacleDensity) {
        Random random = new Random();
        int numObstacles = (int) (width * height * obstacleDensity);
        
        for (int i = 0; i < numObstacles; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            grid[y][x] = OBSTACLE;
        }
    }
    
    /**
     * Vérifie si une position est bloquée par un obstacle
     * @param x Coordonnée x
     * @param y Coordonnée y
     * @return true si bloqué, false sinon
     */
    public boolean isObstacle(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        return grid[y][x] == OBSTACLE;
    }
    
    /**
     * Vérifie si une position est bloquée (pour la visualisation)
     */
    public boolean isBlocked(int x, int y) {
        return isObstacle(x, y);
    }
    
    /**
     * Applique le wrapping pour les coordonnées qui sortent de la carte
     * @param coord Coordonnée à traiter
     * @param max Valeur maximale
     * @return Coordonnée avec wrapping appliqué
     */
    public int wrapCoordinate(int coord, int max) {
        return ((coord % max) + max) % max;
    }
    
    /**
     * Place un drone sur la carte
     * @param drone Le drone à placer
     */
    public void placeDrone(Drone drone) {
        // Ne place pas de marqueur pour le drone dans la grille
        // car les drones bougent et sont gérés séparément
    }
    
    /**
     * Affiche la carte avec les obstacles et les drones
     * @param drones Liste des drones à afficher
     */
    public void displayMap(java.util.List<Drone> drones) {
        // Créer une copie de la grille pour l'affichage
        char[][] displayGrid = new char[height][width];
        
        // Copier les obstacles
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                displayGrid[y][x] = grid[y][x];
            }
        }
        
        // Placer les drones sur la grille d'affichage
        for (Drone drone : drones) {
            int x = drone.getX();
            int y = drone.getY();
            if (x >= 0 && x < width && y >= 0 && y < height) {
                displayGrid[y][x] = drone.getDisplayChar();
            }
        }
        
        // Afficher la grille
        System.out.println("Carte de simulation (" + width + "x" + height + "):");
        System.out.print("+");
        for (int x = 0; x < width; x++) {
            System.out.print("-");
        }
        System.out.println("+");
        
        for (int y = 0; y < height; y++) {
            System.out.print("|");
            for (int x = 0; x < width; x++) {
                System.out.print(displayGrid[y][x]);
            }
            System.out.println("|");
        }
        
        System.out.print("+");
        for (int x = 0; x < width; x++) {
            System.out.print("-");
        }
        System.out.println("+");
    }
    
    /**
     * Affiche la carte sans drones (juste les obstacles)
     */
    public void displayMap() {
        System.out.println("Carte de simulation (" + width + "x" + height + "):");
        System.out.print("+");
        for (int x = 0; x < width; x++) {
            System.out.print("-");
        }
        System.out.println("+");
        
        for (int y = 0; y < height; y++) {
            System.out.print("|");
            for (int x = 0; x < width; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println("|");
        }
        
        System.out.print("+");
        for (int x = 0; x < width; x++) {
            System.out.print("-");
        }
        System.out.println("+");
    }
    
    /**
     * Enlève un obstacle à une position spécifique
     * @param x Coordonnée x
     * @param y Coordonnée y
     */
    public void removeObstacle(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y][x] = EMPTY;
        }
    }
    
    /**
     * Ajoute un obstacle à une position spécifique
     */
    public void addObstacle(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y][x] = OBSTACLE;
        }
    }
    
    /**
     * Supprime tous les obstacles
     */
    public void clearObstacles() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = EMPTY;
            }
        }
    }
    
    // Getters
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}

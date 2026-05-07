package com.drone.simulator;

/**
 * Classe métier représentant un drone avec sa position, orientation et commandes
 */
public class Drone {
    private int x, y;
    private char orientation;
    private String commands;
    private int currentIndex = 0;
    
    public Drone(int x, int y, char orientation, String commands) {
        this.x = x;
        this.y = y;
        this.orientation = orientation;
        this.commands = commands;
    }
    
    /**
     * Exécute une commande sur le drone
     * @param cmd La commande à exécuter (L, R, M, B)
     * @param map La carte pour vérifier les obstacles
     */
    public void executeCommand(char cmd, SimulatorMap map) {
        switch (cmd) {
            case 'L':
                turnLeft();
                break;
            case 'R':
                turnRight();
                break;
            case 'M':
                moveForward(map);
                break;
            case 'B':
                moveBackward(map);
                break;
        }
    }
    
    /**
     * Tourne le drone de 90° vers la gauche
     */
    public void turnLeft() {
        switch (orientation) {
            case 'N':
                orientation = 'O';
                break;
            case 'O':
                orientation = 'S';
                break;
            case 'S':
                orientation = 'E';
                break;
            case 'E':
                orientation = 'N';
                break;
        }
    }
    
    /**
     * Tourne le drone de 90° vers la droite
     */
    public void turnRight() {
        switch (orientation) {
            case 'N':
                orientation = 'E';
                break;
            case 'E':
                orientation = 'S';
                break;
            case 'S':
                orientation = 'O';
                break;
            case 'O':
                orientation = 'N';
                break;
        }
    }
    
    /**
     * Fait avancer le drone d'une case dans sa direction actuelle
     * @param map La carte pour vérifier les obstacles
     */
    public void moveForward(SimulatorMap map) {
        int newX = x, newY = y;
        
        switch (orientation) {
            case 'N':
                newY++;
                break;
            case 'S':
                newY--;
                break;
            case 'E':
                newX++;
                break;
            case 'O':
                newX--;
                break;
        }
        
        // Wrapping
        newX = map.wrapCoordinate(newX, map.getWidth());
        newY = map.wrapCoordinate(newY, map.getHeight());
        
        // Vérification des obstacles
        if (!map.isBlocked(newX, newY)) {
            x = newX;
            y = newY;
        }
    }
    
    /**
     * Fait reculer le drone d'une case dans la direction opposée
     * @param map La carte pour vérifier les obstacles
     */
    public void moveBackward(SimulatorMap map) {
        int newX = x, newY = y;
        
        switch (orientation) {
            case 'N':
                newY--;
                break;
            case 'S':
                newY++;
                break;
            case 'E':
                newX--;
                break;
            case 'O':
                newX++;
                break;
        }
        
        // Wrapping
        newX = map.wrapCoordinate(newX, map.getWidth());
        newY = map.wrapCoordinate(newY, map.getHeight());
        
        // Vérification des obstacles
        if (!map.isBlocked(newX, newY)) {
            x = newX;
            y = newY;
        }
    }
    
    /**
     * Retourne la position et l'orientation actuelle du drone
     * @return Chaîne formatée "x y orientation"
     */
    public String getPosition() {
        return x + " " + y + " " + orientation;
    }
    
    /**
     * Retourne le caractère représentant l'orientation pour l'affichage
     * @return Caractère d'affichage
     */
    public char getDisplayChar() {
        switch (orientation) {
            case 'N':
                return '^';
            case 'S':
                return 'v';
            case 'E':
                return '>';
            case 'O':
                return '<';
            default:
                return '?';
        }
    }
    
    // Getters et setters
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public char getOrientation() {
        return orientation;
    }
    
    public String getCommands() {
        return commands;
    }
    
    /**
     * Vérifie s'il reste des commandes à exécuter
     */
    public boolean hasRemainingCommands() {
        return currentIndex < commands.length();
    }
    
    /**
     * Exécute la prochaine commande
     */
    public void executeNextCommand(SimulatorMap map) {
        if (hasRemainingCommands()) {
            char command = commands.charAt(currentIndex++);
            executeCommand(command, map);
        }
    }
    
    /**
     * Retourne les commandes restantes
     */
    public String getRemainingCommands() {
        if (currentIndex >= commands.length()) {
            return "Aucune";
        }
        return commands.substring(currentIndex);
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setOrientation(char orientation) {
        this.orientation = orientation;
    }
    
    /**
     * Définit les commandes du drone
     */
    public void setCommands(String commands) {
        this.commands = commands;
        this.currentIndex = 0;
    }
    
    /**
     * Réinitialise l'index des commandes
     */
    public void resetCommandIndex() {
        this.currentIndex = 0;
    }
}

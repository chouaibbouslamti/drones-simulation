#!/bin/bash

echo "🚁 Simulateur de Flotte de Drones"
echo "================================"
echo ""
echo "1. Visualisation simple"
echo "2. Visualisation interactive"  
echo "3. Éditeur de commandes"
echo "4. Mode console (basique)"
echo "5. Mode console (concurrent)"
echo "6. Mode serveur"
echo "7. Mode client"
echo ""
read -p "Choisissez une option (1-7): " choice

case $choice in
    1)
        echo "🎨 Lancement visualisation simple..."
        java -cp classes com.drone.simulator.visual.DroneVisualizer
        ;;
    2)
        echo "🎮 Lancement visualisation interactive..."
        java -cp classes com.drone.simulator.visual.InteractiveDroneVisualizer
        ;;
    3)
        echo "✏️ Lancement éditeur de commandes..."
        java -cp classes com.drone.simulator.visual.CommandEditorVisualizer
        ;;
    4)
        echo "📋 Lancement mode console basique..."
        java -cp classes com.drone.simulator.DroneSimulatorMain basic drones_test.txt
        ;;
    5)
        echo "🧵 Lancement mode console concurrent..."
        java -cp classes com.drone.simulator.DroneSimulatorMain concurrent drones_test.txt
        ;;
    6)
        echo "🌐 Lancement mode serveur..."
        java -cp classes com.drone.simulator.DroneSimulatorMain server
        ;;
    7)
        echo "📡 Lancement mode client..."
        java -cp classes com.drone.simulator.DroneSimulatorMain client drones_test.txt
        ;;
    *)
        echo "❌ Option invalide"
        ;;
esac

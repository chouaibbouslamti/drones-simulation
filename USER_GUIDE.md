# Drone Simulator - Complete User Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Installation](#installation)
4. [Configuration File Format](#configuration-file-format)
5. [Running the Simulator](#running-the-simulator)
6. [Mode 1: Basic Sequential Mode](#mode-1-basic-sequential-mode)
7. [Mode 2: Concurrent Mode](#mode-2-concurrent-mode)
8. [Mode 3: Client-Server Mode](#mode-3-client-server-mode)
9. [Command Reference](#command-reference)
10. [Troubleshooting](#troubleshooting)
11. [Examples](#examples)

---

## Introduction

The Drone Simulator is a Java-based application that simulates the movement of a fleet of drones on a map with obstacles. The simulator provides three different execution modes:

- **Basic Mode**: Sequential execution where drones move one after another
- **Concurrent Mode**: Parallel execution using threads where drones move simultaneously
- **Client-Server Mode**: Network-based simulation where a client sends configuration to a remote server

This guide will walk you through each step of using the simulator, from installation to advanced usage.

---

## Prerequisites

Before using the Drone Simulator, ensure you have the following installed on your system:

### Required Software

1. **Java Development Kit (JDK) 11 or higher**
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Verify installation: `java -version`
   - Expected output: `java version "11.x.x"` or higher

2. **Apache Maven 3.6 or higher**
   - Download from: https://maven.apache.org/download.cgi
   - Verify installation: `mvn -version`
   - Expected output: `Apache Maven 3.x.x`

### System Requirements

- **Operating System**: Windows, Linux, or macOS
- **Memory**: Minimum 512 MB RAM
- **Disk Space**: Minimum 100 MB for project files

### Optional (for Client-Server mode)

- Network connectivity if running client and server on different machines
- Firewall permissions for port 5000 (or custom port)

---

## Installation

### Step 1: Clone or Download the Project

If you have the project files, ensure they are in a directory. The project structure should look like:

```
drone-simulator/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── drone/
│   │               └── simulator/
│   └── test/
│       └── java/
└── target/
```

### Step 2: Compile the Project

Navigate to the project root directory (where `pom.xml` is located) and run:

```bash
mvn clean compile
```

**What this does:**
- `clean`: Removes any previous build artifacts in the `target/` directory
- `compile`: Compiles all Java source files into bytecode in `target/classes/`

**Expected output:**
```
[INFO] Scanning for projects...
[INFO] Building drone-simulator 1.0-SNAPSHOT
[INFO] --- maven-clean-plugin:3.1.0:clean (default-clean) @ drone-simulator ---
[INFO] Deleting /home/chou/drone/target
[INFO] --- maven-compiler-plugin:3.8.1:compile (default-compile) @ drone-simulator ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 11 source files to /home/chou/drone/target/classes
[INFO] BUILD SUCCESS
```

### Step 3: Create the Executable JAR (Optional but Recommended)

To create a standalone JAR file that can be run anywhere:

```bash
mvn clean package
```

**What this does:**
- Compiles the project
- Packages all compiled classes into a JAR file
- Creates `target/drone-simulator-1.0-SNAPSHOT.jar`

**Expected output:**
```
[INFO] Building jar: /home/chou/drone/target/drone-simulator-1.0-SNAPSHOT.jar
[INFO] BUILD SUCCESS
```

### Step 4: Verify Installation

Run the simulator without arguments to see the interactive menu:

```bash
java -cp target/classes com.drone.simulator.DroneSimulatorMain
```

Or if you created the JAR:

```bash
java -jar target/drone-simulator-1.0-SNAPSHOT.jar
```

**Expected output:**
```
=========================================
    SIMULATEUR DE FLOTTE DE DRONES
=========================================

Choisissez le mode d'exécution:
1. Mode Basique (séquentiel)
2. Mode Concurrent (avec threads)
3. Mode Serveur (communicant)
4. Mode Client (communicant)
5. Quitter
Votre choix (1-5):
```

---

## Configuration File Format

The simulator uses a text-based configuration file to define the map and drone movements.

### File Structure

```
<width> <height>
<x1> <y1> <orientation1> <commands1>
<x2> <y2> <orientation2> <commands2>
...
```

### Parameters Explained

#### First Line: Map Dimensions
- **width**: Integer representing the horizontal size of the map (number of columns)
- **height**: Integer representing the vertical size of the map (number of rows)
- **Example**: `20 20` creates a 20x20 grid

#### Subsequent Lines: Drone Definitions
Each line defines one drone with the following format:

- **x**: Starting X coordinate (0 to width-1)
- **y**: Starting Y coordinate (0 to height-1)
- **orientation**: Starting direction (N, S, E, O)
- **commands**: Sequence of movement commands (L, R, M, B)

### Orientation Values

- **N**: North (facing up, represented as ↑)
- **S**: South (facing down, represented as ↓)
- **E**: East (facing right, represented as →)
- **O**: West (facing left, represented as ←)

### Command Values

- **L**: Turn Left 90°
- **R**: Turn Right 90°
- **M**: Move Forward one cell
- **B**: Move Backward one cell

### Example Configuration File

Create a file named `config.txt` with the following content:

```
20 20
0 5 N LMLMLMLMM
5 0 S MMRMMRMRRM
```

**Explanation:**
- Map size: 20 columns × 20 rows
- Drone 0: Starts at (0,5) facing North, executes: L, M, L, M, L, M, L, M, M, M
- Drone 1: Starts at (5,0) facing South, executes: M, M, R, M, M, R, M, R, R, M

### Creating Your Own Configuration

1. Open a text editor (Notepad, VS Code, etc.)
2. On the first line, enter your desired map dimensions separated by a space
3. On each subsequent line, enter drone data separated by spaces
4. Save the file with a `.txt` extension
5. Note the file path for use with the simulator

### Tips for Configuration Files

- Ensure coordinates are within map bounds (0 to width-1 for x, 0 to height-1 for y)
- Commands can be any length
- You can have any number of drones
- Empty lines are ignored
- Lines starting with `#` are treated as comments (in some implementations)

---

## Running the Simulator

The simulator can be run in two ways:

### Method 1: Interactive Menu (Recommended for Beginners)

This method provides a user-friendly menu to select options.

**Command:**
```bash
java -cp target/classes com.drone.simulator.DroneSimulatorMain
```

**Or with JAR:**
```bash
java -jar target/drone-simulator-1.0-SNAPSHOT.jar
```

**Steps:**
1. Run the command above
2. Select a mode by entering 1-5
3. Follow the on-screen prompts
4. Enter configuration file path when asked
5. Enter server details if using client mode

**Advantages:**
- No need to remember command-line arguments
- Interactive prompts guide you through options
- Default values are suggested

### Method 2: Command-Line Arguments (For Advanced Users)

This method allows direct execution without interactive prompts.

**General syntax:**
```bash
java -cp target/classes com.drone.simulator.DroneSimulatorMain <mode> [arguments...]
```

**Or with JAR:**
```bash
java -jar target/drone-simulator-1.0-SNAPSHOT.jar <mode> [arguments...]
```

**Advantages:**
- Faster execution
- Can be scripted
- Useful for automation and testing

---

## Mode 1: Basic Sequential Mode

### Overview

In Basic Mode, drones execute their commands one after another in sequence. This is the simplest mode and is ideal for:
- Understanding the basic drone movement logic
- Debugging drone paths
- Learning the simulator's behavior

### How It Works

1. The simulator reads the configuration file
2. It creates a map with random obstacles (15% density by default)
3. It places drones at their starting positions
4. For each drone:
   - Executes all commands one by one
   - Displays position after each command
5. After all drones complete, displays final positions

### Running Basic Mode

#### Interactive Method

1. Start the simulator:
   ```bash
   java -jar target/drone-simulator-1.0-SNAPSHOT.jar
   ```

2. Select option `1` for "Mode Basique (séquentiel)"

3. Enter the configuration file path when prompted:
   ```
   Entrez le chemin du fichier de configuration: config.txt
   ```

#### Command-Line Method

```bash
java -jar target/drone-simulator-1.0-SNAPSHOT.jar basic config.txt
```

**Parameters:**
- `basic`: Specifies basic mode
- `config.txt`: Path to your configuration file

### Expected Output

```
=== DÉMARRAGE MODE BASIQUE ===
Initialisation de la simulation basique...
Carte initialisée (20x20)
Nombre de drones: 2
Obstacles générés automatiquement

Début de la simulation séquentielle...
========================================

Carte initiale:
Carte de simulation (20x20):
+--------------------+
|....................|
|....................|
|....................|
|....................|
|....................|
|<...................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
+--------------------+

Drone 0 : Position initiale : 0 5 N | Commandes : LMLMLMLMM
===========================================
Position du drone 0 : 0 5 O (après commande 1 : L)
Position du drone 0 : 19 5 O (après commande 2 : M)
Position du drone 0 : 19 5 S (après commande 3 : L)
Position du drone 0 : 18 5 S (après commande 4 : M)
Position du drone 0 : 18 5 E (après commande 5 : L)
Position du drone 0 : 19 5 E (après commande 6 : M)
Position du drone 0 : 19 5 N (après commande 7 : L)
Position du drone 0 : 18 5 N (après commande 8 : M)
Position du drone 0 : 17 5 N (après commande 9 : M)
Position du drone 0 : 16 5 N (après commande 10 : M)

Drone 1 : Position initiale : 5 0 S | Commandes : MMRMMRMRRM
===========================================
Position du drone 1 : 5 19 S (après commande 1 : M)
Position du drone 1 : 5 18 S (après commande 2 : M)
Position du drone 1 : 5 18 O (après commande 3 : R)
Position du drone 1 : 4 18 O (après commande 4 : M)
Position du drone 1 : 3 18 O (après commande 5 : M)
Position du drone 1 : 3 18 N (après commande 6 : R)
Position du drone 1 : 2 18 N (après commande 7 : M)
Position du drone 1 : 2 18 O (après commande 8 : R)
Position du drone 1 : 2 18 S (après commande 9 : R)
Position du drone 1 : 2 17 S (après commande 10 : M)

========================================
Simulation terminée !
========================================

Carte finale:
Carte de simulation (20x20):
+--------------------+
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|................v...|
|....................|
|....................|
|................^...|
+--------------------+

Positions finales des drones:
Drone 0 : 16 5 N
Drone 1 : 2 17 S
=== MODE BASIQUE TERMINÉ ===
```

### Understanding the Output

- **Carte initiale**: Shows the map before any movement
  - `.`: Empty cell
  - `#`: Obstacle
  - `^`, `v`, `>`, `<`: Drone with orientation

- **Position updates**: Shows each drone's position after every command
  - Format: `Drone X : x y orientation (après commande Y : command)`

- **Carte finale**: Shows the map after all movements complete

- **Positions finales**: Summary of final positions for all drones

### Key Features in Basic Mode

- **Obstacle Avoidance**: Drones cannot move into obstacle cells
- **Wrapping**: When a drone moves off one edge, it appears on the opposite edge
- **Sequential Execution**: Drones don't interfere with each other

### Common Issues and Solutions

**Issue**: `File not found` error
- **Solution**: Ensure the configuration file path is correct and the file exists

**Issue**: Drone doesn't move as expected
- **Solution**: Check if obstacles are blocking the path; obstacles are randomly generated

**Issue**: Coordinates out of bounds
- **Solution**: Verify starting coordinates are within map dimensions

---

## Mode 2: Concurrent Mode

### Overview

In Concurrent Mode, all drones move simultaneously using separate threads. This mode demonstrates:
- Multi-threading in Java
- Thread synchronization
- Real-time visualization
- Concurrent programming concepts

### How It Works

1. The simulator reads the configuration file
2. It creates a map with random obstacles
3. For each drone, it creates a separate `DroneWorker` thread
4. It creates a dedicated `DisplayWorker` thread for visualization
5. All drone threads start simultaneously
6. The display thread refreshes the map in real-time
7. Synchronization mechanisms ensure thread-safe operations

### Running Concurrent Mode

#### Interactive Method

1. Start the simulator:
   ```bash
   java -jar target/drone-simulator-1.0-SNAPSHOT.jar
   ```

2. Select option `2` for "Mode Concurrent (avec threads)"

3. Enter the configuration file path when prompted:
   ```
   Entrez le chemin du fichier de configuration: config.txt
   ```

#### Command-Line Method

```bash
java -jar target/drone-simulator-1.0-SNAPSHOT.jar concurrent config.txt
```

**Parameters:**
- `concurrent`: Specifies concurrent mode
- `config.txt`: Path to your configuration file

### Expected Output

```
=== DÉMARRAGE MODE CONCURRENT ===
Initialisation de la simulation concurrente...
Carte initialisée (20x20)
Nombre de drones: 2
Obstacles générés automatiquement

Démarrage des threads de simulation...
Début de la simulation concurrente...
=========================================

[Screen clears and displays real-time animation]

Carte de simulation (20x20):
+--------------------+
|....................|
|....................|
|....................|
|....................|
|....................|
|<...................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
|....................|
+--------------------+

Informations des drones:
Drone 0 : 0 5 N | Commandes restantes : 10 commandes
Drone 1 : 5 0 S | Commandes restantes : 10 commandes

[Map updates in real-time as drones move]

=========================================
Simulation concurrente terminée !
=========================================

Positions finales des drones:
Drone 0 : 16 5 N
Drone 1 : 2 17 S
=== MODE CONCURRENT TERMINÉ ===
```

### Understanding the Concurrent Execution

**Thread Architecture:**

1. **DroneWorker Threads** (one per drone):
   - Executes drone commands independently
   - Signals the display thread after each movement
   - Uses synchronization locks to coordinate with display

2. **DisplayWorker Thread**:
   - Continuously refreshes the display
   - Waits for signals from drone threads
   - Updates the map visualization in real-time
   - Shows drone positions and remaining commands

**Synchronization Mechanisms:**

- **ReentrantLock**: Ensures thread-safe access to shared resources
- **Condition**: Allows threads to wait and signal each other
- **volatile boolean**: Controls thread lifecycle safely

### Key Features in Concurrent Mode

- **Real-time Visualization**: Watch drones move simultaneously
- **Thread Synchronization**: Proper coordination between threads
- **200ms Delay**: Each movement takes 200ms for visibility
- **Screen Clearing**: Console is cleared between updates for smooth animation

### Differences from Basic Mode

| Feature | Basic Mode | Concurrent Mode |
|---------|-----------|-----------------|
| Execution | Sequential | Parallel |
| Threads | Single thread | Multiple threads |
| Visualization | Step-by-step | Real-time animation |
| Speed | Fast | Slower (200ms per move) |
| Complexity | Simple | Complex (synchronization) |

### Common Issues and Solutions

**Issue**: Screen flickering during animation
- **Solution**: This is normal; the console clears between updates

**Issue**: Simulation appears frozen
- **Solution**: Wait a few seconds; threads may be processing

**Issue**: Race conditions or inconsistent output
- **Solution**: The synchronization mechanisms should prevent this; if it occurs, it may be a bug

---

## Mode 3: Client-Server Mode

### Overview

Client-Server Mode enables network-based simulation where:
- A **Server** runs on one machine and listens for connections
- A **Client** runs on any machine and sends configuration to the server
- The server executes the simulation and sends results back

This mode demonstrates:
- Socket programming in Java
- Client-server architecture
- Network communication protocols
- Remote execution

### Architecture

```
Client Machine                    Server Machine
┌─────────────┐                  ┌─────────────┐
│  DroneClient│                  │  DroneServer│
│             │                  │             │
│  1. Read    │                  │  1. Listen  │
│     config  │                  │     on port │
│             │                  │             │
│  2. Connect │◄────────────────►│  2. Accept  │
│     to srv  │   TCP Socket     │     connect │
│             │                  │             │
│  3. Send    │────────────────►│  3. Receive │
│     config  │                  │     config  │
│             │                  │             │
│  4. Receive │◄────────────────►│  4. Execute │
│     results │                  │     sim     │
│             │                  │             │
│  5. Display │                  │  5. Send    │
│     results │                  │     results │
└─────────────┘                  └─────────────┘
```

### Running the Server

#### Interactive Method

1. Start the simulator:
   ```bash
   java -jar target/drone-simulator-1.0-SNAPSHOT.jar
   ```

2. Select option `3` for "Mode Serveur (communicant)"

3. Enter the port number when prompted (or press Enter for default 5000):
   ```
   Entrez le port d'écoute (5000): 5000
   ```

#### Command-Line Method

**Default port (5000):**
```bash
java -jar target/drone-simulator-1.0-SNAPSHOT.jar server
```

**Custom port:**
```bash
java -jar target/drone-simulator-1.0-SNAPSHOT.jar server 8080
```

**Parameters:**
- `server`: Specifies server mode
- `8080`: Optional port number (default: 5000)

#### Expected Server Output

```
=== DÉMARRAGE MODE SERVEUR ===
Entrez le port d'écoute (5000): 5000

Serveur démarré sur le port 5000
En attente de connexions clientes...
```

The server will now wait for client connections. It can handle multiple clients sequentially.

#### Stopping the Server

Press `Ctrl+C` to stop the server gracefully. The shutdown hook will ensure proper cleanup.

### Running the Client

#### Interactive Method

1. Start the simulator in a separate terminal:
   ```bash
   java -jar target/drone-simulator-1.0-SNAPSHOT.jar
   ```

2. Select option `4` for "Mode Client (communicant)"

3. Enter the configuration file path:
   ```
   Entrez le chemin du fichier de configuration: config.txt
   ```

4. Enter the server host (or press Enter for localhost):
   ```
   Entrez l'hôte du serveur (localhost): localhost
   ```

5. Enter the server port (or press Enter for 5000):
   ```
   Entrez le port du serveur (5000): 5000
   ```

#### Command-Line Method

**Localhost with default port:**
```bash
java -jar target/drone-simulator-1.0-SNAPSHOT.jar client config.txt
```

**Remote host with custom port:**
```bash
java -jar target/drone-simulator-1.0-SNAPSHOT.jar client config.txt 192.168.1.100 8080
```

**Parameters:**
- `client`: Specifies client mode
- `config.txt`: Path to configuration file
- `192.168.1.100`: Optional server host (default: localhost)
- `8080`: Optional server port (default: 5000)

#### Expected Client Output

```
=== DÉMARRAGE MODE CLIENT ===
Entrez le chemin du fichier de configuration: config.txt
Entrez l'hôte du serveur (localhost): localhost
Entrez le port du serveur (5000): 5000

Connecté au serveur localhost:5000
Envoyé dimensions: 20x20
Envoyé nombre de drones: 2
Envoyé drone 0: 0 5 N LMLMLMLMM
Envoyé drone 1: 5 0 S MMRMMRMRRM

En attente des résultats du serveur...

Simulation réussie !

Positions finales des drones:
Drone 0 : 16 5 N (initial: 0 5 N)
Drone 1 : 2 17 S (initial: 5 0 S)
=== MODE CLIENT TERMINÉ ===
```

#### Expected Server Output (During Client Connection)

```
Client connecté : /127.0.0.1
Carte initialisée (20x20)
Nombre de drones: 2
Obstacles générés automatiquement
Démarrage des threads de simulation...
Début de la simulation concurrente...
=========================================
[Simulation runs on server]
Simulation terminée pour le client /127.0.0.1
```

### Network Configuration

#### Local Testing (Same Machine)

- Server: `localhost` or `127.0.0.1`
- Port: Any available port (default: 5000)
- No network configuration needed

#### Remote Testing (Different Machines)

**Server Machine:**
1. Ensure firewall allows incoming connections on the chosen port
2. Find the server's IP address:
   - Linux/Mac: `ifconfig` or `ip addr`
   - Windows: `ipconfig`
3. Start the server

**Client Machine:**
1. Ensure network connectivity to server
2. Use server's IP address as host
3. Use the same port as server

#### Firewall Configuration

**Linux (UFW):**
```bash
sudo ufw allow 5000/tcp
```

**Windows Firewall:**
- Add inbound rule for port 5000
- Allow TCP connections

**Mac:**
```bash
sudo /usr/libexec/ApplicationFirewall/socketfilterfw --add /path/to/java
```

### Communication Protocol

The client-server communication uses a simple text-based protocol:

**Client → Server:**
```
Line 1: "<width> <height>"
Line 2: "<num_drones>"
Line 3+: "<x> <y> <orientation> <commands>"
```

**Server → Client:**
```
Line 1: "SUCCESS" or "ERROR: <message>"
Line 2+: "<x> <y> <orientation>" (for each drone)
```

### Key Features in Client-Server Mode

- **Remote Execution**: Run simulations on a different machine
- **Multiple Clients**: Server can handle multiple sequential connections
- **Concurrent Execution**: Server uses concurrent mode for simulation
- **Error Handling**: Proper error messages sent to client
- **Graceful Shutdown**: Ctrl+C handling for clean server shutdown

### Common Issues and Solutions

**Issue**: `Connection refused` error
- **Solution**: Ensure server is running and listening on the correct port

**Issue**: `Unknown host` error
- **Solution**: Verify the host address is correct and reachable

**Issue**: Firewall blocking connection
- **Solution**: Configure firewall to allow traffic on the chosen port

**Issue**: Port already in use
- **Solution**: Choose a different port or stop the process using the port

**Issue**: Client hangs waiting for results
- **Solution**: Check server logs for errors; may be simulation failure

---

## Command Reference

### Quick Reference

| Command | Description |
|---------|-------------|
| `L` | Turn Left 90° |
| `R` | Turn Right 90° |
| `M` | Move Forward one cell |
| `B` | Move Backward one cell |

### Orientation Reference

| Orientation | Direction | Symbol | Turns |
|-------------|-----------|--------|-------|
| N | North | ↑ | L→O, R→E |
| S | South | ↓ | L→E, R→O |
| E | East | → | L→N, R→S |
| O | West | ← | L→S, R→N |

### Command-Line Arguments Reference

#### General Format
```bash
java -jar drone-simulator-1.0-SNAPSHOT.jar <mode> [arguments...]
```

#### Mode: Basic
```bash
java -jar drone-simulator-1.0-SNAPSHOT.jar basic <config_file>
```
- `config_file`: Path to configuration file (required)

#### Mode: Concurrent
```bash
java -jar drone-simulator-1.0-SNAPSHOT.jar concurrent <config_file>
```
- `config_file`: Path to configuration file (required)

#### Mode: Server
```bash
java -jar drone-simulator-1.0-SNAPSHOT.jar server [port]
```
- `port`: Port number (optional, default: 5000)

#### Mode: Client
```bash
java -jar drone-simulator-1.0-SNAPSHOT.jar client <config_file> [host] [port]
```
- `config_file`: Path to configuration file (required)
- `host`: Server address (optional, default: localhost)
- `port`: Server port (optional, default: 5000)

### Maven Commands Reference

```bash
# Clean and compile
mvn clean compile

# Create JAR
mvn clean package

# Run tests
mvn test

# Clean only
mvn clean

# Compile only
mvn compile

# Skip tests during build
mvn clean package -DskipTests
```

---

## Troubleshooting

### Common Errors and Solutions

#### Error: `java.lang.ClassNotFoundException`

**Cause**: Classpath not set correctly or JAR not built

**Solution**:
```bash
# Rebuild the project
mvn clean package

# Use correct classpath
java -cp target/classes com.drone.simulator.DroneSimulatorMain
```

#### Error: `java.io.FileNotFoundException: config.txt`

**Cause**: Configuration file not found at specified path

**Solution**:
- Use absolute path: `/home/user/drone/config.txt`
- Use relative path from project root: `config.txt`
- Verify file exists: `ls -la config.txt`

#### Error: `Port already in use`

**Cause**: Another process is using the specified port

**Solution**:
```bash
# Find process using port (Linux/Mac)
lsof -i :5000

# Kill the process
kill -9 <PID>

# Or use a different port
java -jar drone-simulator.jar server 8080
```

#### Error: `Connection refused`

**Cause**: Server not running or wrong port

**Solution**:
- Verify server is running
- Check port number matches
- Test connectivity: `telnet localhost 5000`

#### Error: `Invalid orientation` or `Invalid command`

**Cause**: Configuration file has invalid characters

**Solution**:
- Check orientation is N, S, E, or O
- Check commands are L, R, M, or B
- Verify no typos in configuration file

#### Error: `ArrayIndexOutOfBoundsException`

**Cause**: Drone coordinates outside map bounds

**Solution**:
- Ensure x is between 0 and width-1
- Ensure y is between 0 and height-1
- Check map dimensions in configuration file

### Performance Issues

#### Issue: Slow simulation in concurrent mode

**Cause**: Too many drones or large map

**Solution**:
- Reduce number of drones
- Use smaller map dimensions
- Close other applications

#### Issue: Memory errors

**Cause**: Insufficient heap memory

**Solution**:
```bash
java -Xmx512m -jar drone-simulator.jar
```

### Debugging Tips

#### Enable Verbose Output

Add debug prints in code or use:
```bash
java -verbose:class -jar drone-simulator.jar
```

#### Check Java Version

```bash
java -version
```

#### Check Maven Version

```bash
mvn -version
```

#### View Test Results

```bash
mvn test
```

### Getting Help

If issues persist:
1. Check the README.md file
2. Review test files for examples
3. Examine source code comments
4. Check Java and Maven documentation

---

## Examples

### Example 1: Single Drone, Simple Path

**Configuration (`simple.txt`):**
```
10 10
5 5 N MMMM
```

**Run:**
```bash
java -jar drone-simulator.jar basic simple.txt
```

**Result**: Drone moves north 4 steps from center

### Example 2: Multiple Drones, Complex Paths

**Configuration (`complex.txt`):**
```
15 15
0 0 E MRMRMRM
14 14 W MLMLMLM
7 7 N LMLMRMLM
```

**Run:**
```bash
java -jar drone-simulator.jar concurrent complex.txt
```

**Result**: Three drones move in different patterns simultaneously

### Example 3: Large Map, Many Drones

**Configuration (`large.txt`):**
```
50 50
0 0 N MMMMMMMMMM
10 10 S MMMMMMMMMM
20 20 E MMMMMMMMMM
30 30 O MMMMMMMMMM
40 40 N MMMMMMMMMM
```

**Run:**
```bash
java -jar drone-simulator.jar concurrent large.txt
```

**Result**: Five drones on a large map

### Example 4: Client-Server on Same Machine

**Terminal 1 (Server):**
```bash
java -jar drone-simulator.jar server 6000
```

**Terminal 2 (Client):**
```bash
java -jar drone-simulator.jar client config.txt localhost 6000
```

**Result**: Client sends config to local server, receives results

### Example 5: Client-Server on Different Machines

**Server Machine (IP: 192.168.1.100):**
```bash
java -jar drone-simulator.jar server 5000
```

**Client Machine:**
```bash
java -jar drone-simulator.jar client config.txt 192.168.1.100 5000
```

**Result**: Remote simulation execution

### Example 6: Testing with Different Obstacle Densities

Modify `SimulatorBasic.java` or `SimulatorConcurrent.java`:
```java
// Change obstacle density from 0.15 to 0.30
map.addObstacles(0.30); // 30% obstacles
```

**Result**: More obstacles on the map

---

## Conclusion

This user guide covers all aspects of using the Drone Simulator, from installation to advanced client-server mode. For more information about the project's architecture and implementation, refer to the `PROJECT_REALIZATION.md` file.

### Additional Resources

- **README.md**: Project overview and quick start
- **PROJECT_REALIZATION.md**: Detailed technical documentation
- **Source Code**: Well-commented Java files in `src/main/java/`
- **Test Files**: Unit tests in `src/test/java/`

### Support

For issues or questions:
1. Check this guide's troubleshooting section
2. Review the source code comments
3. Examine test cases for examples
4. Consult Java and Maven documentation

---

**Version**: 1.0  
**Last Updated**: 2026  
**Project**: Drone Fleet Simulator (M1 RSI 2025-2026)

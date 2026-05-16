# Drone Simulator - Project Realization Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Project Context and Objectives](#project-context-and-objectives)
3. [Technology Stack](#technology-stack)
4. [Architecture Design](#architecture-design)
5. [Component Breakdown](#component-breakdown)
6. [Implementation Details](#implementation-details)
7. [Design Patterns Used](#design-patterns-used)
8. [Concurrency Implementation](#concurrency-implementation)
9. [Network Communication](#network-communication)
10. [Testing Strategy](#testing-strategy)
11. [Build and Deployment](#build-and-deployment)
12. [Challenges and Solutions](#challenges-and-solutions)
13. [Future Improvements](#future-improvements)

---

## Project Overview

### What is the Drone Simulator?

The Drone Simulator is a Java-based N-Tiers application designed to simulate the movement of a fleet of delivery drones on a city map with obstacles. The project was developed as an educational exercise to demonstrate advanced Java programming concepts including concurrent programming, network communication, and multi-tier architecture.

### Project Context

This project was created as part of the M1 RSI (Master 1 Réseaux et Systèmes Informatiques) program for the 2025-2026 academic year at FSTS (Faculté des Sciences et Techniques), under the direction of Professor A. Marzouk.

### Problem Statement

A delivery company uses a fleet of drones to perform home deliveries. The city map is represented as a rectangular grid containing obstacles. Drones receive coded paths to execute their deliveries. The simulator needs to:
- Visualize drone movements on a map
- Handle obstacles that drones cannot traverse
- Support multiple drones operating simultaneously
- Provide different execution modes for learning purposes

### Project Goals

**Educational Objectives:**
- Demonstrate proficiency in Java programming
- Implement concurrent programming using threads
- Develop network communication using sockets
- Apply N-Tiers architecture principles
- Practice software engineering best practices

**Functional Objectives:**
- Simulate drone movements on a 2D grid
- Handle obstacle avoidance
- Support sequential and concurrent execution
- Enable remote execution via client-server architecture
- Provide real-time visualization

---

## Project Context and Objectives

### Why This Project Was Created

**Educational Purpose:**
This project serves as a comprehensive learning exercise for students to master several key computer science concepts:

1. **Java Programming**: Using object-oriented principles, exception handling, and standard libraries
2. **Concurrent Programming**: Understanding threads, synchronization, and race conditions
3. **Network Programming**: Implementing client-server communication using TCP sockets
4. **Software Architecture**: Applying N-Tiers architecture principles
5. **Testing**: Writing unit tests to validate functionality

**Real-World Relevance:**
The project simulates a realistic scenario that delivery companies face today:
- Amazon Prime Air drone delivery
- Google Wing drone delivery
- Medical drone deliveries
- Autonomous drone fleets

### Target Audience

**Primary Users:**
- Students learning Java and concurrent programming
- Instructors teaching advanced programming concepts
- Developers interested in simulation systems

**Secondary Users:**
- Researchers studying drone fleet management
- Companies prototyping drone delivery systems

### Success Criteria

The project is considered successful when:
- All three execution modes work correctly
- Drones properly avoid obstacles
- Concurrent execution demonstrates thread safety
- Client-server communication is reliable
- Code is well-documented and maintainable
- Unit tests validate core functionality

---

## Technology Stack

### Programming Language: Java 11

**Why Java 11?**
- **Long-Term Support (LTS)**: Java 11 is an LTS version with extended support
- **Modern Features**: Includes var keyword, improved HTTP client, and other enhancements
- **Industry Standard**: Widely used in enterprise environments
- **Cross-Platform**: Runs on Windows, Linux, and macOS
- **Rich Ecosystem**: Extensive libraries and frameworks available

**Key Java Features Used:**
- Object-oriented programming (classes, inheritance, polymorphism)
- Exception handling (try-catch, custom exceptions)
- Collections framework (List, ArrayList)
- Concurrent utilities (Thread, ReentrantLock, Condition, CountDownLatch)
- I/O streams (BufferedReader, PrintWriter, FileReader)
- Network programming (Socket, ServerSocket)

### Build Tool: Apache Maven 3.6+

**Why Maven?**
- **Standardization**: De facto standard for Java project management
- **Dependency Management**: Handles project dependencies automatically
- **Build Lifecycle**: Provides clean, compile, test, package phases
- **Project Structure**: Enforces standard directory layout
- **Reproducibility**: Ensures consistent builds across environments

**Maven Configuration (pom.xml):**
```xml
<groupId>com.drone</groupId>
<artifactId>drone-simulator</artifactId>
<version>1.0-SNAPSHOT</version>
<packaging>jar</packaging>

<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

**Plugins Used:**
- `maven-compiler-plugin`: Compiles Java source code
- `maven-jar-plugin`: Creates executable JAR with manifest

### Testing Framework: JUnit 4.13.2

**Why JUnit 4?**
- **Maturity**: Well-established testing framework
- **Simplicity**: Easy to write and understand tests
- **Integration**: Works seamlessly with Maven
- **Widespread Adoption**: Industry standard for Java testing

**Test Coverage:**
- Unit tests for core classes (Drone, SimulatorMap, ConfigurationReader)
- Integration tests for each simulator version
- Network tests for client-server communication

### Development Environment

**Recommended Tools:**
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code
- **Version Control**: Git
- **Build Tool**: Maven
- **JDK**: OpenJDK 11 or Oracle JDK 11

---

## Architecture Design

### N-Tiers Architecture

The project follows a three-tier architecture pattern:

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Tier                      │
│  (User Interface - Console Menu / Command Line)         │
│  DroneSimulatorMain.java                                │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                   Business Logic Tier                     │
│  (Simulation Logic - Drone Movement, Map Management)     │
│  - Drone.java                                           │
│  - SimulatorMap.java                                    │
│  - SimulatorBasic.java                                  │
│  - SimulatorConcurrent.java                             │
│  - ConfigurationReader.java                             │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                   Data Access Tier                       │
│  (Data Persistence - File I/O, Network I/O)             │
│  - ConfigurationReader.java (File I/O)                   │
│  - DroneClient.java (Network I/O)                       │
│  - DroneServer.java (Network I/O)                       │
└─────────────────────────────────────────────────────────┘
```

### Package Structure

```
com.drone.simulator/
├── Drone.java                    # Business object representing a drone
├── SimulatorMap.java              # Map representation with obstacles
├── ConfigurationReader.java      # File parsing and configuration loading
├── SimulatorBasic.java           # Sequential simulation implementation
├── DroneSimulatorMain.java       # Main entry point with menu
├── concurrent/                   # Concurrent execution package
│   ├── DroneWorker.java         # Thread for individual drone execution
│   ├── DisplayWorker.java       # Thread for real-time display
│   └── SimulatorConcurrent.java # Concurrent simulation coordinator
├── server/                       # Server-side network package
│   ├── DroneServer.java         # Server socket listener
│   └── ServerConnectionHandler.java # Per-client connection handler
└── client/                       # Client-side network package
    └── DroneClient.java         # Client socket connector
```

### Design Principles Applied

**1. Separation of Concerns**
- Each class has a single, well-defined responsibility
- Business logic separated from presentation and data access
- Network code isolated in dedicated packages

**2. Encapsulation**
- Private fields with public getters/setters
- Internal implementation details hidden
- Clear public interfaces

**3. Modularity**
- Packages organized by functionality
- Components can be tested independently
- Easy to extend with new features

**4. Reusability**
- Core classes (Drone, SimulatorMap) reused across all versions
- Configuration reader used by all modes
- Network components can be reused for other protocols

---

## Component Breakdown

### 1. Drone.java

**Purpose**: Represents a drone entity with position, orientation, and movement capabilities.

**Location**: `src/main/java/com/drone/simulator/Drone.java`

**Key Responsibilities**:
- Store drone position (x, y coordinates)
- Store drone orientation (N, S, E, O)
- Store and execute movement commands (L, R, M, B)
- Handle movement logic including obstacle checking
- Provide display representation

**Key Methods**:
```java
public void executeCommand(char cmd, SimulatorMap map)
public void turnLeft()
public void turnRight()
public void moveForward(SimulatorMap map)
public void moveBackward(SimulatorMap map)
public String getPosition()
public char getDisplayChar()
public boolean hasRemainingCommands()
public void executeNextCommand(SimulatorMap map)
```

**Implementation Details**:
- **Position Tracking**: Uses integer coordinates (x, y)
- **Orientation**: Stored as character (N, S, E, O)
- **Command Execution**: Sequential execution with index tracking
- **Movement Logic**: 
  - Forward/backward movement based on orientation
  - Wrapping using modulo arithmetic
  - Obstacle checking before movement
- **Display Characters**: Arrow symbols (↑, ↓, →, ←) for visualization

**Why This Design**:
- Simple, intuitive representation of a drone
- Encapsulates all drone-specific logic
- Reusable across all simulation modes
- Easy to extend with new commands or behaviors

### 2. SimulatorMap.java

**Purpose**: Represents the simulation environment as a 2D grid with obstacles.

**Location**: `src/main/java/com/drone/simulator/SimulatorMap.java`

**Key Responsibilities**:
- Store map dimensions (width, height)
- Manage obstacle placement
- Check if positions are blocked
- Implement coordinate wrapping
- Display the map with drones and obstacles

**Key Methods**:
```java
public void addObstacles(double obstacleDensity)
public boolean isObstacle(int x, int y)
public boolean isBlocked(int x, int y)
public int wrapCoordinate(int coord, int max)
public void displayMap(List<Drone> drones)
public void removeObstacle(int x, int y)
public void addObstacle(int x, int y)
```

**Implementation Details**:
- **Grid Representation**: 2D char array (`char[][] grid`)
- **Obstacle Character**: `#` for obstacles, `.` for empty cells
- **Obstacle Generation**: Random placement based on density percentage
- **Wrapping Logic**: `((coord % max) + max) % max` handles negative coordinates
- **Display**: ASCII art with borders for clear visualization
- **Drone Overlay**: Drones overlaid on obstacle grid for display

**Why This Design**:
- Simple grid representation is easy to understand
- Random obstacle generation provides variety
- Wrapping creates a toroidal (donut-shaped) world
- ASCII display works in any terminal
- Efficient for small to medium maps

### 3. ConfigurationReader.java

**Purpose**: Parses configuration files and loads simulation parameters.

**Location**: `src/main/java/com/drone/simulator/ConfigurationReader.java`

**Key Responsibilities**:
- Read configuration files
- Parse map dimensions
- Parse drone data (position, orientation, commands)
- Validate input data
- Provide structured configuration object

**Key Methods**:
```java
public Configuration readConfiguration(String filename)
private Drone parseDroneData(String line, int droneIndex)
public int[] parseMapDimensions(String filename)
public List<Drone> parseDroneData(String filename)
```

**Inner Class**:
```java
public static class Configuration {
    private final int mapWidth;
    private final int mapHeight;
    private final List<Drone> drones;
}
```

**Implementation Details**:
- **File Format**: Text file with space-separated values
- **Parsing**: Uses `BufferedReader` for efficient line-by-line reading
- **Validation**: 
  - Checks orientation validity (N, S, E, O)
  - Validates command characters (L, R, M, B)
  - Ensures numeric format for coordinates
- **Error Handling**: Throws descriptive exceptions for invalid input
- **Immutable Configuration**: Configuration class uses final fields

**Why This Design**:
- Separates file parsing from simulation logic
- Provides clear error messages for invalid input
- Reusable across all simulation modes
- Easy to extend with new configuration options
- Immutable configuration prevents accidental modification

### 4. SimulatorBasic.java

**Purpose**: Implements sequential (Version 1) simulation where drones move one after another.

**Location**: `src/main/java/com/drone/simulator/SimulatorBasic.java`

**Key Responsibilities**:
- Initialize simulation from configuration file
- Execute drone commands sequentially
- Display simulation progress
- Show final results

**Key Methods**:
```java
public void initializeSimulation(String filename)
public void executeSimulation()
public void displayResults()
public void runSimulation(String filename)
```

**Implementation Details**:
- **Initialization**: 
  - Reads configuration file
  - Creates map with obstacles
  - Places drones at starting positions
  - Removes obstacles at drone starting positions
- **Execution**:
  - Iterates through drones sequentially
  - For each drone, executes all commands
  - Displays position after each command
- **Display**: Shows initial map, step-by-step movements, and final map
- **No Synchronization Needed**: Single-threaded execution

**Why This Design**:
- Simplest implementation for learning
- Easy to debug and understand
- Serves as baseline for comparison with concurrent version
- Demonstrates basic simulation logic without complexity

### 5. SimulatorConcurrent.java

**Purpose**: Implements concurrent (Version 2) simulation where drones move simultaneously using threads.

**Location**: `src/main/java/com/drone/simulator/concurrent/SimulatorConcurrent.java`

**Key Responsibilities**:
- Initialize concurrent simulation
- Create and manage drone worker threads
- Create and manage display worker thread
- Coordinate thread synchronization
- Handle graceful shutdown

**Key Methods**:
```java
public void initializeSimulation(String filename)
public void initializeSimulation(int width, int height, List<Drone> drones, SimulatorMap map)
public void initializeThreads()
public void startSimulation()
public void waitForCompletion()
public void displayResults()
public void stopSimulation()
```

**Key Fields**:
```java
private ReentrantLock displayLock;
private java.util.concurrent.locks.Condition displayCondition;
private CountDownLatch completionLatch;
private List<DroneWorker> droneWorkers;
private DisplayWorker displayWorker;
```

**Implementation Details**:
- **Thread Architecture**:
  - One `DroneWorker` thread per drone
  - One `DisplayWorker` thread for visualization
  - Main thread coordinates and waits for completion
- **Synchronization**:
  - `ReentrantLock` for mutual exclusion
  - `Condition` for thread signaling
  - `CountDownLatch` for completion tracking
- **Initialization**:
  - Can initialize from file or from parameters (for server mode)
  - Creates all worker threads
  - Sets up synchronization primitives
- **Execution**:
  - Starts display thread first
  - Starts all drone threads
  - Waits for all drone threads to complete
  - Stops display thread
- **Shutdown**:
  - Graceful thread interruption
  - Proper resource cleanup

**Why This Design**:
- Demonstrates real concurrent programming
- Shows proper synchronization techniques
- Enables real-time visualization
- Reusable for server mode
- Teaches thread lifecycle management

### 6. DroneWorker.java

**Purpose**: Worker thread that executes commands for a single drone in concurrent mode.

**Location**: `src/main/java/com/drone/simulator/concurrent/DroneWorker.java`

**Key Responsibilities**:
- Execute drone commands independently
- Signal display thread after each movement
- Handle thread interruption gracefully
- Support graceful shutdown

**Key Methods**:
```java
public void run()
public void stopWorker()
public Drone getDrone()
```

**Implementation Details**:
- **Command Execution**:
  - Iterates through drone's command string
  - Executes each command with 200ms delay for visibility
  - Checks running flag for shutdown
- **Synchronization**:
  - Locks display lock before signaling
  - Signals display condition to wake display thread
  - Unlocks display lock
- **Interruption Handling**:
  - Catches InterruptedException
  - Re-interrupts current thread
  - Logs interruption message
- **Shutdown**:
  - Sets running flag to false
  - Interrupts thread to wake from sleep

**Why This Design**:
- Each drone operates independently
- Demonstrates thread communication
- Allows real-time visualization
- Proper interruption handling
- Clean shutdown mechanism

### 7. DisplayWorker.java

**Purpose**: Dedicated thread for real-time display of the simulation map and drone positions.

**Location**: `src/main/java/com/drone/simulator/concurrent/DisplayWorker.java`

**Key Responsibilities**:
- Continuously refresh the display
- Wait for signals from drone threads
- Display map with current drone positions
- Show drone information (position, remaining commands)
- Handle graceful shutdown with final display

**Key Methods**:
```java
public void run()
private void clearScreen()
private String getRemainingCommands(Drone drone)
public void stopDisplay()
```

**Implementation Details**:
- **Display Loop**:
  - Runs while running flag is true
  - Locks display lock
  - Waits for signal or timeout (100ms)
  - Clears screen and displays map
  - Shows drone information
  - Unlocks display lock
- **Screen Clearing**:
  - Uses ANSI escape codes: `\033[H\033[2J`
  - Works in most modern terminals
- **Timeout**:
  - 100ms timeout ensures display updates even without signals
  - Prevents display from freezing if no signals received
- **Final Display**:
  - On shutdown, displays final map
  - Shows final drone positions
  - Provides simulation summary

**Why This Design**:
- Separates display logic from drone logic
- Enables smooth real-time animation
- Demonstrates producer-consumer pattern
- Proper synchronization with drone threads
- Clean final state display

### 8. DroneServer.java

**Purpose**: Server that listens for client connections and executes simulations remotely.

**Location**: `src/main/java/com/drone/simulator/server/DroneServer.java`

**Key Responsibilities**:
- Listen on specified port for connections
- Accept incoming client connections
- Spawn handler threads for each client
- Handle graceful shutdown
- Manage server lifecycle

**Key Methods**:
```java
public void startServer()
public void stopServer()
public static void main(String[] args)
```

**Implementation Details**:
- **Server Socket**:
  - Creates `ServerSocket` on specified port
  - Default port: 5000
  - Listens in a loop while running
- **Connection Handling**:
  - Accepts incoming connections with `accept()`
  - Creates `ServerConnectionHandler` for each client
  - Spawns new thread for each handler
  - Allows multiple sequential clients
- **Error Handling**:
  - Catches IOException for connection errors
  - Checks running flag to distinguish shutdown from errors
- **Shutdown**:
  - Sets running flag to false
  - Closes server socket
  - Shutdown hook for Ctrl+C handling
  - Graceful cleanup

**Why This Design**:
- Demonstrates server-side socket programming
- Supports multiple clients (sequentially)
- Clean shutdown mechanism
- Thread-per-client model for simplicity
- Reuses concurrent simulation logic

### 9. ServerConnectionHandler.java

**Purpose**: Handles individual client connections, receives configuration, executes simulation, sends results.

**Location**: `src/main/java/com/drone/simulator/server/ServerConnectionHandler.java`

**Key Responsibilities**:
- Parse client configuration
- Validate received data
- Execute concurrent simulation
- Send results back to client
- Handle errors and send error messages

**Key Methods**:
```java
public void run()
private Drone parseDroneData(String line, int droneIndex)
private void executeSimulation(int width, int height, List<Drone> drones, PrintWriter output)
private void sendError(PrintWriter output, String message)
```

**Implementation Details**:
- **Protocol**:
  - Receives map dimensions (width height)
  - Receives number of drones
  - Receives drone data for each drone
  - Sends SUCCESS or ERROR status
  - Sends final drone positions
- **Validation**:
  - Validates dimension format
  - Validates numeric values
  - Validates drone data format
  - Validates orientation and commands
- **Simulation**:
  - Creates map with obstacles
  - Uses `SimulatorConcurrent` for execution
  - Initializes with received parameters
  - Waits for completion
- **Error Handling**:
  - Sends descriptive error messages to client
  - Catches and reports exceptions
  - Ensures socket cleanup in finally block
- **Resource Management**:
  - Uses try-with-resources for streams
  - Closes socket in finally block

**Why This Design**:
- Implements Runnable for threading
- Separates protocol logic from simulation
- Comprehensive error handling
- Clean resource management
- Reuses existing simulation components

### 10. DroneClient.java

**Purpose**: Client that connects to server, sends configuration, receives and displays results.

**Location**: `src/main/java/com/drone/simulator/client/DroneClient.java`

**Key Responsibilities**:
- Connect to remote server
- Read configuration file
- Send configuration to server
- Receive and display results
- Handle connection errors

**Key Methods**:
```java
public void sendConfiguration(String filename)
private void receiveResults(BufferedReader input, List<Drone> originalDrones)
public static void main(String[] args)
```

**Implementation Details**:
- **Connection**:
  - Creates `Socket` with host and port
  - Default: localhost:5000
  - Uses try-with-resources for automatic cleanup
- **Protocol**:
  - Sends map dimensions
  - Sends number of drones
  - Sends drone data for each drone
  - Flushes output after sending
- **Result Reception**:
  - Waits for status line (SUCCESS/ERROR)
  - Parses error messages if ERROR
  - Receives final positions if SUCCESS
  - Displays results with initial positions for comparison
- **Error Handling**:
  - UnknownHostException for invalid host
  - ConnectException for server not running
  - IOException for communication errors
  - Descriptive error messages

**Why This Design**:
- Simple client implementation
- Clear protocol implementation
- Good error handling and reporting
- Reuses configuration reader
- Easy to use from command line

### 11. DroneSimulatorMain.java

**Purpose**: Main entry point providing interactive menu and command-line argument parsing.

**Location**: `src/main/java/com/drone/simulator/DroneSimulatorMain.java`

**Key Responsibilities**:
- Provide interactive menu for mode selection
- Parse command-line arguments
- Delegate to appropriate simulator
- Handle user input
- Display usage information

**Key Methods**:
```java
public static void main(String[] args)
private static void executeFromArgs(String[] args)
private static void showMainMenu()
private static void runBasicMode(String filename)
private static void runConcurrentMode(String filename)
private static void runServerMode()
private static void runClientMode(String filename, String host, int port)
private static void showUsage()
```

**Implementation Details**:
- **Argument Parsing**:
  - Checks if arguments provided
  - Parses mode (basic, concurrent, server, client)
  - Parses optional parameters (port, host)
  - Validates numeric values
- **Interactive Menu**:
  - Scanner for user input
  - Switch statement for mode selection
  - Prompts for required parameters
  - Default values for optional parameters
- **Mode Delegation**:
  - Creates appropriate simulator instance
  - Calls run methods
  - Wraps in try-catch for error handling
- **Usage Display**:
  - Shows all command-line options
  - Provides examples
  - Explains parameters

**Why This Design**:
- Single entry point for all modes
- Flexible (interactive or command-line)
- Clear user interface
- Good error handling
- Comprehensive help documentation

---

## Implementation Details

### Coordinate System

**Grid Representation**:
- Origin (0,0) at top-left corner
- X increases to the right
- Y increases downward
- Width: number of columns (x dimension)
- Height: number of rows (y dimension)

**Example for 5x5 map**:
```
y=0: (0,0) (1,0) (2,0) (3,0) (4,0)
y=1: (0,1) (1,1) (2,1) (3,1) (4,1)
y=2: (0,2) (1,2) (2,2) (3,2) (4,2)
y=3: (0,3) (1,3) (2,3) (3,3) (4,3)
y=4: (0,4) (1,4) (2,4) (3,4) (4,4)
```

**Array Access**: `grid[y][x]` - row-major order

### Movement Logic

**Forward Movement by Orientation**:
```
N (North):  y increases (y + 1)
S (South):  y decreases (y - 1)
E (East):   x increases (x + 1)
O (West):   x decreases (x - 1)
```

**Backward Movement by Orientation**:
```
N (North):  y decreases (y - 1)
S (South):  y increases (y + 1)
E (East):   x decreases (x - 1)
O (West):   x increases (x + 1)
```

### Wrapping Implementation

**Formula**: `((coord % max) + max) % max`

**Why This Formula**:
- Handles positive coordinates: `coord % max`
- Handles negative coordinates: adds max before modulo
- Ensures result is always in range [0, max-1]

**Examples** (for max=10):
```
coord = 5:  ((5 % 10) + 10) % 10 = (5 + 10) % 10 = 15 % 10 = 5
coord = 10: ((10 % 10) + 10) % 10 = (0 + 10) % 10 = 10 % 10 = 0
coord = -1: ((-1 % 10) + 10) % 10 = (-1 + 10) % 10 = 9 % 10 = 9
coord = -5: ((-5 % 10) + 10) % 10 = (-5 + 10) % 10 = 5 % 10 = 5
```

### Obstacle Generation

**Algorithm**:
```java
int numObstacles = (int) (width * height * obstacleDensity);
Random random = new Random();
for (int i = 0; i < numObstacles; i++) {
    int x = random.nextInt(width);
    int y = random.nextInt(height);
    grid[y][x] = OBSTACLE;
}
```

**Characteristics**:
- Random placement
- May have overlapping obstacles (same cell selected multiple times)
- Density parameter controls obstacle percentage
- Default density: 15% (0.15)

**Why Random**:
- Provides variety in simulations
- Simulates real-world unpredictable obstacles
- Easy to implement
- Sufficient for educational purposes

### Orientation Change Logic

**Turn Left (90° counter-clockwise)**:
```
N → O → S → E → N
```

**Turn Right (90° clockwise)**:
```
N → E → S → O → N
```

**Implementation**: Switch statement mapping each orientation to its new value

### Display System

**ASCII Art Format**:
```
+------------------+  (top border with + corners and - dashes)
|..................|  (map content with | side borders)
|..................|
|..................|
+------------------+  (bottom border)
```

**Symbols**:
- `.`: Empty cell
- `#`: Obstacle
- `^`: Drone facing North
- `v`: Drone facing South
- `>`: Drone facing East
- `<`: Drone facing West

**Display Algorithm**:
1. Copy obstacle grid to display grid
2. Overlay drones on display grid
3. Print borders and content
4. Print drone information below map

---

## Design Patterns Used

### 1. Strategy Pattern

**Where Used**: Different simulation modes (Basic, Concurrent, Client-Server)

**Implementation**:
- `SimulatorBasic`: Sequential strategy
- `SimulatorConcurrent`: Concurrent strategy
- `ServerConnectionHandler`: Network strategy

**Benefit**: Easy to add new simulation strategies without modifying existing code

### 2. Factory Pattern

**Where Used**: Configuration reader creating Drone objects

**Implementation**:
```java
private Drone parseDroneData(String line, int droneIndex) {
    // Parse and create Drone instance
    return new Drone(x, y, orientation, commandStr);
}
```

**Benefit**: Centralized object creation with validation

### 3. Observer Pattern

**Where Used**: DisplayWorker observing drone movements

**Implementation**:
- Drone threads signal display thread after each movement
- Display thread waits for signals and updates display
- Uses Condition variable for signaling

**Benefit**: Decouples drone logic from display logic

### 4. Template Method Pattern

**Where Used**: Simulation execution flow

**Implementation**:
- `initializeSimulation()`: Common initialization
- `executeSimulation()`: Mode-specific execution
- `displayResults()`: Common result display

**Benefit**: Defines algorithm structure, allows subclasses to override specific steps

### 5. Singleton Pattern (Implicit)

**Where Used**: Configuration reader instances

**Implementation**:
- Each simulator creates its own ConfigurationReader instance
- Not a strict singleton, but similar concept

**Benefit**: Encapsulates configuration parsing logic

### 6. Builder Pattern (Partial)

**Where Used**: Configuration object construction

**Implementation**:
```java
public static class Configuration {
    private final int mapWidth;
    private final int mapHeight;
    private final List<Drone> drones;
    
    public Configuration(int mapWidth, int mapHeight, List<Drone> drones) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.drones = drones;
    }
}
```

**Benefit**: Immutable configuration object with clear construction

### 7. Command Pattern

**Where Used**: Drone command execution

**Implementation**:
```java
public void executeCommand(char cmd, SimulatorMap map) {
    switch (cmd) {
        case 'L': turnLeft(); break;
        case 'R': turnRight(); break;
        case 'M': moveForward(map); break;
        case 'B': moveBackward(map); break;
    }
}
```

**Benefit**: Encapsulates commands as objects (characters in this case)

---

## Concurrency Implementation

### Thread Architecture

**Thread Types**:

1. **Main Thread**:
   - Coordinates simulation
   - Creates worker threads
   - Waits for completion
   - Displays results

2. **DroneWorker Threads** (one per drone):
   - Execute drone commands
   - Signal display thread
   - Handle interruption

3. **DisplayWorker Thread**:
   - Continuously refreshes display
   - Waits for signals
   - Shows real-time animation

### Synchronization Mechanisms

**ReentrantLock**:
```java
private ReentrantLock displayLock;
```
- Provides mutual exclusion
- Reentrant (same thread can acquire multiple times)
- More flexible than synchronized keyword
- Used for protecting shared display resources

**Condition Variable**:
```java
private java.util.concurrent.locks.Condition displayCondition;
```
- Allows threads to wait and signal
- Associated with a lock
- Used for producer-consumer pattern
- Drone threads signal, display thread waits

**CountDownLatch**:
```java
private CountDownLatch completionLatch;
```
- Allows main thread to wait for all drone threads
- Initialized with count equal to number of drones
- Each drone thread counts down on completion
- Main thread awaits latch reaching zero

**Volatile Boolean**:
```java
private volatile boolean running = true;
```
- Ensures visibility across threads
- Used for thread lifecycle control
- Changes immediately visible to all threads
- Prevents thread caching issues

### Thread Communication Flow

```
DroneWorker Thread          DisplayWorker Thread
       │                            │
       │ executeCommand()           │
       │                            │
       │ lock(displayLock)          │
       │ signal(displayCondition)   │
       │ unlock(displayLock)        │
       │───────────────────────────►│ await()
       │                            │
       │ sleep(200ms)               │
       │                            │ clearScreen()
       │                            │ displayMap()
       │                            │
       │ executeCommand()           │
       │                            │
       │ lock(displayLock)          │
       │ signal(displayCondition)   │
       │ unlock(displayLock)        │
       │───────────────────────────►│ await()
       │                            │
       │ ...                        │
       │                            │
       │ (completion)               │
       │ countdownLatch.countDown() │
       │                            │
```

### Race Condition Prevention

**Potential Race Conditions**:
1. Multiple drones accessing same map cell
2. Display thread reading while drones writing
3. Concurrent modification of drone positions

**Prevention Strategies**:
1. **Map Access**: Drones check obstacles but don't modify shared map structure
2. **Display Synchronization**: Lock protects display operations
3. **Position Updates**: Each drone has its own position field (no shared state)

**Why No Map Lock**:
- Map is read-only after initialization (obstacles don't change)
- Drones only read obstacles, don't modify them
- Each drone's position is independent
- Display creates copy for rendering

### Thread Lifecycle Management

**Startup Sequence**:
1. Main thread creates all worker threads
2. Main thread starts display thread first
3. Main thread starts all drone threads
4. Main thread waits for completion

**Shutdown Sequence**:
1. Main thread calls `stopSimulation()`
2. Sets running flag to false on all workers
3. Interrupts all worker threads
4. Main thread waits for all threads to join
5. Display thread shows final state
6. Resources cleaned up

**Interruption Handling**:
```java
try {
    // Thread work
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
    // Cleanup
}
```
- Always re-interrupt thread
- Perform cleanup in catch block
- Exit gracefully

---

## Network Communication

### Protocol Design

**Client → Server Protocol**:
```
Line 1: "<width> <height>"           // Map dimensions
Line 2: "<num_drones>"              // Number of drones
Line 3-N: "<x> <y> <orientation> <commands>"  // Drone data
```

**Server → Client Protocol**:
```
Line 1: "SUCCESS" or "ERROR: <message>"  // Status
Line 2-N: "<x> <y> <orientation>"        // Final positions (if SUCCESS)
```

**Design Rationale**:
- Simple text-based protocol
- Easy to debug (human-readable)
- Line-based for simplicity
- Minimal overhead
- No need for binary serialization

### Socket Implementation

**Server Socket**:
```java
ServerSocket serverSocket = new ServerSocket(port);
Socket clientSocket = serverSocket.accept();
```
- Creates listening socket on specified port
- Blocks until client connects
- Returns client socket for communication

**Client Socket**:
```java
Socket socket = new Socket(host, port);
```
- Connects to server at specified host and port
- Throws exception if connection fails
- Returns connected socket for communication

### Stream Management

**Server Streams**:
```java
BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
```

**Client Streams**:
```java
PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
```

**Why These Streams**:
- `BufferedReader`: Efficient line-by-line reading
- `PrintWriter`: Convenient line printing with auto-flush
- `InputStreamReader/OutputStreamWriter`: Bridge between byte and character streams
- Auto-flush (true): Ensures data sent immediately

### Error Handling

**Network Errors**:
- `UnknownHostException`: Invalid host address
- `ConnectException`: Server not running or port blocked
- `SocketTimeoutException`: Connection timeout
- `IOException`: General I/O error

**Handling Strategy**:
- Catch specific exceptions
- Provide descriptive error messages
- Send error to client (server side)
- Display error to user (client side)
- Clean up resources in finally block

### Resource Cleanup

**Try-with-Resources**:
```java
try (Socket socket = new Socket(host, port);
     PrintWriter output = new PrintWriter(...);
     BufferedReader input = new BufferedReader(...)) {
    // Use resources
} // Automatically closed
```

**Benefits**:
- Automatic resource cleanup
- Even if exception occurs
- No need for explicit close() calls
- Prevents resource leaks

### Connection Model

**Thread-per-Client Model**:
- Each client connection gets its own thread
- Server accepts connections in main loop
- Handler thread processes client request
- Handler terminates after sending results
- Server ready for next client

**Why This Model**:
- Simple to implement
- Good for educational purposes
- Handles sequential clients well
- Each client isolated from others

**Alternative Models** (not implemented):
- Thread pool: Reuse threads for multiple clients
- NIO: Non-blocking I/O for scalability
- Async: Asynchronous processing

---

## Testing Strategy

### Test Structure

**Test Packages**:
```
src/test/java/com/drone/simulator/
├── SimulatorBasicTest.java
├── client/
│   └── DroneClientTest.java
├── concurrent/
│   └── SimulatorConcurrentTest.java
└── server/
    └── DroneServerTest.java
```

### Test Categories

**Unit Tests**:
- Test individual classes in isolation
- Mock dependencies if needed
- Fast execution
- High coverage of business logic

**Integration Tests**:
- Test interaction between components
- Use real dependencies
- Slower execution
- Test workflows

**Network Tests**:
- Test client-server communication
- Require running server
- Test protocol compliance
- Test error handling

### Test Implementation

**JUnit 4 Features Used**:
- `@Test`: Marks test methods
- `@Before`: Setup before each test
- `@After`: Cleanup after each test
- `assertEquals`: Assertion for equality
- `assertTrue`: Assertion for boolean conditions
- `@Test(expected=...)`: Exception testing

### Example Test Cases

**Drone Movement Test**:
```java
@Test
public void testMoveForward() {
    Drone drone = new Drone(5, 5, 'N', "M");
    SimulatorMap map = new SimulatorMap(10, 10);
    drone.executeCommand('M', map);
    assertEquals(5, drone.getX());
    assertEquals(6, drone.getY());
}
```

**Configuration Parsing Test**:
```java
@Test
public void testReadConfiguration() throws IOException {
    ConfigurationReader reader = new ConfigurationReader();
    Configuration config = reader.readConfiguration("test_config.txt");
    assertEquals(20, config.getMapWidth());
    assertEquals(20, config.getMapHeight());
    assertEquals(2, config.getDrones().size());
}
```

### Testing Challenges

**Concurrent Testing**:
- Non-deterministic execution order
- Timing-dependent behavior
- Thread scheduling variability

**Solutions**:
- Use CountDownLatch for synchronization in tests
- Add delays for thread coordination
- Test final states rather than intermediate states
- Run tests multiple times for consistency

**Network Testing**:
- Requires running server
- Port conflicts
- Firewall issues

**Solutions**:
- Use random ports for tests
- Start/stop server in test lifecycle
- Mock network components for unit tests
- Integration tests with real server

---

## Build and Deployment

### Maven Build Lifecycle

**Phases**:
1. **clean**: Remove build artifacts
2. **compile**: Compile source code
3. **test**: Run unit tests
4. **package**: Create JAR file
5. **install**: Install to local repository
6. **deploy**: Deploy to remote repository

**Common Commands**:
```bash
mvn clean compile      # Compile only
mvn clean test         # Compile and test
mvn clean package      # Create JAR
mvn clean install      # Install to local repo
```

### JAR Creation

**Manifest Configuration**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <configuration>
        <archive>
            <manifest>
                <mainClass>com.drone.simulator.DroneSimulatorMain</mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```

**Result**: Executable JAR with main class specified

### Deployment Options

**Local Deployment**:
- Copy JAR to target machine
- Ensure Java 11+ installed
- Run with `java -jar drone-simulator.jar`

**Server Deployment**:
- Copy JAR to server
- Run server mode: `java -jar drone-simulator.jar server`
- Configure firewall for port access
- Set up systemd service for auto-start

**Docker Deployment** (potential future):
- Create Dockerfile with Java runtime
- Copy JAR to container
- Expose port for server mode
- Run container with docker run

### Distribution

**Distribution Package**:
```
drone-simulator-1.0.zip
├── drone-simulator-1.0-SNAPSHOT.jar
├── README.md
├── USER_GUIDE.md
├── PROJECT_REALIZATION.md
├── config_example.txt
└── LICENSE
```

**Delivery Methods**:
- Direct download
- Maven Central (if published)
- Git repository
- Learning management system

---

## Challenges and Solutions

### Challenge 1: Thread Synchronization

**Problem**: Multiple threads accessing shared resources could cause race conditions.

**Solution**:
- Used ReentrantLock for mutual exclusion
- Condition variable for thread signaling
- Careful design to minimize shared state
- Each drone maintains independent position

**Lesson Learned**: Minimize shared state in concurrent programs.

### Challenge 2: Display Flickering

**Problem**: Rapid screen clearing caused flickering in concurrent mode.

**Solution**:
- ANSI escape codes for screen clearing
- 200ms delay between movements
- Single display thread for consistency
- Timeout in display worker ensures updates

**Lesson Learned**: Balance between real-time updates and visual quality.

### Challenge 3: Network Reliability

**Problem**: Network connections could fail at any point.

**Solution**:
- Comprehensive error handling
- Descriptive error messages
- Resource cleanup in finally blocks
- Try-with-resources for automatic cleanup

**Lesson Learned**: Always handle network failures gracefully.

### Challenge 4: Configuration Validation

**Problem**: Invalid configuration could cause crashes or undefined behavior.

**Solution**:
- Validate all input parameters
- Provide descriptive error messages
- Use exceptions for error propagation
- Validate orientation and command characters

**Lesson Learned**: Validate input early and fail fast with clear messages.

### Challenge 5: Obstacle Generation

**Problem**: Random obstacles might block all paths or make simulation impossible.

**Solution**:
- Remove obstacles at drone starting positions
- 15% density provides reasonable obstacle count
- Drones simply stay in place if blocked
- Wrapping provides alternative paths

**Lesson Learned**: Balance randomness with playability.

### Challenge 6: Testing Concurrent Code

**Problem**: Concurrent code is non-deterministic and hard to test.

**Solution**:
- Test final states rather than intermediate
- Use synchronization primitives in tests
- Add delays for thread coordination
- Run tests multiple times for consistency

**Lesson Learned**: Focus on observable outcomes, not execution order.

---

## Future Improvements

### Potential Enhancements

**1. GUI Interface**
- Add graphical user interface using JavaFX or Swing
- Interactive map with mouse controls
- Real-time animation with smooth graphics
- Configuration editor with visual feedback

**2. Advanced Drone Behaviors**
- Collision detection between drones
- Drone-to-drone communication
- Dynamic pathfinding algorithms
- Fuel/battery constraints

**3. Enhanced Map Features**
- Different terrain types (water, buildings, parks)
- Variable movement costs
- Dynamic obstacles (moving vehicles)
- Weather effects

**4. Improved Network Features**
- Multiple simultaneous clients
- Authentication and authorization
- Encryption (SSL/TLS)
- REST API for integration

**5. Performance Optimizations**
- Thread pool for client handling
- NIO for non-blocking I/O
- Caching of frequently used data
- Database persistence for simulations

**6. Analytics and Reporting**
- Simulation statistics and metrics
- Export results to CSV/JSON
- Visualization of drone paths
- Performance benchmarking

**7. Testing Improvements**
- Increased test coverage
- Property-based testing
- Load testing for server
- Automated integration tests

**8. Documentation**
- API documentation with Javadoc
- Video tutorials
- Interactive examples
- Contribution guidelines

### Technical Debt

**Areas for Improvement**:
- Add more comprehensive logging
- Implement proper logging framework (SLF4J, Logback)
- Add configuration file validation schema
- Improve error messages with context
- Add internationalization support
- Refactor large methods into smaller units

### Scalability Considerations

**Current Limitations**:
- Single-threaded server (sequential clients)
- In-memory map (no persistence)
- No horizontal scaling
- Limited to small maps

**Scalability Solutions**:
- Distributed architecture with message queues
- Database-backed map storage
- Microservices for different components
- Load balancing for multiple server instances

---

## Conclusion

### Project Summary

The Drone Simulator project successfully demonstrates:
- Object-oriented programming in Java
- Concurrent programming with threads
- Network programming with sockets
- N-Tiers architecture
- Software engineering best practices

### Key Achievements

**Technical**:
- Three working execution modes
- Thread-safe concurrent implementation
- Reliable client-server communication
- Comprehensive error handling
- Well-tested codebase

**Educational**:
- Clear demonstration of advanced concepts
- Well-documented code
- Progressive complexity (basic → concurrent → network)
- Real-world applicable scenario

### Lessons Learned

**Development**:
- Start simple, add complexity gradually
- Test early and often
- Document as you code
- Handle errors gracefully
- Design for extensibility

**Concurrency**:
- Minimize shared state
- Use appropriate synchronization primitives
- Handle interruption properly
- Test final states, not execution order
- Consider thread lifecycle

**Network Programming**:
- Validate all input
- Handle connection failures
- Clean up resources properly
- Design simple protocols
- Provide clear error messages

### Project Value

This project provides significant educational value by:
- Bridging theory and practice
- Demonstrating real-world applications
- Teaching industry-relevant skills
- Providing a foundation for more complex systems
- Serving as a reference for future projects

### Acknowledgments

- Professor A. Marzouk for guidance and supervision
- FSTS (Faculté des Sciences et Techniques) for the learning opportunity
- The open-source community for Java and Maven tools

---

**Document Version**: 1.0  
**Last Updated**: 2026  
**Project**: Drone Fleet Simulator (M1 RSI 2025-2026)  
**Author**: Project Team

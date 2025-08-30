# Build Your Own Garden (BYOG) - Complete Project Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Architecture Overview](#architecture-overview)
3. [Core Classes Documentation](#core-classes-documentation)
4. [Tile Engine Documentation](#tile-engine-documentation)
5. [Utility Classes Documentation](#utility-classes-documentation)
6. [Testing Documentation](#testing-documentation)
7. [Game Mechanics](#game-mechanics)
8. [World Generation Algorithm](#world-generation-algorithm)
9. [Performance Characteristics](#performance-characteristics)
10. [Development Guidelines](#development-guidelines)

## Project Overview

Build Your Own Garden (BYOG) is a sophisticated 2D procedural world generation game built in Java. The project demonstrates advanced programming concepts including procedural generation, dual-avatar systems, dimensional gameplay, and persistent state management.

### Key Features
- **Procedural World Generation**: Deterministic worlds based on user seeds
- **Dual Avatar System**: Simultaneous control of gardener and duck characters
- **Carrot World Dimension**: Special picnic table environment for carrot collection
- **Persistent Save System**: Complete game state preservation
- **Mathematical Precision**: Advanced algorithms for room placement and connectivity
- **Beautiful UI**: Rose pattern menu system with mathematical curves

## Architecture Overview

The project follows a modular architecture with clear separation of concerns:

```
BYOG Project Structure
├── Core Game Logic (core/)
│   ├── Main.java              # Application entry point
│   ├── Game.java              # Main game controller
│   ├── World.java             # World generation engine
│   ├── AvatarMoves.java       # Avatar movement system
│   ├── CarrotMoves.java       # Carrot world movement
│   ├── CarrotWorld.java       # Picnic table dimension
│   ├── Coord.java             # Coordinate system
│   └── PythagoreanTree.java   # Room center storage
├── Rendering Engine (tileengine/)
│   ├── TERenderer.java        # 2D tile renderer
│   ├── TETile.java            # Individual tile representation
│   └── Tileset.java           # Tile definitions
├── Utilities (utils/)
│   ├── FileUtils.java         # File I/O operations
│   └── RandomUtils.java       # Random number generation
└── Testing (tests/)
    └── WorldGenTests.java     # JUnit test suite
```

## Core Classes Documentation

### Main.java
**Purpose**: Application entry point and initialization
**Key Responsibilities**:
- Initialize the tile renderer
- Launch the title screen
- Start the keyboard input handler

**Design Pattern**: Singleton entry point
**Dependencies**: Game, TERenderer, Coord

### Game.java
**Purpose**: Central game controller and state manager
**Key Responsibilities**:
- Game loop management
- Keyboard input processing
- Save/load functionality
- Menu system rendering
- Carrot world transitions

**Design Pattern**: Controller pattern
**Dependencies**: All core classes, TERenderer, StdDraw

**Key Methods**:
- `runGame()`: Main game loop
- `moveAvatar()`: Input processing for both avatars
- `saveGame()`: Game state persistence
- `loadGame()`: Game state restoration
- `drawRose()`: Mathematical rose pattern generation

### World.java
**Purpose**: Procedural world generation engine
**Key Responsibilities**:
- Room generation and placement
- Hallway connectivity
- Terrain generation
- Carrot distribution

**Design Pattern**: Builder pattern
**Dependencies**: TETile, Tileset, PythagoreanTree, Random

**World Generation Algorithm**:
1. **Background Initialization**: Fill with grass tiles
2. **Room Generation**: Random placement with size constraints (13x13 to 30x30)
3. **Room Validation**: Ensure no overlaps or edge touching
4. **Hallway Connection**: L-shaped paths between room centers
5. **Wall Generation**: Decorative flower walls around hallways
6. **Carrot Placement**: Random distribution in walkable areas

**Key Methods**:
- `roomMaker()`: Main room generation loop
- `makeHallways()`: Connect all rooms with hallways
- `pickCarrot()`: Distribute collectible carrots

### AvatarMoves.java
**Purpose**: Avatar movement and positioning system
**Key Responsibilities**:
- Movement validation and collision detection
- Avatar spawning in valid locations
- Coordinate tracking for both avatars
- Loading saved positions

**Design Pattern**: Component pattern
**Dependencies**: TETile, Tileset, Coord, Random

**Movement System**:
- **Gardener Avatar**: WASD controls, can collect carrots
- **Duck Avatar**: IJKL controls, cannot access carrot world
- **Collision Detection**: Prevents movement through walls
- **Tile Restoration**: Maintains world state during movement

### CarrotMoves.java
**Purpose**: Special movement system for carrot world dimension
**Key Responsibilities**:
- Restricted movement on plate tiles only
- Carrot collection and counting
- Synchronized movement with main world avatar

**Design Pattern**: Strategy pattern
**Dependencies**: TETile, Tileset, Coord

**Carrot World Mechanics**:
- Movement restricted to plates, carrots, and stems
- Automatic carrot collection when moving over orange tiles
- Counter tracking for collected carrots

### CarrotWorld.java
**Purpose**: Picnic table dimension generation and management
**Key Responsibilities**:
- Checkerboard tablecloth generation
- Table setting creation (plate, fork, napkin)
- Triangular carrot generation with mathematical precision
- Carrot respawning system

**Design Pattern**: Factory pattern
**Dependencies**: TETile, Tileset, Coord, Random

**Mathematical Features**:
- **Rose Pattern**: Mathematical rose curves for menu background
- **Triangular Carrots**: Point-in-triangle testing using barycentric coordinates
- **Circular Plates**: Distance-based circle generation
- **Geometric Precision**: Trigonometric calculations for shape generation

### Coord.java
**Purpose**: 2D coordinate representation with unique identifiers
**Key Responsibilities**:
- Position tracking for game objects
- Unique identification for data structures
- Mathematical coordinate operations

**Design Pattern**: Value object pattern
**Dependencies**: None

### PythagoreanTree.java
**Purpose**: Specialized data structure for room center storage
**Key Responsibilities**:
- Efficient room center storage and retrieval
- Automatic sorting by distance from origin
- Collision handling for coordinate uniqueness

**Design Pattern**: Decorator pattern (extends TreeMap)
**Dependencies**: TreeMap, Coord

**Algorithm**:
- Uses Pythagorean theorem (√(x² + y²)) as natural ordering key
- Handles coordinate collisions by adjusting keys
- Ensures proper room spacing for hallway generation

## Tile Engine Documentation

### TERenderer.java
**Purpose**: 2D tile rendering system
**Key Features**:
- 16x16 pixel tile rendering
- Double buffering for smooth animation
- Configurable window sizing and offsets
- Efficient frame rendering

**Rendering Pipeline**:
1. Clear background
2. Iterate through tile array
3. Draw each tile at calculated screen position
4. Display buffer

### TETile.java
**Purpose**: Individual tile representation
**Key Features**:
- Character and image-based rendering
- Color management and variant generation
- Immutable design for thread safety
- Fallback rendering for missing images

**Tile Properties**:
- Character representation
- Text and background colors
- Description for UI display
- Optional image file path

### Tileset.java
**Purpose**: Predefined tile constants and definitions
**Tile Categories**:
- **Avatars**: Gardener and duck characters
- **Terrain**: Walls, floors, grass, dirt
- **Collectibles**: Carrots and interactive items
- **Table Setting**: Plates, utensils, napkins
- **Decorative**: Flowers and visual elements

## Utility Classes Documentation

### FileUtils.java
**Purpose**: File I/O operations for save/load functionality
**Key Methods**:
- `writeFile()`: Write contents to file
- `readFile()`: Read file contents
- `fileExists()`: Check file existence

**Error Handling**: RuntimeException wrapping for IOException

### RandomUtils.java
**Purpose**: Comprehensive random number generation library
**Distributions Available**:
- Uniform (discrete and continuous)
- Gaussian (normal distribution)
- Bernoulli (binary outcomes)
- Geometric, Poisson, Pareto, Cauchy
- Exponential and discrete distributions

**Additional Features**:
- Array shuffling algorithms
- Permutation generation
- Statistical validation

## Testing Documentation

### WorldGenTests.java
**Purpose**: Comprehensive testing suite for world generation and game mechanics
**Test Categories**:
- **Basic Tests**: World generation with specific seeds
- **Interactive Tests**: Avatar movement and interaction validation
- **Save/Load Tests**: Game state persistence verification
- **Automated Tests**: AutograderBuddy integration for automated validation

**Testing Approach**:
- Visual inspection for world generation
- Automated validation for save/load consistency
- Input sequence testing for game mechanics
- Regression testing for world generation algorithms

## Game Mechanics

### Control System
- **Gardener Avatar**: WASD movement, carrot collection capability
- **Duck Avatar**: IJKL movement, exploration only
- **Special Commands**: `:` for menu access (N=New, Q=Save/Quit, L=Load)

### Game Flow
1. **Title Screen**: Rose pattern background with game options
2. **Seed Input**: User enters world generation seed
3. **World Generation**: Procedural creation of rooms and hallways
4. **Gameplay**: Dual avatar exploration and interaction
5. **Carrot Collection**: Transport to picnic table dimension
6. **Save System**: Automatic state preservation

### Carrot World Mechanics
- **Access**: Triggered by moving over carrots in main world
- **Duration**: 12 seconds (120 frames at 10 FPS)
- **Environment**: Picnic table with triangular carrots
- **Collection**: Move over orange carrots to collect
- **Return**: Automatic return to main world after timer

## World Generation Algorithm

### Room Generation
1. **Random Placement**: Generate random coordinates and dimensions
2. **Validation**: Check for overlaps, edge touching, and world boundaries
3. **Room Creation**: Build walls and floors with proper spacing
4. **Center Marking**: Store room center for hallway connection

### Hallway Generation
1. **Room Ordering**: Sort rooms by distance from origin
2. **Sequential Connection**: Connect each room to the next
3. **Path Calculation**: Determine L-shaped hallway paths
4. **Wall Addition**: Decorative walls around hallway segments

### Connectivity Guarantee
- **Algorithm**: Sequential room connection ensures full connectivity
- **Path Types**: L-shaped hallways for efficient routing
- **Validation**: All rooms accessible from any starting point

## Performance Characteristics

### Memory Usage
- **World Size**: 100x60 tiles = 6,000 total tiles
- **Tile Storage**: Efficient 2D array representation
- **Coordinate System**: Optimized for quick lookups

### Rendering Performance
- **Frame Rate**: 10 FPS (100ms pause between frames)
- **Tile Updates**: Only modified tiles are re-rendered
- **Memory Management**: Efficient tile object reuse

### Generation Speed
- **World Creation**: Sub-second generation for most seeds
- **Room Placement**: Intelligent collision detection
- **Hallway Algorithm**: Efficient connectivity algorithm

## Development Guidelines

### Code Organization
- **Package Structure**: Clear separation of concerns
- **Class Responsibilities**: Single responsibility principle
- **Method Documentation**: Comprehensive JavaDoc comments
- **Error Handling**: Graceful fallbacks and user feedback

### Best Practices
- **Immutability**: TETile objects are immutable for thread safety
- **Deterministic Generation**: Same seed always produces identical world
- **Efficient Algorithms**: Optimized room placement and hallway generation
- **User Experience**: Intuitive controls and visual feedback

### Extensibility
- **Tile System**: Easy addition of new tile types
- **World Generation**: Modular algorithm components
- **Game Mechanics**: Pluggable movement and interaction systems
- **Rendering**: Configurable display options

### Testing Strategy
- **Unit Tests**: Individual component validation
- **Integration Tests**: System interaction verification
- **Visual Tests**: World generation inspection
- **Automated Tests**: Regression testing with AutograderBuddy

## Future Enhancements

### Potential Improvements
- **Scrolling World**: Larger world sizes with camera movement
- **More Tile Types**: Additional terrain and decorative elements
- **Sound Effects**: Audio feedback for interactions
- **Animation System**: Smooth character movement
- **Multiplayer Support**: Cooperative or competitive play
- **Level Progression**: Increasing difficulty and complexity

### Code Optimizations
- **Spatial Partitioning**: Improved collision detection
- **Lazy Loading**: Dynamic world generation
- **Memory Pooling**: Reduced object allocation
- **Parallel Generation**: Multi-threaded world creation

## Conclusion

The Build Your Own Garden project represents a sophisticated implementation of procedural generation, game development, and software architecture principles. The codebase demonstrates advanced Java programming concepts while maintaining clean, readable, and well-documented code.

The project's success lies in its:
- **Modular Architecture**: Clear separation of concerns
- **Mathematical Precision**: Advanced algorithms for generation
- **User Experience**: Intuitive controls and beautiful visuals
- **Extensibility**: Easy to modify and enhance
- **Documentation**: Comprehensive code comments and project documentation

This project serves as an excellent example of how to build complex, interactive applications with proper software engineering practices.

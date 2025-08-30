# Build Your Own Garden (BYOG) - Comprehensive Documentation

## Project Overview

Build Your Own Garden (BYOG) is an interactive, procedurally generated 2D world exploration game built in Java. The game features a unique dual-avatar system where players control both a gardener and a duck character, explore randomly generated worlds, and interact with a special "carrot world" dimension.

This project was developed as part of the Data Structures course at UC Berkeley, demonstrating advanced programming concepts including procedural generation, dual-avatar systems, dimensional gameplay, and persistent state management.

## Visual Preview

### Initial Menu Page
<img width="503" alt="flower" src="https://github.com/azaleambailey/BuildYourOwnWorld/assets/110420935/569bc8d5-f0ca-4f98-9a49-246265f5b717">

### Enter Seed Page
<img width="500" alt="enter_seed" src="https://github.com/azaleambailey/BuildYourOwnWorld/assets/110420935/783c83e9-075f-4f3b-b735-eaf8c5033403">

### The Game Interface
<img width="1506" alt="game" src="https://github.com/azaleambailey/BuildYourOwnWorld/assets/110420935/38d52e1d-88fa-4216-a784-6ae298ab5977">

### Carrot World
<img width="1499" alt="carrot_world" src="https://github.com/azaleambailey/BuildYourOwnWorld/assets/110420935/c404e47a-fc45-4216-ab9f-11828909356e">

## Features

### Core Gameplay
- **Procedural World Generation**: Each world is uniquely generated based on a user-provided seed
- **Dual Avatar System**: Control both a gardener (WASD keys) and a duck (IJKL keys) simultaneously
- **Interactive World**: Explore rooms, hallways, and outdoor areas with different tile types
- **Carrot Collection**: Gardener avatar can collect carrots to access a special dimension
- **Save/Load System**: Persistent game state with automatic saving and loading capabilities

### World Generation
- **Room Generation**: Randomly sized rooms (13x13 to 30x30 tiles) with flower walls
- **Hallway System**: Connected hallways ensure all rooms are accessible
- **Terrain Variety**: Multiple tile types including dirt floors, grass, and decorative elements
- **Deterministic**: Same seed always produces identical world layout

### Special Features
- **Carrot World**: A unique picnic table dimension accessible when collecting carrots
- **Visual Effects**: Beautiful rose pattern menu screen with animated elements
- **HUD Display**: Real-time tile information and carrot counter
- **Collision Detection**: Proper movement restrictions and wall interactions

## Technical Architecture

### Project Structure
```
proj3/
├── src/
│   ├── core/           # Main game logic and classes
│   ├── tileengine/     # Rendering engine and tile system
│   └── utils/          # Utility classes for file I/O and randomness
├── tests/              # JUnit test suite
├── out/                # Compiled bytecode
└── README.md           # This documentation
```

### Core Classes

#### `Main.java`
- Entry point for the application
- Initializes the game and title screen

#### `Game.java`
- Main game controller and state manager
- Handles keyboard input processing
- Manages game flow and rendering
- Implements save/load functionality
- Renders beautiful menu screens

#### `World.java`
- Procedural world generation engine
- Room placement and hallway connection algorithms
- Terrain generation and tile management
- Carrot placement system

#### `AvatarMoves.java`
- Movement logic for both gardener and duck avatars
- Collision detection and valid move validation
- Avatar spawning and positioning

#### `CarrotMoves.java`
- Special movement system for carrot world dimension
- Carrot collection mechanics
- Avatar positioning in picnic table environment

#### `CarrotWorld.java`
- Picnic table dimension generation
- Table setting creation (plates, forks, napkins)
- Triangular carrot generation with mathematical precision

#### `Coord.java`
- Coordinate system for 2D positioning
- Unique identifier system for tracking objects

#### `PythagoreanTree.java`
- Data structure for room center storage
- Ensures proper room spacing and connectivity

### Tile Engine

#### `TERenderer.java`
- 2D tile rendering system
- Window management and display scaling
- Efficient frame rendering with double buffering

#### `TETile.java`
- Individual tile representation
- Support for both character and image-based tiles
- Color management and variant generation

#### `Tileset.java`
- Predefined tile constants
- Visual assets for game elements
- Color scheme definitions

### Utility Classes

#### `FileUtils.java`
- File I/O operations for save/load functionality
- Error handling and exception management

#### `RandomUtils.java`
- Comprehensive random number generation library
- Multiple distribution types (uniform, gaussian, poisson, etc.)
- Array shuffling and permutation utilities

## How to Play

### Starting the Game
1. Run `Main.main()` to launch the application
2. Press `N` for a new game
3. Enter a random seed (any sequence of numbers)
4. Press `S` to start the game

### Controls

#### Gardener Avatar (WASD)
- `W` - Move up
- `A` - Move left
- `S` - Move down
- `D` - Move right

#### Duck Avatar (IJKL)
- `I` - Move up
- `J` - Move left
- `K` - Move down
- `L` - Move right

#### Special Commands
- `:` - Access command menu
  - `N` - New game
  - `Q` - Save and quit
  - `L` - Load saved game

### Gameplay Mechanics

#### World Exploration
- Navigate through procedurally generated rooms and hallways
- Avoid walls (flower tiles) and stay within walkable areas
- Discover carrots scattered throughout the world

#### Carrot Collection
- When the gardener avatar moves over a carrot, they're transported to "carrot world"
- Carrot world is a picnic table environment with a triangular carrot to eat
- After eating the carrot, return to the main world after a brief timer
- Carrots respawn after collection

#### Save System
- Game automatically saves avatar positions and remaining carrots
- Use `:Q` to save and quit
- Use `L` to load from the last save point

## Technical Implementation Details

### World Generation Algorithm
1. **Background Filling**: Initialize world with grass tiles
2. **Room Generation**: Randomly place rooms with size constraints
3. **Room Validation**: Ensure rooms don't overlap or touch edges
4. **Hallway Connection**: Connect room centers using L-shaped paths
5. **Wall Generation**: Add decorative walls around hallways
6. **Carrot Placement**: Randomly distribute carrots in walkable areas

### Rendering System
- **Tile-Based Graphics**: 16x16 pixel tiles for consistent visual style
- **Double Buffering**: Smooth animation and rendering
- **Coordinate System**: Y-axis inverted for proper screen positioning
- **Image Support**: PNG files for enhanced visual elements

### Save/Load System
- **File Format**: CSV-style text file with coordinates and state
- **Persistence**: Maintains game state between sessions
- **Error Handling**: Graceful fallback for missing save files

## Development and Testing

### Building the Project
```bash
# Compile the project
javac -cp "lib/*" src/**/*.java

# Run the main class
java -cp "src:lib/*" core.Main
```

### Testing
- JUnit test suite included in `tests/` directory
- Test world generation with different seeds
- Validate save/load functionality
- Interactive testing with visual output

### Dependencies
- **Princeton Standard Library**: Core algorithms and data structures
- **JUnit 5**: Testing framework
- **Google Truth**: Assertion library for tests

## Performance Characteristics

### Memory Usage
- **World Size**: 100x60 tiles (6,000 total tiles)
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

## Troubleshooting

### Common Issues
1. **Game Won't Start**: Ensure Java 8+ is installed
2. **Save File Errors**: Check file permissions in project directory
3. **Rendering Issues**: Verify image files are in correct locations
4. **Performance Problems**: Reduce world size or increase frame delay

### Debug Mode
- Enable console output for detailed generation information
- Use test suite for isolated functionality testing
- Validate world generation with known seeds

## License and Attribution

This project was developed as an educational exercise in procedural generation, game development, and software architecture. The codebase demonstrates advanced Java programming concepts including:

- Object-oriented design principles
- Algorithm implementation and optimization
- User interface development
- File I/O and persistence
- Testing and debugging methodologies

## Contact and Support

For questions about the implementation or suggestions for improvements, please refer to the code comments and documentation within each class file. The project serves as an excellent example of how to build complex, interactive applications with proper software engineering practices.


package core;

import tileengine.TETile;
import tileengine.Tileset;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.abs;

/**
 * Procedural world generation engine for the Build Your Own Garden game.
 * 
 * This class is responsible for creating the main game world including:
 * - Random room generation with size constraints
 * - Hallway connection system ensuring all rooms are accessible
 * - Terrain generation with multiple tile types
 * - Carrot placement throughout the world
 * - Deterministic generation based on user-provided seeds
 * 
 * The world generation algorithm creates a connected network of rooms
 * and hallways that guarantees players can reach all areas.
 * 
 * @author Azalea Bailey
 * @version 1.0
 */
public class World {
    
    // World dimensions and generation parameters
    int width = 100;           // Width of the world in tiles
    int height = 60;           // Height of the world in tiles
    int maxRoomSize = 30;      // Maximum room size (width/height)
    int minRoomSize = 13;      // Minimum room size (width/height)
    int maxFalse = 10000;      // Maximum attempts for room placement
    
    // Tile type definitions
    TETile wall = Tileset.FLOWER;      // Room and hallway walls
    TETile floor = Tileset.DIRT;       // Room floors
    TETile hall = Tileset.HALLDIRT;    // Hallway floors
    TETile outside = Tileset.GRASS;    // Background terrain
    TETile carrot = Tileset.CARROT;    // Collectible items
    
    // World state and generation
    long seed;                         // Random seed for deterministic generation
    TETile[][] world;                 // 2D array representing the world
    Random rand;                       // Random number generator
    int numFalse;                      // Counter for failed room placement attempts
    PythagoreanTree roomCenters;       // Data structure storing room center coordinates
    ArrayList<Coord> carrotCoord;      // List of carrot locations

    /**
     * Constructor that generates a complete world based on the provided seed.
     * 
     * The generation process follows these steps:
     * 1. Initialize the world with grass background
     * 2. Generate rooms with random sizes and positions
     * 3. Connect all rooms with hallways
     * 4. Place carrots throughout the world
     * 
     * @param input The seed string for world generation
     */
    public World(String input) {
        world = new TETile[width][height];
        roomCenters = new PythagoreanTree();
        this.seed = parseInput(input);
        rand = new Random(this.seed);
        numFalse = 0;

        fillBackground();
        roomMaker(rand.nextInt(width), rand.nextInt(height));
        makeHallways();
    }

    /**
     * Parses the input seed string and converts it to a long value.
     * 
     * Handles different input formats:
     * - Direct seed numbers
     * - Seeds prefixed with 'n' or 'N'
     * 
     * @param input The seed string from user input
     * @return The parsed seed as a long value
     */
    public long parseInput(String input) {
        if (input.charAt(0) != ('n') && input.charAt(0) != 'N') {
            this.seed = Long.parseLong(input.substring(0, input.length() - 1));
        } else {
            this.seed = Long.parseLong(input.substring(1, input.length() - 1));
        }
        return this.seed;
    }

    /**
     * Randomly distributes carrots throughout the world.
     * 
     * Places 2-7 carrots in random walkable locations (dirt floors).
     * Carrots are stored with their coordinates for tracking during gameplay.
     */
    public void pickCarrot() {
        carrotCoord = new ArrayList<>();
        int x = rand.nextInt(width);
        int y = rand.nextInt(height);
        int carrotNum = rand.nextInt(7 - 2 + 1) + 2;  // Random number between 2-7

        for (int i = 0; i < carrotNum; i++) {
            // Find a valid location (dirt floor) for each carrot
            while (world[x][y] != floor) {
                x = rand.nextInt(width);
                y = rand.nextInt(height);
            }
            world[x][y] = carrot;
            carrotCoord.add(new Coord(x * x + y * y, x, y));
        }
    }

    /**
     * Fills the entire world with grass background tiles.
     * 
     * This creates the base terrain that rooms and hallways will be built upon.
     */
    public void fillBackground() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = outside;
            }
        }
    }

    /**
     * Main room generation loop that attempts to place rooms until the limit is reached.
     * 
     * Continues generating rooms as long as the number of failed attempts
     * is below the maximum threshold. This ensures the world has a good
     * balance of rooms while preventing infinite loops.
     * 
     * @param coordX Initial random X coordinate for room placement
     * @param coordY Initial random Y coordinate for room placement
     */
    public void roomMaker(int coordX, int coordY) {
        while (numFalse < maxFalse) {
            roomMakerHelper(coordX, coordY);
            coordX = rand.nextInt(width);
            coordY = rand.nextInt(height);
        }
    }

    /**
     * Attempts to create a single room at the specified coordinates.
     * 
     * Generates a room with random dimensions within the size constraints.
     * If the room placement is valid, it creates the room structure.
     * If invalid, increments the failure counter.
     * 
     * @param coordX X coordinate for room placement
     * @param coordY Y coordinate for room placement
     */
    public void roomMakerHelper(int coordX, int coordY) {
        // Generate random room dimensions within constraints
        int roomW = rand.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;
        int roomH = rand.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;

        // Calculate room boundaries with padding
        int xMin = coordX + 4;
        int xMax = coordX + roomW - 4;
        int yMin = coordY + 4;
        int yMax = coordY + roomH - 4;

        ArrayList<int[]> wallTiles = new ArrayList<>();
        
        // Check if room placement is valid
        if (makeRoomOrNot(coordX, coordY, roomW, roomH)) {
            // Create room walls
            for (int x = xMin; x < xMax; x++) {
                for (int y = yMin; y < yMax; y++) {
                    world[x][y] = wall;
                    wallTiles.add(new int[]{x, y});
                    makeFloor(xMin, xMax, yMin, yMax);
                }
            }
            // Mark room center for hallway connection
            pickDirt(xMin + 1, xMax - 2, yMin + 1, yMax - 2);
        } else {
            numFalse += 1;
        }
    }

    /**
     * Creates the floor tiles within a room.
     * 
     * Fills the interior of a room with dirt floor tiles,
     * leaving the walls intact around the perimeter.
     * 
     * @param xMin Minimum X coordinate of room interior
     * @param xMax Maximum X coordinate of room interior
     * @param yMin Minimum Y coordinate of room interior
     * @param yMax Maximum Y coordinate of room interior
     */
    public void makeFloor(int xMin, int xMax, int yMin, int yMax) {
        for (int x = xMin + 1; x < xMax - 1; x++) {
            for (int y = yMin + 1; y < yMax - 1; y++) {
                world[x][y] = floor;
            }
        }
    }

    /**
     * Validates whether a room can be placed at the specified location.
     * 
     * Room placement rules:
     * - Room must not touch world edges
     * - Room must not overlap with existing rooms or hallways
     * - Room must fit entirely within world boundaries
     * 
     * @param coordX X coordinate for room placement
     * @param coordY Y coordinate for room placement
     * @param roomW Width of the room to place
     * @param roomH Height of the room to place
     * @return true if room placement is valid, false otherwise
     */
    public boolean makeRoomOrNot(int coordX, int coordY, int roomW, int roomH) {
        // Check each tile in the proposed room area
        for (int x = coordX; x < coordX + roomW; x++) {
            for (int y = coordY; y < coordY + roomH; y++) {
                boolean one = world[x][y].equals(wall);
                boolean two = world[x][y].equals(floor);
                
                // Room is invalid if it touches edges or overlaps existing structures
                if (x == 0 || y == 0 || x >= width - 1 || y >= height - 1 || one || two) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Selects and stores a random dirt tile location as a room center.
     * 
     * Room centers are used for hallway connection. This method ensures
     * that room centers are properly spaced to avoid overlapping hallways.
     * 
     * @param xMin Minimum X coordinate for selection
     * @param xMax Maximum X coordinate for selection
     * @param yMin Minimum Y coordinate for selection
     * @param yMax Maximum Y coordinate for selection
     */
    public void pickDirt(int xMin, int xMax, int yMin, int yMax) {
        int x = rand.nextInt(xMax - xMin + 1) + xMin;
        int y = rand.nextInt(yMax - yMin + 1) + yMin;
        boolean flip = pickDirtValid(x, y);
        
        // Keep trying until a valid location is found
        while (!flip) {
            x = rand.nextInt(xMax - xMin + 1) + xMin;
            y = rand.nextInt(yMax - yMin + 1) + yMin;
            flip = pickDirtValid(x, y);
        }
        roomCenters.putCoord(x, y);
    }

    /**
     * Checks if a proposed room center location is valid.
     * 
     * Ensures room centers are at least 2 tiles apart to prevent
     * overlapping hallways and maintain proper spacing.
     * 
     * @param x X coordinate of proposed room center
     * @param y Y coordinate of proposed room center
     * @return true if location is valid, false otherwise
     */
    public boolean pickDirtValid(int x, int y) {
        for (Coord c : roomCenters.values()) {
            if (abs(c.x - x) == 1 || abs(c.y - y) == 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates hallways connecting all rooms in the world.
     * 
     * Uses a simple algorithm that connects rooms sequentially:
     * 1. Takes the first two room centers
     * 2. Creates a hallway between them
     * 3. Moves to the next room and repeats
     * 
     * This ensures all rooms are connected in a chain, making the
     * entire world accessible to the player.
     */
    public void makeHallways() {
        // Get the first two room centers
        Map.Entry<Double, Coord> firstCoord = roomCenters.pollFirstEntry();
        Map.Entry<Double, Coord> secondCoord = roomCenters.pollFirstEntry();

        // Connect each room to the next one
        while (secondCoord != null) {
            makeHallwaysHelper(firstCoord, secondCoord);
            firstCoord = secondCoord;
            secondCoord = roomCenters.pollFirstEntry();
        }
    }

    /**
     * Creates a hallway between two room centers.
     * 
     * Determines the relative positions of the rooms and creates
     * an L-shaped hallway connecting them. The hallway consists of:
     * - A vertical segment from one room center
     * - A horizontal segment to the other room center
     * 
     * @param room1 First room center coordinates
     * @param room2 Second room center coordinates
     */
    public void makeHallwaysHelper(Map.Entry<Double, Coord> room1, Map.Entry<Double, Coord> room2) {
        int x1 = room1.getValue().x;
        int x2 = room2.getValue().x;
        int y1 = room1.getValue().y;
        int y2 = room2.getValue().y;

        // Determine relative positions and create appropriate hallway
        if (x1 < x2 && y1 < y2) {
            downAndLeft(x1, y1, x2, y2);
        }

        if (x2 < x1 && y1 < y2) {
            downAndRight(x1, y1, x2, y2);
        }

        if (x2 < x1 && y2 < y1) {
            upAndRight(x1, y1, x2, y2);
        }

        if (x1 < x2 && y2 < y1) {
            upAndLeft(x1, y1, x2, y2);
        }

        if (y1 == y2) {
            horizontal(y1, x1, x2);
        }

        if (x1 == x2) {
            vertical(x1, y1, y2);
        }
    }

    /**
     * Creates a hallway when the second room is up and to the left of the first.
     * 
     * Creates an L-shaped hallway:
     * 1. Vertical segment from second room center
     * 2. Horizontal segment to first room center
     * 3. Adds decorative walls around the hallway
     * 
     * @param x1 First room X coordinate
     * @param y1 First room Y coordinate
     * @param x2 Second room X coordinate
     * @param y2 Second room Y coordinate
     */
    public void upAndLeft(int x1, int y1, int x2, int y2) {
        // Vertical segment from second room
        for (int y = y2; y < y1; y++) {
            world[x2][y] = hall;
            makeWall(x2, y, true);
        }
        
        // Horizontal segment to first room
        for (int x = x1; x < x2 + 1; x++) {
            world[x][y1] = hall;
            makeWall(y1, x, false);
        }

        // Add corner walls
        makeWallCorners(x2 + 1, y1, true);
    }

    /**
     * Creates a hallway when the second room is up and to the right of the first.
     * 
     * Similar to upAndLeft but for the opposite diagonal direction.
     * 
     * @param x1 First room X coordinate
     * @param y1 First room Y coordinate
     * @param x2 Second room X coordinate
     * @param y2 Second room Y coordinate
     */
    public void upAndRight(int x1, int y1, int x2, int y2) {
        // Vertical segment from second room
        for (int y = y2; y < y1; y++) {
            world[x2][y] = hall;
            makeWall(x2, y, true);
        }

        // Horizontal segment to first room
        for (int x = x2; x < x1; x++) {
            world[x][y1] = hall;
            makeWall(y1, x, false);
        }

        // Add corner walls
        makeWallCorners(x2 - 1, y1, true);
    }

    /**
     * Creates a hallway when the second room is down and to the left of the first.
     * 
     * Creates an L-shaped hallway for the lower-left diagonal direction.
     * 
     * @param x1 First room X coordinate
     * @param y1 First room Y coordinate
     * @param x2 Second room X coordinate
     * @param y2 Second room Y coordinate
     */
    public void downAndLeft(int x1, int y1, int x2, int y2) {
        // Vertical segment from second room
        for (int y = y2; y > y1; y--) {
            world[x2][y] = hall;
            makeWall(x2, y, true);
        }

        // Horizontal segment to first room
        for (int x = x2; x > x1; x--) {
            world[x][y1] = hall;
            makeWall(y1, x, false);
        }

        // Add corner walls
        makeWallCorners(x2 + 1, y1, false);
    }

    /**
     * Creates a hallway when the second room is down and to the right of the first.
     * 
     * Creates an L-shaped hallway for the lower-right diagonal direction.
     * 
     * @param x1 First room X coordinate
     * @param y1 First room Y coordinate
     * @param x2 Second room X coordinate
     * @param y2 Second room Y coordinate
     */
    public void downAndRight(int x1, int y1, int x2, int y2) {
        // Vertical segment from second room
        for (int y = y2; y > y1; y--) {
            world[x2][y] = hall;
            makeWall(x2, y, true);
        }

        // Horizontal segment to first room
        for (int x = x2; x < x1; x++) {
            world[x][y1] = hall;
            makeWall(y1, x, false);
        }

        // Add corner walls
        makeWallCorners(x2 - 1, y1, false);
    }

    /**
     * Creates a vertical hallway when rooms share the same X coordinate.
     * 
     * Creates a straight vertical path between the two room centers.
     * 
     * @param x Shared X coordinate
     * @param y1 First room Y coordinate
     * @param y2 Second room Y coordinate
     */
    public void vertical(int x, int y1, int y2) {
        for (int y = Math.min(y1, y2); y < Math.max(y1, y2); y++) {
            world[x][y] = hall;
            makeWall(x, y, true);
        }
    }

    /**
     * Creates a horizontal hallway when rooms share the same Y coordinate.
     * 
     * Creates a straight horizontal path between the two room centers.
     * 
     * @param y Shared Y coordinate
     * @param x1 First room X coordinate
     * @param x2 Second room X coordinate
     */
    public void horizontal(int y, int x1, int x2) {
        for (int x = Math.min(x1, x2); x < Math.max(x1, x2); x++) {
            world[x][y] = hall;
            makeWall(y, x, false);
        }
    }

    /**
     * Adds decorative walls around hallways.
     * 
     * Creates flower wall tiles on either side of hallway segments
     * to enhance the visual appearance and provide clear boundaries.
     * 
     * @param stagnant The coordinate that doesn't change (X or Y)
     * @param change The coordinate that varies along the hallway
     * @param xStagnant true if X is stagnant, false if Y is stagnant
     */
    public void makeWall(int stagnant, int change, boolean xStagnant) {
        if (xStagnant) {
            // Add walls on left and right sides of vertical hallway
            if (world[stagnant - 1][change] == outside) {
                world[stagnant - 1][change] = wall;
            }
            if (world[stagnant + 1][change] == outside) {
                world[stagnant + 1][change] = wall;
            }
        } else {
            // Add walls on top and bottom sides of horizontal hallway
            if (world[change][stagnant - 1] == outside) {
                world[change][stagnant - 1] = wall;
            }
            if (world[change][stagnant + 1] == outside) {
                world[change][stagnant + 1] = wall;
            }
        }
    }

    /**
     * Adds decorative walls at hallway corners.
     * 
     * Ensures that L-shaped hallway corners have proper wall coverage
     * for a polished appearance and clear visual boundaries.
     * 
     * @param x X coordinate of the corner
     * @param y Y coordinate of the corner
     * @param up true if this is an upward corner, false if downward
     */
    public void makeWallCorners(int x, int y, boolean up) {
        if (world[x][y] == outside) {
            world[x][y] = wall;
        }
        if (up && world[x][y + 1] == outside) {
            world[x][y + 1] = wall;
        }
        if (!up && world[x][y - 1] == outside) {
            world[x][y - 1] = wall;
        }
    }
}

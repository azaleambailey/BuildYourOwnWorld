package core;

import edu.princeton.cs.algs4.In;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

/**
 * Manages movement and positioning for both the gardener and duck avatars.
 * 
 * This class handles:
 * - Avatar spawning in valid locations
 * - Movement validation and collision detection
 * - Coordinate tracking for both avatars
 * - Loading saved avatar positions
 * - Carrot interaction and removal
 * 
 * The movement system ensures avatars can only move on walkable tiles
 * (dirt floors, hallways, and carrots) while preventing movement
 * through walls and outside the world boundaries.
 * 
 * @author Azalea Bailey
 * @version 1.0
 */
public class AvatarMoves {
    
    // World and rendering references
    TETile[][] world;          // Main world tile array
    TETile[][] carrotWorld;    // Carrot world tile array
    
    // Avatar state tracking
    TETile prev;               // Previous tile the gardener avatar was on
    TETile prevDuck;           // Previous tile the duck avatar was on
    Coord avatarCoord;         // Current coordinates of the gardener avatar
    Coord duckCoord;           // Current coordinates of the duck avatar
    
    // Tile type constants
    TETile avatar = Tileset.AVATAR;    // Gardener avatar tile representation
    TETile duck = Tileset.DUCK;        // Duck avatar tile representation
    TETile wall = Tileset.FLOWER;      // Wall tiles (impassable)
    TETile room = Tileset.DIRT;        // Room floor tiles (walkable)
    TETile hall = Tileset.HALLDIRT;    // Hallway tiles (walkable)
    TETile outside = Tileset.GRASS;    // Outside tiles (impassable)
    TETile carrot = Tileset.CARROT;    // Carrot tiles (walkable and collectible)
    
    // World properties
    long seed;                 // Random seed for deterministic positioning
    int width;                 // World width in tiles
    int height;                // World height in tiles
    Random rand;               // Random number generator for positioning

    /**
     * Constructor that initializes the avatar movement system.
     * 
     * Sets up references to both worlds, initializes coordinate tracking,
     * and prepares the random number generator for avatar spawning.
     * 
     * @param w The main world instance
     * @param c The carrot world instance
     */
    public AvatarMoves(World w, CarrotWorld c) {
        avatarCoord = new Coord(0, 0, 0);
        duckCoord = new Coord(0, 0, 0);
        this.width = w.width;
        this.height = w.height;
        this.rand = new Random(seed);
        this.world = w.world;
        this.carrotWorld = c.carrotWorld;
        this.seed = w.seed;
    }

    /**
     * Spawns both avatars in valid locations within the world.
     * 
     * Places the gardener and duck avatars on walkable tiles (dirt floors
     * or hallways) ensuring they don't spawn on walls or outside areas.
     * The avatars are positioned at least one tile apart to prevent overlap.
     */
    public void spawnAvatar() {
        // Spawn gardener avatar in a valid location
        int x = rand.nextInt(width);
        int y = rand.nextInt(height);
        
        // Find a walkable location for the gardener
        while (world[x][y] == wall || world[x][y] == outside) {
            x = rand.nextInt(width);
            y = rand.nextInt(height);
        }
        
        // Place gardener avatar and store previous tile
        avatarCoord = new Coord(x * x + y * y, x, y);
        prev = world[x][y];
        world[x][y] = avatar;

        // Spawn duck avatar in a different valid location
        int x1 = rand.nextInt(width);
        int y1 = rand.nextInt(height);
        
        // Ensure duck doesn't spawn on gardener or impassable tiles
        while (world[x1][y1] == wall || world[x1][y1] == outside || world[x1][y1] == avatar) {
            x1 = rand.nextInt(width);
            y1 = rand.nextInt(height);
        }
        
        // Place duck avatar and store previous tile
        duckCoord = new Coord(x1 * x1 + y1 * y1, x1, y1);
        prevDuck = world[x1][y1];
        world[x1][y1] = duck;
    }

    /**
     * Removes a carrot from the world when collected by the gardener.
     * 
     * Replaces the carrot tile with a dirt floor tile, effectively
     * removing the collectible item from the world.
     * 
     * @param x X coordinate of the carrot to remove
     * @param y Y coordinate of the carrot to remove
     */
    public void removeCarrot(int x, int y) {
        world[x][y] = room;
    }

    /**
     * Loads avatar positions from a saved game file.
     * 
     * Reads the save.txt file and places both avatars at their
     * previously saved coordinates. This method is called when
     * loading a saved game to restore the exact game state.
     */
    public void loadAvatar() {
        In file = new In("./save.txt");

        String currentLine = file.readLine();
        String[] splitLine = currentLine.split(",");

        // Load gardener avatar position (indices 1 and 2)
        int avatarX = Integer.parseInt(splitLine[1]);
        int avatarY = Integer.parseInt(splitLine[2]);
        avatarCoord = new Coord(avatarX * avatarX + avatarY * avatarY, avatarX, avatarY);
        prev = world[avatarX][avatarY];
        world[avatarX][avatarY] = avatar;

        // Load duck avatar position (indices 3 and 4)
        int duckX = Integer.parseInt(splitLine[3]);
        int duckY = Integer.parseInt(splitLine[4]);
        duckCoord = new Coord(duckX * duckX + duckY * duckY, duckX, duckY);
        prevDuck = world[duckX][duckY];
        world[duckX][duckY] = duck;
    }

    /**
     * Validates whether an avatar can move to a specific tile.
     * 
     * Checks if the target tile is walkable:
     * - Hallway floors (hall)
     * - Room floors (room)
     * - Carrots (carrot)
     * 
     * @param t The tile to check for movement validity
     * @return true if the tile is walkable, false otherwise
     */
    public boolean validMove(TETile t) {
        return t.equals(hall) || t.equals(room) || t.equals(carrot);
    }

    /**
     * Moves the gardener avatar upward (north).
     * 
     * Checks if the move is valid, then:
     * 1. Places the avatar on the new tile
     * 2. Restores the previous tile
     * 3. Updates the avatar's coordinates
     * 4. Stores the new previous tile
     */
    public void avatarUp() {
        TETile temp = world[avatarCoord.x][avatarCoord.y + 1];

        if (validMove(temp)) {
            world[avatarCoord.x][avatarCoord.y + 1] = avatar;
            world[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.y += 1;
        }
    }

    /**
     * Moves the gardener avatar downward (south).
     * 
     * Similar to avatarUp but moves in the negative Y direction.
     * Implements the same movement validation and tile restoration logic.
     */
    public void avatarDown() {
        TETile temp = world[avatarCoord.x][avatarCoord.y - 1];

        if (validMove(temp)) {
            world[avatarCoord.x][avatarCoord.y - 1] = avatar;
            world[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.y -= 1;
        }
    }

    /**
     * Moves the gardener avatar leftward (west).
     * 
     * Moves the avatar in the negative X direction.
     * Implements the same movement validation and tile restoration logic.
     */
    public void avatarLeft() {
        TETile temp = world[avatarCoord.x - 1][avatarCoord.y];

        if (validMove(temp)) {
            world[avatarCoord.x - 1][avatarCoord.y] = avatar;
            world[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.x -= 1;
        }
    }

    /**
     * Moves the gardener avatar rightward (east).
     * 
     * Moves the avatar in the positive X direction.
     * Implements the same movement validation and tile restoration logic.
     */
    public void avatarRight() {
        TETile temp = world[avatarCoord.x + 1][avatarCoord.y];

        if (validMove(temp)) {
            world[avatarCoord.x + 1][avatarCoord.y] = avatar;
            world[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.x += 1;
        }
    }

    /**
     * Moves the duck avatar upward (north).
     * 
     * Implements the same movement logic as the gardener avatar
     * but for the duck character. The duck can move on the same
     * walkable tiles as the gardener.
     */
    public void duckUp() {
        TETile temp = world[duckCoord.x][duckCoord.y + 1];

        if (validMove(temp)) {
            world[duckCoord.x][duckCoord.y + 1] = duck;
            world[duckCoord.x][duckCoord.y] = prevDuck;
            prevDuck = temp;
            duckCoord.y += 1;
        }
    }

    /**
     * Moves the duck avatar leftward (west).
     * 
     * Moves the duck avatar in the negative X direction.
     * Implements the same movement validation and tile restoration logic.
     */
    public void duckLeft() {
        TETile temp = world[duckCoord.x - 1][duckCoord.y];

        if (validMove(temp)) {
            world[duckCoord.x - 1][duckCoord.y] = duck;
            world[duckCoord.x][duckCoord.y] = prevDuck;
            prevDuck = temp;
            duckCoord.x -= 1;
        }
    }

    /**
     * Moves the duck avatar downward (south).
     * 
     * Moves the duck avatar in the negative Y direction.
     * Implements the same movement validation and tile restoration logic.
     */
    public void duckDown() {
        TETile temp = world[duckCoord.x][duckCoord.y - 1];

        if (validMove(temp)) {
            world[duckCoord.x][duckCoord.y - 1] = duck;
            world[duckCoord.x][duckCoord.y] = prevDuck;
            prevDuck = temp;
            duckCoord.y -= 1;
        }
    }

    /**
     * Moves the duck avatar rightward (east).
     * 
     * Moves the duck avatar in the positive X direction.
     * Implements the same movement validation and tile restoration logic.
     */
    public void duckRight() {
        TETile temp = world[duckCoord.x + 1][duckCoord.y];

        if (validMove(temp)) {
            world[duckCoord.x + 1][duckCoord.y] = duck;
            world[duckCoord.x][duckCoord.y] = prevDuck;
            prevDuck = temp;
            duckCoord.x += 1;
        }
    }
}
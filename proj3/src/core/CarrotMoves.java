package core;

import tileengine.TETile;
import tileengine.Tileset;
import java.util.ArrayList;
import java.util.Random;

/**
 * Manages movement and interaction for the gardener avatar in the carrot world dimension.
 * 
 * This class handles the special movement system when the gardener avatar
 * is transported to the carrot world after collecting a carrot. The carrot
 * world is a picnic table environment where the gardener can move on plates
 * and collect triangular carrots.
 * 
 * Key features:
 * - Restricted movement on plate tiles only
 * - Carrot collection and counting
 * - Synchronized movement with main world avatar
 * - Special tile interactions in picnic table environment
 * 
 * @author Azalea Bailey
 * @version 1.0
 */
public class CarrotMoves {
    
    // World and rendering references
    TETile[][] world;          // Main world tile array (for synchronization)
    TETile[][] carrotWorld;    // Carrot world tile array (picnic table environment)
    
    // Avatar state tracking
    TETile prev;               // Previous tile the gardener avatar was on in carrot world
    Coord avatarCoord;         // Current coordinates of the gardener avatar in carrot world
    
    // Tile type constants
    TETile avatar = Tileset.AVATAR;    // Gardener avatar tile representation
    
    // World properties
    long seed;                 // Random seed for deterministic positioning
    int width;                 // World width in tiles
    int height;                // World height in tiles
    Random rand;               // Random number generator for positioning
    
    // Carrot collection tracking
    ArrayList<Coord> carrotCoords;     // List of carrot coordinates in carrot world
    int eatenCarrotCounter;            // Number of carrots eaten by gardener avatar
    int finalCarrotCount;              // Final carrot count to be displayed

    /**
     * Constructor that initializes the carrot world movement system.
     * 
     * Sets up references to both worlds, initializes coordinate tracking,
     * and prepares the random number generator for avatar positioning.
     * 
     * @param w The main world instance
     * @param c The carrot world instance
     */
    public CarrotMoves(World w, CarrotWorld c) {
        avatarCoord = new Coord(0, 0, 0);
        this.width = w.width;
        this.height = w.height;
        this.rand = w.rand;
        this.world = w.world;
        this.carrotWorld = c.carrotWorld;
        this.seed = w.seed;
        this.carrotCoords = new ArrayList<>();
        eatenCarrotCounter = 0;
        finalCarrotCount = 0;
    }

    /**
     * Spawns the gardener avatar in the carrot world at a fixed location.
     * 
     * Places the avatar on a plate tile in the center-left area of the
     * picnic table. This provides a consistent starting point for
     * carrot collection activities.
     */
    public void spawnCarrotAvatar() {
        int x = width / 3;     // Position avatar in left third of world
        int y = 14;            // Position avatar at specific height for plate access
        
        avatarCoord = new Coord(x * x + y * y, x, y);
        prev = carrotWorld[x][y];
        carrotWorld[x][y] = avatar;
    }

    /**
     * Validates whether the gardener avatar can move to a specific tile in carrot world.
     * 
     * In carrot world, the gardener can only move on:
     * - Orange carrot tiles (collectible)
     * - Green carrot stem tiles
     * - Gray plate tiles (light and dark)
     * 
     * This restriction creates the picnic table environment where the
     * avatar must navigate carefully to collect carrots.
     * 
     * @param t The tile to check for movement validity
     * @return true if the tile is walkable in carrot world, false otherwise
     */
    public boolean validMove(TETile t) {
        return (t.equals(Tileset.ORANGE) || t.equals(Tileset.GREEN) || 
                t.equals(Tileset.GRAY) || t.equals(Tileset.LIGHTGRAY));
    }

    /**
     * Moves the gardener avatar upward (north) in carrot world.
     * 
     * Implements the same movement logic as the main world but with
     * carrot world-specific validation and carrot collection mechanics.
     * When moving off an orange carrot tile, it's collected and counted.
     */
    public void avatarCarrotUp() {
        TETile temp = carrotWorld[avatarCoord.x][avatarCoord.y + 1];

        if (validMove(temp)) {
            // Move avatar to new position
            carrotWorld[avatarCoord.x][avatarCoord.y + 1] = avatar;
            carrotWorld[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.y += 1;
        }
        
        // Check if previous tile was a carrot and collect it
        if (prev == Tileset.ORANGE) {
            prev = Tileset.GRAY;        // Replace carrot with plate
            eatenCarrotCounter += 1;    // Increment collection counter
        }
    }

    /**
     * Moves the gardener avatar downward (south) in carrot world.
     * 
     * Similar to avatarCarrotUp but moves in the negative Y direction.
     * Implements the same movement validation and carrot collection logic.
     */
    public void avatarCarrotDown() {
        TETile temp = carrotWorld[avatarCoord.x][avatarCoord.y - 1];

        if (validMove(temp)) {
            // Move avatar to new position
            carrotWorld[avatarCoord.x][avatarCoord.y - 1] = avatar;
            carrotWorld[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.y -= 1;
        }

        // Check if previous tile was a carrot and collect it
        if (prev == Tileset.ORANGE) {
            prev = Tileset.GRAY;        // Replace carrot with plate
            eatenCarrotCounter += 1;    // Increment collection counter
        }
    }

    /**
     * Moves the gardener avatar leftward (west) in carrot world.
     * 
     * Moves the avatar in the negative X direction.
     * Implements the same movement validation and carrot collection logic.
     */
    public void avatarCarrotLeft() {
        TETile temp = carrotWorld[avatarCoord.x - 1][avatarCoord.y];

        if (validMove(temp)) {
            // Move avatar to new position
            carrotWorld[avatarCoord.x - 1][avatarCoord.y] = avatar;
            carrotWorld[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.x -= 1;
        }

        // Check if previous tile was a carrot and collect it
        if (prev == Tileset.ORANGE) {
            prev = Tileset.GRAY;        // Replace carrot with plate
            eatenCarrotCounter += 1;    // Increment collection counter
        }
    }

    /**
     * Moves the gardener avatar rightward (east) in carrot world.
     * 
     * Moves the avatar in the positive X direction.
     * Implements the same movement validation and carrot collection logic.
     */
    public void avatarCarrotRight() {
        TETile temp = carrotWorld[avatarCoord.x + 1][avatarCoord.y];

        if (validMove(temp)) {
            // Move avatar to new position
            carrotWorld[avatarCoord.x + 1][avatarCoord.y] = avatar;
            carrotWorld[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.x += 1;
        }

        // Check if previous tile was a carrot and collect it
        if (prev == Tileset.ORANGE) {
            prev = Tileset.GRAY;        // Replace carrot with plate
            eatenCarrotCounter += 1;    // Increment collection counter
        }
    }
}

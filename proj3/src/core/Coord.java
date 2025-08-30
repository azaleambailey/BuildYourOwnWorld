package core;

/**
 * Represents a 2D coordinate position with a unique identifier.
 * 
 * This class is used throughout the game to track the positions of:
 * - Avatars (gardener and duck)
 * - Carrots and collectible items
 * - Room centers for hallway generation
 * - Various game objects and entities
 * 
 * The unique identifier (id) is calculated using mathematical formulas
 * to ensure each coordinate has a distinct value for efficient storage
 * and retrieval in data structures like the PythagoreanTree.
 * 
 * @author Azalea Bailey
 * @version 1.0
 */
public class Coord {
    
    // Coordinate components
    int x;      // X coordinate (horizontal position)
    int y;      // Y coordinate (vertical position)
    int id;     // Unique identifier for this coordinate

    /**
     * Constructor that creates a new coordinate with a unique identifier.
     * 
     * The identifier is typically calculated using mathematical formulas
     * such as x² + y² or other deterministic functions to ensure
     * uniqueness while maintaining the relationship between coordinates.
     * 
     * @param id Unique identifier for this coordinate
     * @param x X coordinate (horizontal position)
     * @param y Y coordinate (vertical position)
     */
    public Coord(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}

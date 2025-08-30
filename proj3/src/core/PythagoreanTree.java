package core;

import java.util.TreeMap;

/**
 * Specialized data structure for storing room center coordinates.
 * 
 * This class extends TreeMap to provide efficient storage and retrieval
 * of room center coordinates during world generation. The key innovation
 * is using the Pythagorean theorem (distance from origin) as the key,
 * which ensures proper ordering and spacing of rooms.
 * 
 * Key features:
 * - Automatic sorting by distance from origin (0,0)
 * - Efficient room center storage and retrieval
 * - Handles coordinate collisions gracefully
 * - Essential for hallway generation algorithms
 * 
 * The Pythagorean distance serves as a natural ordering mechanism
 * that helps create well-distributed room layouts.
 * 
 * @author Azalea Bailey
 * @version 1.0
 */
public class PythagoreanTree extends TreeMap<Double, Coord> {

    /**
     * Default constructor that initializes an empty coordinate tree.
     * 
     * Creates a new TreeMap with Double keys (Pythagorean distances)
     * and Coord values (room center coordinates).
     */
    public PythagoreanTree() {
        super();
    }

    /**
     * Stores a coordinate in the tree using its Pythagorean distance as the key.
     * 
     * If a coordinate with the same distance already exists, the new coordinate
     * is stored with a slightly modified key (distance + 1) to prevent collisions.
     * This ensures that all room centers can be stored while maintaining
     * the tree's ordering properties.
     * 
     * @param x X coordinate of the room center
     * @param y Y coordinate of the room center
     */
    public void putCoord(int x, int y) {
        double c = pythagorean(x, y);
        
        // Handle coordinate collisions by adjusting the key
        if (containsKey(c)) {
            put(c + 1, new Coord(size(), x, y));
        }
        
        // Store the coordinate with its Pythagorean distance as the key
        put(c, new Coord(size(), x, y));
    }

    /**
     * Calculates the Pythagorean distance from the origin (0,0) to a point.
     * 
     * Uses the Pythagorean theorem: c = √(a² + b²)
     * This distance serves as the natural ordering key for the tree,
     * ensuring coordinates are stored in order of increasing distance
     * from the world origin.
     * 
     * @param a X coordinate of the point
     * @param b Y coordinate of the point
     * @return The Euclidean distance from (0,0) to (a,b)
     */
    public double pythagorean(int a, int b) {
        return Math.sqrt((a * a) + (b * b));
    }
}


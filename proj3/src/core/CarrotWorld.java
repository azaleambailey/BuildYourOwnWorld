package core;

import tileengine.TETile;
import tileengine.Tileset;
import java.util.Random;

/**
 * Generates and manages the special "carrot world" dimension.
 * 
 * The carrot world is a picnic table environment that the gardener avatar
 * is transported to when collecting carrots from the main world. This
 * dimension features:
 * - A checkerboard tablecloth pattern
 * - A circular plate with fork and napkin
 * - Triangular carrots that can be collected
 * - Mathematical precision in shape generation
 * 
 * The environment is procedurally generated based on the same seed as
 * the main world, ensuring consistency between play sessions.
 * 
 * @author Azalea Bailey
 * @version 1.0
 */
public class CarrotWorld {
    
    // World dimensions
    int width = 100;           // Width of carrot world in tiles
    int height = 60;           // Height of carrot world in tiles
    
    // World representation
    TETile[][] carrotWorld;    // 2D array representing the carrot world
    
    // Table setting elements
    TETile red = Tileset.RED;          // Red checker tile for tablecloth
    TETile white = Tileset.WHITE;      // White checker tile for tablecloth
    
    // Generation and state
    Random rand;                // Random number generator for procedural generation
    long seed;                  // Seed for deterministic generation
    int scale;                  // Size factor for carrot generation (2-5)
    Coord carrotStem;           // Location of the carrot stem (green part)
    int carrotSquares;          // Number of tiles that make up the carrot

    /**
     * Constructor that initializes and generates the carrot world.
     * 
     * Creates a complete picnic table environment with:
     * 1. Checkerboard tablecloth background
     * 2. Table setting (plate, fork, napkin)
     * 3. Triangular carrot for collection
     * 
     * @param seed The seed string for deterministic generation
     */
    public CarrotWorld(String seed) {
        carrotWorld = new TETile[width][height];
        carrotSquares = 0;

        this.seed = parseInput(seed);
        rand = new Random(this.seed);
        scale = rand.nextInt(5 - 2 + 1) + 2;  // Random scale between 2-5

        fillBackground();
        tableSetter();
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
     * Creates the checkerboard tablecloth background pattern.
     * 
     * Generates an alternating red and white checkerboard pattern
     * that covers the entire picnic table. The pattern alternates
     * both horizontally and vertically for a classic checkerboard look.
     */
    public void fillBackground() {
        TETile first = red;
        TETile second = white;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Alternate tiles based on row position
                if (Math.floorMod(y, 2) == 0) {
                    carrotWorld[x][y] = first;
                } else {
                    carrotWorld[x][y] = second;
                }
            }
            // Swap colors for next column to create checkerboard
            TETile temp = first;
            first = second;
            second = temp;
        }
    }

    /**
     * Creates the table setting elements on the picnic table.
     * 
     * Generates:
     * - A circular plate in the center
     * - A fork positioned to the right
     * - A napkin positioned to the right
     * - Triangular carrots on the plate
     * 
     * All elements are positioned using mathematical calculations
     * to ensure proper placement and proportions.
     */
    public void tableSetter() {
        int centerX = width / 3;    // Center X coordinate for plate
        int centerY = height / 2;   // Center Y coordinate for plate

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                // Create circular plate using distance calculation
                double distance = Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
                
                // Outer plate rim (light gray)
                if (distance <= 20) {
                    carrotWorld[x][y] = Tileset.LIGHTGRAY;
                }
                
                // Inner plate (dark gray)
                if (distance <= 14) {
                    carrotWorld[x][y] = Tileset.GRAY;
                }

                // Create napkin (rectangular white area)
                if (x > 61 && x < 84 && y < 50 && y > 10) {
                    carrotWorld[x][y] = Tileset.NAPKIN;
                }

                // Create fork handle (vertical dark gray bar)
                if (y < 38 && y > 15 && x > 71 && x < 75) {
                    carrotWorld[x][y] = Tileset.DARKGRAY;
                }
                
                // Create fork tines (horizontal dark gray bars)
                if (y < 45 && y > 37 && (x == 69 || x == 77 || x == 73)) {
                    carrotWorld[x][y] = Tileset.DARKGRAY;
                }
                
                // Create fork tine base (thicker horizontal section)
                if (y < 40 && y > 37 && x > 69 && x < 77) {
                    carrotWorld[x][y] = Tileset.DARKGRAY;
                }

                // Generate triangular carrots on the plate
                carrotMaker(x, y);
            }
        }
    }

    /**
     * Creates triangular carrots on the plate for collection.
     * 
     * Generates mathematically precise triangular carrots using:
     * - Trigonometric calculations for triangle vertices
     * - Scale factor for carrot size variation
     * - Point-in-triangle testing for accurate shape
     * 
     * @param x X coordinate to check for carrot placement
     * @param y Y coordinate to check for carrot placement
     */
    public void carrotMaker(int x, int y) {
        int centerX = width / 3;
        int centerY = height / 2;
        
        // Calculate triangle vertices using trigonometry
        int xDistance = scale * (int) Math.round(Math.cos(Math.toRadians(60.0)));
        int yDistance = scale * (int) Math.round(Math.sin(Math.toRadians(60.0)));

        // Define triangle vertices (equilateral triangle)
        int[] pointOne = new int[]{centerX - xDistance, centerY + yDistance};
        int[] pointTwo = new int[]{centerX + xDistance, centerY + yDistance};
        int[] pointThree = new int[]{centerX, centerY - yDistance};

        // Check if current position is inside the triangle
        if (isInsideTriangle(x, y, pointOne, pointTwo, pointThree)) {
            carrotWorld[x][y] = Tileset.ORANGE;
            carrotSquares += 1;
        }

        // Add green stem above the carrot
        carrotWorld[centerX][centerY + yDistance + 1] = Tileset.GREEN;
        carrotStem = new Coord(centerX * centerX, centerX, centerY + yDistance + 1);
    }

    /**
     * Determines if a point is inside a triangle using barycentric coordinates.
     * 
     * Uses the sign method to test if a point lies inside a triangle:
     * 1. Calculate the sign of the area for each triangle edge
     * 2. If all signs are the same (all positive or all negative), point is inside
     * 3. If signs differ, point is outside
     * 
     * @param x X coordinate of the point to test
     * @param y Y coordinate of the point to test
     * @param point1 First triangle vertex [x, y]
     * @param point2 Second triangle vertex [x, y]
     * @param point3 Third triangle vertex [x, y]
     * @return true if the point is inside the triangle, false otherwise
     */
    private boolean isInsideTriangle(int x, int y, int[] point1, int[] point2, int[] point3) {
        double d1, d2, d3;
        boolean hasNeg, hasPos;

        // Calculate the sign of the area for each edge
        d1 = sign(x, y, point1[0], point1[1], point2[0], point2[1]);
        d2 = sign(x, y, point2[0], point2[1], point3[0], point3[1]);
        d3 = sign(x, y, point3[0], point3[1], point1[0], point1[1]);

        // Check if all signs are the same
        hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0);

        // Point is inside if all signs are the same
        return !(hasNeg && hasPos);
    }

    /**
     * Respawns the carrot after the gardener avatar exits carrot world.
     * 
     * Creates a new triangular carrot with:
     * - Random scale factor (2-5) for size variation
     * - Same mathematical precision as original
     * - Green stem positioned above the carrot
     * 
     * This ensures that each visit to carrot world offers a fresh
     * carrot collection experience.
     */
    public void respawnCarrot() {
        carrotSquares = 0;
        carrotWorld[carrotStem.x][carrotStem.y] = Tileset.GRAY;

        int centerX = width / 3;
        int centerY = height / 2;
        
        // Generate new random scale for carrot size
        scale = rand.nextInt(5 - 2 + 1) + 2;
        
        // Calculate new triangle vertices
        int xDistance = scale * (int) Math.round(Math.cos(Math.toRadians(60.0)));
        int yDistance = scale * (int) Math.round(Math.sin(Math.toRadians(60.0)));

        int[] pointOne = new int[]{centerX - xDistance, centerY + yDistance};
        int[] pointTwo = new int[]{centerX + xDistance, centerY + yDistance};
        int[] pointThree = new int[]{centerX, centerY - yDistance};

        // Generate new triangular carrot
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isInsideTriangle(x, y, pointOne, pointTwo, pointThree)) {
                    carrotWorld[x][y] = Tileset.ORANGE;
                    carrotSquares += 1;
                }
            }
        }

        // Add new green stem
        carrotWorld[centerX][centerY + yDistance + 1] = Tileset.GREEN;
        carrotStem = new Coord(centerX, centerX, centerY + yDistance + 1);
    }

    /**
     * Calculates the sign of the area of a triangle formed by three points.
     * 
     * Uses the cross product formula to determine the orientation:
     * - Positive result: counterclockwise orientation
     * - Negative result: clockwise orientation
     * - Zero result: collinear points
     * 
     * This method is essential for the point-in-triangle test used
     * in carrot generation and respawning.
     * 
     * @param x1 X coordinate of first point
     * @param y1 Y coordinate of first point
     * @param x2 X coordinate of second point
     * @param y2 Y coordinate of second point
     * @param x3 X coordinate of third point
     * @param y3 Y coordinate of third point
     * @return The sign of the triangle area (positive, negative, or zero)
     */
    private double sign(int x1, int y1, int x2, int y2, int x3, int y3) {
        return (x1 - x3) * (y2 - y3) - (x2 - x3) * (y1 - y3);
    }
}
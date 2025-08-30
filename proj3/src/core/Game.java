package core;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;

/**
 * Main game controller class for the Build Your Own Garden (BYOG) game.
 * 
 * This class manages the entire game flow including:
 * - Game initialization and world generation
 * - Keyboard input processing for both avatars
 * - Rendering and display management
 * - Save/load functionality
 * - Menu system and user interface
 * 
 * The game features a dual-avatar system where players control both
 * a gardener (WASD keys) and a duck (IJKL keys) simultaneously.
 * 
 * @author Azalea Bailey
 * @version 1.0
 */
public class Game {

    /**
     * Main game loop that runs the interactive BYOG game.
     * 
     * This method:
     * 1. Prompts the user for a seed input
     * 2. Generates the world based on the seed
     * 3. Spawns avatars and carrots
     * 4. Enters the main game loop for user interaction
     * 5. Handles carrot world transitions and respawning
     * 
     * @param ter The tile renderer for displaying the game world
     * @throws IOException If there's an error during file operations
     */
    public static void runGame(TERenderer ter) throws IOException {
        // Get seed input from user for world generation
        String seed = takeInput();
        
        // Generate the main world and carrot world based on seed
        World ourWorld = new World(seed);
        ourWorld.pickCarrot();
        CarrotWorld c = new CarrotWorld(seed);
        
        // Initialize movement controllers for both avatars
        AvatarMoves movement = new AvatarMoves(ourWorld, c);
        CarrotMoves carrotMovement = new CarrotMoves(ourWorld, c);
        
        // Set up the rendering window (100x60 tiles)
        ter.initialize(100, 60);
        
        // Spawn both avatars in the world
        movement.spawnAvatar();
        carrotMovement.spawnCarrotAvatar();
        
        // Render the initial world state
        ter.renderFrame(movement.world);
        
        // Track carrot collection state
        int carrotX = movement.avatarCoord.x;
        int carrotY = movement.avatarCoord.y;

        // Game loop variables
        long counter = 0;           // Timer for carrot world duration
        boolean renderCarrot = false; // Whether to show carrot world
        
        // Main game loop - runs continuously until game exit
        while (true) {
            // Check if gardener avatar is on a carrot tile
            for (Coord i : ourWorld.carrotCoord) {
                if (i.x == movement.avatarCoord.x && i.y == movement.avatarCoord.y) {
                    renderCarrot = true;
                    carrotX = i.x;
                    carrotY = i.y;
                    break;
                }
            }
            
            // Process user input and move avatars
            moveAvatar(renderCarrot, seed, movement, carrotMovement, ter, ourWorld);
            
            // Render appropriate world (main world or carrot world)
            if (renderCarrot) {
                counter += 1;
                ter.renderFrame(movement.carrotWorld);
                hUDisplay(movement.carrotWorld, renderCarrot, carrotMovement.finalCarrotCount);
            } else {
                ter.renderFrame(movement.world);
                hUDisplay(movement.world, renderCarrot, carrotMovement.finalCarrotCount);
            }

            // Control game speed (10 FPS)
            StdDraw.pause(100);

            // Return to main world after 120 frames (12 seconds) in carrot world
            if (counter == (120)) {
                renderCarrot = false;
                counter = 0;
                c.respawnCarrot();
                movement.removeCarrot(carrotX, carrotY);
                
                // Remove the collected carrot from the world
                ArrayList<Coord> copy = new ArrayList<>(ourWorld.carrotCoord);
                for (Coord p : copy) {
                    if (p.x == carrotX && p.y == carrotY) {
                        ourWorld.carrotCoord.remove(p);
                    }
                }
                System.out.println(ourWorld.carrotCoord + ourWorld.carrotCoord.toString());
            }
        }
    }

    /**
     * Processes keyboard input for both gardener and duck avatar movement.
     * 
     * This method handles:
     * - WASD keys for gardener avatar movement
     * - IJKL keys for duck avatar movement
     * - Special commands (:) for game menu access
     * - Synchronized movement in carrot world when applicable
     * 
     * @param c Whether the gardener is currently in carrot world
     * @param s The current game seed
     * @param M The avatar movement controller
     * @param m The carrot movement controller
     * @param t The tile renderer
     * @param w The main world
     * @throws IOException If there's an error during file operations
     */
    public static void moveAvatar(boolean c, String s, AvatarMoves M, CarrotMoves m, TERenderer t, World w) throws IOException {
        if (StdDraw.hasNextKeyTyped()) {
            switch (StdDraw.nextKeyTyped()) {
                // Gardener avatar movement (WASD)
                case 'w':
                    M.avatarUp();
                    if (c) {
                        m.avatarCarrotUp();
                    }
                    break;
                case 'a':
                    M.avatarLeft();
                    if (c) {
                        m.avatarCarrotLeft();
                    }
                    break;
                case 's':
                    M.avatarDown();
                    if (c) {
                        m.avatarCarrotDown();
                    }
                    break;
                case 'd':
                    M.avatarRight();
                    if (c) {
                        m.avatarCarrotRight();
                    }
                    break;
                
                // Duck avatar movement (IJKL)
                case 'i':
                    M.duckUp();
                    break;
                case 'j':
                    M.duckLeft();
                    break;
                case 'k':
                    M.duckDown();
                    break;
                case 'l':
                    M.duckRight();
                    break;
                
                // Special command menu access
                case ':':
                    keyboard(s, M.avatarCoord, M.duckCoord, w.carrotCoord, t);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Displays information about the tile under the mouse cursor.
     * 
     * This method provides real-time feedback about what the player
     * is looking at, including tile type descriptions and carrot count.
     * 
     * @param world The current world being displayed
     * @param carrot Whether the gardener is in carrot world
     * @param count The number of carrots collected
     */
    public static void hUDisplay(TETile[][] world, boolean carrot, int count) {
        TETile curr;
        double mx = Math.floor(StdDraw.mouseX());
        double my = Math.floor(StdDraw.mouseY());

        // Ensure mouse coordinates are within world bounds
        if (mx >= 100 || my >= 60 || mx < 0 || my < 0) {
            curr = world[99][59];
        } else {
            curr = world[(int) mx][(int) my];
        }

        // Determine tile type and display appropriate description
        if (curr.equals(Tileset.FLOWER)) {
            hUDisplayer("garden wall", carrot, count);
        } else if (curr.equals(Tileset.HALLDIRT) || curr.equals(Tileset.DIRT)) {
            hUDisplayer("dirt", carrot, count);
        } else if (curr.equals(Tileset.AVATAR)) {
            hUDisplayer("gardener", carrot, count);
        } else if (curr.equals(Tileset.DUCK)) {
            hUDisplayer("duck", carrot, count);
        } else if (curr.equals(Tileset.CARROT)) {
            hUDisplayer("carrot", carrot, count);
        } else if (curr.equals(Tileset.RED) || curr.equals(Tileset.WHITE)) {
            hUDisplayer("tablecloth", carrot, count);
        } else if (curr.equals(Tileset.GRAY) || curr.equals(Tileset.LIGHTGRAY)) {
            hUDisplayer("plate", carrot, count);
        } else if (curr.equals(Tileset.NAPKIN)) {
            hUDisplayer("napkin", carrot, count);
        } else if (curr.equals(Tileset.DARKGRAY)) {
            hUDisplayer("fork", carrot, count);
        } else if (curr.equals(Tileset.GREEN) || curr.equals(Tileset.ORANGE)) {
            hUDisplayer("carrot", carrot, count);
        } else {
            hUDisplayer("grass", carrot, count);
        }
    }

    /**
     * Helper method to render the HUD display with tile information.
     * 
     * Creates a colored background rectangle and displays the current
     * tile type and carrot count information.
     * 
     * @param tileType Description of the tile under the mouse
     * @param carrot Whether the gardener is in carrot world
     * @param count Number of carrots collected
     */
    public static void hUDisplayer(String tileType, boolean carrot, int count) {
        // Set background color based on current world (carrot world = red, main world = green)
        if (carrot) {
            StdDraw.setPenColor(191, 65, 65);
            StdDraw.filledRectangle(7.4, 59, 6.8, .75);
            StdDraw.setPenColor(Color.white);
        } else {
            StdDraw.setPenColor(140, 199, 94);
            StdDraw.filledRectangle(7.4, 59, 6.8, .75);
            StdDraw.setPenColor(243, 78, 182);
        }
        
        // Display tile information and carrot count
        StdDraw.textLeft(1, 59,"Current Tile: " + tileType);
        StdDraw.show();
    }

    /**
     * Renders the main title page with game options.
     * 
     * Creates a beautiful rose pattern background with:
     * - Game title "BYOG" (Build Your Own Garden)
     * - Subtitle and game options
     * - Decorative flower pattern using mathematical rose curves
     */
    public static void titlePage() {
        StdDraw.setCanvasSize();

        // Draw the decorative rose pattern background
        drawRose();

        // Draw the central yellow circle
        drawCircle();

        // Set up title text
        StdDraw.setPenColor(new Color(117, 68, 43));

        // Main title
        Font titleFont = new Font("Arial", Font.BOLD, 60);
        StdDraw.setFont(titleFont);
        StdDraw.text(0.5, 0.58, "BYOG");

        // Subtitle
        Font subtitleFont = new Font("Arial", Font.ITALIC, 18);
        StdDraw.setFont(subtitleFont);
        StdDraw.text(0.5, 0.52, "Build Your Own Garden");

        // Game options
        Font optionsFont = new Font("Arial", 1, 22);
        StdDraw.setFont(optionsFont);
        StdDraw.text(0.5, 0.42, "New Game (N)");
        StdDraw.text(0.5, 0.37, "Load Game (L)");
        StdDraw.text(0.5, 0.32, "Quit Game (Q)");

        StdDraw.show();
    }

    /**
     * Handles user input for the world seed.
     * 
     * Displays a seed input screen where users can type any sequence
     * of characters. The input is processed when 's' is pressed.
     * 
     * @return The seed string entered by the user
     */
    public static String takeInput() {
        String input = "";
        boolean flip = true;
        
        // Continue accepting input until 's' is pressed
        while (flip) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();

                // Handle backspace for input correction
                if (key == KeyEvent.VK_BACK_SPACE && input.length() > 0) {
                    input = input.substring(0, input.length() - 1);
                } else {
                    input += key;
                }

                // Clear screen and redraw input interface
                StdDraw.clear(StdDraw.BLACK);
                StdDraw.setPenColor(StdDraw.WHITE);

                // Title
                Font titleFont = new Font("Arial", Font.BOLD, 36);
                StdDraw.setFont(titleFont);
                StdDraw.text(0.5, 0.6, "Enter Seed Below:");

                // Input display
                Font optionsFont = new Font("Arial", Font.PLAIN, 24);
                StdDraw.setFont(optionsFont);
                StdDraw.text(0.5, 0.4, input);
                StdDraw.show();
                
                // Exit on 's' key
                if (key == 's' || key == 'S') {
                    flip = false;
                }
            }
        }
        
        System.out.println(input);
        return input;
    }

    /**
     * Handles the command menu system for game control.
     * 
     * Processes commands entered after pressing ':' including:
     * - N: Start a new game
     * - Q: Save and quit
     * - L: Load a saved game
     * 
     * @param s The current game seed
     * @param aC Avatar coordinates
     * @param dC Duck coordinates
     * @param cC Carrot coordinates
     * @param ter The tile renderer
     * @throws IOException If there's an error during file operations
     */
    public static void keyboard(String s, Coord aC, Coord dC, ArrayList<Coord> cC, TERenderer ter) throws IOException {
        Font titleFont;
        
        // Command processing loop
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                switch (StdDraw.nextKeyTyped()) {
                    case 'N', 'n':
                        // Start new game - clear screen and prompt for seed
                        StdDraw.clear(StdDraw.BLACK);
                        StdDraw.setPenColor(StdDraw.WHITE);

                        titleFont = new Font("Arial", Font.BOLD, 36);
                        StdDraw.setFont(titleFont);
                        StdDraw.text(0.5, 0.6, "Enter Seed Below:");
                        runGame(ter);
                        break;

                    case 'Q', 'q':
                        // Save game and return to title screen
                        saveGame(s, aC, dC, cC, ter);
                        break;
                        
                    case 'L', 'l':
                        // Load saved game
                        loadGame(ter);
                        break;
                        
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Saves the current game state to a file.
     * 
     * Creates a save.txt file containing:
     * - Game seed
     * - Avatar coordinates
     * - Duck coordinates
     * - Remaining carrot coordinates
     * 
     * @param s The game seed
     * @param aC Avatar coordinates
     * @param dC Duck coordinates
     * @param cC Carrot coordinates
     * @param ter The tile renderer
     * @throws IOException If there's an error during file operations
     */
    public static void saveGame(String s, Coord aC, Coord dC, ArrayList<Coord> cC, TERenderer ter) throws IOException {
        try {
            PrintWriter saveTxt = new PrintWriter("save.txt");

            // Clean up seed string format
            if (s.charAt(0) != ('n')) {
                s = s.substring(0, s.length());
            } else {
                s = s.substring(1, s.length());
            }

            // Write game state in CSV format: seed,avatarX,avatarY,duckX,duckY,carrot1X,carrot1Y,...
            saveTxt.write(s + "," + aC.x + "," + aC.y + "," + dC.x + "," + dC.y);
            for (Coord c : cC) {
                saveTxt.write("," + c.x + "," + c.y);
            }
            saveTxt.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        
        // Return to title screen after saving
        titlePage();
        keyboard(s, aC, dC, cC, ter);
    }

    /**
     * Loads a saved game from the save file.
     * 
     * Reads the save.txt file and restores:
     * - World generation with saved seed
     * - Avatar and duck positions
     * - Remaining carrot locations
     * - Game state for continued play
     * 
     * @param ter The tile renderer
     * @throws IOException If there's an error during file operations
     */
    public static void loadGame(TERenderer ter) throws IOException {
        // Load seed from save file
        String seed = getSeed("");
        System.out.println(seed);
        
        // Regenerate world and carrot world with saved seed
        World ourWorld = new World(seed);
        CarrotWorld c = new CarrotWorld(seed);
        
        // Initialize movement controllers
        AvatarMoves movement = new AvatarMoves(ourWorld, c);
        CarrotMoves carrotMovement = new CarrotMoves(ourWorld, c);
        
        // Set up rendering and load saved positions
        ter.initialize(100, 60);
        movement.loadAvatar();
        carrotMovement.spawnCarrotAvatar();
        loadCarrots(ourWorld, carrotMovement);
        
        // Render initial state
        ter.renderFrame(movement.world);
        
        // Initialize game loop variables
        int carrotX = movement.avatarCoord.x;
        int carrotY = movement.avatarCoord.y;
        long counter = 0;
        boolean renderCarrot = false;

        // Main game loop for loaded game
        while (true) {
            // Check for carrot collection
            for (Coord i : ourWorld.carrotCoord) {
                if (i.x == movement.avatarCoord.x && i.y == movement.avatarCoord.y) {
                    renderCarrot = true;
                    carrotX = i.x;
                    carrotY = i.y;
                    break;
                }
            }
            
            // Process input and render appropriate world
            moveAvatar(renderCarrot, seed, movement, carrotMovement, ter, ourWorld);
            if (renderCarrot) {
                counter += 1;
                ter.renderFrame(movement.carrotWorld);
                hUDisplay(movement.carrotWorld, renderCarrot, carrotMovement.finalCarrotCount);
            } else {
                ter.renderFrame(movement.world);
                hUDisplay(movement.world, renderCarrot, carrotMovement.finalCarrotCount);
            }
            
            StdDraw.pause(100);

            // Return to main world after carrot world timer expires
            if (counter == (120)) {
                renderCarrot = false;
                counter = 0;
                c.respawnCarrot();
                movement.removeCarrot(carrotX, carrotY);
                
                // Remove collected carrot
                ArrayList<Coord> copy = new ArrayList<>(ourWorld.carrotCoord);
                for (Coord p : copy) {
                    if (p.x == carrotX && p.y == carrotY) {
                        ourWorld.carrotCoord.remove(p);
                    }
                }
                System.out.println(ourWorld.carrotCoord.toString());
            }
        }
    }

    /**
     * Extracts the seed from the save file.
     * 
     * Reads the first line of save.txt and parses the seed value
     * from the comma-separated format.
     * 
     * @param seed Default seed value (unused parameter)
     * @return The seed string from the save file
     */
    public static String getSeed(String seed) {
        In save = new In("./save.txt");

        while (save.hasNextLine()) {
            String[] splitLine = save.readLine().split(",");
            seed = splitLine[0];
        }
        return seed;
    }

    /**
     * Loads carrot positions from the save file.
     * 
     * Reads the save file and places carrots at their saved coordinates
     * in the world. Carrots are stored starting from index 5 in the CSV.
     * 
     * @param w The world to place carrots in
     * @param move The carrot movement controller
     */
    public static void loadCarrots(World w, CarrotMoves move) {
        In file = new In("./save.txt");

        String currentLine = file.readLine();
        String[] splitLine = currentLine.split(",");
        ArrayList<Coord> carrotCoordsCopy = new ArrayList<Coord>();

        // Parse carrot coordinates (starting from index 5)
        for (int i = 5; i < splitLine.length; i += 2) {
            int carrotX = Integer.parseInt(splitLine[i]);
            int carrotY = Integer.parseInt(splitLine[i + 1]);
            move.world[carrotX][carrotY] = Tileset.CARROT;
            carrotCoordsCopy.add(new Coord(carrotX * carrotX, carrotX, carrotY));
        }

        w.carrotCoord = carrotCoordsCopy;
    }

    /**
     * Renders a beautiful rose pattern using mathematical rose curves.
     * 
     * Creates a decorative flower pattern using the mathematical formula:
     * r = sin(nθ) where n determines the number of petals.
     * 
     * @source https://introcs.cs.princeton.edu/java/15inout/Rose.java.html
     */
    public static void drawRose() {
        StdDraw.clear(new Color(108, 162, 87));
        int n = 5;  // Number of rose petals
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(-1, +1);
        StdDraw.setYscale(-1, +1);
        
        // Draw multiple layers of rose curves with different colors and thicknesses
        
        // Layer 1: Thick pink lines
        StdDraw.setPenRadius(0.15);
        StdDraw.setPenColor(new Color(231, 103, 182));
        double x0 = 0, y0 = 0;
        for (double t = 0.0; t <= 360.0; t += 0.1) {
            double theta = Math.toRadians(t);
            double x1 = Math.sin(n * theta) * Math.cos(theta);
            double y1 = Math.sin(n * theta) * Math.sin(theta);
            StdDraw.line(x0, y0, x1, y1);
            x0 = x1;
            y0 = y1;
        }

        // Layer 2: Medium pink lines
        StdDraw.setPenRadius(0.08);
        StdDraw.setPenColor(new Color(255, 149, 217));
        double x2 = 0, y2 = 0;
        for (double t = 360.0; t >= 0; t -= 0.1) {
            double theta = Math.toRadians(t);
            double x3 = Math.sin(n * theta) * Math.cos(theta);
            double y3 = Math.sin(n * theta) * Math.sin(theta);
            StdDraw.line(x2, y2, x3, y3);
            x0 = x3;
            y0 = y3;
        }

        // Layer 3: Thin light pink lines
        StdDraw.setPenRadius(0.06);
        StdDraw.setPenColor(new Color(255, 192, 232));
        double x5 = 0, y5 = 0;
        for (double t = 0.0; t <= 360.0; t += 0.1) {
            double theta = Math.toRadians(t);
            double x6 = Math.sin(n * theta) * Math.cos(theta);
            double y6 = Math.sin(n * theta) * Math.sin(theta);
            StdDraw.line(x5, y5, x6, y6);
            x5 = x6;
            y5 = y6;
        }

        // Layer 4: Very thin pink lines
        StdDraw.setPenRadius(0.04);
        StdDraw.setPenColor(new Color(231, 103, 182));
        double x7 = 0, y7 = 0;
        for (double t = 360.0; t >= 0; t -= 0.1) {
            double theta = Math.toRadians(t);
            double x8 = Math.sin(n * theta) * Math.cos(theta);
            double y8 = Math.sin(n * theta) * Math.sin(theta);
            StdDraw.line(x7, y7, x8, y8);
            x5 = x8;
            y5 = y8;
        }

        // Layer 5: Finest pink lines
        StdDraw.setPenRadius(0.02);
        StdDraw.setPenColor(new Color(255, 149, 217));
        double x9 = 0, y9 = 0;
        for (double t = 0.0; t <= 360.0; t += 0.1) {
            double theta = Math.toRadians(t);
            double x10 = Math.sin(n * theta) * Math.cos(theta);
            double y10 = Math.sin(n * theta) * Math.sin(theta);
            StdDraw.line(x9, y9, x10, y10);
            x9 = x10;
            y9 = y10;
        }

        StdDraw.show();
        StdDraw.disableDoubleBuffering();
        
        // Final layer: Half rose pattern
        StdDraw.setPenRadius(0.00004);
        StdDraw.setPenColor(new Color(255, 192, 232));
        double x11 = 0, y11 = 0;
        for (double t = 0.0; t <= 180.0; t += 0.1) {
            double theta = Math.toRadians(t);
            double x12 = Math.sin(n * theta) * Math.cos(theta);
            double y12 = Math.sin(n * theta) * Math.sin(theta);
            StdDraw.line(x11, y11, x12, y12);
            x5 = x11;
            y5 = y11;
        }
    }

    /**
     * Draws the central yellow circle in the rose pattern.
     * 
     * Creates a layered circular design with multiple shades of yellow
     * to simulate the center of a flower.
     */
    public static void drawCircle() {
        StdDraw.setXscale();
        StdDraw.setYscale();
        
        // Large yellow circle
        StdDraw.setPenRadius(0.3);
        StdDraw.setPenColor(new Color(255, 217, 3));
        StdDraw.point(0.5, 0.5);
        
        // Medium orange circle
        StdDraw.setPenRadius(0.2);
        StdDraw.setPenColor(new Color(246, 167, 21));
        StdDraw.point(0.5, 0.5);
        
        // Small yellow circle
        StdDraw.setPenRadius(0.1);
        StdDraw.setPenColor(new Color(255, 217, 3));
        StdDraw.point(0.5, 0.5);
    }
}

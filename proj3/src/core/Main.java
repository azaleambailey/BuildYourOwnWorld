package core;

import tileengine.TERenderer;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main entry point for the Build Your Own Garden (BYOG) game.
 * 
 * This class serves as the application launcher and initializes the game environment.
 * It creates the tile renderer and starts the game with the title screen.
 * 
 * @author Azalea Bailey
 * @version 1.0
 */
public class Main {
    
    /**
     * Main method that launches the BYOG game application.
     * 
     * This method:
     * 1. Creates a new TERenderer instance for 2D tile rendering
     * 2. Displays the title screen with game options
     * 3. Initializes the keyboard input handler for user interaction
     * 
     * @param args Command line arguments (not used in this application)
     * @throws IOException If there's an error during file I/O operations
     */
    public static void main(String[] args) throws IOException {
        // Initialize the tile renderer for 2D graphics
        TERenderer ter = new TERenderer();

        // Display the title screen and start the game
        Game.titlePage();
        
        // Initialize keyboard input handler with default coordinates
        // The game will prompt for a seed and handle all subsequent input
        Game.keyboard(" ", new Coord(0, 0, 0), new Coord(0, 0, 0), new ArrayList<Coord>(), ter);
    }
}
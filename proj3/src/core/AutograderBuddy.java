package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */


    //calls arraifyWorld to symbolize our world as an array
    public static TETile[][] getWorldFromInput(String input) {
        String seed = "";
        ArrayList<Character> moves = new ArrayList<>();
        //have i saved before?
        File f = new File("./save.txt");
        boolean saved = f.exists();
        AvatarMoves movement;
        World w;

        if (input.charAt(0) == 'L' || input.charAt(0) == 'l' && saved) {
            seed = Game.getSeed("");
            w = new World(seed);
            movement = new AvatarMoves(w, new CarrotWorld(seed));
            movement.loadAvatar();
            input = input.substring(1);
            for (Character c : input.toCharArray()) { //create list of moves
                moves.add(c);
            }
        } else {
            for (Character c : input.toCharArray()) { //create list of moves
                moves.add(c);
            }
            char first = moves.get(0);
            while (first != 's' && first != 'S') { // && moves.get(0) != ':') {
                seed += moves.remove(0);
                first = moves.get(0);
            }
            seed += moves.remove(0);
            w = new World(seed);
            movement = new AvatarMoves(w, new CarrotWorld(seed));
            movement.spawnAvatar();
        }

        doTheMoves(movement, moves, seed);

        return arraifyWorld(w, movement);
    }

    // turn world 2D array into string
    public static TETile[][] arraifyWorld(World w, AvatarMoves movement) {
        TETile[][] warr = movement.world;
        StringBuilder world = new StringBuilder();
        for (TETile[] teTiles : warr) {
            for (TETile tile : teTiles) {
                world.append(tile.character);
            }
            world.append("\n");
        }

        System.out.println(world);
        return warr;
    }

    //execute all the moves inputted from keyboard
    public static void doTheMoves(AvatarMoves movement, ArrayList<Character> moves, String seed) {
        while (!moves.isEmpty()) {
            switch (moves.get(0)) {
                case 'W':
                    movement.avatarUp();
                    break;
                case 'A':
                    movement.avatarLeft();
                    break;
                case 'S':
                    movement.avatarDown();
                    break;
                case 'D':
                    movement.avatarRight();
                    break;
                case 'w':
                    movement.avatarUp();
                    break;
                case 'a':
                    movement.avatarLeft();
                    break;
                case 's':
                    movement.avatarDown();
                    break;
                case 'd':
                    movement.avatarRight();
                    break;
                case ':':
                    moves.remove(0);
                    fakeSave(seed, movement.avatarCoord, movement.duckCoord);
                    break;
                default:
                    break;
            }
            moves.remove(0);
        }
    }

    //saves where the duck avatar and the gardener avatar are on the map in a .txt file
    public static void fakeSave(String seed, Coord avatarCoord, Coord duckCoord) {
        try {
            PrintWriter saveTxt = new PrintWriter("save.txt");

            if (seed.charAt(0) != ('n')) {
                seed = seed.substring(0, seed.length());
            } else {
                seed = seed.substring(1, seed.length());
            }

            saveTxt.write(seed + "," + avatarCoord.x + "," + avatarCoord.y + "," + duckCoord.x + "," + duckCoord.y);
            saveTxt.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    //Used to tell the autograder which tiles are the floor/ground (including any lights/items resting on the ground). Change this method if you add additional tiles
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.GRASS.character()
                || t.character() == Tileset.HALLDIRT.character()
                || t.character() == Tileset.DIRT.character();
    }

    //Used to tell the autograder while tiles are the walls/boundaries. Change this method if you add additional tiles.
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.FLOWER.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
package core;

import tileengine.TERenderer;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Runs this file to play the game.
 */

public class Main {
    public static void main(String[] args) throws IOException {
        TERenderer ter = new TERenderer();

        Game.titlePage();
        Game.keyboard(" ", new Coord(0, 0, 0), new Coord(0, 0, 0), new ArrayList<Coord>(), ter);
    }

}
package core;

import java.util.TreeMap;

/**
 * Used to store each room in our game.
 */

public class PythagoreanTree extends TreeMap<Double, Coord> {

    public PythagoreanTree() {
        super();
    }

    //stores the coordinate of uniquely located rooms
    public void putCoord(int x, int y) {
        double c = pythagorean(x, y);
        if (containsKey(c)) {
            put(c + 1, new Coord(size(), x, y));
        }
        put(c, new Coord(size(), x, y));
        // System.out.println(c + this.get(c).x + this.get(c).y);
    }

    //returns the hypotenuse of the inputs
    public double pythagorean(int a, int b) {
        return Math.sqrt((a * a) + (b * b));
    }
}


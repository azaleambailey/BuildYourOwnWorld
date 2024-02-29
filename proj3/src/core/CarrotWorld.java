package core;

import tileengine.TETile;
import tileengine.Tileset;
import java.util.Random;

public class CarrotWorld { //constructor
    int width = 100;
    int height = 60;
    TETile[][] carrotWorld; //2D array that stores the tiles that represent carrot world
    TETile red = Tileset.RED; //red checker tile
    TETile white = Tileset.WHITE; //white checker tile
    Random rand; //pseudo random number general
    long seed; //number that determines what seed our pseudo-randomly generated world is based on
    int scale; //determines the size of the carrot in carrot world
    Coord carrotStem; //coordinate where the carrot stem is located
    int carrotSquares; //number of tiles that the carrot in carrot world is made of

    public CarrotWorld(String seed) { //initializes carrot world
        carrotWorld = new TETile[width][height];
        carrotSquares = 0;

        this.seed = parseInput(seed);
        rand = new Random(this.seed);
        scale = rand.nextInt(5 - 2 + 1) + 2;

        fillBackground();
        tableSetter();
    }

    //handles the format of how the seed is inputted in the menu
    public long parseInput(String input) {
        if (input.charAt(0) != ('n') && input.charAt(0) != 'N') {
            this.seed = Long.parseLong(input.substring(0, input.length() - 1));
        } else {
            this.seed = Long.parseLong(input.substring(1, input.length() - 1));
        }
        return this.seed;
    }

    //creates the picnic table background
    public void fillBackground() {
        TETile first = red;
        TETile second = white;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (Math.floorMod(y, 2) == 0) {
                    carrotWorld[x][y] = first;
                } else {
                    carrotWorld[x][y] = second;
                }
            }
            TETile temp = first;
            first = second;
            second = temp;
        }
    }

    //creates plate, fork, and napkin on the picnic table
    public void tableSetter() {
        int centerX = width / 3;
        int centerY = height / 2;


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                //plate
                double distance = Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
                if (distance <= 20) {
                    // This tile is within the circle
                    carrotWorld[x][y] = Tileset.LIGHTGRAY; /* your logic to modify the tile */;
                }
                if (distance <= 14) {
                    // This tile is within the circle
                    carrotWorld[x][y] = Tileset.GRAY; /* your logic to modify the tile */;

                }

                //napkin
                if (x > 61 && x < 84 && y < 50 && y > 10) {
                    carrotWorld[x][y] = Tileset.NAPKIN;
                }

                //fork handle
                if (y < 38 && y > 15 && x > 71 && x < 75) {
                    carrotWorld[x][y] = Tileset.DARKGRAY;
                }
                //fork tine
                if (y < 45 && y > 37 && (x == 69 || x == 77 || x == 73)) {
                    carrotWorld[x][y] = Tileset.DARKGRAY;
                }
                //fork tine
                if (y < 40 && y > 37 && x > 69 && x < 77) {
                    carrotWorld[x][y] = Tileset.DARKGRAY;
                }

                carrotMaker(x, y);

            }
        }

    }

    //creates the carrot the gardener avatar in carrot world will eat
    public void carrotMaker(int x, int y) {
        int centerX = width / 3;
        int centerY = height / 2;
        int xDistance = scale * (int) Math.round(Math.cos(Math.toRadians(60.0)));
        int yDistance = scale * (int) Math.round(Math.sin(Math.toRadians(60.0)));

        int[] pointOne = new int[]{centerX - xDistance, centerY + yDistance};
        int[] pointTwo = new int[]{centerX + xDistance, centerY + yDistance};
        int[] pointThree = new int[]{centerX, centerY - yDistance};

        if (isInsideTriangle(x, y, pointOne, pointTwo, pointThree)) {
            carrotWorld[x][y] = Tileset.ORANGE;
            carrotSquares += 1;
        }

        carrotWorld[centerX][centerY + yDistance + 1] = Tileset.GREEN;
        carrotStem = new Coord(centerX * centerX, centerX, centerY + yDistance + 1);
    }

    //helper function to create a triangular shaped carrot
    private boolean isInsideTriangle(int x, int y, int[] point1, int[] point2, int[] point3) {
        double d1, d2, d3;
        boolean hasNeg, hasPos;

        d1 = sign(x, y, point1[0], point1[1], point2[0], point2[1]);
        d2 = sign(x, y, point2[0], point2[1], point3[0], point3[1]);
        d3 = sign(x, y, point3[0], point3[1], point1[0], point1[1]);

        hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0);

        return !(hasNeg && hasPos);
    }

    //respawns the carrot the gardener avatar eats in carrot world after the gardener avatar exits carrot world
    public void respawnCarrot() {
        carrotSquares = 0;
        carrotWorld[carrotStem.x][carrotStem.y] = Tileset.GRAY;

        int centerX = width / 3;
        int centerY = height / 2;
        scale = rand.nextInt(5 - 2 + 1) + 2;
        int xDistance = scale * (int) Math.round(Math.cos(Math.toRadians(60.0)));
        int yDistance = scale * (int) Math.round(Math.sin(Math.toRadians(60.0)));

        int[] pointOne = new int[]{centerX - xDistance, centerY + yDistance};
        int[] pointTwo = new int[]{centerX + xDistance, centerY + yDistance};
        int[] pointThree = new int[]{centerX, centerY - yDistance};

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isInsideTriangle(x, y, pointOne, pointTwo, pointThree)) {
                    carrotWorld[x][y] = Tileset.ORANGE;
                    carrotSquares += 1;
                }
            }
        }

        carrotWorld[centerX][centerY + yDistance + 1] = Tileset.GREEN;
        carrotStem = new Coord(centerX, centerX, centerY + yDistance + 1);
    }

    //calculates the 3 coordinate points of the carrot triangle
    private double sign(int x1, int y1, int x2, int y2, int x3, int y3) {
        return (x1 - x3) * (y2 - y3) - (x2 - x3) * (y1 - y3);
    }

}
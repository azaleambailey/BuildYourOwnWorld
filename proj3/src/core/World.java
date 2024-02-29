
package core;

import tileengine.TETile;
import tileengine.Tileset;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.abs;

public class World { //constructor
    int width = 100; //width of our world
    int height = 60; //height of our world
    int maxRoomSize = 30; //maximum room size of the possible rooms in our world
    int minRoomSize = 13; //minimum room size of the possible rooms in our world
    int maxFalse = 10000; //parameter that determines how many times we try to randomly load valid rooms in our world
    TETile wall = Tileset.FLOWER; //tile that represents walls in our world
    TETile floor = Tileset.DIRT; //tile that represents the floor in our world
    TETile hall = Tileset.HALLDIRT; //tile that represents the hall in our world
    TETile outside = Tileset.GRASS; //tile that represents the grass in our world
    TETile carrot = Tileset.CARROT; //tile that represents what the carrot the gardener avatar can eat
    long seed; //what we base our pseudo-randomness on
    TETile[][] world; //2D array where we stored the tiles that represent our world
    Random rand; //random number generator
    int numFalse; //counter that tracks how many times we attempted to generate a valid room
    PythagoreanTree roomCenters; //stores the coordinates where each room is located
    ArrayList<Coord> carrotCoord; //stores the coordinates where carrots are located

    //generates the rooms, hallways, and background in our world pseudo-randomly based on the seed given by the user
    public World(String input) {
        world = new TETile[width][height];
        roomCenters = new PythagoreanTree();
        this.seed = parseInput(input);
        rand = new Random(this.seed);
        numFalse = 0;

        fillBackground();
        roomMaker(rand.nextInt(width), rand.nextInt(height));
        makeHallways();
    }

    //takes the string seed input and stores it as a long data type
    public long parseInput(String input) {
        if (input.charAt(0) != ('n') && input.charAt(0) != 'N') {
            this.seed = Long.parseLong(input.substring(0, input.length() - 1));
        } else {
            this.seed = Long.parseLong(input.substring(1, input.length() - 1));
        }
        return this.seed;
    }

    //pseudo-randomly populates the world with carrots
    public void pickCarrot() {
        carrotCoord = new ArrayList<>();
        int x = rand.nextInt(width);
        int y = rand.nextInt(height);
        int carrotNum = rand.nextInt(7 - 2 + 1) + 2;

        for (int i = 0; i < carrotNum; i++) {
            while (world[x][y] != floor) {
                x = rand.nextInt(width);
                y = rand.nextInt(height);
            }
            world[x][y] = carrot;
            carrotCoord.add(new Coord(x * x + y * y, x, y));
        }
    }

    //fills the background of the world with grass
    public void fillBackground() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = outside;
            }
        }
    }

    /**
     * Makes room if number of invalid room attempts is less than 60
     * Calls roomMakerHelper to make the room
     *
     * @param coordX pseudorandom x generated from rand.nextInt(width)
     * @param coordY pseudorandom y generated from rand.nextInt(height)
     */
    public void roomMaker(int coordX, int coordY) {
        while (numFalse < maxFalse) {
            roomMakerHelper(coordX, coordY);
            coordX = rand.nextInt(width);
            coordY = rand.nextInt(height);
        }
    }

    /**
     * Makes a room based on pseudorandom width and height and provided x and y
     * if room rules are satisfied (if makes call to makeRoomOrNot returns true)
     * if rooms not satisfied: increments numFalse
     *
     * @param coordX pseudorandom x generated from rand.nextInt(width)
     * @param coordY pseudorandom y generated from rand.nextInt(height)
     */
    public void roomMakerHelper(int coordX, int coordY) {
        //rand.nextInt(max - min + 1) + min
        int roomW = rand.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;
        int roomH = rand.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;

        int xMin = coordX + 4;
        int xMax = coordX + roomW - 4;

        int yMin = coordY + 4;
        int yMax = coordY + roomH - 4;

        ArrayList<int[]> wallTiles = new ArrayList<>();
        if (makeRoomOrNot(coordX, coordY, roomW, roomH)) {
            for (int x = xMin; x < xMax; x++) {
                for (int y = yMin; y < yMax; y++) {
                    world[x][y] = wall;
                    wallTiles.add(new int[]{x, y});
                    makeFloor(xMin, xMax, yMin, yMax);
                }
            }
            pickDirt(xMin + 1, xMax - 2, yMin + 1, yMax - 2);
            //pickWall(wallTiles);
        } else {
            numFalse += 1;
        }
    }

    /**
     * Makes floor for room
     */
    public void makeFloor(int xMin, int xMax, int yMin, int yMax) {
        for (int x = xMin + 1; x < xMax - 1; x++) {
            for (int y = yMin + 1; y < yMax - 1; y++) {
                world[x][y] = floor;
            }
        }
    }

    /**
     * checks if we can a room of size roomW by roomH in location (coordX, coordY)
     * <p>
     * Rules:
     * - not touching a world edge
     * - room doesn't fall off the world
     * - room atleast n tiles from other rooms [NEED TO IMPLEMENT]
     *
     * @param coordX
     * @param coordY
     * @param roomW
     * @param roomH
     */
    public boolean makeRoomOrNot(int coordX, int coordY, int roomW, int roomH) {
        //implement +-3 border
        for (int x = coordX; x < coordX + roomW; x++) {
            for (int y = coordY; y < coordY + roomH; y++) {
                boolean one = world[x][y].equals(wall);
                boolean two = world[x][y].equals(floor);
                if (x == 0 || y == 0 || x >= width - 1 || y >= height - 1 || one || two) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * picks and stores some random dirt tile
     */
    public void pickDirt(int xMin, int xMax, int yMin, int yMax) {
        int x = rand.nextInt(xMax - xMin + 1) + xMin;
        int y = rand.nextInt(yMax - yMin + 1) + yMin;
        boolean flip = pickDirtValid(x, y);
        while (!flip) {
            x = rand.nextInt(xMax - xMin + 1) + xMin;
            y = rand.nextInt(yMax - yMin + 1) + yMin;
            flip = pickDirtValid(x, y);
        }
        roomCenters.putCoord(x, y);
    }

    //checks if a room has already been made in the randomly selected location
    public boolean pickDirtValid(int x, int y) {
        for (Coord c : roomCenters.values()) {
            if (abs(c.x - x) == 1 || abs(c.y - y) == 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * makes the hallways
     *
     * @return
     */
    public void makeHallways() {

        Map.Entry<Double, Coord> firstCoord = roomCenters.pollFirstEntry();
        Map.Entry<Double, Coord> secondCoord = roomCenters.pollFirstEntry();

        while (secondCoord != null) {
            makeHallwaysHelper(firstCoord, secondCoord);
            firstCoord = secondCoord;
            secondCoord = roomCenters.pollFirstEntry();
        }
    }

    /**
     * connects room one('s room center coords) to room two('s room center coords)
     */
    public void makeHallwaysHelper(Map.Entry<Double, Coord> room1, Map.Entry<Double, Coord> room2) {
        int x1 = room1.getValue().x;
        int x2 = room2.getValue().x;
        int y1 = room1.getValue().y;
        int y2 = room2.getValue().y;

        if (x1 < x2 && y1 < y2) {
            downAndLeft(x1, y1, x2, y2);
        }

        if (x2 < x1 && y1 < y2) {
            downAndRight(x1, y1, x2, y2);
        }

        if (x2 < x1 && y2 < y1) {
            upAndRight(x1, y1, x2, y2);
        }

        if (x1 < x2 && y2 < y1) {
            upAndLeft(x1, y1, x2, y2);
        }

        if (y1 == y2) {
            horizontal(y1, x1, x2);
        }

        if (x1 == x2) {
            vertical(x1, y1, y2);
        }
    }

    //helper function that builds a hallway if the room is up and to the left of the previous room
    public void upAndLeft(int x1, int y1, int x2, int y2) {
        for (int y = y2; y < y1; y++) {
            world[x2][y] = hall;
            makeWall(x2, y, true);

        }
        for (int x = x1; x < x2 + 1; x++) {
            world[x][y1] = hall;
            makeWall(y1, x, false);

        }

        makeWallCorners(x2 + 1, y1, true);

    }

    //helper function that builds a hallway if the room is up and to the right of the previous room
    public void upAndRight(int x1, int y1, int x2, int y2) {
        for (int y = y2; y < y1; y++) {
            world[x2][y] = hall;
            makeWall(x2, y, true);

        }

        for (int x = x2; x < x1; x++) {
            world[x][y1] = hall;
            makeWall(y1, x, false);

        }

        makeWallCorners(x2 - 1, y1, true);

    }

    //helper function that builds a hallway if the room is down and to the left of the previous room
    public void downAndLeft(int x1, int y1, int x2, int y2) {
        for (int y = y2; y > y1; y--) {
            world[x2][y] = hall;
            makeWall(x2, y, true);


        }

        for (int x = x2; x > x1; x--) {
            world[x][y1] = hall;
            makeWall(y1, x, false);

        }

        makeWallCorners(x2 + 1, y1, false);

    }

    //helper function that builds a hallway if the room is down and to the right of the previous room
    public void downAndRight(int x1, int y1, int x2, int y2) {
        for (int y = y2; y > y1; y--) {
            world[x2][y] = hall;
            makeWall(x2, y, true);

        }

        for (int x = x2; x < x1; x++) {
            world[x][y1] = hall;
            makeWall(y1, x, false);

        }

        makeWallCorners(x2 - 1, y1, false);
    }

    //helper function that builds a hallway if the room center shares the same y coordinate as the previous room
    public void vertical(int x, int y1, int y2) {
        for (int y = Math.min(y1, y2); y < Math.max(y1, y2); y++) {
            world[x][y] = hall;
            makeWall(x, y, true);

        }
    }

    //helper function that builds a hallway if the room center shares the same x coordinate as the previous room
    public void horizontal(int y, int x1, int x2) {
        for (int x = Math.min(x1, x2); x < Math.max(x1, x2); x++) {
            world[x][y] = hall;
            makeWall(y, x, false);

        }
    }


    //builds the flower wall around the hallway
    public void makeWall(int stagnant, int change, boolean xStagnant) {
        if (xStagnant) {
            if (world[stagnant - 1][change] == outside) {
                world[stagnant - 1][change] = wall;
            }
            if (world[stagnant + 1][change] == outside) {
                world[stagnant + 1][change] = wall;
            }
        } else {
            if (world[change][stagnant - 1] == outside) {
                world[change][stagnant - 1] = wall;
            }
            if (world[change][stagnant + 1] == outside) {
                world[change][stagnant + 1] = wall;
            }
        }
    }

    //makes the walls around hallways that are corners
    public void makeWallCorners(int x, int y, boolean up) {
        if (world[x][y] == outside) {
            world[x][y] = wall;
        }
        if (up && world[x][y + 1] == outside) {
            world[x][y + 1] = wall;
        }
        if (!up && world[x][y - 1] == outside) {
            world[x][y - 1] = wall;
        }

    }

}

package core;

import edu.princeton.cs.algs4.In;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;
public class AvatarMoves { //constructor
    TETile[][] world; //2D array that stores the tiles that represent our world
    TETile[][] carrotWorld; //2D array that stores the tiles that represent the carrot world
    TETile prev; //previous tile that the gardener avatar was on
    TETile prevDuck; //previous tile that the duck avatar was on
    Coord avatarCoord; //x, y coordinate of the gardener avatar
    Coord duckCoord; //x, y coordinate of the duck avatar
    TETile avatar = Tileset.AVATAR; //tile that represents the gardener avatar
    TETile duck = Tileset.DUCK; //tile that represents the duck avatar
    TETile wall = Tileset.FLOWER; //tile that represents the walls in our world
    TETile room = Tileset.DIRT; //tile that represents the rooms in our world
    TETile hall = Tileset.HALLDIRT; //tile that represents the halls in our world
    TETile outside = Tileset.GRASS; //tile that represents the outside in our world
    TETile carrot = Tileset.CARROT; //tile that represents carrots in our world
    long seed; //number that determines what seed our pseudo-randomly generated world is based on
    int width; //width of our world
    int height; //height of our world
    Random rand; //our world's random number generator

    //initializes our world
    public AvatarMoves(World w, CarrotWorld c) {
        avatarCoord = new Coord(0, 0, 0);
        duckCoord = new Coord(0, 0, 0);
        this.width = w.width;
        this.height = w.height;
        this.rand = new Random(seed);
        this.world = w.world;
        this.carrotWorld = c.carrotWorld;
        this.seed = w.seed;
    }

    //handles gardener avatar and duck avatar moving logic
    public void spawnAvatar() {
        int x = rand.nextInt(width);
        int y = rand.nextInt(height);
        while (world[x][y] == wall || world[x][y] == outside) {
            x = rand.nextInt(width);
            y = rand.nextInt(height);
        }
        avatarCoord = new Coord(x * x + y * y, x, y);
        prev = world[x][y];
        world[x][y] = avatar;

        int x1 = rand.nextInt(width);
        int y1 = rand.nextInt(height);
        while (world[x1][y1] == wall || world[x1][y1] == outside || world[x1][y1] == avatar) {
            x1 = rand.nextInt(width);
            y1 = rand.nextInt(height);
        }
        duckCoord = new Coord(x1 * x1 + y1 * y1, x1, y1);
        prevDuck = world[x1][y1];
        world[x1][y1] = duck;
    }

    //removes carrot from our world once the gardener avatar moves on the tile
    public void removeCarrot(int x, int y) {
        world[x][y] = room;
    }

    //loads avatars into our world after the game is saved
    public void loadAvatar() {
        In file = new In("./save.txt");

        String currentLine = file.readLine();
        String[] splitLine = currentLine.split(",");

        int avatarX = Integer.parseInt(splitLine[1]);
        int avatarY = Integer.parseInt(splitLine[2]);
        avatarCoord = new Coord(avatarX * avatarX + avatarY * avatarY, avatarX, avatarY);
        prev = world[avatarX][avatarY];
        world[avatarX][avatarY] = avatar;

        int duckX = Integer.parseInt(splitLine[3]);
        int duckY = Integer.parseInt(splitLine[4]);
        duckCoord = new Coord(duckX * duckX + duckY * duckY, duckX, duckY);
        prevDuck = world[duckX][duckY];
        world[duckX][duckY] = duck;
    }

    //checks if the gardener avatar can move onto a tile (can't move on a wall tile)
    public boolean validMove(TETile t) {
        return t.equals(hall) || t.equals(room) || t.equals(carrot);
    }

    //handles the moving gardener avatar up logic
    public void avatarUp() {
        TETile temp = world[avatarCoord.x][avatarCoord.y + 1];

        if (validMove(temp)) {
            world[avatarCoord.x][avatarCoord.y + 1] = avatar;
            world[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.y += 1;
        }
    }

    //handles the moving gardener avatar down logic
    public void avatarDown() {
        TETile temp = world[avatarCoord.x][avatarCoord.y - 1];

        if (validMove(temp)) {
            world[avatarCoord.x][avatarCoord.y - 1] = avatar;
            world[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.y -= 1;
        }
    }

    //handles the moving gardener avatar left logic
    public void avatarLeft() {
        TETile temp = world[avatarCoord.x - 1][avatarCoord.y];

        if (validMove(temp)) {
            world[avatarCoord.x - 1][avatarCoord.y] = avatar;
            world[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.x -= 1;
        }
    }

    //handles the moving gardener avatar right logic
    public void avatarRight() {
        TETile temp = world[avatarCoord.x + 1][avatarCoord.y];

        if (validMove(temp)) {
            world[avatarCoord.x + 1][avatarCoord.y] = avatar;
            world[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.x += 1;
        }
    }

    //handles the moving duck avatar up logic
    public void duckUp() {
        TETile temp = world[duckCoord.x][duckCoord.y + 1];

        if (validMove(temp)) {
            world[duckCoord.x][duckCoord.y + 1] = duck;
            world[duckCoord.x][duckCoord.y] = prevDuck;
            prevDuck = temp;
            duckCoord.y += 1;
        }
    }

    //handles the moving duck avatar left logic
    public void duckLeft() {
        TETile temp = world[duckCoord.x - 1][duckCoord.y];

        if (validMove(temp)) {
            world[duckCoord.x - 1][duckCoord.y] = duck;
            world[duckCoord.x][duckCoord.y] = prevDuck;
            prevDuck = temp;
            duckCoord.x -= 1;
        }
    }

    //handles the moving duck avatar down logic
    public void duckDown() {
        TETile temp = world[duckCoord.x][duckCoord.y - 1];

        if (validMove(temp)) {
            world[duckCoord.x][duckCoord.y - 1] = duck;
            world[duckCoord.x][duckCoord.y] = prevDuck;
            prevDuck = temp;
            duckCoord.y -= 1;
        }
    }

    //handles the moving duck avatar right logic
    public void duckRight() {
        TETile temp = world[duckCoord.x + 1][duckCoord.y];

        if (validMove(temp)) {
            world[duckCoord.x + 1][duckCoord.y] = duck;
            world[duckCoord.x][duckCoord.y] = prevDuck;
            prevDuck = temp;
            duckCoord.x += 1;
        }
    }
}
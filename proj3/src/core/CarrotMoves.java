package core;

import tileengine.TETile;
import tileengine.Tileset;
import java.util.ArrayList;
import java.util.Random;

public class CarrotMoves {//constructor
    TETile[][] world; //2D array that stores the tiles that represent our world
    TETile[][] carrotWorld; //2D array that stores the tiles that represent carrot world
    TETile prev; //previous tile that the gardener avatar was on
    Coord avatarCoord; //x, y coordinate of the gardener avatar
    TETile avatar = Tileset.AVATAR; //tile that represents the gardener avatar
    long seed; //number that determines what seed our pseudo-randomly generated world is based on
    int width; //width of our world
    int height; //height of our world
    Random rand;//our world's pseudo random number generator
    ArrayList<Coord> carrotCoords; //list of coordinates that have a carrot tile
    int eatenCarrotCounter; //number of carrots eaten by gardener avatar
    int finalCarrotCount; //number of carrots to be displayed

    //initializes carrot world
    public CarrotMoves(World w, CarrotWorld c) {
        avatarCoord = new Coord(0, 0, 0);
        this.width = w.width;
        this.height = w.height;
        this.rand = w.rand;
        this.world = w.world;
        this.carrotWorld = c.carrotWorld;
        this.seed = w.seed;
        this.carrotCoords = new ArrayList<>();
        eatenCarrotCounter = 0;
        finalCarrotCount = 0;
    }

    //spawns our gardener avatar in carrot world
    public void spawnCarrotAvatar() {
        int x = width / 3;
        int y = 14;
        avatarCoord = new Coord(x * x + y * y, x, y);
        prev = carrotWorld[x][y];
        carrotWorld[x][y] = avatar;
    }

    //gardener avatar can only move on the plate in carrot world
    public boolean validMove(TETile t) {
        return (t.equals(Tileset.ORANGE) || t.equals(Tileset.GREEN) || t.equals(Tileset.GRAY) || t.equals(Tileset.LIGHTGRAY));
    }

    //handles the moving gardener avatar up logic in carrot world
    public void avatarCarrotUp() {
        TETile temp = carrotWorld[avatarCoord.x][avatarCoord.y + 1];

        if (validMove(temp)) {
            carrotWorld[avatarCoord.x][avatarCoord.y + 1] = avatar;
            carrotWorld[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.y += 1;
        }
        if (prev == Tileset.ORANGE) {
            prev = Tileset.GRAY;
            eatenCarrotCounter += 1;
        }
    }

    //handles the moving gardener avatar down logic in carrot world
    public void avatarCarrotDown() {
        TETile temp = carrotWorld[avatarCoord.x][avatarCoord.y - 1];

        if (validMove(temp)) {
            carrotWorld[avatarCoord.x][avatarCoord.y - 1] = avatar;
            carrotWorld[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.y -= 1;
        }

        if (prev == Tileset.ORANGE) {
            prev = Tileset.GRAY;
            eatenCarrotCounter += 1;
        }
    }

    //handles the moving gardener avatar left logic in carrot world
    public void avatarCarrotLeft() {
        TETile temp = carrotWorld[avatarCoord.x - 1][avatarCoord.y];

        if (validMove(temp)) {
            carrotWorld[avatarCoord.x - 1][avatarCoord.y] = avatar;
            carrotWorld[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.x -= 1;
        }

        if (prev == Tileset.ORANGE) {
            prev = Tileset.GRAY;
            eatenCarrotCounter += 1;
        }
    }

    //handles the moving gardener avatar right logic in carrot world
    public void avatarCarrotRight() {
        TETile temp = carrotWorld[avatarCoord.x + 1][avatarCoord.y];

        if (validMove(temp)) {
            carrotWorld[avatarCoord.x + 1][avatarCoord.y] = avatar;
            carrotWorld[avatarCoord.x][avatarCoord.y] = prev;
            prev = temp;
            avatarCoord.x += 1;
        }

        if (prev == Tileset.ORANGE) {
            prev = Tileset.GRAY;
            eatenCarrotCounter += 1;
        }
    }

}

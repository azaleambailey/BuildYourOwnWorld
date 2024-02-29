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
 * Runs the interactive game. Handles keyboard inpurts.
 */
public class Game {

    //runs the game and renders the world onto the screen
    public static void runGame(TERenderer ter) throws IOException {
        String seed = takeInput();
        World ourWorld = new World(seed);
        ourWorld.pickCarrot();
        CarrotWorld c = new CarrotWorld(seed);
        AvatarMoves movement = new AvatarMoves(ourWorld, c);
        CarrotMoves carrotMovement = new CarrotMoves(ourWorld, c);
        ter.initialize(100, 60);
        movement.spawnAvatar();
        carrotMovement.spawnCarrotAvatar();
        ter.renderFrame(movement.world);
        int carrotX = movement.avatarCoord.x;
        int carrotY = movement.avatarCoord.y;

        long counter = 0;
        boolean renderCarrot = false;
        while (true) {
            for (Coord i : ourWorld.carrotCoord) {
                if (i.x == movement.avatarCoord.x && i.y == movement.avatarCoord.y) {
                    renderCarrot = true;
                    carrotX = i.x;
                    carrotY = i.y;
                    break;
                }
            }
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

            if (counter == (120)) {
                renderCarrot = false;
                counter = 0;
                c.respawnCarrot();
                movement.removeCarrot(carrotX, carrotY);
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

    //handles the keyboard inputs for the gardener and duck avatar
    public static void moveAvatar(boolean c, String s, AvatarMoves M, CarrotMoves m, TERenderer t, World w) throws IOException {
        if (StdDraw.hasNextKeyTyped()) {
            switch (StdDraw.nextKeyTyped()) {
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
                case ':':
                    keyboard(s, M.avatarCoord, M.duckCoord, w.carrotCoord, t);
                    break;
                default:
                    break;
            }
        }
    }

    //displays the type of tile that the user's mouse is hovering over
    public static void hUDisplay(TETile[][] world, boolean carrot, int count) {
        TETile curr;
        double mx = Math.floor(StdDraw.mouseX());
        double my = Math.floor(StdDraw.mouseY());

        if (mx >= 100 || my >= 60 || mx < 0 || my < 0) {
            curr = world[99][59];
        } else {
            curr = world[(int) mx][(int) my];
        }

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

    //helper function to render which tile the user's mouse is hovering over on the screen
    public static void hUDisplayer(String tileType, boolean carrot, int count) {
        if (carrot) {
            StdDraw.setPenColor(191, 65, 65);
            StdDraw.filledRectangle(7.4, 59, 6.8, .75);
            StdDraw.setPenColor(Color.white);
        } else {
            StdDraw.setPenColor(140, 199, 94);
            StdDraw.filledRectangle(7.4, 59, 6.8, .75);
            StdDraw.setPenColor(243, 78, 182);
        }
        StdDraw.textLeft(1, 59,"Current Tile: " + tileType);
        StdDraw.show();
    }

    //renders the menu page on the user's screen
    public static void titlePage() {
        StdDraw.setCanvasSize();

        drawRose();

        drawCircle();

        StdDraw.setPenColor(new Color(117, 68, 43));

        Font titleFont = new Font("Arial", Font.BOLD, 60);
        StdDraw.setFont(titleFont);
        StdDraw.text(0.5, 0.58, "BYOG");

        Font subtitleFont = new Font("Arial", Font.ITALIC, 18);
        StdDraw.setFont(subtitleFont);
        StdDraw.text(0.5, 0.52, "Build Your Own Garden");

        Font optionsFont = new Font("Arial", 1, 22);
        StdDraw.setFont(optionsFont);
        StdDraw.text(0.5, 0.42, "New Game (N)");
        StdDraw.text(0.5, 0.37, "Load Game (L)");
        StdDraw.text(0.5, 0.32, "Quit Game (Q)");

        StdDraw.show();
    }

    //renders the input seed page onto the user's screen
    public static String takeInput() {
        String input = "";
        boolean flip = true;
        while (flip) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();

                if (key == KeyEvent.VK_BACK_SPACE && input.length() > 0) {
                    input = input.substring(0, input.length() - 1);
                } else {
                    input += key;
                }

                StdDraw.clear(StdDraw.BLACK);
                StdDraw.setPenColor(StdDraw.WHITE);

                Font titleFont = new Font("Arial", Font.BOLD, 36);
                StdDraw.setFont(titleFont);
                StdDraw.text(0.5, 0.6, "Enter Seed Below:");

                Font optionsFont = new Font("Arial", Font.PLAIN, 24);
                StdDraw.setFont(optionsFont);
                StdDraw.text(0.5, 0.4, input);
                StdDraw.show();
                if (key == 's' || key == 'S') {
                    flip = false;
                }
            }
        }
        System.out.println(input);
        return input;
    }

    //handles saving, quitting, and reloading the game from save
    public static void keyboard(String s, Coord aC, Coord dC, ArrayList<Coord> cC, TERenderer ter) throws IOException {
        Font titleFont;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                switch (StdDraw.nextKeyTyped()) {
                    case 'N', 'n':
                        StdDraw.clear(StdDraw.BLACK);
                        StdDraw.setPenColor(StdDraw.WHITE);

                        titleFont = new Font("Arial", Font.BOLD, 36);
                        StdDraw.setFont(titleFont);
                        StdDraw.text(0.5, 0.6, "Enter Seed Below:");
                        runGame(ter);
                        break;

                    case 'Q', 'q':
                        saveGame(s, aC, dC, cC, ter);
                        break;
                    case 'L', 'l':
                        loadGame(ter);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //saves the game (avatar coordinates,  duck coordinates, non-eaten carrots) to a .txt file
    public static void saveGame(String s, Coord aC, Coord dC, ArrayList<Coord> cC, TERenderer ter) throws IOException {
        try {
            PrintWriter saveTxt = new PrintWriter("save.txt");

            if (s.charAt(0) != ('n')) {
                s = s.substring(0, s.length());
            } else {
                s = s.substring(1, s.length());
            }

            saveTxt.write(s + "," + aC.x + "," + aC.y + "," + dC.x + "," + dC.y);
            for (Coord c : cC) {
                saveTxt.write("," + c.x + "," + c.y);
            }
            saveTxt.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        titlePage();
        keyboard(s, aC, dC, cC, ter);
    }

    //loads the game with the saved avatar coordinates, duck coordinates, and non-eaten carrots
    public static void loadGame(TERenderer ter) throws IOException {
        String seed = getSeed("");
        System.out.println(seed);
        World ourWorld = new World(seed);
        CarrotWorld c = new CarrotWorld(seed);
        AvatarMoves movement = new AvatarMoves(ourWorld, c);
        CarrotMoves carrotMovement = new CarrotMoves(ourWorld, c);
        ter.initialize(100, 60);
        movement.loadAvatar();
        carrotMovement.spawnCarrotAvatar();
        loadCarrots(ourWorld, carrotMovement);
        ter.renderFrame(movement.world);
        int carrotX = movement.avatarCoord.x;
        int carrotY = movement.avatarCoord.y;
        long counter = 0;
        boolean renderCarrot = false;

        while (true) {
            for (Coord i : ourWorld.carrotCoord) {
                if (i.x == movement.avatarCoord.x && i.y == movement.avatarCoord.y) {
                    renderCarrot = true;
                    carrotX = i.x;
                    carrotY = i.y;
                    break;
                }
            }
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

            if (counter == (120)) {
                renderCarrot = false;
                counter = 0;
                c.respawnCarrot();
                movement.removeCarrot(carrotX, carrotY);
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

    //gets seed from saved game file
    public static String getSeed(String seed) {
        In save = new In("./save.txt");

        while (save.hasNextLine()) {
            String[] splitLine = save.readLine().split(",");
            seed = splitLine[0];
        }
        return seed;
    }

    //loads the non-eaten carrots to the saved game
    public static void loadCarrots(World w, CarrotMoves move) {
        In file = new In("./save.txt");

        String currentLine = file.readLine();
        String[] splitLine = currentLine.split(",");
        ArrayList<Coord> carrotCoordsCopy = new ArrayList<Coord>();

        for (int i = 5; i < splitLine.length; i += 2) {
            int carrotX = Integer.parseInt(splitLine[i]);
            int carrotY = Integer.parseInt(splitLine[i + 1]);
            move.world[carrotX][carrotY] = Tileset.CARROT;
            carrotCoordsCopy.add(new Coord(carrotX * carrotX, carrotX, carrotY));
        }

        w.carrotCoord = carrotCoordsCopy;
    }

    //@source https://introcs.cs.princeton.edu/java/15inout/Rose.java.html
    //renders the flower menu screen
    public static void drawRose() {
        StdDraw.clear(new Color(108, 162, 87));
        int n = 5;
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(-1, +1);
        StdDraw.setYscale(-1, +1);
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

    //draws the yellow center of the flower
    public static void drawCircle() {
        StdDraw.setXscale();
        StdDraw.setYscale();
        StdDraw.setPenRadius(0.3);
        StdDraw.setPenColor(new Color(255, 217, 3));
        StdDraw.point(0.5, 0.5);
        StdDraw.setPenRadius(0.2);
        StdDraw.setPenColor(new Color(246, 167, 21));
        StdDraw.point(0.5, 0.5);
        StdDraw.setPenRadius(0.1);
        StdDraw.setPenColor(new Color(255, 217, 3));
        StdDraw.point(0.5, 0.5);
    }
}

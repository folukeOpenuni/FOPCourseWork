/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.bradford.spacegame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 * The GameEngine class is responsible for managing information about the game,
 * creating levels, the player, aliens and asteroids, as well as updating information
 * when a key is pressed while the game is running.
 * @author prtrundl
 */
public class GameEngine {

    /**
     * An enumeration type to represent different types of tiles that make up
     * the level. Each type has a corresponding image file that is used
     * to draw the right tile to the screen for each tile in a level. Space is
     * open for the player and asteroids to move into, black holes will kill the
     * player if they move into the tile and destroy asteroids that move into them,
     * pulsars will damage the player if they are in or adjacent to a pulsar
     * tile while it is active.
     */
    public enum TileType {
        SPACE, BLACK_HOLE, PULSAR_ACTIVE, PULSAR_INACTIVE
    }

    /**
     * The width of the level, measured in tiles. Changing this may
     * cause the display to draw incorrectly, and as a minimum the size of the
     * GUI would need to be adjusted.
     */
    public static final int GRID_WIDTH = 25;
    
    /**
     * The height of the level, measured in tiles. Changing this may
     * cause the display to draw incorrectly, and as a minimum the size of the
     * GUI would need to be adjusted.
     */
    public static final int GRID_HEIGHT = 18;
    
    /**
     * The chance of a black hole being generated instead of open space when generating
     * the level. 1.0 is 100% chance, 0.0 is 0% chance. This can be changed to
     * affect the difficulty.
     */
    private static final double BLACK_HOLE_CHANCE = 0.07;
    
    /**
     * The chance of a pulsar being created instead of open space when generating
     * the level. 1.0 is 100% chance, 0.0 is 0% chance. This can be changed to affect
     * the difficulty.
     */
    private static final double PULSAR_CHANCE = 0.03;

    /**
     * A random number generator that can be used to include randomised choices
     * in the creation of levels, in choosing places to spawn the player, aliens and
     * asteroids, and to randomise movement or other factors.
     */
    private Random rng = new Random(911);

    /**
     * The number of levels cleared by the player in this game. Can be used to
     * generate harder games as the player clears levels.
     */
    private int cleared = 0;
    
    /**
     * The number of points the player has gained this level. Used to track when
     * the current level is won and a new one should be generated.
     */
    private int points = 0;
    
    /**
     * Tracks the current turn number. Used to control pulsar activation and
     * asteroid movement.
     */
    private int turnNumber = 1;
    
    /**
     * The GUI associated with a GameEngine object. THis link allows the engine
     * to pass level (tiles) and entity information to the GUI to be drawn.
     */
    private GameGUI gui;

    /**
     * The 2 dimensional array of tiles the represent the current level.
     * The size of this array should use the GRID_HEIGHT and GRID_WIDTH attributes when it is created.
     */
    private TileType[][] tiles;
    
    /**
     * An ArrayList of Point objects used to create and track possible locations
     * to spawn the player, aliens and asteroids.
     */
    private ArrayList<Point> spawns;

    /**
     * A Player object that is the current player. This object stores the state
     * information for the player, including hull strength and the current position (which
     * is a pair of co-ordinates that corresponds to a tile in the current level)
     */
    private Player player;
    
    /**
     * An array of Alien objects that represents the aliens in the current
     * level. Elements in this array should be of the type Alien,
     * meaning that an alien is alive and needs to be drawn or moved, or should
     * be null which means nothing is drawn or processed for movement.
     * Null values in this array are skipped during drawing and movement processing.
     */
    private Alien[] aliens;
    
    /**
     * An array of Asteroid objects that represents the asteroids in the current
     * level. Elements in this array should be of the type Asteroid,
     * meaning that an asteroid exists and needs to be drawn or moved, or should
     * be null which means nothing is drawn or processed for movement.
     * Null values in this array are skipped during drawing and movement processing.
     */
    private Asteroid[] asteroids;

    /**
     * Constructor that creates a GameEngine object and connects it with a GameGUI
     * object.
     * @param gui The GameGUI object that this engine will pass information to in
     * order to draw levels and entities to the screen.
     */
    public GameEngine(GameGUI gui) {
        this.gui = gui;
        startGame();
    }

    /**
     * Generates a new level. The method builds a 2D array of TileTypes
     * that will be used to draw tiles to the screen and to add a variety of
     * elements into each level. Tiles can be space, black holes, active pulsars
     * or inactive pulsars. This method should contain the implementation of an
     * algorithm to create an interesting and varied level each time it is called.
     * @return A 2D array of TileTypes representing the tiles in the current
     * level of the dungeon. The size of this array should use the width and
     * height attributes of the level specified by GRID_WIDTH and GRID_HEIGHT.
     */
    private TileType[][] generateLevel() {
        
        TileType[][] tiles = new TileType[GRID_WIDTH][GRID_HEIGHT];
        TileType[][] blackHolesTiles = new TileType[GRID_WIDTH][GRID_HEIGHT];
//        TileType[][] activePulsarsTiles = new TileType[GRID_WIDTH][GRID_HEIGHT];
//        TileType[][] inactivePulsarsTiles = new TileType[GRID_WIDTH][GRID_HEIGHT];
        
        
        for (int i = 0; i < GRID_WIDTH; i++) {
            
            for (int j = 0; j < GRID_HEIGHT; j++) {

           tiles[i][j] = TileType.SPACE;
           //tiles[10][10] = TileType.BLACK_HOLE;

//           tiles[i][j] = TileType.BLACK_HOLE;
//           tiles[i][j] = TileType.PULSAR_ACTIVE;
//           tiles[i][j] = TileType.PULSAR_INACTIVE;
            }            
        }
        
        for (int x = 1; x < GRID_WIDTH; x+=3){
            for (int y = 1; y < GRID_HEIGHT; y+=3) {
                    //blackHolesTiles[x][y] = TileType.BLACK_HOLE;
                tiles[x][y] = TileType.BLACK_HOLE;
   
            }
        }
        
        
        //tiles[10][10] = TileType.BLACK_HOLE;
        tiles[1][7] = TileType.PULSAR_INACTIVE;
        tiles[10][10] = TileType.PULSAR_INACTIVE;
        tiles[5][5] = TileType.PULSAR_ACTIVE;
        
        return tiles;            //modify this to return a dynamically generated 2D array
       
    }
    
    /**
     * Generates spawn points for entities. The method processes
     * the tiles array and finds tiles that are suitable for spawning, i.e.
     * space tiles. Suitable tiles should be added
     * to the ArrayList that will be returned as Point objects - Points are a
     * simple kind of object that contain an X and a Y co-ordinate stored using
     * the int primitive type.
     * @return An ArrayList containing Point objects representing suitable X
     * and Y co-ordinates in the current level that entities can be spawned in.
     */
    private ArrayList<Point> getSpawns() {
        ArrayList<Point> s = new ArrayList<Point>();
        //add code here to generate Point objects witrh suitable X and Y co-ordinates
        
        return s;
    }

    /**
     * Spawns aliens in suitable locations in the current level. The method
     * uses the spawns ArrayList to pick suitable positions to add aliens,
     * removing these positions from the spawns ArrayList as they are used
     * (using the remove() method) to avoid multiple entities spawning in the
     * same location. The method creates aliens by instantiating the Alien
     * class, setting health and the X and Y position for the alien using the
     * Point object removed from the spawns ArrayList.
     * @return An array of Alien objects representing the aliens for the current level
     */
    private Alien[] spawnAliens() {
        return null;            //modify to return array of Alien objects
    }

    /**
     * Spawns a Player entity in the game. The method uses the spawns ArrayList
     * to select a suitable location to spawn the player and removes the Point
     * from the spawns ArrayList. The method instantiates the Player class and
     * assigns values for the health and position of the player.
     * @return A Player object representing the player in the game
     */
    private Player spawnPlayer() {
        return null;            //modify to return a Player object
    }

    /**
     * Handles the movement of the player when attempting to move left in the
     * game. This method is called by the InputHandler class when the
     * user has pressed the left arrow key on the keyboard. The method checks
     * whether the tile to the left of the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new position.
     * If the tile to the left of the player is not empty the method will not
     * update the player position, but could make other changes to the game.
     */
    public void movePlayerLeft() {
        
    }

    /**
     * Handles the movement of the player when attempting to move right in the
     * game. This method is called by the InputHandler class when the
     * user has pressed the right arrow key on the keyboard. The method checks
     * whether the tile to the right of the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new position.
     * If the tile to the right of the player is not empty the method will not
     * update the player position, but could make other changes to the game.
     */
    public void movePlayerRight() {
        
    }

    /**
     * Handles the movement of the player when attempting to move up in the
     * game. This method is called by the InputHandler class when the
     * user has pressed the up arrow key on the keyboard. The method checks
     * whether the tile above the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new position.
     * If the tile above the player is not empty the method will not
     * update the player position, but could make other changes to the game.
     */
    public void movePlayerUp() {
        
    }

    /**
     * Handles the movement of the player when attempting to move right in the
     * game. This method is called by the InputHandler class when the
     * user has pressed the down arrow key on the keyboard. The method checks
     * whether the tile below the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new position.
     * If the tile below the player is not empty the method will not
     * update the player position, but could make other changes to the game.
     */
    public void movePlayerDown() {
        
    }
    
    /**
     * Updates the position of Asteroid objects by altering their X and Y co-ordinates
     * according to their moveDirection attribute value. This iterates over the
     * asteroids array one element at a time, checks if the current element is
     * null (skipping it if it is null) and finding the moveDirection value for the
     * current asteroid object. Asteroids with a moveDirection value other than
     * NONE should have their position updated accordingly, and if their new
     * position puts them outside the map or inside a black hole they are
     * "destroyed". Destroyed asteroids should be replaced by creating a new,
     * randomly positioned asteroid in the same index of the asteroids array that
     * the destroyed asteroid used to occupy.
     */
    private void moveAsteroids() {
    
    }

    /**
     * Moves all aliens on the current level. The method checks for non-null
     * elements in the aliens array and calls the moveAlien method for each one that
     * is not null.
     */
    private void moveAliens() {
        
    }

    /**
     * Moves a specific alien in the game. The method updates the X and Y
     * attributes of the alien to reflect its new position.
     * @param a The Alien that needs to be moved
     */
    private void moveAlien(Alien a) {
        
    }

    /**
     * Spawns asteroids in suitable locations in the current level. The method
     * uses the spawns ArrayList to pick suitable positions to add asteroids,
     * removing these positions from the spawns ArrayList as they are used
     * (using the remove() method) to avoid multiple entities spawning in the
     * same location. The method creates asteroids by repeatedly instantiating the Asteroid
     * class and setting the X and Y position for the asteroid using the
     * Point object removed from the spawns ArrayList.
     * @return An array of Asteroid objects representing the asteroids for the current level
     */
    private Asteroid[] spawnAsteroids() {
        return null;            //modify to return an array of Asteroid objects
    }
    
    /**
     * Processes the tiles array to find inactive pulsars and change them to
     * active pulsars. When a tile is found of the correct type, that tile is
     * set to PULSAR_ACTIVE. When the map is drawn to the screen next the
     * inactive pulsar will now be an active pulsar.
     */
    private void activatePulsars() {
        
    }
    
    /**
     * Processes the tiles array to find active pulsars and change them to
     * inactive pulsars. When a tile is found of the correct type, that tile is
     * set to PULSAR_INACTIVE. When the map is drawn to the screen next the
     * active pulsar will now be an inactive pulsar.
     */
    private void deactivatePulsars() {
        
    }
    
    /**
     * Damages the player if the player is in an active pulsar tile, or any of
     * the eight tiles adjacent to the active pulsar, when this method is called.
     * The method uses the player's current x and y position and searches around
     * the player looking for pulsar tiles. Any pulsar tiles found this way
     * result in a call to the changeHullStrength method for the player object
     * to damage the player.
     */
    private void pulsarDamage() {
        
    }

    /**
     * Called in response to the player collecting enough points win the current
     * level. The method increases the valued of cleared by one, resets the
     * value of points to zero, generates a new level by calling the generateLevel
     * method, fills the spawns ArrayList with suitable spawn locations, then
     * spawns aliens and asteroids. Finally it places the player in the new level
     * by calling the placePlayer() method.
     * Note that a new player object should not be created here as this will reset
     * the player's health to maximum.
     */
    private void newLevel() {
        
    }

    /**
     * Places the player in a level by choosing a spawn location from the
     * spawns ArrayList, removing the spawn position as it is used. The method sets
     * the players position in the level by calling its setPosition method with the
     * x and y values of the Point taken from the spawns ArrayList.
     */
    private void placePlayer() {
        
    }

    /**
     * Performs a single turn of the game when the user presses a key on the
     * keyboard. This method activates or deactivates pulsars periodically by
     * using the turn attribute, moves any aliens and asteroids and then checks
     * if the player is dead, exiting the game or resetting it. It checks if the
     * player has collected enough asteroids to win the level and calls the
     * method if it does. Finally it requests the GUI to redraw the game level
     * by passing it the tiles, player, aliens and asteroids for the current level.
     */
    public void doTurn() {
        if (turnNumber % 20 == 0)
            activatePulsars();
        if (turnNumber % 20 == 5)
            deactivatePulsars();
        if (turnNumber % 10 == 5) {
            moveAsteroids();
        }
        moveAliens();
        pulsarDamage();
        if (player.getHullStrength()< 1) {
            System.exit(0);
        }
        if (points >= 5) {
            newLevel();
        }
        gui.updateDisplay(tiles, player, aliens, asteroids);
        turnNumber++;
    }

    /**
     * Starts a game. This method generates a level, finds spawn positions in
     * the level, spawns aliens, asteroids and the player and then requests the
     * GUI to update the level on screen using the information on tiles, player,
     * asteroids and aliens.
     */
    public void startGame() {
        tiles = generateLevel();
        spawns = getSpawns();
        asteroids = spawnAsteroids();
        aliens = spawnAliens();
        player = spawnPlayer();
        gui.updateDisplay(tiles, player, aliens, asteroids);
    }
}

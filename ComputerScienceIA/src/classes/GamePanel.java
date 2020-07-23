package classes;

// graphics imports
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
// action event imports for keyboard controls
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
// shape imports
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JOptionPane;
// Java Swing imports
import javax.swing.JPanel;
import javax.swing.Timer;
// file handling imports
import java.io.File;

/**
 * GamePanel.java - contains the main game loop and variables
 *
 * @author Darwin
 */
public final class GamePanel extends JPanel implements KeyListener {

    // object instances
    private Drone drone;
    private Line2D.Double laser;
    private Rectangle2D.Double zone;
    private Rectangle2D.Double miniMap;
    private BasicStroke laserStroke;
    private Line2D.Double deltaInd;
    private final Timer gameLoop;
    private Font textHUD;
    private Enemy[] enemy;
    private Ellipse2D.Double[] enemyBlip;
    private Point2D.Double[] star;
    private Rectangle2D.Double[] building;
    private AudioPlayer audioPlayer;
    private Rectangle2D.Double view;
    private final Timer waveTimer;
    // constants
    private final int DRONE_SIZE = 20;
    private final int X_CENTER = 900;
    private final int Y_CENTER = 500;
    private final int ZONE_SIZE = 5000;
    private final int MAP_SIZE = ZONE_SIZE / 10;
    private final int MAX_ENEMY_AMOUNT = 20;
    private final double gravity = 0.2;
    private final int HUD_TEXT_HEIGHT = 40;
    // variables
    private double rotationDelta = 0;
    private double xDelta = 0;
    private double yDelta = 0;
    private double thrust = 0;
    private double recoil = 0;
    private int ammo = 50;
    private double xCam = 0;
    private double yCam = 0;
    private int enemiesDead = 0;
    private int waveNumber = 1;
    private boolean enableHUD = true;
    private int score = 0;
    // sound files
    private File shoot;
    private File explosion;

    /**
     * Default Constructor
     */
    GamePanel() {
        // add key listener to the JPanel
        addKeyListener(this);
        // set background to black
        this.setBackground(Color.BLACK);
        // make the JPanel focusable
        this.setFocusable(true);
        // game loop timer occurs every 20 milliseconds
        gameLoop = new Timer(20, (ActionEvent ae) -> {
            // move the drone
            moveDrone();
            // move the laser
            moveLaser();
            // check for collisions between the drone and a wall
            checkWallCollisions();
            // calculate enemy collisions with lasers
            checkForEnemyDeaths();
            // calculate the enemies' movement
            moveEnemy();
            // update the heads up display
            updateHUD();
            // update the camera position
            updateCamera();
            // repaint all graphics
            GamePanel.this.repaint();
        });

        /**
         * the wave timer is activated once a wave is completed
         *
         */
        waveTimer = new Timer(1000, (ActionEvent ae) -> {
            // wave number increases
            waveNumber++;
            // number of dead enemies is reset to zero
            enemiesDead = 0;
            // spawn a new wave of enemies
            for (int k = 0; k < enemy.length; k++) {
                // the enemies are initialized 
                // (they obtain new velocities and sizes)
                // their velocities are dependent on the wave number
                enemy[k] = new Enemy(waveNumber);
                // place the enemies in random locations at the top of the map
                enemy[k].setFrame(Math.random()
                        * (ZONE_SIZE - enemy[k].getSize()),
                        -ZONE_SIZE * 0.8 + Math.random()
                        * -ZONE_SIZE * 0.2,
                        enemy[k].getSize(), enemy[k].getSize());
            }

        });
        // the wave timer does not repeat as it should only run once per wave
        waveTimer.setRepeats(false);
    }

    /**
     * Initialize class instances
     */
    private void initClasses() {
        // create drone instance 
        drone = new Drone(DRONE_SIZE);
        // create laser instance
        laser = new Line2D.Double();
        // create the playing zone
        zone = new Rectangle2D.Double(0, -ZONE_SIZE, ZONE_SIZE, ZONE_SIZE);
        // set the location of the drone
        drone.setLocation(ZONE_SIZE / 2, -DRONE_SIZE);
        // create minimap rectangle instance
        miniMap = new Rectangle2D.Double();
        // create a new stroke with a thickness of 5
        laserStroke = new BasicStroke(5);
        // create instance of velocity indicator
        deltaInd = new Line2D.Double();
        // set the font of the HUD text
        textHUD = new Font("Monospaced", 0, 17);
        // create instance of view rectangle (what the player sees onscreen)
        view = new Rectangle2D.Double(0, 0, X_CENTER * 2, Y_CENTER * 2);
        // array of enemies is initialized
        enemy = new Enemy[MAX_ENEMY_AMOUNT];
        for (int i = 0; i < enemy.length; i++) {
            // create a new enemy class for each index
            enemy[i] = new Enemy(waveNumber);
            // set the enemies in random spots at the top of the map
            enemy[i].setFrame(Math.random() * (ZONE_SIZE - enemy[i].getSize()),
                    -ZONE_SIZE * 0.8 + Math.random() * -ZONE_SIZE * 0.2,
                    enemy[i].getSize(), enemy[i].getSize());
        }
        // array of enemies radar blips is initialized
        enemyBlip = new Ellipse2D.Double[MAX_ENEMY_AMOUNT];
        for (int i = 0; i < enemyBlip.length; i++) {
            // create a new ellipse for each index
            enemyBlip[i] = new Ellipse2D.Double();
        }
        // array of stars is initialized
        star = new Point2D.Double[(int) ZONE_SIZE / 5];
        for (int i = 0; i < star.length; i++) {

            star[i] = new Point2D.Double(Math.random() * ZONE_SIZE,
                    Math.random() * -ZONE_SIZE);
        }
        // array of buildings is initialized
        building = new Rectangle2D.Double[10];
        for (int i = 0; i < building.length; i++) {
            double height = 100 + Math.random() * 500;
            building[i] = new Rectangle2D.Double(Math.random() * (ZONE_SIZE - height),
                    0 - height, height / (2 + Math.random()), height);
        }

        // audio audioPlayer
        audioPlayer = new AudioPlayer();

        // sound effects
        shoot = new File("src/sounds/laser.mp3");
        explosion = new File("src/sounds/explosion.mp3");
    }

    /**
     * Paints shapes on the panel
     *
     * @param g the graphics class
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // check if the HUD is toggled on
        if (enableHUD) {
            // set colour to green for HUD
            g2d.setColor(Color.GREEN);
            // draw minimap
            g2d.draw(miniMap);
            // draw velocity vector
            g2d.draw(deltaInd);
            // set the font size for the HUD
            g2d.setFont(textHUD);
            // print drone throttle
            g2d.drawString("THR: " + (int) (thrust * 50 / gravity) + "%", 300, 300);
            // print drone horizontal movement
            g2d.drawString("HORIZ: " + (int) (Math.abs(xDelta * 7))
                    + "m/s", 300, 300 + HUD_TEXT_HEIGHT * 1);
            // print drone vertical movement
            g2d.drawString("VERT: " + (int) (-yDelta * 7) + "m/s", 300,
                    300 + HUD_TEXT_HEIGHT * 2);
            // print drone altitude
            g2d.drawString("ALT: " + (int) (-drone.getY() - DRONE_SIZE)
                    / 10 + "m", 300, 300 + HUD_TEXT_HEIGHT * 3);
            // print ammo rounds left
            g2d.drawString("LSR: " + ammo, 300, 300 + HUD_TEXT_HEIGHT * 4);
            // draw enemy blip on minimap
            for (Ellipse2D.Double i : enemyBlip) {
                g2d.draw(i);
            }
        }

        // commence camera tracking 
        // (making a transition from rendering HUD elements to rendering the game world)
        g2d.translate(-xCam, -yCam);
        // set color to gray
        g2d.setColor(Color.GRAY);
        // draw stars that appear onscreen
        for (Point2D.Double i : star) {
            if (view.contains(i)) {
                g2d.draw(new Line2D.Double(i, i));
            }
        }

        // draw buildings that appear onscreen
        for (Rectangle2D.Double i : building) {
            if (view.intersects(i)) {
                g2d.draw(i);
            }

        }

        // set color to orange for the engine exhaust
        g2d.setColor(Color.ORANGE);
        // the width of the exhaust brush stroke depends on the thrust
        g2d.setStroke(new BasicStroke((float) thrust * 20));
        // draw thruster exhaust
        g2d.draw(new Line2D.Double(drone.getX() + (DRONE_SIZE / 2),
                drone.getY() + (DRONE_SIZE / 2),
                drone.getX() + (DRONE_SIZE / 2) + thrust * 100 * Math.random()
                * (-Math.sin(Math.toRadians(drone.getAngle()))),
                drone.getY() + (DRONE_SIZE / 2) + thrust * 100 * Math.random()
                * (Math.cos(Math.toRadians(drone.getAngle())))));
        // brush stroke is set to normal width
        g2d.setStroke(new BasicStroke());
        // color is set to white
        g2d.setColor(Color.WHITE);
        // draw boundaries of playing zone
        if (view.intersects(zone)) {
            g2d.draw(zone);
        }

        // draw the drones transformations and rotations
        g2d.draw(drone.getTransformedInstance());

        // set the drawing color to red
        g2d.setColor(Color.RED);
        // draw enemies which appear onscreen
        for (Rectangle2D.Double i : enemy) {
            if (view.intersects(i)) {
                // draw the enemy rectangles
                g2d.draw(i);
            }
        }

        // set the drawing color to yellow
        g2d.setColor(Color.YELLOW);
        // the brush is set to a thicker stroke
        g2d.setStroke(laserStroke);
        // check if the laser is within the boundaries
        if (view.contains(laser.getP1())) {
            if (zone.contains(laser.getP1())) {
                // draw the laser
                g2d.draw(laser);
            }

        }
        // dispose of the drawn objects
        g2d.dispose();
    }

    /**
     * Detects when keys are typed
     *
     * @param ke
     */
    @Override
    public void keyTyped(KeyEvent ke
    ) {

    }

    /**
     * Detects when keys are pressed
     *
     * @param ke
     */
    @Override
    public void keyPressed(KeyEvent ke
    ) {
        // A pressed
        if (ke.getKeyCode() == KeyEvent.VK_A) {
            // drone rotates left
            rotationDelta = -3;
        }
        // D pressed
        if (ke.getKeyCode() == KeyEvent.VK_D) {
            // drone rotates right
            rotationDelta = 3;
        }
        // W pressed
        if (ke.getKeyCode() == KeyEvent.VK_W) {
            // drone increases thrust to full
            thrust = gravity * 2;
        }
        // S pressed
        if (ke.getKeyCode() == KeyEvent.VK_S) {
            // drone decreases thrust to zero
            thrust = 0;

        }

    }

    /**
     * Detects when keys are released
     *
     * @param ke
     */
    @Override
    public void keyReleased(KeyEvent ke
    ) {
        // S released
        if (ke.getKeyCode() == KeyEvent.VK_A) {
            // drone stops rotating
            rotationDelta = 0;
        }
        // D released
        if (ke.getKeyCode() == KeyEvent.VK_D) {
            // drone stops rotating
            rotationDelta = 0;
        }
        // W released
        if (ke.getKeyCode() == KeyEvent.VK_W) {
            // drone sets thrust to half (hovering)
            thrust = gravity;
        }
        // S released
        if (ke.getKeyCode() == KeyEvent.VK_S) {
            if (drone.getY() + DRONE_SIZE < zone.getMaxY()) {
                // drone sets thrust to half (hovering)
                thrust = gravity;
            }

        }
        // left arrow released
        if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
            // check if ammo is greater than zero
            if (ammo > 0) {
                // creates a laser starting from the drone's center
                laser = new Line2D.Double(
                        drone.getX() + (DRONE_SIZE / 2),
                        drone.getY() + (DRONE_SIZE / 2),
                        drone.getX() + (DRONE_SIZE / 2) + 100
                        * (-Math.cos(Math.toRadians(drone.getAngle()))),
                        drone.getY() + (DRONE_SIZE / 2) + 100
                        * (-Math.sin(Math.toRadians(drone.getAngle()))));

                // gun recoil
                recoil = -1;
                // ammo diminishes
                ammo--;
                // play laser sound effect
                audioPlayer.playSound(shoot);
            }

        }
        // right arrow released
        if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
            // check if ammo is greater than zero
            if (ammo > 0) {
                // create new laser starting from the drone's center
                laser = new Line2D.Double(
                        drone.getX() + (DRONE_SIZE / 2),
                        drone.getY() + (DRONE_SIZE / 2),
                        drone.getX() + (DRONE_SIZE / 2) + 100
                        * (Math.cos(Math.toRadians(drone.getAngle()))),
                        drone.getY() + (DRONE_SIZE / 2) + 100
                        * (Math.sin(Math.toRadians(drone.getAngle()))));

                // gun recoil
                recoil = 1;
                // ammo diminishes
                ammo--;
                // play laser sound effect
                audioPlayer.playSound(shoot);
            }

        }
        // H released (HUD key)
        if (ke.getKeyCode() == KeyEvent.VK_H) {
            // the HUD is toggled to the opposite of what it currently set to
            enableHUD = !enableHUD;
        }

    }

    /**
     * Check for collisions between the drone and a wall
     */
    private void checkWallCollisions() {
        // west wall collision
        if (drone.getX() <= zone.getMinX()) {
            // horizontal velocity set to zero
            xDelta = 0;
            // the drone is moved to the edge of the wall (in case it is stuck)
            drone.setLocation(zone.getMinX(), drone.getY());
        }
        // east wall collision
        if (drone.getX() + DRONE_SIZE >= zone.getMaxX()) {
            // horizontal velocity set to zero
            xDelta = 0;
            // the drone is moved to the edge of the wall
            drone.setLocation(zone.getMaxX() - DRONE_SIZE, drone.getY());
        }
        // top wall collision
        if (drone.getY() <= zone.getMinY()) {
            // vertica velocity is set to zero
            yDelta = 0;
            // drone is moved to wall edge
            drone.setLocation(drone.getX(), zone.getMinY());
        }
        // ground collision
        if (drone.getY() + DRONE_SIZE >= zone.getMaxY()) {
            // thrust is set to zero
            thrust = 0;
            // vertical velocity set to zero
            yDelta = 0;
            // horizontal velocity decays
            xDelta *= 0.9;
            // drone is set at an upright angle
            drone.rotateByDegrees(-drone.getAngle());
            // drone is moved to ground level (in case it was stuck in the ground)
            drone.setLocation(drone.getX(), zone.getMaxY() - DRONE_SIZE);
            // replenish ammo if it is lower than maximum
            if (ammo < 50) {
                // ammo increases by one
                ammo++;
            }
        }
    }

    /**
     * Calculate drone thrust and movement
     */
    private void moveDrone() {
        // calculate horizontal movement using thrust and recoil
        xDelta += thrust * Math.sin(Math.toRadians(drone.getAngle()))
                - recoil * Math.cos(Math.toRadians(drone.getAngle()));
        // calculate vertical movement using thrust, recoil and gravity
        yDelta += thrust * -Math.cos(Math.toRadians(drone.getAngle())) + gravity
                - recoil * Math.sin(Math.toRadians(drone.getAngle()));
        // rotate the drone according to rotation delta
        drone.rotateByDegrees(rotationDelta);
        // drone moves according to calculated deltas
        drone.moveLocatioBy(xDelta, yDelta);

    }

    /**
     * Calculate laser movement
     */
    private void moveLaser() {
        // check if drone has shot a laser
        if (recoil > 0 || recoil < 0) {
            // gun recoil set to zero
            recoil = 0;
        }   // check if the laser is within the boundaries of the playing zone
        if (zone.contains(laser.getP1())) {
            // laser starts moving
            laser = new Line2D.Double(laser.getP2(), new Point2D.Double(
                    laser.getX2() * 2 - laser.getX1(), laser.getY2() * 2 - laser.getY1()));
        }
    }

    /**
     * Calculate movement of HUD elements
     */
    private void updateHUD() {
        // set the minimap HUD
        miniMap.setFrame(X_CENTER - drone.getX() / 10,
                Y_CENTER - drone.getY() / 10 - MAP_SIZE, MAP_SIZE, MAP_SIZE);
        // set the velocity vector indicator
        deltaInd.setLine(X_CENTER, Y_CENTER, X_CENTER + xDelta * 10,
                Y_CENTER + yDelta * 10);
        // adjust radar blips to the number of enemies
        for (int i = 0; i < enemyBlip.length; i++) {
            // check if the enemy is dead
            if (enemy[i].isDead == false) {
                // place a blip on the minimap if the enemy is alive
                enemyBlip[i].setFrame(X_CENTER - drone.getX() / 10
                        + (enemy[i].getX() - enemy[i].getSize() / 5) / 10,
                        Y_CENTER - drone.getY() / 10
                        + (enemy[i].getY() - enemy[i].getSize() / 5) / 10, 5, 5);
            } else if (enemy[i].isDead == true && enemyBlip[i].getWidth() > 0) {
                // the blip is removed if the associated enemy is dead
                enemyBlip[i] = new Ellipse2D.Double();
            }
        }
    }

    /**
     * Calculate enemy movement
     */
    private void moveEnemy() {
        // enemy movement
        for (Enemy i : enemy) {
            // update the enemy class' coordinates
            i.updateCoordinates(i.getX(), i.getY());
            // check if the enemy is alive
            if (i.isDead == false) {
                // move the enemy
                i.setFrame(i.getMinX(), i.getMinY() + i.vSpeed,
                        i.getSize(), i.getSize());
                // if the enemy is touching the ground... 
                if (i.getMaxY() > zone.getMaxY()) {

                    loseGame();
                }
            }
        }
    }

    /**
     * Calculate enemy collisions with lasers, controls enemy wave spawning
     */
    private void checkForEnemyDeaths() {
        // enemy death checks
        for (Enemy i : enemy) {
            // update the enemy class' coordinates
            i.updateCoordinates(i.x, i.y);
            // check if the enemy is touching a laser
            if (laser.intersects(i)) {
                // enemy becomess dead
                i.die();
                // score increases
                score++;
                // the enemy is moved eleswhere
                i.setFrame(0, 0, 0, 0);
                // the audioplayer plays an explosion sound effect
                audioPlayer.playSound(explosion);
                // amount of dead enemies increases
                enemiesDead++;
                // check if all enemies are dead
                if (enemiesDead == MAX_ENEMY_AMOUNT) {
                    // load a new wave
                    waveTimer.start();
                }
            }
        }
    }

    /**
     * Moves the game camera to the position of the drone
     */
    private void updateCamera() {
        // set camera coordinates
        xCam = drone.getX() - X_CENTER + DRONE_SIZE / 2;
        yCam = drone.getY() - Y_CENTER + DRONE_SIZE / 2;
        // the player's viewing rectangle is updated with the camera
        view.setFrame(xCam, yCam, X_CENTER * 2, Y_CENTER * 2);
    }

    /**
     * Restarts the game to a "fresh" state
     */
    public void load() {
        // sets all variables to their default values
        rotationDelta = 0;
        xDelta = 0;
        yDelta = 0;
        thrust = 0;
        recoil = 0;
        ammo = 50;
        xCam = 0;
        yCam = 0;
        enemiesDead = 0;
        waveNumber = 1;
        enableHUD = true;
        score = 0;

        // initialize all classes
        initClasses();

        // start the game loop
        gameLoop.start();
    }

    /**
     * Stops gameplay and goes back to the main menu
     */
    private void loseGame() {
        // stop the game loop
        gameLoop.stop();
        // show game over message
        JOptionPane.showMessageDialog(null,
                "The enemies have invaded the city!\nGame over.\nFinal Score: " + score);
        // go back to the main menu
        Main.mainMenu();
    }

}

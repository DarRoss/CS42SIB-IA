package classes;

import java.awt.geom.Rectangle2D;

/**
 * Enemy.java - variables and methods for the enemies
 *
 * @author Darwin
 */
public class Enemy extends Rectangle2D.Double implements Locator {

    // coordinates of the enemy
    private double xCoord;
    private double yCoord;

    // velocity variable
    double vSpeed;
    // death state boolean
    boolean isDead;
    // size integer
    int size;

    /**
     * constructor for creating a new enemy
     *
     * @param speedMultiplier adjusts the speed of the enemy (higher number
     * means faster speeds)
     */
    Enemy(int speedMultiplier) {
        // set the enemy speed to a random number
        vSpeed = Math.pow(1.7, Math.random() * speedMultiplier);
        // set the enemy size to a random number (40 to 80)
        size = (int) (40 + Math.random() * 40);
        // enemy is set as alive
        isDead = false;
    }

    /**
     * retrieve the enemy's size
     *
     * @return the size
     */
    public double getSize() {
        return size;
    }

    /**
     * set the enemy state to dead
     */
    public void die() {
        // enemy becomes dead
        isDead = true;
    }

    /**
     * retrieve enemy x coordinate
     *
     * @return the enemy's x coordinate
     */
    @Override
    public double getX() {
        return xCoord;
    }

    /**
     * retrieve enemy y coordinate
     *
     * @return the enemy's y coordinate
     */
    @Override
    public double getY() {
        return yCoord;
    }

    /**
     * updates the enemy's coordinates into the class
     *
     * @param x the enemy's y coordinate
     * @param y the enemy's x coordinate
     */
    public void updateCoordinates(double x, double y) {
        xCoord = x;
        yCoord = y;
    }

}

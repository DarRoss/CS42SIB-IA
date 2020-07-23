package classes;

import java.awt.geom.Rectangle2D;

/**
 * Enemy.java - variables and methods for the enemy
 *
 * @author d.ross2
 */
public class Enemy extends Rectangle2D.Double implements Locator {

    private double xCoord;
    private double yCoord;

    // velocity variable
    double vSpeed;
    // death state boolean
    boolean isDead;
    // size integer
    int size;

    public Enemy() {
        // set the enemy speed to a random number (1 to 2)
        vSpeed = 1 + Math.random();
        // set the enemy size to a random number (40 to 80)
        size = (int) (40 + Math.random() * 40);
        // enemy is set to alive
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

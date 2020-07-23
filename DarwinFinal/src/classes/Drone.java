package classes;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

/**
 * Drone.java - the player-controlled drone methods
 *
 * @author d.ross2
 */
public class Drone extends Path2D.Double implements Locator {
    // drone variables
    private double angle = 0;
    private double xCoord = 0;
    private double yCoord = 0;

    /**
     * constructor draws the drone's shape
     *
     * @param size width and height of the drone
     */
    public Drone(int size) {
        // "molar" shape creation
        // start at the top left corner
        moveTo(0, 0);
        // draw to the top right corner
        lineTo(size, 0);
        // draw to the bottom right corner
        lineTo(size, size);
        // draw to the center
        lineTo(size / 2, size / 2);
        // draw to the bottom left corner
        lineTo(0, size);
        // close the path (draw to the top left corner)
        closePath();
    }

    /**
     * retrieve the drone's angle
     *
     * @return the drone's angle
     */
    public double getAngle() {
        return angle;
    }

    /**
     * retrieve the drone's xCoord coordinate
     *
     * @return the xCoord coordinate
     */
    @Override
    public double getX() {
        return xCoord;
    }

    /**
     * retrieve the drone's yCoord coordinate
     *
     * @return the yCoord coordinate
     */
    @Override
    public double getY() {
        return yCoord;
    }

    /**
     * uses the drone's velocity and coordinates to determine next location
     *
     * @param xDelta xCoord velocity
     * @param yDelta yCoord velocity
     */
    public void moveLocatioBy(double xDelta, double yDelta) {
        this.xCoord += xDelta;
        this.yCoord += yDelta;
    }

    /**
     * determine the amount of rotation using current angle and rotation delta
     *
     * @param delta the angle to rotate by
     */
    public void rotateByDegrees(double delta) {
        angle += delta;
    }

    /**
     * places the drone at a set of coordinates
     *
     * @param x the xCoord value to place the drone
     * @param y the yCoord value to place the drone
     */
    public void setLocation(double x, double y) {
        this.xCoord = x;
        this.yCoord = y;
    }

    /**
     * determines the updated drone shape using movement and rotation
     *
     * @return the updated drone shape
     */
    public Shape getTransformedInstance() {
        // create a new transformed shape of the drone path
        AffineTransform at = new AffineTransform();
        // rotate the drone path according to the angle
        at.rotate(Math.toRadians(angle), xCoord + (getBounds().width / 2), yCoord + (getBounds().height / 2));
        // translate the drone path
        at.translate(xCoord, yCoord);
        // return the newly transformed drone path
        return createTransformedShape(at);
    }

}

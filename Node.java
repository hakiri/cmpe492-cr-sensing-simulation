/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public abstract class Node {
   
    /**
     * Position of the node
     */
    protected Point2D.Double position = new Point2D.Double(0,0);
    /**
     * Velocity of the node
     */
    protected double velocity = 0;
    
    /**
     * 
     * @return position of the node
     */
    public Point2D.Double getPosition() {
        return position;
    }
    
    /**
     * 
     * @return Velocity of the node
     */
    public double getVelocity() {
        return velocity;
    }
    
    /**
     * Sets a new position for the node
     * @param position Position of the node
     */
    public void setPosition(Point2D.Double position) {
        this.position = new Point2D.Double(position.x, position.y);
    }
    
    /**
     * Sets a new velocity for the node
     * @param velocity Velocity of the node
     */
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
    
}

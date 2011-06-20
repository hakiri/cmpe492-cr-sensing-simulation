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
    public Double getPosition() {
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
    public void setPosition(Double position) {
        this.position = position;
    }
    
    /**
     * Sets a new velocity for the node
     * @param velocity Velocity of the node
     */
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
    
}

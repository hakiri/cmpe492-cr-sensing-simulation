package Nodes;

import java.awt.geom.Point2D;

/**
 * An abstract class that holds general information about both CR and Primary nodes.
 */
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
     * Id o the Node
     */
    protected int id;
    
    /**
     * Gets the current position of the Node
     * @return position of the node
     */
    public Point2D.Double getPosition() {
        return position;
    }
    
    /**
     * Gets the current velocity of the Node
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
    
    /**
     * Sets an id for the node.
     * @param id 
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Returns the current id.
     * @return Id of the node
     */
    public int getId() {
        return id;
    }
    
    
}

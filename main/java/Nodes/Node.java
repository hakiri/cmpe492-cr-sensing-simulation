package Nodes;

import java.awt.geom.Point2D;

/**
 * An abstract class that holds general information about both CR and Primary nodes.
 */
public interface Node {
    
    /**
     * Gets the current position of the Node
     * @return position of the node
     */
    public Point2D.Double getPosition();
    
    /**
     * Gets the current velocity of the Node
     * @return Velocity of the node
     */
    public double getVelocity();
    
    /**
     * Sets a new position for the node
     * @param position Position of the node
     */
    public void setPosition(Point2D.Double position);
    
    /**
     * Sets a new velocity for the node
     * @param velocity Velocity of the node
     */
    public void setVelocity(double velocity);
    
    /**
     * Sets an id for the node.
     * @param id 
     */
    public void setId(int id);
    
    /**
     * Returns the current id.
     * @return Id of the node
     */
    public int getId();
    
    
}

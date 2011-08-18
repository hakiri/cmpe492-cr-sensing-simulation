/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import java.awt.geom.Point2D;

public class PrimaryTrafficGeneratorNode extends Node{
    /**
     * Constructor of the PrimaryTrafficGeneratorNode.
     * @param pos Position of the node
     * @param vel Velocity of the node.
     */
    public PrimaryTrafficGeneratorNode(Point2D.Double pos, double vel,int id) {
       this.position = new Point2D.Double(pos.x, pos.y);
       this.velocity = vel;
       this.id = id;
    } 
    /**
     * Sets a new position for the primary traffic generator node.
     */
    public void setRandomPosition(){
        setPosition(Cell.deployNodeinCell());
    }
}

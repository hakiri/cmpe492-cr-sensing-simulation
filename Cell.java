/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import cern.jet.random.Uniform;


public class Cell {
    /**
     * Position of the baseStation
     */
    Point2D.Double position = new Point2D.Double(0, 0);
    /**
     * The CRBase at the center of the cell
     */
    private CRBase baseStation = new CRBase(position);
    /**
     * Radius of the of the network coverage
     */
    static double radius;
    /**
     * Number of sectors in the network coverage. "number_of_sectors" must divide 
     * 360 without remainder
     */
    int number_of_sectors;
    /**
     * This is the corresponding angle at the baseStation for a zone.
     * "alpha" must divide (360/number_of_sectors) without remainder
     */
    int alpha;
    /**
     * List of all distances between the baseStation and the zones which are in 
     * the same sector and have the same angle interval with the baseStation.
     * Distances must be in the ascending order.
     */
    ArrayList<Integer> set_of_d;
    
    /**
     * It assigns a random position for a node. Position should be in the coverage area. 
     * It uses the uniform distribution for choosing a random point.
     * @return Position of the node.
     */
    public static Point2D.Double deployNode(){
        Point2D.Double position_of_node = new Point2D.Double(0, 0); //initializes the position of the node
        Uniform uniform = new Uniform(SimulationRunner.randEngine); 
        
        if(uniform.nextBoolean()){      //if the boolean is true then we choose the x-axis first
            position_of_node.x = uniform.nextDoubleFromTo(-radius, radius);     //first we choose a random point from -radius to +radius for the x-axis of the position of the node
            double temp_double = Math.sqrt((radius*radius)-(position_of_node.x*position_of_node.x));
            position_of_node.y = uniform.nextDoubleFromTo(-temp_double, temp_double);   //then we choose the y-axis of the position so that the (x,y) point should be is in the cell
        }
        else{       //if the boolean is false then we choose the y-axis first.
            position_of_node.y = uniform.nextDoubleFromTo(-radius, radius);     //first we choose a random point from -radius to +radius for the y-axis of the position of the node
            double temp_double = Math.sqrt((radius*radius)-(position_of_node.y*position_of_node.y));
            position_of_node.x = uniform.nextDoubleFromTo(-temp_double, temp_double);   //then we choose the x-axis of the position so that the (x,y) point should be in the cell
        }
        return position_of_node;    //finally, it returns the position of the node.
    }
    /**
     * Sets a new position for the baseStation.
     * @param position 
     */
    public void setPosition(Double position) {
        baseStation.setPosition(position);
    }
    /**
     * Gets the current position of the baseStation.
     * @return Position of the baseStation
     */
    public Double getPosition() {
        return baseStation.getPosition();
    }
    
}

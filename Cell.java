/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import cern.jet.random.Uniform;


public class Cell {
    /**
     * The CRBase at the center of the cell
     */
    private CRBase baseStation = new CRBase(new Point2D.Double(0, 0));
    /**
     * Radius of the of the network coverage
     */
    static double radius;
    /**
     * Number of sectors in the network coverage. "number_of_sectors" must divide 
     * 360 without remainder
     */
    static int number_of_sectors;
    /**
     * This is the corresponding angle at the baseStation for a zone.
     * "alpha" must divide (360/number_of_sectors) without remainder
     */
    static int alpha;
    /**
     * Uniform distribuion to set random positions to nodes
     */
    private static Uniform uniform;
    /**
     * List of all distances between the baseStation and the zones which are in 
     * the same sector and have the same angle interval with the baseStation.
     * Distances must be in the ascending order.
     */
    static ArrayList<Double> set_of_d;
    
    /**
     * Constructor of the Cell
     * @param baseStation CRBase
     * @param radius Radius of the Cell
     * @param number_of_sectors Number of sectors in the cell.
     * @param alpha Corresponding angle for a zone at the baseStation
     * @param set_of_d List of distances
     */
    public Cell(CRBase baseStation,double radius,int number_of_sectors, int alpha, ArrayList<Double> set_of_d) {
        this.baseStation = baseStation;
        this.radius = radius;
        this.number_of_sectors = number_of_sectors;
        this.alpha = alpha;
        this.set_of_d = set_of_d;
        this.uniform = new Uniform(SimulationRunner.randEngine);
    }
    
    /**
     * Finds a random position in the cell with respect to given angles and distances.
     * This method can be used as creating random position in the cell or 
     * can be used as creating a random position in a specified zone.
     * @param angle_small   The minimum angle that random point can have.
     * @param angle_big     The maximum angle that random point can have.
     * @param distance_small    The minimum distance from center of the cell to that random point.
     * @param distance_big      The maximum distance from center of the cell to that random point.
     * @return Position of the node.
     */
    private static Point2D.Double deployNode(double angle_small,double angle_big,double distance_small,double distance_big){
        Point2D.Double position_of_node = new Point2D.Double(0, 0); //initializes the position of the node
        
        double random_distance = uniform.nextDoubleFromTo(distance_small,distance_big); 
        double random_angle = uniform.nextDoubleFromTo( angle_small, angle_big);
        position_of_node.x = random_distance * Math.cos(random_angle);  //finding x and y axis by using
        position_of_node.y = random_distance * Math.sin(random_angle);  //angle and distance to center.
        
        return position_of_node;
    }
    
    /**
     * Finds a random position for a node in the Cell.
     * @return Position of the node.
     */
    public static Point2D.Double deployNodeinCell(){
        return deployNode(0,360,0,radius);
    }
    /**
     * Finds a random position for a node in the specified zone.
     * Note: all the parameter values starts from zero.
     * @param sector_number Sector number of the node.
     * @param angle_number Angle number in the sector of the node.
     * @param distance_number Distance of the zone to the center.
     * @return Position of the node.
     */
    public static Point2D.Double deployNodeinZone(int sector_number,int angle_number,int distance_number){
        double min_angle = (360/number_of_sectors)*sector_number + alpha*angle_number ; 
        double max_angle = min_angle + alpha ;  //finds the angle values that random point
                                                //supposed to be in that angle intervals.
        double min_distance,max_distance;
        if(distance_number == 0){   //likewise the angles, finds the corresponding min and max distances.
            min_distance = 0;
            max_distance = set_of_d.get(distance_number);
        }
        else{
            min_distance = set_of_d.get(distance_number - 1);
            max_distance = set_of_d.get(distance_number);
        }
        return deployNode(min_angle,max_angle,min_distance,max_distance);
    }
    
    /**
     * Sets a new position for the baseStation.
     * @param position 
     */
    public void setPosition(Point2D.Double position) {
        baseStation.setPosition(position);
    }
    /**
     * Gets the current position of the baseStation.
     * @return Position of the baseStation
     */
    public Point2D.Double getPosition() {
        return baseStation.getPosition();
    }
    
}

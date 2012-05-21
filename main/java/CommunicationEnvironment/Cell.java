package CommunicationEnvironment;

import Nodes.CRBase;
import Nodes.Node;
import SimulationRunner.SimulationRunner;
import java.awt.geom.Point2D;
import cern.jet.random.Uniform;

/**
 * This class handles general cell structure with zone informations. It also handles
 * deployment of nodes in a specified zone or entire cell.
 */
public class Cell {
    /**
     * The CRBase at the center of the cell
     */
    private static CRBase baseStation = null;
    /**
     * Radius of the of the network coverage
     */
    static double radius;
	/**
     * Radius of the of the network coverage
     */
    static double primaryRadius;
    /**
     * Uniform distribution to set random positions to nodes
     */
    private static Uniform uniform;
    
    /**
     * Constructor of the Cell
     * @param baseStation CRBase
     * @param radius Radius of the Cell
     * @param number_of_sectors Number of sectors in the cell.
     * @param alpha Corresponding angle for a zone at the baseStation
     * @param set_of_d List of distances
     */
    public Cell(CRBase baseStation,double radius) {
        Cell.baseStation = baseStation;
        Cell.radius = radius;
		Cell.primaryRadius = SimulationRunner.args.getPrimaryRadius();
        
        Cell.uniform = new Uniform(SimulationRunner.randEngine);
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
        position_of_node.x = random_distance * Math.cos((random_angle/180)*Math.PI);  //finding x and y axis by using
        position_of_node.y = random_distance * Math.sin((random_angle/180)*Math.PI);  //angle and distance to center.
        
        return position_of_node;
    }
    
	/**
     * Finds a random position for a node in a circle with given radius.
	 * @param radius Radius of the circle in which the node will be deployed.
     * @return Position of the node.
     */
    private static Point2D.Double deployNode(double radius){
        Point2D.Double position_of_node = new Point2D.Double(0, 0); //initializes the position of the node
        while(true){
			position_of_node.x = uniform.nextDoubleFromTo(-radius, radius);
			position_of_node.y = uniform.nextDoubleFromTo(-radius, radius);
			if(position_of_node.distance(new Point2D.Double(0, 0))<radius)
				break;
		}
        
        return position_of_node;
    }
	
    /**
     * Finds a random position for a node in the Cell.
     * @return Position of the node.
     */
    public static Point2D.Double deployNodeinCell(){
        return deployNode(radius);
    }
	
	/**
     * Finds a random position for a node in the Cell.
     * @return Position of the node.
     */
    public static Point2D.Double deployNodeinPrimaryCell(){
        return deployNode(primaryRadius);
    }
    
	/**
	 * Deploys a node within a range it could have been relocated. This method is
	 * designed for <b>primary node</b> relocation only. It is assured that the node will
	 * never leave the cell.
	 * @param node			Node to relocate
	 * @param routeRadius	Range of node's relocation
	 * @return				New location of the node
	 */
	public static Point2D.Double deployNodeInRouteCircle(Node node, double routeRadius)
	{
		int i = 0;
		Point2D.Double n;
		do{
			n = deployNode(0, 360, 0, routeRadius);
			n.x += node.getPosition().x;
			n.y += node.getPosition().y;
			i++;
		}while(n.distance(baseStation.getPosition()) > primaryRadius && i<2);
		if(n.distance(baseStation.getPosition()) > primaryRadius){
			n = deployNodeinPrimaryCell();
		}
		return n;
	}
    
    /**
     * Sets a CRBase as a base station to the Cell.
     * @param baseStation Base station
     */
    public static void setBaseStation(CRBase baseStation) {
        Cell.baseStation = baseStation;
    }
    
    /**
     * Gets the current baseStation of the Cell.
     * @return Base Station
     */
    public static CRBase getBaseStation() {
        return baseStation;
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
    
    /**
     * Sets a new radius value for the Cell.
     * @param radius Radius of the Cell.
     */
    public static void setRadius(double radius) {
        Cell.radius = radius;
    }
    
    /**
     * Gets the current radius value of the Cell.
     * @return Radius of the Cell
     */
    public static double getRadius() {
        return radius;
    }
}

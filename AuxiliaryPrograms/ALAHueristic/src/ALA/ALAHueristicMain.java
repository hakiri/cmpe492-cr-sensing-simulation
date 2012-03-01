package ALA;

import Animation.DrawCell;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ALAHueristicMain {

	static RandomEngine randEngine = new MersenneTwister(new Date());
	static Uniform uniform = null;
	static double radius = 1500;
	static int numberOfNodes = 1500;
	static int numberOfClusters = 30;
	static boolean isSimulationOn = true;
	static ArrayList<Point2D.Double> nodes = new ArrayList<>();
	static ArrayList<Point2D.Double> clusterCenters = new ArrayList<>();
	static ArrayList<ArrayList<Integer>> yij = new ArrayList<>();
	
	public static void main(String[] args) {
		if(args.length > 0){
			numberOfNodes = Integer.parseInt(args[0]);
			numberOfClusters = Integer.parseInt(args[1]);
			isSimulationOn = Integer.parseInt(args[2]) != 0;
		}
		uniform = new Uniform(randEngine);
		long begin = System.currentTimeMillis();
		initializeNodePositions();
		double prevObjVal = 1000000000;
		double  newObjVal =  999999999;
		DrawCell cell = new DrawCell((int)radius, numberOfNodes, numberOfClusters, 40, yij);
		DrawCell.drawCell(true);
		int ite = 0;
		for(;newObjVal < prevObjVal;ite++){
			try {
				prevObjVal = newObjVal;
				clusterCenters.clear();
				for(int i = 0;i < numberOfClusters ; i++)
					clusterCenters.add(solveSingleClusterProblem(i));
				allocateNodes();
				newObjVal = objectiveValue();
				if(isSimulationOn){
					drawSolution();
					Thread.sleep(250);
				}
			} catch (Exception ex) {
				Logger.getLogger(ALAHueristicMain.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		begin = System.currentTimeMillis() - begin;
		System.out.println("Objective value of the problem: "+newObjVal);
		System.out.println("Number of iterations: "+ite);
		if(!isSimulationOn)
			System.out.println("Runtime of the algorithm: "+begin);
		
		System.out.println("\n");
		ArrayList<Integer> clusterSizes = new ArrayList<>();
		for(int i=0;i<numberOfClusters;i++){
			clusterSizes.add(yij.get(i).size());
		}
		
		Collections.sort(clusterSizes);
		System.out.println("Sizes of the clusters in ascending order:");
		System.out.println(clusterSizes);
		
		drawSolution();
	}
	
	/**
	 * Draws the current solution
	 */
	static void drawSolution(){
		for(int i=0;i<numberOfNodes;i++){
			DrawCell.paintNode(nodes.get(i), Color.BLUE, i);
		}
		for(int i=0;i<numberOfClusters;i++){
			DrawCell.paintClusterCenter(clusterCenters.get(i), Color.RED, i);
		}
	}
	
	/**
	 * Evaluates the objective value of the current solution.
	 * @return Objective Value of the problem
	 */
	static double objectiveValue()
	{
		double objVal = 0;
		for(int i=0;i<numberOfClusters;i++){
			for(int j=0;j<yij.get(i).size();j++)
				objVal += clusterCenters.get(i).distance(nodes.get(yij.get(i).get(j)));
		}
		return objVal;
	}
	
	/**
	 * Allocates nodes to its nearest cluster center.
	 */
	static void allocateNodes()
	{
		for(int i=0;i<numberOfClusters;i++)
			yij.get(i).clear();
		
		for(int i=0;i<numberOfNodes;i++){
			int nearestCluster = -1;
			double distance = Double.POSITIVE_INFINITY;
			for(int j=0;j<numberOfClusters;j++){
				double dist = nodes.get(i).distance(clusterCenters.get(j));
				if(dist < distance){
					distance = dist;
					nearestCluster = j;
				}
			}
			yij.get(nearestCluster).add(i);
		}
	}
	
	/**
	 * Solves a single facility location problem for a given cluster and returns its cluster center.
	 * @param cluster Cluster id for which the facility location problem will be solved.
	 * @return Cluster center of the given cluster.
	 */
	static Point2D.Double solveSingleClusterProblem(int cluster)
	{
		double x1Numerator=0, x2Numerator=0;
		int numberOfNodesInCluster = yij.get(cluster).size();
		for(int i=0;i<numberOfNodesInCluster;i++){
			x1Numerator += nodes.get(yij.get(cluster).get(i)).x;
			x2Numerator += nodes.get(yij.get(cluster).get(i)).y;
		}
		return new Point2D.Double(x1Numerator/numberOfNodesInCluster, x2Numerator/numberOfNodesInCluster);
	}
	
	/**
	 * Initializes the positions of the nodes and creates random clusters.
	 */
	static void initializeNodePositions()
	{
		for(int i=0;i<numberOfClusters;i++)
			yij.add(new ArrayList<Integer>());
		
		for(int i=0;i<numberOfNodes;i++){
			nodes.add(deployNode(0, 360, 0, radius));
			int cluster = uniform.nextIntFromTo(0, numberOfClusters - 1);
			yij.get(cluster).add(i);
		}
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
}

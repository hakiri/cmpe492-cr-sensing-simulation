package ATL;

import Animation.DrawCell;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ATLHueristicMain {

	static RandomEngine randEngine = new MersenneTwister(new Date());
	static Uniform uniform = null;
	static double radius = 1500;
	static int numberOfNodes = 1500;
	static int numberOfClusters = 30;
	static int clusterCapacity = 70;
	static boolean isSimulationOn = true;
	static ArrayList<Point2D.Double> nodes = new ArrayList<>();
	static ArrayList<Point2D.Double> clusterCenters = new ArrayList<>();
	static ArrayList<ArrayList<Integer>> yij = new ArrayList<>();
	
	public static void main(String[] args) {
		String fileName = "";
		boolean random = false;
		boolean guiOn = true;
		if(args.length > 0){
			random = Integer.parseInt(args[0]) == 0;
			if(random)
				numberOfNodes = Integer.parseInt(args[1]);
			else
				fileName = args[1];
			numberOfClusters = Integer.parseInt(args[2]);
			clusterCapacity = Integer.parseInt(args[3]);
			isSimulationOn = Integer.parseInt(args[4]) != 0;
			guiOn = Integer.parseInt(args[5]) != 0;

		}
		uniform = new Uniform(randEngine);
		
		if(random)
			initializeNodePositions();
		else
			parsePositions(fileName);
		double prevObjVal = 1000000000;
		double  newObjVal =  999999999;
		if(guiOn){
			DrawCell cell = new DrawCell((int)radius, numberOfNodes, numberOfClusters, yij,true);
			DrawCell.drawCell(true);
		}
		long begin = System.currentTimeMillis();
		int ite = 0;
		for(;newObjVal < prevObjVal;ite++){
			try {
				prevObjVal = newObjVal;
				clusterCenters.clear();
				for(int i = 0;i < numberOfClusters ; i++)
					clusterCenters.add(solveSingleClusterProblem(i));
				newObjVal = allocateNodes();
				//newObjVal = objectiveValue();
				if(guiOn && isSimulationOn){
					drawSolution();
					Thread.sleep(250);
				}
			} catch (Exception ex) {
				Logger.getLogger(ATLHueristicMain.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		clusterCenters.clear();
		for(int i = 0;i < numberOfClusters ; i++)
			clusterCenters.add(solveOneMedian(i));
		newObjVal = objectiveValue();
		begin = System.currentTimeMillis() - begin;
		System.out.println("Objective value of the problem: "+newObjVal);
		System.out.println("Number of iterations: "+ite);
		if(!guiOn || !isSimulationOn)
			System.out.println("Runtime of the algorithm: "+begin);
		
		System.out.println("\n");
		ArrayList<Integer> clusterSizes = new ArrayList<>();
		for(int i=0;i<numberOfClusters;i++){
			clusterSizes.add(yij.get(i).size());
		}
		
		Collections.sort(clusterSizes);
		System.out.println("Sizes of the clusters in ascending order:");
		System.out.println(clusterSizes);
		if(guiOn){
			drawSolution();
		}
	}
	
	/**
	 * Draws the current solution
	 */
	static void drawSolution(){
		for(int i=0;i<numberOfNodes;i++){
			DrawCell.paintNode(nodes.get(i), i);
		}
		for(int i=0;i<numberOfClusters;i++){
			DrawCell.paintClusterCenter(clusterCenters.get(i), i);
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
	 * Allocates nodes to its nearest cluster center regarding the capacity constraints.
	 */
	static double allocateNodes()
	{
		for(int i=0;i<numberOfClusters;i++)
			yij.get(i).clear();
		
		return Transportation.solve();
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
		if(numberOfNodesInCluster == 0)
			return new Point2D.Double(0, 0);
		for(int i=0;i<numberOfNodesInCluster;i++){
			x1Numerator += nodes.get(yij.get(cluster).get(i)).x;
			x2Numerator += nodes.get(yij.get(cluster).get(i)).y;
		}
		return new Point2D.Double(x1Numerator/numberOfNodesInCluster, x2Numerator/numberOfNodesInCluster);
	}
	
	static Point2D.Double solveOneMedian(int cluster)
	{
		ArrayList<Double> distances = new ArrayList<>();
		int numberOfNodesInCluster = yij.get(cluster).size();
		for(int i=0;i<numberOfNodesInCluster;i++){
			double dist = 0;
			Point2D.Double n = nodes.get(yij.get(cluster).get(i));
			for(int j=0;j<numberOfNodesInCluster;j++){
				dist += n.distance(nodes.get(yij.get(cluster).get(j)));
			}
			distances.add(dist);
		}
		double min = Collections.min(distances);
		int node = distances.indexOf(min);
		return nodes.get(yij.get(cluster).get(node));
	}
	
	/**
	 * Initializes the positions of the nodes and creates random clusters.
	 */
	static void initializeNodePositions()
	{
		for(int i=0;i<numberOfClusters;i++)
			yij.add(new ArrayList<Integer>());
		
		for(int i=0;i<numberOfNodes;i++){
			nodes.add(deployNode(radius));
			if(i < numberOfClusters)
				yij.get(i).add(i);
			else if(nodes.get(i).x >= 0 && nodes.get(i).y >= 0){
				int cluster = uniform.nextIntFromTo(0, numberOfClusters/4);
				yij.get(cluster).add(i);
			}
			else if(nodes.get(i).x <= 0 && nodes.get(i).y >= 0){
				int cluster = uniform.nextIntFromTo(numberOfClusters/4 + 1, numberOfClusters/2);
				yij.get(cluster).add(i);
			}
			else if(nodes.get(i).x <= 0 && nodes.get(i).y <= 0){
				int cluster = uniform.nextIntFromTo(numberOfClusters/2, (3*numberOfClusters)/4);
				yij.get(cluster).add(i);
			}
			else if(nodes.get(i).x >= 0 && nodes.get(i).y <= 0){
				int cluster = uniform.nextIntFromTo((3*numberOfClusters)/4, numberOfClusters - 1);
				yij.get(cluster).add(i);
			}
		}
	}
	
	static void parsePositions(String inFile)
	{
		for(int i=0;i<numberOfClusters;i++)
			yij.add(new ArrayList<Integer>());
		
		Scanner input = null;
		if(!nodes.isEmpty())
			nodes.clear();
		try {
			input = new Scanner(new FileInputStream(inFile));
		} catch (FileNotFoundException ex) {
			
		}
		
		numberOfNodes = input.nextInt();
		numberOfClusters = input.nextInt();
		Double.parseDouble(input.next());
		for(int i=0;i<numberOfNodes;i++){
			nodes.add(new Point2D.Double(Double.parseDouble(input.next()), Double.parseDouble(input.next())));
			if(i < numberOfClusters)
				yij.get(i).add(i);
			else if(nodes.get(i).x >= 0 && nodes.get(i).y >= 0){
				int cluster = uniform.nextIntFromTo(0, numberOfClusters/4);
				yij.get(cluster).add(i);
			}
			else if(nodes.get(i).x <= 0 && nodes.get(i).y >= 0){
				int cluster = uniform.nextIntFromTo(numberOfClusters/4 + 1, numberOfClusters/2);
				yij.get(cluster).add(i);
			}
			else if(nodes.get(i).x <= 0 && nodes.get(i).y <= 0){
				int cluster = uniform.nextIntFromTo(numberOfClusters/2, (3*numberOfClusters)/4);
				yij.get(cluster).add(i);
			}
			else if(nodes.get(i).x >= 0 && nodes.get(i).y <= 0){
				int cluster = uniform.nextIntFromTo((3*numberOfClusters)/4, numberOfClusters - 1);
				yij.get(cluster).add(i);
			}
		}
		input.close();
	}
	
	/**
     * Finds a random position in the cell with respect to given angles and distances.
     * This method can be used as creating random position in the cell or 
     * can be used as creating a random position in a specified zone.
     * @param radius      The maximum distance from center of the cell to that random point.
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
}
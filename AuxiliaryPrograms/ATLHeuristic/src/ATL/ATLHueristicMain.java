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

/**
 * Solves capacitated clustering problem with Tranpostation-Location Heuristic.
 */
public class ATLHueristicMain {

	static RandomEngine randEngine = new MersenneTwister(new Date());
	static Uniform uniform = null;
	static double radius = 1500;
	static int numberOfNodes = 1500;
	static int numberOfClusters = 30;
	static int clusterCapacity = 70;
	static boolean isSimulationOn = true;
	static boolean guiOn = true;
	static ArrayList<Point2D.Double> nodes = new ArrayList<>();
	static ArrayList<Point2D.Double> clusterCenters = new ArrayList<>();
	static ArrayList<ArrayList<Integer>> yij = new ArrayList<>();
	
	/**
	 * Main method of program which parses command line arguments and run the algorithm.
	 * @param args
	 */
	public static void main(String[] args) {
		DrawCell cell = parseArguments(args);
		
		double prevObjVal = 1000000000;
		double  newObjVal =  999999999;
		long begin = System.currentTimeMillis();
		int ite = 0;
		for(;newObjVal < prevObjVal;ite++){
			try {
				prevObjVal = newObjVal;
				clusterCenters.clear();
				for(int i = 0;i < numberOfClusters ; i++)
					clusterCenters.add(solveEuclideanLocationProblem(i));
				newObjVal = allocateNodes();
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
		outputReport(begin, ite, newObjVal);
	}
	
	static DrawCell parseArguments(String []args){
		String fileName = "";
		boolean random = false;
		guiOn = true;
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
		
		if(guiOn){
			DrawCell cell = new DrawCell((int)radius, numberOfNodes, numberOfClusters, yij,true);
			DrawCell.drawCell(true);
			return cell;
		}
		return null;
	}
	
	static void outputReport(long runTime, int numberOfIterations, double objVal){
		
		System.out.println("Objective value of the problem: "+objVal);
		System.out.println("Number of iterations: "+numberOfIterations);
		if(!guiOn || !isSimulationOn)
			System.out.println("Runtime of the algorithm: "+runTime);
		
		System.out.println("\n");
		ArrayList<Integer> clusterSizes = new ArrayList<>();
		for(int i=0;i<numberOfClusters;i++){
			clusterSizes.add(yij.get(i).size());
		}
		
		Collections.sort(clusterSizes);
		System.out.println("Sizes of the clusters in ascending order:");
		System.out.println(clusterSizes);
		ArrayList<Integer> groupSizeNumber = new ArrayList<>(Collections.nCopies(7, 0));
		for (Integer cluster : clusterSizes) {
			int groupSize, index;
			if(cluster<=25){
				groupSize = cluster - 1 + (cluster % 2);
				index = (groupSize - 1)/2 - 6;
				groupSizeNumber.set(index, groupSizeNumber.get(index)+1);
			}
			else if(cluster<=50){
				int newCluster = cluster/2;
				groupSize = newCluster - 1 + (newCluster % 2);
				index = (groupSize - 1)/2 - 6;
				int disposed = cluster - groupSize*2;
				groupSizeNumber.set(index, groupSizeNumber.get(index)+1);
				groupSizeNumber.set(index+(disposed/2), groupSizeNumber.get(index+(disposed/2))+1);
			}
			else{
				int newCluster = cluster/3;
				groupSize = newCluster - 1 + (newCluster % 2);
				index = (groupSize - 1)/2 - 6;
				int disposed = cluster - groupSize*3;
				int inc = disposed/2;
				groupSizeNumber.set(index, groupSizeNumber.get(index)+1+(2-inc));
				groupSizeNumber.set(index+1, groupSizeNumber.get(index+1)+inc);
			}
			
		}
		System.out.println(groupSizeNumber);
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
	 * Solves a single facility location problem with squared Euclidean distances
	 * for a given cluster and returns its cluster center.
	 * @param cluster Cluster id for which the facility location problem will be solved.
	 * @return Cluster center of the given cluster.
	 */
	static Point2D.Double solveSquaredEuclideanLocationProblem(int cluster)
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
	
	/**
	 * Solves a single facility location problem with Euclidean distances
	 * for a given cluster and returns its cluster center.
	 * @param cluster Cluster id for which the facility location problem will be solved.
	 * @return Cluster center of the given cluster.
	 */
	static Point2D.Double solveEuclideanLocationProblem(int cluster)
	{
		int numberOfNodesInCluster = yij.get(cluster).size();
		if(numberOfNodesInCluster == 0)
			return new Point2D.Double(0, 0);
		Point2D.Double xt = solveSquaredEuclideanLocationProblem(cluster);
		Point2D.Double xt1 = new Point2D.Double();
		double denumerator = 0;
		int j=0;
		double eps = 0.0001;
		for(;true;j++){
			
			for(int i=0;i<numberOfNodesInCluster;i++){
				xt1.x += (nodes.get(yij.get(cluster).get(i)).x)/(nodes.get(yij.get(cluster).get(i)).distance(xt)+eps);
				xt1.y += (nodes.get(yij.get(cluster).get(i)).y)/(nodes.get(yij.get(cluster).get(i)).distance(xt)+eps);
				denumerator += 1/(nodes.get(yij.get(cluster).get(i)).distance(xt)+eps);
			}
			xt1.x /= denumerator;
			xt1.y /= denumerator;
			
			if(xt.distance(xt1) < 0.01)
				break;
			xt = xt1;
			xt1 = new Point2D.Double();
			denumerator = 0;
		}
		return xt1;
	}
	
	/**
	 * Solves one median problem for a given cluster
	 * @param cluster	Id of the cluster
	 * @return	New cluster center according to solution of one median which is
	 *			typically on a node in the cluster
	 */
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
		ArrayList<ArrayList<Integer>> indexes = new ArrayList<>();
		for(int i=0;i<4;i++){
			indexes.add(new ArrayList<Integer>());
			for(int j=(i*numberOfClusters)/4;j<((i+1)*numberOfClusters)/4;j++){
				indexes.get(i).add(j);
			}
		}
		
		for(int i=0;i<numberOfClusters;i++)
			yij.add(new ArrayList<Integer>());
		
		for(int i=0;i<numberOfNodes;i++){
			nodes.add(deployNode(radius));
			assignRandomCluster(indexes, i);
		}
	}
	
	/**
	 * Parse positions of the nodes from a given file.
	 * @param inFile Name of the file to be parsed
	 */
	static void parsePositions(String inFile)
	{
		ArrayList<ArrayList<Integer>> indexes = new ArrayList<>();
		for(int i=0;i<4;i++){
			indexes.add(new ArrayList<Integer>());
			for(int j=(i*numberOfClusters)/4;j<((i+1)*numberOfClusters)/4;j++){
				indexes.get(i).add(j);
			}
		}
		
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
			assignRandomCluster(indexes, i);
		}
		input.close();
	}
	
	/**
	 * Assignes the given node to a random cluster.
	 * @param indexes	Indexes of available cluster centers
	 * @param nodeId	Id of the node to be assigned
	 */
	private static void assignRandomCluster(ArrayList<ArrayList<Integer>> indexes, int nodeId){
		if(nodeId < numberOfClusters)
			yij.get(nodeId).add(nodeId);
		else if(nodes.get(nodeId).x >= 0 && nodes.get(nodeId).y >= 0){
			if(indexes.get(0).isEmpty()){
				for(int j=0;j<4;j++){
					if(!indexes.get(j).isEmpty()){
						yij.get(indexes.get(j).get(0)).add(nodeId);
						break;
					}
				}
				return;
			}
			int ind = uniform.nextIntFromTo(0, indexes.get(0).size() - 1);
			int cluster = indexes.get(0).get(ind);
			yij.get(cluster).add(nodeId);
			if(yij.get(cluster).size() >= clusterCapacity)
				indexes.get(0).remove(ind);
		}
		else if(nodes.get(nodeId).x <= 0 && nodes.get(nodeId).y >= 0){
			if(indexes.get(1).isEmpty()){
				for(int j=0;j<4;j++){
					if(!indexes.get(j).isEmpty()){
						yij.get(indexes.get(j).get(0)).add(nodeId);
						break;
					}
				}
				return;
			}
			int ind = uniform.nextIntFromTo(0, indexes.get(1).size() - 1);
			int cluster = indexes.get(1).get(ind);
			yij.get(cluster).add(nodeId);
			if(yij.get(cluster).size() >= clusterCapacity)
				indexes.get(1).remove(ind);
		}
		else if(nodes.get(nodeId).x <= 0 && nodes.get(nodeId).y <= 0){
			if(indexes.get(2).isEmpty()){
				for(int j=0;j<4;j++){
					if(!indexes.get(j).isEmpty()){
						yij.get(indexes.get(j).get(0)).add(nodeId);
						break;
					}
				}
				return;
			}
			int ind = uniform.nextIntFromTo(0, indexes.get(2).size() - 1);
			int cluster = indexes.get(2).get(ind);
			yij.get(cluster).add(nodeId);
			if(yij.get(cluster).size() >= clusterCapacity)
				indexes.get(2).remove(ind);
		}
		else if(nodes.get(nodeId).x >= 0 && nodes.get(nodeId).y <= 0){
			if(indexes.get(3).isEmpty()){
				for(int j=0;j<4;j++){
					if(!indexes.get(j).isEmpty()){
						yij.get(indexes.get(j).get(0)).add(nodeId);
						break;
					}
				}
				return;
			}
			int ind = uniform.nextIntFromTo(0, indexes.get(3).size() - 1);
			int cluster = indexes.get(3).get(ind);
			yij.get(cluster).add(nodeId);
			if(yij.get(cluster).size() >= clusterCapacity)
				indexes.get(3).remove(ind);
		}
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

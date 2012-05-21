package Heuristic;

import CommunicationEnvironment.WirelessChannel;
import Nodes.CRNode;
import SimulationRunner.SimulationRunner;
import cern.jet.random.Uniform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Solves capacitated clustering problem with Tranpostation-Location Heuristic.
 */
public class ATLHueristic {

	static Uniform uniform = null;
	public static int numberOfClusters;
	public static int clusterCapacity;
	public static ArrayList<ArrayList<Integer>> groupCapacities;
	static ArrayList<Point2D.Double> clusterCenters = new ArrayList<>();
	static ArrayList<Integer> clusterCenterIds = new ArrayList<>();
	public static ArrayList<ArrayList<Integer>> yij = new ArrayList<>();
	
	/**
	 * Main method of program which parses command line arguments and run the algorithm.
	 */
	public static void solve() {
		uniform = new Uniform(SimulationRunner.randEngine);
		numberOfClusters = SimulationRunner.args.getNumberOfZones();
		clusterCapacity = (int)Math.ceil((SimulationRunner.args.getNumberOfCrNodes()*(4.0/3.0))/numberOfClusters);
		clusterCenters = new ArrayList<>();
		yij = new ArrayList<>();
		initializeClusters();
		
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
			} catch (Exception ex) {
				Logger.getLogger(ATLHueristic.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		clusterCenters.clear();
		for(int i = 0;i < numberOfClusters ; i++){
			int id;
			clusterCenters.add(SimulationRunner.crBase.getCRNode(id=solveOneMedian(i)).getPosition());
			clusterCenterIds.add(id);
		}
		newObjVal = objectiveValue();
		begin = System.currentTimeMillis() - begin;
		outputReport(begin, ite, newObjVal);
	}
	
	/**
	 * Outputs the final report of the algorithm.
	 * @param runTime				Run time of the algorithm in terms of msec.
	 * @param numberOfIterations	Number of iteration iterated by the algorithm.
	 * @param objVal				Objective value of the final solution.
	 */
	static void outputReport(long runTime, int numberOfIterations, double objVal){
		groupCapacities = new ArrayList<>();
		for(int i=0;i<numberOfClusters;i++){
			groupCapacityFinder(i, yij.get(i).size());
		}
	}
	
	private static void groupCapacityFinder(int clusterId, int numberOfNodesInCluster){
		int numberOfGroups = (numberOfNodesInCluster-1)/25;
		numberOfGroups++;
		int numberOfNodesInGroups = numberOfNodesInCluster / numberOfGroups;
		numberOfNodesInGroups += (numberOfNodesInGroups%2-1);
		int dispose = numberOfNodesInCluster - numberOfNodesInGroups*numberOfGroups;
		
		groupCapacities.add(new ArrayList<Integer>());
		for(int i = 0;i<numberOfGroups;i++){
			if(dispose < 2)
				groupCapacities.get(clusterId).add(numberOfNodesInGroups);
			else{
				groupCapacities.get(clusterId).add(numberOfNodesInGroups+2);
				dispose -= 2;
			}
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
				objVal += clusterCenters.get(i).distance(SimulationRunner.crBase.getCRNode(yij.get(i).get(j)).getPosition());
		}
		return objVal;
	}
	
	static double prx = -60;
	static double l0 = 38.4;
	static double alpha = 35;
	/**
	 * Evaluates the objective value of the current solution.
	 * @return Objective Value of the problem
	 */
	static double powerConsumption()
	{
		double power = 0;
		double dist;
		double dbPower;
		for(int i=0;i<numberOfClusters;i++){
			for(int j=0;j<yij.get(i).size();j++){
				dist = clusterCenters.get(i).distance(SimulationRunner.crBase.getCRNode(yij.get(i).get(j)).getPosition()) / 1000.0;
				if(dist != 0.0){
					dbPower = prx + l0 + alpha*Math.log10(dist);
					power += WirelessChannel.dbmToMag(dbPower);
				}
			}
			dist = clusterCenters.get(i).distance(new Point2D.Double(0, 0)) / 1000.0;
			dbPower = prx + l0 + alpha*Math.log10(dist);
			power += WirelessChannel.dbmToMag(dbPower);
		}
		return WirelessChannel.magTodbm(power);
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
			x1Numerator += SimulationRunner.crBase.getCRNode(yij.get(cluster).get(i)).getPosition().x;
			x2Numerator += SimulationRunner.crBase.getCRNode(yij.get(cluster).get(i)).getPosition().y;
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
				xt1.x += (SimulationRunner.crBase.getCRNode(yij.get(cluster).get(i)).getPosition().x)/(SimulationRunner.crBase.getCRNode(yij.get(cluster).get(i)).getPosition().distance(xt)+eps);
				xt1.y += (SimulationRunner.crBase.getCRNode(yij.get(cluster).get(i)).getPosition().y)/(SimulationRunner.crBase.getCRNode(yij.get(cluster).get(i)).getPosition().distance(xt)+eps);
				denumerator += 1/(SimulationRunner.crBase.getCRNode(yij.get(cluster).get(i)).getPosition().distance(xt)+eps);
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
	static int solveOneMedian(int cluster)
	{
		ArrayList<Double> distances = new ArrayList<>();
		int numberOfNodesInCluster = yij.get(cluster).size();
		for(int i=0;i<numberOfNodesInCluster;i++){
			double dist = 0;
			Point2D.Double n = SimulationRunner.crBase.getCRNode(yij.get(cluster).get(i)).getPosition();
			for(int j=0;j<numberOfNodesInCluster;j++){
				dist += n.distance(SimulationRunner.crBase.getCRNode(yij.get(cluster).get(j)).getPosition());
			}
			distances.add(dist);
			SimulationRunner.crBase.getCRNode(yij.get(cluster).get(i)).setZoneId(cluster);
		}
		double min = Collections.min(distances);
		int node = distances.indexOf(min);
		return yij.get(cluster).get(node);
	}
	
	/**
	 * Initializes the positions of the nodes and creates random clusters.
	 */
	static void initializeClusters()
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
		
		for(int i=0;i<SimulationRunner.args.getNumberOfCrNodes();i++){
			assignRandomCluster(indexes, i);
		}
	}
	
	/**
	 * Assignes the given node to a random cluster.
	 * @param indexes	Indexes of available cluster centers
	 * @param nodeId	Id of the node to be assigned
	 */
	private static void assignRandomCluster(ArrayList<ArrayList<Integer>> indexes, int nodeId){
		if(nodeId < numberOfClusters)
			yij.get(nodeId).add(nodeId);
		else if(SimulationRunner.crBase.getCRNode(nodeId).getPosition().x >= 0 && SimulationRunner.crBase.getCRNode(nodeId).getPosition().y >= 0){
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
		else if(SimulationRunner.crBase.getCRNode(nodeId).getPosition().x <= 0 && SimulationRunner.crBase.getCRNode(nodeId).getPosition().y >= 0){
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
		else if(SimulationRunner.crBase.getCRNode(nodeId).getPosition().x <= 0 && SimulationRunner.crBase.getCRNode(nodeId).getPosition().y <= 0){
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
		else if(SimulationRunner.crBase.getCRNode(nodeId).getPosition().x >= 0 && SimulationRunner.crBase.getCRNode(nodeId).getPosition().y <= 0){
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
}

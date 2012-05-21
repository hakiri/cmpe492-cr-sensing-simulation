package Heuristic;

import SimulationRunner.SimulationRunner;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class FAHMain {

	public static ArrayList<ArrayList<ArrayList<Integer>>> groups;
	public static int maxSlots;
	public static void solve(){
		int numberOfClusters;
		ArrayList<ArrayList<Integer>> xij = ATLHueristic.yij;
		ArrayList<Point2D.Double> nodes;
		
		
		int numberOfNodes = SimulationRunner.args.getNumberOfCrNodes();
		numberOfClusters = ATLHueristic.numberOfClusters;
		nodes = new ArrayList<>(numberOfNodes);
		groups = new ArrayList<>();
		
		for(int i=0;i<numberOfNodes;i++){
			nodes.add(SimulationRunner.crBase.getCRNode(i).getPosition());
		}
				
		findGroupsInClusters(numberOfClusters, xij, nodes, groups, ATLHueristic.groupCapacities);
		maxSlots = 0;
		int numberOfSlots;
		for(int i=0;i<SimulationRunner.args.getNumberOfZones();i++){
			numberOfSlots = (int)Math.ceil((double)SimulationRunner.args.getNumberOfFreq()/groups.get(i).size());
			if(numberOfSlots > maxSlots){
				maxSlots = numberOfSlots;
			}
		}
		//SimulationRunner.crDesScheduler.setNumberOfSlots(maxSlots);
		//System.out.println(groups);
	}
	
	/**
	 * Find frequency assignments (groups) in each cluster.
	 * @param numberOfClusters	Number of clusters in the cell
	 * @param xij				Cluster assignments of the nodes
	 * @param nodes				Positions of the nodes
	 * @param groups			Groups in the cluster
	 * @param capacities		Capacities of the groups
	 * @return	Objective values of the group calculations for each cluster.
	 */
	public static ArrayList<Double> findGroupsInClusters(int numberOfClusters, ArrayList<ArrayList<Integer>> xij, ArrayList<Point2D.Double> nodes,
									 ArrayList<ArrayList<ArrayList<Integer>>> groups, ArrayList<ArrayList<Integer>> capacities){
		
		ArrayList<Double> objectiveValues = new ArrayList<>();
		for(int i=0;i<numberOfClusters;i++){
			groups.add(new ArrayList<ArrayList<Integer>>());
		}
		
		for(int i=0;i<numberOfClusters;i++){
			//System.out.println("Cluster "+i+" FAH started");
			
			double obj = FAHSimulatedAnnealing.solve(createNodesArrayForCluster(i, xij, nodes), capacities.get(i),groups.get(i));
			
			objectiveValues.add(obj);
		}
		for(int i=0;i<numberOfClusters;i++){
			backTraceNodesInGroups(i, xij, groups);
		}
		return objectiveValues;
	}
	
	/**
	 * Creates an array in which the nodes of a given cluster exists.
	 * @param clusterIndex	Index of the cluster
	 * @param xij			Cluster assignments of all nodes
	 * @param nodes			Positions of all nodes
	 * @return	array in which the positions of the nodes of a given cluster exists.
	 */
	private static ArrayList<Point2D.Double> createNodesArrayForCluster(int clusterIndex, ArrayList<ArrayList<Integer>> xij, 
																ArrayList<Point2D.Double> nodes)
	{
		int clusterSize = xij.get(clusterIndex).size();
		ArrayList<Point2D.Double> clusterNodes = new ArrayList<>(clusterSize);
		for(int i=0;i<clusterSize/*-dispose*/;i++){
			Point2D.Double p = nodes.get(xij.get(clusterIndex).get(i));
			clusterNodes.add(p);
		}
		return clusterNodes;
	}
	
	/**
	 * Finds id of a node from its cluster and its position in its cluster.
	 * @param clusterIndex	Index of the cluster
	 * @param xij			Cluster assignments of all nodes
	 * @param groups		Groups in the cluster
	 */
	private static void backTraceNodesInGroups(int clusterIndex, ArrayList<ArrayList<Integer>> xij, ArrayList<ArrayList<ArrayList<Integer>>> groups)
	{
		int numberOfGroups = groups.get(clusterIndex).size();
		for(int k=0;k<numberOfGroups;k++){
			int groupSize = groups.get(clusterIndex).get(k).size();
			for(int i = 0; i< groupSize;i++){
				int actualIndex = xij.get(clusterIndex).get(groups.get(clusterIndex).get(k).get(i));
				groups.get(clusterIndex).get(k).set(i, actualIndex);
				SimulationRunner.crBase.getCRNode(actualIndex).setGroupId(k);
			}
		}
	}
}

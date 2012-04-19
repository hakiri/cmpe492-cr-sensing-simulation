package FrequencyAssignment;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssignFrequencies {
	
	public static ArrayList<Double> findGroupsInClusters(int numberOfClusters, double radius, ArrayList<ArrayList<Integer>> xij, ArrayList<Point2D.Double> nodes,
									 ArrayList<ArrayList<ArrayList<Integer>>> groups){
		
		ArrayList<ArrayList<Integer>> capacities = new ArrayList<>();
		ArrayList<Double> objectiveValues = new ArrayList<>();
		for(int i=0;i<numberOfClusters;i++){
			groups.add(new ArrayList<ArrayList<Integer>>());
			capacities.add(new ArrayList<Integer>());
		}
		
		for (int i=0;i<numberOfClusters;i++) {
			int cluster = xij.get(i).size();
			int groupSize;
			if(cluster<=25){
				groupSize = cluster - 1 + (cluster % 2);
				capacities.get(i).add(groupSize);
			}
			else if(cluster<=50){
				int newCluster = cluster/2;
				groupSize = newCluster - 1 + (newCluster % 2);
				capacities.get(i).add(groupSize);
				int disposed = cluster - groupSize*2;
				capacities.get(i).add(groupSize+disposed-(disposed%2));
			}
			else{
				int newCluster = cluster/3;
				groupSize = newCluster - 1 + (newCluster % 2);
				capacities.get(i).add(groupSize);
				int disposed = cluster - groupSize*3;
				int inc = disposed/2;
				capacities.get(i).add(groupSize+inc*2-(inc/2)*2);
				newCluster = cluster - capacities.get(i).get(0) - capacities.get(i).get(1);
				groupSize = newCluster - 1 + (newCluster % 2);
				capacities.get(i).add(groupSize);
			}
		}
		
		ArrayList<Integer> clusterSizes = new ArrayList<>();
		for(int i=0;i<numberOfClusters;i++){
			clusterSizes.add(xij.get(i).size());
		}
		
		for(int i=0;i<numberOfClusters;i++){
			int dispose = clusterSizes.get(i);
			for(int j=0;j<capacities.get(i).size();j++){
				dispose -= capacities.get(i).get(j);
			}
			System.out.print(clusterSizes.get(i));
			long time = System.currentTimeMillis();
			//double obj = FrequencyAssignDistSum.solve2(capacities.get(i), createNodesArrayForCluster(i, dispose, xij, nodes), radius, groups.get(i));
			double obj = FrequencyAssignDistSum.solve2(capacities.get(i), createNodesArrayForCluster(i, dispose, xij, nodes), radius, groups.get(i));
			time = System.currentTimeMillis() - time;
			System.out.println("\t"+time+"\t"+obj);
			objectiveValues.add(obj);
		}
		for(int i=0;i<numberOfClusters;i++){
			backTraceNodesInGroups(i, xij, groups);
		}
		return objectiveValues;
	}
	
	public static ArrayList<Double> findGroupsInClusters(int numberOfClusters, double radius, ArrayList<ArrayList<Integer>> xij, ArrayList<Point2D.Double> nodes,
									 ArrayList<ArrayList<ArrayList<Integer>>> groups, ArrayList<ArrayList<Integer>> capacities){
		
		ArrayList<Double> objectiveValues = new ArrayList<>();
		for(int i=0;i<numberOfClusters;i++){
			groups.add(new ArrayList<ArrayList<Integer>>());
		}
		
		ArrayList<Integer> clusterSizes = new ArrayList<>();
		for(int i=0;i<numberOfClusters;i++){
			clusterSizes.add(xij.get(i).size());
		}
		
		for(int i=0;i<numberOfClusters;i++){
			int dispose = clusterSizes.get(i);
			for(int j=0;j<capacities.get(i).size();j++){
				dispose -= capacities.get(i).get(j);
			}
			System.out.print(clusterSizes.get(i));
			long time = System.currentTimeMillis();
			//double obj = FrequencyAssignDistSum.solve2(capacities.get(i), createNodesArrayForCluster(i, dispose, xij, nodes), radius, groups.get(i));
			double obj = FrequencyAssignDistSum.solveWithWkijSummation(capacities.get(i), createNodesArrayForCluster(i, dispose, xij, nodes), radius, groups.get(i));
			time = System.currentTimeMillis() - time;
			System.out.println("\t"+time+"\t"+obj);
			objectiveValues.add(obj);
		}
		for(int i=0;i<numberOfClusters;i++){
			backTraceNodesInGroups(i, xij, groups);
		}
		return objectiveValues;
	}
	
	/**
	 * Creates an output file and writes number of nodes, number of clusters, radius of the circle, and the positions of the nodes in that file.
	 * @param outFile Name of the output file
	 */
	private static ArrayList<Point2D.Double> createNodesArrayForCluster(int clusterIndex, int dispose, ArrayList<ArrayList<Integer>> xij, 
																ArrayList<Point2D.Double> nodes)
	{
		int clusterSize = xij.get(clusterIndex).size();
		ArrayList<Point2D.Double> clusterNodes = new ArrayList<>(clusterSize);
		for(int i=0;i<clusterSize-dispose;i++){
			Point2D.Double p = nodes.get(xij.get(clusterIndex).get(i));
			clusterNodes.add(p);
		}
		return clusterNodes;
	}
	
	/**
	 * Creates an output file and writes number of nodes, number of clusters, radius of the circle, and the positions of the nodes in that file.
	 * @param outFile Name of the output file
	 */
	private static void backTraceNodesInGroups(int clusterIndex, ArrayList<ArrayList<Integer>> xij, ArrayList<ArrayList<ArrayList<Integer>>> groups)
	{
		int numberOfGroups = groups.get(clusterIndex).size();
		for(int k=0;k<numberOfGroups;k++){
			int groupSize = groups.get(clusterIndex).get(k).size();
			for(int i = 0; i< groupSize;i++){
				int actualIndex = xij.get(clusterIndex).get(groups.get(clusterIndex).get(k).get(i));
				groups.get(clusterIndex).get(k).set(i, actualIndex);
			}
		}
	}
	
	public static void main(String []args){
		int numberOfClusters;
		double radius;
		ArrayList<ArrayList<Integer>> xij;
		ArrayList<Point2D.Double> nodes;
		ArrayList<ArrayList<ArrayList<Integer>>> groups;
		ArrayList<ArrayList<Integer>> capacities;
		Scanner sc = null;
		try {
			sc = new Scanner(new File(args[0]));
		} catch (FileNotFoundException ex) {
			Logger.getLogger(AssignFrequencies.class.getName()).log(Level.SEVERE, null, ex);
		}
		int numberOfNodes = sc.nextInt();
		numberOfClusters = sc.nextInt();
		radius = 250;
		nodes = new ArrayList<>(numberOfNodes);
		xij = new ArrayList<>(numberOfClusters);
		capacities = new ArrayList<>();
		for(int i=0;i<numberOfClusters;i++){
			xij.add(new ArrayList<Integer>());
			capacities.add(new ArrayList<Integer>());
		}
		groups = new ArrayList<>();
		
		for(int i=0;i<numberOfNodes;i++){
			double x = Double.parseDouble(sc.next());
			double y = Double.parseDouble(sc.next());
			int cluster = sc.nextInt() - 1;
			nodes.add(new Point2D.Double(x, y));
			xij.get(cluster).add(i);
		}
		int numberOfGroups = sc.nextInt();
		for(int i = 0;i<numberOfClusters;i++){
			sc.nextInt();
			for(int j=0;j<numberOfGroups;j++){
				capacities.get(i).add(sc.nextInt());
			}
		}
		
		findGroupsInClusters(numberOfClusters, radius, xij, nodes, groups, capacities);
		System.out.println(groups);
	}
	
	private static void parse(){
		
	}
}

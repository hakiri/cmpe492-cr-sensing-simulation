package PositonDraw;

import Animation.DrawCell;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class to generate a random GAMS model for a clustering problem with the given parameters.
 */
public class RandomPositionDrawMain {
	
	int numberOfNodes = 50;
	int numberOfClusters = 5;
	int customerLimit = 15;
	double radius = 150;
	int maxDigit;
	String []args = null;
	ArrayList<Point2D.Double> nodes;
	ArrayList<Point2D.Double> clusters;
	RandomEngine randEngine;
	Uniform uniform;
	PrintWriter positionFile;
	Scanner input;
	static RandomPositionDrawMain rpdm;
	ArrayList<ArrayList<Integer>> xij = new ArrayList<>();
	
	/**
	 * The main method of the program. It generates a GAMS model or draws a given solution
	 * @param args The first command line argument indicates whether it should create a GAMS model file or draw a given solution.
	 *             <br></br>If the first argument is "0" it commands the program to draw a given solution with the following parameters:
	 *             <ul>
	 *                  <li>Second parameter is the name of the file that contains position of the nodes for the solution</li>
	 *                  <li>Third parameter is the name of the GAMS solution file</li>
	 *             </ul>
	 *             <br></br>If the first argument is "1" it commands the program to generate a GAMS model with the given parameters:
	 *             <ul>
	 *                  <li>Second parameter is the name of the file to write the positions of the nodes in the model</li>
	 *                  <li>Third parameter is number of nodes</li>
	 *                  <li>Forth parameter is number of clusters</li>
	 *                  <li>Fifth parameter is the maximum number of customers per cluster</li>
	 *                  <li>Sixth parameter is the radius of the circle in which the nodes will be deployed uniformly</li>
	 *             </ul>
	 */
	public static void main(String []args)
	{
		rpdm = new RandomPositionDrawMain();
		
		if(args[0].contains("0")){
			rpdm.parsePositions(args[1]);
			Parser parser = new Parser(args[2]);
			String objVal;
			ArrayList<Integer> yj = new ArrayList<>();
			objVal = parser.parse(rpdm.nodes, rpdm.clusters, rpdm.xij, rpdm.numberOfClusters, rpdm.numberOfNodes,yj);
			parser.closeFile();
			
			ArrayList<Integer> clusterSizes = new ArrayList<>();
			for(int i=0;i<rpdm.numberOfClusters;i++){
				clusterSizes.add(rpdm.xij.get(i).size());
			}
			Collections.sort(clusterSizes);
			System.out.println("Objective value: "+objVal);
			System.out.println("Sizes of the clusters in ascending order:");
			System.out.println(clusterSizes);
			
			DrawCell cell = new DrawCell((int)(rpdm.radius), rpdm.numberOfNodes, rpdm.numberOfClusters, rpdm.xij,true);
			for(int i=0;i<rpdm.numberOfClusters;i++)
				DrawCell.paintClusterCenter(rpdm.clusters.get(i), i);
			for(int i=0;i<rpdm.numberOfNodes;i++){
				DrawCell.paintNode(rpdm.nodes.get(i), i);
			}
		}
		else if(args[0].contains("1")){
			rpdm.numberOfNodes = Integer.parseInt(args[2]);
			rpdm.numberOfClusters = Integer.parseInt(args[3]);
			rpdm.customerLimit = Integer.parseInt(args[4]);
			rpdm.radius = Integer.parseInt(args[5]);
			rpdm.randomlyPositionNodes(args[1]);
			rpdm.outputGamsSourceFile();
		}
		else{
			help();
		}
		
	}

	/**
	 * Creates an object to keep data about the model.
	 */
	public RandomPositionDrawMain()
	{
		nodes = new ArrayList<>();
		clusters = new ArrayList<>();
		randEngine = new MersenneTwister(new Date());
		uniform = new Uniform(randEngine);
		maxDigit = ((int)Math.log10((int)Math.sqrt(8*radius*radius)))+1;
		if(maxDigit < ((int)Math.log10(numberOfNodes)+1))
			maxDigit = ((int)Math.log10(numberOfNodes)+1);
		maxDigit += 3;
		
	}

	/**
	 * Creates an output file and writes number of nodes, number of clusters, radius of the circle, and the positions of the nodes in that file.
	 * @param outFile Name of the output file
	 */
	public void randomlyPositionNodes(String outFile)
	{
		try {
			positionFile = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile))));
		} catch (FileNotFoundException ex) {
			System.err.println("Error Occured While Creating Position File!!!");
		}
		positionFile.println(numberOfNodes);
		positionFile.println(numberOfClusters);
		positionFile.println(radius);
		for(int i=0;i<numberOfNodes;){
			Point2D.Double p = new Point2D.Double(uniform.nextDoubleFromTo(-radius, radius), uniform.nextDoubleFromTo(-radius, radius));
			if(p.distance(new Point2D.Double()) > radius)
				continue;
			nodes.add(p);
			positionFile.println(p.x+"\t"+p.y);
			i++;
		}
		positionFile.close();
	}
	
	/**
	 * Parse number of nodes, clusters, radius of the cell, and positions of the nodes from a given file.
	 * @param inFile Name of the input file.
	 */
	public void parsePositions(String inFile)
	{
		if(!nodes.isEmpty())
			nodes.clear();
		try {
			input = new Scanner(new FileInputStream(inFile));
		} catch (FileNotFoundException ex) {
			Logger.getLogger(RandomPositionDrawMain.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		numberOfNodes = input.nextInt();
		numberOfClusters = input.nextInt();
		radius = Double.parseDouble(input.next());
		for(int i=0;i<numberOfNodes;i++){
			nodes.add(new Point2D.Double(Double.parseDouble(input.next()), Double.parseDouble(input.next())));
		}
		input.close();
	}
	
	/**
	 * Outputs a GAMS model according to the given parameters.
	 */
	public void outputGamsSourceFile()
	{
		System.out.println("SETS");
		System.out.print("\tI /1");
		for(int i = 2;i <=numberOfNodes ; i++){
			
			System.out.print(", "+i);
		}
		System.out.println("/");
		
		System.out.print("\tJ /1");
		for(int i = 2;i <= numberOfNodes ; i++){
			
			System.out.print(", "+i);
		}
		System.out.println("/;");
		
		//DISTANCE PART START
		System.out.println("TABLE D(I,J)  distance");
		
		for(int j=0;j<maxDigit;j++)
			System.out.print(" ");
		for(int i = 1;i <= numberOfNodes ; i++){
			System.out.print(i);
			int reduction = (int)Math.log10(i)+1;
			for(int j=0;j<maxDigit-reduction;j++)
				System.out.print(" ");
		}
		System.out.println();
		
		
		for(int i = 1;i <= numberOfNodes ; i++){
			
			
			System.out.print(i);
			int reduction = (int)Math.log10(i)+1;
			for(int j=0;j<maxDigit-reduction;j++)
				System.out.print(" ");
			
			for(int j=0;j<numberOfNodes;j++){
				//System.out.print(nodes.get(i).distance(nodes.get(j))+"\t");
				int dist = (int)nodes.get(i-1).distance(nodes.get(j));
				System.out.print(dist);
				if(dist == 0)
					reduction = 1;
				else
					reduction = (int)Math.log10(dist)+1;
				for(int k=0;k<maxDigit-reduction;k++)
					System.out.print(" ");
			}
			if(i<numberOfNodes )
				System.out.println();
			else
				System.out.println(" ;");
		}
		
		//DISTANCE PART END
		System.out.println("SCALAR  P  number of clusters  /"+numberOfClusters+"/");
        System.out.println("        CAP  customer limit    /"+customerLimit+"/;");
		System.out.println("VARIABLES");
        System.out.println("           X(I,J)  whether customer i belong to cluster j in cases");
        System.out.println("           Y(J)    whether customer j is cluster center or not");
        System.out.println("           Z       total sum of distances from all cluster centers to all nodes in their clusters");
        System.out.println("BINARY VARIABLE X;");
        System.out.println("BINARY VARIABLE Y;");
        System.out.println("FREE VARIABLE Z;");
        System.out.println("EQUATIONS");
        System.out.println("           COST                 define objective function");
        System.out.println("           ASSIGNMENT(I)        assign each node to a cluster");
        System.out.println("           CLUSTERCENTER(I,J)   assign nodes to other nodes only if it is cluster center");
        System.out.println("           CLUSTERS             number of clusters");
        System.out.println("           CAPACITY(J)          capacity of cluster j;");
        System.out.println("COST ..                 Z  =E=  SUM((I,J), D(I,J)*X(I,J)) ;");
        System.out.println("ASSIGNMENT(I) ..        SUM(J, X(I,J))  =E=  1 ;");
        System.out.println("CLUSTERCENTER(I,J) ..   X(I,J)  =L=  Y(J) ;");
        System.out.println("CLUSTERS ..             SUM(J, Y(J)) =E= P;");
        System.out.println("CAPACITY(J) ..          SUM(I, X(I,J)) =L= CAP;");
        System.out.println("MODEL CLUSTER /ALL/ ;");
		System.out.println("CLUSTER.optfile = 1;");
		System.out.println("OPTION ResLim=1E75;");
        System.out.println("SOLVE CLUSTER USING MIP MINIMIZING Z ;");
	}
	
	/**
	 * Prints the menu.
	 */
	public static void help()
	{
		System.out.println("The first command line argument indicates whether it should create a GAMS model file or draw a given solution.");
		System.out.println("If the first argument is \"0\" it commands the program to draw a given solution with the following parameters:");
		System.out.println("---Second parameter is the name of the file that contains position of the nodes for the solution");
		System.out.println("---Third parameter is the name of the GAMS solution file");
		System.out.println("If the first argument is \"1\" it commands the program to generate a GAMS model with the given parameters:");
		System.out.println("---Second parameter is the name of the file to write the positions of the nodes in the model");
		System.out.println("---Third parameter is number of nodes");
		System.out.println("---Forth parameter is number of clusters");
		System.out.println("---Fifth parameter is the maximum number of customers per cluster");
		System.out.println("---Sixth parameter is the radius of the circle in which the nodes will be deployed uniformly");
	}
}

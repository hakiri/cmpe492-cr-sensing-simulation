package PositonDraw;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomPositionDrawMain {
	
	int numberOfNodes = 50;
	int numberOfClusters = 5;
	int customerLimit = 15;
	double width = 150;
	double height = 150;
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
	
	public static void main(String []args)
	{
		rpdm = new RandomPositionDrawMain();
		
		if(args[0].contains("0")){
			rpdm.parsePositions(args[1]);
			Parser parser = new Parser(args[2]);
			int constt = Integer.parseInt(args[3]);
			//rpdm.height = rpdm.width = Integer.parseInt(args[4]);
			parser.parse(rpdm.nodes, rpdm.clusters, rpdm.xij, rpdm.numberOfClusters, rpdm.numberOfNodes);
			parser.close();
			
			DrawCell cell = new DrawCell((int)(rpdm.width), rpdm.numberOfNodes, rpdm.numberOfClusters, constt);
			for(int i=0;i<rpdm.numberOfClusters;i++)
				DrawCell.paintClusterCenter(rpdm.clusters.get(i), Color.RED, i);
			for(int i=0;i<rpdm.numberOfNodes;i++){
				DrawCell.paintNode(rpdm.nodes.get(i), Color.BLUE, i);
			}
		}
		else{
			rpdm.numberOfNodes = Integer.parseInt(args[2]);
			rpdm.numberOfClusters = Integer.parseInt(args[3]);
			rpdm.customerLimit = Integer.parseInt(args[4]);
			rpdm.height = rpdm.width = Integer.parseInt(args[5]);
			rpdm.randomlyPositionNodes(args[1]);
			rpdm.outputGamsSourceFile();
		}
		
	}

	public RandomPositionDrawMain()
	{
		nodes = new ArrayList<>();
		clusters = new ArrayList<>();
		randEngine = new MersenneTwister(new Date());
		uniform = new Uniform(randEngine);
		maxDigit = ((int)Math.log10((int)Math.sqrt(4*width*width+4*height*height)))+1;
		if(maxDigit < ((int)Math.log10(numberOfNodes)+1))
			maxDigit = ((int)Math.log10(numberOfNodes)+1);
		maxDigit += 3;
		
	}

	void randomlyPositionNodes(String outFile)
	{
		try {
			positionFile = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile))));
		} catch (FileNotFoundException ex) {
			System.err.println("Error Occured While Creating Position File!!!");
		}
		positionFile.println(numberOfNodes);
		positionFile.println(numberOfClusters);
		positionFile.println(width);
		for(int i=0;i<numberOfNodes;i++){
			nodes.add(new Point2D.Double(uniform.nextDoubleFromTo(-width, width), uniform.nextDoubleFromTo(-height, height)));
			positionFile.println(nodes.get(i).x+"\t"+nodes.get(i).y);
		}
		positionFile.close();
	}
	
	void parsePositions(String inFile)
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
		width = height = Double.parseDouble(input.next());
		for(int i=0;i<numberOfNodes;i++){
			nodes.add(new Point2D.Double(Double.parseDouble(input.next()), Double.parseDouble(input.next())));
		}
		input.close();
	}
	
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
        System.out.println("           Y(J)    qq");
        System.out.println("           Z       total transportation costs in thousands of dollars");
        System.out.println("BINARY VARIABLE X;");
        System.out.println("BINARY VARIABLE Y;");
        System.out.println("FREE VARIABLE Z;");
        System.out.println("EQUATIONS");
        System.out.println("           COST          define objective function");
        System.out.println("           SUPPLY(I)     observe supply limit at plant i");
        System.out.println("           DEMAND(I,J)   satisfy demand at market j");
        System.out.println("           CLUSTERS      qq");
        System.out.println("           CAPACITY(J)   ww;");
        System.out.println("COST ..          Z  =E=  SUM((I,J), D(I,J)*X(I,J)) ;");
        System.out.println("SUPPLY(I) ..     SUM(J, X(I,J))  =E=  1 ;");
        System.out.println("DEMAND(I,J) ..   X(I,J)  =L=  Y(J) ;");
        System.out.println("CLUSTERS ..      SUM(J, Y(J)) =E= P;");
        System.out.println("CAPACITY(J) ..   SUM(I, X(I,J)) =L= CAP;");
        System.out.println("MODEL CLUSTER /ALL/ ;");
		System.out.println("CLUSTER.optfile = 1;");
		System.out.println("OPTION ResLim=1E75;");
        System.out.println("SOLVE CLUSTER USING MIP MINIMIZING Z ;");
	}
}

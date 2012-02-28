package PositonDraw;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;

public class RandomPositionDrawMain {
	
	int numberOfNodes = 1000;
	int numberOfClusters = 20;
	int customerLimit = 75;
	double width = 2000;
	double height = 2000;
	int maxDigit;
	String []args = null;
	ArrayList<Point2D.Double> nodes;
	RandomEngine randEngine;
	Uniform uniform;
	
	public static void main(String []args)
	{
		RandomPositionDrawMain rpdm = new RandomPositionDrawMain(args);
		rpdm.randomlyPositionNodes();
		rpdm.outputGamsSourceFile();
		
	}

	public RandomPositionDrawMain(String []args)
	{
		this.args = args;
		nodes = new ArrayList<>();
		randEngine = new MersenneTwister(new Date());
		uniform = new Uniform(randEngine);
		maxDigit = ((int)Math.log10((int)Math.sqrt(width*width+height*height)))+1;
		if(maxDigit < ((int)Math.log10(numberOfNodes)+1))
			maxDigit = ((int)Math.log10(numberOfNodes)+1);
		maxDigit += 2;
	}
	
	void randomlyPositionNodes()
	{
		for(int i=0;i<numberOfNodes;i++){
			nodes.add(new Point2D.Double(uniform.nextDoubleFromTo(0, width), uniform.nextDoubleFromTo(0, height)));
		}
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
        System.out.println("SOLVE CLUSTER USING MIP MINIMIZING Z ;");
	}
}

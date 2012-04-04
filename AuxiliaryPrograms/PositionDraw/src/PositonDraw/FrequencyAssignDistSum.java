package PositonDraw;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class FrequencyAssignDistSum {
    //TODO Correct model
	public static double solve(ArrayList<Integer> capacities, ArrayList<Point2D.Double> nodes, double radius, ArrayList<ArrayList<Integer>> groups) {
		double objVal = 0;
		int numberOfFrequencies = capacities.size();
		int numberOfNodes = nodes.size();
		for(int i=0;i<numberOfFrequencies;i++)
			groups.add(new ArrayList<Integer>());
		
		try {
			IloCplex cplex = new IloCplex();
			double M = radius * 4;

			IloNumVar[][] x = new IloNumVar[numberOfNodes][];
			IloNumVar z = cplex.numVar(0, 2000000000);

			for (int i = 0; i < numberOfNodes; i++) {
				x[i] = cplex.boolVarArray(numberOfFrequencies);
			} 

			for (int i = 0; i < numberOfNodes; i++)       // assure that every node is assigned to exactly one frequency
				cplex.addEq(cplex.sum(x[i]), 1);

			for(int k = 0; k < numberOfFrequencies; k++){	// frequency capacity assurance
				IloLinearNumExpr v = cplex.linearNumExpr(); 
				for(int i = 0; i < numberOfNodes; i++)
					v.addTerm(1., x[i][k]);
				cplex.addEq(v, capacities.get(k));
			}
			
			for (int k = 0; k < numberOfFrequencies; k++) {     // Assure that none of the clusters exceeds its node limit
				IloLinearNumExpr doubleSum = cplex.linearNumExpr();
				for(int i = 0; i < numberOfNodes - 1; i++){
					IloLinearNumExpr sum = cplex.linearNumExpr();
					for(int j = i+1; j < numberOfNodes; j++){
						IloLinearNumExpr v = cplex.linearNumExpr();
						double dij = nodes.get(i).distance(nodes.get(j));
						v.addTerm((dij-M), x[i][k]);
						v.addTerm((dij-M), x[j][k]);
						v.setConstant(2*M-dij);
						sum.add(v);
					}
					doubleSum.add(sum);
				}
				cplex.addLe(z, doubleSum);
			}      

			IloLinearNumExpr expr = cplex.linearNumExpr();	//Construct objective function
			
			expr.addTerm(z, 1);
			
			cplex.addMaximize(expr);
			
			cplex.setOut(null);
			if ( cplex.solve() ) {
				for (int i = 0; i < numberOfNodes; ++i) {
					for (int k = 0; k < numberOfFrequencies; ++k){
						//System.out.print(cplex.getValue(x[i][j])+" ");
						if(cplex.getValue(x[i][k]) >= 0.999)
							groups.get(k).add(i);
					}
					//System.out.println();
				}
				objVal = cplex.getObjValue();
			}
			
			cplex.end();
		}
		catch (IloException exc) {
			System.out.println(exc);
		}
		return objVal;
   }
}

package FrequencyAssignment;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class FrequencyAssignDistSum {
    
	static double solveWithWkijSummation(ArrayList<Integer> capacities, ArrayList<Point2D.Double> nodes, double radius, ArrayList<ArrayList<Integer>> groups) {
		double objVal = 0;
		int numberOfFrequencies = capacities.size();
		int numberOfNodes = nodes.size();
		for(int i=0;i<numberOfFrequencies;i++)
			groups.add(new ArrayList<Integer>());
		
		try {
			IloCplex cplex = new IloCplex();
			double M = radius * 4;

			IloNumVar[][] x = new IloNumVar[numberOfFrequencies][];
			for (int i = 0; i < numberOfFrequencies; i++) {
				x[i] = cplex.boolVarArray(numberOfNodes);
			} 
			
			IloNumVar[][][] w = new IloNumVar[numberOfFrequencies][][];
			for (int k = 0; k < numberOfFrequencies; k++) {
				w[k] = new IloNumVar[numberOfNodes - 1][];
				for(int i=0;i<numberOfNodes-1;i++){
					w[k][i] = cplex.numVarArray(numberOfNodes - i - 1, 0., 1.);
				}
			}
			
			IloNumVar z = cplex.numVar(0, 2000000000);

			for (int k = 0; k < numberOfFrequencies; k++)       // frequency capacity assurance
				cplex.addEq(cplex.sum(x[k]), capacities.get(k));

			for(int i = 0; i < numberOfNodes; i++){		// assure that every node is assigned to exactly one frequency
				IloLinearNumExpr v = cplex.linearNumExpr(); 
				for(int k = 0; k < numberOfFrequencies; k++)
					v.addTerm(1., x[k][i]);
				cplex.addEq(v, 1.);
				v.clear();
			}
			
			for (int k = 0; k < numberOfFrequencies; k++) {     // Assure that none of the clusters exceeds its node limit
				IloLinearNumExpr doubleSum = cplex.linearNumExpr();
				for(int i = 0; i < numberOfNodes - 1; i++){
					//IloLinearNumExpr sum = cplex.linearNumExpr();
					for(int j = i+1; j < numberOfNodes; j++){
						//IloLinearNumExpr v = cplex.linearNumExpr();
						double dij = nodes.get(i).distance(nodes.get(j));
						doubleSum.addTerm(dij, w[k][i][j-i-1]);
						//sum.add(v);
					}
					//doubleSum.add(sum);
				}
				cplex.addLe(z, doubleSum);
			}
			
			for (int k = 0; k < numberOfFrequencies; k++) {     // Assure that none of the clusters exceeds its node limit
				for(int i = 0; i < numberOfNodes - 1; i++){
					for(int j = i+1; j < numberOfNodes; j++){
						cplex.addLe(w[k][i][j-i-1], x[k][i]);
						cplex.addLe(w[k][i][j-i-1], x[k][j]);
						IloLinearNumExpr v = cplex.linearNumExpr();
						v.addTerm(-1., w[k][i][j-i-1]);
						v.addTerm(1., x[k][i]);
						v.addTerm(1., x[k][j]);
						cplex.addLe(v, 1);
					}
				}
			}

			IloLinearNumExpr expr = cplex.linearNumExpr();	//Construct objective function
			
			expr.addTerm(z, 1);
			
			cplex.addMaximize(expr);
			
			cplex.setOut(null);
			//cplex.exportModel("model.lp");
			if ( cplex.solve() ) {
				for (int i = 0; i < numberOfNodes; ++i) {
					for (int k = 0; k < numberOfFrequencies; ++k){
						//System.out.print(cplex.getValue(x[i][j])+" ");
						if(cplex.getValue(x[k][i]) >= 0.999)
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
	
	static double solveXkijSummation(ArrayList<Integer> capacities, ArrayList<Point2D.Double> nodes, double radius, ArrayList<ArrayList<Integer>> groups) {
		double objVal = 0;
		int numberOfFrequencies = capacities.size();
		int numberOfNodes = nodes.size();
		for(int i=0;i<numberOfFrequencies;i++)
			groups.add(new ArrayList<Integer>());
		
		try {
			IloCplex cplex = new IloCplex(); 
			
			IloNumVar[][][] x = new IloNumVar[numberOfFrequencies][][];
			for (int k = 0; k < numberOfFrequencies; k++) {
				x[k] = new IloNumVar[numberOfNodes][];
				for(int i=0;i<numberOfNodes;i++){
					x[k][i] = cplex.boolVarArray(numberOfNodes);
				}
			}
			
			IloNumVar z = cplex.numVar(0, 2000000000);

			for (int k = 0; k < numberOfFrequencies; k++){       // frequency capacity assurance
				for(int i = 0; i < numberOfNodes; i++){
					IloLinearNumExpr v = cplex.linearNumExpr();
					v.addTerm(x[k][i][i], capacities.get(k));
					cplex.addEq(cplex.sum(x[k][i]), v);
					v.clear();
				}
			}

			for(int i = 0; i < numberOfNodes; i++){		// assure that every node is assigned to exactly one frequency
				IloLinearNumExpr v = cplex.linearNumExpr(); 
				for(int k = 0; k < numberOfFrequencies; k++)
					v.addTerm(1., x[k][i][i]);
				cplex.addEq(v, 1.);
				v.clear();
			}
			
			
			
//			for (int k = 0; k < numberOfFrequencies; k++) {     // Assure that none of the clusters exceeds its node limit
//				IloLinearNumExpr v = cplex.linearNumExpr(); 
//				for(int i = 0; i < numberOfNodes; i++){
//					for(int j = 0; j < numberOfNodes; j++){
//						double dij = nodes.get(i).distance(nodes.get(j));
//						v.addTerm(dij, x[k][i][j]);
//					}
//				}
//				cplex.addLe(z, v);
//			}
//			
//			cplex.addMaximize(z);
			
			IloLinearNumExpr expr = cplex.linearNumExpr();	//Construct objective function
			
			for (int k = 0; k < numberOfFrequencies; k++) {     // Assure that none of the clusters exceeds its node limit
				for(int i = 0; i < numberOfNodes; i++){
					for(int j = 0; j < numberOfNodes; j++){
						double dij = nodes.get(i).distance(nodes.get(j));
						expr.addTerm(dij, x[k][i][j]);
					}
				}
			}
			
			cplex.addMaximize(expr);
			
			//cplex.setOut(null);
			cplex.exportModel("model.lp");
			if ( cplex.solve() ) {
				for (int i = 0; i < numberOfNodes; ++i) {
					for (int k = 0; k < numberOfFrequencies; ++k){
						System.out.print(cplex.getValue(x[k][i][i])+" ");
						if(cplex.getValue(x[k][i][i]) >= 0.999)
							groups.get(k).add(i);
					}
					System.out.println();
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
	
	static double solve2(ArrayList<Integer> capacities, ArrayList<Point2D.Double> nodes, double radius, ArrayList<ArrayList<Integer>> groups) {
		double objVal = 0;
		int numberOfFrequencies = capacities.size();
		int numberOfNodes = nodes.size();
		for(int i=0;i<numberOfFrequencies;i++)
			groups.add(new ArrayList<Integer>());
		
		try {
			IloCplex cplex = new IloCplex();
			double M = radius * 4;

			IloNumVar[][] x = new IloNumVar[numberOfFrequencies][];
			for (int i = 0; i < numberOfFrequencies; i++) {
				x[i] = cplex.boolVarArray(numberOfNodes);
			} 
			
			IloNumVar z = cplex.numVar(-2000000000, 2000000000);

			for (int k = 0; k < numberOfFrequencies; k++)       // frequency capacity assurance
				cplex.addEq(cplex.sum(x[k]), capacities.get(k));

			for(int i = 0; i < numberOfNodes; i++){		// assure that every node is assigned to exactly one frequency
				IloLinearNumExpr v = cplex.linearNumExpr(); 
				for(int k = 0; k < numberOfFrequencies; k++)
					v.addTerm(1., x[k][i]);
				cplex.addEq(v, 1.);
			}
			
			for (int k = 0; k < numberOfFrequencies; k++) {     // Assure that none of the clusters exceeds its node limit
				IloLinearNumExpr doubleSum = cplex.linearNumExpr();
				for(int i = 0; i < numberOfNodes - 1; i++){
					IloLinearNumExpr sum = cplex.linearNumExpr();
					for(int j = i+1; j < numberOfNodes; j++){
						IloLinearNumExpr v = cplex.linearNumExpr();
						double dij = nodes.get(i).distance(nodes.get(j));
						v.addTerm(dij, x[k][i]);
						v.addTerm(dij, x[k][j]);
						v.setConstant(-dij);
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
						if(cplex.getValue(x[k][i]) >= 0.999)
							groups.get(k).add(i);
					}
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

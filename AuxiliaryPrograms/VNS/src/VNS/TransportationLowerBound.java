package VNS;

import ilog.cplex.*;
import ilog.concert.*;

public class TransportationLowerBound {
    
	public static double solve(VNS.Solution a, boolean exportModel) {
		double objVal = 0;
		try {
			IloCplex cplex = new IloCplex();

			int nbMedians = VNSMain.numberOfClusters;
			int nbNodes = VNSMain.numberOfNodes;
			double capacity = VNSMain.capacity;

			IloNumVar[][] x = new IloNumVar[nbNodes][];

			for (int i = 0; i < nbNodes; i++) {
				x[i] = cplex.numVarArray(nbMedians, 0., 1.);
			} 

			for (int i = 0; i < nbNodes; i++)       // assure that every node is assigned to a cluster
				cplex.addEq(cplex.sum(x[i]), 1);

			for (int j = 0; j < nbMedians; j++) {     // Assure that none of the clusters exceeds its node limit
				IloLinearNumExpr v = cplex.linearNumExpr(); 
				for(int i = 0; i < nbNodes; i++)
					v.addTerm(1., x[i][j]);
				cplex.addLe(v, capacity);
			}      

			
			
			
			IloLinearNumExpr expr = cplex.linearNumExpr();	//Construct objective function
			for (int i = 0; i < nbNodes; ++i) {
				for (int j = 0; j < nbMedians; ++j) {
					expr.addTerm(x[i][j], VNSMain.nodes.get(a.yj.get(j)).distance(VNSMain.nodes.get(i)));
				}
			}

			cplex.addMinimize(expr);

			if(exportModel)
				cplex.exportModel("TransportModel.lp");
			cplex.setOut(null);
			if ( cplex.solve() ) {
//				for (int i = 0; i < nbNodes; ++i) {
//					System.out.print("   " + i + ": ");
//					for (int j = 0; j < nbMedians; ++j)
//						if(cplex.getValue(x[i][j]) != 0.0)
//							System.out.print("" + cplex.getValue(x[i][j]) + "\t");
//					System.out.println();
//				}
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

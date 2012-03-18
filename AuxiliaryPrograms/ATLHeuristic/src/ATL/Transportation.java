package ATL;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Transportation {
    
	public static double solve() {
		double objVal = 0;
		try {
			IloCplex cplex = new IloCplex();

			int nbMedians = ATLHueristicMain.numberOfClusters;
			int nbNodes = ATLHueristicMain.numberOfNodes;
			double capacity = ATLHueristicMain.clusterCapacity;

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
					expr.addTerm(x[i][j], ATLHueristicMain.clusterCenters.get(j).distance(ATLHueristicMain.nodes.get(i)));
				}
			}

			cplex.addMinimize(expr);

			cplex.setOut(null);
			if ( cplex.solve() ) {
				for (int i = 0; i < nbNodes; ++i) {
					for (int j = 0; j < nbMedians; ++j){
						if(cplex.getValue(x[i][j]) != 0.0){
							ATLHueristicMain.yij.get(j).add(i);
							break;
						}
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

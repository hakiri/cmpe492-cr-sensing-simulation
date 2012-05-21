package Heuristic;

import SimulationRunner.SimulationRunner;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 * This class solves transportation in order to assign nodes to the closest cluster center also regarding its capacity constraint.
 */
public class Transportation {
    
	/**
	 * Solves transportation in order to assign nodes to the closest cluster center also regarding its capacity constraint.
	 * @return Objective value of the solution.
	 */
	public static double solve() {
		double objVal = 0;
		try {
			IloCplex cplex = new IloCplex();

			int nbMedians = ATLHueristic.numberOfClusters;
			int nbNodes = SimulationRunner.args.getNumberOfCrNodes();
			double capacity = ATLHueristic.clusterCapacity;

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
				cplex.addGe(v, 1.);
			}      

			IloLinearNumExpr expr = cplex.linearNumExpr();	//Construct objective function
			for (int i = 0; i < nbNodes; ++i) {
				for (int j = 0; j < nbMedians; ++j) {
					expr.addTerm(x[i][j], ATLHueristic.clusterCenters.get(j).distance(SimulationRunner.crBase.getCRNode(i).getPosition()));
				}
			}

			cplex.addMinimize(expr);

			cplex.setOut(null);
			if ( cplex.solve() ) {
				for (int i = 0; i < nbNodes; ++i) {
					for (int j = 0; j < nbMedians; ++j){
						if(cplex.getValue(x[i][j]) != 0.0){
							ATLHueristic.yij.get(j).add(i);
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

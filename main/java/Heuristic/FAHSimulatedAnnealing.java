package Heuristic;

import SimulationRunner.SimulationRunner;
import cern.jet.random.Uniform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Solves the maximization problem for FAH.
 */
public class FAHSimulatedAnnealing {

	/**
	 * Solution representation class which generates random solutions, neighbours,
	 * calculates objective values and penalty and compares two solutions.
	 */
	public static class Solution implements Comparable<Solution> {
		double objVal;
		double penalty;
		ArrayList<ArrayList<Integer>> soln;
		int disposed;

		/**
		 * Create a random solution or an initial best solution.
		 * @param random Determines whether the solution will be random or not
		 */
		public Solution(boolean random) {
			if(random){
				soln = new ArrayList<>(numberOfNodesInCluster);
				for(int i=0;i<numberOfGroupsInCluster;i++){
					soln.add(new ArrayList<Integer>());
				}
				for(int i=0;i<numberOfNodesInCluster - 1;i++){
					soln.get(uniform.nextIntFromTo(0, numberOfGroupsInCluster-1)).add(i);
				}
				disposed = numberOfNodesInCluster - 1;
				computeObjVal();
			}
			else{
				objVal = Double.NEGATIVE_INFINITY;
				penalty = 0;
			}
		}

		/**
		 * Create a copy of a given solution.
		 * @param rhs Solution to be copied.
		 */
		public Solution(Solution rhs) {
			this.objVal = rhs.objVal;
			this.penalty = rhs.penalty;
			this.disposed = rhs.disposed;
			this.soln = new ArrayList<>(numberOfGroupsInCluster);
			for(int i=0;i<numberOfGroupsInCluster;i++){
				this.soln.add(new ArrayList<>(Collections.nCopies(rhs.soln.get(i).size(), 0)));
				Collections.copy(this.soln.get(i), rhs.soln.get(i));
			}
			
		}
		
		/**
		 * Computes the objective value and penalty of the solution.
		 * @return Objective value
		 */
		public final double computeObjVal(){
			computePenalty();
			objVal = Double.POSITIVE_INFINITY;
			for(int k=0;k<numberOfGroupsInCluster;k++){
				double dummy=0;
				for(int i=0;i<soln.get(k).size()-1;i++){
					for(int j=i+1;j<soln.get(k).size();j++){
						dummy += distances[soln.get(k).get(i)][soln.get(k).get(j)];
					}
				}
				if(dummy < objVal)
					objVal = dummy;
			}
			objVal -= pressure*penalty;		//Penalize objective value with penalty
												// and pressure
			return objVal;
		}
		
		/**
		 * Computes penalty value of the solution.
		 * @return Penalty value
		 */
		public double computePenalty(){
			penalty = 0;
			for(int j=0;j<numberOfGroupsInCluster;j++){
				penalty += Math.abs(soln.get(j).size() - capacities.get(j));
			}
			return penalty;
		}
		
		/**
		 * Generate neighbour of the solution by changing frequency assignment of
		 * a node randomly.
		 * @return Random solution in neighbourhood.
		 */
		private Solution generateShiftNeighbour(){
			Solution s = new Solution(this);
			int group1;
			do{
				 group1 = uniform.nextIntFromTo(0, numberOfGroupsInCluster-1);
			}while(s.soln.get(group1).isEmpty());
			int ind1 = uniform.nextIntFromTo(0, s.soln.get(group1).size()-1);
			if(uniform.nextBoolean()&&numberOfGroupsInCluster>1){
				int group2;
				do{
					group2 = uniform.nextIntFromTo(0, numberOfGroupsInCluster-1);
				}while(group1 == group2);
				s.soln.get(group2).add(s.soln.get(group1).get(ind1));
				s.soln.get(group1).remove((int)ind1);
			}
			else{
				if(s.disposed == -1){
					s.disposed = s.soln.get(group1).get(ind1);
					s.soln.get(group1).remove((int)ind1);
				}
				else{
					s.soln.get(group1).add(disposed);
					s.disposed = -1;
				}
			}
			s.computeObjVal();
			return s;
		}
		
		/**
		 * Generate neighbour of the solution by changing frequency assignment of
		 * a node randomly.
		 * @return Random solution in neighbourhood.
		 */
		private Solution generateSwapNeighbour(){
			Solution s = new Solution(this);
			int group1;
			do{
				 group1 = uniform.nextIntFromTo(0, numberOfGroupsInCluster-1);
			}while(s.soln.get(group1).isEmpty());
			int group2;
			do{
				group2 = uniform.nextIntFromTo(0, numberOfGroupsInCluster-1);
			}while(group1 == group2 || s.soln.get(group2).isEmpty());
			int ind1 = uniform.nextIntFromTo(0, s.soln.get(group1).size()-1);
			int ind2 = uniform.nextIntFromTo(0, s.soln.get(group2).size()-1);
			int node1 = s.soln.get(group1).get(ind1);
			int node2 = s.soln.get(group2).get(ind2);
			
			s.soln.get(group1).add(node2);
			s.soln.get(group2).add(node1);
			
			s.soln.get(group1).remove((int)ind1);
			s.soln.get(group2).remove((int)ind2);
			
			s.computeObjVal();
			return s;
		}
		
		/**
		 * Shift Neighbourhood structure
		 */
		public static final int SHIFT = 0;
		/**
		 * Swap Neighbourhood structure
		 */
		public static final int SWAP = 1;
		
		/**
		 * Generates neighbour according to given structure
		 * @param type	Type of neighbourhood
		 * @return	Neighbouring solution
		 */
		public Solution generateNeighbour(int type){
			if(type == SHIFT)
				return generateShiftNeighbour();
			else
				return generateSwapNeighbour();
		}

		static final int ACCEPT = -1;
		static final int REJECT = 1;
		
		/**
		 * Compares two solution and if the objective value of the second solution
		 * is larger, return ACCEPT with probability 1. If the objective value
		 * of the second solution is smaller, return ACCEPT with probability
		 * exp(difference of the objective values over temperature).
		 * @param rhs Solution to compare
		 */
		@Override
		public int compareTo(Solution rhs) {
			double objValDiff = Math.exp((rhs.objVal - objVal)/temperature);
			double random = uniform.nextDoubleFromTo(0, 1);
			if(random < objValDiff)
				return ACCEPT;
			else
				return REJECT;
		}
	}
	
	static int numberOfNodesInCluster;
	static int numberOfGroupsInCluster;
	static ArrayList<Integer> capacities;
	static double pressure;
	static double finalPressure;
	static double temperature;
	static final double beta = 0.97;
	static final double gama = 0.05;
	static int k;
	static int numberOfIterationsPerCycle;
	static double [][]distances;
	static Uniform uniform;
	
	/**
	 * Solves the maximization problem for FAH.
	 * @param nodes		Nodes in cluster
	 * @param caps		Capacities of the groups
	 * @param groups	Groups in the cluster
	 * @return  Objective value
	 */
	public static double solve(ArrayList<Point2D.Double> nodes, ArrayList<Integer> caps, ArrayList<ArrayList<Integer>> groups) {
		System.out.println(nodes.size());
		//System.out.println("Group capacities: "+caps);
		uniform = new Uniform(SimulationRunner.randEngine);
		capacities = caps;
		numberOfGroupsInCluster = caps.size();
		numberOfNodesInCluster = nodes.size();
		calculateDistanceMatrix(nodes);
		pressure = 0;
		System.out.println("Initial temperature: "+initialTemperature(0.99999, 1000));
		System.out.println("Final pressure:      "+finalPressure+"\n");
		Solution current = new Solution(true);
		Solution bestFeasible = new Solution(false);
		Solution best = new Solution(false);
		numberOfIterationsPerCycle = 10000;
		int numberOfAcceptedSoln = 0;
		int numberOfAcceptedInfeasibleSoln = 0;
		int numberOfCyclesWithoutImprovement = 0;
		/*
		 * Continue cycles until
		 * 1- At least 500 cycles completed
		 * 2- No improvement in the incumbent solution is achieved in the last 150 cycles
		 * 3- Objective value is larger than negative infinity
		 */
		for(k = 1; k < 251 || numberOfCyclesWithoutImprovement < 75 || bestFeasible.objVal == Double.NEGATIVE_INFINITY;k++){
			for(int i = 0; i < numberOfIterationsPerCycle; i++){
				Solution neighbor;
				neighbor = current.generateNeighbour(Solution.SHIFT);
				//If solution is accepted
				if(current.compareTo(neighbor) == Solution.ACCEPT){
					current = neighbor;			//Update current solution
					//If it has better objective value than incumbent solution and
					//it is feasible update incumbent solution
					if(current.penalty == 0 && current.objVal > bestFeasible.objVal){
						bestFeasible = new Solution(current);
						numberOfCyclesWithoutImprovement = -1;
					}
					//If new solution has better objective value than best solution
					//seen so far (may be infeasible) update best solution
					if(current.objVal > best.objVal){
						best = new Solution(current);
					}
					
					numberOfAcceptedSoln++;
					if(current.penalty != 0)
						numberOfAcceptedInfeasibleSoln++;
				}
				//System.out.println("Cycle: "+i+" Current:       "+current.objVal);
			}
			numberOfCyclesWithoutImprovement++;
			//System.out.println("Cycle: "+k+" Current:       "+current.objVal);
			//At each 50 cycle output current, best, and best feasible solution
			if(k%25 == 0){
				System.out.println("Cycle: "+k+" Current:       "+current.objVal);
				System.out.println("Cycle: "+k+" Best:          "+best.objVal);
				System.out.println("Cycle: "+k+" Best feasible: "+bestFeasible.objVal+"\n");
			}
			updateTemperature();
			
			/* If the number of accepted infeasible solution more than half of
			 * the number of all accepted solution (i.e. number of accepted
			 * infeasible solutions is larger than number of accepted feasible
			 * solutions) increment the pressure
			 */
			if(numberOfAcceptedInfeasibleSoln > numberOfAcceptedSoln / 2)
				changePressure(true);
			/* Otherwise decrease it */
			else
				changePressure(false);
			//Set these numbers to zero for the next cycle
			numberOfAcceptedInfeasibleSoln = 0;
			numberOfAcceptedSoln = 0;
		}
		
		System.out.println("Cycle: "+k+" Best:          "+best.objVal);
		System.out.println("Cycle: "+k+" Best feasible: "+bestFeasible.objVal);
		for(int i=0;i<numberOfGroupsInCluster;i++){
			groups.add(new ArrayList<Integer>(caps.get(i)));
			for(int j=0; j<caps.get(i);j++){
				groups.get(i).add(bestFeasible.soln.get(i).get(j));
			}
			Collections.sort(groups.get(i));
		}
		return bestFeasible.objVal;
	}
	
	/**
	 * Find initial temperature and final pressure.
	 * @param initalAcceptanceRatio	Desired inital acceptance ratio
	 * @param numberOfSoln			Number of solutions to be generated to determine parameters
	 * @return						Inital temperature
	 */
	static double initialTemperature(double initalAcceptanceRatio, int numberOfSoln){
		double deltaF = 0;
		double K = 10;
		finalPressure = 0;
		for(int i = 0; i < numberOfSoln; i++){
			Solution s1 = new Solution(true);
			Solution s2 = s1.generateShiftNeighbour();
			deltaF += Math.abs(s1.objVal - s2.objVal);
			double press = (s1.objVal/s1.penalty)*K;
			if(s1.penalty > 0.0 && press > finalPressure)
				finalPressure = press;
		}
		deltaF /= numberOfSoln;
		return temperature = -deltaF/Math.log(initalAcceptanceRatio);
	}
	
	static int pressureIndex = 0;
	/**
	 * Changes value of the pressure parameter
	 * @param increment If true increments the pressure
	 */
	static void changePressure(boolean increment){
		if(increment)
			pressureIndex++;
		else
			pressureIndex--;
		pressure = finalPressure*(1-Math.exp(-gama*pressureIndex));
		
	}
	
	/**
	 * Updates temperature
	 */
	static void updateTemperature(){
		temperature *= beta;
	}
		
	/**
	 * Calculates the pairwise distance matrix for the nodes in the cluster.
	 * @param nodes	Nodes in the cluster
	 */
	public static void calculateDistanceMatrix(ArrayList<Point2D.Double> nodes){
		distances = new double[numberOfNodesInCluster][numberOfNodesInCluster];
		for(int i=0;i<numberOfNodesInCluster;i++){
			distances[i][i] = 0;
			for(int j=i+1;j<numberOfNodesInCluster;j++){
				double dist = nodes.get(i).distance(nodes.get(j));
				distances[i][j] = dist;
				distances[j][i] = dist;
			}
		}
	}
}

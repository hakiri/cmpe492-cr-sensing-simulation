package VNS;

import java.util.ArrayList;

public class VNS {
	public static class Solution{
		public ArrayList<Integer> yj = new ArrayList<>();
		public ArrayList<ArrayList<Integer>> xij = new ArrayList<>();

		/**
		 * Creates a random solution w.r.t cluster centers
		 */
		public Solution() {
			for(int i=0;i<VNSMain.numberOfClusters;){
				int node = VNSMain.uniform.nextIntFromTo(0, VNSMain.numberOfNodes);
				if(yj.contains(node))
					continue;
				yj.add(node);
				i++;
			}
		}
		
	}
	
	/**
	 * Obtain a good initial solution for VNS procedure
	 * @return Good arbitrary solution
	 */
	public Solution vnslb(){
		Solution a0 = new Solution();
		
		return a0;
	}
	
	/**
	 * Find a solution in one neighbourhood of a
	 * @param a		An initial solution
	 * @param fStar	A boundary for solution
	 * @return		A probably better solution
	 */
	public Solution nslb(Solution a, double fStar){
		return a;
	}
	
	/**
	 * Compares two solutions with respect to their transportation and assignment lower bounds
	 * @param aPrime	A new solution to compare
	 * @param a			Previous solution
	 * @return			<ul> 
	 *						<li>True if aPrime is better</li>
	 *						<li>False otherwise</li>
	 *					</ul>
	 */
	public boolean isBetterLB(Solution aPrime, Solution a){
		return true;
	}
	
	/**
	 * Main solution algorithm
	 * @param a0	Initial solution
	 * @return		Best solution found by heuristic
	 */
	public Solution vns(Solution a0){
		return a0;
	}
	
	/**
	 * Finds the best solution in one neighbourhood of a
	 * @param a	An initial solution
	 * @return	Best solution in the neighbourhood
	 */
	public Solution ns(Solution a){
		return a;
	}
	
	/**
	 * Compares two solutions with respect to their transportation and assignment lower bounds and objective values
	 * @param aPrime	A new solution to compare
	 * @param a			Previous solution
	 * @return			<ul> 
	 *						<li>True if aPrime is better</li>
	 *						<li>False otherwise</li>
	 *					</ul>
	 */
	public boolean isBetter(Solution aPrime, Solution a){
		return true;
	}
}

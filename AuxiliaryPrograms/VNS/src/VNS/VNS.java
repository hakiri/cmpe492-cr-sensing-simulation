package VNS;

import java.util.ArrayList;
import java.util.HashMap;

public class VNS {
	public static class Solution{
		public ArrayList<Integer> yj = new ArrayList<>();
		public ArrayList<ArrayList<Integer>> xij = new ArrayList<>();

		/**
		 * Creates a random solution w.r.t cluster centers
		 */
		public Solution() {
			for(int i=0;i<VNSMain.numberOfClusters;){
				int node = VNSMain.uniform.nextIntFromTo(0, VNSMain.numberOfNodes-1);
				if(yj.contains(node))
					continue;
				yj.add(node);
				i++;
			}
		}
		
		Solution(Solution rhs){
			this.xij = new ArrayList<>();
			this.yj = new ArrayList<>();
			for(int i=0;i<rhs.yj.size();i++)
				this.yj.add(new Integer(rhs.yj.get(i)));
		}
		
	}
	
	HashMap<Solution, Double> solutions = new HashMap<>();

	public VNS() {
	}
	
	/**
	 * Obtain a good initial solution for VNS procedure
	 * @return Good arbitrary solution
	 */
	public Solution vnslb(){
		Solution a = new Solution();
		a = nslb(a,Double.NEGATIVE_INFINITY);
		int r = 1;
		for(int k=1;k<VNSMain.numberOfClusters/5;k++){
			int i;
			for(i=0;i<r;i++){
				Solution aPrime = new Solution(a);
				int ind = -1;
				for(int j=0;j<k;){
					int node = VNSMain.uniform.nextIntFromTo(0, VNSMain.numberOfNodes-1);
					if(aPrime.yj.contains(node))
						continue;
					 ind = VNSMain.uniform.nextIntFromTo(ind+1, VNSMain.numberOfClusters-k+j);
					 aPrime.yj.set(ind, node);
					 j++;
				}
				aPrime = nslb(aPrime,Double.NEGATIVE_INFINITY);
				if(TransportationLowerBound.solve(aPrime, false) < TransportationLowerBound.solve(a, false)){
					a=aPrime;
					break;
				}
			}
//			if(i<r)
//				k=-1;
		}
		return a;
	}
	
	/**
	 * Find a solution in one neighborhood of a
	 * @param a		An initial solution
	 * @param fStar	A boundary for solution
	 * @return		A probably better solution
	 */
	public Solution nslb(Solution a, double fStar){
		Solution aPrime = new Solution(a);
		
		for(int i=0;i<VNSMain.numberOfClusters;i++){
			int initialMedian = a.yj.get(i);
			for(int j=0;j<VNSMain.numberOfNodes;j++){
				if(aPrime.yj.contains(j) || j == initialMedian)
					continue;
				aPrime.yj.set(i, j);
				if(isBetterLB(aPrime, a)){
					if(TransportationLowerBound.solve(aPrime, false) < fStar)
						return aPrime;
					a = new Solution(aPrime);
					//i = -1;
					break;
				}
				aPrime.yj.set(i, initialMedian);
			}
		}
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
		double lb2=TransportationLowerBound.solve(a, false);
		if(NativeLowerBound.findObjective(aPrime)>=lb2)
			return false;
		if(TransportationLowerBound.solve(aPrime, false)>=lb2)
			return false;
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
	 * Finds the best solution in one neighborhood of a
	 * @param a	An initial solution
	 * @return	Best solution in the neighborhood
	 */
	public Solution ns(Solution a){
		Solution aPrime = new Solution(a);
		//TODO Debug this part
		for(int i=0;i<VNSMain.numberOfClusters;i++){
			int initialMedian = a.yj.get(i);
			for(int j=0;j<VNSMain.numberOfNodes;j++){
				if(aPrime.yj.contains(j) || j == initialMedian)
					continue;
				aPrime.yj.set(i, j);
				if(isBetter(aPrime, a)){
					//return ns(aPrime);
					a = new Solution(aPrime);
					//i = -1;
					break;
				}
				aPrime.yj.set(i, initialMedian);
			}
		}
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

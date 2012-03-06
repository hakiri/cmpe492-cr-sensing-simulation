package VNS;

import java.util.ArrayList;

public class NativeLowerBound {
    
    static ArrayList<ArrayList<Integer>> yij = null;
	
    /**
     * First allocates the nodes and then returns the objective value.
     * @return Objective value
     */
    static double findObjective(VNS.Solution a){
        allocateNodes(a);
        return objectiveValue(a);
    }
    
    /**
	 * Evaluates the objective value of the current solution.
	 * @return Objective Value of the problem
	 */
	static private double objectiveValue(VNS.Solution a)
	{
		double objVal = 0;
		for(int i=0;i<VNSMain.numberOfClusters;i++){
			for(int j=0;j<yij.get(i).size();j++)
				objVal += VNSMain.nodes.get(a.yj.get(i)).distance(VNSMain.nodes.get(yij.get(i).get(j)));
		}
		return objVal;
	}
	
	/**
	 * Allocates nodes to its nearest cluster center.
	 */
	static private void allocateNodes(VNS.Solution a)
	{
        yij = new ArrayList<>();
		for(int i=0;i<VNSMain.numberOfClusters;i++)
			yij.add(new ArrayList<Integer>());
		
		for(int i=0;i<VNSMain.numberOfNodes;i++){
			int nearestCluster = -1;
			double distance = Double.POSITIVE_INFINITY;
			for(int j=0;j<VNSMain.numberOfClusters;j++){
				double dist = VNSMain.nodes.get(i).distance(VNSMain.nodes.get(a.yj.get(j)));
				if(dist < distance){
					distance = dist;
					nearestCluster = j;
				}
			}
			yij.get(nearestCluster).add(i);
		}
	}
	
    
}

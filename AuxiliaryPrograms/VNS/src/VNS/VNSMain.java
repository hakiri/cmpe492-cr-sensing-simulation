package VNS;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class VNSMain {
	
    static int numberOfNodes = 1500;
    static int numberOfClusters = 30;
	static int capacity = 75;
    static ArrayList<Point2D.Double> nodes = new ArrayList<>();
    static ArrayList<Point2D.Double> clusterCenters = new ArrayList<>();
    static Uniform uniform;
	public static void main(String []args){
		uniform = new Uniform(new MersenneTwister(new Date()));
		
		parsePositions("untitled8_positions.txt");
		System.out.println(TransportationLowerBound.solve(false));
		//System.out.println(TransportationLowerBound.solve(true));
		System.out.println(NativeLowerBound.findObjective());
	}

	static void randomlyPositionNodes()
	{
		for(int i=0;i<numberOfNodes;i++){
			nodes.add(new Point2D.Double(uniform.nextDoubleFromTo(-1500, 1500), uniform.nextDoubleFromTo(-1500, 1500)));
		}
		for(int i=0;i<numberOfClusters;){
			int ind = uniform.nextIntFromTo(0, numberOfNodes-1);
			if(clusterCenters.contains(nodes.get(ind)))
				continue;
			clusterCenters.add(nodes.get(ind));
			i++;
		}
	}
	
	static void parsePositions(String inFile)
	{
		Scanner input = null;
		if(!nodes.isEmpty())
			nodes.clear();
		try {
			input = new Scanner(new FileInputStream(inFile));
		} catch (FileNotFoundException ex) {
			
		}
		
		numberOfNodes = input.nextInt();
		numberOfClusters = input.nextInt();
		Double.parseDouble(input.next());
		for(int i=0;i<numberOfNodes;i++){
			nodes.add(new Point2D.Double(Double.parseDouble(input.next()), Double.parseDouble(input.next())));
		}
		input.close();
		
		for(int i=0;i<numberOfClusters;){
			int ind = uniform.nextIntFromTo(0, numberOfNodes-1);
			if(clusterCenters.contains(nodes.get(ind)))
				continue;
			clusterCenters.add(nodes.get(ind));
			i++;
		}
	}
	
    public VNSMain() {
    }
	
}

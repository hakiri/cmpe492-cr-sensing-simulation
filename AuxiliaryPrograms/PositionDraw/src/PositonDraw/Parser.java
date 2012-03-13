package PositonDraw;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This classes parses GAMS solution file and determined cluster structures accordingly.
 */
public class Parser {
	Scanner in;

	/**
	 * Creates a Parser object which opens the file with the given name.
	 * @param filename Name of the file to be parsed.
	 */
	public Parser(String filename) {
		try {
			in = new Scanner(new FileInputStream(filename));
		} catch (FileNotFoundException ex) {
			Logger.getLogger(RandomPositionDrawMain.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	/**
	 * This method parses the file and determines the cluster structures.
	 * @param nodes				Positions of the nodes (as input to the method).
	 * @param clusters			Positions of the clusters (as output of the method).
	 * @param xij				(Information about which node belongs to which cluster (as output of the method).
	 * @param numberOfClusters	Number of clusters (as input to the method).
	 * @param numberOfNodes		Number of nodes (as input to the method).
	 * @param yj				Index of the nodes that are selected as cluster centers (as output of the method).
	 * @return					The method return the objective value of the parsed solution (as a string not number).
	 */
	public String parse(ArrayList<Point2D.Double> nodes, ArrayList<Point2D.Double> clusters, ArrayList<ArrayList<Integer>> xij, int numberOfClusters, int numberOfNodes, ArrayList<Integer> yj)
	{
		for(int i=0;i<numberOfClusters;i++){
			xij.add(new ArrayList<Integer>());
		}
		while(in.hasNextLine()){
			String line = in.nextLine();
			if(line.contains(new String("---- VAR X")))
				break;
		}
		
		in.nextLine();
		in.nextLine();
		in.nextLine();
				
		for(int i = 0; i < numberOfNodes * numberOfNodes ; i++){
			String line = in.nextLine();
			String [] list = line.split(" +");
			if(list[0].contains(".")){
				if(list[2].equals("1.000")){
					String [] list2 = list[0].split("[.]");
					int node = Integer.parseInt(list2[0]) - 1;
					int cluster = Integer.parseInt(list2[1]) - 1;
					int clusterId = yj.indexOf(cluster);
					if(clusterId == -1){
						clusterId = yj.size();
						yj.add(cluster);
						xij.get(clusterId).add(node);
						clusters.add(nodes.get(cluster));
					}
					else{
						xij.get(clusterId).add(node);
					}
					if(node == numberOfNodes)
						break;
				}
			}
			else{
				if(list[3].equals("1.000")){
					int node = Integer.parseInt(list[0]) - 1;
					char[] dummy = new char[list[1].length()-1];
					for(int j=0;j<dummy.length;j++)
						dummy[j]=list[1].charAt(j+1);
					int cluster = Integer.parseInt(new String(dummy)) - 1;
					int clusterId = yj.indexOf(cluster);
					if(clusterId == -1){
						clusterId = yj.size();
						yj.add(cluster);
						xij.get(clusterId).add(node);
						clusters.add(nodes.get(cluster));
					}
					else{
						xij.get(clusterId).add(node);
					}
				}
			}
		}
		String objVal = "";
		while(in.hasNextLine()){
			String line = in.nextLine();
			if(line.contains(new String("---- VAR Z"))){
				String [] list = line.split(" +");
				objVal = list[4];
				break;
			}
		}
		
		return objVal;
	}
	
	/**
	 * Closes the input file.
	 */
	public void closeFile(){
		in.close();
	}
}

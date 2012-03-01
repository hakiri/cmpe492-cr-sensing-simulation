/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PositonDraw;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acar
 */
public class Parser {
	Scanner in;

	public Parser(String filename) {
		try {
			in = new Scanner(new FileInputStream(filename));
		} catch (FileNotFoundException ex) {
			Logger.getLogger(RandomPositionDrawMain.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	ArrayList<Integer> parse(ArrayList<Point2D.Double> nodes, ArrayList<Point2D.Double> clusters, ArrayList<ArrayList<Integer>> xij, int numberOfClusters, int numberOfNodes)
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
		
		ArrayList<Integer> yj = new ArrayList<>();
		
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
		return yj;
	}
	
	void close(){
		in.close();
	}
}

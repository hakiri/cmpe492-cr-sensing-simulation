/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;

public class CRNode extends Node{
    
    /**
     * List of frequencies assigned to this node with respect to their snr values.
     */
    private HashMap<Integer,Double> snrValues;
    /**
     * Number of the CRNode.
     */
    private int number;
    
    private static PrintWriter pw = null;
    private static String file_name;
    
    /**
     * Average snr values of the frequencies.
     */
    private static ArrayList<Double> averageSnr = null;
    /**
     * Creates a CRNode with the given frequencies, position and velocity values.
     * @param pos Position of the CRNode
     * @param vel Velocity of the CRNode
     * @param frequencies List of frequencies that are assigned to this node.
     */
    public CRNode(int num,Point2D.Double pos, double vel, ArrayList<Integer> frequencies) {
        this.number = num;
        this.position = new Point2D.Double(pos.x, pos.y);
        this.velocity = vel;
	snrValues = new HashMap<Integer, Double>();
        for(int i=0;i<frequencies.size();i++){ 
            snrValues.put(frequencies.get(i), 0.0); //adding all the frequency values to the 
                                                    //hash table with 0.0 initial snr value
        }
    }
    /**
     * Updates all the snr values of the frequencies which are assigned to this CRNode.
     */
    public void sense(){
        for(Integer i:snrValues.keySet()){
            snrValues.put(i,SimulationRunner.wc.generateSNR(this, i));
            averageSnr.set(i, (averageSnr.get(i)+SimulationRunner.wc.generateSNR(this, i)));
        }
    }
    /**
     * 
     * @return Snr values of each frequencies which are assigned to this node.
     */
    public HashMap<Integer, Double> getSnrValues() {
        return snrValues;
    }
    
    public void logSnrValues(){        
        pw.println("number: "+String.valueOf(number) + " -- position: " +position.toString() + " -- snrValues: " + snrValues.toString());
    }

    public static void initializeAverageSnr(int total_number_of_frequencies){
        averageSnr = new ArrayList<Double>(total_number_of_frequencies);
        
        for(int i=0;i<total_number_of_frequencies;i++){
            averageSnr.add(0.0);
        }
    }
    
    public static void logAverageSnr(int number_of_crnodes){
        for(int i=0;i<averageSnr.size();i++){
            averageSnr.set(i,(averageSnr.get(i)/number_of_crnodes));
        }
        
        pw.println("average snr values: " + averageSnr.toString());
        
        for(int i=0;i<averageSnr.size();i++){
            averageSnr.set(i,0.0);
        }
    }
    
    public static void createLogFile(String file_name){
        try {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_name))));
        } catch (IOException ex) {    
            System.err.println("Error during file operations");
        }
    }
    
    public static void writeLogFile(String log_string){
        pw.println(log_string);
    }
    
    public static void closeLogFile(){
        pw.close();   
    }
       
}

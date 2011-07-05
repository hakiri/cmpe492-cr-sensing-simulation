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

public class CRNode extends Node{
    
    /**
     * List of frequencies assigned to this node with respect to their snr values.
     */
    private HashMap<Integer,Double> snrValues;
    /**
     * Writer for the log file.
     */
    private static PrintWriter pw = null;
    /**
     * Average snr values of the frequencies.
     */
    private static ArrayList<Double> averageSnr = null;
    
    private ArrayList<Integer> freq_list;
    
    /**
     * Creates a CRNode with the given frequencies, position and velocity values.
     * @param pos Position of the CRNode
     * @param vel Velocity of the CRNode
     * @param frequencies List of frequencies that are assigned to this node.
     */
    public CRNode(int id, Point2D.Double pos, double vel) {
        this.id = id;
        this.position = new Point2D.Double(pos.x, pos.y);
        this.velocity = vel;
    }
    
    /**
     * Updates all the snr values of the frequencies which are assigned to this CRNode.
     */
    public void sense(int freq){
        snrValues.put(freq_list.get(freq),SimulationRunner.wc.generateSNR(this, freq_list.get(freq)));
        averageSnr.set(freq_list.get(freq), (averageSnr.get(freq_list.get(freq))+snrValues.get(freq_list.get(freq))));
    }
    
    /**
     * 
     * @return Snr values of each frequencies which are assigned to this node.
     */
    public HashMap<Integer, Double> getSnrValues() {
        return snrValues;
    }
    
    /**
     * It creates the averageSnr arraylist and initially add zeros to the elements.
     * @param total_number_of_frequencies Total number of frequencies 
     */
    public static void initializeAverageSnr(int total_number_of_frequencies){
        averageSnr = new ArrayList<Double>(total_number_of_frequencies);
        for(int i=0;i<total_number_of_frequencies;i++){
            averageSnr.add(0.0);
        }
    }
    
    /**
     * Writes the id of the CRNode, position of the CRNode and snrValues of the CRNode
     * to the log file, respectively.
     */
    public void logSnrValues(){
        pw.println("number: "+String.valueOf(id) + " -- position: " +position.toString() + " -- snrValues: " + snrValues.toString());
    }
    
    /**
     * Calculates average snr values then writes these values to the log file 
     * and then resets the average snr values.
     * @param number_of_crnodes Total number of CRNodes.
     */
    public static void logAverageSnr(int number_of_crnodes, int colomn, double time){
        for(int i=0;i<averageSnr.size();i++){   //calculates the average snr values
            averageSnr.set(i,(averageSnr.get(i)/SimulationRunner.crBase.getFrequency_list().get(i))); // gets the current crnode 
                                                                                        //number that listens to this freq.
        }
        
		SimulationRunner.plot.addPoint(time, averageSnr);
        pw.println("average snr values: " + averageSnr.toString()); //writing to log file
        
        for(int i=0;i<averageSnr.size();i++){ //resets the avarageSnr list.
            averageSnr.set(i,0.0);
        }
    }
    
    /**
     * Creates the log file.
     * @param file_name Name of the log file
     */
    public static void createLogFile(String file_name){
        try {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_name))));
        } catch (IOException ex) {    
            System.err.println("Error during file operations");
        }
    }
    
    /**
     * Writes the input string to the log file.
     * @param log_string String
     */
    public static void writeLogFile(String log_string){
        pw.println(log_string);
    }
    
    /**
     * Closes the log file.
     */
    public static void closeLogFile(){
        pw.close();   
    }
    
    public void  setFrequencyList(ArrayList<Integer> frequencies){
        snrValues = new HashMap<Integer, Double>();
        for(int i=0;i<frequencies.size();i++){ 
            snrValues.put(frequencies.get(i), 0.0); //adding all the frequency values to the 
                                                   //hash table with 0.0 initial snr value
        }
        freq_list=frequencies;
    }
}

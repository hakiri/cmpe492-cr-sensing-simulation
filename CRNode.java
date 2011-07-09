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
    
    private int communication_frequency = -1;
    
    private ArrayList<Integer> freq_list_to_listen;
    
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
     * Updates the snr value of the frequency.
     * @param freq Number of the frequency in snrValues list.
     */
    public void sense(int freq){
        snrValues.put(freq_list_to_listen.get(freq),SimulationRunner.wc.generateSNR(this, freq_list_to_listen.get(freq)));
        averageSnr.set(freq_list_to_listen.get(freq), (averageSnr.get(freq_list_to_listen.get(freq))+snrValues.get(freq_list_to_listen.get(freq))));
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
    public static void logAverageSnr(double time){
        for(int i=0;i<averageSnr.size();i++){   //calculates the average snr values
            averageSnr.set(i,(averageSnr.get(i)/SimulationRunner.crBase.getFrequency_list().get(i))); // gets the current crnode 
                                                                                        //number that listens to this freq.
        }
        
        SimulationRunner.crBase.setLast_averageSnr(averageSnr);
	SimulationRunner.plot.addPoint(0,time, averageSnr);
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
        freq_list_to_listen=frequencies;
    }

    public void setCommunication_frequency(int communication_frequency) {
        this.communication_frequency = communication_frequency;
    }
    
    //TODO communication(collision var mi diye bakacak bikac kere, snir degerlerini log'a yaz.)
    public static void communicate(double time){
        ArrayList<Double> sinr = new ArrayList<Double>();
        for(int i=0;i<SimulationRunner.wc.numberOfFreq();i++){
            sinr.add(0.0);
        }
        for(int i=0;i<SimulationRunner.crNodes.size();i++){
            String collision = "no collision";
            int freq = SimulationRunner.crNodes.get(i).communication_frequency;
            if(freq >= 0){
                sinr.set(freq,SimulationRunner.wc.generateSINR(SimulationRunner.crBase, SimulationRunner.crNodes.get(i), freq));
                if(sinr.get(i)<SimulationRunner.wc.sinrThreshold) //checks if collision occured
                    collision = "collision occured";
                writeLogFile("time:" + String.format("Time: %.2f", (double)(time)) +"number: "+String.valueOf(SimulationRunner.crNodes.get(i).id) + " -- frequency: " + String.valueOf(SimulationRunner.crNodes.get(i).communication_frequency) + " -- sinrValue: " + sinr.get(i).toString() + " --- " + collision );
            }
        }       
        SimulationRunner.plot.addPoint(1,time, sinr);
    }
    
    
}

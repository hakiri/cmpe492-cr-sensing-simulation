/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
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
            averageSnr.add(i, (averageSnr.get(i)+SimulationRunner.wc.generateSNR(this, i)));
        }
    }
    /**
     * 
     * @return Snr values of each frequencies which are assigned to this node.
     */
    public HashMap<Integer, Double> getSnrValues() {
        return snrValues;
    }
    
    public void logSnrValues(double time){
            // Add to the desired logger
            String log_string;
            log_string = "time: " + String.valueOf(time) + "--- number: "+String.valueOf(number) + "--- position: " +position.toString() + "--- snrValues: " + snrValues.toString();
            SimulationRunner.logger.info(log_string);
            SimulationRunner.logger.addHandler(SimulationRunner.handler);
    }

    public static void initializeAverageSnr(int total_number_of_frequencies){
        averageSnr = new ArrayList<Double>(total_number_of_frequencies);
        
        for(int i=0;i<total_number_of_frequencies;i++){
            averageSnr.add(i,0.0);
        }
    }
    
    public static void logAverageSnr(int number_of_crnodes, double time){
        for(int i=0;i<averageSnr.size();i++){
            averageSnr.add(i,(averageSnr.get(i)/number_of_crnodes));
        }
        String log_string;
        log_string = "time: " + String.valueOf(time) + "--- average snr values: " + averageSnr.toString();
        SimulationRunner.logger.info(log_string);
        SimulationRunner.logger.addHandler(SimulationRunner.handler);
        
        for(int i=0;i<averageSnr.size();i++){
            averageSnr.add(i,0.0);
        }
    }
}

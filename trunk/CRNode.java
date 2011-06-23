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
     * Creates a CRNode with the given frequencies, position and velocity values.
     * @param pos Position of the CRNode
     * @param vel Velocity of the CRNode
     * @param frequencies List of frequencies that are assigned to this node.
     */
    public CRNode(Point2D.Double pos, double vel, ArrayList<Integer> frequencies) {
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
        
    }
 //TODO logSnrValues function should be implemented   
    
}

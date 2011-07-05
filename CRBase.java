/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class CRBase extends Node{
    /**
     * the frequency number that crbase starts from it to deploy frequencies to the crnodes.
     */
    private int frequency_to_be_listen;
    /**
     * the list that keeps the number of listener crnodes for each frequency.  
     */
    private ArrayList<Integer> frequency_list;
    /**
     * The number of frequencies to be listened per crnode
     */
    public static int number_of_freq_per_crnode;
    /**
     * Creates a CRBase at the given position.
     * @param pos Position of the CRBase
     */
    public CRBase(Point2D.Double pos,int id,int number_of_freq_per_crnode){
        this.id = id;
        this.position = new Point2D.Double(pos.x, pos.y);
        this.velocity = 0.0;
        this.frequency_to_be_listen=0;
        this.number_of_freq_per_crnode = number_of_freq_per_crnode;
        this.frequency_list = new ArrayList<Integer>(SimulationRunner.wc.numberOfFreq());
        for(int i=0;i<SimulationRunner.wc.numberOfFreq();i++){
            frequency_list.add(0);
        }
    }
    
    public ArrayList<Integer> deploy_freq(){
        ArrayList<Integer> freq = new ArrayList<Integer>(number_of_freq_per_crnode);
        for(int i=0;i<number_of_freq_per_crnode;i++){
            freq.add(frequency_to_be_listen);
            frequency_to_be_listen=(frequency_to_be_listen+1) % SimulationRunner.wc.numberOfFreq();
        }
        return freq;
    }
    
    public void assignFrequencies(){
        for(int i=0;i<SimulationRunner.crNodes.size();i++){
            SimulationRunner.crNodes.get(i).setFrequencyList(deploy_freq()); //assigns new freq list for crnode
            //for(int j=0;j<CRBase.number_of_freq_per_crnode;j++){ //updates the frequency_list
               // frequency_list.set(frequencies.get(j), (frequency_list.get(frequencies.get(j)) + 1));
            //}//TODO frequency list tam dogru hesaplanmiyor. duzelt.
        }
    }

    public ArrayList<Integer> getFrequency_list() {
        return frequency_list;
    }
    
     
    
}

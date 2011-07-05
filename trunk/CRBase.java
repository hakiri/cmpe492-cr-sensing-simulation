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
    private static int frequency_to_be_listen;
    /**
     * the list that keeps the number of listener crnodes for each frequency.  
     */
    private static ArrayList<Integer> frequency_list;
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
        for(int i=0;i<frequency_list.size();i++){
            frequency_list.set(i, 0);
        }
    }
    
    public static ArrayList<Integer> deploy_freq(){
        ArrayList<Integer> freq = new ArrayList<Integer>(number_of_freq_per_crnode);
        for(int i=0;i<number_of_freq_per_crnode;i++){
            freq.set(i, frequency_to_be_listen);
            if(frequency_to_be_listen < SimulationRunner.wc.numberOfFreq()-1)
                frequency_to_be_listen++;
            else
                frequency_to_be_listen=0;
        }
        return freq;
    }
    
    public static void assignFrequencies(){
        for(int i=0;i<SimulationRunner.crNodes.size();i++){
            ArrayList<Integer> frequencies = new ArrayList<Integer>(CRBase.number_of_freq_per_crnode);
            frequencies = CRBase.deploy_freq();
            SimulationRunner.crNodes.get(i).setFrequencyList(frequencies); //assigns new freq list for crnode
            for(int j=0;j<CRBase.number_of_freq_per_crnode;j++){ //updates the frequency_list
                frequency_list.set(frequencies.get(j), (frequency_list.get(frequencies.get(j)) + 1));
            }
        }
    }

    public static ArrayList<Integer> getFrequency_list() {
        return frequency_list;
    }
    
     
    
}

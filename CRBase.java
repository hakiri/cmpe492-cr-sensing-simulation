/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import cern.jet.random.Uniform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


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
    private static int number_of_freq_per_crnode;
    
    private ArrayList<Double> current_averageSnr = null;  
    
    private ArrayList<Double> last_averageSnr = null;   
    
    private ArrayList<FreqSNR> free_frequencies = null;
    
    private static Uniform uniform ;
    
    /**
     * Creates a CRBase at the given position.
     * @param pos Position of the Base station
     * @param id Id of the CRBase
     * @param number_of_freq_per_crnode The number of the frequencies that a CRNode is going to listen in a frame.
     */
    public CRBase(Point2D.Double pos,int id,int number_of_freq_per_crnode){
        this.id = id;
        this.position = new Point2D.Double(pos.x, pos.y);
        this.velocity = 0.0;
        this.frequency_to_be_listen=0;
        this.number_of_freq_per_crnode = number_of_freq_per_crnode;
        this.uniform = new Uniform(SimulationRunner.randEngine);
    }
    
    private class FreqSNR implements Comparator<FreqSNR> {
        Integer freq;
        Double SNR;

        public FreqSNR(Integer freq, Double SNR) {
            this.freq = freq;
            this.SNR = SNR;
        }

        public FreqSNR() {
        }

        public int compare(FreqSNR o1, FreqSNR o2) {
            double diff=o1.SNR-o2.SNR;
            if(diff<0)
                return -1;
            else if(diff==0)
                return 0;
            return 1;
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
	frequency_list = new ArrayList<Integer>();
        for(int i=0;i<SimulationRunner.wc.numberOfFreq();i++){
            frequency_list.add(0);
        }
        for(int i=0;i<SimulationRunner.crNodes.size();i++){
            ArrayList<Integer> frequencies = deploy_freq();
            SimulationRunner.crNodes.get(i).setFrequencyList(frequencies); //assigns new freq list for crnode
            for(int j=0;j<CRBase.number_of_freq_per_crnode;j++){ //updates the frequency_list
                frequency_list.set(frequencies.get(j), (frequency_list.get(frequencies.get(j)) + 1));
            }//TODO frequency list tam dogru hesaplanmiyor. duzelt.
        }
    }
    
  
    
    public void communicationScheduleAdvertiser(){
        double max_dist = 0.0;
        double temp,snr_from_base,threshold;
        free_frequencies = new ArrayList<FreqSNR>();
        for(int i=0;i<SimulationRunner.crNodes.size();i++){ //finding the max distance btw crbase and crnodes
            temp = SimulationRunner.crNodes.get(i).position.distance(this.position);
            if(max_dist < temp)
                max_dist = temp;
            
        }
        snr_from_base = SimulationRunner.wc.maxSNR/Math.exp(0.12*max_dist);
        
        for(int i=0;i<current_averageSnr.size();i++){//collision olmayan freqleri bulup onlari fre_freq'e ekliyor.
            threshold = WirelessChannel.magTodb(WirelessChannel.dbToMag(SimulationRunner.wc.generateSNR(this, i) - SimulationRunner.wc.sinrThreshold)-1);
            if(threshold < 0)
                threshold = 0;
            if(current_averageSnr.get(i) < threshold)
               free_frequencies.add(new FreqSNR(i, current_averageSnr.get(i)));
        }
        Collections.sort(free_frequencies, new FreqSNR()); //descending sorting of snr values 
        
        ArrayList<Integer> crnode_ids = new ArrayList<Integer>();
        for(int j=0;j<SimulationRunner.crNodes.size();j++){
            crnode_ids.add(j);
        }
        //distributing free frequencies to the crnodes
        for(int i=0;i<free_frequencies.size();i++){
            if(crnode_ids.isEmpty())
                break;
            int node_id=uniform.nextIntFromTo(0, crnode_ids.size()-1);
            SimulationRunner.crNodes.get(node_id).setCommunication_frequency(free_frequencies.get(i).freq);
            crnode_ids.remove(node_id);
        }
        
    }
    
    
    public void setLast_averageSnr(ArrayList<Double> current_averageSnr) {
        this.current_averageSnr = new ArrayList<Double>();
        this.last_averageSnr = new ArrayList<Double>();
        this.last_averageSnr.addAll(this.current_averageSnr);
        this.current_averageSnr.addAll(current_averageSnr);
    }
    
      /**
     * Returns the frequency_list
     * @return frequency_list
     */
    public ArrayList<Integer> getFrequency_list() {
        return frequency_list;
    }
    
}

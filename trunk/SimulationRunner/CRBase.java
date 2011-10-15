package SimulationRunner;

import cern.jet.random.Uniform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

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
    /**
     * Current average snr values for all of the listened frequencies.
     */
    private ArrayList<Double> current_averageSnr = null;  
    /**
     * Average snr values of the previous reading.
     */
    private ArrayList<Double> last_averageSnr = null;   
    /**
     * List of frequencies which are available to talk.
     */
    private ArrayList<FreqSNR> free_frequencies = null;
    /**
     * Uniform distribution
     */
    private static Uniform uniform ;
    
    private ArrayList<ArrayList<Integer>> registeredZones;
    /**
     * Cumulative total of crnodes in zones.
     */
    private ArrayList<Integer> nodesInZone;
    
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
        CRBase.number_of_freq_per_crnode = number_of_freq_per_crnode;
        CRBase.uniform = new Uniform(SimulationRunner.randEngine);
        this.current_averageSnr = new ArrayList<Double>();
        this.registeredZones = new ArrayList<ArrayList<Integer>>();
    }
    
    /**
     * TODO add javadoc 
     */
    private class FreqSNR implements Comparable<FreqSNR> {
        Integer freq;
        Double SNR;

        public FreqSNR(Integer freq, Double SNR) {
            this.freq = freq;
            this.SNR = SNR;
        }

	@Override
	public int compareTo(FreqSNR o) {
            double diff=this.SNR-o.SNR;
            if(diff<0)
                return -1;
            else if(diff==0)
                return 0;
            return 1;
	}
		
        @Override
        public String toString()
        {
                return "["+String.valueOf(freq)+" "+String.valueOf(SNR)+"]";
        }
    }
    
    /**
     * Takes number_of_freq_per_crnode frequencies from frequency list by
     * paying attention to the order of the frequencies.
     * @return number_of_freq_per_crnode frequencies
     */
    public ArrayList<Integer> deploy_freq(){
        ArrayList<Integer> freq = new ArrayList<Integer>(number_of_freq_per_crnode);
        for(int i=0;i<number_of_freq_per_crnode;i++){
            freq.add(frequency_to_be_listen);
            frequency_to_be_listen=(frequency_to_be_listen+1) % SimulationRunner.wc.numberOfFreq();
        }
        return freq;
    }
    
    /**
     * Assigns frequencies to the crnodes to be listened in the sensing slots and also
     * updates frequency list.
     */
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
            }
        }
    }
    
    /**
     * First, finds the threshold value for the collision purposes.
     * Second, finds available(free) frequencies by using threshold value.
     * Third, deploys these free frequencies to the crnodes to communicate at the next frame.
     */
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
        //calculates threshold value
        threshold = WirelessChannel.magTodb(WirelessChannel.dbToMag(snr_from_base - SimulationRunner.wc.sinrThreshold)-1);
        if(threshold < 0)
            threshold = 0;
        //checks averagesnr values of the frequencies and adds frequencies to the free_frequencies list
        //if there was no collision in the previous measurement 
        for(int i=0;i<last_averageSnr.size();i++){//finds collision free frequencies and adds them to fre_freq
            if(last_averageSnr.get(i) <= threshold)
               free_frequencies.add(new FreqSNR(i, last_averageSnr.get(i)));
        }
        Collections.sort(free_frequencies);	 //descending sorting of snr values 
        ArrayList<Integer> crnode_ids = new ArrayList<Integer>();
        for(int j=0;j<SimulationRunner.crNodes.size();j++){
            crnode_ids.add(j);
        }
        //distributing free frequencies to the crnodes
        for(int i=0;i<free_frequencies.size();i++){
            if(crnode_ids.isEmpty())
                break;
            int node_id=uniform.nextIntFromTo(0, crnode_ids.size()-1);
            SimulationRunner.crNodes.get(crnode_ids.get(node_id)).setCommunication_frequency(free_frequencies.get(i).freq);
            crnode_ids.remove(node_id);
        }
    }
    
    /**
     * Updates average_snr values. Assigns the previous current_averagesnr
     * value to the last_averagesnr.
     * @param current_averageSnr Most up-to-date snr value.
     */
    public void setLast_averageSnr(ArrayList<Double> current_averageSnr) {
        this.last_averageSnr = new ArrayList<Double>();
        this.last_averageSnr.addAll(this.current_averageSnr);
        this.current_averageSnr = new ArrayList<Double>();
        this.current_averageSnr.addAll(current_averageSnr);
    }
    
    /**
     * Returns the frequency_list
     * @return frequency_list
     */
    public ArrayList<Integer> getFrequency_list() {
        return frequency_list;
    }
    
	/**
	 * Takes parameters of a zone and registers this zone into registeredZones
	 * @param sector Sector of the zone
	 * @param alpha Alpha number of the zone
	 * @param d Distance section of the zone
	 * @param crnodes Total number of crnodes in that zone
	 */
    public void registerZone(int sector, int alpha, int d, int crnodes){
        ArrayList<Integer> zone = new ArrayList<Integer>();
        zone.add(sector);
        zone.add(alpha);
        zone.add(d);
        registeredZones.add(zone);
        if(registeredZones.size() > 0)
            nodesInZone.add(crnodes + nodesInZone.get(nodesInZone.size() - 1));
        else
            nodesInZone.add(crnodes);
    }
    
	/**
	 * Takes id of crnode then finds that crnode's zone, after this, calls deployNodeinZone function
	 * with the corresponding zone parameters.
	 * @param id Id of CRNode
	 * @return Point of the CRNode
	 */
    public Point2D.Double deployNodeinZone(int id){
        for(int i=0;i<nodesInZone.size();i++){
            if(id <= nodesInZone.get(i))
				return Cell.deployNodeinZone(registeredZones.get(i).get(0), registeredZones.get(i).get(1), registeredZones.get(i).get(2));
        }
		return new Point2D.Double(0.0,0.0);
    }
}

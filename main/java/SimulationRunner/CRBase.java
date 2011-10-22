package SimulationRunner;

import Animation.DrawCell;
import DES.Scheduler;
import cern.jet.random.Uniform;
import java.awt.Color;
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
    private ArrayList<ArrayList<Integer>> frequency_list;
    /**
     * The number of frequencies to be listened per crnode
     */
    private static int number_of_freq_per_crnode;
    /**
     * Current average snr values for all of the listened frequencies.
     */
    private ArrayList<ArrayList<Double>> current_averageSnr = null;  
    /**
     * Average snr values of the previous reading.
     */
    private ArrayList<ArrayList<Double>> last_averageSnr = null;   
    /**
     * List of frequencies which are available to talk.
     */
    private ArrayList<ArrayList<FreqSNR>> free_frequencies = null;
    /**
     * Uniform distribution
     */
    private static Uniform uniform ;
    
    public ArrayList<ArrayList<Integer>> registeredZones;
	public static final int SECTOR = 0;
	public static final int ALPHA = 1;
	public static final int D = 2;
	public static final int CRNODES = 3;
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
        this.current_averageSnr = new ArrayList<ArrayList<Double>>();
        this.registeredZones = new ArrayList<ArrayList<Integer>>();
		this.nodesInZone = new ArrayList<Integer>();
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
	 * @param startFromFirst If true the deployed frequency values will start from the first frequency
     * @return number_of_freq_per_crnode frequencies
     */
    public ArrayList<Integer> deploy_freq(boolean startFromFirst){
        ArrayList<Integer> freq = new ArrayList<Integer>(number_of_freq_per_crnode);
		if(startFromFirst)
			frequency_to_be_listen = 0;
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
        frequency_list = new ArrayList<ArrayList<Integer>>();
		for(int j=0;j<registeredZones.size();j++){
			frequency_list.add(new ArrayList<Integer>());
			for(int i=0;i<SimulationRunner.wc.numberOfFreq();i++){
				frequency_list.get(j).add(0);
			}
		
			int iStart, iEnd;
			iStart = j==0 ? 0:nodesInZone.get(j-1);
			iEnd = j==0 ? nodesInZone.get(0):nodesInZone.get(j);
			
			for(int i=iStart;i<iEnd;i++){
				ArrayList<Integer> frequencies = deploy_freq(i==iStart);
				SimulationRunner.crNodes.get(i).setFrequencyList(frequencies); //assigns new freq list for crnode
				for(int k=0;k<CRBase.number_of_freq_per_crnode;k++){ //updates the frequency_list
					frequency_list.get(j).set(frequencies.get(k), (frequency_list.get(j).get(frequencies.get(k)) + 1));
				}
			}
		}
    }
    
    /**
     * First, finds the threshold value for the collision purposes.
     * Second, finds available(free) frequencies by using threshold value.
     * Third, deploys these free frequencies to the crnodes to communicate at the next frame.
     */
    public void communicationScheduleAdvertiser(){
		ArrayList<Integer> collidedInZone = new ArrayList<Integer>();
		int totalNumberOfCollided = 0;
		//this loop finds the number of collided crnodes for each zone(and takes their comm_freq),
		//also finds free frequencies for each zone.
		for(int i=0;i<registeredZones.size();i++){
			double max_dist = 0.0;
			double temp,snr_from_base,threshold;
			
			int iStart, iEnd;
			iStart = i==0 ? 0:nodesInZone.get(i-1);
			iEnd = i==0 ? nodesInZone.get(0):nodesInZone.get(i);
			
			collidedInZone.add(0);
			for(int j=iStart;j<iEnd;j++){
				temp = SimulationRunner.crNodes.get(i).position.distance(this.position);
				if(max_dist < temp)
					max_dist = temp;
				if(SimulationRunner.crNodes.get(j).getIsCollided()){
					SimulationRunner.crNodes.get(j).releaseCommunication_frequency(); 
					collidedInZone.set(i, collidedInZone.get(i) + 1);
					totalNumberOfCollided ++;
				}
			}
			
			snr_from_base = SimulationRunner.wc.maxSNR/Math.exp(0.12*max_dist);
			//calculates threshold value
			threshold = WirelessChannel.magTodb(WirelessChannel.dbToMag(snr_from_base - SimulationRunner.wc.sinrThreshold)-1);
			if(threshold < 0)
				threshold = 0;
			//checks averagesnr values of the frequencies and adds frequencies to the free_frequencies list
			//if there was no collision in the previous measurement 
			free_frequencies.add(new ArrayList<FreqSNR>());
			for(int j=0;j<last_averageSnr.get(i).size();j++){//finds collision-free frequencies and adds them to fre_freq
				if(last_averageSnr.get(i).get(j) <= threshold)
					if(!SimulationRunner.wc.isOccupied(j, WirelessChannel.CR))
						free_frequencies.get(i).add(new FreqSNR(j, last_averageSnr.get(i).get(j)));
			}
			Collections.sort(free_frequencies.get(i));	 //ascending sorting of snr values
		}
		
		//this for loop assigns a frequency at each loop for the collided crnodes
		for(int i=0;i<totalNumberOfCollided;i++){
			int lowest=0;
			
			for(int j=0;j<free_frequencies.size();j++){
				if(free_frequencies.get(lowest).isEmpty()){
					lowest++;
					continue;
				}
				if(free_frequencies.get(j).isEmpty())
					continue;
				if(free_frequencies.get(lowest).get(0).SNR > free_frequencies.get(j).get(0).SNR)
					lowest = j;
			}
			if(lowest == free_frequencies.size())
				break;
			int iStart, iEnd;
			iStart = lowest==0 ? 0:nodesInZone.get(lowest-1);
			iEnd = lowest==0 ? nodesInZone.get(0):nodesInZone.get(lowest);
			for(int j=iStart;j<iEnd;j++){
				if(SimulationRunner.crNodes.get(j).getReadytoComm()){
					SimulationRunner.crNodes.get(j).setCommunication_frequency(free_frequencies.get(lowest).get(0).freq);
					SimulationRunner.crNodes.get(j).setReadytoComm(false);
					SimulationRunner.crNodes.get(j).setNumberOfForcedHandoff(SimulationRunner.crNodes.get(j).getNumberOfForcedHandoff() + 1);
					
					collidedInZone.set(lowest, collidedInZone.get(lowest) - 1);
					//updates free_frequencies
					for(int k=0;k<free_frequencies.size();k++){
						if(!(k==lowest || free_frequencies.get(k).isEmpty())){
							for(int l=0;l<free_frequencies.get(k).size();l++){
								if(free_frequencies.get(k).get(l).freq == free_frequencies.get(lowest).get(0).freq){
									free_frequencies.get(k).remove(l);
									break;
								}
							}
						}
					}
					free_frequencies.get(lowest).remove(0);
					SimulationRunner.crNodes.get(j).setCommOrNot(true);
					if(collidedInZone.get(lowest)==0)
						free_frequencies.get(lowest).clear();
					if(SimulationRunner.animationOffButton.isSelected())
						SimulationRunner.crDesScheduler.sendEndCommEvent(j);
					else{
						SimulationRunner.crSensor.setCommunationDuration(j);
						DrawCell.paintCrNode(SimulationRunner.crNodes.get(j), Color.GREEN);
					}
					break;
				}
			}
		} //end of assigment of the free frequencies to the collided crnodes
		
		//this loop is for collided crnodes which cannot find a new comm_freq to talk.
		//updates number of drops for crnodes, updates commOrNot to false,
		for(int i=0;i<registeredZones.size();i++){
			int iStart, iEnd;
			iStart = i==0 ? 0:nodesInZone.get(i-1);
			iEnd = i==0 ? nodesInZone.get(0):nodesInZone.get(i);
			
			if(collidedInZone.get(i) > 0){
				for(int j=iStart;j<iEnd;j++){
					if(SimulationRunner.crNodes.get(j).getIsCollided()){
						if(SimulationRunner.crNodes.get(j).getCommunication_frequency() == -1){
							SimulationRunner.crNodes.get(j).setNumberOfDrops(SimulationRunner.crNodes.get(j).getNumberOfDrops() + 1);
							SimulationRunner.crNodes.get(j).setCommOrNot(false);
							if(SimulationRunner.animationOnButton.isSelected()){
								SimulationRunner.crSensor.setInactiveDuration(j, true);
							}
							else{
								SimulationRunner.crDesScheduler.sendStartCommEvent(j);
								Scheduler.deregister(SimulationRunner.crNodes.get(j).endEventHandle);
							}
						}
					}
				}
			}
		}
		
		ArrayList<Integer> readyToCommInZone = new ArrayList<Integer>();
		int totalNumberOfReadytoComm = 0;
		if(last_averageSnr.isEmpty())
			return;
		for(int k=0;k<registeredZones.size();k++){
			int iStart, iEnd;
			iStart = k==0 ? 0:nodesInZone.get(k-1);
			iEnd = k==0 ? nodesInZone.get(0):nodesInZone.get(k);
			
			readyToCommInZone.add(0);
			for(int i=iStart;i<iEnd;i++){ //finding the max distance btw crbase and crnodes
				if(SimulationRunner.crNodes.get(i).getReadytoComm()){
					readyToCommInZone.set(k, readyToCommInZone.get(k) + 1);
					totalNumberOfReadytoComm++;
				}
			}
		}
		
		//this for loop assigns a frequency at each loop
		for(int i=0;i<totalNumberOfReadytoComm;i++){
			int lowest=0;
			
			for(int j=0;j<free_frequencies.size();j++){
				if(free_frequencies.get(lowest).isEmpty()){
					lowest++;
					continue;
				}
				if(free_frequencies.get(j).isEmpty())
					continue;
				if(free_frequencies.get(lowest).get(0).SNR > free_frequencies.get(j).get(0).SNR)
					lowest = j;
			}
			if(lowest == free_frequencies.size())
				break;
			int iStart, iEnd;
			iStart = lowest==0 ? 0:nodesInZone.get(lowest-1);
			iEnd = lowest==0 ? nodesInZone.get(0):nodesInZone.get(lowest);
			for(int j=iStart;j<iEnd;j++){
				if(SimulationRunner.crNodes.get(j).getReadytoComm()){
					SimulationRunner.crNodes.get(j).setCommunication_frequency(free_frequencies.get(lowest).get(0).freq);
					SimulationRunner.crNodes.get(j).setReadytoComm(false);
					
					readyToCommInZone.set(lowest, readyToCommInZone.get(lowest)-1);
					for(int k=0;k<free_frequencies.size();k++){
						if(!(k==lowest || free_frequencies.get(k).isEmpty())){
							for(int l=0;l<free_frequencies.get(k).size();l++){
								if(free_frequencies.get(k).get(l).freq == free_frequencies.get(lowest).get(0).freq){
									free_frequencies.get(k).remove(l);
									break;
								}
							}
						}
					}
					free_frequencies.get(lowest).remove(0);
					SimulationRunner.crNodes.get(j).setCommOrNot(true);
					if(readyToCommInZone.get(lowest)==0)
						free_frequencies.get(lowest).clear();
					if(SimulationRunner.animationOffButton.isSelected())
						SimulationRunner.crDesScheduler.sendEndCommEvent(j);
					else{
						SimulationRunner.crSensor.setCommunationDuration(j);
						DrawCell.paintCrNode(SimulationRunner.crNodes.get(j), Color.GREEN);
					}
					break;
				}
			}
		}
		
		for(int i=0;i<SimulationRunner.crNodes.size();i++){			//Send communication start event for the blocked users
			if(SimulationRunner.crNodes.get(i).getReadytoComm()){
				SimulationRunner.crNodes.get(i).setReadytoComm(false);
				if(SimulationRunner.animationOffButton.isSelected())
					SimulationRunner.crDesScheduler.sendStartCommEvent(i);
				else{
					SimulationRunner.crSensor.setInactiveDuration(i,false);
					DrawCell.paintCrNode(SimulationRunner.crNodes.get(i), Color.GRAY);
				}
				SimulationRunner.crNodes.get(i).numberOfBlocks++;
			}
		}
		
		
		for(int i=0;i<SimulationRunner.crNodes.size();i++){
			SimulationRunner.crNodes.get(i).setIsCollided(false);
		}
    }
    
    /**
     * Updates average_snr values. Assigns the previous current_averagesnr
     * value to the last_averagesnr.
     * @param current_averageSnr Most up-to-date snr value.
     */
    public void setLast_averageSnr(ArrayList<ArrayList<Double>> current_averageSnr) {
        this.last_averageSnr = new ArrayList<ArrayList<Double>>();
        this.last_averageSnr.addAll(this.current_averageSnr);
        this.current_averageSnr = new ArrayList<ArrayList<Double>>();
        this.current_averageSnr.addAll(current_averageSnr);
    }
    
    /**
     * Returns the frequency_list
     * @return frequency_list
     */
    public ArrayList<ArrayList<Integer>> getFrequency_list() {
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
        if(nodesInZone.size() > 0)
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
            if(id < nodesInZone.get(i))
				return Cell.deployNodeinZone(registeredZones.get(i).get(0), registeredZones.get(i).get(1), registeredZones.get(i).get(2));
        }
		return new Point2D.Double(0.0,0.0);
    }
	
	public int findZone(int id)
	{
		for(int i=0;i<nodesInZone.size();i++)
			if(id<nodesInZone.get(i))
				return i;
		return 0;
	}
	
	public double farthestZoneDistance()
	{
		int d = 0;
		for(int i = 0 ; i<registeredZones.size() ; i++){
			if(registeredZones.get(i).get(D) > d)
				d = registeredZones.get(i).get(D);
		}
		return Cell.set_of_d.get(d);
	}
}

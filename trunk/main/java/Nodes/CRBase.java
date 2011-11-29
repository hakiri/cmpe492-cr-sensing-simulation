package Nodes;

import Animation.DrawCell;
import CommunicationEnvironment.Cell;
import CommunicationEnvironment.WirelessChannel;
import DES.Scheduler;
import SimulationRunner.SimulationRunner;
import cern.jet.random.Uniform;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This class handles basic operation of a CR base station such as sensing schedule
 * advertisement, communication schedule advertisement, handoff of CR users, etc.
 * It also keeps information about which CR node belongs which zone
 */
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
     * Current available or not decision for all of the listened frequencies.
     */
    private ArrayList<ArrayList<Integer>> currentSensingDecisions = null;
    /**
     * Available or not decision of the previous reading.
     */
    private ArrayList<ArrayList<Integer>> lastSensingDecisions = null;
    /**
     * List of frequencies which are available to talk.
     */
    private ArrayList<ArrayList<Integer>> free_frequencies = null;
    /**
     * Uniform distribution
     */
    private static Uniform uniform ;
    
	/**
	 * Keeps sector number, alpha number, d number, and number of CR nodes of a zone
	 */
	public ArrayList<ArrayList<Integer>> registeredZones;
	/**
	 * Index of sector number in zone array
	 */
	public static final int SECTOR = 0;
	/**
	 * Index of alpha number in zone array
	 */
	public static final int ALPHA = 1;
	/**
	 * Index of d number in zone array
	 */
	public static final int D = 2;
	/**
	 * Index of number of CR nodes in zone array
	 */
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
		this.currentSensingDecisions = new ArrayList<ArrayList<Integer>>();
        this.registeredZones = new ArrayList<ArrayList<Integer>>();
		this.nodesInZone = new ArrayList<Integer>();
    }
    
    /**
     * An inner class to keep frequency ID and its corresponding average SNR value.
	 * This class objects are comparable with respect to their SNR values.
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
		if(lastSensingDecisions.isEmpty())
			return;
		findFreeFrequencies();
		
		handoffCollidedUsersInZones();
		
		ArrayList<Integer> readyToCommInZone = new ArrayList<Integer>();
		int totalNumberOfReadytoComm = 0;
		for(int zoneNumber=0;zoneNumber<registeredZones.size();zoneNumber++){
			int iStart, iEnd;
			iStart = zoneNumber==0 ? 0:nodesInZone.get(zoneNumber-1);
			iEnd = zoneNumber==0 ? nodesInZone.get(0):nodesInZone.get(zoneNumber);
			
			readyToCommInZone.add(0);
			for(int crInZone=iStart;crInZone<iEnd;crInZone++){ //finding the max distance btw crbase and crnodes
				if(SimulationRunner.crNodes.get(crInZone).getReadytoComm()){
					readyToCommInZone.set(zoneNumber, readyToCommInZone.get(zoneNumber) + 1);
					totalNumberOfReadytoComm++;
				}
			}
		}
		
		//this for loop assigns a frequency at each loop
		while(totalNumberOfReadytoComm > 0){
			int zone=getAZone(readyToCommInZone);
			if(zone == -1)
				break;
			int iStart, iEnd;
			iStart = zone==0 ? 0:nodesInZone.get(zone-1);
			iEnd = zone==0 ? nodesInZone.get(0):nodesInZone.get(zone);
			for(int crInZone=iStart;crInZone<iEnd;crInZone++){
				if(SimulationRunner.crNodes.get(crInZone).getReadytoComm()){
					int randomFreq = getARandomIndex(free_frequencies.get(zone));
					SimulationRunner.crNodes.get(crInZone).setCommunication_frequency(free_frequencies.get(zone).get(randomFreq));
					SimulationRunner.crNodes.get(crInZone).setReadytoComm(false);
					
					readyToCommInZone.set(zone, readyToCommInZone.get(zone)-1);
					for(int k=0;k<free_frequencies.size();k++){
						if(!(k==zone || free_frequencies.get(k).isEmpty())){
							for(int l=0;l<free_frequencies.get(k).size();l++){
								if(free_frequencies.get(k).get(l) == free_frequencies.get(zone).get(randomFreq)){
									free_frequencies.get(k).remove(l);
									break;
								}
							}
						}
					}
					free_frequencies.get(zone).remove(randomFreq);
					if(readyToCommInZone.get(zone)==0)
						free_frequencies.get(zone).clear();
					if(SimulationRunner.animationOffButton.isSelected())
						SimulationRunner.crDesScheduler.sendEndCommEvent(crInZone);
					else{
						SimulationRunner.crSensor.setCommunationDuration(crInZone);
						DrawCell.paintCrNode(SimulationRunner.crNodes.get(crInZone), Color.GREEN);
					}
					totalNumberOfReadytoComm--;
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
				SimulationRunner.crNodes.get(i).setNumberOfBlocks(SimulationRunner.crNodes.get(i).getNumberOfBlocks()+1);
			}
		}
		
		
		for(int i=0;i<SimulationRunner.crNodes.size();i++){
			SimulationRunner.crNodes.get(i).setIsCollided(false);
		}
    }
    
	private void findFreeFrequencies()
	{
		free_frequencies = new ArrayList<ArrayList<Integer>>();
		for(int zoneNumber = 0 ; zoneNumber < registeredZones.size() ; zoneNumber++){
			//checks averagesnr values of the frequencies and adds frequencies to the free_frequencies list
			//if there was no collision in the previous measurement 
			free_frequencies.add(new ArrayList<Integer>());
			for(int freqInZone=0;freqInZone<lastSensingDecisions.get(zoneNumber).size();freqInZone++){//finds collision-free frequencies and adds them to fre_freq
				if(lastSensingDecisions.get(zoneNumber).get(freqInZone) == 0)
					if(!SimulationRunner.wc.isOccupied(freqInZone, WirelessChannel.CR))
						free_frequencies.get(zoneNumber).add(freqInZone);
			}
		}
	}
	
	private int getAZone(ArrayList<Integer> numberOfCollidedOrReadyToCommCRNodes)
	{
		ArrayList<Integer> availableZones = new ArrayList<Integer>();
		for(int i=0;i<free_frequencies.size();i++)
			if(!free_frequencies.get(i).isEmpty() && numberOfCollidedOrReadyToCommCRNodes.get(i) != 0)
				availableZones.add(i);
		if(availableZones.isEmpty())
			return -1;
		return availableZones.get(uniform.nextIntFromTo(0, availableZones.size() - 1));
	}
	
	private int getARandomIndex(ArrayList<Integer> param)
	{
		return uniform.nextIntFromTo(0, param.size() - 1);
	}
	
	private void handoffCollidedUsersInZones()
	{
		ArrayList<Integer> collidedInZone = new ArrayList<Integer>();
		int totalNumberOfCollided = 0;
		//these loops finds the number of collided crnodes for each zone(and releases their comm_freq)
		for(int i=0;i<registeredZones.size();i++)
			collidedInZone.add(0);
		for(int i=0;i<SimulationRunner.crNodes.size();i++){
			if(SimulationRunner.crNodes.get(i).getIsCollided()){
				SimulationRunner.crNodes.get(i).releaseCommunication_frequency();
				collidedInZone.set(findZone(i), collidedInZone.get(findZone(i)) + 1);
				totalNumberOfCollided++;
			}
		}
		
		//this for loop assigns a frequency at each loop for the collided crnodes
		while(totalNumberOfCollided > 0){
			int zone = getAZone(collidedInZone);
			if(zone == -1)
				break;
			int iStart, iEnd;
			iStart = zone==0 ? 0:nodesInZone.get(zone-1);
			iEnd = zone==0 ? nodesInZone.get(0):nodesInZone.get(zone);
			for(int crInZone=iStart;crInZone<iEnd;crInZone++){
				if(SimulationRunner.crNodes.get(crInZone).getIsCollided()){
					int randomFreq = getARandomIndex(free_frequencies.get(zone));
					SimulationRunner.crNodes.get(crInZone).setCommunication_frequency(free_frequencies.get(zone).get(randomFreq));
					SimulationRunner.crNodes.get(crInZone).setNumberOfForcedHandoff(SimulationRunner.crNodes.get(crInZone).getNumberOfForcedHandoff() + 1);
					
					collidedInZone.set(zone, collidedInZone.get(zone) - 1);
					//updates free_frequencies
					for(int k=0;k<free_frequencies.size();k++){
						if(!(k==zone || free_frequencies.get(k).isEmpty())){
							for(int l=0;l<free_frequencies.get(k).size();l++){
								if(free_frequencies.get(k).get(l) == free_frequencies.get(zone).get(randomFreq)){
									free_frequencies.get(k).remove(l);
									break;
								}
							}
						}
					}
					free_frequencies.get(zone).remove(randomFreq);
					totalNumberOfCollided--;
					break;
				}
			}
		} //end of assigment of the free frequencies to the collided crnodes
		
		//this loop is for collided crnodes which cannot find a new comm_freq to talk.
		//updates number of drops for crnodes, updates commOrNot to false,
		for(int zoneNumber=0;zoneNumber<registeredZones.size();zoneNumber++){
			int iStart, iEnd;
			iStart = zoneNumber==0 ? 0:nodesInZone.get(zoneNumber-1);
			iEnd = zoneNumber==0 ? nodesInZone.get(0):nodesInZone.get(zoneNumber);
			
			if(collidedInZone.get(zoneNumber) > 0){
				for(int crInZone=iStart;crInZone<iEnd;crInZone++){
					if(SimulationRunner.crNodes.get(crInZone).getIsCollided()){
						if(SimulationRunner.crNodes.get(crInZone).getCommunication_frequency() == -1){
							SimulationRunner.crNodes.get(crInZone).setNumberOfDrops(SimulationRunner.crNodes.get(crInZone).getNumberOfDrops() + 1);
							
							double msec = Scheduler.instance().getTime();
							int hour = (int)(msec/3600000.0);
							msec -= hour*3600000.0;
							int min = (int)(msec/60000.0);
							msec -= min*60000.0;
							int sec = (int)(msec/1000.0);
							msec-= sec*1000.0;
							
							CRNode.writeLogFile("Time: " + String.format(Locale.US,"%2d:%2d:%2d:%.2f", hour,min,sec,msec) + " -- number: " + crInZone + " is dropped");
							
							if(SimulationRunner.animationOnButton.isSelected()){
								SimulationRunner.crSensor.setInactiveDuration(crInZone, true);
							}
							else{
								SimulationRunner.crDesScheduler.sendStartCommEvent(crInZone);
								Scheduler.deregister(SimulationRunner.crNodes.get(crInZone).endEventHandle);
							}
						}
					}
				}
			}
		}
	}
	
    /**
     * Updates average_snr values. Assigns the previous current_averagesnr
     * value to the last_averagesnr.
     * @param current_averageSnr Most up-to-date snr value.
     */
    public void setLastSensingResults(ArrayList<ArrayList<Integer>> currentDecisions) {
		this.lastSensingDecisions = new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<this.currentSensingDecisions.size();i++){
			this.lastSensingDecisions.add(new ArrayList<Integer>());
			for(int j=0;j<this.currentSensingDecisions.get(i).size();j++){
				this.lastSensingDecisions.get(i).add(this.currentSensingDecisions.get(i).get(j));
			}
		}
		this.currentSensingDecisions = new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<currentDecisions.size();i++){
			this.currentSensingDecisions.add(new ArrayList<Integer>());
			for(int j=0;j<currentDecisions.get(i).size();j++){
				this.currentSensingDecisions.get(i).add(currentDecisions.get(i).get(j));
			}
		}
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
	
	/**
	 * Finds zone ID of a given CR node
	 * @param id	ID of CR node
	 * @return		Zone ID of the given CR node
	 */
	public int findZone(int id)
	{
		for(int i=0;i<nodesInZone.size();i++)
			if(id<nodesInZone.get(i))
				return i;
		return 0;
	}
	
	/**
	 * Finds farthest point of the all registered zones to base station
	 * @return	Distance of the farthest point
	 */
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

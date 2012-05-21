package Nodes;

import Animation.DrawCell;
import CommunicationEnvironment.Cell;
import CommunicationEnvironment.WirelessChannel;
import DES.Scheduler;
import Heuristic.ATLHueristic;
import Heuristic.FAHMain;
import SimulationRunner.SimulationRunner;
import cern.jet.random.Uniform;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * This class handles basic operation of a CR base station such as sensing schedule
 * advertisement, communication schedule advertisement, hand-off of CR users, etc.
 * It also keeps information about which CR node belongs which zone
 */
public class CRBase extends ArrayList<CRNode> implements Node{

// <editor-fold defaultstate="collapsed" desc="Attributes">
	/**
	 * Position of the node
     */
    protected Point2D.Double position = new Point2D.Double(0,0);
    /**
     * Velocity of the node
     */
    protected double velocity = 0;
    /**
     * Id o the Node
     */
    protected int id;
    /**
     * the frequency number that crbase starts from it to deploy frequencies to the crnodes.
     */
    private int frequency_to_be_listen;
    /**
     * the list that keeps the number of listener crnodes for each frequency.  
     */
    private ArrayList<ArrayList<Integer>> frequency_list;
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
     * Cumulative total of crnodes in zones.
     */
    private ArrayList<Double> falseAlarm;
    private ArrayList<Double> missDetection;
    private ArrayList<Double> collisions;
    private ArrayList<Double> drops;
    private ArrayList<Double> blocks;
    private ArrayList<Double> totalNumberOfBitsTransmitted;
    private ArrayList<Double> totalCommunicatedFrames;
    private ArrayList<Double> numberOfCalls;
    private ArrayList<Double> numberOfCallAttempts;
// </editor-fold>
	
    /**
     * Creates a CRBase at the given position.
     * @param pos Position of the Base station
     * @param id Id of the CRBase
     */
    public CRBase(Point2D.Double pos,int id){
		super();
        this.id = id;
        this.position = new Point2D.Double(pos.x, pos.y);
        this.velocity = 0.0;
        this.frequency_to_be_listen=0;
        CRBase.uniform = new Uniform(SimulationRunner.randEngine);
		this.currentSensingDecisions = new ArrayList<>();
        this.falseAlarm = new ArrayList<>();
        this.missDetection = new ArrayList<>();
        this.collisions = new ArrayList<>();
        this.blocks = new ArrayList<>();
        this.drops = new ArrayList<>();
        this.totalNumberOfBitsTransmitted = new ArrayList<>();
        this.totalCommunicatedFrames = new ArrayList<>();
        this.numberOfCalls = new ArrayList<>();
        this.numberOfCallAttempts = new ArrayList<>();
    }

	@Override
	public Point2D.Double getPosition() {
		return position;
	}

	@Override
	public double getVelocity() {
		return velocity;
	}

	@Override
	public void setPosition(Point2D.Double position) {
		this.position = position;
	}

	@Override
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
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
     * Takes numberOfFreq frequencies from frequency list by
     * paying attention to the order of the frequencies.
	 * @param startFromFirst If true the deployed frequency values will start from the first frequency
	 * @param numberOfFreq   Number of frequencies to be sensed
     * @return number_of_freq_per_crnode frequencies
     */
    public ArrayList<Integer> deploy_freq(boolean startFromFirst, int numberOfFreq){
        ArrayList<Integer> freq = new ArrayList<>(numberOfFreq);
		if(startFromFirst)
			frequency_to_be_listen = 0;
        for(int i=0;i<numberOfFreq;i++){
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
        frequency_list = new ArrayList<>();
		
		for(int i=0;i<SimulationRunner.args.getNumberOfZones();i++){
			frequency_list.add(new ArrayList<Integer>());
			int numberOfFreqPerGroup = (int)Math.ceil((double)SimulationRunner.wc.numberOfFreq()/FAHMain.groups.get(i).size());
			for(int j=0;j<SimulationRunner.wc.numberOfFreq();j++){
				frequency_list.get(i).add(0);
			}
		
			
			for(int j=0;j<FAHMain.groups.get(i).size();j++){
				ArrayList<Integer> frequencies = deploy_freq(j==0,numberOfFreqPerGroup);
				for(int k = 0; k < FAHMain.groups.get(i).get(j).size();k++){
					get(FAHMain.groups.get(i).get(j).get(k)).setFrequencyList(frequencies); //assigns new freq list for crnode
				}
				for(int k=0;k<numberOfFreqPerGroup;k++){ //updates the frequency_list
					frequency_list.get(i).set(frequencies.get(k), (frequency_list.get(i).get(frequencies.get(k)) + FAHMain.groups.get(i).get(j).size()));
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
		
		ArrayList<Integer> readyToCommInZone = new ArrayList<>();
		int totalNumberOfReadytoComm = 0;
		for(int zoneNumber=0;zoneNumber<SimulationRunner.args.getNumberOfZones();zoneNumber++){
			
			readyToCommInZone.add(0);
			for(int i=0;i<ATLHueristic.yij.get(zoneNumber).size();i++){
				int crInZone = ATLHueristic.yij.get(zoneNumber).get(i);
				if(get(crInZone).getReadytoComm()){
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
			for(int i=0;i<ATLHueristic.yij.get(zone).size();i++){
				int crInZone = ATLHueristic.yij.get(zone).get(i);
				if(get(crInZone).getReadytoComm()){
					int randomFreq = getARandomIndex(free_frequencies.get(zone));
					get(crInZone).setCommunication_frequency(free_frequencies.get(zone).get(randomFreq));
					get(crInZone).setReadytoComm(false);
					
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
					if(!SimulationRunner.args.isAnimationOn())
						SimulationRunner.crDesScheduler.sendEndCommEvent(crInZone);
					else{
						SimulationRunner.crSensor.setCommunationDuration(crInZone);
						DrawCell.paintCrNode(get(crInZone), Color.GREEN);
					}
					totalNumberOfReadytoComm--;
					break;
				}
			}
		}
		
		for(int i=0;i<size();i++){			//Send communication start event for the blocked users
			if(get(i).getReadytoComm()){
				get(i).setReadytoComm(false);
				if(!SimulationRunner.args.isAnimationOn())
					SimulationRunner.crDesScheduler.sendStartCommEvent(i);
				else{
					SimulationRunner.crSensor.setInactiveDuration(i,false);
					DrawCell.paintCrNode(get(i), Color.GRAY);
				}
				get(i).setNumberOfBlocks(get(i).getNumberOfBlocks()+1);
                incrementBlock(findZone(i));
			}
		}
		
		
		for(int i=0;i<size();i++){
			get(i).setIsCollided(false);
		}
    }
    
	private void findFreeFrequencies()
	{
		free_frequencies = new ArrayList<>();
		for(int zoneNumber = 0 ; zoneNumber < SimulationRunner.args.getNumberOfZones() ; zoneNumber++){
			//checks averagesnr values of the frequencies and adds frequencies to the free_frequencies list
			//if there was no collision in the previous measurement 
			free_frequencies.add(new ArrayList<Integer>());
			for(int freqInZone=0;freqInZone<lastSensingDecisions.get(zoneNumber).size();freqInZone++){//finds collision-free frequencies and adds them to fre_freq
				if(lastSensingDecisions.get(zoneNumber).get(freqInZone) == 0){
					if(!SimulationRunner.wc.isOccupied(freqInZone, WirelessChannel.CR))
						free_frequencies.get(zoneNumber).add(freqInZone);
                }
                else{
                    CRNode n = (CRNode)SimulationRunner.wc.getFreq(freqInZone).get(WirelessChannel.CR);
                    if(n != null){
                        if(findZone(n.getId()) == zoneNumber){
                            n.setIsCollided(true);
                            if(SimulationRunner.args.isAnimationOn()){
                            	SimulationRunner.crSensor.setWarningExpirationFrame(n.getId());
                        	}
                            n.incrementEstimatedNumberOfCollision();
                        }
                    }
                }
			}
		}
	}
	
	private int getAZone(ArrayList<Integer> numberOfCollidedOrReadyToCommCRNodes)
	{
		ArrayList<Integer> availableZones = new ArrayList<>();
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
		ArrayList<Integer> collidedInZone = new ArrayList<>();
		int totalNumberOfCollided = 0;
		//these loops finds the number of collided crnodes for each zone(and releases their comm_freq)
		for(int i=0;i<SimulationRunner.args.getNumberOfZones();i++)
			collidedInZone.add(0);
		for(int i=0;i<size();i++){
			if(get(i).getIsCollided()){
				get(i).releaseCommunication_frequency();
				collidedInZone.set(findZone(i), collidedInZone.get(findZone(i)) + 1);
				totalNumberOfCollided++;
			}
		}
		
		//this for loop assigns a frequency at each loop for the collided crnodes
		while(totalNumberOfCollided > 0){
			int zone = getAZone(collidedInZone);
			if(zone == -1)
				break;
			
			for(int i=0;i<ATLHueristic.yij.get(zone).size();i++){
				int crInZone = ATLHueristic.yij.get(zone).get(i);
				if(get(crInZone).getIsCollided()){
					int randomFreq = getARandomIndex(free_frequencies.get(zone));
					get(crInZone).setCommunication_frequency(free_frequencies.get(zone).get(randomFreq));
					get(crInZone).setNumberOfForcedHandoff(get(crInZone).getNumberOfForcedHandoff() + 1);
					
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
		for(int zoneNumber=0;zoneNumber<SimulationRunner.args.getNumberOfZones();zoneNumber++){
			
			if(collidedInZone.get(zoneNumber) > 0){
				for(int i=0;i<ATLHueristic.yij.get(zoneNumber).size();i++){
					int crInZone = ATLHueristic.yij.get(zoneNumber).get(i);
					if(get(crInZone).getIsCollided()){
						if(get(crInZone).getCommunication_frequency() == -1){
							get(crInZone).setNumberOfDrops(get(crInZone).getNumberOfDrops() + 1);
							incrementDrop(zoneNumber);
							// <editor-fold defaultstate="collapsed" desc="Time Calculations">
//							double msec = Scheduler.instance().getTime();
//							int hour = (int)(msec/3600000.0);
//							msec -= hour*3600000.0;
//							int min = (int)(msec/60000.0);
//							msec -= min*60000.0;
//							int sec = (int)(msec/1000.0);
//							msec-= sec*1000.0;
							
//							CRNode.writeLogFile("Time: " + String.format(Locale.US,"%2d:%2d:%2d:%.2f", hour,min,sec,msec) + " -- number: " + crInZone + " is dropped");
							//</editor-fold>
							if(SimulationRunner.args.isAnimationOn()){
								SimulationRunner.crSensor.setInactiveDuration(crInZone, true);
							}
							else{
								SimulationRunner.crDesScheduler.sendStartCommEvent(crInZone);
								Scheduler.deregister(get(crInZone).endEventHandle);
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
	 * @param currentDecisions	Current channel state decisions for all frequencies
     */
    public void setLastSensingResults(ArrayList<ArrayList<Integer>> currentDecisions) {
		this.lastSensingDecisions = new ArrayList<>();
		for(int i=0;i<this.currentSensingDecisions.size();i++){
			this.lastSensingDecisions.add(new ArrayList<Integer>());
			for(int j=0;j<this.currentSensingDecisions.get(i).size();j++){
				this.lastSensingDecisions.get(i).add(this.currentSensingDecisions.get(i).get(j));
			}
		}
		this.currentSensingDecisions = new ArrayList<>();
		for(int i=0;i<currentDecisions.size();i++){
			this.currentSensingDecisions.add(new ArrayList<Integer>());
			for(int j=0;j<currentDecisions.get(i).size();j++){
				this.currentSensingDecisions.get(i).add(currentDecisions.get(i).get(j));
			}
		}
    }
    
    /**
     * Returns the frequency_list
     * @return Frequency list
     */
    public ArrayList<ArrayList<Integer>> getFrequency_list() {
        return frequency_list;
    }
    
	/**
	 * Takes parameters of multiple zones and registers those zones into registeredZones
	 * @param sector Sector of the zone
	 * @param alpha Alpha number of the zone
	 * @param d Distance section of the zone
	 * @param crnodes Total number of crnodes in that zone
	 */
    public void registerZones(){
        for(int i=0;i<SimulationRunner.args.getNumberOfZones();i++){
			missDetection.add(0.0);
			falseAlarm.add(0.0);
            collisions.add(0.0);
            blocks.add(0.0);
            drops.add(0.0);
            totalNumberOfBitsTransmitted.add(0.0);
            totalCommunicatedFrames.add(0.0);
            numberOfCalls.add(0.0);
            numberOfCallAttempts.add(0.0);
		}
    }
    
	
	/**
	 * Finds zone ID of a given CR node
	 * @param id	ID of CR node
	 * @return		Zone ID of the given CR node
	 */
	public int findZone(int id)
	{
		return get(id).getZoneId();
	}
	
	/**
	 * Finds farthest point of the all registered zones to base station
	 * @return	Distance of the farthest point
	 */
	public double farthestZoneDistance()
	{
		return Cell.getRadius();
	}
    
    /**
     * Returns the farthest distance from a CR node to base station for a given zone.
     * @param zoneId Id of zone
     * @return Farthest distance between a CR node and the base station
     */
    public double  farthestDistanceInZone(int zoneId)
    {
        double distance=0.0,temp_dist;
        
        for(int i=0;i<ATLHueristic.yij.get(zoneId).size();i++){
            temp_dist = get(ATLHueristic.yij.get(zoneId).get(i)).getPosition().distance(position);
            if(temp_dist>distance)
                distance = temp_dist;
        }
        return distance;
    }
    
    /**
     * Returns the total number of false alarms in a zone.
     * @param zoneId Id number of the zone
     * @return total number of false alarms in a zone
     */
    public double  getFalseAlarm(int zoneId) {
        return falseAlarm.get(zoneId);
    }
    
    /**
     * Returns the total number of miss detections in a zone.
     * @param zoneId Id number of the zone
     * @return total number of miss detections in a zone
     */
    public double getMissDetection(int zoneId) {
        return missDetection.get(zoneId);
    }
    
    /**
     * Returns the total number of collisions in a zone.
     * @param zoneId Id number of the zone
     * @return total number of collisions in a zone
     */
    public double getCollisions(int zoneId) {
        return collisions.get(zoneId);
    }
    
    /**
     * Returns the total number of drops in a zone.
     * @param zoneId Id number of the zone
     * @return total number of drops in a zone
     */
    public double getDrops(int zoneId) {
        return drops.get(zoneId);
    }
    
    /**
     * Returns the total number of blocks in a zone.
     * @param zoneId Id number of the zone
     * @return total number of blocks in a zone
     */
    public double getBlocks(int zoneId) {
        return blocks.get(zoneId);
    }
    
    /**
     * Returns the total number of bits transmitted from cr users in a zone.
     * @param zoneId Id number of the zone
     * @return total number of bits transmitted from cr users in a zone
     */
    public double getTotalBitsTransmitted(int zoneId) {
        return totalNumberOfBitsTransmitted.get(zoneId);
    }
    
    /**
     * Returns the total number of communicated frames of cr users in a zone.
     * @param zoneId Id number of the zone
     * @return total number of communicated frames of cr users in a zone
     */
    public double getTotalCommunicatedFrames(int zoneId) {
        return totalCommunicatedFrames.get(zoneId);
    }
    
    /**
     * Returns the total number of calls of cr users in a zone.
     * @param zoneId Id number of the zone
     * @return total number of calls of cr users in a zone
     */
    public double getNumberOfCalls(int zoneId){
        return numberOfCalls.get(zoneId);
    }
    
    /**
     * Returns the total number of call attempts of cr users in a zone.
     * @param zoneId Id number of the zone
     * @return total number of call attempts of cr users in a zone
     */
    public double getNumberOfCallAttempts(int zoneId){
        return numberOfCallAttempts.get(zoneId);
    }
    
    /**
     * Increments the number of false alarms in a zone, by one.
     * @param zoneId Id number of the zone
     */
    public void incrementFalseAlarm(int zoneId) {
        falseAlarm.set(zoneId, falseAlarm.get(zoneId)+1);
    }
	
    /**
     * Increments the number of miss detections in a zone, by one.
     * @param zoneId Id number of the zone
     */
    public void incrementMissDetection(int zoneId) {
        missDetection.set(zoneId, missDetection.get(zoneId)+1);
    }
	
    /**
     * Increments the number of collisions in a zone, by one.
     * @param zoneId Id number of the zone
     */
    public void incrementCollision(int zoneId) {
        collisions.set(zoneId, collisions.get(zoneId)+1);
    }
    
    /**
     * Increments the number of drops in a zone, by one.
     * @param zoneId Id number of the zone
     */
    public void incrementDrop(int zoneId) {
        drops.set(zoneId, drops.get(zoneId)+1);
    }
    
    /**
     * Increments the number of blocks in a zone, by one.
     * @param zoneId Id number of the zone
     */
    public void incrementBlock(int zoneId) {
        blocks.set(zoneId, blocks.get(zoneId)+1);
    }
    
    /**
     * Increments the number of bits transmitted from cr users in a zone, by increase value.
     * @param zoneId Id number of the zone
     * @param increase Increase in the total number of bits transmitted
     */
    public void incrementTotalBitsTransmitted(int zoneId, double increase) {
        totalNumberOfBitsTransmitted.set(zoneId, totalNumberOfBitsTransmitted.get(zoneId)+increase);
    }
    
    /**
     * Increments the number of total communicated frames of cr users in a zone, by one.
     * @param zoneId Id number of the zone
     */
    public void incrementTotalCommunicatedFrames(int zoneId) {
        totalCommunicatedFrames.set(zoneId, totalCommunicatedFrames.get(zoneId)+1);
    }
    
    /**
     * Increments the number of calls of cr users in a zone, by one.
     * @param zoneId Id number of the zone
     */
    public void incrementNumberOfCalls(int zoneId){
        numberOfCalls.set(zoneId, numberOfCalls.get(zoneId)+1);
    }
    
    /**
     * Increments the number of call attempts of cr users in a zone, by one.
     * @param zoneId Id number of the zone
     */
    public void incrementNumberOfCallAttempts(int zoneId){
        numberOfCallAttempts.set(zoneId, numberOfCallAttempts.get(zoneId)+1);
    }
    
    /**
     * Adds a cr node to the cr base
     * @param n Cr node
     */
	public void addCRNode(CRNode n)
	{
		super.add(n);
	}
	
    /**
     * Returns the cr node with respect to its id.
     * @param id Id of the cr node
     * @return cr node with respect to its id
     */
	public CRNode getCRNode(int id)
	{
		return super.get(id);
	}
	
    /**
     * Returns total number of cr nodes in the cr base.
     * @return total number of cr nodes in the cr base
     */
	public int numberOfCRNodes()
	{
		return super.size();
	}
	
    /**
     * Returns total utilization of channels due to CR users.
     * @return Utilization  of channels due to CR users
     */
    public double utilization()
	{
		double totalNumberOfCommunicatedFrames = 0;
		for (CRNode node : this) {
			totalNumberOfCommunicatedFrames += node.getNumberOfFramesCommunicated();
		}
		double capacity = CRNode.getTotalNumberOfFrames()*SimulationRunner.args.getNumberOfFreq();
		double utilization = totalNumberOfCommunicatedFrames / capacity;
		return utilization * 100.0;
	}
}

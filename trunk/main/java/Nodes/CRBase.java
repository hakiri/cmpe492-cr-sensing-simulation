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
public class CRBase extends ArrayList<CRNode> implements Node{
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
    private ArrayList<Double> falseAlarm;
    private ArrayList<Double> missDetection;
    private ArrayList<Double> collisions;
    private ArrayList<Double> drops;
    private ArrayList<Double> blocks;
    private ArrayList<Double> totalNumberOfBitsTransmitted;
    private ArrayList<Double> totalCommunicatedFrames;
    private ArrayList<Double> numberOfCalls;
    private ArrayList<Double> numberOfCallAttempts;
    /**
     * Creates a CRBase at the given position.
     * @param pos Position of the Base station
     * @param id Id of the CRBase
     * @param number_of_freq_per_crnode The number of the frequencies that a CRNode is going to listen in a frame.
     */
    public CRBase(Point2D.Double pos,int id,int number_of_freq_per_crnode){
		super();
        this.id = id;
        this.position = new Point2D.Double(pos.x, pos.y);
        this.velocity = 0.0;
        this.frequency_to_be_listen=0;
        CRBase.number_of_freq_per_crnode = number_of_freq_per_crnode;
        CRBase.uniform = new Uniform(SimulationRunner.randEngine);
		this.currentSensingDecisions = new ArrayList<ArrayList<Integer>>();
        this.registeredZones = new ArrayList<ArrayList<Integer>>();
		this.nodesInZone = new ArrayList<Integer>();
        this.falseAlarm = new ArrayList<Double>();
        this.missDetection = new ArrayList<Double>();
        this.collisions = new ArrayList<Double>();
        this.blocks = new ArrayList<Double>();
        this.drops = new ArrayList<Double>();
        this.totalNumberOfBitsTransmitted = new ArrayList<Double>();
        this.totalCommunicatedFrames = new ArrayList<Double>();
        this.numberOfCalls = new ArrayList<Double>();
        this.numberOfCallAttempts = new ArrayList<Double>();
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
				get(i).setFrequencyList(frequencies); //assigns new freq list for crnode
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
			int iStart, iEnd;
			iStart = zone==0 ? 0:nodesInZone.get(zone-1);
			iEnd = zone==0 ? nodesInZone.get(0):nodesInZone.get(zone);
			for(int crInZone=iStart;crInZone<iEnd;crInZone++){
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
		free_frequencies = new ArrayList<ArrayList<Integer>>();
		for(int zoneNumber = 0 ; zoneNumber < registeredZones.size() ; zoneNumber++){
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
			int iStart, iEnd;
			iStart = zone==0 ? 0:nodesInZone.get(zone-1);
			iEnd = zone==0 ? nodesInZone.get(0):nodesInZone.get(zone);
			for(int crInZone=iStart;crInZone<iEnd;crInZone++){
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
		for(int zoneNumber=0;zoneNumber<registeredZones.size();zoneNumber++){
			int iStart, iEnd;
			iStart = zoneNumber==0 ? 0:nodesInZone.get(zoneNumber-1);
			iEnd = zoneNumber==0 ? nodesInZone.get(0):nodesInZone.get(zoneNumber);
			
			if(collidedInZone.get(zoneNumber) > 0){
				for(int crInZone=iStart;crInZone<iEnd;crInZone++){
					if(get(crInZone).getIsCollided()){
						if(get(crInZone).getCommunication_frequency() == -1){
							get(crInZone).setNumberOfDrops(get(crInZone).getNumberOfDrops() + 1);
							incrementDrop(zoneNumber);
							double msec = Scheduler.instance().getTime();
							int hour = (int)(msec/3600000.0);
							msec -= hour*3600000.0;
							int min = (int)(msec/60000.0);
							msec -= min*60000.0;
							int sec = (int)(msec/1000.0);
							msec-= sec*1000.0;
							
//							CRNode.writeLogFile("Time: " + String.format(Locale.US,"%2d:%2d:%2d:%.2f", hour,min,sec,msec) + " -- number: " + crInZone + " is dropped");
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
	 * Takes parameters of multiple zones and registers those zones into registeredZones
	 * @param sector Sector of the zone
	 * @param alpha Alpha number of the zone
	 * @param d Distance section of the zone
	 * @param crnodes Total number of crnodes in that zone
	 */
    public void registerZones(ArrayList<Integer> sector, ArrayList<Integer> alpha, ArrayList<Integer> d, ArrayList<Integer> crnodes){
        for(int i=0;i<sector.size();i++){
			ArrayList<Integer> zone = new ArrayList<Integer>();
			zone.add(sector.get(i));
			zone.add(alpha.get(i));
			zone.add(d.get(i));
			registeredZones.add(zone);
			missDetection.add(0.0);
			falseAlarm.add(0.0);
            collisions.add(0.0);
            blocks.add(0.0);
            drops.add(0.0);
            totalNumberOfBitsTransmitted.add(0.0);
            totalCommunicatedFrames.add(0.0);
            numberOfCalls.add(0.0);
            numberOfCallAttempts.add(0.0);
			if(nodesInZone.size() > 0)
				nodesInZone.add(crnodes.get(i) + nodesInZone.get(nodesInZone.size() - 1));
			else
				nodesInZone.add(crnodes.get(i));
		}
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
    
    /**
     * Returns the farthest distance from a CR node to base station for a given zone.
     * @param zoneId Id of zone
     * @return Farthest distance between a CR node and the base station
     */
    public double  farthestDistanceInZone(int zoneId)
    {
        int iStart,iEnd;
        double distance=0.0,temp_dist;
        if(zoneId == 0)
            iStart=0;
        else
            iStart = nodesInZone.get(zoneId-1);
        iEnd = nodesInZone.get(zoneId);
        for(int i=iStart;i<iEnd;i++){
            temp_dist = get(i).getPosition().distance(position);
            if(temp_dist>distance)
                distance = temp_dist;
        }
        return distance;
    }
    
    /**
     * Returns the total number of false alarms in a zone.
     * @param zoneId Id number of the zone
     * @return 
     */
    public double  getFalseAlarm(int zoneId) {
        return falseAlarm.get(zoneId);
    }
    
    /**
     * Returns the total number of miss detections in a zone.
     * @param zoneId Id number of the zone
     * @return 
     */
    public double getMissDetection(int zoneId) {
        return missDetection.get(zoneId);
    }
    
    /**
     * Returns the total number of collisions in a zone.
     * @param zoneId Id number of the zone
     * @return 
     */
    public double getCollisions(int zoneId) {
        return collisions.get(zoneId);
    }
    
    /**
     * Returns the total number of drops in a zone.
     * @param zoneId Id number of the zone
     * @return 
     */
    public double getDrops(int zoneId) {
        return drops.get(zoneId);
    }
    
    /**
     * Returns the total number of blocks in a zone.
     * @param zoneId Id number of the zone
     * @return 
     */
    public double getBlocks(int zoneId) {
        return blocks.get(zoneId);
    }
    
    /**
     * Returns the total number of bits transmitted from cr users in a zone.
     * @param zoneId Id number of the zone
     * @return 
     */
    public double getTotalBitsTransmitted(int zoneId) {
        return totalNumberOfBitsTransmitted.get(zoneId);
    }
    
    /**
     * Returns the total number of communicated frames of cr users in a zone.
     * @param zoneId Id number of the zone
     * @return 
     */
    public double getTotalCommunicatedFrames(int zoneId) {
        return totalCommunicatedFrames.get(zoneId);
    }
    
    /**
     * Returns the total number of calls of cr users in a zone.
     * @param zoneId Id number of the zone
     * @return 
     */
    public double getNumberOfCalls(int zoneId){
        return numberOfCalls.get(zoneId);
    }
    
    /**
     * Returns the total number of call attempts of cr users in a zone.
     * @param zoneId Id number of the zone
     * @return 
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
     * @return 
     */
	public CRNode getCRNode(int id)
	{
		return super.get(id);
	}
	
    /**
     * Returns total number of cr nodes in the cr base.
     * @return 
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

package MultiThreadedSimulation;

import Animation.DrawCell;
import Animation.SimulationStatsTable;
import Nodes.CRNode;
import SimulationRunner.GraphicalUserInterface;
import SimulationRunner.SimulationRunner;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * This class handles the frame structure of the CR nodes.
 */
public class CRSensorThread implements Runnable{

	/**
	 * Runner thread
	 */
	private Thread runner=null;
	/**
	 * Remaining simulation time
	 */
	private double remainingSimulationDuration;
	/**
	 * Total simulation duration
	 */
	private double totalSimulationDuration;
	/**
	 * boolean variable to terminate thread
	 */
	private boolean finished = false;
	/**
	 * Duration of one time unit
	 */
	private double unitTime;
	/**
	 * Duration of a frame in terms of msec
	 */
	private double frameDuration;
	/**
	 * Number of sensing slots in a frame
	 */
	private int numberOfSlots;
	/**
	 * Duration of a sensing slot
	 */
	private double slotDur;
	/**
	 * Duration of sensing schedule advertisement
	 */
	private double senseScheduleAdvertisement;
	/**
	 * Duration of communication schedule advertisement
	 */
	private double commScheduleAdvertisement;
	/**
	 * Duration of communication
	 */
	private double commDur;
	/**
	 * Duration of sense schedule advertisement
	 */
	private double senseResultAdvertisement;
	/**
	 * Current time
	 */
	private long time;
	/**
	 * Current frame
	 */
	private int frame;
	/**
	 * Communication related times of cr users
	 */
	private ArrayList<Integer> commRelatedTimes;
	private ArrayList<Integer> collisionWarningExpires;
	
	/**
	 * Creates a thread that performs simulation action for CR sensor nodes
	 * @param simulationDuration			Duration of the simulation in unit time
	 * @param unitTime						Unit of time in milliseconds
	 * @param numberOfSlots					Number of sensing slots in the frame
	 * @param slotDur						Duration of the sensing slots in terms of unit time
	 * @param senseScheduleAdvertisement	Duration of the sensing schedule advertisement in terms of unit time
	 * @param commScheduleAdvertisement		Duration of the communication schedule advertisement in terms of unit time
	 * @param commDur						Duration of the communication in terms of unit time
	 * @param senseResultAdvertisement		Duration of the sensing result advertisement in terms of unit time
	 */
	public CRSensorThread(double simulationDuration,double unitTime, int numberOfSlots, double slotDur,
			double senseScheduleAdvertisement, double commScheduleAdvertisement, double commDur,
			double senseResultAdvertisement)
	{
		this.remainingSimulationDuration = simulationDuration*unitTime;	//Perform simulation for every half of time unit
		this.unitTime = unitTime;
		this.numberOfSlots = numberOfSlots;
		this.slotDur = slotDur*unitTime;
		this.senseScheduleAdvertisement = senseScheduleAdvertisement*unitTime;
		this.commScheduleAdvertisement = commScheduleAdvertisement*unitTime;
		this.commDur = commDur*unitTime;
		this.senseResultAdvertisement = senseResultAdvertisement*unitTime;
		this.collisionWarningExpires = new ArrayList<Integer>();
		frameDuration = senseScheduleAdvertisement + numberOfSlots*slotDur + senseResultAdvertisement +
																					commScheduleAdvertisement + commDur;
		CRNode.setTotalNumberOfFrames((int)(simulationDuration / frameDuration));
		
		commRelatedTimes = new ArrayList<Integer>();
		for(int i = 0 ; i<SimulationRunner.crBase.numberOfCRNodes() ; i++){
			collisionWarningExpires.add(-1);
			int offDuration = SimulationRunner.crBase.getCRNode(i).nextOffDuration(frameDuration);
			offDuration = offDuration == 0 ? 1:offDuration;
			commRelatedTimes.add(offDuration);
			DrawCell.paintCrNode(SimulationRunner.crBase.getCRNode(i), Color.GRAY);
		}
		
		finished = false;
		if(runner==null){
            runner=new Thread(this);            //Create the thread
            runner.start();			//Start the thread: This method will call run method below
        }
	}
	
	/**
	 * Main thread operation
	 */
	@Override
	public void run() {
		totalSimulationDuration = remainingSimulationDuration;		//Save initial simulation duration
		ArrayList<String> namesList = new ArrayList<String>();
		namesList.add("Block");
		namesList.add("Drop");
		namesList.add("Real Collision");
		namesList.add("Measured Collision");
		ArrayList<String> namesList2 = new ArrayList<String>();
		namesList2.add("False Alarm");
		namesList2.add("Miss-detection");
		for(frame = 0; remainingSimulationDuration>0&&!finished ; frame++){		//Until simulation duration is elapsed or thread is terminated
			
			arrangeCommunicationVariables();
			
			expireCollisionWarnings();
			
			senseScheduleAdvertise();
			GraphicalUserInterface.progressBar.setValue((int)(((totalSimulationDuration-remainingSimulationDuration)*100)/totalSimulationDuration));	//Update progress bar
			if(remainingSimulationDuration<=0&&finished)
				break;
			
			for(int j=0;j<numberOfSlots;j++){
				sensingSlot(j);
				GraphicalUserInterface.progressBar.setValue((int)(((totalSimulationDuration-remainingSimulationDuration)*100)/totalSimulationDuration));	//Update progress bar
			}
			if(remainingSimulationDuration<=0&&finished)
				break;
			
			senseResultAdvertise();
			GraphicalUserInterface.progressBar.setValue((int)(((totalSimulationDuration-remainingSimulationDuration)*100)/totalSimulationDuration));	//Update progress bar
			if(remainingSimulationDuration<=0&&finished)
				break;
			
			commScheduleAdvertise();
			GraphicalUserInterface.progressBar.setValue((int)(((totalSimulationDuration-remainingSimulationDuration)*100)/totalSimulationDuration));	//Update progress bar
			if(remainingSimulationDuration<=0&&finished)
				break;
			
			communicate();
			GraphicalUserInterface.progressBar.setValue((int)(((totalSimulationDuration-remainingSimulationDuration)*100)/totalSimulationDuration));	//Update progress bar
		
			String fileName = SimulationRunner.args.getLogFileDirectory() + 
							  "Probs1_"+String.valueOf(SimulationRunner.args.getSeed())+
							  "_"+String.valueOf(SimulationRunner.args.getNumberOfPriNodes())+
							  "_"+String.valueOf(SimulationRunner.args.getNumberOfCrNodes());
			SimulationRunner.plotProbs.plotAllXWithLegend(fileName, 0, namesList,totalSimulationDuration/unitTime);
			fileName = SimulationRunner.args.getLogFileDirectory() + 
					   "Probs2_"+String.valueOf(SimulationRunner.args.getSeed())+
					   "_"+String.valueOf(SimulationRunner.args.getNumberOfPriNodes())+
					   "_"+String.valueOf(SimulationRunner.args.getNumberOfCrNodes());
			SimulationRunner.plotSensingProbs.plotAllXWithLegend(fileName, 0, namesList2, CRNode.getTotalNumberOfFrames()+1);
		}
		finalizeSimulation();
	}
	
	private void senseScheduleAdvertise()
	{
		time = System.currentTimeMillis();		//Save current time
		SimulationRunner.crBase.assignFrequencies();
		time = (long)senseScheduleAdvertisement - (System.currentTimeMillis() - time);	//Calculate time spent by now and subtract it from
		while(time>1){												//unit time if it is greater than 1 milli sec sleep for that amount
			long sleepStartTime = System.currentTimeMillis();
			try {
				Thread.sleep(time);		//Wait for unit time amount
				time = 0;
			} catch (InterruptedException ex) {
				long sleepDuration = System.currentTimeMillis() - sleepStartTime;
				time -= sleepDuration;
			}
		}
		remainingSimulationDuration-=senseScheduleAdvertisement;
	}
	
	private void sensingSlot(int slotNumber)
	{
		time = System.currentTimeMillis();		//Save current time
		/*Perform some semaphore locks to solve reader writer problem*/
		try {
			PrimaryTrafficGenerator.z.acquire();
		} catch (InterruptedException ex) {
			Logger.getLogger(CRSensorThread.class.getName()).log(Level.SEVERE, null, ex);
		}
		try {
			PrimaryTrafficGenerator.readLock.acquire();
		} catch (InterruptedException ex) {
			Logger.getLogger(CRSensorThread.class.getName()).log(Level.SEVERE, null, ex);
		}
		try {
			PrimaryTrafficGenerator.x.acquire();
		} catch (InterruptedException ex) {
			Logger.getLogger(CRSensorThread.class.getName()).log(Level.SEVERE, null, ex);
		}
		PrimaryTrafficGenerator.readerCount++;
		if(PrimaryTrafficGenerator.readerCount==1){
			try {
				PrimaryTrafficGenerator.writeLock.acquire();
			} catch (InterruptedException ex) {
				Logger.getLogger(CRSensorThread.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		PrimaryTrafficGenerator.x.release();
		PrimaryTrafficGenerator.readLock.release();
		PrimaryTrafficGenerator.z.release();

		for(int i=0;i<SimulationRunner.crBase.numberOfCRNodes();i++){
			SimulationRunner.crBase.getCRNode(i).sense(slotNumber);		//Sense the frequencies for each CR node
		}

		try {
			PrimaryTrafficGenerator.x.acquire();
		} catch (InterruptedException ex) {
			Logger.getLogger(CRSensorThread.class.getName()).log(Level.SEVERE, null, ex);
		}
		PrimaryTrafficGenerator.readerCount--;
		if(PrimaryTrafficGenerator.readerCount==0)
			PrimaryTrafficGenerator.writeLock.release();
		PrimaryTrafficGenerator.x.release();
		time = (long)slotDur - (System.currentTimeMillis() - time);	//Calculate time spent by now and subtract it from
		while(time>1){												//unit time if it is greater than 1 milli sec sleep for that amount
			long sleepStartTime = System.currentTimeMillis();
			try {
				Thread.sleep(time);		//Wait for unit time amount
				time = 0;
			} catch (InterruptedException ex) {
				long sleepDuration = System.currentTimeMillis() - sleepStartTime;
				time -= sleepDuration;
			}
		}
		remainingSimulationDuration-=slotDur;
	}
	
	private void senseResultAdvertise()
	{
        int totalBlocks=0,totalDrops=0,totalCallAttempts=0,totalCollisions=0,totalCalls=0,totalFrames = 0,totalEstimatedCollisions = 0;
        double falseAlarmsForAZone=0,missDetectionsForAZone=0,collisionsForAZone=0,dropsForAZone=0,blocksForAZone=0,
                callsForAZone=0,callAttemptsForAZone=0,communicatedFramesForAZone=0;
        double blockProb, dropProb,collisionProb,estimatedCollisionProb;
        String falseAlarmsString="",missDetectionsString="",collisionsString="",dropsString="",blocksString="",
                throughputString="",commFramesString="",callsString="",callAttemptsString="";
        String probFalseAlarmString="",probMissDetectionString="",probCollisionString="",probDropsString="",probBlocksString="";
        time = System.currentTimeMillis();		//Save current time
		/*Write time to log file*/
		double msec = (double)(totalSimulationDuration-remainingSimulationDuration)/unitTime;
//		int hour = (int)(msec/3600000.0);
//		msec -= hour*3600000.0;
//		int min = (int)(msec/60000.0);
//		msec -= min*60000.0;
//		int sec = (int)(msec/1000.0);
//		msec-= sec*1000.0;
//		CRNode.writeLogFile(String.format(Locale.US,"%2d:%2d:%2d:%.2f", hour,min,sec,msec));
//        CRNode.writeLogFileProb(String.format(Locale.US,"Time: %2d:%2d:%2d:%.2f", hour,min,sec,msec));
		//Reports statistics
        if(CRNode.reportingFrames.contains(frame)){
            for(int i=0;i<SimulationRunner.crBase.numberOfCRNodes();i++){
                totalBlocks += SimulationRunner.crBase.getCRNode(i).getNumberOfBlocks();
                totalDrops += SimulationRunner.crBase.getCRNode(i).getNumberOfDrops();
                totalCallAttempts += SimulationRunner.crBase.getCRNode(i).getNumberOfCallAttempts();
                totalCollisions += SimulationRunner.crBase.getCRNode(i).getNumberOfCollision();
                totalCalls += SimulationRunner.crBase.getCRNode(i).getNumberOfCalls();
                totalFrames += SimulationRunner.crBase.getCRNode(i).getNumberOfFramesCommunicated();
                totalEstimatedCollisions += SimulationRunner.crBase.getCRNode(i).getEstimatedNumberOfCollison();
            }
            if(totalCallAttempts == 0){
                blockProb = 0.0;
            }else{
                blockProb = (double)totalBlocks/totalCallAttempts;
            }
            if(totalCalls == 0){
                dropProb = 0.0;
            }
            else{
                dropProb = (double)totalDrops/totalCalls;
            }
            if(totalFrames == 0){
                collisionProb = 0.0;
                estimatedCollisionProb = 0.0;
            }
            else{
                collisionProb = (double)totalCollisions/totalFrames;
                estimatedCollisionProb = (double)totalEstimatedCollisions/totalFrames;
            }
            ArrayList<Double> probs = new ArrayList<Double>();
            probs.add(blockProb);
            probs.add(dropProb);
            probs.add(collisionProb);
            probs.add(estimatedCollisionProb);
            SimulationRunner.plotProbs.addPoint((totalSimulationDuration-remainingSimulationDuration)/unitTime, probs);
//            CRNode.writeLogFileProb(String.format(Locale.US,"Block prob: %.4f --- Drop prob: %.4f --- Collision prob: %.4f --- Estimated Collision prob: %.4f", blockProb,dropProb,collisionProb,estimatedCollisionProb));

            for(int i=0;i<SimulationRunner.args.getNumberOfZones();i++){
                falseAlarmsForAZone = SimulationRunner.crBase.getFalseAlarm(i);
                missDetectionsForAZone = SimulationRunner.crBase.getMissDetection(i);
                collisionsForAZone = SimulationRunner.crBase.getCollisions(i);
                blocksForAZone = SimulationRunner.crBase.getBlocks(i);
                dropsForAZone = SimulationRunner.crBase.getDrops(i);
                callsForAZone = SimulationRunner.crBase.getNumberOfCalls(i);
                callAttemptsForAZone = SimulationRunner.crBase.getNumberOfCallAttempts(i);
                communicatedFramesForAZone = SimulationRunner.crBase.getTotalCommunicatedFrames(i);
                //Strings for the log file
                falseAlarmsString += String.valueOf(falseAlarmsForAZone)+";";
                missDetectionsString += String.valueOf(missDetectionsForAZone)+";";
                collisionsString += String.valueOf(collisionsForAZone)+";";
                blocksString += String.valueOf(blocksForAZone)+";";
                dropsString += String.valueOf(dropsForAZone)+";";
                throughputString += String.valueOf(SimulationRunner.crBase.getTotalBitsTransmitted(i)/(communicatedFramesForAZone*getCommDurationInTermsOfUnitTime()))+";";
                commFramesString += String.valueOf(communicatedFramesForAZone)+";";
                callsString += String.valueOf(callsForAZone)+";";
                callAttemptsString += String.valueOf(callAttemptsForAZone)+";";
                //Calculation of probabilities and strings for the probability log file
                if(callAttemptsForAZone == 0){
                    probBlocksString += String.valueOf(0.0)+";";
                }else{
                    probBlocksString += String.valueOf(blocksForAZone/callAttemptsForAZone)+";";
                }
                if(callsForAZone == 0){
                    probDropsString += String.valueOf(0.0)+";";
                }
                else{
                    probDropsString += String.valueOf(dropsForAZone/callsForAZone)+";";
                }
                if(communicatedFramesForAZone == 0){
                    probCollisionString = String.valueOf(0.0)+";";
                }
                else{
                    probCollisionString = String.valueOf(collisionsForAZone/communicatedFramesForAZone)+";";
                }
                probFalseAlarmString = String.valueOf(((falseAlarmsForAZone/SimulationRunner.args.getNumbersOfCrUsersInAZone(i))/frame)/SimulationRunner.args.getNumberOfSensingSlots())+";";
                probMissDetectionString = String.valueOf(((missDetectionsForAZone/SimulationRunner.args.getNumbersOfCrUsersInAZone(i))/frame)/SimulationRunner.args.getNumberOfSensingSlots())+";";
            }
            CRNode.writeLogFile(String.format(Locale.US, "%.2f;"+falseAlarmsString+missDetectionsString+collisionsString
                    +blocksString+dropsString+throughputString+commFramesString+callsString+callAttemptsString,msec));
            CRNode.writeProbabilityLogFile(String.format(Locale.US, "%.2f"+probFalseAlarmString+probMissDetectionString
                    +probCollisionString+probBlocksString+probDropsString,msec));
        }
        CRNode.fuseSensingResults((double)(totalSimulationDuration-remainingSimulationDuration)/unitTime);	//Log average of SNR values sensed by the CR nodes
		
		time = (long)senseResultAdvertisement - (System.currentTimeMillis() - time);	//Calculate time spent by now and subtract it from
		while(time>1){												//unit time if it is greater than 1 milli sec sleep for that amount
			long sleepStartTime = System.currentTimeMillis();
			try {
				Thread.sleep(time);		//Wait for unit time amount
				time = 0;
			} catch (InterruptedException ex) {
				long sleepDuration = System.currentTimeMillis() - sleepStartTime;
				time -= sleepDuration;
			}
		}
		remainingSimulationDuration-=senseResultAdvertisement;
	}
	
	private void commScheduleAdvertise()
	{
		time = System.currentTimeMillis();		//Save current time
		SimulationRunner.crBase.communicationScheduleAdvertiser();
		time = (long)commScheduleAdvertisement - (System.currentTimeMillis() - time);	//Calculate time spent by now and subtract it from
		while(time>1){												//unit time if it is greater than 1 milli sec sleep for that amount
			long sleepStartTime = System.currentTimeMillis();
			try {
				Thread.sleep(time);		//Wait for unit time amount
				time = 0;
			} catch (InterruptedException ex) {
				long sleepDuration = System.currentTimeMillis() - sleepStartTime;
				time -= sleepDuration;
			}
		}
		remainingSimulationDuration-=commScheduleAdvertisement;
	}
	
	private void communicate()
	{
		time = System.currentTimeMillis();		//Save current time
		CRNode.communicate((double)(totalSimulationDuration-remainingSimulationDuration)/unitTime,true, false);
//		CRNode.writeLogFile("");
		double endRemainingSimulationDuration = remainingSimulationDuration - commDur;
		time = (long)(commDur) - (System.currentTimeMillis() - time);	//Calculate time spent by now and subtract it from
		while(time>1){												//unit time if it is greater than 1 milli sec
			long sleepStartTime = System.currentTimeMillis();
			try {												//sleep for that amount
				Thread.sleep(time);		//Wait for unit time amount
				time = 0;
			} catch (InterruptedException ex) {
				long sleepDuration = System.currentTimeMillis() - sleepStartTime;
				remainingSimulationDuration -= sleepDuration;
				CRNode.communicate((double)(totalSimulationDuration-remainingSimulationDuration)/unitTime,false ,false);
				time -= sleepDuration;
			}
		}
		remainingSimulationDuration = endRemainingSimulationDuration;
		CRNode.communicate((double)(totalSimulationDuration-remainingSimulationDuration)/unitTime,true ,true);
//		CRNode.writeLogFile("\n");
	}
	
	private void finalizeSimulation()
	{
		String[][] crStats = CRNode.logStats();
		String[][] priStats = SimulationRunner.priTrafGen.logStats();
		SimulationRunner.priTrafGen.terminateAllThreads();		//Terminate other thread in case of they did not
		if(finished)	//If the thread is terminated
			JOptionPane.showMessageDialog(null, "Simulation Terminated", "Simulation", JOptionPane.INFORMATION_MESSAGE);
		else			//If simulation duration is elapsed
			JOptionPane.showMessageDialog(null, "Simulation Completed", "Simulation", JOptionPane.INFORMATION_MESSAGE);
		GraphicalUserInterface.progressBar.setVisible(false);			//Hide progress bar
		GraphicalUserInterface.progressBar.setValue(0);				//Set its value to zero
		SimulationRunner.clear();								//Clear data related to simulation
		GraphicalUserInterface.terminateSimulation.setVisible(false);	//Hide "Terminate" button
		CRNode.closeLogFile();									//Close log file
        CRNode.closeProbabilityLogFile();                       //Close probability log file
		SimulationStatsTable sst;
		if(!SimulationRunner.args.isBatchMode())
			sst = new SimulationStatsTable(crStats, priStats, SimulationRunner.guiRunner);
		ArrayList<Integer> xs = new ArrayList<Integer>();
		xs.add(0);		
		if(SimulationRunner.args.isPlotOn()){
			ArrayList<String> names = new ArrayList<String>();
			for(int i=0;i<SimulationRunner.crBase.registeredZones.size();i++){
				names.add("SNR of Zone "+i);
			}
			names.add("SINR");
			SimulationRunner.plot.plotAll(names);			//Plot the time vs average SNR graphs
		}
		finished=true;											//Set finished as true
	}
	
	/**
	 * Returns whether the thread is finished or not
	 * @return finished
	 */
	public boolean isFinished()
	{
		return finished;
	}
	
	/**
	 * Terminates the thread
	 */
	public void terminate()
	{
		finished=true;
	}

	/**
	 * Returns remaining simulation duration
	 * @return	Remaining simulation duration
	 */
	public double getRemainingSimulationDuration() {
		return remainingSimulationDuration;
	}

	/**
	 * Returns total simulation duration
	 * @return	total simulation duration
	 */
	public double getSimulationDuration() {
		return totalSimulationDuration;
	}
    
	/**
	 * Returns the duration of a communication segment in a frame in terms of
	 * unit time (i.e. msec)
	 * @return	Duration of communication segment
	 */
	public double getCommDurationInTermsOfUnitTime(){
        return commDur/unitTime;
    }
    
	/**
	 * Return the ms per unit time
	 * @return Unit time
	 */
	public double getUnitTime() {
		return unitTime;
	}

	/**
	 * Returns the frame duration in terms of msec
	 * @return Frame Duration
	 */
	public double getFrameDuration() {
		return frameDuration;
	}
	
	/**
	 * Sets the ending frame of communication of the given CR node
	 * @param crnode_id		ID of the CR node
	 */
	public void setCommunationDuration(int crnode_id){
		int onDuration = SimulationRunner.crBase.getCRNode(crnode_id).nextOnDuration(frameDuration) - 1;
		commRelatedTimes.set(crnode_id, commRelatedTimes.get(crnode_id) + onDuration);
	}
	
	/**
	 * Sets the starting frame of communication of the given blocked or dropped CR node
	 * @param crnode_id		ID of the CR node
	 * @param dropped		Indicates whether the node dropped or not
	 */
	public void setInactiveDuration(int crnode_id, boolean dropped){
		int offDuration;
		if(dropped){
			offDuration = SimulationRunner.crBase.getCRNode(crnode_id).nextOffDuration(frameDuration);
			commRelatedTimes.set(crnode_id, frame + offDuration);
			if(SimulationRunner.args.isAnimationOn()){
				DrawCell.paintCrNode(SimulationRunner.crBase.getCRNode(crnode_id), Color.GRAY);
			}
		}
		else{
			offDuration = SimulationRunner.crBase.getCRNode(crnode_id).nextOffDuration(frameDuration) - 1;
			if(offDuration > 0)
				commRelatedTimes.set(crnode_id, commRelatedTimes.get(crnode_id) + offDuration);
		}
	}
	
	/**
	 * Sets variables associated to communication of a CR node whose communication
	 * event occurs at the current frame
	 */
	public void arrangeCommunicationVariables()
	{
		while(commRelatedTimes.contains(frame)){
			int cr = commRelatedTimes.indexOf(frame);
			if(SimulationRunner.crBase.getCRNode(cr).getCommOrNot()){
				DrawCell.paintCrNode(SimulationRunner.crBase.getCRNode(cr), Color.GRAY);
				SimulationRunner.crBase.getCRNode(cr).setIsCollided(false);
				SimulationRunner.crBase.getCRNode(cr).releaseCommunication_frequency();
				commRelatedTimes.set(cr, frame + SimulationRunner.crBase.getCRNode(cr).nextOffDuration(frameDuration));
			}
			else{
				SimulationRunner.crBase.getCRNode(cr).setReadytoComm(true);
				DrawCell.paintCrNode(SimulationRunner.crBase.getCRNode(cr), Color.ORANGE);
				commRelatedTimes.set(cr, commRelatedTimes.get(cr) + 1);
			}
		}
	}
	
	/**
	 * Expire the collision warnigs that should expire at current frame.
	 */
	public void expireCollisionWarnings()
	{
		while(collisionWarningExpires.contains(frame)){
			int cr = collisionWarningExpires.indexOf(frame);
			collisionWarningExpires.set(cr,-1);
			DrawCell.drawCollision(SimulationRunner.crBase.getCRNode(cr), false);
		}
		
	}
	
	/**
	 * Sets end time of collision warning of a given CR node
	 * @param crNodeId	ID of a CR node
	 */
	public void setWarningExpirationFrame(int crNodeId)
	{
		DrawCell.drawCollision(SimulationRunner.crBase.getCRNode(crNodeId), true);
		collisionWarningExpires.set(crNodeId, frame + (int)((2000.0)/(unitTime*frameDuration)));
	}

	/**
	 * Returns the runner thread object associated
	 * @return	Runner thread
	 */
	public Thread getRunner() {
		return runner;
	}
    
    /**
     * Returns the number of the current frame.
     * @return The current frame number
     */
    public int getFrame() {
        return frame;
    }
    
    
}

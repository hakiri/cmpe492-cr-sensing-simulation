package DESSimulation;

import Animation.SimulationStatsTable;
import DES.Event;
import DES.Scheduler;
import DES.Scheduler.EventHandle;
import DES.SimEnt;
import Nodes.CRBase;
import Nodes.CRNode;
import SimulationRunner.GraphicalUserInterface;
import SimulationRunner.SimulationRunner;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.JOptionPane;

/**
 * This class handles the frame structure of the CR nodes.
 */
public class CRDESScheduler extends SimEnt{
	/**
	 * Event class to start sense schedule advertisement for CR nodes
	 */
	private static class SenseScheduleAdvertiseEvent implements Event{
		@Override
		public void entering(SimEnt locale) {}
	}
	
	/**
	 * Event class to start sensing for CR nodes
	 */
	private static class SensingSlotEvent implements Event{
		int slotNumber=0;

		public void setSlotNumber(int slotNumber) {
			this.slotNumber = slotNumber;
		}
		
		@Override
		public void entering(SimEnt locale) {}
	}
	
	/**
	 * Event class to start sense result advertisement for CR nodes
	 */
	private static class SenseResultAdvertiseEvent implements Event{
		@Override
		public void entering(SimEnt locale) {}
	}
	
	/**
	 * Event class to start communication schedule advertisement for CR nodes
	 */
	private static class CommunicationScheduleAdvertiseEvent implements Event{
		@Override
		public void entering(SimEnt locale) {}
	}
	
	/**
	 * Event class to start communication for CR nodes
	 */
	private static class CommunicateEvent implements Event{
		boolean lastReport = false;
        boolean isReg ;

        public CommunicateEvent(boolean isReg) {
            this.isReg = isReg;
        }
        
		@Override
		public void entering(SimEnt locale) {}
        
        public void setLastReport(boolean lastReport) {
            this.lastReport = lastReport;
        }
	}
	
	private final static SenseScheduleAdvertiseEvent senseScheAdverEvent = new SenseScheduleAdvertiseEvent();
	private static SensingSlotEvent senseSlotEvent = new SensingSlotEvent();
	private final static SenseResultAdvertiseEvent senseResultAdverEvent = new SenseResultAdvertiseEvent();
	private final static CommunicationScheduleAdvertiseEvent commScheAdverEvent = new CommunicationScheduleAdvertiseEvent();
	private static CommunicateEvent regCommEvent = new CommunicateEvent(true);
	static CommunicateEvent collCommEvent = new CommunicateEvent(false);
	/**
	 * Simulation Duration
	 */
	private double simulationDuration;
	/**
	 * Whether the simulation is finished or not
	 */
	private boolean finished = false;
	/**
	 * Duration of one time unit
	 */
	private double unitTime;
	/**
	 * Number of sensing slots in CR frame
	 */
	private int numberOfSlots;
	/**
	 * Duration of sensing slots
	 */
	private double slotDur;
	/**
	 * Duration of sense schedule advertisement
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
	 * Duration of sense result advertisement
	 */
	private double senseResultAdvertisement;
	/**
	 * Duration of frame
	 */
	private double frameDuration;
	/**
	 * Current frame ID
	 */
	private long currentFrame;
    private boolean isInComm = false;    
     String justComma = "";    
	/**
	 * Creates a DES scheduler that performs frame action for CR sensor nodes
	 * @param simulationDuration			Duration of the simulation in unit time
	 * @param unitTime						Unit of time in milliseconds
	 * @param numberOfSlots					Number of sensing slots in the frame
	 * @param slotDur						Duration of the sensing slots in terms of unit time
	 * @param senseScheduleAdvertisement	Duration of the sensing schedule advertisement in terms of unit time
	 * @param commScheduleAdvertisement		Duration of the communication schedule advertisement in terms of unit time
	 * @param commDur						Duration of the communication in terms of unit time
	 * @param senseResultAdvertisement		Duration of the sensing result advertisement in terms of unit time
	 */
	public CRDESScheduler(double simulationDuration,double unitTime, int numberOfSlots, double slotDur,
			double senseScheduleAdvertisement, double commScheduleAdvertisement, double commDur,
			double senseResultAdvertisement)
	{
		this.simulationDuration = simulationDuration*unitTime;	//Perform simulation for every half of time unit
		this.unitTime = unitTime;
		this.numberOfSlots = numberOfSlots;
		this.slotDur = slotDur*unitTime;
		this.senseScheduleAdvertisement = senseScheduleAdvertisement*unitTime;
		this.commScheduleAdvertisement = commScheduleAdvertisement*unitTime;
		this.commDur = (commDur*unitTime);
		this.senseResultAdvertisement = senseResultAdvertisement*unitTime;
		finished = false;
		currentFrame = 0;
		this.frameDuration = senseScheduleAdvertisement + numberOfSlots*slotDur + senseResultAdvertisement + commScheduleAdvertisement + commDur;
		CRNode.setTotalNumberOfFrames((int)(simulationDuration / this.frameDuration));
        for(int i=0;i<SimulationRunner.args.getNumberOfZones();i++){
                justComma += ";";
        }
	}
	
	/**
	 * Starts the Simulation
	 */
	public void start()
	{
		send(this, senseScheAdverEvent, 0.0);
		for(int i=0;i<SimulationRunner.crBase.numberOfCRNodes();i++){
			double offDuration = SimulationRunner.crBase.getCRNode(i).nextOffDurationDES(this.frameDuration);
			SimulationRunner.crBase.getCRNode(i).startEventHandle = send(this, SimulationRunner.crBase.getCRNode(i).startCommEvent, offDuration);
		}
	}
	
	/**
	 * Main frame operation
	 * @param src	Source of the event
	 * @param ev	Occurred Event
	 */
	@Override
	public void recv(SimEnt src, Event ev) {
		if(ev instanceof SenseScheduleAdvertiseEvent){
			currentFrame++;
			senseScheduleAdvertise();
			senseSlotEvent.setSlotNumber(0);
			send(this,senseSlotEvent,senseScheduleAdvertisement);
		}
		else if(ev instanceof SensingSlotEvent){
			SensingSlotEvent sse = (SensingSlotEvent) ev;
			sensingSlot(sse.slotNumber);
			senseSlotEvent.setSlotNumber(sse.slotNumber+1);
			if(senseSlotEvent.slotNumber<numberOfSlots)
				send(this,senseSlotEvent,slotDur);
			else
				send(this,senseResultAdverEvent,slotDur);
		}
		else if(ev instanceof SenseResultAdvertiseEvent){
			senseResultAdvertise();
			send(this,commScheAdverEvent,senseResultAdvertisement);
		}
		else if(ev instanceof CommunicationScheduleAdvertiseEvent){
			commScheduleAdvertise();
			//commEvent.setNumberOfReports(0);
			regCommEvent.lastReport = false;
			send(this,regCommEvent,commScheduleAdvertisement);
		}
		else if(ev instanceof CommunicateEvent){
			CommunicateEvent ce = (CommunicateEvent)ev;
            if(ce == regCommEvent || (!ce.isReg && isInComm))
                communicate(ce.isReg,ce.lastReport);
			
            if(ce == regCommEvent && !(ce.lastReport)){
                isInComm = true;
                regCommEvent.setLastReport(true);
                send(this,regCommEvent,commDur);
            }
            else if(ce == regCommEvent && ce.lastReport){
                isInComm = false;
//				CRNode.writeLogFile("");
				send(this,senseScheAdverEvent,0.0);
			}
		}
		else if(ev instanceof CRNode.StartCommunicationEvent){
			CRNode.StartCommunicationEvent sce = (CRNode.StartCommunicationEvent) ev;
			SimulationRunner.crBase.getCRNode(sce.id).setReadytoComm(true);
		}
		else if(ev instanceof CRNode.EndCommunicationEvent){
			CRNode.EndCommunicationEvent ece = (CRNode.EndCommunicationEvent) ev;
			if(SimulationRunner.crBase.getCRNode(ece.id).getCommunication_frequency() != -1){
				SimulationRunner.crBase.getCRNode(ece.id).releaseCommunication_frequency();
				SimulationRunner.crBase.getCRNode(ece.id).setIsCollided(false);
				SimulationRunner.crBase.getCRNode(ece.id).startEventHandle = send(this,SimulationRunner.crBase.getCRNode(ece.id).startCommEvent,SimulationRunner.crBase.getCRNode(ece.id).nextOffDurationDES(this.frameDuration));
			}
		}
		SimulationRunner.args.setProgress((int)(((Scheduler.instance().getTime())*100)/simulationDuration));	//Update progress bar
		if(simulationDuration < Scheduler.instance().getTime()||finished)
			Scheduler.instance().stop();
	}

	/**
	 * Sets and displays statistics about simulation and reset the GUI on the program.
	 */
	@Override
	protected void destructor() {
		super.destructor();
		String[][] crStats = CRNode.logStats();
		String[][] priStats = SimulationRunner.priTrafGenDes.logStats();
		if(finished){	//If the thread is terminated
			if(SimulationRunner.args.isBatchMode())
				System.out.println("Simulation Terminated");
			else
				JOptionPane.showMessageDialog(null, "Simulation Terminated", "Simulation", JOptionPane.INFORMATION_MESSAGE);
		}
		else{			//If simulation duration is elapsed
			if(SimulationRunner.args.isBatchMode())
				System.out.println("Simulation Completed");
			else
				JOptionPane.showMessageDialog(null, "Simulation Completed", "Simulation", JOptionPane.INFORMATION_MESSAGE);
		}
		SimulationRunner.args.setProgress(-1);
		SimulationRunner.clear();								//Clear data related to simulation
		if(!SimulationRunner.args.isBatchMode())
			GraphicalUserInterface.terminateSimulation.setVisible(false);	//Hide "Terminate" button
		CRNode.closeLogFile();									//Close log file
		CRNode.closeLogFileProb();
		SimulationStatsTable sst;
		if(!SimulationRunner.args.isBatchMode())
			sst = new SimulationStatsTable(crStats, priStats, SimulationRunner.guiRunner);
		ArrayList<Integer> xs = new ArrayList<Integer>();
		xs.add(0);
		ArrayList<String> namesList = new ArrayList<String>();
		namesList.add("Block");
		namesList.add("Drop");
		namesList.add("Real Collision");
		namesList.add("Measured Collision");
		SimulationRunner.plotProbs.plotAllXWithLegend("Probabilities", 0, namesList,-1);
		namesList = new ArrayList<String>();
		namesList.add("False Alarm");
		namesList.add("Miss-detection");
		SimulationRunner.plotSensingProbs.plotAllXWithLegend("Probabilities2", 0, namesList, -1);
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
	
	
	private void senseScheduleAdvertise()
	{
		SimulationRunner.crBase.assignFrequencies();
	}
	
	private void sensingSlot(int slotNumber)
	{
		for(int i=0;i<SimulationRunner.crBase.numberOfCRNodes();i++){
			SimulationRunner.crBase.getCRNode(i).sense(slotNumber);		//Sense the frequencies for each CR node
		}
	}
	
	private void senseResultAdvertise()
	{
		int totalBlocks=0,totalDrops=0,totalCallAttempts=0,totalCollisions=0,totalCalls=0,totalFrames = 0,totalEstimatedCollisions = 0;
        double blockProb, dropProb,collisionProb,estimatedCollisionProb;
        String falseAlarms="",missDetections="",collisions="",drops="",blocks="",throughput="",commFrames="";
        /*Write time to log file*/
		double msec = (double)(Scheduler.instance().getTime())/unitTime,th=0.0;
//		int hour = (int)(msec/3600000.0);
//		msec -= hour*3600000.0;
//		int min = (int)(msec/60000.0);
//		msec -= min*60000.0;
//		int sec = (int)(msec/1000.0);
//		msec-= sec*1000.0;
//		CRNode.writeLogFile(String.format(Locale.US,"Time: %2d:%2d:%2d:%.2f", hour,min,sec,msec));
//		CRNode.writeLogFileProb(String.format(Locale.US,"Time: %2d:%2d:%2d:%.2f", hour,min,sec,msec));
        //calculate drop,block and collision probabilities
        
//		CRNode.writeLogFileProb(String.format(Locale.US,"Total number of Call Attempts: %d --- Total number of calls: %d --- Total number of drops: %d", totalCallAttempts,totalCalls,totalDrops));
//        CRNode.writeLogFileProb(String.format(Locale.US,"Block prob: %.4f --- Drop prob: %.4f --- Collision prob: %.4f --- Estimated Collision prob: %.4f", blockProb,dropProb,collisionProb,estimatedCollisionProb));
		
        if(CRNode.reportingFrames.contains((int)currentFrame)){
            for(int i=0;i<SimulationRunner.crBase.numberOfCRNodes();i++){
            totalBlocks += SimulationRunner.crBase.getCRNode(i).getNumberOfBlocks();
            totalDrops += SimulationRunner.crBase.getCRNode(i).getNumberOfDrops();
            totalCallAttempts += SimulationRunner.crBase.getCRNode(i).getNumberOfCallAttempts();
            totalCalls += SimulationRunner.crBase.getCRNode(i).getNumberOfCalls();
            totalCollisions += SimulationRunner.crBase.getCRNode(i).getNumberOfCollision();
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
            SimulationRunner.plotProbs.addPoint(Scheduler.instance().getTime(), probs);
            for(int i=0;i<SimulationRunner.args.getNumberOfZones();i++){
                falseAlarms += String.valueOf(SimulationRunner.crBase.getFalseAlarm(i))+";";
                missDetections += String.valueOf(SimulationRunner.crBase.getMissDetection(i))+";";
                collisions += String.valueOf(SimulationRunner.crBase.getCollisions(i))+";";
                blocks += String.valueOf(SimulationRunner.crBase.getBlocks(i))+";";
                drops += String.valueOf(SimulationRunner.crBase.getDrops(i))+";";
                throughput += String.valueOf(SimulationRunner.crBase.getTotalBitsTransmitted(i))+";";
                commFrames += String.valueOf(SimulationRunner.crBase.getTotalCommunicatedFrames(i))+";";
            }
            CRNode.writeLogFile(String.format(Locale.US, "%.2f;"+falseAlarms+missDetections+collisions+blocks+drops+throughput+commFrames,msec));
        }
        CRNode.fuseSensingResults((double)(Scheduler.instance().getTime())/unitTime);	//Log average of SNR values sensed by the CR nodes
	}
	
	private void commScheduleAdvertise()
	{
		SimulationRunner.crBase.communicationScheduleAdvertiser();
	}
	
	private void communicate(boolean isRegular, boolean lastReport)
	{
		CRNode.communicate((double)(Scheduler.instance().getTime())/unitTime,isRegular,lastReport);
	}

	/**
	 * Terminates the simulation
	 */
	public void terminate()
	{
		finished=true;
	}
	
	/**
	 * Returns whether the simulation is finished or not.
	 * @return True if simulation is finished, false otherwise.
	 */
	public boolean isFinished()
	{
		return finished;
	}
	
	/**
	 * Acknowledge event delivery
	 * @param h	Handle of the event
	 */
	@Override
	public void deliveryAck(EventHandle h) {
		//no op
	}
	
	/**
	 * Returns remaining simulation duration
	 * @return Remaining simulation duration
	 */
	public double getRemainingSimulationDuration() {
		return simulationDuration - Scheduler.instance().getTime();
	}

	/**
	 * Returns total simulation duration
	 * @return Total simulation duration
	 */
	public double getSimulationDuration() {
		return simulationDuration;
	}
	
	/**
	 * Returns duration of a communication segment in a frame
	 * @return	Duration of communication segment
	 */
	public double getCommDur() {
        return commDur;
    }

	/**
	 * Sets duration of a communication segment in a frame
	 * @param commDur	Duration of communication segment
	 */
	public void setCommDur(double commDur) {
        this.commDur = commDur;
    }

	/**
	 * Sends a communication end event for a given CR node
	 * @param crnode_id	ID of the CR node
	 */
	public void sendEndCommEvent(int crnode_id){
		SimulationRunner.crBase.getCRNode(crnode_id).endEventHandle = send(this,SimulationRunner.crBase.getCRNode(crnode_id).endCommEvent,SimulationRunner.crBase.getCRNode(crnode_id).nextOnDurationDES(this.frameDuration)-(this.frameDuration-this.commScheduleAdvertisement-this.commDur));
	}
	
	/**
	 * Sends a communication start event for a given CR node
	 * @param crnode_id	ID of the CR node
	 */
	public void sendStartCommEvent(int crnode_id){
		SimulationRunner.crBase.getCRNode(crnode_id).startEventHandle = send(this,SimulationRunner.crBase.getCRNode(crnode_id).startCommEvent,SimulationRunner.crBase.getCRNode(crnode_id).nextOffDurationDES(this.frameDuration)-(this.frameDuration-this.commScheduleAdvertisement-this.commDur));
	}

	public long getCurrentFrame() {
		return currentFrame;
	}

	public double getFrameDuration() {
		return frameDuration;
	}
}

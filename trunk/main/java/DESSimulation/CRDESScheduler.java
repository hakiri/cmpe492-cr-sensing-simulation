package DESSimulation;

import Animation.SimulationStatsTable;
import DES.Event;
import DES.Scheduler;
import DES.Scheduler.EventHandle;
import DES.SimEnt;
import SimulationRunner.CRNode;
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
		int numberOfReports = 0;
		
		@Override
		public void entering(SimEnt locale) {}

		public void setNumberOfReports(int numberOfReports) {
			this.numberOfReports = numberOfReports;
		}
	}
	
	private final static SenseScheduleAdvertiseEvent senseScheAdverEvent = new SenseScheduleAdvertiseEvent();
	private static SensingSlotEvent senseSlotEvent = new SensingSlotEvent();
	private final static SenseResultAdvertiseEvent senseResultAdverEvent = new SenseResultAdvertiseEvent();
	private final static CommunicationScheduleAdvertiseEvent commScheAdverEvent = new CommunicationScheduleAdvertiseEvent();
	private static CommunicateEvent commEvent = new CommunicateEvent();
	
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
	 * Number of SINR value reports during communication
	 */
	final static int numberOfReports = 3;
	/**
	 * Duration of frame
	 */
	private double frameDuration;
        
        
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
		this.commDur = (commDur*unitTime)/(numberOfReports);
		this.senseResultAdvertisement = senseResultAdvertisement*unitTime;
		finished = false;
		
		this.frameDuration = senseScheduleAdvertisement + numberOfSlots*slotDur + senseResultAdvertisement + commScheduleAdvertisement + commDur;
		CRNode.setTotalNumberOfFrames((int)(simulationDuration / this.frameDuration));
	}
	
	/**
	 * Starts the Simulation
	 */
	public void start()
	{
		send(this, senseScheAdverEvent, 0.0);
		for(int i=0;i<SimulationRunner.crNodes.size();i++){
			double offDuration = SimulationRunner.crNodes.get(i).nextOffDurationDES(this.frameDuration);
			SimulationRunner.crNodes.get(i).startEventHandle = send(this, SimulationRunner.crNodes.get(i).startCommEvent, offDuration);
		}
	}
	
	/**
	 * Main frame operation
	 * @param src	Source of the event
	 * @param ev	Occured Event
	 */
	@Override
	public void recv(SimEnt src, Event ev) {
		if(ev instanceof SenseScheduleAdvertiseEvent){
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
			commEvent.setNumberOfReports(0);
			send(this,commEvent,commScheduleAdvertisement);
		}
		else if(ev instanceof CommunicateEvent){
			CommunicateEvent ce = (CommunicateEvent)ev;
			boolean lastReport = ce.numberOfReports==numberOfReports;
			communicate(lastReport);
			commEvent.setNumberOfReports(ce.numberOfReports+1);
			if(lastReport){
				CRNode.writeLogFile("");
				send(this,senseScheAdverEvent,0.0);
			}
			else{
				send(this,commEvent,commDur);
			}
		}
		else if(ev instanceof CRNode.StartCommunicationEvent){
			CRNode.StartCommunicationEvent sce = (CRNode.StartCommunicationEvent) ev;
			SimulationRunner.crNodes.get(sce.id).setReadytoComm(true);
		}
		else if(ev instanceof CRNode.EndCommunicationEvent){
			CRNode.EndCommunicationEvent ece = (CRNode.EndCommunicationEvent) ev;
			//SimulationRunner.crNodes.get(ece.id).setCommOrNot(false);
			SimulationRunner.crNodes.get(ece.id).releaseCommunication_frequency();
			SimulationRunner.crNodes.get(ece.id).setIsCollided(false);
			SimulationRunner.crNodes.get(ece.id).startEventHandle = send(this,SimulationRunner.crNodes.get(ece.id).startCommEvent,SimulationRunner.crNodes.get(ece.id).nextOffDurationDES(this.frameDuration));
		}
		SimulationRunner.progressBar.setValue((int)(((Scheduler.instance().getTime())*100)/simulationDuration));	//Update progress bar
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
		if(finished)	//If the thread is terminated
			JOptionPane.showMessageDialog(null, "Simulation Terminated", "Simulation", JOptionPane.INFORMATION_MESSAGE);
		else			//If simulation duration is elapsed
			JOptionPane.showMessageDialog(null, "Simulation Completed", "Simulation", JOptionPane.INFORMATION_MESSAGE);
		SimulationRunner.progressBar.setVisible(false);			//Hide progress bar
		SimulationRunner.progressBar.setValue(0);				//Set its value to zero
		SimulationRunner.clear();								//Clear data related to simulation
		SimulationRunner.terminateSimulation.setVisible(false);	//Hide "Terminate" button
		CRNode.closeLogFile();									//Close log file
		SimulationStatsTable sst = new SimulationStatsTable(crStats, priStats, SimulationRunner.runner);
		if(SimulationRunner.plotOnButton.isSelected()){
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
		for(int i=0;i<SimulationRunner.crNodes.size();i++){
			SimulationRunner.crNodes.get(i).sense(slotNumber);		//Sense the frequencies for each CR node
		}
	}
	
	private void senseResultAdvertise()
	{
		/*Write time to log file*/
		CRNode.writeLogFile(String.format(Locale.US,"Time: %.2f", (double)(Scheduler.instance().getTime())/unitTime));
		for(int i=0;i<SimulationRunner.crNodes.size();i++){
			SimulationRunner.crNodes.get(i).logSnrValues();		//Log SNR values sensed by the CR nodes
		}
		CRNode.logAverageSnr((double)(Scheduler.instance().getTime())/unitTime);	//Log average of SNR values sensed by the CR nodes
	}
	
	private void commScheduleAdvertise()
	{
		SimulationRunner.crBase.communicationScheduleAdvertiser();
	}
	
	private void communicate(boolean lastReport)
	{
		CRNode.communicate((double)(Scheduler.instance().getTime())/unitTime,lastReport);
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
	
	public void sendEndCommEvent(int crnode_id){
		SimulationRunner.crNodes.get(crnode_id).endEventHandle = send(this,SimulationRunner.crNodes.get(crnode_id).endCommEvent,SimulationRunner.crNodes.get(crnode_id).nextOnDurationDES(this.frameDuration)-(this.frameDuration-this.commScheduleAdvertisement-(this.commDur*CRDESScheduler.numberOfReports)));
	}
	
	public void sendStartCommEvent(int crnode_id){
		SimulationRunner.crNodes.get(crnode_id).startEventHandle = send(this,SimulationRunner.crNodes.get(crnode_id).startCommEvent,SimulationRunner.crNodes.get(crnode_id).nextOffDurationDES(this.frameDuration)-(this.frameDuration-this.commScheduleAdvertisement-(this.commDur*CRDESScheduler.numberOfReports)));
	}
	
}
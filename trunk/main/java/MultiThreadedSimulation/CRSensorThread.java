package MultiThreadedSimulation;

import Animation.DrawCell;
import Animation.SimulationStatsTable;
import Nodes.CRNode;
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
	 * Number of checks for primary nodes during communication
	 */
	private final static int numberOfReports = 3;
	/**
	 * Current frame
	 */
	private int frame;
	/**
	 * Communication related times of cr users
	 */
	private ArrayList<Integer> commRelatedTimes;
	
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
		
		frameDuration = senseScheduleAdvertisement + numberOfSlots*slotDur + senseResultAdvertisement +
																					commScheduleAdvertisement + commDur;
		CRNode.setTotalNumberOfFrames((int)(simulationDuration / frameDuration));
		
		commRelatedTimes = new ArrayList<Integer>();
		for(int i = 0 ; i<SimulationRunner.crNodes.size() ; i++){
			int offDuration = SimulationRunner.crNodes.get(i).nextOffDuration(frameDuration);
			offDuration = offDuration == 0 ? 1:offDuration;
			commRelatedTimes.add(offDuration);
			DrawCell.paintCrNode(SimulationRunner.crNodes.get(i), Color.GRAY);
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
		for(frame = 0; remainingSimulationDuration>0&&!finished ; frame++){		//Until simulation duration is elapsed or thread is terminated
			
			while(commRelatedTimes.contains(frame)){
				int cr = commRelatedTimes.indexOf(frame);
				if(SimulationRunner.crNodes.get(cr).getCommOrNot()){
					DrawCell.paintCrNode(SimulationRunner.crNodes.get(cr), Color.GRAY);
					SimulationRunner.crNodes.get(cr).releaseCommunication_frequency();
					SimulationRunner.crNodes.get(cr).setIsCollided(false);
					commRelatedTimes.set(cr, frame + SimulationRunner.crNodes.get(cr).nextOffDuration(frameDuration));
				}
				else{
					SimulationRunner.crNodes.get(cr).setReadytoComm(true);
					DrawCell.paintCrNode(SimulationRunner.crNodes.get(cr), Color.ORANGE);
					commRelatedTimes.set(cr, commRelatedTimes.get(cr) + 1);
				}
			}
			
			senseScheduleAdvertise();
			SimulationRunner.progressBar.setValue((int)(((totalSimulationDuration-remainingSimulationDuration)*100)/totalSimulationDuration));	//Update progress bar
			if(remainingSimulationDuration<=0&&finished)
				break;
			
			for(int j=0;j<numberOfSlots;j++){
				sensingSlot(j);
				SimulationRunner.progressBar.setValue((int)(((totalSimulationDuration-remainingSimulationDuration)*100)/totalSimulationDuration));	//Update progress bar
			}
			if(remainingSimulationDuration<=0&&finished)
				break;
			
			senseResultAdvertise();
			SimulationRunner.progressBar.setValue((int)(((totalSimulationDuration-remainingSimulationDuration)*100)/totalSimulationDuration));	//Update progress bar
			if(remainingSimulationDuration<=0&&finished)
				break;
			
			commScheduleAdvertise();
			SimulationRunner.progressBar.setValue((int)(((totalSimulationDuration-remainingSimulationDuration)*100)/totalSimulationDuration));	//Update progress bar
			if(remainingSimulationDuration<=0&&finished)
				break;
			
			communicate(numberOfReports);
			SimulationRunner.progressBar.setValue((int)(((totalSimulationDuration-remainingSimulationDuration)*100)/totalSimulationDuration));	//Update progress bar
		}
		String[][] crStats = CRNode.logStats();
		String[][] priStats = SimulationRunner.priTrafGen.logStats();
		SimulationRunner.priTrafGen.terminateAllThreads();		//Terminate other thread in case of they did not
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
			names.add("SNR");
			names.add("SINR");
			SimulationRunner.plot.plotAll(names);			//Plot the time vs average SNR graphs
		}
		finished=true;											//Set finished as true
	}
	
	private void senseScheduleAdvertise()
	{
		time = System.currentTimeMillis();		//Save current time
		SimulationRunner.crBase.assignFrequencies();
		time = (long)senseScheduleAdvertisement - (System.currentTimeMillis() - time);	//Calculate time spent by now and subtract it from
		if(time>1){												//unit time if it is greater than 1 milli sec
			try {												//sleep for that amount
				Thread.sleep(time);		//Wait for unit time amount
			} catch (InterruptedException ex) {
				Logger.getLogger(CRSensorThread.class.getName()).log(Level.SEVERE, null, ex);
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

		for(int i=0;i<SimulationRunner.crNodes.size();i++){
			SimulationRunner.crNodes.get(i).sense(slotNumber);		//Sense the frequencies for each CR node
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
		if(time>1){												//unit time if it is greater than 1 milli sec
			try {												//sleep for that amount
				Thread.sleep(time);		//Wait for unit time amount
			} catch (InterruptedException ex) {
				Logger.getLogger(CRSensorThread.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		remainingSimulationDuration-=slotDur;
	}
	
	private void senseResultAdvertise()
	{
		time = System.currentTimeMillis();		//Save current time
		/*Write time to log file*/
		double msec = (double)(totalSimulationDuration-remainingSimulationDuration)/unitTime;
		int hour = (int)(msec/3600000.0);
		msec -= hour*3600000.0;
		int min = (int)(msec/60000.0);
		msec -= min*60000.0;
		int sec = (int)(msec/1000.0);
		msec-= sec*1000.0;
		CRNode.writeLogFile(String.format(Locale.US,"%2d:%2d:%2d:%.2f", hour,min,sec,msec));
		for(int i=0;i<SimulationRunner.crNodes.size();i++){
			SimulationRunner.crNodes.get(i).logSnrValues();		//Log SNR values sensed by the CR nodes
		}
		CRNode.logAverageSnr((double)(totalSimulationDuration-remainingSimulationDuration)/unitTime);	//Log average of SNR values sensed by the CR nodes
		//CRNode.writeLogFile("\n");
		time = (long)senseResultAdvertisement - (System.currentTimeMillis() - time);	//Calculate time spent by now and subtract it from
		if(time>1){												//unit time if it is greater than 1 milli sec
			try {												//sleep for that amount
				Thread.sleep(time);		//Wait for unit time amount
			} catch (InterruptedException ex) {
				Logger.getLogger(CRSensorThread.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		remainingSimulationDuration-=senseResultAdvertisement;
	}
	
	private void commScheduleAdvertise()
	{
		time = System.currentTimeMillis();		//Save current time
		SimulationRunner.crBase.communicationScheduleAdvertiser();
		time = (long)commScheduleAdvertisement - (System.currentTimeMillis() - time);	//Calculate time spent by now and subtract it from
		if(time>1){												//unit time if it is greater than 1 milli sec
			try {												//sleep for that amount
				Thread.sleep(time);		//Wait for unit time amount
			} catch (InterruptedException ex) {
				Logger.getLogger(CRSensorThread.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		remainingSimulationDuration-=commScheduleAdvertisement;
	}
	
	private void communicate(int numberOfReports)
	{
		if(numberOfReports<1)
			numberOfReports=1;
		for(int i=0;i<numberOfReports;i++){
			time = System.currentTimeMillis();		//Save current time
			CRNode.communicate((double)(totalSimulationDuration-remainingSimulationDuration)/unitTime, false);
			CRNode.writeLogFile("");
			time = (long)(commDur/numberOfReports) - (System.currentTimeMillis() - time);	//Calculate time spent by now and subtract it from
			if(time>1){												//unit time if it is greater than 1 milli sec
				try {												//sleep for that amount
					Thread.sleep(time);		//Wait for unit time amount
				} catch (InterruptedException ex) {
					Logger.getLogger(CRSensorThread.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			remainingSimulationDuration-=(commDur/numberOfReports);
		}
		CRNode.communicate((double)(totalSimulationDuration-remainingSimulationDuration)/unitTime, true);
		CRNode.writeLogFile("\n");
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
		int onDuration = SimulationRunner.crNodes.get(crnode_id).nextOnDuration(frameDuration) - 1;
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
			offDuration = SimulationRunner.crNodes.get(crnode_id).nextOffDuration(frameDuration);
			commRelatedTimes.set(crnode_id, frame + offDuration);
			//SimulationRunner.crNodes.get(crnode_id).setCommOrNot(false);
		}
		else{
			offDuration = SimulationRunner.crNodes.get(crnode_id).nextOffDuration(frameDuration) - 1;
			if(offDuration > 0)
				commRelatedTimes.set(crnode_id, commRelatedTimes.get(crnode_id) + offDuration);
		}
	
	}
}

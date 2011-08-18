package MultiThreadedSimulation;

import Animation.SimulationStatsTable;
import SimulationRunner.CRNode;
import SimulationRunner.SimulationRunner;
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
	private long unitTime;
	private int numberOfSlots;
	private double slotDur;
	private double senseScheduleAdvertisement;
	private double commScheduleAdvertisement;
	private double commDur;
	private double senseResultAdvertisement;
	private long time;
	
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
	public CRSensorThread(double simulationDuration,long unitTime, int numberOfSlots, double slotDur,
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
		
		double frameDuration = senseScheduleAdvertisement + numberOfSlots*slotDur + senseResultAdvertisement +
																					commScheduleAdvertisement + commDur;
		CRNode.setTotalNumberOfFrames((int)(simulationDuration / frameDuration));
		
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
		while(remainingSimulationDuration>0&&!finished){		//Until simulation duration is elapsed or thread is terminated
			senseScheduleAdvertise();
			SimulationRunner.progressBar.setValue((((int)totalSimulationDuration-(int)remainingSimulationDuration)*100)/(int)totalSimulationDuration);	//Update progress bar
			if(remainingSimulationDuration<=0&&finished)
				break;
			
			for(int j=0;j<numberOfSlots;j++){
				sensingSlot(j);
				SimulationRunner.progressBar.setValue((((int)totalSimulationDuration-(int)remainingSimulationDuration)*100)/(int)totalSimulationDuration);	//Update progress bar
			}
			if(remainingSimulationDuration<=0&&finished)
				break;
			
			senseResultAdvertise();
			SimulationRunner.progressBar.setValue((((int)totalSimulationDuration-(int)remainingSimulationDuration)*100)/(int)totalSimulationDuration);	//Update progress bar
			if(remainingSimulationDuration<=0&&finished)
				break;
			
			commScheduleAdvertise();
			SimulationRunner.progressBar.setValue((((int)totalSimulationDuration-(int)remainingSimulationDuration)*100)/(int)totalSimulationDuration);	//Update progress bar
			if(remainingSimulationDuration<=0&&finished)
				break;
			
			communicate(3);
			SimulationRunner.progressBar.setValue((((int)totalSimulationDuration-(int)remainingSimulationDuration)*100)/(int)totalSimulationDuration);	//Update progress bar
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
		CRNode.writeLogFile(String.format(Locale.US,"Time: %.2f", (double)(totalSimulationDuration-remainingSimulationDuration)/unitTime));
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
}

package firstproject;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author acar
 */
public class CRSensorThread implements Runnable{

	/**
	 * Runner thread
	 */
	private Thread runner=null;
	/**
	 * Remaining simulation time
	 */
	private int simulationDuration;
	/**
	 * boolean variable to terminate thread
	 */
	private boolean finished = false;
	/**
	 * Duration of one time unit
	 */
	private long unitTime;
	
	/**
	 * Creates a thread that performs simulation action for CR sensor nodes
	 * @param simulationDuration	Duration of the simulation in unit time
	 * @param unitTime				Unit of time in milliseconds
	 */
	public CRSensorThread(int simulationDuration,long unitTime)
	{
		
		this.simulationDuration = simulationDuration*2;	//Perform simulation for every half of time unit
		this.unitTime = unitTime/2;
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
		int simulationDur = simulationDuration;		//Save initial simulation duration
		long time = 0;								//Time to sleep
		while(simulationDuration>0&&!finished){		//Until simulation duration is elapsed or thread is terminated
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
				SimulationRunner.crNodes.get(i).sense();		//Sense the frequencies for each CR node
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

			/*Write time to log file*/
			CRNode.writeLogFile(String.format("Time: %.2f", (double)(simulationDur-simulationDuration)/2.0));
			for(int i=0;i<SimulationRunner.crNodes.size();i++){
				SimulationRunner.crNodes.get(i).logSnrValues();		//Log SNR values sensed by the CR nodes
			}
			CRNode.logAverageSnr(SimulationRunner.crNodes.size(),simulationDur-simulationDuration+1,
					(double)(simulationDur-simulationDuration)/2.0);	//Log average of SNR values sensed by the CR nodes
			CRNode.writeLogFile("\n");
			time = unitTime - (System.currentTimeMillis() - time);	//Calculate time spent by now and subtract it from
			if(time>1){												//unit time if it is greater than 1 milli sec
				try {												//sleep for that amount
					Thread.sleep(time);		//Wait for unit time amount
				} catch (InterruptedException ex) {
					Logger.getLogger(CRSensorThread.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			simulationDuration-=1;			//Reduce the simulation time for that amount
			SimulationRunner.progressBar.setValue(((simulationDur-simulationDuration)*100)/simulationDur);	//Update progress bar
		}
		SimulationRunner.priTrafGen.terminateAllThreads();		//Terminate other thread in case of they did not
		if(finished)	//If the thread is terminated
			JOptionPane.showMessageDialog(null, "Simulation Terminated", "Simulation", JOptionPane.WARNING_MESSAGE);
		else			//If simulation duration is elapsed
			JOptionPane.showMessageDialog(null, "Simulation Completed", "Simulation", JOptionPane.WARNING_MESSAGE);
		SimulationRunner.progressBar.setVisible(false);			//Hide progress bar
		SimulationRunner.progressBar.setValue(0);				//Set its value to zero
		SimulationRunner.clear();								//Clear data related to simulation
		SimulationRunner.terminateSimulation.setVisible(false);	//Hide "Terminate" button
		CRNode.closeLogFile();									//Close log file
		SimulationRunner.plot.plotAll(simulationDur/2);			//Plot the time vs average SNR graphs
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
}

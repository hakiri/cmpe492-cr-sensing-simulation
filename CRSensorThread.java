/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
	
	@Override
	public void run() {
		int simulationDur = simulationDuration;
		long time = 0;
		while(simulationDuration>0&&!finished){
			time = System.currentTimeMillis();
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
				SimulationRunner.crNodes.get(i).sense();
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


			CRNode.writeLogFile(String.format("Time: %.2f", (double)(simulationDur-simulationDuration)/(double)2));
			for(int i=0;i<SimulationRunner.crNodes.size();i++){
				SimulationRunner.crNodes.get(i).logSnrValues();
			}
			CRNode.logAverageSnr(SimulationRunner.crNodes.size());
			CRNode.writeLogFile("\n");
			time = unitTime - (System.currentTimeMillis() - time);
			if(time>1){
				try {
					Thread.sleep(time);		//Wait for unit time amount
				} catch (InterruptedException ex) {
					Logger.getLogger(CRSensorThread.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			simulationDuration-=1;			//Reduce the simulation time for that amount
			SimulationRunner.progressBar.setValue(((simulationDur-simulationDuration)*100)/simulationDur);
			System.out.println("\n");
		}
		if(finished)
			JOptionPane.showMessageDialog(null, "Simulation Terminated", "Simulation", JOptionPane.WARNING_MESSAGE);
		else
			JOptionPane.showMessageDialog(null, "Simulation Completed", "Simulation", JOptionPane.WARNING_MESSAGE);
		SimulationRunner.progressBar.setVisible(false);
		SimulationRunner.progressBar.setValue(0);
		SimulationRunner.priTrafGen.terminateAllThreads();
		SimulationRunner.clear();
		SimulationRunner.terminateSimulation.setVisible(false);
		CRNode.closeLogFile();
		finished=true;
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

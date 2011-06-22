/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import java.util.logging.Level;
import java.util.logging.Logger;

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
	private long simulationDuration;
	/**
	 * boolean variable to terminate thread
	 */
	private boolean finished = false;
	/**
	 * Duration of one time unit
	 */
	private long unitTime;
	
	public CRSensorThread(long simulationDuration,long unitTime)
	{
		
		this.simulationDuration = simulationDuration;
		this.unitTime = unitTime;
		finished = false;
		if(runner==null){
            runner=new Thread(this);            //Create the thread
            runner.start();			//Start the thread: This method will call run method below
        }
	}
	@Override
	public void run() {
		long simulationDur = simulationDuration;
		long time = 0;
		while(simulationDuration>0&&!finished){
			try {
				PrimaryTrafficGenerator.z.lock();
				PrimaryTrafficGenerator.readLock.lock();
				PrimaryTrafficGenerator.x.lock();
				PrimaryTrafficGenerator.readerCount++;
				if(PrimaryTrafficGenerator.readerCount==1)
					PrimaryTrafficGenerator.writeLock.lock();
				PrimaryTrafficGenerator.x.unlock();
				PrimaryTrafficGenerator.readLock.unlock();
				PrimaryTrafficGenerator.z.unlock();
				
				for(int i=0;i<SimulationRunner.crNodes.size();i++){
					SimulationRunner.crNodes.get(i).sense();
				}
				
				PrimaryTrafficGenerator.x.lock();
				PrimaryTrafficGenerator.readerCount--;
				if(PrimaryTrafficGenerator.readerCount==0)
					PrimaryTrafficGenerator.writeLock.unlock();
				PrimaryTrafficGenerator.x.unlock();
				
				
				for(int i=0;i<SimulationRunner.crNodes.size();i++){
					//TODO Log SNR values
				}
				
				Thread.sleep(time);		//Wait for that amount
				simulationDuration-=1;			//Reduce the simulation time for that amount
			} catch (InterruptedException ex) {
				Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ex);
			} finally{
				PrimaryTrafficGenerator.writeLock.unlock();	//Release the critical section
				PrimaryTrafficGenerator.readLock.unlock();
				PrimaryTrafficGenerator.x.unlock();
				PrimaryTrafficGenerator.z.unlock();
			}
		}
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

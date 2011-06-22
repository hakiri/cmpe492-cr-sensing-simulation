/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread responsible for generating an invidual traffic in the wireless channel.
 */
public class PrimaryTrafficGeneratorThread implements Runnable{
	/**
	 * Asscoiated node to this traffic generator
	 */
	private PrimaryTrafficGeneratorNode n;
	/**
	 * Runner thread
	 */
	private Thread runner;
	/**
	 * Remaining simulation time
	 */
	private long simulationDuration;
	/**
	 * boolean variable to terminate thread
	 */
	private boolean finished = false;
	
	/**
	 * Creates a primary traffic generator thread associated with node n
	 * @param n
	 * @param simulationDuration duration of the simulation in nanoseconds
	 */
	public PrimaryTrafficGeneratorThread(PrimaryTrafficGeneratorNode n, long simulationDuration)
	{
		
		this.simulationDuration = simulationDuration;
		this.n = n;
		finished = false;
		if(runner==null){
            runner=new Thread(this);            //Create the thread
            runner.start();			//Start the thread: This method will call run method below
        }
	}
	
	/**
	 * Finds a free frequnecy and occupies it. This method is synchronized. That
	 * is only one thread at a time can run it
	 * @return ID of the occupied frequency
	 */
	private int generateTraffic()
	{
		int freq = SimulationRunner.wc.freeFrequency();         //Find a free frequency
		if(freq==WirelessChannel.NOFREEFREQ)			//If there is no available frequency
			return freq;								//Return immediately
		n.setRandomPosition();
		SimulationRunner.wc.occupyFrequency(freq, n);	//Occupy the frequency
		return freq;									//Return its ID
	}
	
	/**
	 * Main thread operation
	 */
	@Override
	public void run() {
		long time = 0;
		int freq=0;
		while(simulationDuration>0&&!finished){
			try {
				PrimaryTrafficGenerator.interArrivalLock.lock();
				time = Math.round(PrimaryTrafficGenerator.interArrival.nextDouble());	//Take a random inter arrival time
				PrimaryTrafficGenerator.interArrivalLock.unlock();
				simulationDuration-=time;			//Reduce the simulation time for that amount
				if(simulationDuration<0)			//If times up
					break;                                  //stop simulation
				Thread.sleep(time);		//Wait for that amount
				
				PrimaryTrafficGenerator.y.lock();
				PrimaryTrafficGenerator.writerCount++;
				if(PrimaryTrafficGenerator.writerCount==1)
					PrimaryTrafficGenerator.readLock.lock();
				PrimaryTrafficGenerator.y.unlock();
				
				PrimaryTrafficGenerator.writeLock.lock();
				if((freq = generateTraffic())==WirelessChannel.NOFREEFREQ)	//If no frequency occupied
					continue;						//Wait for another inter arrival time
				PrimaryTrafficGenerator.writeLock.unlock();
				
				PrimaryTrafficGenerator.y.lock();
				PrimaryTrafficGenerator.writerCount--;
				if(PrimaryTrafficGenerator.writerCount==0)
					PrimaryTrafficGenerator.readLock.unlock();
				PrimaryTrafficGenerator.y.unlock();
				
				PrimaryTrafficGenerator.callDurationLock.lock();
				time = Math.round(PrimaryTrafficGenerator.callDuration.nextDouble());	//Take a random call duration
				PrimaryTrafficGenerator.callDurationLock.unlock();
				simulationDuration-=time;		//Reduce the simulation time for that amount
				if(simulationDuration<0){		//if times up
					time=time+simulationDuration;	//Last the call until end of the simulation
				}
				Thread.sleep(time);		//Wait for that amount
				
				PrimaryTrafficGenerator.y.lock();
				PrimaryTrafficGenerator.writerCount++;
				if(PrimaryTrafficGenerator.writerCount==1)
					PrimaryTrafficGenerator.readLock.lock();
				PrimaryTrafficGenerator.y.unlock();
				
				PrimaryTrafficGenerator.writeLock.lock();
				SimulationRunner.wc.releaseFrequency(freq);	//Release frequency
				PrimaryTrafficGenerator.writeLock.unlock();
				
				PrimaryTrafficGenerator.y.lock();
				PrimaryTrafficGenerator.writerCount--;
				if(PrimaryTrafficGenerator.writerCount==0)
					PrimaryTrafficGenerator.readLock.unlock();
				PrimaryTrafficGenerator.y.unlock();
			} catch (InterruptedException ex) {
				Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ex);
			} finally{
				PrimaryTrafficGenerator.writeLock.unlock();	//Release the critical section
				PrimaryTrafficGenerator.readLock.unlock();
				PrimaryTrafficGenerator.y.unlock();
				PrimaryTrafficGenerator.interArrivalLock.unlock();
				PrimaryTrafficGenerator.callDurationLock.unlock();
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

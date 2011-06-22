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
	private Node n;
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
	 * Creates a primary traffic generator thread associated with node n
	 * @param n
	 * @param simulationDuration duration of the simulation in nanoseconds
	 */
	public PrimaryTrafficGeneratorThread(Node n, long simulationDuration)
	public PrimaryTrafficGeneratorThread(PrimaryTrafficGeneratorNode n, long simulationDuration)
	{
		
		this.simulationDuration = simulationDuration;
		this.n = n;
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
	private synchronized int generateTraffic()
	{
		int freq = SimulationRunner.wc.freeFrequency();         //Find a free frequency
		if(freq==WirelessChannel.NOFREEFREQ)			//If there is no available frequency
			return freq;								//Return immediately
		n.setRandomPosition();
		SimulationRunner.wc.occupyFrequency(freq, n);	//Occupy the frequency
		return freq;									//Return its ID
		return freq;									//Return its ID
			return freq;					//Return immediately
		SimulationRunner.wc.occupyFrequency(freq, n);           //Occupy the frequency
		return freq;						//Return its ID
	}
	
	/**
	 * Main thread operation
	 */
	@Override
	public void run() {
		long time = 0;
		int freq=0;
		while(simulationDuration>0){
			try {
				PrimaryTrafficGenerator.interArrivalLock.lock();
				time = Math.round(PrimaryTrafficGenerator.interArrival.nextDouble());	//Take a random inter arrival time
				PrimaryTrafficGenerator.interArrivalLock.unlock();
				simulationDuration-=time;			//Reduce the simulation time for that amount
				if(simulationDuration<0)			//If times up
					break;                                  //stop simulation
				Thread.sleep(time);		//Wait for that amount
				
				if((freq = generateTraffic())==WirelessChannel.NOFREEFREQ)	//If no frequency occupied
					continue;						//Wait for another inter arrival time
				
				PrimaryTrafficGenerator.callDurationLock.lock();
				time = Math.round(PrimaryTrafficGenerator.callDuration.nextDouble());	//Take a random call duration
				PrimaryTrafficGenerator.callDurationLock.unlock();
				simulationDuration-=time;		//Reduce the simulation time for that amount
				if(simulationDuration<0){		//if times up
					time=time+simulationDuration;	//Last the call until end of the simulation
				}
				Thread.sleep(time);		//Wait for that amount
				PrimaryTrafficGenerator.lock.lock();//Create a critical section to release occupied frequency
				SimulationRunner.wc.releaseFrequency(freq);	//Release frequency
			} catch (InterruptedException ex) {
				Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ex);
			} finally{
				PrimaryTrafficGenerator.lock.unlock();	//Release the critical section
				PrimaryTrafficGenerator.interArrivalLock.unlock();
				PrimaryTrafficGenerator.callDurationLock.unlock();
			}
		}
	}
	
	
}

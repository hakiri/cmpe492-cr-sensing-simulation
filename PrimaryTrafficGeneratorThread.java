/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import cern.jet.random.Exponential;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrimaryTrafficGeneratorThread implements Runnable{
	/**
	 * Asscoiated node to this traffic generator
	 */
	private Node n;
	/**
	 * Runner thread
	 */
	private Thread runner;
	/**
	 * Distribution for inter arrival times
	 */
	private Exponential interArrival;
	/**
	 * Distribution for call durations
	 */
	private Exponential callDuration;
	/**
	 * Remaining simulation time
	 */
	private long simulationDuration;

	/**
	 * Creates a primary traffic generator thread associated with node n
	 * @param n
	 * @param alpha number of calls per unit time
	 * @param meanCallDuration expected value for duration of a call
	 * @param simulationDuration duration of the simulation in nanoseconds
	 */
	public PrimaryTrafficGeneratorThread(Node n, int alpha, double meanCallDuration, int simulationDuration)
	{
		interArrival = new Exponential((double)alpha, SimulationRunner.randEngine);
		callDuration = new Exponential((double)1/meanCallDuration, SimulationRunner.randEngine);
		this.simulationDuration = simulationDuration;
		this.n = n;
		if(runner==null){
            runner=new Thread(this);                //Create the thread
            runner.start();							//Start the thread: This method will call run method below
        }
	}
	
	/**
	 * Finds a free frequnecy and occupies it. This method is synchronized. That
	 * is only one thread at a time can run it
	 * @return ID of the occupied frequency
	 */
	private synchronized int generateTraffic()
	{
		int freq = SimulationRunner.wc.freeFrequency();	//Find a free frequency
		if(freq==WirelessChannel.NOFREEFREQ)			//If there is no available frequency
			return freq;								//Return immediately
		SimulationRunner.wc.occupyFrequency(freq, n);	//Occupy the frequency
		return freq;									//Return its ID
	}
	
	/**
	 * Main thread operation
	 */
	@Override
	public void run() {
		double time = 0;
		int nanos = 0;
		int millis = 0;
		int freq=0;
		while(simulationDuration>0){
			try {
				time = Math.round(interArrival.nextDouble()*1000);	//Take a random inter arrival time
				simulationDuration-=time;			//Reduce the simulation time for that amount
				if(simulationDuration<0)			//If times up
					break;							//stop simulation
				nanos = (int)time%1000;				//Divide the waiting time into nano
				time/=1000;
				millis = (int)time;					//and milli seconds
				Thread.sleep(millis, nanos);		//Wait for that amount
				
				if((freq = generateTraffic())==WirelessChannel.NOFREEFREQ)	//If no frequency occupied
					continue;						//Wait for another inter arrival time
				
				time = Math.round(callDuration.nextDouble()*1000);	//Take a random call duration
				simulationDuration-=time;			//Reduce the simulation time for that amount
				if(simulationDuration<0){			//if times up
					time=time+simulationDuration;	//Last the call until end of the simulation
				}
				nanos = (int)time%1000;				//Divide the call duration into nano
				time/=1000;
				millis = (int)time;					//and milli seconds
				Thread.sleep(millis, nanos);		//Wait for that amount
				PrimaryTrafficGenerator.lock.lock();//Create a critical section to release occupied frequency
				SimulationRunner.wc.releaseFrequency(freq);	//Release frequency
			} catch (InterruptedException ex) {
				Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ex);
			} finally{
				PrimaryTrafficGenerator.lock.unlock();	//Release the critical section
			}
		}
	}
	
	
}

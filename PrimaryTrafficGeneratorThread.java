/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import cern.jet.random.Exponential;
import java.util.concurrent.locks.ReentrantLock;
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
	 */
	public PrimaryTrafficGeneratorThread(Node n, int alpha, double meanCallDuration)
	{
		interArrival = new Exponential((double)alpha, SimulationRunner.randEngine);
		callDuration = new Exponential((double)1/meanCallDuration, SimulationRunner.randEngine);
		this.n = n;
		if(runner==null){
            runner=new Thread(this);                //Create the thread and start
            runner.start();
        }
	}
	
	private synchronized int generateTraffic()
	{
		int freq = SimulationRunner.wc.freeFrequency();
		if(freq<0)
			return freq;
		SimulationRunner.wc.occupyFrequency(freq, n);
		return freq;
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
				time = Math.round(interArrival.nextDouble()*1000);
				simulationDuration-=time;
				if(simulationDuration<0)
					break;
				nanos = (int)time%1000;
				time/=1000;
				millis = (int)time;
				Thread.sleep(millis, nanos);
				
				if((freq = generateTraffic())<0)
					continue;
				
				time = Math.round(callDuration.nextDouble()*1000);
				simulationDuration-=time;
				if(simulationDuration<0){
					time=time+simulationDuration;
				}
				nanos = (int)time%1000;
				time/=1000;
				millis = (int)time;
				Thread.sleep(millis, nanos);
				PrimaryTrafficGenerator.lock.lock();
				SimulationRunner.wc.releaseFrequency(freq);
			} catch (InterruptedException ex) {
				Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ex);
			} finally{
				PrimaryTrafficGenerator.lock.unlock();
			}
		}
	}
	
	
}

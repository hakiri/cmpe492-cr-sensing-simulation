package MultiThreadedSimulation;

import Animation.DrawCell;
import CommunicationEnvironment.WirelessChannel;
import Nodes.PrimaryTrafficGeneratorNode;
import SimulationRunner.ParetoDistribution;
import SimulationRunner.SimulationRunner;
import cern.jet.random.Exponential;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread responsible for generating an individual primary traffic in the wireless channel.
 */
public class PrimaryTrafficGeneratorThread implements Runnable{
	/**
	 * Associated node to this traffic generator
	 */
	private PrimaryTrafficGeneratorNode n;
	/**
	 * Runner thread
	 */
	private Thread runner=null;
	/**
	 * boolean variable to terminate thread
	 */
	private boolean finished = false;
	/**
	 * Distribution for Poisson traffic inter arrival times
	 */
	private Exponential interArrival;
	/**
	 * Distribution for Poisson traffic call durations
	 */
	private Exponential callDuration;
	/**
	 * Distribution for ON-OFF traffic off durations
	 */
	private ParetoDistribution offDuration;
	/**
	 * Distribution for ON-OFF traffic on durations
	 */
	private ParetoDistribution onDuration;
	private double remainingSimulationDuration;
	
	/**
	 * Creates a primary traffic generator thread associated with node n
	 * @param n					Primary node associated with this thread
	 * @param meanOnDuration	<ul>
	 *								<li><i>If Poisson traffic model:</i> Expected value for duration of a call
	 *									in terms of time units
	 *								<li><i>If ON-OFF traffic model:</i> Expected value for duration of a ON
	 *									period in terms of time units
	 *							</ul>
	 * @param meanOffDuration	<ul>
	 *								<li><i>If Poisson traffic model:</i> Mean number of calls per unit time
	 *								<li><i>If ON-OFF traffic model:</i> Mean OFF period duration of a node in terms of time units
	 *							</ul>
	 */
	public PrimaryTrafficGeneratorThread(PrimaryTrafficGeneratorNode n, double meanOnDuration, double meanOffDuration, double simulationDuration)
	{
		remainingSimulationDuration = simulationDuration*SimulationRunner.args.getTimeUnit();
		if(SimulationRunner.wc.getTrafficModel() == WirelessChannel.POISSON){
			interArrival = new Exponential(meanOffDuration, SimulationRunner.randEngine);
			callDuration = new Exponential(1.0/meanOnDuration, SimulationRunner.randEngine);
			onDuration = null;
			offDuration = null;
		}
		else if(SimulationRunner.wc.getTrafficModel() == WirelessChannel.ON_OFF){
			interArrival = null;
			callDuration = null;
			onDuration = new ParetoDistribution(meanOnDuration, SimulationRunner.randEngine);
			offDuration = new ParetoDistribution(meanOffDuration, SimulationRunner.randEngine);
		}
		this.n = n;
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
		long time = 0;
		int freq=0;
		DrawCell.paintPrimaryNode(n, Color.BLACK);
		while(!finished && remainingSimulationDuration > 0.0){
			double previousOffDuration = interArrival();
			
			if(finished)
				break;
			
			freq = generateTraffic(previousOffDuration);

			if(freq == WirelessChannel.NOFREEFREQ)	//If no frequency occupied
				continue;						//Wait for another inter arrival time
			
			if(finished)
				break;
			
			communicate();
			
			releaseFreq(freq);
			n.setCommunicationFreq(-1);
		}
		finished=true;
	}
	
	private double interArrival()
	{
		double previousOffDuration;
		long time = Math.round(previousOffDuration = nextOffDuration());
		remainingSimulationDuration -= time;
		if(remainingSimulationDuration <= 0)
			finished = true;
		try{
			Thread.sleep(time);		//Wait for that amount
		}
		catch(InterruptedException ie){
			Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ie);
		}
		return previousOffDuration;
	}
	
	private int generateTraffic(double previousOffDuration)
	{
		try {
			PrimaryTrafficGenerator.y.acquire();
		} catch (InterruptedException ex) {
			Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ex);
		}
		PrimaryTrafficGenerator.writerCount++;
		if(PrimaryTrafficGenerator.writerCount==1){
			try {
				PrimaryTrafficGenerator.readLock.acquire();
			} catch (InterruptedException ex) {
				Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		PrimaryTrafficGenerator.y.release();
		try {
			PrimaryTrafficGenerator.writeLock.acquire();
		} catch (InterruptedException ex) {
			Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ex);
		}
		int freq = n.generateTraffic(previousOffDuration);		//Get a free frequency from the channel
		PrimaryTrafficGenerator.writeLock.release();
		try {
			PrimaryTrafficGenerator.y.acquire();
		} catch (InterruptedException ex) {
			Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ex);
		}
		PrimaryTrafficGenerator.writerCount--;
		if(PrimaryTrafficGenerator.writerCount==0)
			PrimaryTrafficGenerator.readLock.release();
		PrimaryTrafficGenerator.y.release();
		return freq;
	}
	
	private void communicate()
	{
		DrawCell.paintPrimaryNode(n, Color.RED);
		double commDur = nextOnDuration();
		if(commDur > remainingSimulationDuration){
			commDur = remainingSimulationDuration;
			remainingSimulationDuration = -1;
		}
		else
			remainingSimulationDuration -= commDur;
		if(remainingSimulationDuration <= 0)
			finished = true;
		long time = Math.round(commDur);	//Take a random call duration
		n.incrementTotalCommunicationDuration(commDur/SimulationRunner.args.getTimeUnit());//WirelessChannel.unitTime);

		try{
			if(time > 1)
				Thread.sleep(time);		//Wait for that amount
		}
		catch(InterruptedException ie){
			Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ie);
		}
	}
	
	private void releaseFreq(int freq)
	{
		try {
			PrimaryTrafficGenerator.y.acquire();
		} catch (InterruptedException ex) {
			Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ex);
		}
		PrimaryTrafficGenerator.writerCount++;
		if(PrimaryTrafficGenerator.writerCount==1){
			try {
				PrimaryTrafficGenerator.readLock.acquire();
			} catch (InterruptedException ex) {
				Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		PrimaryTrafficGenerator.y.release();
		try {
			PrimaryTrafficGenerator.writeLock.acquire();
		} catch (InterruptedException ex) {
			Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ex);
		}
		SimulationRunner.wc.releaseFrequency(freq,n);	//Release frequency
		PrimaryTrafficGenerator.writeLock.release();
		try {
			PrimaryTrafficGenerator.y.acquire();
		} catch (InterruptedException ex) {
			Logger.getLogger(PrimaryTrafficGeneratorThread.class.getName()).log(Level.SEVERE, null, ex);
		}
		PrimaryTrafficGenerator.writerCount--;
		if(PrimaryTrafficGenerator.writerCount==0)
			PrimaryTrafficGenerator.readLock.release();
		PrimaryTrafficGenerator.y.release();
		DrawCell.paintPrimaryNode(n, Color.BLACK);
	}
	
	private double nextOnDuration()
	{
		if(SimulationRunner.wc.getTrafficModel() == WirelessChannel.POISSON){
			double val = callDuration.nextDouble();
			return val*WirelessChannel.unitTime*60000;
		}
		else if(SimulationRunner.wc.getTrafficModel() == WirelessChannel.ON_OFF){
			double val = onDuration.nextDouble();
			return val*WirelessChannel.unitTime*60000;
		}
		else
			return 0.0;
	}
	
	private double nextOffDuration()
	{
		if(SimulationRunner.wc.getTrafficModel() == WirelessChannel.POISSON)
			return interArrival.nextDouble()*WirelessChannel.unitTime*3600000;
		else if(SimulationRunner.wc.getTrafficModel() == WirelessChannel.ON_OFF){
			double val = offDuration.nextDouble();
			return val*WirelessChannel.unitTime*3600000;
		}
		else
			return 0.0;
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

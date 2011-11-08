package DESSimulation;

import CommunicationEnvironment.WirelessChannel;
import DES.Event;
import DES.Scheduler.EventHandle;
import DES.SimEnt;
import Nodes.PrimaryTrafficGeneratorNode;
import SimulationRunner.ParetoDistribution;
import SimulationRunner.SimulationRunner;
import cern.jet.random.Exponential;

/**
 * This class handles events related to one primary node's traffic generation.
 */
public class PrimaryTrafficGeneratorSimEnt extends SimEnt{

	/**
	 * Event class to start communication of a primary node
	 */
	private static class CommunicationStartEvent implements Event{

		@Override
		public void entering(SimEnt locale) {
			//no op
		}
		
	}
	
	/**
	 * Event class to end communication of a primary node
	 */
	private static class CommunicationEndEvent implements Event{

		@Override
		public void entering(SimEnt locale) {
			//no op
		}
		
	}
	
	/**
	 * Event class to start off period of a primary node
	 */
	private static class WaitEvent implements Event{

		@Override
		public void entering(SimEnt locale) {
			//no op
		}

	}
	
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
	
	private final static CommunicationStartEvent commStart = new CommunicationStartEvent();
	private final static CommunicationEndEvent commEnd = new CommunicationEndEvent();
	private final static WaitEvent wait = new WaitEvent();
	
	private PrimaryTrafficGeneratorNode node;
	private double previousOffDuration;
	
	/**
	 * Creates a simulation entity for the given node with given probabilistic mean values.
	 * It checks the traffic model from DESPrimaryTrafficGenerator class.
	 * @param node				Node associated to this simulation entity
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
	public PrimaryTrafficGeneratorSimEnt(PrimaryTrafficGeneratorNode node, double meanOnDuration, double meanOffDuration) {
		super();
		previousOffDuration = 0.0;
		this.node = node;
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
	}

	/**
	 * Starts the simulation
	 */
	void start() {
		send(this,wait,0.0);
	}
	
	/**
	 * Main event handler
	 * @param src	Source of the event
	 * @param ev	Occured event
	 */
	@Override
	public void recv(SimEnt src, Event ev) {
		if(ev instanceof WaitEvent){
			send(this,commStart,previousOffDuration = nextOffDuration());
		}
		else if(ev instanceof CommunicationStartEvent){
			int freq = node.generateTraffic(previousOffDuration);
			if(freq == WirelessChannel.NOFREEFREQ){
				send(this, wait, 0.0);
			}
			else{
				double commDur = nextOnDuration();
				double remainingDur = SimulationRunner.crDesScheduler.getRemainingSimulationDuration();
				if(commDur > remainingDur)
					commDur = remainingDur;
				node.incrementTotalCommunicationDuration(commDur/(double)WirelessChannel.unitTime);
				
				send(this, commEnd, commDur);
			}
		}
		else if(ev instanceof CommunicationEndEvent){
			SimulationRunner.wc.releaseFrequency(node.getCommunicationFreq(), node);
			send(this, wait, 0.0);
		}
	}

	@Override
	public void deliveryAck(EventHandle h) {
		//no op
	}
	
	/**
	 * Finds the next on duration according to the traffic model
	 * @return	On duration
	 */
	private double nextOnDuration()
	{
		if(SimulationRunner.wc.getTrafficModel() == WirelessChannel.POISSON)
			return callDuration.nextDouble()*WirelessChannel.unitTime*60000;
		else if(SimulationRunner.wc.getTrafficModel() == WirelessChannel.ON_OFF){
			double val = onDuration.nextDouble();
			return val*WirelessChannel.unitTime*60000;
		}
		else
			return 0.0;
	}
	
	/**
	 * Finds the next off duration accoriding to the traffic model
	 * @return	Off duration
	 */
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
	
}

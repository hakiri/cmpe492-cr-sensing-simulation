package CommunicationEnvironment;

import Heuristic.ATLHueristic;
import Nodes.CRNode;
import Nodes.Node;
import Nodes.PrimaryTrafficGeneratorNode;
import SimulationRunner.SimulationRunner;
import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class handles main channel operations, such as SNR and SINR calculations.
 * It also provides frequencies to primary and CR nodes.
 */
public class WirelessChannel {
	/**
	 * Integer value for Primary node
	 */
	public static final int PRIMARY = 0;
	/**
	 * Integer value for CR node
	 */
	public static final int CR = 1;
	/**
	 * List of frequencies in the channel. ArrayList holds two nodes, first element
	 * Primary node second element CR node, which use the channel.
	 */
	private HashMap<Integer,ArrayList<Node>> frequencies;
	/**
	 * There is no available frequency right now
	 */
	public static final int NOFREEFREQ = -1;
	/**
	 * Transmitter power
	 */
	public double Ptx = -10.0;	//TODO take as parameter
	/**
	 * Uniform distribution to accomplish frequency assignments
	 */
	public Uniform uniform = null;
	private Normal transmitNormal = null;
	private Normal noiseNormal = null;
	private static final double l0 = 38.4;
	private static final double alpha = 35;
	private static final double variance = 8;
	private double noiseFloor = -100.0;
	private double meanOnDuration;
	private double meanOffDuration;
	private int trafficModel;
	private double interferenceDistance;
	/**
	 * Poisson traffic model
	 */
	public static final int POISSON = 0;
	/**
	 * On-Off traffic model
	 */
	public static final int ON_OFF = 1;
	/**
	 * Scale of msec during animation
	 */
	public static double unitTime;
	/**
	 * Bandwidth of each of the available channels 
	 */
	public static int bandwidth ;
    /**
     * Probabilities of frequency intervals. When a primary user wants to talk, he gets a 
     * free channel to communicate(if possible), and the probability of which channel that he gets is 
     * determined by this probability array.
     */
    public ArrayList<Double> probsOfFreqIntervals = null;
    /**
     * This array keeps the boundaries of frequency intervals with respect to 
     * their probabilities about how they are likely to come when we get a random free frequency
     */
	public ArrayList<Integer> indexesOfFreqIntervals = null;
    /**
     * This array keeps the total number of usages of each frequency by primary users.
     */
    public ArrayList<Integer> usageOfFreqs = new ArrayList<Integer>();
    /**
     * This array keeps the total number of usages of each frequency intervals by primary users.
     */
    public ArrayList<Integer> usageOfFreqsInIntervals = new ArrayList<Integer>();
	
    /**
	 * Creates a wireless channel with the given model.
	 * It creates numberOfFrequencies amount frequency.
	 * Initially there is no node in the channel.
	 * @param numberOfFrequencies	Number of frequencies in the channel
	 * @param transmitPower				max SNR value of the channel
	 * @param meanOffDuration 	<ul>
	 *								<li><i>If Poisson traffic model:</i> Mean number of calls per unit time
	 *								<li><i>If ON-OFF traffic model:</i> Mean OFF period duration of a node in terms of time units
	 *							</ul>
	 * @param meanOnDuration	<ul>
	 *								<li><i>If Poisson traffic model:</i> Expected value for duration of a call
	 *									in terms of time units
	 *								<li><i>If ON-OFF traffic model:</i> Expected value for duration of a ON
	 *									period in terms of time units
	 *							</ul>
	 * @param trafficModel		Model for traffic generation
	 * @param unitTime			Scale of msec during animation
	 * @param bandwidth			Bandwidth of each of the available channels 
	 */
	public WirelessChannel(int numberOfFrequencies, double transmitPower,
                double meanOffDuration, double meanOnDuration, int trafficModel, double unitTime,int bandwidth)
	{
		frequencies = new HashMap<Integer, ArrayList<Node>>();	//Create a hash table for frequencies with integer keys and 
													//node values which occupied the frequency
		for(int i=0;i<numberOfFrequencies;i++){
			ArrayList<Node> temp = new ArrayList<Node>();
			temp.add(null);
			temp.add(null);
			frequencies.put(i, temp);				//Create frequencies
            usageOfFreqs.add(0);
		}
		uniform = new Uniform(SimulationRunner.randEngine);			//Create Uniform distribution to select number of frequencies and their values
		this.transmitNormal = new Normal(0, variance, SimulationRunner.randEngine);
		this.noiseNormal = new Normal(0, SimulationRunner.args.getNoiseStdDev(), SimulationRunner.randEngine);
		this.noiseFloor = SimulationRunner.args.getNoiseFloor();
		this.meanOffDuration = meanOffDuration;
		this.meanOnDuration = meanOnDuration;
		this.trafficModel = trafficModel;
		this.Ptx = transmitPower;
		interferenceDistance = SimulationRunner.args.getInterferenceDistance();
		WirelessChannel.unitTime = unitTime;
        WirelessChannel.bandwidth = bandwidth;
        initializeFreqIntervals();
	}
	
	/**
	 * Finds an received power of a CR node from a primary node.
	 * @param sensor	Node to find received power
	 * @param frequency Frequency to which the sensor senses
	 * @return SNR value
	 */
	public double generateReceivedPower(Node sensor, int frequency)
	{
		double distance = 0;
		if(frequencies.get(frequency).get(PRIMARY) !=null){	//If the frequency is occupied
			distance = sensor.getPosition().distance(frequencies.get(frequency).get(PRIMARY).getPosition());	//Find distance
			double prx;
			if(distance >= 80)
				prx = Ptx - l0 - alpha*Math.log10(distance / 1000.0) + transmitNormal.nextDouble();
			else
				prx = Ptx + transmitNormal.nextDouble();
			return prx;			//between occupier and sensor and compute
		}										//attenuation based on this distance
		return noiseFloor + noiseNormal.nextDouble();
	}
	
	/**
     * Finds an received power for a given distance.
     * @param distance Distance between the nodes in terms meters.
     * @return SNR value of the signal transmitted by transmitter node.
     */
    public double generateReceivedPower(double distance)
    {
		double prx = Ptx;
		if(distance >= 80)
			prx = Ptx - l0 - alpha*Math.log10(distance / 1000.0);
		return prx + transmitNormal.nextDouble();
    }
	
	/**
	 * Finds channel capacity between two nodes.
	 * @param transmitter	Node transmitting the signal
	 * @param receiver		Node receiving the signal
	 * @param freq			Frequency which will be used during the communication between transmitter and receiver
	 * @return Bandwidth in terms of bps
	 */
	public double currentChannelCapacity(Node transmitter, Node receiver, int freq)
	{	//TODO Check SINR calculation
		double interference_db = 0;
		double distance = transmitter.getPosition().distance(receiver.getPosition());
		double signal_db = generateReceivedPower(distance);
		double noise_db = noiseFloor + noiseNormal.nextDouble();
		double interferencePower;
		if(frequencies.get(freq).get(PRIMARY) != null){	//If the frequency is occupied
			interference_db = generateReceivedPower(receiver, freq);
			interferencePower = dbmToMag(interference_db);
		}
		else
			interferencePower = 0.0;
		double noise_Power = dbmToMag(noise_db);
		double signalPower = dbmToMag(signal_db);
		double mag = signalPower / (interferencePower + noise_Power);
		double capacity = Math.log1p(mag) / Math.log(2);
		capacity *= bandwidth;
		return capacity;

	}
	
	/**
	 * Lets a primary user to occupy a frequency. That is,
	 * the primary user starts transmission.
	 * @param frequency In which the user transmits
	 * @param n			Node that occupies the frequency
	 */
	public void occupyFrequency(int frequency, Node n)
	{
		if(n instanceof CRNode){
			if(frequencies.get(frequency).get(CR) == null)	//If the frequency is not occupied
				frequencies.get(frequency).set(CR, n);		//Assign it to the node n
		}
		else if(n instanceof PrimaryTrafficGeneratorNode){
			if(frequencies.get(frequency).get(PRIMARY) == null)	//If the frequency is not occupied
				frequencies.get(frequency).set(PRIMARY, n);		//Assign it to the node n
		}
	}
	
	/**
	 * Re turns whether a frequency is occupied by a given type of node
	 * @param freq		ID of frequency
	 * @param nodeType	Type node, CR or Primary
	 * @return			<ul>
	 *						<li><b><i>True</i></b> if given frequency is occupied by a given type of node</li>
	 *						<li><b><i>False</i></b> otherwise</li>
	 *					</ul>
	 */
	public boolean isOccupied(int freq, int nodeType)
	{
		return frequencies.get(freq).get(nodeType) != null;
	}
	
	/**
	 * Lets a primary user to release a frequency. That is,
	 * the primary user finishes transmission.
	 * @param frequency		In which the user transmits
	 * @param n				Node which releases the frequency
	 */
	public void releaseFrequency(int frequency, Node n)
	{
		if(n instanceof CRNode){
			frequencies.get(frequency).set(CR, null);		//Release the frequency by deleting its occupier
		}
		else{
			frequencies.get(frequency).set(PRIMARY, null);	//Release the frequency by deleting its occupier
		}
	}
	
	/**
	 * Finds a free frequency and returns its ID
	 * @return ID of frequency. -1 if no free frequency
	 */
	public int freeFrequency()
	{
		ArrayList<Integer> free = new ArrayList<Integer>();
		ArrayList<Integer> indexes = freqInterval();
        if(indexes.get(0) == -1)
            return NOFREEFREQ;
        else{
            for(int i=indexes.get(0);i<indexes.get(1);i++){	//Search for a free frequency
                if(frequencies.get(i).get(PRIMARY) == null){		//If no primary node occupied it
                    free.add(i);//return i;
                }
            }
            return free.get(uniform.nextIntFromTo(0, free.size()-1));
        }
	}
	
    /**
     * Finds all available frequency intervals and selects an interval randomly with respect to the 
     * frequency interval probabilities.
     * @return  <ul>
     *              <li>Boundaries of the selected frequency interval, if there are some available frequencies. </li>
     *              <li>An arraylist with two elements in it and each element is '-1', otherwise.</li>
     *          </ul>
     */
    public ArrayList<Integer> freqInterval(){
        ArrayList<Integer> freqInterval = new ArrayList<Integer>();
        ArrayList<Integer> availableFreqIntervals = new ArrayList<Integer>();
        ArrayList<Double> cumulativeProbsOfFreq = new ArrayList<Double>();
        int temp=0,iStart=-1,iEnd=-1; 
        double totalProb=0.0,expandCoeff,cumProb=0.0,random;
        //finds available frequency intervals and add their probability value to the totalProb
        for(int i=0;i<probsOfFreqIntervals.size();i++){
            for(int j=temp;j<indexesOfFreqIntervals.get(i);j++){
                if(frequencies.get(j).get(PRIMARY) == null){
                    totalProb+=probsOfFreqIntervals.get(i);
                    availableFreqIntervals.add(i);
                    break;
                }
            }
            temp = indexesOfFreqIntervals.get(i);
        }
        if(totalProb == 0.0){
            freqInterval.add(iStart);
            freqInterval.add(iEnd);
            return freqInterval;
        }
        else{
            expandCoeff = 1/totalProb;
            for(int i=0;i<availableFreqIntervals.size();i++){
                cumProb += probsOfFreqIntervals.get(availableFreqIntervals.get(i))*expandCoeff;
                cumulativeProbsOfFreq.add(cumProb);
            }
            random = uniform.nextDouble();
            for(int i=0;i<cumulativeProbsOfFreq.size();i++){
                if(random < cumulativeProbsOfFreq.get(i)){
                    usageOfFreqsInIntervals.set(availableFreqIntervals.get(i), usageOfFreqsInIntervals.get(availableFreqIntervals.get(i))+1);
                    if(availableFreqIntervals.get(i) == 0)
                        iStart = 0;
                    else
                        iStart = indexesOfFreqIntervals.get(availableFreqIntervals.get(i)-1);
                    iEnd = indexesOfFreqIntervals.get(availableFreqIntervals.get(i));
                    freqInterval.add(iStart);
                    freqInterval.add(iEnd);
                    return freqInterval;
                }
            }
        }
        freqInterval.add(iStart);
        freqInterval.add(iEnd);
        return freqInterval;
    }
    
    /**
     * Assigns probabilities to the frequency intervals, and initializes other arrays related with 
     * these frequency intervals
     */
    public final void initializeFreqIntervals(){
        int temp=0;
        probsOfFreqIntervals = new ArrayList<Double>();
        indexesOfFreqIntervals = new ArrayList<Integer>();
        probsOfFreqIntervals.add(0.2);  //probability of first frequency interval
        probsOfFreqIntervals.add(0.6);  //probability of second frequency interval
        probsOfFreqIntervals.add(0.2);  //probability of third frequency interval
        temp = (int)Math.ceil(frequencies.size()/probsOfFreqIntervals.size());
        for(int i=0;i<probsOfFreqIntervals.size()-1;i++){
            indexesOfFreqIntervals.add(temp*i+temp);
            usageOfFreqsInIntervals.add(0);
        }
        indexesOfFreqIntervals.add(frequencies.size());
        usageOfFreqsInIntervals.add(0);
    }
    
	/**
	 * Checks whether there is a primary user using the given frequency at a distance
	 * from which it can cause interference to the secondary users.
	 * @param freq	Frequency to be checked
	 * @param zone	Zone to be checked
	 * @return <ul>
	 *				<li><i>False </i> if there is primary user which can cause interference
	 *				<li><i>True </i> otherwise
	 *		   </ul>
	 */
	public boolean isChannelAvailable(int freq, int zone)
	{
		Node primary = frequencies.get(freq).get(PRIMARY);
		if(primary == null)
			return true;
		Point2D.Double primaryPosition = primary.getPosition();
		for(int i=0;i<ATLHueristic.yij.get(zone).size();i++){
			int crInZone = ATLHueristic.yij.get(zone).get(i);
			if(primaryPosition.distance(SimulationRunner.crBase.getCRNode(crInZone).getPosition()) < interferenceDistance)
				return false;
		}
		return true;
	}
	
	/**
	 * Computes the magnitude of a given dB
	 * @param db dB value to be computed
	 * @return magnitude equivalent of db
	 */
	public static double dbmToMag(double db)
	{
		return Math.pow(10, (db / 10));
	}
	
	/**
	 * Computes the dB of a given magnitude
	 * @param mag Magnitude value to be computed
	 * @return dB equivalent of mag
	 */
	public static double magTodbm(double mag)
	{
		return 10.0*Math.log10(mag);
	}
	
	/**
	 * Computes the number of frequencies
	 * @return Number of frequencies
	 */
	public int numberOfFreq()
	{
		return frequencies.size();
	}

	/**
	 * Returns the mean on (active) duration of all (both primary & secondary)
	 * users.
	 * @return Mean on duration
	 */
	public double getMeanOnDuration() {
		return meanOnDuration;
	}
	
	/**
	 * Returns the mean off (inactive) duration of all (both primary & secondary)
	 * users.
	 * @return Mean off duration
	 */
	public double getMeanOffDuration() {
		return meanOffDuration;
	}

	/**
	 * Returns the traffic model of all (both primary & secondary) users.
	 * @return Traffic Model
	 */
	public int getTrafficModel() {
		return trafficModel;
	}
	
	/**
	 * Returns both CR node and primary node using a given frequency
	 * @param freq	ID of frequency to find its current users
	 * @return		Arraylist that contains current users of the given frequency
	 */
	public ArrayList<Node> getFreq(int freq)
	{
		return frequencies.get(freq);
	}
}

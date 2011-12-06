package CommunicationEnvironment;

import Nodes.CRNode;
import Nodes.Node;
import Nodes.PrimaryTrafficGeneratorNode;
import SimulationRunner.SimulationRunner;
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
	 * List of nodes using channel
	 */
	private ArrayList<Node> registeredNodes;
	/**
	 * List of frequencies in the channel. ArrayList holds two nodes, first element
	 * Primary node second element CR node, which use the channel.
	 */
	private HashMap<Integer,ArrayList<Node>> frequencies;
	/**
	 * 0 for AWGN, 1 for Rayleigh, 2 for Lognormal
	 */
	int channelModel;
	/**
	 * Simple Channel Model such that SNR = maxSNR / e^f(distance)
	 */
	public static final int SIMPLECH = 0;
	/**
	 * Lognormal channel model
	 */
	public static final int LOGNORMALCH = 1;
	/**
	 * There is no available frequency right now
	 */
	public static final int NOFREEFREQ = -1;
	/**
	 * Max SNR value of the channel
	 */
	public double maxSNR;
	/**
	 * Minimum SINR threshold to be able to communicate
	 */
	public double sinrThreshold;
	/**
	 * Uniform distribution to accomplish frequency assignments
	 */
	public Uniform uniform = null;
	
	private double meanOnDuration;
	private double meanOffDuration;
	private int trafficModel;
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
    
    public ArrayList<Double> probsOfFreqIntervals = null;
	public ArrayList<Integer> indexesOfFreqIntervals = null;
    public ArrayList<Integer> usageOfFreqs = new ArrayList<Integer>();
    public ArrayList<Integer> usageOfFreqsInIntervals = new ArrayList<Integer>();
	/**
	 * Creates a wireless channel with the given model.
	 * It creates numberOfFrequencies amount frequency.
	 * Initially there is no node in the channel.
	 * @param channelModel			0 for Simple ch., 1 for Lognormal ch.
	 * @param numberOfFrequencies	Number of frequencies in the channel
	 * @param maxSNR				max SNR value of the channel
	 * @param sinrThreshold			SINR threshold for CR nodes to be able to communicate without collision
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
	public WirelessChannel(int channelModel, int numberOfFrequencies, double maxSNR, double sinrThreshold,
                double meanOffDuration, double meanOnDuration, int trafficModel, double unitTime,int bandwidth)
	{
		registeredNodes = new ArrayList<Node>();	//Create an empty list for registered nodes
		frequencies = new HashMap<Integer, ArrayList<Node>>();	//Create a hash table for frequencies with integer keys and 
													//node values which occupied the frequency
		for(int i=0;i<numberOfFrequencies;i++){
			ArrayList<Node> temp = new ArrayList<Node>();
			temp.add(null);
			temp.add(null);
			frequencies.put(i, temp);				//Create frequencies
            usageOfFreqs.add(0);
		}
		this.channelModel = channelModel;			//Set channel model
		this.maxSNR = maxSNR;						//Sets max SNR value
		this.sinrThreshold = sinrThreshold;
		uniform = new Uniform(SimulationRunner.randEngine);			//Create Uniform distribution to select number of frequencies and their values
		this.meanOffDuration = meanOffDuration;
		this.meanOnDuration = meanOnDuration;
		this.trafficModel = trafficModel;
		WirelessChannel.unitTime = unitTime;
        WirelessChannel.bandwidth = bandwidth;
        initializeFreqIntervals();
	}
	
	/**
	 * Registers a node to the channel.
	 * @param n node that will be registered
	 */
	public void registerNode(Node n)
	{
		registeredNodes.add(n);						//Add the node to the registered nodes
	}
	
	/**
	 * Finds an SNR value according to the channel model.
	 * @param sensor	Node to assign SNR value
	 * @param frequency Frequency to which the sensor senses
	 * @return SNR value
	 */
	public double generateSNR(Node sensor, int frequency)
	{
		if(channelModel==SIMPLECH){
			double distance = 0;
			if(frequencies.get(frequency).get(PRIMARY) !=null){	//If the frequency is occupied
				distance = sensor.getPosition().distance(frequencies.get(frequency).get(PRIMARY).getPosition());	//Find distance
				return maxSNR/Math.exp(0.12*distance);							//between occupier and sensor and compute
			}																	//attenuation based on this distance
		}
		if(channelModel==LOGNORMALCH){	//NOT SUPPORTED YET
			return 0;
		}
		return 0;
	}
    
    /**
     * Finds an SNR value according to the channel model for a given distance.
     * @param distance Distance between the nodes.
     * @return SNR value of the signal transmitted by transmitter node.
     */
    public double generateSNR(double distance)
    {
        return maxSNR/Math.exp(0.12*distance);
    }
    
    /**
     * Finds an INR threshold value according to the SNR value for a zone.
     * @param zoneId Id of zone
     * @return INR threshold
     */
    public double getInrThreshold(int zoneId){
        double tr = WirelessChannel.magTodb(WirelessChannel.dbToMag(SimulationRunner.wc.generateSNR(SimulationRunner.crBase.farthestDistanceInZone(zoneId)) - SimulationRunner.wc.sinrThreshold)-1);
        return tr < 0.0 ? 0.0 : tr;
    }
	
	/**
	 * Finds an SNR value according to the channel model.
	 * @param transmitter	Node transmitting the signal
	 * @param receiver		Node to assign SNR value
	 * @param freq			Frequency which will be used during the communication between transmitter and receiver
	 * @return SNR value at receiver caused by transmitter
	 */
	public double generateSINR(Node transmitter, Node receiver, int freq)
	{
		double inrdb = generateSNR(receiver, freq);
		if(channelModel==SIMPLECH){
			double distance = transmitter.getPosition().distance(receiver.getPosition());
			double snrdb = maxSNR/Math.exp(0.12*distance);
			if(inrdb==0)
				return snrdb;
			inrdb = magTodb(dbToMag(inrdb)+1);
			return snrdb - inrdb;
		}
		if(channelModel==LOGNORMALCH){	//NOT SUPPORTED YET
			return 0;
		}
		return 0;
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
	
    public ArrayList<Integer> freqInterval(){
        ArrayList<Integer> freqInterval = new ArrayList<Integer>();
        ArrayList<Integer> availableFreqIntervals = new ArrayList<Integer>();
        ArrayList<Double> cumulativeProbsOfFreq = new ArrayList<Double>();
        int temp=0,iStart=-1,iEnd=-1; 
        double totalProb=0.0,expandCoeff,cumProb=0.0,random;
        
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
    
    public final void initializeFreqIntervals(){
        int temp=0;
        probsOfFreqIntervals = new ArrayList<Double>();
        indexesOfFreqIntervals = new ArrayList<Integer>();
        probsOfFreqIntervals.add(0.2);
        probsOfFreqIntervals.add(0.6);
        probsOfFreqIntervals.add(0.2);
        temp = (int)Math.ceil(frequencies.size()/probsOfFreqIntervals.size());
        for(int i=0;i<probsOfFreqIntervals.size()-1;i++){
            indexesOfFreqIntervals.add(temp*i+temp);
            usageOfFreqsInIntervals.add(0);
        }
        indexesOfFreqIntervals.add(frequencies.size());
        usageOfFreqsInIntervals.add(0);
    }
    
	/**
	 * Computes the magnitude of a given dB
	 * @param db dB value to be computed
	 * @return magnitude equivalent of db
	 */
	public static double dbToMag(double db)
	{
		return Math.pow(10, (db/20));
	}
	
	/**
	 * Computes the dB of a given magnitude
	 * @param mag Magnitude value to be computed
	 * @return dB equivalent of mag
	 */
	public static double magTodb(double mag)
	{
		return 20.0*Math.log10(mag);
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

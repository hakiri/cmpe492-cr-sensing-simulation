package firstproject;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;


public class WirelessChannel {
	/**
	 * List of nodes using channel
	 */
	private ArrayList<Node> registeredNodes;
	/**
	 * List of frequencies in the channel
	 */
	private HashMap<Integer,Node> frequencies;
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
	double maxSNR;
	
	/**
	 * Creates a wireless channel with the given model.
	 * It creates numberOfFrequencies amount frequncy.
	 * Initially there is no node in the channel.
	 * @param channelModel: 0 for Simple ch., 1 for Lognormal ch.
	 * @param numberOfFrequencies 
	 * @param maxSNR max SNR value of the channel
	 */
	public WirelessChannel(int channelModel, int numberOfFrequencies, double maxSNR)
	{
		registeredNodes = new ArrayList<Node>();	//Create an empty list for registered nodes
		frequencies = new HashMap<Integer, Node>();	//Create a hash table for frequencies with integer keys and 
													//node values which occupied the frequency
		for(int i=0;i<numberOfFrequencies;i++){
			frequencies.put(i, null);				//Create frequencies
		}
		this.channelModel = channelModel;			//Set channel model
		this.maxSNR = maxSNR;						//Sets max SNR value
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
	 * Finds an snr value according to the channel model.
	 * @param sensor Node to assign SNR value
	 * @param frequency Frequency to which the sensor senses
	 * @return snr value
	 */
	public double generateSNR(Node sensor, int frequency)
	{
		if(channelModel==SIMPLECH){
			double distance = 0;
			if(frequencies.get(frequency)!=null){	//If the frequency is occupied
				distance = sensor.getPosition().distance(frequencies.get(frequency).getPosition());	//Find distance
				return maxSNR/Math.exp(0.12*distance);							//between occupier and sensor and compute
			}																	//attenuation based on this distance
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
	 * @param n Node that occupies the frequency
	 */
	public void occupyFrequency(int frequency, Node n)
	{
		if(frequencies.get(frequency)==null)	//If the frequency is not occupied
			frequencies.put(frequency, n);		//Assign it to the node n
	}
	
	/**
	 * Lets a primary user to release a frequency. That is,
	 * the primary user finishes transmission.
	 * @param frequency In which the user transmits
	 */
	public void releaseFrequency(int frequency)
	{
		frequencies.put(frequency, null);		//Release the frequency by deleting its occupier
	}
	
	/**
	 * Finds a free frequency and returns its ID
	 * @return ID of frequency. -1 if no free frequency
	 */
	public int freeFrequency()
	{
		for(int i=0;i<frequencies.size();i++){	//Search for a free frequency
			if(frequencies.get(i)==null)		//If no node occupied it
				return i;						//Return its ID
		}
		return NOFREEFREQ;						//Return -1 otherwise
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
	 * Computes the number of frequencies
	 * @return Number of frequencies
	 */
	public int numberOfFreq()
	{
		return frequencies.size();
	}
}

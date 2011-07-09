package firstproject;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;


public class WirelessChannel {
	public static final int PRIMARY = 0;
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
	double maxSNR;
	/**
	 * Minimum SINR threshold to be able to communicate
	 */
	double sinrThreshold;
	
	/**
	 * Creates a wireless channel with the given model.
	 * It creates numberOfFrequencies amount frequency.
	 * Initially there is no node in the channel.
	 * @param channelModel: 0 for Simple ch., 1 for Lognormal ch.
	 * @param numberOfFrequencies 
	 * @param maxSNR max SNR value of the channel
	 */
	public WirelessChannel(int channelModel, int numberOfFrequencies, double maxSNR, double sinrThreshold)
	{
		registeredNodes = new ArrayList<Node>();	//Create an empty list for registered nodes
		frequencies = new HashMap<Integer, ArrayList<Node>>();	//Create a hash table for frequencies with integer keys and 
													//node values which occupied the frequency
		for(int i=0;i<numberOfFrequencies;i++){
			ArrayList<Node> temp = new ArrayList<Node>();
			temp.add(null);
			temp.add(null);
			frequencies.put(i, temp);				//Create frequencies
		}
		this.channelModel = channelModel;			//Set channel model
		this.maxSNR = maxSNR;						//Sets max SNR value
		this.sinrThreshold = sinrThreshold;
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
	 * Finds an snr value according to the channel model.
	 * @param transmitter	Node transmitting the signal
	 * @param receiver		Node to assign SNR value
	 * @return snr value at receiver caused by transmitter
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
		if(n.getClass().getName().equals("firstproject.CRNode")){
			if(frequencies.get(frequency).get(CR) == null)	//If the frequency is not occupied
				frequencies.get(frequency).set(CR, n);		//Assign it to the node n
		}
		else{
			if(frequencies.get(frequency).get(PRIMARY) == null)	//If the frequency is not occupied
				frequencies.get(frequency).set(PRIMARY, n);		//Assign it to the node n
		}
	}
	
	/**
	 * Lets a primary user to release a frequency. That is,
	 * the primary user finishes transmission.
	 * @param frequency In which the user transmits
	 */
	public void releaseFrequency(int frequency, Node n)
	{
		if(n.getClass().getName().equals("firstproject.CRNode")){
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
		for(int i=0;i<frequencies.size();i++){	//Search for a free frequency
			if(frequencies.get(i).get(PRIMARY) == null){		//If no primary node occupied it
				free.add(i);//return i;
			}
		}
		if(free.isEmpty())
			return NOFREEFREQ;						//Return -1 otherwise
		return free.get(SimulationRunner.uniform.nextIntFromTo(0, free.size()-1));
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
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import java.util.ArrayList;


public class WirelessChannel {
	/**
	 * List of nodes using channel
	 */
	private ArrayList<Node> registeredNodes;
	/**
	 * List of frequencies in the channel
	 */
	private ArrayList<Boolean> frequencies;
	/**
	 * 0 for AWGN, 1 for Rayleigh, 2 for Lognormal
	 */
	int channelModel;

	/**
	 * Creates a wireless channel with the given model.
	 * It creates numberOfFrequencies amount frequncy.
	 * Initially there is no node in the channel.
	 * @param channelModel: 0 for AWGN ch., 1 for Rayleigh ch., 2 for Lognormal ch.
	 * @param numberOfFrequencies 
	 */
	public WirelessChannel(int channelModel, int numberOfFrequencies)
	{
		registeredNodes = new ArrayList<Node>();
		frequencies = new ArrayList<Boolean>(numberOfFrequencies);
		for(int i=0;i<numberOfFrequencies;i++){
			frequencies.add(Boolean.FALSE);
		}
		this.channelModel = channelModel;
	}
	
	/**
	 * Registers a node to the channel.
	 * @param n node that will be registered
	 */
	public void registerNode(Node n)
	{
		registeredNodes.add(n);
	}
	
	/**
	 * Finds an snr value according to the channel model.
	 * @param sensor Node to assign SNR value
	 * @param frequency Frequency to which the sensor senses
	 * @return snr value
	 */
	public double generateSNR(Node sensor, int frequency)
	{
		double snr=0;
		return snr;
	}
	
	/**
	 * Lets a primary user to occupy a frequency. That is,
	 * the primary user starts transmission.
	 * @param frequency In which the user transmits
	 */
	public void occupyFrequency(int frequency)
	{
		frequencies.set(frequency, Boolean.TRUE);
	}
	
	/**
	 * Lets a primary user to release a frequency. That is,
	 * the primary user finishes transmission.
	 * @param frequency In which the user transmits
	 */
	public void releaseFrequency(int frequency)
	{
		frequencies.set(frequency, Boolean.FALSE);
	}
}

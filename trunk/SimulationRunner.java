package firstproject;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;

public class SimulationRunner {

	/**
	 * Main wireless channel which all types of nodes are accessing
	 */
	public static WirelessChannel wc = new WirelessChannel(WirelessChannel.LognormalCh, 10,5);
	/**
	 * Cognitive radio cell structure
	 */
	public static Cell cell=null;
	private static int RANDOM_ENGINE_SEED = 111211211;
	public static RandomEngine randEngine = new MersenneTwister(RANDOM_ENGINE_SEED);
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		// TODO code application logic here
		
	}
}

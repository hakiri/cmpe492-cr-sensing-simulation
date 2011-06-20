package firstproject;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import java.util.ArrayList;

public class SimulationRunner {

	/**
	 * Main wireless channel which all types of nodes are accessing
	 */
	public static WirelessChannel wc = new WirelessChannel(WirelessChannel.LognormalCh, 10,5);
	/**
	 * Cognitive radio cell structure
	 */
	public static Cell cell = null;
	/**
	 * Primary traffic generator for wireless channel frequencies
	 */
	public static PrimaryTrafficGenerator priTrafGen = null;
	/**
	 * Base station of CR cell
	 */
	public static CRBase crBase = null;
	/**
	 * CR nodes which sense the wireless channel
	 */
	public static ArrayList<CRNode> crNodes = new ArrayList<CRNode>();
	/**
	 * Primary traffic generator nodes which cause traffic in wireless channel
	 */
	public static ArrayList<PrimaryTrafficGeneratorNode> priTrafGenNodes = new ArrayList<PrimaryTrafficGeneratorNode>();
	private final static int RANDOM_ENGINE_SEED = 111211211;
	/**
	 * Random generator for all random number generation operations in the simulation
	 */
	public static RandomEngine randEngine = new MersenneTwister(RANDOM_ENGINE_SEED);
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		// TODO code application logic here
		
	}
}

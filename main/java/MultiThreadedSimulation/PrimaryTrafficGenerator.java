
package MultiThreadedSimulation;

import SimulationRunner.CRNode;
import SimulationRunner.Node;
import SimulationRunner.PrimaryTrafficGeneratorNode;
import SimulationRunner.SimulationRunner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Semaphore;

/**
 * This class holds necessary semaphores and traffic model information 
 * for primary traffic generation. It also logs and prepares the statistics
 * of the simulation related to primary nodes.
 */
public class PrimaryTrafficGenerator {
	/**
	 * Semaphore for writing wireless channel frequencies
	 */
	static Semaphore writeLock = new Semaphore(1);
	/**
	 * Number of writers waiting for accessing the frequencies
	 */
	static int writerCount=0;
	/**
	 * Number of readers waiting for accessing the frequencies
	 */
	static int readerCount=0;
	/**
	 * Semaphore for reading wireless channel frequencies
	 */
	static Semaphore readLock = new Semaphore(1);
	static Semaphore x = new Semaphore(1);
	static Semaphore y = new Semaphore(1);
	static Semaphore z = new Semaphore(1);
	/**
	 * List of nodes and their associated threads
	 */
	private HashMap<PrimaryTrafficGeneratorNode,PrimaryTrafficGeneratorThread> registeredNodes;
	
	/**
	 * Creates a primary traffic generator with no node registered to it
	 * @param unit				Unit of time in milliseconds
	 */
	public PrimaryTrafficGenerator()
	{
		registeredNodes = new HashMap<PrimaryTrafficGeneratorNode, PrimaryTrafficGeneratorThread>();
	}
	
	/**
	 * Registers a node and creates a thread for it to generate traffic
	 * @param n		node to be registered
	 */
	public void registerNode(PrimaryTrafficGeneratorNode n)
	{
		registeredNodes.put(n,new PrimaryTrafficGeneratorThread(n, SimulationRunner.wc.getMeanOnDuration(), SimulationRunner.wc.getMeanOffDuration()));
	}
	
	/**
	 * Terminates all associated threads
	 */
	public void terminateAllThreads()
	{
		for(Node i:registeredNodes.keySet()){
            registeredNodes.get(i).terminate();
        }
	}
	
	/**
	 * Logs and creates statistics of the simulation related to primary nodes
	 * @return	Primary node statistics of the simulation
	 */
	public String[][] logStats()
	{
		return PrimaryTrafficGeneratorNode.logStats(registeredNodes);
	}
}

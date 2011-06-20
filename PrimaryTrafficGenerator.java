
package firstproject;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class PrimaryTrafficGenerator {
	/**
	 * Lock for accessing wireless channel frequencies
	 */
	static ReentrantLock lock = new ReentrantLock();
	/**
	 * List of nodes and their associated threads
	 */
	private HashMap<Node,PrimaryTrafficGeneratorThread> registeredNodes;

	/**
	 * Creates a primary traffic generator with no registered to it
	 */
	public PrimaryTrafficGenerator()
	{
		registeredNodes = new HashMap<Node, PrimaryTrafficGeneratorThread>();
	}
	
	/**
	 * Registers a node and creates a thread for it to generate traffic
	 * @param n node to be registered
	 */
	public void registerNode(Node n)
	{
		registeredNodes.put(n,new PrimaryTrafficGeneratorThread(n, 5, 10,1000000));
	}
}

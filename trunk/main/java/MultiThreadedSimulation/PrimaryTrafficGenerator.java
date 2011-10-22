
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
		int totalCallAttempts = 0, totalDrops = 0;
		double totalCommDur = 0;
		CRNode.writeLogFile("\n-----PRIMARY NODE STATS-----");
		ArrayList<PrimaryTrafficGeneratorNode> list = new ArrayList<PrimaryTrafficGeneratorNode>(registeredNodes.keySet());
		String[][] data = new String[list.size()+1][4];
		Collections.sort(list,new Comparator<PrimaryTrafficGeneratorNode>(){

			@Override
			public int compare(PrimaryTrafficGeneratorNode o1, PrimaryTrafficGeneratorNode o2) {
				return o1.getId() - o2.getId();
			}
			
		});
		
		int i = 0;
		for (PrimaryTrafficGeneratorNode n : list) {
			CRNode.writeLogFile(String.format(Locale.US,"Primary Node: %2d\tNumber of Call Attempts: %d\t\tNumber of Drops: %2d\t\tCommunication Duration: %.2f",
				n.getId(), n.getNumberOfCallAttempts(), n.getNumberOfDrops(), n.getComunicationDuration()));
			totalCallAttempts += n.getNumberOfCallAttempts();
			totalCommDur += n.getComunicationDuration();
			totalDrops += n.getNumberOfDrops();
			
			data[i][0] = String.valueOf(n.getId());
			data[i][1] = String.valueOf(n.getNumberOfCallAttempts());
			data[i][2] = String.valueOf(n.getNumberOfDrops());
			data[i][3] = String.format(Locale.US,"%.2f", n.getComunicationDuration());
			i++;
		}
		CRNode.writeLogFile(String.format(Locale.US,"TOTAL\t\t\t\tNumber of Call Attempts: %d\t\tNumber of Drops: %2d\t\tCommunication Duration: %.2f",
				totalCallAttempts, totalDrops, totalCommDur));
		
		data[i][0] = "Total";
		data[i][1] = String.valueOf(totalCallAttempts);
		data[i][2] = String.valueOf(totalDrops);
		data[i][3] = String.format(Locale.US,"%.2f", totalCommDur);
		return data;
	}
}

package DESSimulation;

import Nodes.PrimaryTrafficGeneratorNode;
import SimulationRunner.SimulationRunner;
import java.util.HashMap;

/**
 * This class holds primary traffic generation model and registered primary nodes.
 * It also logs and prepares the statistics of the simulation related to primary
 * nodes.
 */
public class DESPrimaryTrafficGenerator{
	/**
	 * SimEnt objects associated with each primary traffic generator node
	 */
	private HashMap<PrimaryTrafficGeneratorNode,PrimaryTrafficGeneratorSimEnt> registeredNodes;
	
	/**
	 * Creates a primary traffic generator with no node registered to it and with the
	 * given model.
	 */
	public DESPrimaryTrafficGenerator()
	{
		registeredNodes = new HashMap<PrimaryTrafficGeneratorNode, PrimaryTrafficGeneratorSimEnt>();
	}
	
	/**
	 * Registers a node and creates a Simulation entity for it to generate traffic
	 * @param n		node to be registered
	 */
	public void registerNode(PrimaryTrafficGeneratorNode n)
	{
		registeredNodes.put(n, new PrimaryTrafficGeneratorSimEnt(n, SimulationRunner.wc.getMeanOnDuration(),
																	SimulationRunner.wc.getMeanOffDuration()));
	}

	/**
	 * Starts the simulation
	 */
	public void start()
	{
		for(PrimaryTrafficGeneratorNode n:registeredNodes.keySet())
			registeredNodes.get(n).start();
	}

	/**
	 * Logs and creates statistics of the simulation related to primary nodes
	 * @return	Primary node statistics of the simulation
	 */
	public String[][] logStats()
	{
		return PrimaryTrafficGeneratorNode.logStats(registeredNodes);
	}

	public HashMap<PrimaryTrafficGeneratorNode, PrimaryTrafficGeneratorSimEnt> getRegisteredNodes() {
		return registeredNodes;
	}
}

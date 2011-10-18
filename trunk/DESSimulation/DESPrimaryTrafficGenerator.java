package DESSimulation;

import SimulationRunner.CRNode;
import SimulationRunner.PrimaryTrafficGeneratorNode;
import SimulationRunner.SimulationRunner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

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
	 * Time unit
	 */
	public static int unitTime;
	
	/**
	 * Creates a primary traffic generator with no node registered to it and with the
	 * given model.
	 * @param unit				Unit of time in milliseconds
	 */
	public DESPrimaryTrafficGenerator(int unit)
	{
		unitTime = unit;
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

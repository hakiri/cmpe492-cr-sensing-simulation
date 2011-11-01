package SimulationRunner;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public class PrimaryTrafficGeneratorNode extends Node{
	int communicationFreq;
	int numberOfCallAttempts = 0;
	int numberOfDrops = 0;
	double comunicationDuration = 0.0;
	private double routingRadius = 0.0;
    /**
     * Constructor of the PrimaryTrafficGeneratorNode.
     * @param pos Position of the node
     * @param vel Velocity of the node.
     */
    public PrimaryTrafficGeneratorNode(Point2D.Double pos, double vel,int id) {
       this.position = new Point2D.Double(pos.x, pos.y);
       this.velocity = vel;
       this.id = id;
	   communicationFreq = -1;
    } 
    /**
     * Sets a new position for the primary traffic generator node.
	 * @param	offDuration Previous off duration
     */
    public void setRandomPosition(double offDuration){
		routingRadius = offDuration*2.0;
		routingRadius = routingRadius > 20.0 ? 20.0:routingRadius;
        setPosition(Cell.deployNodeInRouteCircle(this, routingRadius));
    }
	
	/**
	 * Finds a free frequnecy and occupies it. This method is synchronized. That
	 * is only one thread at a time can run it
	 * @param	offDuration Previous off duration
	 * @return ID of the occupied frequency
	 */
	public int generateTraffic(double offDuration)
	{
		numberOfCallAttempts++;
		communicationFreq = SimulationRunner.wc.freeFrequency();    //Find a free frequency
		if(communicationFreq==WirelessChannel.NOFREEFREQ){			//If there is no available frequency
			numberOfDrops++;
			return communicationFreq;								//Return immediately
		}
		setRandomPosition(offDuration);
		SimulationRunner.wc.occupyFrequency(communicationFreq, this);	//Occupy the frequency
		return communicationFreq;									//Return its ID
	}

	public int getCommunicationFreq() {
		return communicationFreq;
	}

	public int getNumberOfCallAttempts() {
		return numberOfCallAttempts;
	}

	public int getNumberOfDrops() {
		return numberOfDrops;
	}

	public double getComunicationDuration() {
		return comunicationDuration;
	}
	
	public void incrementTotalCommunicationDuration(double commDur)
	{
		comunicationDuration += commDur;
	}

	public void setRoutingRadius(double routingRadius) {
		this.routingRadius = routingRadius;
	}

	public double getRoutingRadius() {
		return routingRadius;
	}
	
	public static String[][] logStats(HashMap registeredNodes)
	{
		int totalCallAttempts = 0, totalDrops = 0;
		double totalCommDur = 0;
		CRNode.writeLogFile("\n-----PRIMARY NODE STATS-----");
		
		ArrayList<PrimaryTrafficGeneratorNode> list = new ArrayList<PrimaryTrafficGeneratorNode>(registeredNodes.keySet());
		String[][] data = new String[list.size()+2][3];
		Collections.sort(list,new Comparator <PrimaryTrafficGeneratorNode>(){

			@Override
			public int compare(PrimaryTrafficGeneratorNode o1, PrimaryTrafficGeneratorNode o2) {
				return o1.getId() - o2.getId();
			}
			
		});
		
		int i = 0;
		for (PrimaryTrafficGeneratorNode n : list) {
			double msec = n.getComunicationDuration();
			int hour = (int)(msec/3600000.0);
			msec -= hour*3600000.0;
			int min = (int)(msec/60000.0);
			msec -= min*60000.0;
			int sec = (int)(msec/1000.0);
			msec-= sec*1000.0;
			
			CRNode.writeLogFile(String.format(Locale.US,"Primary Node: %2d\tNumber of Calls: %d\t\tCommunication Duration: %2d:%2d:%2d:%.2f",
				n.getId(), n.getNumberOfCallAttempts() - n.getNumberOfDrops(), hour,min,sec,msec));
			totalCallAttempts += n.getNumberOfCallAttempts();
			totalCommDur += n.getComunicationDuration();
			totalDrops += n.getNumberOfDrops();
			
			data[i][0] = String.valueOf(n.getId());
			data[i][1] = String.valueOf(n.getNumberOfCallAttempts() - n.getNumberOfDrops());
			
			//data[i][2] = String.format(Locale.US,"%.2f", n.getComunicationDuration());
			data[i][2] = String.format(Locale.US,"%2d:%2d:%2d:%.2f", hour,min,sec,msec);
			i++;
		}
		
		double msec = totalCommDur;
		int hour = (int)(msec/3600000.0);
		msec -= hour*3600000.0;
		int min = (int)(msec/60000.0);
		msec -= min*60000.0;
		int sec = (int)(msec/1000.0);
		msec-= sec*1000.0;
		
		CRNode.writeLogFile(String.format(Locale.US,"TOTAL\t\t\t\tNumber of Calls: %d\t\tCommunication Duration: %2d:%2d:%2d:%.2f",
				totalCallAttempts - totalDrops, hour,min,sec,msec));
		
		msec = totalCommDur/(double)list.size();
		hour = (int)(msec/3600000.0);
		msec -= hour*3600000.0;
		min = (int)(msec/60000.0);
		msec -= min*60000.0;
		sec = (int)(msec/1000.0);
		msec-= sec*1000.0;
		
		CRNode.writeLogFile(String.format(Locale.US,"Average\t\t\t\tNumber of Calls: %.2f\t\tCommunication Duration: %2d:%2d:%2d:%.2f",
				(double)(totalCallAttempts - totalDrops) / (double)list.size(), hour,min,sec,msec));
		
		data[i][0] = "Total";
		data[i][1] = String.valueOf(totalCallAttempts - totalDrops);
		data[i][2] = String.format(Locale.US,"%f", totalCommDur);
		i++;
		
		data[i][0] = "Average";
		data[i][1] = String.valueOf((double)(totalCallAttempts - totalDrops)/(double)list.size());
		data[i][2] = String.format(Locale.US,"%2d:%2d:%2d:%.2f", hour,min,sec,msec);
		return data;
	}
}

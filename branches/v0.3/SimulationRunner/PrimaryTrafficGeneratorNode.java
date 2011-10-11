package SimulationRunner;

import java.awt.geom.Point2D;

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
}

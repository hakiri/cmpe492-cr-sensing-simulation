package Nodes;

import CommunicationEnvironment.WirelessChannel;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Locale;
import DES.Event;
import DES.Scheduler;
import DES.SimEnt;
import SimulationRunner.ParetoDistribution;
import SimulationRunner.SimulationRunner;
import cern.jet.random.Exponential;

public class CRNode extends Node{
    
    /**
     * List of frequencies assigned to this node with respect to their snr values.
     */
    private HashMap<Integer,Double> snrValues;
    /**
     * Writer for the log file.
     */
    private static PrintWriter pw = null;
    /**
     * Average snr values of the frequencies.
     */
    private static ArrayList<ArrayList<Double>> averageSnr = null;
    /**
     * Communication frequency of the crnode.
     * If the assigned value is lower than zero this means that 
     * there will be no communication for the crnode. 
     */
    private int communication_frequency = -1;
    /**
     * Frequency list to be listen in the sensing slots.
     */
    private ArrayList<Integer> freq_list_to_listen;
    /**
     * Total number of frames communicated by crnode in the entire simulation time.
     */
    private int numberOfFramesCommunicated = 0;
    /**
     * Total number of collisions between this crnode and primary traffic 
     * generators during the simulation time.
     */
    private int numberOfCollision = 0;
    /**
     * Boolean value that keeps whether a collision happened or not.
     */
    private boolean collisionOccured = false;
	public int numberOfBlocks = 0;
    /**
     * Total number of frames in the simulation.
     */
    private static int totalNumberOfFrames = 0;
    /**
     * Boolean value that shows whether this crnode communicates or not
     */
    private boolean commOrNot = false;
	private boolean readytoComm = false;
    
    public final StartCommunicationEvent startCommEvent ;
	public Scheduler.EventHandle startEventHandle;
    
    public final EndCommunicationEvent endCommEvent ;
	public Scheduler.EventHandle endEventHandle;
    
    private Exponential expoInterarrival;
    
    private Exponential expoCommDuration;
	
    private static ParetoDistribution parOnDuration;
    
    private static ParetoDistribution parOffDuration;
    
	private int numberOfDrops = 0;
	private int numberOfForcedHandoff = 0;
	private int numberOfCallAttempts = 0;
	/**
	 * True if the crnode has collided in the previous frame
	 */
	private boolean isCollided = false;
	
    /**
     * Creates a CRNode with the given frequencies, position and velocity values.
     * @param pos Position of the CRNode
     * @param vel Velocity of the CRNode
     * @param frequencies List of frequencies that are assigned to this node.
     */
    public CRNode(int id, Point2D.Double pos, double vel) {
        this.id = id;
        this.position = new Point2D.Double(pos.x, pos.y);
        this.velocity = vel;
        this.startCommEvent = new StartCommunicationEvent(this.id);
        this.endCommEvent = new EndCommunicationEvent(this.id);
        this.expoInterarrival = new Exponential(SimulationRunner.wc.getMeanOffDuration(), SimulationRunner.randEngine);
        this.expoCommDuration = new Exponential((1.0/SimulationRunner.wc.getMeanOnDuration()), SimulationRunner.randEngine);
    }
    
    /**
     * Updates the snr value of the frequency.
     * @param freq Number of the frequency in snrValues list.
     */
    public void sense(int freq){
        snrValues.put(freq_list_to_listen.get(freq),SimulationRunner.wc.generateSNR(this, freq_list_to_listen.get(freq)));
		int zone = SimulationRunner.crBase.findZone(id);
        averageSnr.get(zone).set(freq_list_to_listen.get(freq), (averageSnr.get(zone).get(freq_list_to_listen.get(freq))+snrValues.get(freq_list_to_listen.get(freq))));
    }
    
    /**
     * 
     * @return Snr values of each frequencies which are assigned to this node.
     */
    public HashMap<Integer, Double> getSnrValues() {
        return snrValues;
    }
    
    /**
     * It creates the averageSnr arraylist and initially add zeros to the elements.
     * @param total_number_of_frequencies Total number of frequencies 
     */
    public static void initializeAverageSnr(int total_number_of_frequencies, int numberOfZones){
        averageSnr = new ArrayList<ArrayList<Double>>();
		for(int j=0;j<numberOfZones;j++){
			averageSnr.add(new ArrayList<Double>());
			for(int i=0;i<total_number_of_frequencies;i++){
				averageSnr.get(j).add(0.0);
			}
		}
    }
    
    /**
     * Writes the id of the CRNode, position of the CRNode and snrValues of the CRNode
     * to the log file, respectively.
     */
    public void logSnrValues(){
        pw.println("number: "+String.valueOf(id) + " -- position: " +position.toString() + " -- snrValues: " + snrValues.toString());
    }
    
    /**
     * Calculates average snr values then writes these values to the log file 
     * and then resets the average snr values.
     * @param number_of_crnodes Total number of CRNodes.
     */
    public static void logAverageSnr(double time){
        for(int i=0;i<averageSnr.size();i++){   //calculates the average snr values
			for(int j=0;j<averageSnr.get(i).size();j++)
				averageSnr.get(i).set(j,(averageSnr.get(i).get(j)/SimulationRunner.crBase.getFrequency_list().get(i).get(j))); // gets the current crnode 
                                                                                        //number that listens to this freq.
        }
        
        SimulationRunner.crBase.setLast_averageSnr(averageSnr);
		if(SimulationRunner.plotOnButton.isSelected())
			for(int i=0;i<averageSnr.size();i++)
				SimulationRunner.plot.addPoint(i,time, averageSnr.get(i));
        pw.println("average snr values: "); //writing to log file
		for(int i=0;i<averageSnr.size();i++){
			pw.println(averageSnr.get(i));
		}
		for(int i=0;i<SimulationRunner.wc.numberOfFreq();i++){
			ArrayList<Node> users = SimulationRunner.wc.getFreq(i);
			pw.print("Freq: "+i+" ");
			pw.print("Primary: ");
			if(users.get(WirelessChannel.PRIMARY) == null)
				pw.print("null ");
			else
				pw.print(users.get(WirelessChannel.PRIMARY).getId()+" ");
			
			pw.print("CR: ");
			if(users.get(WirelessChannel.CR) == null)
				pw.print("null ");
			else
				pw.print(users.get(WirelessChannel.CR).getId()+" ");
			pw.println();
		}
        
        for(int i=0;i<averageSnr.size();i++){ //resets the avarageSnr list.
			for(int j=0;j<averageSnr.get(i).size();j++)
				averageSnr.get(i).set(j,0.0);
        }
    }
    
    /**
     * Creates the log file.
     * @param file_name Name of the log file
     */
    public static void createLogFile(String file_name){
        try {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_name))));
        } catch (IOException ex) {    
            System.err.println("Error during file operations");
        }
    }
    
    /**
     * Writes the input string to the log file.
     * @param log_string String
     */
    public static void writeLogFile(String log_string){
        pw.println(log_string);
    }
    
    /**
     * Closes the log file.
     */
    public static void closeLogFile(){
        pw.close();   
    }
    
    /**
     * Sets frequency list to listen in the sensing slots.
     * @param frequencies Frequency list
     */
    public void  setFrequencyList(ArrayList<Integer> frequencies){
        snrValues = new HashMap<Integer, Double>();
        for(int i=0;i<frequencies.size();i++){ 
            snrValues.put(frequencies.get(i), 0.0); //adding all the frequency values to the 
                                                   //hash table with 0.0 initial snr value
        }
        freq_list_to_listen=frequencies;
    }
    
    /**
     * Sets a frequency to communicate 
     * @param communication_frequency Communication frequency
     */
    public void setCommunication_frequency(int communication_frequency) {
        this.communication_frequency = communication_frequency;
		commOrNot = true;
		SimulationRunner.wc.occupyFrequency(communication_frequency, this);
    }

	public int getCommunication_frequency() {
		return communication_frequency;
	}
	
	/**
	 * Releases its communication frequency
	 */
	public void releaseCommunication_frequency()
	{
		SimulationRunner.wc.releaseFrequency(this.communication_frequency, this);
		commOrNot = false;
		communication_frequency = -1;
	}
    
    /**
     * Checks whether a collision occured or not if there is an assigned 
     * frequency to the crnode for that frame and resets the collision value
     * if it is the last report. 
     * @param time Time of the simulation
     * @param lastReport True if it is the last report otherwise false
     */
    public static void communicate(double time, boolean lastReport){
		
        ArrayList<Double> sinr = new ArrayList<Double>();
        for(int i=0;i<SimulationRunner.wc.numberOfFreq();i++){
            sinr.add(0.0);
        }
        for(int i=0;i<SimulationRunner.crNodes.size();i++){
			if(!SimulationRunner.crNodes.get(i).commOrNot)
				continue;
            String collision = "no collision";
            int freq = SimulationRunner.crNodes.get(i).communication_frequency;
			
			sinr.set(freq,SimulationRunner.wc.generateSINR(SimulationRunner.crBase, SimulationRunner.crNodes.get(i), freq));
			if(sinr.get(freq)<SimulationRunner.wc.sinrThreshold){ //checks if collision occured
				collision = "collision occured";
				SimulationRunner.crNodes.get(i).collisionOccured = true;
			}
			
			double msec = (double)time;
			int hour = (int)(msec/3600000.0);
			msec -= hour*3600000.0;
			int min = (int)(msec/60000.0);
			msec -= min*60000.0;
			int sec = (int)(msec/1000.0);
			msec-= sec*1000.0;
			
			writeLogFile(String.format(Locale.US,"Time: %2d:%2d:%2d:%.2f", hour,min,sec,msec) +" -- number: "+String.valueOf(SimulationRunner.crNodes.get(i).id) + " -- frequency: " + String.valueOf(freq) + " -- sinrValue: " + sinr.get(freq).toString() + " --- " + collision );
			if(lastReport){
				SimulationRunner.crNodes.get(i).numberOfFramesCommunicated++;
				if(SimulationRunner.crNodes.get(i).collisionOccured){
					SimulationRunner.crNodes.get(i).numberOfCollision++;
					SimulationRunner.crNodes.get(i).isCollided = true;
				}
				SimulationRunner.crNodes.get(i).collisionOccured = false;
			}
        }
		if(SimulationRunner.plotOnButton.isSelected())
			SimulationRunner.plot.addPoint(SimulationRunner.crBase.registeredZones.size(),time, sinr);
    }
    
    /**
     * Sets the total number of frames for a simulation.
     * @param totalNumberOfFrames Number of Frames
     */
    public static void setTotalNumberOfFrames(int totalNumberOfFrames) {
		CRNode.totalNumberOfFrames = totalNumberOfFrames;
    }
    
    /**
     * Returns total number of frames.
     * @return Total number of frames
     */
    public static int getTotalNumberOfFrames() {
		return totalNumberOfFrames;
    }
    
    /**
     * Calculates and writes CrNode statistics to the log file.
     * @return CrNode statistic values
     */
    public static String[][] logStats(){
            writeLogFile("-----CR NODE STATS-----");
            writeLogFile(String.format(Locale.US,"Total Number of frames: %d", totalNumberOfFrames));
            double totalNumberOfFramesComm = 0.0, totalNumberOfCollision = 0.0, totalNumberOfCallAttempts = 0.0;
			double totalNumberOfCalls = 0.0, totalNumberOfBlocks = 0.0, totalNumberOfDrops = 0.0, totalNumberOfForcedHandoffs = 0.0;
            String[][] data = new String[SimulationRunner.crNodes.size()+1][8];
            int i = 0;
            for(; i<SimulationRunner.crNodes.size();i++){
                    CRNode c = SimulationRunner.crNodes.get(i);
                    writeLogFile(String.format(Locale.US,"CR Node: %d\t\tNumber of Call Attempts: %d\t\tNumber of Calls: %d\t\tNumber of Frames Communicated: %d\t\tNumber of Blocks: %d\t\tNumber of Drops: %d\t\tNumber of Forced Handoffs: %d\t\tNumber of Collisions: %d",
                            c.id, c.numberOfCallAttempts, c.numberOfCallAttempts - c.numberOfBlocks, c.numberOfFramesCommunicated, c.numberOfBlocks, c.numberOfDrops, c.numberOfForcedHandoff, c.numberOfCollision));
                    totalNumberOfFramesComm += c.numberOfFramesCommunicated;
                    totalNumberOfCollision += c.numberOfCollision;
					totalNumberOfCallAttempts += c.numberOfCallAttempts;
					totalNumberOfCalls += (c.numberOfCallAttempts - c.numberOfBlocks);
					totalNumberOfBlocks += c.numberOfBlocks;
					totalNumberOfDrops += c.numberOfDrops;
					totalNumberOfForcedHandoffs += c.numberOfForcedHandoff;

                    data[i][0] = String.valueOf(c.id);
                    data[i][1] = String.valueOf(c.numberOfCallAttempts);
                    data[i][2] = String.valueOf(c.numberOfCallAttempts - c.numberOfBlocks);
                    data[i][3] = String.valueOf(c.numberOfFramesCommunicated);
                    data[i][4] = String.valueOf(c.numberOfBlocks);
                    data[i][5] = String.valueOf(c.numberOfDrops);
                    data[i][6] = String.valueOf(c.numberOfForcedHandoff);
					data[i][7] = String.valueOf(c.numberOfCollision);
            }
			totalNumberOfFramesComm /= SimulationRunner.crNodes.size();
			totalNumberOfCollision /= SimulationRunner.crNodes.size();
			totalNumberOfCallAttempts /= SimulationRunner.crNodes.size();
			totalNumberOfCalls /= SimulationRunner.crNodes.size();
			totalNumberOfBlocks /= SimulationRunner.crNodes.size();
			totalNumberOfDrops /= SimulationRunner.crNodes.size();
			totalNumberOfForcedHandoffs /= SimulationRunner.crNodes.size();
			writeLogFile("\nAverage:");
			writeLogFile(String.format(Locale.US,"Number of CR Nodes\t\t\t\t: %d", SimulationRunner.crNodes.size()));
			writeLogFile(String.format(Locale.US,"Number of Call Attempts\t\t\t: %.2f", totalNumberOfCallAttempts));
			writeLogFile(String.format(Locale.US,"Number of Calls\t\t\t\t\t: %.2f", totalNumberOfCalls));
			writeLogFile(String.format(Locale.US,"Number of Frames Communicated\t: %.2f", totalNumberOfFramesComm));
			writeLogFile(String.format(Locale.US,"Number of Blocks\t\t\t\t: %.2f", totalNumberOfBlocks));
			writeLogFile(String.format(Locale.US,"Number of Drops\t\t\t\t\t: %.2f", totalNumberOfDrops));
			writeLogFile(String.format(Locale.US,"Number of Forced Handoff\t\t: %.2f", totalNumberOfForcedHandoffs));
			writeLogFile(String.format(Locale.US,"Number of Collisions\t\t\t: %.2f", totalNumberOfCollision));

            data[i][0] = "Average";
            data[i][1] = String.format(Locale.US,"%.2f", totalNumberOfCallAttempts);
            data[i][2] = String.format(Locale.US,"%.2f", totalNumberOfCalls);
            data[i][3] = String.format(Locale.US,"%.2f", totalNumberOfFramesComm);
            data[i][4] = String.format(Locale.US,"%.2f", totalNumberOfBlocks);
            data[i][5] = String.format(Locale.US,"%.2f", totalNumberOfDrops);
            data[i][6] = String.format(Locale.US,"%.2f", totalNumberOfForcedHandoffs);
			data[i][7] = String.format(Locale.US,"%.2f", totalNumberOfCollision);
            return data;
    }
    
    
    public static class StartCommunicationEvent implements Event{
            public int id = 0;

            @Override
            public void entering(SimEnt locale){}

            public StartCommunicationEvent(int crnode_id) {
                this.id = crnode_id;
            }
    }
    
    public static class EndCommunicationEvent implements Event{
            public int id = 0;

            @Override
            public void entering(SimEnt locale){}

            public EndCommunicationEvent(int crnode_id) {
                this.id = crnode_id;
            }
    }
    
    /**
	 * Finds the next on duration according to the traffic model for DES
	 * @param frameDuration		Duration of one frame
	 * @return	On duration
	 */
	public double nextOnDurationDES(double frameDuration)
	{
		double nextDuration = expoCommDuration.nextDouble() * WirelessChannel.unitTime * 60000;
		return Math.ceil(nextDuration/frameDuration) * frameDuration;
	}
	
	/**
	 * Finds the next off duration according to the traffic model for DES
	 * @param frameDuration		Duration of one frame
	 * @return	Off duration
	 */
	public double nextOffDurationDES(double frameDuration)
	{
		double nextOffDur = expoInterarrival.nextDouble() * WirelessChannel.unitTime * 3600000;
		return Math.round(nextOffDur/frameDuration) * frameDuration;
	}
	
	/**
	 * Finds the next on duration in terms of number of frames according to the
	 * traffic model for Multithreaded Simulation
	 * @param frameDuration		Duration of one frame
	 * @return	On duration
	 */
	public int nextOnDuration(double frameDuration)
	{
		double nextDuration = expoCommDuration.nextDouble() * 60000;
		return (int)Math.ceil(nextDuration/frameDuration);
	}
	
	/**
	 * Finds the next off duration in terms of number of frames according to the
	 * traffic model for Multithreaded Simulation
	 * @param frameDuration		Duration of one frame
	 * @return	Off duration
	 */
	public int nextOffDuration(double frameDuration)
	{
		double nextOffDur = expoInterarrival.nextDouble() * 3600000;
		return (int)Math.round(nextOffDur/frameDuration);
	}

//    public void setCommOrNot(boolean commOrNot) {
//        this.commOrNot = commOrNot;
//    }
	
	public boolean getCommOrNot() {
        return commOrNot;
    }

	public void setReadytoComm(boolean readytoComm) {
		if(readytoComm){
			numberOfCallAttempts++;
		}
		this.readytoComm = readytoComm;
	}
    
	public boolean getReadytoComm()
	{
		return readytoComm;
	}
	
	public boolean getIsCollided() {
		return isCollided;
	}
	
	public void setIsCollided(boolean iscollided) {
		this.isCollided = iscollided;
	}

	public int getNumberOfForcedHandoff() {
		return numberOfForcedHandoff;
	}

	public void setNumberOfForcedHandoff(int numberOfForcedHandoff) {
		this.numberOfForcedHandoff = numberOfForcedHandoff;
	}

	public int getNumberOfDrops() {
		return numberOfDrops;
	}

	public void setNumberOfDrops(int numberOfDrops) {
		this.numberOfDrops = numberOfDrops;
	}

	public int getNumberOfBlocks() {
		return numberOfBlocks;
	}

	public void setNumberOfBlocks(int numberOfBlocks) {
		this.numberOfBlocks = numberOfBlocks;
	}
	
}

package Nodes;

import ATL.ATLHueristicMain;
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
import Heuristic.ATLHueristic;
import Heuristic.FAHMain;
import SimulationRunner.ParetoDistribution;
import SimulationRunner.SimulationRunner;
import cern.jet.random.Exponential;
import java.util.Collections;

/**
 * This class basic operations of a CR node. It also concerns with logging operations
 * communication events of all CR nodes.
 * @see Node
 */
public class CRNode implements Node {
	/**
	 * Position of the node
     */
    protected Point2D.Double position = new Point2D.Double(0,0);
    /**
     * Velocity of the node
     */
    protected double velocity = 0;
    /**
     * Id of the Node
     */
    protected int id;
	static double powerThreshold = 0;
    
    /**
     * List of frequencies assigned to this node with respect to their SNR values.
     */
    private HashMap<Integer, Double> receievedPowers;
    /**
     * Writer for the log file.
     */
    private static PrintWriter logFileWriter = null;
    /**
     * Writer for the probability log file.
     */
    private static PrintWriter probabilityLogFileWriter = null;
    /**
     * Average SNR values of the frequencies.
     */
    private static ArrayList<ArrayList<Double>> averageReceivedPower = null;
	private static ArrayList<ArrayList<Integer>> sensingDecision = null;
    /**
     * Communication frequency of the CR node.
     * If the assigned value is lower than zero this means that 
     * there will be no communication for the CR node. 
     */
    private int communication_frequency = -1;
    /**
     * Frequency list to be listen in the sensing slots.
     */
    private ArrayList<Integer> freq_list_to_listen;
    /**
     * Total number of frames communicated by CR node in the entire simulation time.
     */
    private int numberOfFramesCommunicated = 0;
    /**
     * Total number of collisions between this CR node and primary traffic 
     * generators during the simulation time.
     */
    private int numberOfCollision = 0;
    private int estimatedNumberOfCollison = 0;
    /**
     * Boolean value that keeps whether a collision happened or not.
     */
    private boolean collisionOccured = false;
    /**
     * Count of how many times this CR node is blocked
     */
    private int numberOfBlocks = 0;
    /**
     * Total number of frames in the simulation.
     */
    private static int totalNumberOfFrames = 0;
    /**
     * Boolean value that shows whether this CR node communicates or not
     */
    private boolean commOrNot = false;
    private boolean readytoComm = false;
    /**
     * An event to start communication for this CR node
     */
    public final StartCommunicationEvent startCommEvent;
    /**
     * Handle of the start communication event of this CR node
     */
    public Scheduler.EventHandle startEventHandle;
    /**
     * An event to end communication for this CR node
     */
    public final EndCommunicationEvent endCommEvent;
    /**
     * Handle of the end communication event of this CR node
     */
    public Scheduler.EventHandle endEventHandle;
    private Exponential expoInterarrival;
    private Exponential expoCommDuration;
    private static ParetoDistribution parOnDuration;
    private static ParetoDistribution parOffDuration;
    private int numberOfDrops = 0;
    private int numberOfForcedHandoff = 0;
    private int numberOfCallAttempts = 0;
    private int numberOfCalls = 0;
    /**
     * True if the CR node has collided in the previous frame
     */
    private boolean isCollided = false;
    private double last_time;
    private double lastChannelCapacity;
    private double totalNumberOfBitsTransmitted = 0;
    /**
     * Array list that keeps the reporting frame numbers, so that at each "reporting frame"
     * all related data will be logged in the log file.
     */
    public static ArrayList<Integer> reportingFrames;
	
	private final static int MAJORITY = 0;
	private final static int NK = 1;
	
	private static int cooperationRule;
    private int zoneId;
	private int groupId;
    /**
     * Creates a CRNode with the given frequencies, position and velocity values.
     * @param id ID of this CR node
     * @param pos Position of the CRNode
     * @param vel Velocity of the CRNode
     */
    public CRNode(int id, Point2D.Double pos, double vel) {
        this.id = id;
        this.position = new Point2D.Double(pos.x, pos.y);
        this.velocity = vel;
        this.startCommEvent = new StartCommunicationEvent(this.id);
        this.endCommEvent = new EndCommunicationEvent(this.id);
        this.expoInterarrival = new Exponential(SimulationRunner.wc.getMeanOffDuration(), SimulationRunner.randEngine);
        this.expoCommDuration = new Exponential((1.0 / SimulationRunner.wc.getMeanOnDuration()), SimulationRunner.randEngine);
		CRNode.powerThreshold = SimulationRunner.args.getPowerThreshold();
        CRNode.reportingFrames = new ArrayList<>();
		cooperationRule = NK;
    }

    /**
     * Updates the SNR value of the frequency that is sensed during current slot.
     * @param sensingSlot	Current sensing slot of the CR frame.
     */
    public void sense(int sensingSlot) {
		if(freq_list_to_listen == null)
			return;
		if(sensingSlot >= freq_list_to_listen.size())
			return;
		int frequency = freq_list_to_listen.get(sensingSlot);
		double receivedPower = SimulationRunner.wc.generateReceivedPower(this, frequency);
        receievedPowers.put(frequency, receivedPower);
        int zone = SimulationRunner.crBase.findZone(id);
        averageReceivedPower.get(zone).set(frequency, (averageReceivedPower.get(zone).get(frequency) + receivedPower));
		if(powerThreshold < receivedPower)
			sensingDecision.get(zone).set(frequency, sensingDecision.get(zone).get(frequency) + 1);
    }

    /**
     * Returns SNR values of each frequencies which are assigned to this node.
     * @return SNR values of each frequencies which are assigned to this node.
     */
    public HashMap<Integer, Double> getReceivedPowers() {
        return receievedPowers;
    }

    /**
     * It creates the averageSnr ArrayList and initially add zeros to the elements.
     * @param total_number_of_frequencies	Total number of frequencies
     * @param numberOfZones					Number of zones currently simulating
     */
    public static void initializeAverageReceivedPowers(int total_number_of_frequencies, int numberOfZones) {
        averageReceivedPower = new ArrayList<ArrayList<Double>>();
		sensingDecision = new ArrayList<ArrayList<Integer>>();
        for (int j = 0; j < numberOfZones; j++) {
            averageReceivedPower.add(new ArrayList<Double>());
			sensingDecision.add(new ArrayList<Integer>());
            for (int i = 0; i < total_number_of_frequencies; i++) {
                averageReceivedPower.get(j).add(0.0);
				sensingDecision.get(j).add(0);
            }
        }
    }

    /**
     * Writes the id of the CRNode, position of the CRNode and snrValues of the CRNode
     * to the log file, respectively.
     */
    public void logReceivedPowers() {
        logFileWriter.println("number: " + String.valueOf(id) + " -- position: " + point2DtoString(position) + " -- snrValues: " + hashMapToString(receievedPowers));
    }
    
    private static void calculate_Pf_Pm()
    {
        double totalFalseAlarm=0.0,totalMissDetection=0.0,frame;
        double averageFalseAlarm,averageMissDetection;
        for (int i=0;i<SimulationRunner.args.getNumberOfZones();i++){
            for(int j=0;j<SimulationRunner.args.getNumberOfFreq();j++){
				boolean isChannelAvailable = SimulationRunner.wc.isChannelAvailable(j, i);
				if(isChannelAvailable && (sensingDecision.get(i).get(j) == 1)){
                    SimulationRunner.crBase.incrementFalseAlarm(i);
                }
                else if(!isChannelAvailable && (sensingDecision.get(i).get(j) == 0)){
                    SimulationRunner.crBase.incrementMissDetection(i);
                }
            }
        }
        if(SimulationRunner.args.isAnimationOn()){
            frame = (double)(SimulationRunner.crSensor.getFrame()) + 1;
        }
        else{
            frame = SimulationRunner.crDesScheduler.getCurrentFrame();
        }
        if(CRNode.reportingFrames.contains((int)frame)){
            for (int i=0;i<SimulationRunner.args.getNumberOfZones();i++){
                totalFalseAlarm += SimulationRunner.crBase.getFalseAlarm(i);
                totalMissDetection += SimulationRunner.crBase.getMissDetection(i);
            }
            averageFalseAlarm = ((totalFalseAlarm/SimulationRunner.args.getNumberOfZones())/frame)/FAHMain.maxSlots;
            averageMissDetection = ((totalMissDetection/SimulationRunner.args.getNumberOfZones())/frame)/FAHMain.maxSlots;
            ArrayList<Double> probs = new ArrayList<Double>();
            probs.add(averageFalseAlarm);
            probs.add(averageMissDetection);
            if(SimulationRunner.args.isAnimationOn()){
                frame *= SimulationRunner.crSensor.getFrameDuration();
            }
            else{
                frame *= SimulationRunner.crDesScheduler.getFrameDuration();
            }
            SimulationRunner.plotSensingProbs.addPoint(frame, probs);
        }
    }
    
    /**
     * Calculates average SNR values then writes these values to the log file 
     * and then resets the average SNR values.
     * @param time Current time
     */
    public static void fuseSensingResults(double time) {
        for (int i = 0; i < averageReceivedPower.size(); i++) {   //calculates the average received powers
            for (int j = 0; j < averageReceivedPower.get(i).size(); j++) {
                averageReceivedPower.get(i).set(j, (averageReceivedPower.get(i).get(j) / SimulationRunner.crBase.getFrequency_list().get(i).get(j))); // gets the current crnode 
			                                                                                        //number that listens to this freq.
				//If more than half of the CR nodes sensing a channel decides that the channel is busy then it is decided busy
				if(cooperationRule == MAJORITY){
					if(sensingDecision.get(i).get(j) > SimulationRunner.crBase.getFrequency_list().get(i).get(j) / 2)
						sensingDecision.get(i).set(j,1);
					else	//Vacant otherwise
						sensingDecision.get(i).set(j,0);
				}
				else if(cooperationRule == NK){
					if(sensingDecision.get(i).get(j) > (SimulationRunner.crBase.getFrequency_list().get(i).get(j)*4)/10)
						sensingDecision.get(i).set(j,1);
					else	//Vacant otherwise
						sensingDecision.get(i).set(j,0);
				}
            }
        }
        calculate_Pf_Pm();
        SimulationRunner.crBase.setLastSensingResults(sensingDecision);
        //pw.println("average snr values and sensing decisions: "); //writing to log file
//        for (int i = 0; i < averageReceivedPower.size(); i++) {
//            pw.println(arrayListToString(averageReceivedPower.get(i)));
//			pw.println(sensingDecision.get(i));
//        }
//        for (int i = 0; i < SimulationRunner.wc.numberOfFreq(); i++) {
//            ArrayList<Node> users = SimulationRunner.wc.getFreq(i);
//            pw.print("Freq: " + i + " ");
//            pw.print("Primary: ");
//            if (users.get(WirelessChannel.PRIMARY) == null) {
//                pw.print("null ");
//            } else {
//                pw.print(users.get(WirelessChannel.PRIMARY).getId() + " ");
//            }
//
//            pw.print("CR: ");
//            if (users.get(WirelessChannel.CR) == null) {
//                pw.print("null ");
//            } else {
//                pw.print(users.get(WirelessChannel.CR).getId() + " ");
//            }
//            pw.println();
//        }

        for (int i = 0; i < averageReceivedPower.size(); i++) { //resets the avarageSnr list.
            for (int j = 0; j < averageReceivedPower.get(i).size(); j++) {
                averageReceivedPower.get(i).set(j, 0.0);
				sensingDecision.get(i).set(j, 0);
            }
        }
    }
    
    /**
     * Creates the log file.
     * @param file_name Name of the log file
     */
    public static void createLogFile(String file_name) {
        String justComma = "",zones="";
        try {
            logFileWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_name))));
            for(int i=0;i<SimulationRunner.args.getNumberOfZones();i++){
                justComma += ";";
                zones += String.valueOf(i) + ". zone;";
            }
            writeLogFile(String.format(Locale.US, "Time;Number of False Alarms"+justComma+"Number of Miss Detection"
                    +justComma+"Number of Collisions"+justComma+"Number of Blocks"+justComma
                    +"Number of Drops"+justComma+"Throughput"+justComma+"Total communicated frames"+justComma
                    +"Number of calls"+justComma+"Number of call attempts"+justComma));
            writeLogFile(String.format(Locale.US, ";"+zones+zones+zones+zones+zones+zones+zones+zones+zones));
        } catch (IOException ex) {
            System.err.println("Error during file operations in log file creation");
        }
    }
    
    /**
     * Creates the log file for the probabilities of some cr statistics.
     * @param file_name Name of the log file
     */
    public static void createProbabilityLogFile(String file_name) {
        String justComma = "",zones="";
        try {
            probabilityLogFileWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_name))));
			writeProbabilityLogFile(";");
            for(int i=0;i<SimulationRunner.args.getNumberOfZones();i++){
                justComma += ";";
                zones += String.valueOf(i) + ". zone;";
				writeProbabilityLogFile(String.valueOf(ATLHueristic.yij.get(i).size())+";");
            }
            writeProbabilityLogFileWithEndLine(String.format(Locale.US, "\nTime;Probability of False Alarm"+justComma
                    +"Probability of Miss Detection"+justComma+"Probability of Collision"+justComma
                    +"Probability of Block"+justComma+"Probability of Drop"+justComma));
            writeProbabilityLogFileWithEndLine(String.format(Locale.US, ";"+zones+zones+zones+zones+zones));
        } catch (IOException ex) {
            System.err.println("Error during file operations in probability log file creation");
        }
    }
    
    /**
     * Writes the input string into the log file.
     * @param log_string String
     */
    public static void writeLogFile(String log_string) {
        logFileWriter.println(log_string);
    }
    
    /**
     * Writes the input string into the probability log file.
     * @param log_string String
     */
    public static void writeProbabilityLogFileWithEndLine(String log_string) {
        probabilityLogFileWriter.println(log_string);
    }
	
	/**
     * Writes the input string into the probability log file.
     * @param log_string String
     */
    public static void writeProbabilityLogFile(String log_string) {
        probabilityLogFileWriter.print(log_string);
    }
    
    /**
     * Writes log_string to the log file
     * @param log_string String
     */
    public static void writeLogFileProb(String log_string) {
//        pw_prob.println(log_string);
    }

    /**
     * Closes the log file.
     */
    public static void closeLogFile() {
        logFileWriter.close();
    }    
    
    /**
     * Closes the probability log file.
     */
    public static void closeProbabilityLogFile() {
        probabilityLogFileWriter.close();
    }
    
    /**
     * Sets frequency list to listen in the sensing slots.
     * @param frequencies Frequency list
     */
    public void setFrequencyList(ArrayList<Integer> frequencies) {
        receievedPowers = new HashMap<Integer, Double>();
        for (int i = 0; i < frequencies.size(); i++) {
            receievedPowers.put(frequencies.get(i), 0.0); //adding all the frequency values to the 
            //hash table with 0.0 initial snr value
        }
        freq_list_to_listen = new ArrayList<>(Collections.nCopies(frequencies.size(), -1));
		Collections.copy(freq_list_to_listen, frequencies);
    }

    /**
     * Sets a frequency to communicate 
     * @param communication_frequency Communication frequency
     */
    public void setCommunication_frequency(int communication_frequency) {
        this.communication_frequency = communication_frequency;
        commOrNot = true;
        SimulationRunner.wc.occupyFrequency(communication_frequency, this);
        if(!this.isCollided){    //when a call starts
            this.numberOfCalls++;
            SimulationRunner.crBase.incrementNumberOfCalls(SimulationRunner.crBase.findZone(id));
        }
        isCollided=false;
    }

    /**
     * Returns the current communication frequency of this CR node
     * @return	Current communication frequency
     */
    public int getCommunication_frequency() {
        return communication_frequency;
    }

    /**
     * Releases its communication frequency
     */
    public void releaseCommunication_frequency() {
        SimulationRunner.wc.releaseFrequency(this.communication_frequency, this);
        this.commOrNot = false;
        this.communication_frequency = -1;
    }

    /**
     * Checks whether a collision occured or not if there is an assigned 
     * frequency to the crnode for that frame and resets the collision value
     * if it is the last report. 
     * @param time Time of the simulation
     * @param isRegular False if this function called because of a collision event.
     * @param lastReport True if it is the last report otherwise false
     */
    public static void communicate(double time,boolean isRegular, boolean lastReport) {
        
        ArrayList<Double> channelCapacity = new ArrayList<Double>();
        for (int i = 0; i < SimulationRunner.wc.numberOfFreq(); i++) {
            channelCapacity.add(0.0);
        }
		
		for (CRNode node : SimulationRunner.crBase) {
			if(!node.commOrNot)
				continue;
			node.communicate(time, isRegular, lastReport, channelCapacity);
		}
    }
	
	private void communicate(double time, boolean isRegular, boolean lastReport, ArrayList<Double> channelCapacity)
	{
		int freq = communication_frequency;
		int zoneId = SimulationRunner.crBase.findZone(id);
			
		channelCapacity.set(freq,SimulationRunner.wc.currentChannelCapacity(SimulationRunner.crBase, this, freq));
		if(!SimulationRunner.wc.isChannelAvailable(freq, zoneId)){ //checks if collision occured
			if(isRegular&&(!lastReport))
				collisionOccured = true;
		}
		if(isRegular && !lastReport){   //updates time and sinr value of communicating cr nodes
			last_time = time;
			lastChannelCapacity = channelCapacity.get(freq);
		}
		if(isRegular && lastReport){
			totalNumberOfBitsTransmitted += (time-last_time)*lastChannelCapacity*0.001;
			SimulationRunner.crBase.incrementTotalBitsTransmitted(zoneId, (time-last_time)*lastChannelCapacity*0.001);
		}
		if(!isRegular){
			totalNumberOfBitsTransmitted += (time-last_time)*lastChannelCapacity*0.001;
			SimulationRunner.crBase.incrementTotalBitsTransmitted(zoneId, (time-last_time)*lastChannelCapacity*0.001);
			last_time = time;
			lastChannelCapacity = channelCapacity.get(freq);
		}

//			double msec = (double)time;
//			int hour = (int)(msec/3600000.0);
//			msec -= hour*3600000.0;
//			int min = (int)(msec/60000.0);
//			msec -= min*60000.0;
//			int sec = (int)(msec/1000.0);
//			msec-= sec*1000.0;

//			writeLogFile(String.format(Locale.US,"Time: %2d:%2d:%2d:%.2f", hour,min,sec,msec) +" -- number: "+String.valueOf(SimulationRunner.crBase.getCRNode(i).id) + " -- frequency: " + String.valueOf(freq) + " -- sinrValue: " + sinr.get(freq).toString() + " --- " + collision );
		if(isRegular && lastReport){
			numberOfFramesCommunicated++;
			SimulationRunner.crBase.incrementTotalCommunicatedFrames(zoneId);
			if(collisionOccured){
				numberOfCollision++;
				SimulationRunner.crBase.incrementCollision(zoneId);
			}
			collisionOccured = false;
		}
	}

    /**
     * Sets the total number of frames for a simulation.
     * @param totalNumberOfFrames Number of Frames
     */
    public static void setTotalNumberOfFrames(int totalNumberOfFrames) {
        CRNode.totalNumberOfFrames = totalNumberOfFrames;
        CRNode.assignReportingFrames();
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
    public static String[][] logStats() {
//        writeLogFile("-----CR NODE STATS-----");
//        writeLogFile(String.format(Locale.US, "Total Number of frames: %d", totalNumberOfFrames));
        double totalNumberOfFramesComm = 0.0, totalNumberOfCollision = 0.0, totalNumberOfCallAttempts = 0.0;
        double totalNumberOfCalls = 0.0, totalNumberOfBlocks = 0.0, totalNumberOfDrops = 0.0, totalNumberOfForcedHandoffs = 0.0, totalThroughput = 0.0;
        String[][] data = new String[SimulationRunner.crBase.numberOfCRNodes() + 1][9];
        int throughput = 0;
        int i = 0;
        for (; i < SimulationRunner.crBase.numberOfCRNodes(); i++) {
            CRNode c = SimulationRunner.crBase.getCRNode(i);
            if(SimulationRunner.args.isAnimationOn())
                throughput = 1000*(int)(c.totalNumberOfBitsTransmitted/(SimulationRunner.crSensor.getCommDurationInTermsOfUnitTime()*c.numberOfFramesCommunicated));
            else
                throughput = 1000*(int)(c.totalNumberOfBitsTransmitted/(SimulationRunner.crDesScheduler.getCommDur()*c.numberOfFramesCommunicated));
//            writeLogFile(String.format(Locale.US, "CR Node: %d\t\tNumber of Call Attempts: %d\t\tNumber of Calls: %d\t\tNumber of Frames Communicated: %d\t\tNumber of Blocks: %d\t\tNumber of Drops: %d\t\tNumber of Forced Handoffs: %d\t\tNumber of Collisions: %d\t\tThroughput: %.2f Kbits",
//                    c.id, c.numberOfCallAttempts, c.numberOfCalls, c.numberOfFramesCommunicated, c.numberOfBlocks, c.numberOfDrops, c.numberOfForcedHandoff, c.numberOfCollision, (throughput/1024.0)));
            totalNumberOfFramesComm += c.numberOfFramesCommunicated;
            totalNumberOfCollision += c.numberOfCollision;
            totalNumberOfCallAttempts += c.numberOfCallAttempts;
            totalNumberOfCalls += c.numberOfCalls;
            totalNumberOfBlocks += c.numberOfBlocks;
            totalNumberOfDrops += c.numberOfDrops;
            totalNumberOfForcedHandoffs += c.numberOfForcedHandoff;
            totalThroughput += throughput;
            
            data[i][0] = String.valueOf(c.id);
            data[i][1] = String.valueOf(c.numberOfCallAttempts);
            data[i][2] = String.valueOf(c.numberOfCalls);
            data[i][3] = String.valueOf(c.numberOfFramesCommunicated);
            data[i][4] = String.valueOf(c.numberOfBlocks);
            data[i][5] = String.valueOf(c.numberOfDrops);
            data[i][6] = String.valueOf(c.numberOfForcedHandoff);
            data[i][7] = String.valueOf(c.numberOfCollision);
			data[i][8] = String.format(Locale.US, "%.2f Kbits/sec", throughput/1024.0);
        }
		int numberOfCRNodes = SimulationRunner.crBase.numberOfCRNodes();
        totalNumberOfFramesComm /= numberOfCRNodes;
        totalNumberOfCollision /= numberOfCRNodes;
        totalNumberOfCallAttempts /= numberOfCRNodes;
        totalNumberOfCalls /= numberOfCRNodes;
        totalNumberOfBlocks /= numberOfCRNodes;
        totalNumberOfDrops /= numberOfCRNodes;
        totalNumberOfForcedHandoffs /= numberOfCRNodes;
        totalThroughput /= numberOfCRNodes;
//        writeLogFile("\nAverage:");
//        writeLogFile(String.format(Locale.US, "Number of CR Nodes\t\t\t\t: %d", numberOfCRNodes));
//        writeLogFile(String.format(Locale.US, "Number of Call Attempts\t\t\t: %.2f", totalNumberOfCallAttempts));
//        writeLogFile(String.format(Locale.US, "Number of Calls\t\t\t\t\t: %.2f", totalNumberOfCalls));
//        writeLogFile(String.format(Locale.US, "Number of Frames Communicated\t: %.2f", totalNumberOfFramesComm));
//        writeLogFile(String.format(Locale.US, "Number of Blocks\t\t\t\t: %.2f", totalNumberOfBlocks));
//        writeLogFile(String.format(Locale.US, "Number of Drops\t\t\t\t\t: %.2f", totalNumberOfDrops));
//        writeLogFile(String.format(Locale.US, "Number of Forced Handoff\t\t: %.2f", totalNumberOfForcedHandoffs));
//        writeLogFile(String.format(Locale.US, "Number of Collisions\t\t\t: %.2f", totalNumberOfCollision));
//		writeLogFile(String.format(Locale.US, "Throughput\t\t\t: %.2f Kbits", totalThroughput/1024.0));
        
        data[i][0] = "Average";
        data[i][1] = String.format(Locale.US, "%.2f", totalNumberOfCallAttempts);
        data[i][2] = String.format(Locale.US, "%.2f", totalNumberOfCalls);
        data[i][3] = String.format(Locale.US, "%.2f", totalNumberOfFramesComm);
        data[i][4] = String.format(Locale.US, "%.2f", totalNumberOfBlocks);
        data[i][5] = String.format(Locale.US, "%.2f", totalNumberOfDrops);
        data[i][6] = String.format(Locale.US, "%.2f", totalNumberOfForcedHandoffs);
        data[i][7] = String.format(Locale.US, "%.2f", totalNumberOfCollision);
		data[i][8] = String.format(Locale.US, "%.2f Kbits/sec", totalThroughput/1024.0);
//        writeLogFile("\n\nProbabilities of False Alarm");
//        for(int j=0;j<SimulationRunner.args.getNumberOfZones();j++){
//            writeLogFile(String.format(Locale.US, "%d. Zone : %f --- total false alarm: %f", j, (double)(SimulationRunner.crBase.getFalseAlarm(j)/totalNumberOfFrames),SimulationRunner.crBase.getFalseAlarm(j)));
//        }
//        writeLogFile("\n\nProbabilities of Miss Detection");
//        for(int j=0;j<SimulationRunner.args.getNumberOfZones();j++){
//            writeLogFile(String.format(Locale.US, "%d. Zone : %f --- total miss detection: %f", j, (double)(SimulationRunner.crBase.getMissDetection(j)/totalNumberOfFrames),SimulationRunner.crBase.getMissDetection(j)));
//        }
        return data;
    }

    /**
     * Event class to handle communication starts of CR nodes
     */
    public static class StartCommunicationEvent implements Event {

        /**
         * ID of the associated CR node
         */
        public int id = 0;

        /**
         * Method to handle entering of this event to a simulation entity
         * @param locale	Simulation entity that this event entering
         */
        @Override
        public void entering(SimEnt locale) {
        }

        /**
         * Constructor of this event
         * @param crnode_id	ID of the associated CR node
         */
        public StartCommunicationEvent(int crnode_id) {
            this.id = crnode_id;
        }
    }

    /**
     * Event class to handle communication end of CR nodes
     */
    public static class EndCommunicationEvent implements Event {

        /**
         * ID of the associated CR node
         */
        public int id = 0;

        /**
         * Method to handle entering of this event to a simulation entity
         * @param locale	Simulation entity that this event entering
         */
        @Override
        public void entering(SimEnt locale) {
        }

        /**
         * Constructor of this event
         * @param crnode_id	ID of the associated CR node
         */
        public EndCommunicationEvent(int crnode_id) {
            this.id = crnode_id;
        }
    }

    /**
     * Finds the next on duration according to the traffic model for DES
     * @param frameDuration		Duration of one frame
     * @return	On duration
     */
    public double nextOnDurationDES(double frameDuration) {
        double nextDuration = expoCommDuration.nextDouble() * WirelessChannel.unitTime * 60000;
        return Math.ceil(nextDuration / frameDuration) * frameDuration;
    }

    /**
     * Finds the next off duration according to the traffic model for DES
     * @param frameDuration		Duration of one frame
     * @return	Off duration
     */
    public double nextOffDurationDES(double frameDuration) {
        double nextOffDur = expoInterarrival.nextDouble() * WirelessChannel.unitTime * 3600000;
        return Math.round(nextOffDur / frameDuration) * frameDuration;
    }

    /**
     * Finds the next on duration in terms of number of frames according to the
     * traffic model for Multi threaded Simulation
     * @param frameDuration		Duration of one frame
     * @return	On duration
     */
    public int nextOnDuration(double frameDuration) {
        double nextDuration = expoCommDuration.nextDouble() * 60000;
        return (int) Math.ceil(nextDuration / frameDuration);
    }

    /**
     * Finds the next off duration in terms of number of frames according to the
     * traffic model for Multi threaded Simulation
     * @param frameDuration		Duration of one frame
     * @return	Off duration
     */
    public int nextOffDuration(double frameDuration) {
        double nextOffDur = expoInterarrival.nextDouble() * 3600000;
        return (int) Math.round(nextOffDur / frameDuration);
    }
    
    static private void assignReportingFrames(){
        int totalframes = getTotalNumberOfFrames(), nofReports = SimulationRunner.args.getNumberOfReports();
        for(int i=0;i<nofReports-1;i++){
            reportingFrames.add((totalframes/nofReports)*i+(totalframes/nofReports));
        }
        reportingFrames.add(totalframes);
    } 
    
    /**
     * Returns whether this CR node is currently communicating or not
     * @return <ul>
     *			<li> <b><i>True</i></b> if node is currently communicating</li>
     *			<li> <b><i>False</i></b> otherwise</li>
     *		   </ul>
     */
    public boolean getCommOrNot() {
        return commOrNot;
    }

    /**
     * Sets whether this node can start communication in this frame or not
     * @param readytoComm	Indicates this node can wants to start communicating in this frame if possible
     */
    public void setReadytoComm(boolean readytoComm) {
        if (readytoComm) {
            numberOfCallAttempts++;
            SimulationRunner.crBase.incrementNumberOfCallAttempts(SimulationRunner.crBase.findZone(id));
        }
        this.readytoComm = readytoComm;
    }

    /**
     * Returns whether this CR node wants to start communicating in this frame or not
     * @return <ul>
     *			<li> <b><i>True</i></b> if node wants to start communicating in this frame</li>
     *			<li> <b><i>False</i></b> otherwise</li>
     *		   </ul>
     */
    public boolean getReadytoComm() {
        return readytoComm;
    }

    /**
     * Returns whether this CR node is collided in previous frame or not
     * @return <ul>
     *			<li> <b><i>True</i></b> if node collided in previous frame</li>
     *			<li> <b><i>False</i></b> otherwise</li>
     *		   </ul>
     */
    public boolean getIsCollided() {
        return isCollided;
    }

    /**
     * Sets whether this node is collided in this frame or not
     * @param iscollided Indicates this node collided in this frame or not
     */
    public void setIsCollided(boolean iscollided) {
        this.isCollided = iscollided;
    }

    /**
     * Returns the number of force handoffs this node has made
     * @return	Number of forced handoffs
     */
    public int getNumberOfForcedHandoff() {
        return numberOfForcedHandoff;
    }

    /**
     * Sets the number of forced handoffs this node has made
     * @param numberOfForcedHandoff	Number of forced handoffs
     */
    public void setNumberOfForcedHandoff(int numberOfForcedHandoff) {
        this.numberOfForcedHandoff = numberOfForcedHandoff;
    }

    /**
     * Returns how many times this node has dropped
     * @return	Number of drops
     */
    public int getNumberOfDrops() {
        return numberOfDrops;
    }

    /**
     * Sets how many times this node dropped
     * @param numberOfDrops	Number of drops
     */
    public void setNumberOfDrops(int numberOfDrops) {
        this.numberOfDrops = numberOfDrops;
    }

    /**
     * Returns how many times this node has blocked
     * @return Number of blocks
     */
    public int getNumberOfBlocks() {
        return numberOfBlocks;
    }

    /**
     * Sets how many times this node blocked
     * @param numberOfBlocks Number of blocks
     */
    public void setNumberOfBlocks(int numberOfBlocks) {
        this.numberOfBlocks = numberOfBlocks;
    }

    /**
     * Returns the number of calls of this cr node.
     * @return the number of calls of this cr node.
     */
    public int getNumberOfCalls() {
        return numberOfCalls;
    }

    /**
     * Sets the number of calls of this cr node.
     * @param numberOfCalls number of calls
     */
    public void setNumberOfCalls(int numberOfCalls) {
        this.numberOfCalls = numberOfCalls;
    }

    /**
     * Returns the number of call attempts of this cr node.
     * @return the number of call attempts of this cr node.
     */
    public int getNumberOfCallAttempts() {
        return numberOfCallAttempts;
    }

    /**
     * Returns number of collisions of this cr node with other primary nodes.
     * @return number of collisions of this cr node with other primary nodes.
     */
    public int getNumberOfCollision() {
        return numberOfCollision;
    }
    	
    /**
     * Converts point2D to string
     * @param param point2D
     * @return string notation of that point
     */
    public static String point2DtoString(Point2D.Double param)
	{
		return String.format(Locale.US, "Point2D.Double[%.4f, %.4f]", param.x, param.y);
	}
	
    /**
     * Converts Hash map to string
     * @param param hash map
     * @return string representation of the hash map
     */
    public static String hashMapToString(HashMap<Integer, Double> param)
	{
		String res = "{";
		for(Integer i : param.keySet()){
			if(param.get(i) != 0.0)
				res = res.concat(String.format(Locale.US, "%d=%.4f, ", i, param.get(i)));
			else
				res = res.concat(String.format(Locale.US, "%d=%.1f, ", i, param.get(i)));
		}
		res = res.concat("}");
		return res;
	}
	
    /**
     * Returns the number of frames communicated.
     * @return the number of frames communicated
     */
    public int getNumberOfFramesCommunicated() {
		return numberOfFramesCommunicated;
	}
    
    /**
     * Returns the number of estimated collisions.
     * @return the number of estimated collisions
     */
    public int getEstimatedNumberOfCollison() {
        return estimatedNumberOfCollison;
    }
    
    /**
     * Increments the number of estimated collisions by one.
     */
    public void incrementEstimatedNumberOfCollision() {
        estimatedNumberOfCollison++;
    }
    
    /**
     * 
     * @param estimatedNumberOfCollison Estimated number of collisions
     */
    public void setEstimatedNumberOfCollison(int estimatedNumberOfCollison) {
        this.estimatedNumberOfCollison = estimatedNumberOfCollison;
    }
    
	@Override
	public Point2D.Double getPosition() {
		return position;
	}

	@Override
	public double getVelocity() {
		return velocity;
	}

	@Override
	public void setPosition(Point2D.Double position) {
		this.position = position;
	}

	@Override
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}

	public int getZoneId() {
		return zoneId;
	}

	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
}

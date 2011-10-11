/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimulationRunner;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

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
    private static ArrayList<Double> averageSnr = null;
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
     * Total number of communications done by crnode in the entire simulation time.
     */
    private int numberOfCommunications = 0;
    /**
     * Total number of collisions between this crnode and primary traffic 
     * generators during the simulation time.
     */
    private int numberOfCollision = 0;
    /**
     * Boolean value that keeps whether a collision happened or not.
     */
    private boolean collisionOccured = false;
    /**
     * Total number of frames in the simulation.
     */
    private static int totalNumberOfFrames = 0;
    
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
    }
    
    /**
     * Updates the snr value of the frequency.
     * @param freq Number of the frequency in snrValues list.
     */
    public void sense(int freq){
        snrValues.put(freq_list_to_listen.get(freq),SimulationRunner.wc.generateSNR(this, freq_list_to_listen.get(freq)));
        averageSnr.set(freq_list_to_listen.get(freq), (averageSnr.get(freq_list_to_listen.get(freq))+snrValues.get(freq_list_to_listen.get(freq))));
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
    public static void initializeAverageSnr(int total_number_of_frequencies){
        averageSnr = new ArrayList<Double>(total_number_of_frequencies);
        for(int i=0;i<total_number_of_frequencies;i++){
            averageSnr.add(0.0);
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
            averageSnr.set(i,(averageSnr.get(i)/SimulationRunner.crBase.getFrequency_list().get(i))); // gets the current crnode 
                                                                                        //number that listens to this freq.
        }
        
        SimulationRunner.crBase.setLast_averageSnr(averageSnr);
		if(SimulationRunner.plotOnButton.isSelected())
			SimulationRunner.plot.addPoint(0,time, averageSnr);
        pw.println("average snr values: " + averageSnr.toString()); //writing to log file
        
        for(int i=0;i<averageSnr.size();i++){ //resets the avarageSnr list.
            averageSnr.set(i,0.0);
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
            String collision = "no collision";
            int freq = SimulationRunner.crNodes.get(i).communication_frequency;
            if(freq >= 0){  //if freq>0 then crnode has a frequency to talk
                sinr.set(freq,SimulationRunner.wc.generateSINR(SimulationRunner.crBase, SimulationRunner.crNodes.get(i), freq));
                if(sinr.get(freq)<SimulationRunner.wc.sinrThreshold){ //checks if collision occured
                    collision = "collision occured";
                    SimulationRunner.crNodes.get(i).collisionOccured = true;
		}
                writeLogFile(String.format(Locale.US,"Time: %.2f", (double)(time)) +" -- number: "+String.valueOf(SimulationRunner.crNodes.get(i).id) + " -- frequency: " + String.valueOf(freq) + " -- sinrValue: " + sinr.get(freq).toString() + " --- " + collision );
                if(lastReport){
                    SimulationRunner.crNodes.get(i).numberOfCommunications++;
                    if(SimulationRunner.crNodes.get(i).collisionOccured)
                        SimulationRunner.crNodes.get(i).numberOfCollision++;
                    SimulationRunner.crNodes.get(i).collisionOccured = false;
                }
            }
        }
	if(SimulationRunner.plotOnButton.isSelected())
		SimulationRunner.plot.addPoint(1,time, sinr);
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
            double totalNumberOfComm = 0.0, totalNumberOfCollision = 0.0;
            String[][] data = new String[SimulationRunner.crNodes.size()+1][7];
            int i = 0;
            for(; i<SimulationRunner.crNodes.size();i++){
                    CRNode c = SimulationRunner.crNodes.get(i);
                    writeLogFile(String.format(Locale.US,"CR Node: %d\t\tNumber of Communications: %d\t\tNumber of Collisions: %2d\t\tNumber of Communication w/o Collision: %d\t\tPercentage of Collided Communications: %.2f",
                            c.id, c.numberOfCommunications, c.numberOfCollision, c.numberOfCommunications - c.numberOfCollision, (c.numberOfCollision*100.0)/(double)c.numberOfCommunications));
                    totalNumberOfComm += c.numberOfCommunications;
                    totalNumberOfCollision += c.numberOfCollision;

                    data[i][0] = String.valueOf(c.id);
                    data[i][1] = String.valueOf(c.numberOfCommunications);
                    data[i][2] = String.valueOf(c.numberOfCollision);
                    data[i][3] = String.valueOf(c.numberOfCommunications - c.numberOfCollision);
                    data[i][4] = String.format(Locale.US,"%.2f", (c.numberOfCollision*100.0)/(double)c.numberOfCommunications);
                    data[i][5] = String.format(Locale.US,"%.2f", (c.numberOfCommunications*100.0)/(double)totalNumberOfFrames);
                    data[i][6] = String.format(Locale.US,"%.2f", ((c.numberOfCommunications - c.numberOfCollision)*100.0)/(double)totalNumberOfFrames);
            }
            totalNumberOfComm /= SimulationRunner.crNodes.size();
            totalNumberOfCollision /= SimulationRunner.crNodes.size();
            writeLogFile(String.format(Locale.US,"\nAverage:\nNumber of CR Nodes\t\t\t\t\t\t\t\t: %d\nNumber of Communications\t\t\t\t\t\t: %.2f\nNumber of Collisions\t\t\t\t\t\t\t: %.2f\nNumber of Communication w/o Collision\t\t\t: %.2f\nPercentage of Collided Communications\t\t\t: %.2f\nPercentage of Frames Communicated\t\t\t\t: %.2f\nPercentage of Frames Communicated w/o Collision\t: %.2f",
                            SimulationRunner.crNodes.size(), totalNumberOfComm, totalNumberOfCollision,
                            totalNumberOfComm - totalNumberOfCollision, (totalNumberOfCollision*100.0)/totalNumberOfComm,
                            (totalNumberOfComm*100.0)/(double)totalNumberOfFrames,
                            ((totalNumberOfComm - totalNumberOfCollision)*100.0)/(double)totalNumberOfFrames));

            data[i][0] = "Average";
            data[i][1] = String.format(Locale.US,"%.2f", totalNumberOfComm);
            data[i][2] = String.format(Locale.US,"%.2f", totalNumberOfCollision);
            data[i][3] = String.format(Locale.US,"%.2f", totalNumberOfComm - totalNumberOfCollision);
            data[i][4] = String.format(Locale.US,"%.2f", (totalNumberOfCollision*100.0)/(double)totalNumberOfComm);
            data[i][5] = String.format(Locale.US,"%.2f", (totalNumberOfComm*100.0)/(double)totalNumberOfFrames);
            data[i][6] = String.format(Locale.US,"%.2f", ((totalNumberOfComm - totalNumberOfCollision)*100.0)/(double)totalNumberOfFrames);
            return data;
    }
    
}

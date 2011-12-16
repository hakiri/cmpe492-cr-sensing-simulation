package SimulationRunner;

import Animation.DrawCell;
import Animation.Plot;
import Animation.SimultaneousPlot;
import CommunicationEnvironment.*;
import DES.Scheduler;
import DESSimulation.CRDESScheduler;
import DESSimulation.DESPrimaryTrafficGenerator;
import MultiThreadedSimulation.CRSensorThread;
import MultiThreadedSimulation.PrimaryTrafficGenerator;
import Nodes.*;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class SimulationRunner {
	public static SimulationRunner runner;
	/**
	 * Currently running GraphicalUserInterface instance
	 */
	public static GraphicalUserInterface guiRunner;
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		runner = new SimulationRunner();
		
		if(args.length == 2){
			SimulationRunner.args = new Arguments();
			if(!SimulationRunner.args.parseArguments(args[0])){
				return;
			}
			SimulationRunner.args.setLogFileDirectory(args[1]);
			runner.startSimulationInBatchMode();
			return;
		}
		if(args.length == 3){
			SimulationRunner.args = new Arguments();
			if(!SimulationRunner.args.parseArguments(args)){
				return;
			}
			runner.startSimulationInBatchMode();
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				guiRunner = new GraphicalUserInterface();
				guiRunner.setLocationRelativeTo(null);
				guiRunner.setVisible(true);
			}
		});
	}
	
	/**
	 * Main wireless channel which all types of nodes are accessing
	 */
	public static WirelessChannel wc = null;
	/**
	 * Cognitive radio cell structure
	 */
	public static Cell cell = null;
	/**
	 * Primary traffic generator thread for wireless channel frequencies
	 */
	public static PrimaryTrafficGenerator priTrafGen = null;
	/**
	 * Thread responsible for frame structure of CR nodes
	 */
	public static CRSensorThread crSensor = null;
	/**
	 * DES Framework event scheduler for constructing frame structure of CR nodes
	 */
	public static CRDESScheduler crDesScheduler = null;
	/**
	 * DES Framework Primary traffic generator for wireless channel frequencies
	 */
	public static DESPrimaryTrafficGenerator priTrafGenDes = null;
	/**
	 * Base station of CR cell
	 */
	public static CRBase crBase = null;
	/**
	 * Primary traffic generator nodes which cause traffic in wireless channel
	 */
	public static ArrayList<PrimaryTrafficGeneratorNode> priTrafGenNodes = new ArrayList<PrimaryTrafficGeneratorNode>();
	/**
	 * Random number generator for all random number generation operations in the simulation
	 */
	public static RandomEngine randEngine = null;
	/**
	 * Frame to animate cell structure
	 */
	private static DrawCell drawCell;
	/**
	 * Plots the time versus average SNR and SINR values graphs
	 */
	public static Plot plot = null;
	/**
	 * Plots the time versus block, drop, and collision probabilities graphs
	 */
	public static SimultaneousPlot plotProbs = null;
	/**
	 * Plots the time versus false alarm and miss-detection probabilities graphs
	 */
	public static SimultaneousPlot plotSensingProbs = null;
	/**
	 * Collection of all of the arguments necessary to run the simulation.
	 */
	public static Arguments args = null;
		
	/**
	 * Initializes the simulation guiRunner object for batch mode.
	 */
	public SimulationRunner() {}
		
	private boolean commonParts()
	{
		if(args.getAverageNumberOfCalls()<=2 && args.getTrafficModel() == 1){
			if(args.isBatchMode()){
				System.out.println("Mean Off Period Duration must be greater than 2 time units");
			}
			else {
				JOptionPane.showMessageDialog(guiRunner, "Mean Off Period Duration must be greater than 2 time units",
					"Simulation", JOptionPane.WARNING_MESSAGE);
			}
			return false;
		}
		if(args.getAverageCallDur()<=2 && args.getTrafficModel() == 1){
			if(args.isBatchMode()){
				System.out.println("Mean On Period Duration must be greater than 2 time units");
			}
			else {
				JOptionPane.showMessageDialog(guiRunner, "Mean On Period Duration must be greater than 2 time units",
					"Simulation", JOptionPane.WARNING_MESSAGE);
			}
			return false;
		}

		if(args.getSeedModel() == 0){				//If seed model is random
			randEngine = new MersenneTwister(new Date());	//Give date as seed
		}
		else{
			randEngine = new MersenneTwister(args.getSeed());			//Otherwise get seed from user
		}

		wc = new WirelessChannel(args.getNumberOfFreq(), args.getTransmitPower(), args.getAverageNumberOfCalls(), 
								 args.getAverageCallDur(), args.getTrafficModel(), args.getTimeUnit(), args.getBandwidth());//Create a wireless channel

		cell = new Cell(null, args.getRadius(), args.getNumberOfSectors(), args.getAlphaInDegrees(), args.getSetOfD());//Create a cell

		crBase = new CRBase(new Point2D.Double(0, 0),0,args.getNumberOfSensingSlots()); //Create a CR base station in the origin
		Cell.setBaseStation(crBase);
		crBase.registerZones(args.getSectorNumbers(), args.getAlphaNumbers(), args.getdNumbers(), args.getNumbersOfCrUsersInZone());

		if(args.isAnimationOn()){
			drawCell = new DrawCell((int)args.getRadius(), args.getNumberOfSectors(), args.getNumberOfAlphaSlices(), (int)args.getdNumber(),
									args.getNumberOfCrNodes(), args.getNumberOfPriNodes());
			priTrafGen = new PrimaryTrafficGenerator();
			priTrafGenDes = null;
		}
		else{
			drawCell = null;
			priTrafGen = null;
			priTrafGenDes = new DESPrimaryTrafficGenerator();
		}

		for(int i = 0; i<args.getNumberOfCrNodes() ;i++){
			crBase.addCRNode(new CRNode(i,crBase.deployNodeinZone(i), 0));
			if(args.isAnimationOn())
				DrawCell.paintCrNode(crBase.getCRNode(i), Color.GRAY);
		}
		
		ArrayList<Integer> tempArray = new ArrayList<Integer>();
		tempArray.add(1);
		tempArray.add(1);
		tempArray.add(1);
		tempArray.add(1);
		plotProbs = new SimultaneousPlot(4, tempArray, "Time", "Probabilities", "msec", "");
		tempArray = new ArrayList<Integer>();
		tempArray.add(1);
		tempArray.add(1);
		plotSensingProbs = new SimultaneousPlot(2, tempArray, "Time", "Probabilities", "Frame", "");
		if(args.isPlotOn()){
			ArrayList<Integer> numberOFYs = new ArrayList<Integer>();
			numberOFYs.add(args.getNumberOfFreq());
			numberOFYs.add(args.getNumberOfFreq());
			plot = new Plot(args.getNumberOfZones()+1, numberOFYs, "Time", "Average SNR", "msec", "dB");
		}
		else
			plot = null;
		CRNode.initializeAverageReceivedPowers(args.getNumberOfFreq(),args.getNumberOfZones());	//Set average SNR values to zero
		String logFileName = args.getLogFileDirectory() + "log_"+String.valueOf(args.getSeed())+
								"_"+String.valueOf(args.getNumberOfPriNodes())+
								"_"+String.valueOf(args.getNumberOfCrNodes())+".csv";
		CRNode.createLogFile(logFileName);
		return true;
	}
	
	/**
	 * Initializes the main simulation threads or schedulers
	 */
	public void startSimulation()
	{
		args = new Arguments();
		if(!args.parseArguments(guiRunner)){
			return;
		}
		
		if(!commonParts())
			return;
		
		GraphicalUserInterface.progressBar.setValue(0);							//Initialize progress bar
		GraphicalUserInterface.progressBar.setVisible(true);						//Make it visible
		
		GraphicalUserInterface.terminateSimulation.setVisible(true);
		for(int i = 0;i<args.getNumberOfPriNodes();i++){
			priTrafGenNodes.add(new PrimaryTrafficGeneratorNode(Cell.deployNodeinCell(), 0,i));	//Create primary traffic
			if(args.isAnimationOn())
				priTrafGen.registerNode(priTrafGenNodes.get(i));	//and create threads for each of them
			else
				priTrafGenDes.registerNode(priTrafGenNodes.get(i));
		}
		if(args.isAnimationOn()){	//TODO Resolve racing conditions
			crSensor = new CRSensorThread((int)args.getSimulationDuration(), args.getTimeUnit(), args.getNumberOfSensingSlots(), args.getSensingSlotDur(),
										  args.getSenseScheduleAdvertisementDur(), args.getCommScheduleAdvertisementDur(),
										  args.getCommDur(), args.getSenseResultAdvertisementDur());
			crDesScheduler = null;
		}
		else{
			crSensor = null;
			crDesScheduler = new CRDESScheduler((int)args.getSimulationDuration(), args.getTimeUnit(), args.getNumberOfSensingSlots(), args.getSensingSlotDur(),
												args.getSenseScheduleAdvertisementDur(), args.getCommScheduleAdvertisementDur(),
												args.getCommDur(), args.getSenseResultAdvertisementDur());
		}

		if(!args.isAnimationOn()){
			crDesScheduler.start();
			priTrafGenDes.start();
			Thread t = new Thread(Scheduler.instance());
			t.start();
		}
	}
	
	/**
	 * Initializes the main simulation schedulers for batch mode
	 */
	public void startSimulationInBatchMode()
	{
		if(!commonParts())
			return;
		
		crSensor = null;
		crDesScheduler = new CRDESScheduler((int)args.getSimulationDuration(), args.getTimeUnit(), args.getNumberOfSensingSlots(), args.getSensingSlotDur(),
											args.getSenseScheduleAdvertisementDur(), args.getCommScheduleAdvertisementDur(),
											args.getCommDur(), args.getSenseResultAdvertisementDur());
		
        for(int i = 0;i<args.getNumberOfPriNodes();i++){
			priTrafGenNodes.add(new PrimaryTrafficGeneratorNode(Cell.deployNodeinCell(), 0,i));	//Create primary traffic
			priTrafGenDes.registerNode(priTrafGenNodes.get(i));
		}
        
		crDesScheduler.start();
		priTrafGenDes.start();
		Thread t = new Thread(Scheduler.instance());
		t.start();
	}
	
	/**
	 * Clears the data of the simulation
	 */
	public static void clear()
	{
		crBase.clear();			//Delete CR nodes
		priTrafGenNodes.clear();	//Delete primary nodes
		if(args.isAnimationOn())
			drawCell.terminate();
	}
	
	private void exit()
	{
		if(crSensor!=null){
			if(!crSensor.isFinished())
				return;
		}
		if(crDesScheduler != null)
			if(!crDesScheduler.isFinished())
				return;
		
		WindowEvent wev = new WindowEvent(guiRunner, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
}
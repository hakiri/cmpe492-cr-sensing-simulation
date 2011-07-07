package firstproject;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SimulationRunner extends JFrame{

	/**
	 * Main wireless channel which all types of nodes are accessing
	 */
	public static WirelessChannel wc = null;
	/**
	 * Cognitive radio cell structure
	 */
	public static Cell cell = null;
	/**
	 * Primary traffic generator for wireless channel frequencies
	 */
	public static PrimaryTrafficGenerator priTrafGen = null;
	/**
	 * Thread responsible for sensing and logging actions of CR nodes
	 */
	public static CRSensorThread crSensor = null;
	/**
	 * Base station of CR cell
	 */
	public static CRBase crBase = null;
	/**
	 * CR nodes which sense the wireless channel
	 */
	public static ArrayList<CRNode> crNodes = new ArrayList<CRNode>();
	/**
	 * Primary traffic generator nodes which cause traffic in wireless channel
	 */
	public static ArrayList<PrimaryTrafficGeneratorNode> priTrafGenNodes = new ArrayList<PrimaryTrafficGeneratorNode>();
	/**
	 * Random generator for all random number generation operations in the simulation
	 */
	public static RandomEngine randEngine = null;
	/**
	 * Uniform distribution to accomplish frequency assignments
	 */
	public static Uniform uniform = null;
	/**
	 * Unit of time in milli seconds
	 */
	private static int timeUnit;
	/**
	 * Draw Frame for cell structure
	 */
	private static DrawCell drawCell;
	/**
	 * Plots the time versus average SNR values graphs
	 */
	public static Plot plot = null;
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SimulationRunner inst = new SimulationRunner();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	private JPanel jPanel1;
	private JTextField sectorNo,dNo,alphaNo,crSectorNo,crDNo,crAlphaNo,radiusField;
	private JLabel label1,label2,label3,label4,label5,label6,label7,label8,label9,label10,label11,label12,label13
					,label14,label15,label16,label17,label18,label19,label20,label21,label22,label23,label24,label25
					,label26,label27,label28,label29;
	private JTextField noCrNodes,noPriNodes,seedValue;
	private JTextField noSlotField,slotDurField,sensingResultField,senseScheduleField,commScheduleField,commDurField;
	private JComboBox seedModel,channelModel;
	private JButton startSimulation;
	static JButton terminateSimulation;
	private JTextField noCalls,callDur,unitTime,simDur;
	private JTextField noFreqs,maxSNR,sinrThresholdFied;
	static JProgressBar progressBar;
	private final static int labelPosLeft = 12;
	private final static int labelPosRight = 490;
	private final static int itemPosLeft = 305;
	private final static int itemPosRight = 783;
	
	public SimulationRunner() {
		super();
		initGUI();
	}
	
	private class SimulationKeyAdapter extends KeyAdapter{

		@Override
		public void keyTyped(KeyEvent e) {
			super.keyTyped(e);
			if(e.getKeyChar()=='\n')
				startSimulation();
		}
		
	} 
	private SimulationKeyAdapter keyAdapter = new SimulationKeyAdapter();
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			{
				jPanel1 = new JPanel();
				getContentPane().add(jPanel1, BorderLayout.CENTER);
				jPanel1.setLayout(null);
				
				initMainOptionsGUI();
				initFrameOptionsGUI();
				initTrafficOptionsGUI();
				initZoneOptionsGUI();
				initFrequencyOptionsGUI();
			}
			pack();
			this.setSize(925, 565);
			this.setResizable(false);
			this.setTitle("Simulator");
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	private void initMainOptionsGUI()
	{
		/*
		 * Main Options
		 */
		{
			label1 = new JLabel();
			jPanel1.add(label1);
			label1.setText("Number of CR Nodes");
			label1.setBounds(labelPosLeft, 15, 165, 16);
		}
		{
			noCrNodes = new JTextField();
			jPanel1.add(noCrNodes);
			noCrNodes.setBounds(itemPosLeft, 12, 120, 23);
			noCrNodes.setText("6");
			noCrNodes.addKeyListener(keyAdapter);
		}
		{
			label2 = new JLabel();
			jPanel1.add(label2);
			label2.setText("Number of Primary Nodes");
			label2.setBounds(labelPosLeft, 50, 165, 16);
		}
		{
			noPriNodes = new JTextField();
			jPanel1.add(noPriNodes);
			noPriNodes.setBounds(itemPosLeft, 47, 120, 23);
			noPriNodes.setText("15");
			noPriNodes.addKeyListener(keyAdapter);
		}
		{
			label7 = new JLabel();
			jPanel1.add(label7);
			label7.setText("Channel Model");
			label7.setBounds(labelPosLeft, 85, 165, 16);
		}
		{
			ComboBoxModel channelModelModel = 
				new DefaultComboBoxModel(
							new String[] { "Simple Channel", "Lognormal Channel"});
			channelModel = new JComboBox();
			jPanel1.add(channelModel);
			channelModel.setModel(channelModelModel);
			channelModel.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

				}
			});
			jPanel1.add(channelModel);
			channelModel.setBounds(itemPosLeft, 82, 120, 23);
			channelModel.addKeyListener(keyAdapter);
		}
		{
			label12 = new JLabel();
			jPanel1.add(label12);
			label12.setText("Simulation Duration (unit time)");
			label12.setBounds(labelPosLeft, 120, 275, 16);
		}
		{
			simDur = new JTextField();
			jPanel1.add(simDur);
			simDur.setBounds(itemPosLeft, 120, 120, 23);
			simDur.setText("100");
			simDur.addKeyListener(keyAdapter);
		}
		{
			label17 = new JLabel();
			jPanel1.add(label17);
			label17.setText("Max SNR Value (dB)");
			label17.setBounds(labelPosLeft, 155, 165, 16);
		}
		{
			maxSNR = new JTextField();
			jPanel1.add(maxSNR);
			maxSNR.setBounds(itemPosLeft, 155, 120, 23);
			maxSNR.setText("10");
			maxSNR.addKeyListener(keyAdapter);
		}
		{
			label16 = new JLabel();
			jPanel1.add(label16);
			label16.setText("SINR Threshold Value to Communicate(dB)");
			label16.setBounds(labelPosLeft, 190, 275, 16);
		}
		{
			sinrThresholdFied = new JTextField();
			jPanel1.add(sinrThresholdFied);
			sinrThresholdFied.setBounds(itemPosLeft, 190, 120, 23);
			sinrThresholdFied.setText("1");
			sinrThresholdFied.addKeyListener(keyAdapter);
		}
		{
			label21 = new JLabel();
			jPanel1.add(label21);
			label21.setText("Seed Model");
			label21.setBounds(labelPosLeft, 225, 165, 16);
		}
		{
			ComboBoxModel seedModelModel = 
				new DefaultComboBoxModel(
							new String[] { "Random Seed", "Constant Seed"});
			seedModel = new JComboBox();
			jPanel1.add(seedModel);
			seedModel.setModel(seedModelModel);
			seedModel.setSelectedIndex(1);
			seedModel.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if(seedModel.getSelectedIndex() == 0){
						label22.setVisible(false);
						seedValue.setVisible(false);
					}
					else{
						label22.setVisible(true);
						seedValue.setVisible(true);
					}
				}
			});
			jPanel1.add(seedModel);
			seedModel.setBounds(itemPosLeft, 225, 120, 23);
			seedModel.addKeyListener(keyAdapter);
		}
		{
			label22 = new JLabel();
			jPanel1.add(label22);
			label22.setText("Seed Value");
			label22.setBounds(labelPosLeft, 260, 165, 16);
		}
		{
			seedValue = new JTextField();
			jPanel1.add(seedValue);
			seedValue.setBounds(itemPosLeft, 260, 120, 23);
			seedValue.setText("111211211");
			seedValue.addKeyListener(keyAdapter);
		}
		{
			startSimulation = new JButton();
			jPanel1.add(startSimulation);
			startSimulation.setText("Start");
			startSimulation.setBounds(itemPosRight, 498, 120, 23);
			startSimulation.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					startSimulation();
				}

			});
			startSimulation.addKeyListener(keyAdapter);
		}
		{
			terminateSimulation = new JButton();
			jPanel1.add(terminateSimulation);
			terminateSimulation.setText("Terminate");
			terminateSimulation.setBounds(labelPosRight, 498, 120, 23);
			terminateSimulation.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if(crSensor!=null){
						if(!crSensor.isFinished()){
							priTrafGen.terminateAllThreads();
							crSensor.terminate();
						}
					}
				}

			});
			terminateSimulation.setVisible(false);

		}
		{
			progressBar = new JProgressBar();
			progressBar.setStringPainted(true);
			jPanel1.add(progressBar);
			progressBar.setBounds(labelPosLeft, 498, 165, 23);
			progressBar.setVisible(false);
		}
	}
	
	private void initFrameOptionsGUI()
	{
		{
			label23 = new JLabel();
			jPanel1.add(label23);
			label23.setText("Frame Structure Options");
			label23.setBounds(labelPosRight, 15, 165, 16);
		}
		{
			label24 = new JLabel();
			jPanel1.add(label24);
			label24.setText("Number of Sensing Slots");
			label24.setBounds(labelPosRight, 50, 165, 16);
		}
		{
			noSlotField = new JTextField();
			jPanel1.add(noSlotField);
			noSlotField.setBounds(itemPosRight, 50, 120, 23);
			noSlotField.setText("4");
			noSlotField.addKeyListener(keyAdapter);
		}
		{
			label25 = new JLabel();
			jPanel1.add(label25);
			label25.setText("Sensing Slot Duration (unit time)");
			label25.setBounds(labelPosRight, 85, 275, 16);
		}
		{
			slotDurField = new JTextField();
			jPanel1.add(slotDurField);
			slotDurField.setBounds(itemPosRight, 85, 120, 23);
			slotDurField.setText("0.2");
			slotDurField.addKeyListener(keyAdapter);
		}
		{
			label26 = new JLabel();
			jPanel1.add(label26);
			label26.setText("Dur. of sensing result ack. (unit time)");
			label26.setBounds(labelPosRight, 120, 275, 16);
		}
		{
			sensingResultField = new JTextField();
			jPanel1.add(sensingResultField);
			sensingResultField.setBounds(itemPosRight, 120, 120, 23);
			sensingResultField.setText("0.2");
			sensingResultField.addKeyListener(keyAdapter);
		}
		{
			label27 = new JLabel();
			jPanel1.add(label27);
			label27.setText("Sensing Schedule Advert. Dur. (unit time)");
			label27.setBounds(labelPosRight, 155, 275, 16);
		}
		{
			senseScheduleField = new JTextField();
			jPanel1.add(senseScheduleField);
			senseScheduleField.setBounds(itemPosRight, 155, 120, 23);
			senseScheduleField.setText("0.2");
			senseScheduleField.addKeyListener(keyAdapter);
		}
		{
			label28 = new JLabel();
			jPanel1.add(label28);
			label28.setText("Communication Duration (unit time)");
			label28.setBounds(labelPosRight, 190, 275, 16);
		}
		{
			commDurField = new JTextField();
			jPanel1.add(commDurField);
			commDurField.setBounds(itemPosRight, 190, 120, 23);
			commDurField.setText("0.7");
			commDurField.addKeyListener(keyAdapter);
		}
		{
			label29 = new JLabel();
			jPanel1.add(label29);
			label29.setText("Communication Schedule Advert. Dur. (unit time)");
			label29.setBounds(labelPosRight, 225, 275, 16);
		}
		{
			commScheduleField = new JTextField();
			jPanel1.add(commScheduleField);
			commScheduleField.setBounds(itemPosRight, 225, 120, 23);
			commScheduleField.setText("0.2");
			commScheduleField.addKeyListener(keyAdapter);
		}
	}
	
	private void initZoneOptionsGUI()
	{
		{
			label3 = new JLabel();
			jPanel1.add(label3);
			label3.setText("Zone");
			label3.setBounds(labelPosRight, 260, 165, 16);
		}
		{
			label4 = new JLabel();
			jPanel1.add(label4);
			label4.setText("Sector Number");
			label4.setBounds(labelPosRight, 295, 165, 16);
		}
		{
			sectorNo = new JTextField();
			jPanel1.add(sectorNo);
			sectorNo.setBounds(itemPosRight, 295, 50, 23);
			sectorNo.setText("3");
			sectorNo.addKeyListener(keyAdapter);
		}
		{
			label18 = new JLabel();
			jPanel1.add(label18);
			label18.setText("/");
			label18.setBounds(itemPosRight+59, 295, 18, 23);
		}
		{
			crSectorNo = new JTextField();
			jPanel1.add(crSectorNo);
			crSectorNo.setBounds(itemPosRight+70, 295, 50, 23);
			crSectorNo.setText("0");
			crSectorNo.addKeyListener(keyAdapter);
		}
		{
			label5 = new JLabel();
			jPanel1.add(label5);
			label5.setText("D Number");
			label5.setBounds(labelPosRight, 330, 165, 16);
		}
		{
			dNo = new JTextField();
			jPanel1.add(dNo);
			dNo.setBounds(itemPosRight, 330, 50, 23);
			dNo.setText("3");
			dNo.addKeyListener(keyAdapter);
		}
		{
			label19 = new JLabel();
			jPanel1.add(label19);
			label19.setText("/");
			label19.setBounds(itemPosRight+59, 330, 18, 23);
		}
		{
			crDNo = new JTextField();
			jPanel1.add(crDNo);
			crDNo.setBounds(itemPosRight+70, 330, 50, 23);
			crDNo.setText("0");
			crDNo.addKeyListener(keyAdapter);
		}
		{
			label6 = new JLabel();
			jPanel1.add(label6);
			label6.setText("Alpha Number");
			label6.setBounds(labelPosRight, 365, 165, 16);
		}
		{
			alphaNo = new JTextField();
			jPanel1.add(alphaNo);
			alphaNo.setBounds(itemPosRight, 365, 50, 23);
			alphaNo.setText("4");
			alphaNo.addKeyListener(keyAdapter);
		}
		{
			label20 = new JLabel();
			jPanel1.add(label20);
			label20.setText("/");
			label20.setBounds(itemPosRight+59, 365, 18, 23);
		}
		{
			crAlphaNo = new JTextField();
			jPanel1.add(crAlphaNo);
			crAlphaNo.setBounds(itemPosRight+70, 365, 50, 23);
			crAlphaNo.setText("0");
			crAlphaNo.addKeyListener(keyAdapter);
		}
		{
			label13 = new JLabel();
			jPanel1.add(label13);
			label13.setText("Radius of Cell (100 m)");
			label13.setBounds(labelPosRight, 400, 165, 16);
		}
		{
			radiusField = new JTextField();
			jPanel1.add(radiusField);
			radiusField.setBounds(itemPosRight, 400, 120, 23);
			radiusField.setText("30");
			radiusField.addKeyListener(keyAdapter);
		}
	}
	
	private void initTrafficOptionsGUI()
	{
		{
			label8 = new JLabel();
			jPanel1.add(label8);
			label8.setText("Traffic Model");
			label8.setBounds(labelPosLeft, 295, 165, 16);
		}
		{
			label9 = new JLabel();
			jPanel1.add(label9);
			label9.setText("Number of Calls/unit time");
			label9.setBounds(labelPosLeft, 330, 165, 16);
		}
		{
			noCalls = new JTextField();
			jPanel1.add(noCalls);
			noCalls.setBounds(itemPosLeft, 330, 120, 23);
			noCalls.setText("1");
			noCalls.addKeyListener(keyAdapter);
		}
		{
			label10 = new JLabel();
			jPanel1.add(label10);
			label10.setText("Call Duration (unit time)");
			label10.setBounds(labelPosLeft, 365, 165, 16);
		}
		{
			callDur = new JTextField();
			jPanel1.add(callDur);
			callDur.setBounds(itemPosLeft, 365, 120, 23);
			callDur.setText("4");
			callDur.addKeyListener(keyAdapter);
		}
		{
			label11 = new JLabel();
			jPanel1.add(label11);
			label11.setText("Unit Time Dur. (msec)");
			label11.setBounds(labelPosLeft, 400, 165, 16);
		}
		{
			unitTime = new JTextField();
			jPanel1.add(unitTime);
			unitTime.setBounds(itemPosLeft, 400, 120, 23);
			unitTime.setText("100");
			unitTime.addKeyListener(keyAdapter);
		}
	}
	
	private void initFrequencyOptionsGUI()
	{
		{
			label14 = new JLabel();
			jPanel1.add(label14);
			label14.setText("Frequency Options");
			label14.setBounds(labelPosRight, 435, 165, 16);
		}
		{
			label15 = new JLabel();
			jPanel1.add(label15);
			label15.setText("Number of Frequencies");
			label15.setBounds(labelPosRight, 470, 165, 16);
		}
		{
			noFreqs = new JTextField();
			jPanel1.add(noFreqs);
			noFreqs.setBounds(itemPosRight, 470, 120, 23);
			noFreqs.setText("12");
			noFreqs.addKeyListener(keyAdapter);
		}
	}
	
	/**
	 * Initializes the main simulation threads
	 */
	public void startSimulation()
	{
		if(crSensor!=null){
			if(!crSensor.isFinished())
				return;
		}
		int sectrNo = 0;
		double dNumber = 0;
		int alpha = 0;
		double radius = 0;
		
		int numberOfCrNodes = 0;
		int numberOfPriNodes = 0;
		
		double numberOfCalls = 0;
		double callDura = 0;
		long simDura = 0;
		
		int numberOfFreq = 0;
		int maxFreqCR = 0;
		double maxSnr = 0;
		double sinrThreshold = 0;
		int crAlpha = 0;
		int crSector = 0;
		int crD = 0;
		
		double slotDur = 0.0;
		double senseScheduleAdvertisement = 0.0;
		double commScheduleAdvertisement = 0.0;
		double commDur = 0.0;
		double senseResultAdvertisement = 0.0;
		ArrayList<Double> setOfD = new ArrayList<Double>();
		try{
			slotDur = Double.parseDouble(slotDurField.getText());
			senseScheduleAdvertisement = Double.parseDouble(senseScheduleField.getText());
			commScheduleAdvertisement = Double.parseDouble(commScheduleField.getText());
			commDur = Double.parseDouble(commDurField.getText());
			senseResultAdvertisement = Double.parseDouble(sensingResultField.getText());
			
			if(seedModel.getSelectedIndex()==0){				//If seed model is random
				randEngine = new MersenneTwister(new Date());	//Give date as seed
			}
			else{
				int seed = Integer.parseInt(seedValue.getText());	//Otherwise get seed from user
				randEngine = new MersenneTwister(seed);
			}
			uniform = new Uniform(randEngine);			//Create Uniform distribution to select number of frequencies and their values
			int remainFreq = numberOfFreq = Integer.parseInt(noFreqs.getText());	//Get number of frequencies
			maxSnr = Double.parseDouble(maxSNR.getText());							//Get max SNR value
			sinrThreshold = Double.parseDouble(sinrThresholdFied.getText());
			wc = new WirelessChannel(channelModel.getSelectedIndex(), numberOfFreq, maxSnr, sinrThreshold);	//Create a wireless channel
			sectrNo = Integer.parseInt(sectorNo.getText());			//Get number of sectors in the cell
			dNumber = Integer.parseInt(dNo.getText());				//Get number of d's
			alpha = Integer.parseInt(alphaNo.getText());			//Get number of alpha's
			radius = Double.parseDouble(radiusField.getText());		//Get radius of the cell
			alpha = (360/sectrNo)/alpha;							//Evaluate the angle associated to alpha
			double temp = radius / dNumber;							//Evaluate length of each d as they will be equal
			double inc = temp;
			for(int i = 0;i<dNumber;i++,temp+=inc)
				setOfD.add(temp);									//Create set of d's
			cell = new Cell(crBase, radius, sectrNo, alpha, setOfD);//Create a cell
			
			numberOfCrNodes = Integer.parseInt(noCrNodes.getText());	//Get number of CR nodes
			numberOfPriNodes = Integer.parseInt(noPriNodes.getText());	//Get number of primary nodes
			maxFreqCR = Integer.parseInt(noSlotField.getText());			//Get max number of frequencies a node can sense
			crAlpha = Integer.parseInt(crAlphaNo.getText());			//Get alpha number CR nodes will be in
			crSector = Integer.parseInt(crSectorNo.getText());			//Get sector number CR nodes will be in
			crD = Integer.parseInt(crDNo.getText());					//Get d interval CR nodes will be in
			double dmin;
			if(crD==0)
				dmin=0;
			else
				dmin = setOfD.get(crD-1);
			double dmax = setOfD.get(crD);
			drawCell = new DrawCell((int)radius, sectrNo, crSector, Integer.parseInt(alphaNo.getText()), crAlpha,
																(int)dmin, (int)dmax, numberOfCrNodes, numberOfPriNodes);
			crBase = new CRBase(new Point2D.Double(0, 0),0,maxFreqCR);		//Create a CR base station in the origin
			for(int i = 0; i<numberOfCrNodes ;i++){
				crNodes.add(new CRNode(i,Cell.deployNodeinZone(crSector, crAlpha, crD), 0));
				wc.registerNode(crNodes.get(i));							//Register CR nodes
				DrawCell.paintCrNode(crNodes.get(i));
			}
			
			numberOfCalls = Double.parseDouble(noCalls.getText());	//Get number of calls per unit time
			callDura = Double.parseDouble(callDur.getText());	//Get call duration in terms of unit time
			timeUnit = Integer.parseInt(unitTime.getText());	//Get unit time duration in terms of milliseconds
			priTrafGen = new PrimaryTrafficGenerator(numberOfCalls, callDura, timeUnit);
			simDura = Long.parseLong(simDur.getText());			//Get duration of the simulation in terms of unit time
			plot = new Plot(numberOfFreq);						//Create a new plotter
			CRNode.initializeAverageSnr(numberOfFreq);			//Set average SNR values to zero
			progressBar.setValue(0);							//Initialize progress bar
			progressBar.setVisible(true);						//Make it visible
			CRNode.createLogFile("log.txt");
			terminateSimulation.setVisible(true);
			for(int i = 0;i<numberOfPriNodes;i++){
				priTrafGenNodes.add(new PrimaryTrafficGeneratorNode(new Point2D.Double(0,0), 0,i));	//Create primary traffic
				wc.registerNode(priTrafGenNodes.get(i));					//generator nodes and register them to the channel
				priTrafGen.registerNode(priTrafGenNodes.get(i), simDura);	//and create threads for each of them
			}
			crSensor = new CRSensorThread((int)simDura, timeUnit, maxFreqCR, slotDur, senseScheduleAdvertisement,
												commScheduleAdvertisement, commDur, senseResultAdvertisement);
		}catch(NumberFormatException nfe){
			JOptionPane.showMessageDialog(this, "Invalid argument:\n"+nfe.getMessage(),
					"Simulation", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Clears the data of the simulation
	 */
	public static void clear()
	{
		crNodes.clear();			//Delete CR nodes
		priTrafGenNodes.clear();	//Delete primary nodes
		drawCell.terminate();
	}
}

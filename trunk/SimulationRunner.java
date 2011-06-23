package firstproject;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
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
import javax.swing.WindowConstants;

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
	private final static int RANDOM_ENGINE_SEED = 111211211;
	/**
	 * Random generator for all random number generation operations in the simulation
	 */
	public static RandomEngine randEngine = new MersenneTwister(RANDOM_ENGINE_SEED);
	/**
	 * Uniform distribution to accomplish frequency assignments
	 */
	public static Uniform uniform = new Uniform(randEngine);
	/**
	 * Unit of time in milli seconds
	 */
	private static int timeUnit;
	/**
	 * File handler of the logger
	 */
	static FileHandler handler = null;
	/**
	 * Logger to log SNR values sensed by the CR nodes 
	 */
	static Logger logger = null;
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			// Create a file handler that write log record to a file called log.txt
			handler = new FileHandler("log.txt");

			// Add to the desired logger
			logger = Logger.getLogger("Simulation is started");
			logger.addHandler(handler);
        } catch (IOException e) {
        }
		crBase = new CRBase(new Point2D.Double(0, 0));		//Create a CR base station in the origin
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SimulationRunner inst = new SimulationRunner();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	private JPanel jPanel1;
	private JTextField sectorNo;
	private JTextField dNo;
	private JTextField alphaNo;
	private JTextField crSectorNo;
	private JTextField crDNo;
	private JTextField crAlphaNo;
	private JLabel label3;
	private JLabel label2;
	private JLabel label1;
	private JTextField noCrNodes;
	private JTextField noPriNodes;
	private JLabel label4;
	private JLabel label5;
	private JLabel label6;
	private JLabel label7;
	private JLabel label8;
	private JLabel label9;
	private JLabel label10;
	private JLabel label11;
	private JLabel label12;
	private JLabel label13;
	private JLabel label14;
	private JLabel label15;
	private JLabel label16;
	private JLabel label17;
	private JLabel label18;
	private JLabel label19;
	private JLabel label20;
	private JComboBox channelModel;
	private JButton startSimulation;
	static JButton terminateSimulation;
	private JTextField noCalls;
	private JTextField callDur;
	private JTextField unitTime;
	private JTextField simDur;
	private JTextField radiusField;
	private JTextField noFreqs;
	private JTextField maxFreq;
	private JTextField maxSNR;
	static JProgressBar progressBar;
	private final static int labelPosLeft = 12;
	private final static int labelPosRight = 380;
	private final static int itemPosLeft = 195;
	private final static int itemPosRight = 523;
	
	public SimulationRunner() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			{
				jPanel1 = new JPanel();
				getContentPane().add(jPanel1, BorderLayout.CENTER);
				jPanel1.setLayout(null);
				/*
				 * Main Options
				 */
				{
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
						noCrNodes.setText("5");
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
						noPriNodes.setText("10");
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
					}
					{
						label12 = new JLabel();
						jPanel1.add(label12);
						label12.setText("Simulation Duration");
						label12.setBounds(labelPosLeft, 120, 165, 16);
					}
					{
						simDur = new JTextField();
						jPanel1.add(simDur);
						simDur.setBounds(itemPosLeft, 120, 120, 23);
						simDur.setText("100");
					}
					{
						label17 = new JLabel();
						jPanel1.add(label17);
						label17.setText("Max SNR Value");
						label17.setBounds(labelPosLeft, 155, 165, 16);
					}
					{
						maxSNR = new JTextField();
						jPanel1.add(maxSNR);
						maxSNR.setBounds(itemPosLeft, 155, 120, 23);
						maxSNR.setText("10");
					}
					{
						startSimulation = new JButton();
						jPanel1.add(startSimulation);
						startSimulation.setText("Start");
						startSimulation.setBounds(itemPosRight, 323, 120, 23);
						startSimulation.addMouseListener(new MouseAdapter() {

							@Override
							public void mouseClicked(MouseEvent e) {
								startSimulation();
							}

						});

					}
					{
						terminateSimulation = new JButton();
						jPanel1.add(terminateSimulation);
						terminateSimulation.setText("Terminate");
						terminateSimulation.setBounds(labelPosRight, 323, 120, 23);
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
						progressBar.setBounds(labelPosLeft, 323, 165, 23);
						progressBar.setVisible(false);
					}
				}
				/*
				 * Zone Related Options
				 */
				{
					{
						label3 = new JLabel();
						jPanel1.add(label3);
						label3.setText("Zone");
						label3.setBounds(labelPosRight, 153, 165, 16);
					}
					{
						label4 = new JLabel();
						jPanel1.add(label4);
						label4.setText("Sector Number");
						label4.setBounds(labelPosRight, 188, 165, 16);
					}
					{
						sectorNo = new JTextField();
						jPanel1.add(sectorNo);
						sectorNo.setBounds(itemPosRight, 183, 50, 23);
						sectorNo.setText("3");
					}
					{
						label18 = new JLabel();
						jPanel1.add(label18);
						label18.setText("/");
						label18.setBounds(itemPosRight+59, 183, 18, 23);
					}
					{
						crSectorNo = new JTextField();
						jPanel1.add(crSectorNo);
						crSectorNo.setBounds(itemPosRight+70, 183, 50, 23);
						crSectorNo.setText("0");
					}
					{
						label5 = new JLabel();
						jPanel1.add(label5);
						label5.setText("D Number");
						label5.setBounds(labelPosRight, 223, 165, 16);
					}
					{
						dNo = new JTextField();
						jPanel1.add(dNo);
						dNo.setBounds(itemPosRight, 218, 50, 23);
						dNo.setText("3");
					}
					{
						label19 = new JLabel();
						jPanel1.add(label19);
						label19.setText("/");
						label19.setBounds(itemPosRight+59, 218, 18, 23);
					}
					{
						crDNo = new JTextField();
						jPanel1.add(crDNo);
						crDNo.setBounds(itemPosRight+70, 218, 50, 23);
						crDNo.setText("0");
					}
					{
						label6 = new JLabel();
						jPanel1.add(label6);
						label6.setText("Alpha Number");
						label6.setBounds(labelPosRight, 258, 165, 16);
					}
					{
						alphaNo = new JTextField();
						jPanel1.add(alphaNo);
						alphaNo.setBounds(itemPosRight, 253, 50, 23);
						alphaNo.setText("4");
					}
					{
						label20 = new JLabel();
						jPanel1.add(label20);
						label20.setText("/");
						label20.setBounds(itemPosRight+59, 253, 18, 23);
					}
					{
						crAlphaNo = new JTextField();
						jPanel1.add(crAlphaNo);
						crAlphaNo.setBounds(itemPosRight+70, 253, 50, 23);
						crAlphaNo.setText("0");
					}
					{
						label13 = new JLabel();
						jPanel1.add(label13);
						label13.setText("Radius of Cell");
						label13.setBounds(labelPosRight, 293, 165, 16);
					}
					{
						radiusField = new JTextField();
						jPanel1.add(radiusField);
						radiusField.setBounds(itemPosRight, 288, 120, 23);
						radiusField.setText("20");
					}
				}
				/*
				 * Traffic Model Related Options
				 */
				{
					{
						label8 = new JLabel();
						jPanel1.add(label8);
						label8.setText("Traffic Model");
						label8.setBounds(labelPosRight, 15, 165, 16);
					}
					{
						label9 = new JLabel();
						jPanel1.add(label9);
						label9.setText("Number of Calls");
						label9.setBounds(labelPosRight, 50, 165, 16);
					}
					{
						noCalls = new JTextField();
						jPanel1.add(noCalls);
						noCalls.setBounds(itemPosRight, 50, 120, 23);
						noCalls.setText("2");
					}
					{
						label10 = new JLabel();
						jPanel1.add(label10);
						label10.setText("Call Duration");
						label10.setBounds(labelPosRight, 85, 165, 16);
					}
					{
						callDur = new JTextField();
						jPanel1.add(callDur);
						callDur.setBounds(itemPosRight, 85, 120, 23);
						callDur.setText("2");
					}
					{
						label11 = new JLabel();
						jPanel1.add(label11);
						label11.setText("Unit Time Dur.");
						label11.setBounds(labelPosRight, 120, 165, 16);
					}
					{
						unitTime = new JTextField();
						jPanel1.add(unitTime);
						unitTime.setBounds(itemPosRight, 120, 120, 23);
						unitTime.setText("100");
					}
				}
				/*
				 * Frequency Related Options
				 */
				{
					{
						label14 = new JLabel();
						jPanel1.add(label14);
						label14.setText("Frequency Options");
						label14.setBounds(labelPosLeft, 190, 165, 16);
					}
					{
						label15 = new JLabel();
						jPanel1.add(label15);
						label15.setText("Number of Frequencies");
						label15.setBounds(labelPosLeft, 225, 165, 16);
					}
					{
						noFreqs = new JTextField();
						jPanel1.add(noFreqs);
						noFreqs.setBounds(itemPosLeft, 225, 120, 23);
						noFreqs.setText("10");
					}
					{
						label16 = new JLabel();
						jPanel1.add(label16);
						label16.setText("Maximum # of Freq. per CR");
						label16.setBounds(labelPosLeft, 260, 165, 16);
					}
					{
						maxFreq = new JTextField();
						jPanel1.add(maxFreq);
						maxFreq.setBounds(itemPosLeft, 260, 120, 23);
						maxFreq.setText("4");
					}
				}
			}
			pack();
			this.setSize(665, 390);
			this.setResizable(false);
			this.setTitle("Simulator");
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
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
		int crAlpha = 0;
		int crSector = 0;
		int crD = 0;
		ArrayList<Double> setOfD = new ArrayList<Double>();
		try{
			int remainFreq = numberOfFreq = Integer.parseInt(noFreqs.getText());	//Get number of frequencies
			maxSnr = Double.parseDouble(maxSNR.getText());							//Get max SNR value
			wc = new WirelessChannel(channelModel.getSelectedIndex(), numberOfFreq, maxSnr);	//Create a wireless channel
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
			maxFreqCR = Integer.parseInt(maxFreq.getText());			//Get max number of frequencies a node can sense
			crAlpha = Integer.parseInt(crAlphaNo.getText());
			crSector = Integer.parseInt(crSectorNo.getText());
			crD = Integer.parseInt(crDNo.getText());
			for(int i = 0; i<numberOfCrNodes ;i++){
				ArrayList<Integer> freqList = new ArrayList<Integer>();
				if(remainFreq>0){
					for(int j=0,k=numberOfFreq-remainFreq;j<maxFreqCR&&k<numberOfFreq;j++,k++)	//First nodes covers
						freqList.add(k);														//all frequencies
					remainFreq-=maxFreqCR;
				}
				else{
					int freqCount = uniform.nextIntFromTo(1, maxFreqCR);	//Later nodes pick random amount of random
					for(;freqList.size()!=freqCount;){						//frequencies to sense
						int freq = uniform.nextIntFromTo(0, numberOfFreq-1);//Pick a random frequency
						if(freqList.contains(freq))
							continue;
						freqList.add(freq);									//If its not in the list already add it to the list
					}
				}
				crNodes.add(new CRNode(Cell.deployNodeinZone(crSector, crAlpha, crD), 0, freqList));
				wc.registerNode(crNodes.get(i));							//Register CR nodes
			}
			
			numberOfCalls = Double.parseDouble(noCalls.getText());			//Get number of calls per unit time
			callDura = Double.parseDouble(callDur.getText());				//Get call duration in terms of unit time
			timeUnit = Integer.parseInt(unitTime.getText());				//Get unit time duration in terms of milliseconds
			priTrafGen = new PrimaryTrafficGenerator(numberOfCalls, callDura, timeUnit);
			simDura = Long.parseLong(simDur.getText());						//Get duration of the simulation in terms of unit time
			for(int i = 0;i<numberOfPriNodes;i++){
				priTrafGenNodes.add(new PrimaryTrafficGeneratorNode(new Point2D.Double(0,0), 0));	//Create primary traffic
				wc.registerNode(priTrafGenNodes.get(i));					//generator nodes and register them to the channel
				priTrafGen.registerNode(priTrafGenNodes.get(i), simDura);	//and create threads for each of them
			}
			progressBar.setValue(0);								//Initialize progress bar
			progressBar.setVisible(true);							//Make it visible
			crSensor = new CRSensorThread((int)simDura, timeUnit);	//Create thread for CR sensors
			terminateSimulation.setVisible(true);
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
	}
}

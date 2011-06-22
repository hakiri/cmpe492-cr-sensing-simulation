package firstproject;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class SimulationRunner extends JFrame{

	/**
	 * Main wireless channel which all types of nodes are accessing
	 */
	public static WirelessChannel wc = new WirelessChannel(WirelessChannel.SIMPLECH, 10,5);
	/**
	 * Cognitive radio cell structure
	 */
	public static Cell cell = null;
	/**
	 * Primary traffic generator for wireless channel frequencies
	 */
	public static PrimaryTrafficGenerator priTrafGen = null;
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
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		crBase = new CRBase(new Point2D.Double(0, 0));
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
	private JComboBox channelModel;
	private JButton startSimulation;
	private JTextField alphaNo;
	private JTextField noCalls;
	private JTextField callDur;
	private JTextField unitTime;
	private JTextField simDur;
	private JTextField radiusField;
	private JTextField noFreqs;
	private JTextField maxFreq;
	private final static int labelPosLeft = 12;
	private final static int labelPosRight = 360;
	private final static int itemPosLeft = 195;
	private final static int itemPosRight = 543;
	
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
				}
				/*
				 * Zone Related Options
				 */
				{
					{
						label3 = new JLabel();
						jPanel1.add(label3);
						label3.setText("Zone");
						label3.setBounds(labelPosLeft, 153, 165, 16);
					}
					{
						label4 = new JLabel();
						jPanel1.add(label4);
						label4.setText("Sector Number");
						label4.setBounds(labelPosLeft, 188, 165, 16);
					}
					{
						sectorNo = new JTextField();
						jPanel1.add(sectorNo);
						sectorNo.setBounds(itemPosLeft, 183, 120, 23);
					}
					{
						label5 = new JLabel();
						jPanel1.add(label5);
						label5.setText("D Number");
						label5.setBounds(labelPosLeft, 223, 165, 16);
					}
					{
						dNo = new JTextField();
						jPanel1.add(dNo);
						dNo.setBounds(itemPosLeft, 218, 120, 23);
					}
					{
						label6 = new JLabel();
						jPanel1.add(label6);
						label6.setText("Alpha Number");
						label6.setBounds(labelPosLeft, 258, 165, 16);
					}
					{
						alphaNo = new JTextField();
						jPanel1.add(alphaNo);
						alphaNo.setBounds(itemPosLeft, 253, 120, 23);
					}
					{
						label13 = new JLabel();
						jPanel1.add(label13);
						label13.setText("Radius of Cell");
						label13.setBounds(labelPosLeft, 293, 165, 16);
					}
					{
						radiusField = new JTextField();
						jPanel1.add(radiusField);
						radiusField.setBounds(itemPosLeft, 288, 120, 23);
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
						label14.setBounds(labelPosRight, 155, 165, 16);
					}
					{
						label15 = new JLabel();
						jPanel1.add(label15);
						label15.setText("Number of Frequencies");
						label15.setBounds(labelPosRight, 190, 165, 16);
					}
					{
						noFreqs = new JTextField();
						jPanel1.add(noFreqs);
						noFreqs.setBounds(itemPosRight, 190, 120, 23);
					}
					{
						label16 = new JLabel();
						jPanel1.add(label16);
						label16.setText("Maximum # of Freq. per CR");
						label16.setBounds(labelPosRight, 225, 165, 16);
					}
					{
						maxFreq = new JTextField();
						jPanel1.add(maxFreq);
						maxFreq.setBounds(itemPosRight, 225, 120, 23);
					}
				}
			}
			pack();
			this.setSize(690, 393);
			this.setResizable(false);
			this.setTitle("Simulator");
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	public void startSimulation()
	{
		int sectrNo = 0;
		double dNumber = 0;
		int alpha = 0;
		double radius = 0;
		
		int numberOfCrNodes = 0;
		int numberOfPriNodes = 0;
		
		int numberOfCalls = 0;
		int callDura = 0;
		long simDura = 0;
		
		int numberOfFreq = 0;
		int maxFreqCR = 0;
		ArrayList<Double> setOfD = new ArrayList<Double>();
		try{
			sectrNo = Integer.parseInt(sectorNo.getText());
			dNumber = Integer.parseInt(dNo.getText());
			alpha = Integer.parseInt(alphaNo.getText());
			radius = Double.parseDouble(radiusField.getText());
			alpha = (360/sectrNo)/alpha;
			double temp = radius / dNumber;
			double inc = temp;
			for(int i = 0;i<dNumber;i++,temp+=inc)
				setOfD.add(temp);
			//TODO make radius double in Cell constructor
			//TODO make setOfD ArrayList<Double> in Cell Constructor
			cell = new Cell(crBase, (int)radius, sectrNo, alpha, null/*setOfD*/);
			
			numberOfCrNodes = Integer.parseInt(noCrNodes.getText());
			numberOfPriNodes = Integer.parseInt(noPriNodes.getText());
			int remainFreq = numberOfFreq = Integer.parseInt(noFreqs.getText());
			maxFreqCR = Integer.parseInt(maxFreq.getText());
			for(int i = 0; i<numberOfCrNodes ;i++){
				ArrayList<Integer> freqList = new ArrayList<Integer>();
				if(remainFreq>0){
					for(int j=0,k=numberOfFreq-remainFreq;j<maxFreqCR;j++,k++)
						freqList.add(k);
					remainFreq-=maxFreqCR;
				}
				else{
					int freqCount = uniform.nextIntFromTo(1, maxFreqCR);
					for(;freqList.size()!=freqCount;){
						int freq = uniform.nextIntFromTo(0, numberOfFreq-1);
						if(freqList.contains(freq))
							continue;
						freqList.add(freq);
					}
				}
				crNodes.add(new CRNode(new Point2D.Double(0, 0), 0, freqList));	//TODO give random position in a zone
				wc.registerNode(crNodes.get(i));
			}
			
			numberOfCalls = Integer.parseInt(noCalls.getText());
			callDura = Integer.parseInt(callDur.getText());
			timeUnit = Integer.parseInt(unitTime.getText());
			priTrafGen = new PrimaryTrafficGenerator(numberOfCalls, callDura, timeUnit);
			simDura = Long.parseLong(simDur.getText());
			for(int i = 0;i<numberOfPriNodes;i++){
				priTrafGenNodes.add(new PrimaryTrafficGeneratorNode(new Point2D.Double(0,0), 0));
				wc.registerNode(priTrafGenNodes.get(i));
				priTrafGen.registerNode(priTrafGenNodes.get(i), simDura);
			}
			crNodeSimulation();
		}catch(NumberFormatException nfe){
			JOptionPane.showMessageDialog(this, "Invalid argument:\n"+nfe.getMessage(),
					"Simulation", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Performs the CR node related part of the simulation
	 */
	private void crNodeSimulation()
	{
		//TODO crnodes starts simulation
	}
}

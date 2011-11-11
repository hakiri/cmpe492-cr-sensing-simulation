package SimulationRunner;

import Animation.DrawCell;
import Animation.Plot;
import CommunicationEnvironment.Cell;
import CommunicationEnvironment.WirelessChannel;
import DES.Scheduler;
import DESSimulation.CRDESScheduler;
import DESSimulation.DESPrimaryTrafficGenerator;
import MultiThreadedSimulation.PrimaryTrafficGenerator;
import MultiThreadedSimulation.CRSensorThread;
import Nodes.CRBase;
import Nodes.CRNode;
import Nodes.PrimaryTrafficGeneratorNode;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This is the main class of the software. It instantiates necessary classes and
 * create GUI for taking input from the user.
 */
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
	 * CR nodes which sense the wireless channel
	 */
	public static ArrayList<CRNode> crNodes = new ArrayList<CRNode>();
	/**
	 * Primary traffic generator nodes which cause traffic in wireless channel
	 */
	public static ArrayList<PrimaryTrafficGeneratorNode> priTrafGenNodes = new ArrayList<PrimaryTrafficGeneratorNode>();
	/**
	 * Random number generator for all random number generation operations in the simulation
	 */
	public static RandomEngine randEngine = null;
	/**
	 * Unit of time in milli seconds
	 */
	private static double timeUnit;
	/**
	 * Frame to animate cell structure
	 */
	private static DrawCell drawCell;
	/**
	 * Plots the time versus average SNR and SINR values graphs
	 */
	public static Plot plot = null;
	/**
	 * Currently running SimulationRunner instance
	 */
	public static SimulationRunner runner;
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				runner = new SimulationRunner();
				runner.setLocationRelativeTo(null);
				runner.setVisible(true);
			}
		});
	}
	
	/**
	 * Button to terminate an ongoing simulation
	 */
	public static JButton terminateSimulation;
	/**
	 * Progress bar to show progress of the simulation
	 */
	public static JProgressBar progressBar;
	public static JRadioButton animationOnButton, animationOffButton, plotOnButton, plotOffButton;
	private JPanel panel, tabMainPanel, tabZonePanel, mainPanel, framePanel, zonePanel, trafficPanel, frequencyPanel;
	private JTabbedPane tabPane;
	private ArrayList<JTextField> zoneSectorNos, zoneDNos, zoneAlphaNos, zoneCRUsers;
	private JTextField noSlotField,slotDurField,sensingResultField,senseScheduleField,commScheduleField,commDurField,
					   sectorNo,dNo,alphaNo,radiusField,noPriNodes,seedValue,noCalls,callDur,unitTime,simDur,noFreqs,
					   maxSNR,sinrThresholdFied,noZones;
	private JLabel label2,label3,label4,label5,label6,label7,label8,label9,label10,label11,label12,label13,label14,
				   label15,label16,label17,label19,label21,label22,label23,label24,label25,label26,label27,label28,
				   label29,label30;
	private JComboBox seedModel,channelModel,trafficModel;
	private JButton startSimulation, closeButton;
	private ButtonGroup animationOnOff, plotOnOff;
	
	private final static int labelPos = 12;
	private final static int itemPos = 260;
	private final static int panelLeft = 10;
	private final static int panelRight = 425;
	private final static int panelWidth = 400;
	
	/**
	 * Constructor of the class. This method initializes the GUI.
	 */
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
			setLookAndFeel();
			
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			{
				panel = new JPanel();
				getContentPane().add(panel, BorderLayout.CENTER);
				panel.setLayout(null);
				
				tabMainPanel = new JPanel();
				tabMainPanel.setLayout(null);
				tabZonePanel = new JPanel();
				tabZonePanel.setLayout(null);
				
				tabPane = new JTabbedPane();
				panel.add(tabPane);
				tabPane.setBounds(0, 0, 835, 600);
				
				
				initMainOptionsGUI();
				initFrameOptionsGUI();
				initTrafficOptionsGUI();
				initZoneOptionsGUI();
				initFrequencyOptionsGUI();
				addZoneOptinsGUI();
				
				tabPane.add("Main", tabMainPanel);
				tabPane.add("Zone", tabZonePanel);
				
				createButtons();
			}
			pack();
			this.setSize(845, 670);
			this.setResizable(false);
			this.setTitle("Simulator");
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	private void setLookAndFeel()
	{
		LookAndFeel laf = null;
		UIManager.LookAndFeelInfo[] installedLafs = UIManager.getInstalledLookAndFeels();
		for (UIManager.LookAndFeelInfo lafInfo : installedLafs) {
			try { 
				Class lnfClass = Class.forName(lafInfo.getClassName());
				laf = (LookAndFeel)(lnfClass.newInstance());
				if (laf.isSupportedLookAndFeel()) {
					String name = lafInfo.getName();
					if(name.compareToIgnoreCase("Nimbus") == 0)
						break;
					laf = null;
				}
			} catch (Exception e) { // If ANYTHING weird happens, don't add it
				continue;
			}
		}
		if(laf != null){
			try {
				UIManager.setLookAndFeel(laf);
			} catch (UnsupportedLookAndFeelException ex) {
				Logger.getLogger(SimulationRunner.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	private void initMainOptionsGUI()
	{
		mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createTitledBorder("Main Options"));
		mainPanel.setLayout(null);
		{
			label2 = new JLabel();
			mainPanel.add(label2);
			label2.setToolTipText("Number of Primary Users in The CR Cell");
			label2.setText("Number of Primary Nodes");
			label2.setBounds(labelPos, 30, 165, 16);
		}
		{
			noPriNodes = new JTextField();
			mainPanel.add(noPriNodes);
			noPriNodes.setToolTipText("Number of Primary Users in The CR Cell");
			noPriNodes.setBounds(itemPos, 30, 120, 23);
			noPriNodes.setText("50");
			noPriNodes.addKeyListener(keyAdapter);
		}
		{
			label3 = new JLabel();
			mainPanel.add(label3);
			label3.setToolTipText("Model of Wireless Channel");
			label3.setText("Channel Model");
			label3.setBounds(labelPos, 65, 165, 16);
		}
		{
			ComboBoxModel channelModelModel = 
				new DefaultComboBoxModel(
							new String[] { "Simple Channel", "Lognormal Channel"});
			channelModel = new JComboBox();
			mainPanel.add(channelModel);
			channelModel.setToolTipText("Model of Wireless Channel");
			channelModel.setModel(channelModelModel);
			channelModel.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

				}
			});
			mainPanel.add(channelModel);
			channelModel.setBounds(itemPos, 65, 120, 23);
			channelModel.addKeyListener(keyAdapter);
		}
		{
			label4 = new JLabel();
			mainPanel.add(label4);
			label4.setToolTipText("Duration of Simulation in terms of minutes");
			label4.setText("Simulation Dur. (min)");
			label4.setBounds(labelPos, 100, 240, 16);
		}
		{
			simDur = new JTextField();
			mainPanel.add(simDur);
			simDur.setToolTipText("Duration of Simulation in terms of minutes");
			simDur.setBounds(itemPos, 100, 120, 23);
			simDur.setText("600");
			simDur.addKeyListener(keyAdapter);
		}
		{
			label5 = new JLabel();
			mainPanel.add(label5);
			label5.setToolTipText("Maximum SNR Value of Transmitter");
			label5.setText("Max SNR Value (dB)");
			label5.setBounds(labelPos, 135, 165, 16);
		}
		{
			maxSNR = new JTextField();
			mainPanel.add(maxSNR);
			maxSNR.setToolTipText("Maximum SNR Value of Transmitter");
			maxSNR.setBounds(itemPos, 135, 120, 23);
			maxSNR.setText("10");
			maxSNR.addKeyListener(keyAdapter);
		}
		{
			label6 = new JLabel();
			mainPanel.add(label6);
			label6.setToolTipText("SINR Threshold for CR Nodes To Communicate w/o Collision");
			label6.setText("SINR Threshold (dB)");
			label6.setBounds(labelPos, 170, 165, 16);
		}
		{
			sinrThresholdFied = new JTextField();
			mainPanel.add(sinrThresholdFied);
			sinrThresholdFied.setToolTipText("SINR Threshold for CR Nodes To Communicate w/o Collision");
			sinrThresholdFied.setBounds(itemPos, 170, 120, 23);
			sinrThresholdFied.setText("1");
			sinrThresholdFied.addKeyListener(keyAdapter);
		}
		{
			label7 = new JLabel();
			mainPanel.add(label7);
			label7.setText("Seed Model");
			label7.setBounds(labelPos, 205, 165, 16);
		}
		{
			ComboBoxModel seedModelModel = 
				new DefaultComboBoxModel(
							new String[] { "Random Seed", "Constant Seed"});
			seedModel = new JComboBox();
			seedModel.setModel(seedModelModel);
			seedModel.setSelectedIndex(1);
			seedModel.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if(seedModel.getSelectedIndex() == 0){
						label8.setVisible(false);
						seedValue.setVisible(false);
					}
					else{
						label8.setVisible(true);
						seedValue.setVisible(true);
					}
				}
			});
			mainPanel.add(seedModel);
			seedModel.setBounds(itemPos, 205, 120, 23);
			seedModel.addKeyListener(keyAdapter);
		}
		{
			label8 = new JLabel();
			mainPanel.add(label8);
			label8.setToolTipText("Seed Value of Random Number Generator");
			label8.setText("Seed Value");
			label8.setBounds(labelPos, 240, 165, 16);
		}
		{
			seedValue = new JTextField();
			mainPanel.add(seedValue);
			seedValue.setToolTipText("Seed Value of Random Number Generator");
			seedValue.setBounds(itemPos, 240, 120, 23);
			seedValue.setText("111211211");
			seedValue.addKeyListener(keyAdapter);
		}
		{
			label9 = new JLabel();
			mainPanel.add(label9);
			label9.setToolTipText("Run Animation During Simulation");
			label9.setText("Animation");
			label9.setBounds(labelPos, 275, 165, 16);
			animationOnOff = new ButtonGroup();
		}
		{
			animationOnButton = new JRadioButton("On");
			mainPanel.add(animationOnButton);
			animationOnButton.setBounds(itemPos, 275, 50, 23);
			animationOnButton.setSelected(false);
			animationOnOff.add(animationOnButton);
			animationOnButton.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if(animationOnButton.isSelected()){
						unitTime.setEnabled(true);
					}
				}
			});
			animationOnButton.addKeyListener(keyAdapter);
		}
		{
			animationOffButton = new JRadioButton("Off");
			mainPanel.add(animationOffButton);
			animationOffButton.setBounds(itemPos+70, 275, 50, 23);
			animationOffButton.setSelected(true);
			animationOnOff.add(animationOffButton);
			animationOffButton.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if(animationOffButton.isSelected()){
						unitTime.setEnabled(false);
					}
				}
			});
			animationOffButton.addKeyListener(keyAdapter);
		}
		{
			label10 = new JLabel();
			mainPanel.add(label10);
			label10.setToolTipText("Draw Plots at The End of The Simulation");
			label10.setText("Plot");
			label10.setBounds(labelPos, 310, 165, 16);
			plotOnOff = new ButtonGroup();
		}
		{
			plotOnButton = new JRadioButton("On");
			mainPanel.add(plotOnButton);
			plotOnButton.setBounds(itemPos, 310, 50, 23);
			plotOnButton.setSelected(false);
			plotOnOff.add(plotOnButton);
			plotOnButton.addKeyListener(keyAdapter);
		}
		{
			plotOffButton = new JRadioButton("Off");
			mainPanel.add(plotOffButton);
			plotOffButton.setBounds(itemPos+70, 310, 50, 23);
			plotOffButton.setSelected(true);
			plotOnOff.add(plotOffButton);
			plotOffButton.addKeyListener(keyAdapter);
		}
		tabMainPanel.add(mainPanel);
		mainPanel.setBounds(panelLeft, 10, panelWidth, 345);
	}
	
	private void initFrameOptionsGUI()
	{
		framePanel = new JPanel();
		framePanel.setBorder(BorderFactory.createTitledBorder("Frame Options"));
		framePanel.setLayout(null);
		{
			label11 = new JLabel();
			framePanel.add(label11);
			label11.setToolTipText("Number Of Sensing Slot in CR Frame");
			label11.setText("Number of Sensing Slots");
			label11.setBounds(labelPos, 30, 165, 16);
		}
		{
			noSlotField = new JTextField();
			framePanel.add(noSlotField);
			noSlotField.setToolTipText("Number Of Sensing Slot in CR Frame");
			noSlotField.setBounds(itemPos, 30, 120, 23);
			noSlotField.setText("4");
			noSlotField.addKeyListener(keyAdapter);
		}
		{
			label12 = new JLabel();
			framePanel.add(label12);
			label12.setToolTipText("Duration of Sensing Slot in terms of msec");
			label12.setText("Sensing Slot Duration (msec)");
			label12.setBounds(labelPos, 65, 245, 16);
		}
		{
			slotDurField = new JTextField();
			framePanel.add(slotDurField);
			slotDurField.setToolTipText("Duration of Sensing Slot in terms of msec");
			slotDurField.setBounds(itemPos, 65, 120, 23);
			slotDurField.setText("10");
			slotDurField.addKeyListener(keyAdapter);
		}
		{
			label13 = new JLabel();
			framePanel.add(label13);
			label13.setToolTipText("Duration of Sensing Result Advertisement in terms of msec");
			label13.setText("Dur. of sensing result ack. (msec)");
			label13.setBounds(labelPos, 100, 245, 16);
		}
		{
			sensingResultField = new JTextField();
			framePanel.add(sensingResultField);
			sensingResultField.setToolTipText("Duration of Sensing Result Advertisement in terms of msec");
			sensingResultField.setBounds(itemPos, 100, 120, 23);
			sensingResultField.setText("10");
			sensingResultField.addKeyListener(keyAdapter);
		}
		{
			label14 = new JLabel();
			framePanel.add(label14);
			label14.setToolTipText("Duration of Sensing Schedule Advertisement in terms of msec");
			label14.setText("Sensing Schedule Advert. Dur. (msec)");
			label14.setBounds(labelPos, 135, 245, 16);
		}
		{
			senseScheduleField = new JTextField();
			framePanel.add(senseScheduleField);
			senseScheduleField.setToolTipText("Duration of Sensing Schedule Advertisement in terms of msec");
			senseScheduleField.setBounds(itemPos, 135, 120, 23);
			senseScheduleField.setText("10");
			senseScheduleField.addKeyListener(keyAdapter);
		}
		{
			label15 = new JLabel();
			framePanel.add(label15);
			label15.setToolTipText("Duration of Communication in terms of msec");
			label15.setText("Comm. Duration (msec)");
			label15.setBounds(labelPos, 170, 245, 16);
		}
		{
			commDurField = new JTextField();
			framePanel.add(commDurField);
			commDurField.setToolTipText("Duration of Communication in terms of msec");
			commDurField.setBounds(itemPos, 170, 120, 23);
			commDurField.setText("630");
			commDurField.addKeyListener(keyAdapter);
		}
		{
			label16 = new JLabel();
			framePanel.add(label16);
			label16.setToolTipText("Duration of Communication Schedule Advertisement in terms of msec");
			label16.setText("Comm. Schedule Advert. Dur. (msec)");
			label16.setBounds(labelPos, 205, 245, 16);
		}
		{
			commScheduleField = new JTextField();
			framePanel.add(commScheduleField);
			commScheduleField.setToolTipText("Duration of Communication Schedule Advertisement in terms of msec");
			commScheduleField.setBounds(itemPos, 205, 120, 23);
			commScheduleField.setText("10");
			commScheduleField.addKeyListener(keyAdapter);
		}
		tabMainPanel.add(framePanel);
		framePanel.setBounds(panelRight,185,panelWidth,240);
	}
	
	private void initZoneOptionsGUI()
	{
		zonePanel = new JPanel();
		zonePanel.setBorder(BorderFactory.createTitledBorder("Zone Options"));
		zonePanel.setLayout(null);
		{
			label17 = new JLabel();
			zonePanel.add(label17);
			label17.setToolTipText("Sectors Of Cell");
			label17.setText("Sector Number");
			label17.setBounds(labelPos, 30, 165, 16);
		}
		{
			sectorNo = new JTextField();
			zonePanel.add(sectorNo);
			sectorNo.setToolTipText("Number of Sectors in A Cell");
			sectorNo.setBounds(itemPos, 30, 120, 23);
			sectorNo.setText("3");
			sectorNo.addKeyListener(keyAdapter);
		}
		{
			label19 = new JLabel();
			zonePanel.add(label19);
			label19.setToolTipText("D Sections of Radius");
			label19.setText("D Number");
			label19.setBounds(labelPos, 65, 165, 16);
		}
		{
			dNo = new JTextField();
			zonePanel.add(dNo);
			dNo.setToolTipText("Number of D Sections Radius is Divided");
			dNo.setBounds(itemPos, 65, 120, 23);
			dNo.setText("3");
			dNo.addKeyListener(keyAdapter);
		}
		{
			label21 = new JLabel();
			zonePanel.add(label21);
			label21.setToolTipText("Alpha Sections in A Sector");
			label21.setText("Alpha Number");
			label21.setBounds(labelPos, 100, 165, 16);
		}
		{
			alphaNo = new JTextField();
			zonePanel.add(alphaNo);
			alphaNo.setToolTipText("Number of Alpha Sections in A Sector");
			alphaNo.setBounds(itemPos, 100, 120, 23);
			alphaNo.setText("4");
			alphaNo.addKeyListener(keyAdapter);
		}
		{
			label23 = new JLabel();
			zonePanel.add(label23);
			label23.setToolTipText("Radius of CR Cell in terms of 100s of meters");
			label23.setText("Radius of Cell (100 m)");
			label23.setBounds(labelPos, 135, 165, 16);
		}
		{
			radiusField = new JTextField();
			zonePanel.add(radiusField);
			radiusField.setToolTipText("Radius of CR Cell in terms of 100s of meters");
			radiusField.setBounds(itemPos, 135, 120, 23);
			radiusField.setText("30");
			radiusField.addKeyListener(keyAdapter);
		}
		{
			label22 = new JLabel();
			zonePanel.add(label22);
			label22.setToolTipText("Number of Zones to be simulated");
			label22.setText("Number Of Zones");
			label22.setBounds(labelPos, 170, 165, 16);
		}
		{
			noZones = new JTextField();
			zonePanel.add(noZones);
			noZones.setToolTipText("Number of Zones to be simulated");
			noZones.setBounds(itemPos, 170, 120, 23);
			noZones.setText("4");
			noZones.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					noZones.selectAll();
				}
				
			});
			
			noZones.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void insertUpdate(DocumentEvent e) {
					if(noZones.getText().isEmpty())
						return;
					int i=0;
					try{
						i = Integer.parseInt(noZones.getText());
					}
					catch(NumberFormatException nfe){
						JOptionPane.showMessageDialog(null, "Invalid argument:\n"+nfe.getMessage(),
							"Simulation", JOptionPane.WARNING_MESSAGE);
						return;
					}
					if(i>34){
						JOptionPane.showMessageDialog(null, "Invalid argument:\n"+"Number of Zones cannot be greater than 34",
							"Simulation", JOptionPane.WARNING_MESSAGE);
						return;
					}
					for(int j=0;j<i;j++){
						zoneAlphaNos.get(j).setEnabled(true);
						zoneCRUsers.get(j).setEnabled(true);
						zoneDNos.get(j).setEnabled(true);
						zoneSectorNos.get(j).setEnabled(true);
					}
					
					for(int j=i;j<zoneAlphaNos.size();j++){
						zoneAlphaNos.get(j).setEnabled(false);
						zoneCRUsers.get(j).setEnabled(false);
						zoneDNos.get(j).setEnabled(false);
						zoneSectorNos.get(j).setEnabled(false);
					}
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					if(noZones.getText().isEmpty())
						return;
					int i=0;
					try{
						i = Integer.parseInt(noZones.getText());
					}
					catch(NumberFormatException nfe){
						JOptionPane.showMessageDialog(null, "Invalid argument:\n"+nfe.getMessage(),
							"Simulation", JOptionPane.WARNING_MESSAGE);
						return;
					}
					if(i>34){
						JOptionPane.showMessageDialog(null, "Invalid argument:\n"+"Number of Zones cannot be greater than 34",
							"Simulation", JOptionPane.WARNING_MESSAGE);
						return;
					}
					for(int j=0;j<i;j++){
						zoneAlphaNos.get(i).setEnabled(true);
						zoneCRUsers.get(i).setEnabled(true);
						zoneDNos.get(i).setEnabled(true);
						zoneSectorNos.get(i).setEnabled(true);
					}
					
					for(int j=i;j<zoneAlphaNos.size();j++){
						zoneAlphaNos.get(i).setEnabled(false);
						zoneCRUsers.get(i).setEnabled(false);
						zoneDNos.get(i).setEnabled(false);
						zoneSectorNos.get(i).setEnabled(false);
					}
				}

				@Override
				public void changedUpdate(DocumentEvent e) {}
			});
			noZones.addKeyListener(keyAdapter);
		}
		tabMainPanel.add(zonePanel);
		zonePanel.setBounds(panelLeft, 360, panelWidth, 205);
	}
	
	private void initTrafficOptionsGUI()
	{
		trafficPanel = new JPanel();
		trafficPanel.setBorder(BorderFactory.createTitledBorder("Traffic Options"));
		trafficPanel.setLayout(null);
		{
			label24 = new JLabel();
			trafficPanel.add(label24);
			label24.setToolTipText("Traffic Generation Model of Primary Users");
			label24.setText("Traffic Model");
			label24.setBounds(labelPos, 30, 165, 16);
		}
		{
			ComboBoxModel trafficModelModel = 
				new DefaultComboBoxModel(
							new String[] { "Poisson Traffic", "Pareto On-Off Traffic"});
			trafficModel = new JComboBox();
			trafficModel.setModel(trafficModelModel);
			trafficModel.setSelectedIndex(0);
			trafficModel.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean isPoisson = trafficModel.getSelectedIndex() == 0;
					label26.setVisible(isPoisson);
					label28.setVisible(isPoisson);
					label25.setVisible(!isPoisson);
					label27.setVisible(!isPoisson);
					if(isPoisson)
						noCalls.setText("2");
					else
						noCalls.setText("0.5");
					callDur.setText("4");
				}
			});
			trafficPanel.add(trafficModel);
			trafficModel.setToolTipText("Traffic Generation Model of Primary Users");
			trafficModel.setBounds(itemPos, 30, 120, 23);
			trafficModel.addKeyListener(keyAdapter);
		}
		{
			label25 = new JLabel();
			trafficPanel.add(label25);
			label25.setToolTipText("Mean of OFF Period duration of a Source");
			label25.setText("OFF Duration (hour)");
			label25.setBounds(labelPos, 65, 175, 16);
			label25.setVisible(false);
			
			label26 = new JLabel();
			trafficPanel.add(label26);
			label26.setToolTipText("Mean of Number of Calls of a Source per hour");
			label26.setText("Number of Calls/hour");
			label26.setBounds(labelPos, 65, 165, 16);
		}
		{
			noCalls = new JTextField();
			trafficPanel.add(noCalls);
			noCalls.setBounds(itemPos, 65, 120, 23);
			noCalls.setText("2");
			noCalls.addKeyListener(keyAdapter);
		}
		{
			label27 = new JLabel();
			trafficPanel.add(label27);
			label27.setToolTipText("Mean of ON Period duration of a Source");
			label27.setText("ON Duration (min)");
			label27.setBounds(labelPos, 100, 165, 16);
			label27.setVisible(false);
			
			label28 = new JLabel();
			trafficPanel.add(label28);
			label28.setToolTipText("Mean of Call duration of a Source");
			label28.setText("Call Duration (min)");
			label28.setBounds(labelPos, 100, 165, 16);
		}
		{
			callDur = new JTextField();
			trafficPanel.add(callDur);
			callDur.setBounds(itemPos, 100, 120, 23);
			callDur.setText("4");
			callDur.addKeyListener(keyAdapter);
		}
		{
			label29 = new JLabel();
			trafficPanel.add(label29);
			label29.setToolTipText("Duration of a unit time during animation");
			label29.setText("Unit Time (msec)");
			label29.setBounds(labelPos, 135, 165, 16);
		}
		{
			unitTime = new JTextField();
			trafficPanel.add(unitTime);
			unitTime.setToolTipText("Duration of a unit time during animation");
			unitTime.setBounds(itemPos, 135, 120, 23);
			unitTime.setText("0.1");
			unitTime.addKeyListener(keyAdapter);
			unitTime.setEnabled(false);
		}
		tabMainPanel.add(trafficPanel);
		trafficPanel.setBounds(panelRight, 10, panelWidth, 170);
	}
	
	private void initFrequencyOptionsGUI()
	{
		frequencyPanel = new JPanel();
		frequencyPanel.setBorder(BorderFactory.createTitledBorder("Frequency Options"));
		frequencyPanel.setLayout(null);
		{
			label30 = new JLabel();
			frequencyPanel.add(label30);
			label30.setToolTipText("Available Number of Frequencies in the Channel");
			label30.setText("Number of Frequencies");
			label30.setBounds(labelPos, 30, 165, 16);
		}
		{
			noFreqs = new JTextField();
			frequencyPanel.add(noFreqs);
			noFreqs.setToolTipText("Available Number of Frequencies in the Channel");
			noFreqs.setBounds(itemPos, 30, 120, 23);
			noFreqs.setText("12");
			noFreqs.addKeyListener(keyAdapter);
		}
		tabMainPanel.add(frequencyPanel);
		frequencyPanel.setBounds(panelRight, 430, panelWidth, 65);
	}
	
	private void addZoneOptinsGUI()
	{
		zoneSectorNos = new ArrayList<JTextField>();
		zoneDNos = new ArrayList<JTextField>();
		zoneAlphaNos = new ArrayList<JTextField>();
		zoneCRUsers = new ArrayList<JTextField>();
		int id=1;
		int numberOfRows = 17;
		
		JTextField textField;
		JPanel []panels = new JPanel[2];
		for(int j=0; j<2; j++){
			panels[j] = new JPanel();
			panels[j].setBorder(BorderFactory.createTitledBorder(""));
			panels[j].setLayout(null);

			JLabel label = new JLabel();
			panels[j].add(label);
			label.setText("ID");
			label.setBounds(5, 5, 25, 23);

			label = new JLabel();
			panels[j].add(label);
			label.setText("Sector No");
			label.setBounds(66, 5, 60, 23);

			label = new JLabel();
			panels[j].add(label);
			label.setText("D No");
			label.setBounds(162, 5, 30, 23);

			label = new JLabel();
			panels[j].add(label);
			label.setText("Alpha No");
			label.setBounds(228, 5, 60, 23);

			label = new JLabel();
			panels[j].add(label);
			label.setText("CR Users");
			label.setBounds(324, 5, 60, 23);

			for(int i=0;i<numberOfRows;i++){
				label = new JLabel();
				panels[j].add(label);
				label.setText(String.valueOf(id));
				label.setBounds(5, 35+i*30, 46, 23);
				id++;
				
				textField = new JTextField();
				panels[j].add(textField);
				textField.setToolTipText("In Which Sector The CR Nodes Located During Simulation");
				textField.setBounds(66, 35+i*30, 81, 23);
				textField.addKeyListener(keyAdapter);
				if(id > 2)
					textField.setEnabled(false);
				textField.setText(String.valueOf(((id-2)/4)%3));
				zoneSectorNos.add(textField);

				textField = new JTextField();
				panels[j].add(textField);
				textField.setToolTipText("In Which D Section The CR Nodes Located During Simulation");
				textField.setBounds(162, 35+i*30, 51, 23);
				textField.addKeyListener(keyAdapter);
				if(id > 2)
					textField.setEnabled(false);
				textField.setText(String.valueOf(((id-2)/12)%3));
				zoneDNos.add(textField);

				textField = new JTextField();
				panels[j].add(textField);
				textField.setToolTipText("In Which Alpha Section The CR Nodes Located During Simulation");
				textField.setBounds(228, 35+i*30, 81, 23);
				textField.addKeyListener(keyAdapter);
				if(id > 2)
					textField.setEnabled(false);
				textField.setText(String.valueOf((id-2)%4));
				zoneAlphaNos.add(textField);

				textField = new JTextField();
				panels[j].add(textField);
				textField.setToolTipText("Number of Secondary Users in This Zone");
				textField.setBounds(324, 35+i*30, 81, 23);
				textField.addKeyListener(keyAdapter);
				if(id > 2)
					textField.setEnabled(false);
				textField.setText("4");
				zoneCRUsers.add(textField);
			}
			tabZonePanel.add(panels[j]);
		}
		panels[0].setBounds(5,5,410,548);
		panels[1].setBounds(425,5,410,548);
	}
	
	private void createButtons()
	{
		{
			closeButton = new JButton();
			panel.add(closeButton);
			closeButton.setText("CLOSE");
			closeButton.setMnemonic('c');
			closeButton.setBounds(panelRight+panelWidth-120, 603, 120, 23);
			closeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					exit();
				}
			});
			closeButton.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					exit();
				}

			});
		}
		{
			startSimulation = new JButton();
			panel.add(startSimulation);
			startSimulation.setText("Start");
			startSimulation.setMnemonic('s');
			startSimulation.setBounds(panelRight+panelWidth-120-135, 603, 120, 23);
			startSimulation.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(crSensor!=null){
						if(!crSensor.isFinished())
							return;
					}
					if(crDesScheduler != null)
						if(!crDesScheduler.isFinished())
							return;
					startSimulation();
				}
			});
			startSimulation.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if(crSensor!=null){
						if(!crSensor.isFinished())
							return;
					}
					if(crDesScheduler != null)
						if(!crDesScheduler.isFinished())
							return;
					startSimulation();
				}

			});
			startSimulation.addKeyListener(keyAdapter);
		}
		{
			terminateSimulation = new JButton();
			panel.add(terminateSimulation);
			terminateSimulation.setText("Terminate");
			terminateSimulation.setMnemonic('t');
			terminateSimulation.setBounds(panelRight+panelWidth-120-270, 603, 120, 23);
			terminateSimulation.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(crSensor!=null){
						if(!crSensor.isFinished()){
							priTrafGen.terminateAllThreads();
							crSensor.terminate();
						}
					}
					if(crDesScheduler != null){
						if(!crDesScheduler.isFinished())
							crDesScheduler.terminate();
					}
				}
			});
			terminateSimulation.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if(crSensor!=null){
						if(!crSensor.isFinished()){
							priTrafGen.terminateAllThreads();
							crSensor.terminate();
						}
					}
					if(crDesScheduler != null){
						if(!crDesScheduler.isFinished())
							crDesScheduler.terminate();
					}
				}

			});
			terminateSimulation.setVisible(false);

		}
		{
			progressBar = new JProgressBar();
			progressBar.setStringPainted(true);
			panel.add(progressBar);
			progressBar.setBounds(15, 603, 200, 23);
			progressBar.setVisible(false);
		}
	}
	
	/**
	 * Initializes the main simulation threads or schedulers
	 */
	public void startSimulation()
	{
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
		int numberOfZones = 0;
		
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
			
			sectrNo = Integer.parseInt(sectorNo.getText());			//Get number of sectors in the cell
			dNumber = Integer.parseInt(dNo.getText());				//Get number of d's
			alpha = Integer.parseInt(alphaNo.getText());			//Get number of alpha's
			radius = Double.parseDouble(radiusField.getText());		//Get radius of the cell
			
			numberOfFreq = Integer.parseInt(noFreqs.getText());						//Get number of frequencies
			maxSnr = Double.parseDouble(maxSNR.getText());							//Get max SNR value
			sinrThreshold = Double.parseDouble(sinrThresholdFied.getText());
			
			numberOfPriNodes = Integer.parseInt(noPriNodes.getText());	//Get number of primary nodes
			maxFreqCR = Integer.parseInt(noSlotField.getText());		//Get max number of frequencies a node can sense
			numberOfZones = Integer.parseInt(noZones.getText());		//Get the number of zones to be simulated
			
			numberOfCalls = Double.parseDouble(noCalls.getText());	//Get number of calls per hour
			callDura = Double.parseDouble(callDur.getText());	//Get call duration in terms of min
			
			if(numberOfCalls<=2 && trafficModel.getSelectedIndex() == 1){
				JOptionPane.showMessageDialog(this, "Mean Off Period Duration must be greater than 2 time units",
					"Simulation", JOptionPane.WARNING_MESSAGE);
				return;
			}
			if(callDura<=2 && trafficModel.getSelectedIndex() == 1){
				JOptionPane.showMessageDialog(this, "Mean On Period Duration must be greater than 2 time units",
					"Simulation", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			if(animationOnButton.isSelected())					//Get unit time duration in terms of milliseconds
				timeUnit = Double.parseDouble(unitTime.getText());
			else
				timeUnit = 1;
			simDura = Long.parseLong(simDur.getText());			//Get duration of the simulation in terms of min
			simDura *= 60000;
			
			if(seedModel.getSelectedIndex()==0){				//If seed model is random
				randEngine = new MersenneTwister(new Date());	//Give date as seed
			}
			else{
				int seed = Integer.parseInt(seedValue.getText());	//Otherwise get seed from user
				randEngine = new MersenneTwister(seed);
			}
			
			wc = new WirelessChannel(channelModel.getSelectedIndex(), numberOfFreq, maxSnr, sinrThreshold, numberOfCalls,
													callDura, trafficModel.getSelectedIndex(),timeUnit);//Create a wireless channel
			
			alpha = (360/sectrNo)/alpha;							//Evaluate the angle associated to alpha
			double temp = radius / dNumber;							//Evaluate length of each d as they will be equal
			double inc = temp;
			for(int i = 0;i<dNumber;i++,temp+=inc)
				setOfD.add(temp);									//Create set of d's
			
			cell = new Cell(null, radius, sectrNo, alpha, setOfD);//Create a cell
			
			crBase = new CRBase(new Point2D.Double(0, 0),0,maxFreqCR);		//Create a CR base station in the origin
			Cell.setBaseStation(crBase);
			numberOfCrNodes = 0;
			for(int i = 0; i<numberOfZones ; i++){
				int sectorNumber = Integer.parseInt(zoneSectorNos.get(i).getText());		//Get sector number CR nodes will be in
				int alphaNumber = Integer.parseInt(zoneAlphaNos.get(i).getText());			//Get alpha number CR nodes will be in
				int dNmber = Integer.parseInt(zoneDNos.get(i).getText());					//Get d interval CR nodes will be in
				int numberOfCrUsersInZone = Integer.parseInt(zoneCRUsers.get(i).getText());	//Get number of CR nodes in zone
				numberOfCrNodes += numberOfCrUsersInZone;
				crBase.registerZone(sectorNumber,alphaNumber,dNmber,numberOfCrUsersInZone);
			}
			
			double dmax = crBase.farthestZoneDistance();
			double minSNR = maxSnr/Math.exp(dmax*0.12);
			if(minSNR<=sinrThreshold){
				JOptionPane.showMessageDialog(this, "SINR threshold must be less than possible\nminimum SNR value: "+String.valueOf(minSNR)+"dB",
					"Simulation", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			if(animationOnButton.isSelected()){
				drawCell = new DrawCell((int)radius, sectrNo, Integer.parseInt(alphaNo.getText()),
																(int)dNumber, numberOfCrNodes, numberOfPriNodes);
				priTrafGen = new PrimaryTrafficGenerator();
				priTrafGenDes = null;
			}
			else{
				drawCell = null;
				priTrafGen = null;
				priTrafGenDes = new DESPrimaryTrafficGenerator();
			}
			
			for(int i = 0; i<numberOfCrNodes ;i++){
				crNodes.add(new CRNode(i,crBase.deployNodeinZone(i), 0));
				wc.registerNode(crNodes.get(i));							//Register CR nodes
				if(animationOnButton.isSelected())
					DrawCell.paintCrNode(crNodes.get(i), Color.GREEN);
			}
			
			if(SimulationRunner.plotOnButton.isSelected()){
				ArrayList<Integer> numberOFYs = new ArrayList<Integer>();
				numberOFYs.add(numberOfFreq);
				numberOFYs.add(numberOfFreq);
				plot = new Plot(numberOfZones+1, numberOFYs);
			}
			else
				plot = null;
			CRNode.initializeAverageSnr(numberOfFreq,numberOfZones);			//Set average SNR values to zero
			progressBar.setValue(0);							//Initialize progress bar
			progressBar.setVisible(true);						//Make it visible
			CRNode.createLogFile("log.txt");
			terminateSimulation.setVisible(true);
			for(int i = 0;i<numberOfPriNodes;i++){
				priTrafGenNodes.add(new PrimaryTrafficGeneratorNode(Cell.deployNodeinCell(), 0,i));	//Create primary traffic
				wc.registerNode(priTrafGenNodes.get(i));					//generator nodes and register them to the channel
				if(animationOnButton.isSelected())
					priTrafGen.registerNode(priTrafGenNodes.get(i));	//and create threads for each of them
				else
					priTrafGenDes.registerNode(priTrafGenNodes.get(i));
			}
			if(animationOnButton.isSelected()){
				crSensor = new CRSensorThread((int)simDura, timeUnit, maxFreqCR, slotDur, senseScheduleAdvertisement,
													commScheduleAdvertisement, commDur, senseResultAdvertisement);
				crDesScheduler = null;
			}
			else{
				crSensor = null;
				crDesScheduler = new CRDESScheduler((int)simDura, timeUnit, maxFreqCR, slotDur, senseScheduleAdvertisement,
													commScheduleAdvertisement, commDur, senseResultAdvertisement);
			}
			
			if(animationOffButton.isSelected()){
				crDesScheduler.start();
				priTrafGenDes.start();
				Thread t = new Thread(Scheduler.instance());
				t.start();
			}
			
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
		if(SimulationRunner.animationOnButton.isSelected())
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
		
		WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
}
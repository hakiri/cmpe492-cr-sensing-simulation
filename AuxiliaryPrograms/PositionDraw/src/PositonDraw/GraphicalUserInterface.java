package PositonDraw;

import Animation.DrawCell;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This is the main class of the software. It instantiates necessary classes and
 * create GUI for taking input from the user.
 */
public class GraphicalUserInterface extends JFrame{

	/**
	 * Progress bar to show progress of the simulation
	 */
	public static JProgressBar progressBar;
	private JPanel panel, tabDrawSolutionPanel, tabGenerateModelPanel, drawSolutionPanel, generateModelPanel;
	private JTabbedPane tabPane;
	private JTextField numberOfNodesTextField,numberOfClustersTextField,clusterCapacityTextField,nodesPositionOutputTextField,
					   nodesPositionInputTextField,gamsModelOutputTextField,gamsSolutionTextField, radiusTextField;
	private JLabel label1,label2,label3,label4,label5,label6,label7,label8;
	private JButton startGenerateModelSimulation, closeButton, gamsModelOutputBrowseButton, nodesPositionOutputBrowseButton, nodesPositionInputBrowseButton,
					gamsSolutionBrowseButton, startDrawSolution;
	
	private final static int labelPos = 12;
	private final static int itemPos = 175;
	private final static int panelPos = 10;
	private final static int panelWidth = 615;
	
	/**
	 * Constructor of the class. This method initializes the GUI.
	 */
	public GraphicalUserInterface() {
		super();
		initGUI();
	}

	/**
	 * Initializes the simulation runner object for batch mode.
	 * @param batchMode true if the simulation runs in the batch mode.
	 */
	public GraphicalUserInterface(boolean batchMode) {
	}
	
	private class SimulationKeyAdapter extends KeyAdapter{

		@Override
		public void keyTyped(KeyEvent e) {
			super.keyTyped(e);
			if(e.getKeyChar()=='\n')
				startGMDS();
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
				
				tabDrawSolutionPanel = new JPanel();
				tabDrawSolutionPanel.setLayout(null);
				tabGenerateModelPanel = new JPanel();
				tabGenerateModelPanel.setLayout(null);
				
				tabPane = new JTabbedPane();
				panel.add(tabPane);
				tabPane.setBounds(0, 0, 630, 220);
				
				
				initDrawSolutionOptionsGUI();
				initGenerateModelOptionsGUI();
				createButtons();
				
				tabPane.add("Draw Solution", tabDrawSolutionPanel);
				tabPane.add("Generate Model", tabGenerateModelPanel);
				
				createButtons();
			}
			pack();
			this.setSize(640, 290);
			this.setResizable(false);
			this.setTitle("GODDESS");
		} catch (Exception e) {
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
				Logger.getLogger(GraphicalUserInterface.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	private void initDrawSolutionOptionsGUI()
	{
		drawSolutionPanel = new JPanel();
		drawSolutionPanel.setBorder(BorderFactory.createTitledBorder(""));
		drawSolutionPanel.setLayout(null);
		int y = 5;
		{
			label1 = new JLabel();
			drawSolutionPanel.add(label1);
			label1.setText("Positions of Nodes");
			label1.setBounds(labelPos, y, 165, 16);
		}
		{
			nodesPositionInputTextField = new JTextField();
			drawSolutionPanel.add(nodesPositionInputTextField);
			nodesPositionInputTextField.setBounds(itemPos, y, 300, 25);
			nodesPositionInputTextField.setText("nodes.pos");
			nodesPositionInputTextField.setEditable(false);
			nodesPositionInputTextField.addKeyListener(keyAdapter);
			nodesPositionInputTextField.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					browseNodePositionsInputFile();
				}
				
			});
			
		}
		{
			nodesPositionInputBrowseButton = new JButton();
			drawSolutionPanel.add(nodesPositionInputBrowseButton);
			nodesPositionInputBrowseButton.setText("Browse");
			nodesPositionInputBrowseButton.setBounds(itemPos+310, y, 120, 23);
			nodesPositionInputBrowseButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					browseNodePositionsInputFile();
				}
			});
			nodesPositionInputBrowseButton.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					browseNodePositionsInputFile();
				}

			});
		}
		y += 35;
		{
			label3 = new JLabel();
			drawSolutionPanel.add(label3);
			label3.setText("GAMS Solution File");
			label3.setBounds(labelPos, y, 165, 16);
		}
		{
			gamsSolutionTextField = new JTextField();
			drawSolutionPanel.add(gamsSolutionTextField);
			gamsSolutionTextField.setBounds(itemPos, y, 300, 25);
			gamsSolutionTextField.setText("Uncapacitated_Cell.lst");
			gamsSolutionTextField.setEditable(false);
			gamsSolutionTextField.addKeyListener(keyAdapter);
			
			gamsSolutionTextField.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					browseGamsSolutionFile();
				}
				
			});
		}
		{
			gamsSolutionBrowseButton = new JButton();
			drawSolutionPanel.add(gamsSolutionBrowseButton);
			gamsSolutionBrowseButton.setText("Browse");
			gamsSolutionBrowseButton.setBounds(itemPos+310, y, 120, 23);
			gamsSolutionBrowseButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					browseGamsSolutionFile();
				}
			});
			gamsSolutionBrowseButton.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					browseGamsSolutionFile();
				}

			});
		}
		y += 35;
		{
			startDrawSolution = new JButton();
			drawSolutionPanel.add(startDrawSolution);
			startDrawSolution.setText("Start");
			startDrawSolution.setBounds(itemPos+310, y, 120, 23);
			startDrawSolution.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					startGMDS();
				}

			});
		}
		y+=35;
		tabDrawSolutionPanel.add(drawSolutionPanel);
		drawSolutionPanel.setBounds(panelPos, 10, panelWidth, y);
	}
	
	private void initGenerateModelOptionsGUI()
	{
		generateModelPanel = new JPanel();
		generateModelPanel.setBorder(BorderFactory.createTitledBorder(""));
		generateModelPanel.setLayout(null);
		int y = 5;
		{
			label7 = new JLabel();
			generateModelPanel.add(label7);
			label7.setText("Positions Output File");
			label7.setBounds(labelPos, y, 165, 16);
		}
		{
			nodesPositionOutputTextField = new JTextField();
			generateModelPanel.add(nodesPositionOutputTextField);
			nodesPositionOutputTextField.setBounds(itemPos, y, 300, 25);
			nodesPositionOutputTextField.setText("nodes.pos");
			nodesPositionOutputTextField.setEditable(false);
			nodesPositionOutputTextField.addKeyListener(keyAdapter);
			nodesPositionOutputTextField.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					browseNodePositionsOutputFile();
				}

			});
		}
		{
			nodesPositionOutputBrowseButton = new JButton();
			generateModelPanel.add(nodesPositionOutputBrowseButton);
			nodesPositionOutputBrowseButton.setText("Browse");
			nodesPositionOutputBrowseButton.setBounds(itemPos+310, y, 120, 23);
			nodesPositionOutputBrowseButton.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					browseNodePositionsOutputFile();
				}

			});
		}
		y += 35;
		{
			label6 = new JLabel();
			generateModelPanel.add(label6);
			label6.setText("GAMS Model Output File");
			label6.setBounds(labelPos, y, 165, 16);
		}
		{
			gamsModelOutputTextField = new JTextField();
			generateModelPanel.add(gamsModelOutputTextField);
			gamsModelOutputTextField.setBounds(itemPos, y, 300, 25);
			gamsModelOutputTextField.setText("Cluster.gms");
			gamsModelOutputTextField.setEditable(false);
			gamsModelOutputTextField.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					browseGamsModelFile();
				}
				
			});
			
			gamsModelOutputTextField.addKeyListener(keyAdapter);
		}
		{
			gamsModelOutputBrowseButton = new JButton();
			generateModelPanel.add(gamsModelOutputBrowseButton);
			gamsModelOutputBrowseButton.setText("Browse");
			gamsModelOutputBrowseButton.setBounds(itemPos+310, y, 120, 23);
			gamsModelOutputBrowseButton.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					browseGamsModelFile();;
				}

			});
		}
		y += 35;
		{
			label2 = new JLabel();
			generateModelPanel.add(label2);
			label2.setText("Number of Nodes");
			label2.setBounds(labelPos, y, 165, 16);
		}
		{
			numberOfNodesTextField = new JTextField();
			generateModelPanel.add(numberOfNodesTextField);
			numberOfNodesTextField.setBounds(itemPos, y, 120, 25);
			numberOfNodesTextField.setText("1500");
			numberOfNodesTextField.addKeyListener(keyAdapter);
		}
		
		{
			label4 = new JLabel();
			generateModelPanel.add(label4);
			label4.setText("Number of Clusters");
			label4.setBounds(itemPos+145, y, 165, 16);
		}
		{
			numberOfClustersTextField = new JTextField();
			generateModelPanel.add(numberOfClustersTextField);
			numberOfClustersTextField.setBounds(2*itemPos-labelPos+145, y, 120, 25);
			numberOfClustersTextField.setText("30");
			numberOfClustersTextField.addKeyListener(keyAdapter);
		}
		y += 35;
		{
			label5 = new JLabel();
			generateModelPanel.add(label5);
			label5.setText("Cluster Capacity");
			label5.setBounds(labelPos, y, 165, 16);
		}
		{
			clusterCapacityTextField = new JTextField();
			generateModelPanel.add(clusterCapacityTextField);
			clusterCapacityTextField.setBounds(itemPos, y, 120, 25);
			clusterCapacityTextField.setText("65");
			clusterCapacityTextField.addKeyListener(keyAdapter);
		}
		
		{
			label8 = new JLabel();
			generateModelPanel.add(label8);
			label8.setText("Radius");
			label8.setBounds(itemPos+145, y, 165, 16);
		}
		{
			radiusTextField = new JTextField();
			generateModelPanel.add(radiusTextField);
			radiusTextField.setBounds(2*itemPos-labelPos+145, y, 120, 25);
			radiusTextField.setText("1500");
			radiusTextField.addKeyListener(keyAdapter);
		}
		y+=35;
		{
			startGenerateModelSimulation = new JButton();
			generateModelPanel.add(startGenerateModelSimulation);
			startGenerateModelSimulation.setText("Start");
			startGenerateModelSimulation.setBounds(itemPos+310, y, 120, 23);
			startGenerateModelSimulation.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					startGMDS();
				}

			});
		}
		y += 35;
		
		tabGenerateModelPanel.add(generateModelPanel);
		generateModelPanel.setBounds(panelPos, 10, panelWidth, y);
	}
	
	private void createButtons()
	{
		{
			closeButton = new JButton();
			panel.add(closeButton);
			closeButton.setText("CLOSE");
			closeButton.setMnemonic('c');
			closeButton.setBounds(panelPos+panelWidth-130, 223, 120, 23);
			closeButton.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					exit();
				}

			});
		}
	}
	
	private void exit()
	{
		WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
	
	public void startGMDS()
	{
		if(tabPane.getSelectedIndex() == 0){
			RandomPositionDrawMain.mainApp.parsePositions(nodesPositionInputTextField.getText());
			Parser parser = new Parser(gamsSolutionTextField.getText());
			String objVal;
			ArrayList<Integer> yj = new ArrayList<>();
			objVal = parser.parse(RandomPositionDrawMain.mainApp.nodes, RandomPositionDrawMain.mainApp.clusters, RandomPositionDrawMain.mainApp.xij, 
								  RandomPositionDrawMain.mainApp.numberOfClusters, RandomPositionDrawMain.mainApp.numberOfNodes,yj);
			parser.closeFile();
			
			ArrayList<Integer> clusterSizes = new ArrayList<>();
			for(int i=0;i<RandomPositionDrawMain.mainApp.numberOfClusters;i++){
				clusterSizes.add(RandomPositionDrawMain.mainApp.xij.get(i).size());
			}
			Collections.sort(clusterSizes);
			
			DrawCell cell = new DrawCell((int)(RandomPositionDrawMain.mainApp.radius), RandomPositionDrawMain.mainApp.numberOfNodes, 
										 RandomPositionDrawMain.mainApp.numberOfClusters, RandomPositionDrawMain.mainApp.xij,true);
			for(int i=0;i<RandomPositionDrawMain.mainApp.numberOfClusters;i++)
				DrawCell.paintClusterCenter(RandomPositionDrawMain.mainApp.clusters.get(i), i);
			for(int i=0;i<RandomPositionDrawMain.mainApp.numberOfNodes;i++){
				DrawCell.paintNode(RandomPositionDrawMain.mainApp.nodes.get(i), i);
			}
			
			JOptionPane.showMessageDialog(this, "Objective Value of The Solution: "+objVal,
					"Solution", JOptionPane.INFORMATION_MESSAGE);
		}
		else{
			RandomPositionDrawMain.mainApp.numberOfNodes = Integer.parseInt(numberOfNodesTextField.getText());
			RandomPositionDrawMain.mainApp.numberOfClusters = Integer.parseInt(numberOfClustersTextField.getText());
			RandomPositionDrawMain.mainApp.customerLimit = Integer.parseInt(clusterCapacityTextField.getText());
			RandomPositionDrawMain.mainApp.radius = Integer.parseInt(radiusTextField.getText());
			RandomPositionDrawMain.mainApp.randomlyPositionNodes(nodesPositionOutputTextField.getText());
			RandomPositionDrawMain.mainApp.outputGamsSourceFile(gamsModelOutputTextField.getText());
		}
	}
	
	private void browseGamsSolutionFile(){
		JFileChooser jfc = new JFileChooser();
		jfc.addChoosableFileFilter(createFileFilter("GAMS Solution Listing File", true, "lst"));
		if(jfc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION){
			return;
		}
		String fileName = jfc.getSelectedFile().getAbsolutePath();
		gamsSolutionTextField.setText(fileName);
	}
	
	private void browseNodePositionsInputFile(){
		JFileChooser jfc = new JFileChooser();
		jfc.addChoosableFileFilter(createFileFilter("2D Positions of The Nodes", true, "pos"));
		if(jfc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION){
			return;
		}
		String fileName = jfc.getSelectedFile().getAbsolutePath();
		nodesPositionInputTextField.setText(fileName);
	}
	
	private void browseNodePositionsOutputFile(){
		JFileChooser jfc = new JFileChooser();
		jfc.addChoosableFileFilter(createFileFilter("2D Positions of The Nodes", true, "pos"));
		if(jfc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION){
			return;
		}
		String fileName = jfc.getSelectedFile().getAbsolutePath();
		if(fileName.endsWith(".pos"))
			nodesPositionOutputTextField.setText(fileName);
		else
			nodesPositionOutputTextField.setText(fileName+".pos");
	}
	
	private void browseGamsModelFile(){
		JFileChooser jfc = new JFileChooser();
		jfc.addChoosableFileFilter(createFileFilter("GAMS Model File", true, "gms"));
		if(jfc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION){
			return;
		}
		String fileName = jfc.getSelectedFile().getAbsolutePath();
		if(fileName.endsWith(".gms"))
			gamsModelOutputTextField.setText(fileName);
		else
			gamsModelOutputTextField.setText(fileName+".gms");
	}
	
	private FileFilter createFileFilter(String description, boolean showExtensionInDescription, String...extensions)
	{
		if (showExtensionInDescription) {
			description = createFileNameFilterDescriptionFromExtensions(description, extensions);
		}
		return new FileNameExtensionFilter(description, extensions);
    }
	
	private String createFileNameFilterDescriptionFromExtensions(String description, String[] extensions)
	{
		String fullDescription = (description == null) ? "(" : description + " (";
		// build the description from the extension list
		fullDescription += "." + extensions[0];
		for (int i = 1; i < extensions.length; i++) {
			fullDescription += ", .";
			fullDescription += extensions[i];
		}
		fullDescription += ")";
		return fullDescription;
	}
}

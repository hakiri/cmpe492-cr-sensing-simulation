package Animation;

import DESSimulation.DESPrimaryTrafficGenerator;
import MultiThreadedSimulation.PrimaryTrafficGenerator;
import SimulationRunner.CRNode;
import SimulationRunner.SimulationRunner;
import SimulationRunner.WirelessChannel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SimulationStatsTable extends JFrame{

	private JPanel jPanel;
	private JTable crStatTable, priStatTable;
	private JScrollPane crScrollPane, priScrollPane;
	private JLabel crLabel, crLabel2, priLabel, priLabel2;
	private String[][] crStats, priStats;
	private String[] crStatNames, priStatNames;
	private JButton saveButton, closeButton;
	private JFileChooser jfc;
	private JFrame parentFrame;
	
	/**
	 * Creates a statistic table for the simulation results. It displays two tables
	 * with CR node statistics and Primary node statistics on them.
	 * @param crStats	CR node statistics
	 * @param priStats	Primary node statistics
	 * @param parent	Parent of the this frame
	 */
	public SimulationStatsTable(String[][] crStats, String[][] priStats, JFrame parent) {
		super("STATS");
		parentFrame = parent;
		parent.setEnabled(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		jfc = new JFileChooser("C:\\Users\\acar\\Desktop");
		jfc.addChoosableFileFilter(createFileFilter("Comma Seperated Value Files", true, "csv"));
		
		jPanel = new JPanel();
		getContentPane().add(jPanel, BorderLayout.CENTER);
		jPanel.setLayout(null);
		
		setStatNames();
		
		this.crStats = crStats;
		this.priStats = priStats;
		
		createCrTable();
		createPriTable();
		
		int length = priStats.length;
		if(crStats.length > length)
			length = crStats.length;
		if(length > 21)
			length = 21;
		
		createButtons(length);
		
		pack();
		int width = 1115;
		int height = 26*length+157;
		this.setSize(width, height);
		this.setLocationRelativeTo(parent);
		this.setResizable(false);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				parentFrame.setEnabled(true);
				parentFrame.toFront();
			}
			
		});
		
	}
	
	private void setStatNames()
	{
		crStatNames = new String[7];
		crStatNames[0] = "CR Node";
		crStatNames[1] = "# of Comm";
		crStatNames[2] = "# of Coll";
		crStatNames[3] = "# of Comm w/o Coll";
		crStatNames[4] = "% of Coll Comm";
		crStatNames[5] = "% of Frames Comm";
		crStatNames[6] = "% of Frames Comm w/o Coll";
		
		priStatNames = new String[4];
		priStatNames[0] = "Primary Node";
		priStatNames[1] = "# of Call Attempts";
		priStatNames[2] = "# of Drops";
		priStatNames[3] = "Comm Dur";
	}
	
	private void createCrTable()
	{
		crLabel = new JLabel("CR Nodes Simulation Statistics:");
		jPanel.add(crLabel);
		crLabel.setBounds(10, 10, 250, 16);
		
		crLabel2 = new JLabel(String.format(Locale.US, "Total number of CR Frames: %d", CRNode.getTotalNumberOfFrames()));
		jPanel.add(crLabel2);
		crLabel2.setBounds(10, 35, 360, 16);
		
		crStatTable = new JTable(crStats, crStatNames);
		crScrollPane = new JScrollPane(crStatTable);
		jPanel.add(crScrollPane);
		if(crStats.length < 21)
			crScrollPane.setBounds(10, 60, 720, 26*crStats.length+28);
		else
			crScrollPane.setBounds(10, 60, 720, 26*21+28);
		crStatTable.setRowHeight(crStatTable.getRowHeight()+10);
		crStatTable.setEnabled(false);
		for(int i=0;i<crStatNames.length;i++){
			crStatTable.getColumn(crStatNames[i]).setMinWidth(1);
		}
		crStatTable.getColumn("CR Node").setPreferredWidth(60);
		crStatTable.getColumn("# of Comm").setPreferredWidth(70);
		crStatTable.getColumn("# of Coll").setPreferredWidth(70);
		crStatTable.getColumn("# of Comm w/o Coll").setPreferredWidth(130);
		crStatTable.getColumn("% of Coll Comm").setPreferredWidth(100);
		crStatTable.getColumn("% of Frames Comm").setPreferredWidth(120);
		crStatTable.getColumn("% of Frames Comm w/o Coll").setPreferredWidth(170);
	}
	
	private void createPriTable()
	{
		priLabel = new JLabel("Primary Nodes Simulation Statistics:");
		jPanel.add(priLabel);
		priLabel.setBounds(740, 10, 250, 16);
		
		double primaryCommDur = Double.parseDouble(priStats[priStats.length-1][priStatNames.length-1]);
		int numberOfFreq = SimulationRunner.wc.numberOfFreq();
		double simDur = 0;
		if(SimulationRunner.animationOnButton.isSelected()){
			simDur = SimulationRunner.crSensor.getSimulationDuration() / WirelessChannel.unitTime;
		}
		else{
			simDur = SimulationRunner.crDesScheduler.getSimulationDuration() / WirelessChannel.unitTime;
		}
		double utilization = (primaryCommDur*100.0)/(simDur*numberOfFreq);
		priLabel2 = new JLabel(String.format(Locale.US, "Primary Nodes Utilization: %.2f",utilization)+"%");
		jPanel.add(priLabel2);
		priLabel2.setBounds(740, 35, 360, 16);
		
		priStatTable = new JTable(priStats, priStatNames);
		priScrollPane = new JScrollPane(priStatTable);
		jPanel.add(priScrollPane);
		if(priStats.length < 21)
			priScrollPane.setBounds(740, 60, 360, 26*priStats.length+28);
		else
			priScrollPane.setBounds(740, 60, 360, 26*21+28);
		priStatTable.setRowHeight(priStatTable.getRowHeight()+10);
		priStatTable.setEnabled(false);
		for(int i=0;i<priStatNames.length;i++){
			priStatTable.getColumn(priStatNames[i]).setMinWidth(1);
		}
		priStatTable.getColumn("Primary Node").setPreferredWidth(100);
		priStatTable.getColumn("# of Call Attempts").setPreferredWidth(120);
		priStatTable.getColumn("# of Drops").setPreferredWidth(70);
		priStatTable.getColumn("Comm Dur").setPreferredWidth(70);
	}

	private void createButtons(int length)
	{
		{
			saveButton = new JButton();
			jPanel.add(saveButton);
			saveButton.setText("SAVE");
			saveButton.setMnemonic('s');
			saveButton.setBounds(840, 26*length+89, 120, 23);
			saveButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					saveData();
				}
			});
			saveButton.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					saveData();
				}

			});
		}
		{
			closeButton = new JButton();
			jPanel.add(closeButton);
			closeButton.setText("CLOSE");
			closeButton.setMnemonic('c');
			closeButton.setBounds(980, 26*length+89, 120, 23);
			closeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			closeButton.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					dispose();
				}

			});
		}
	}
	
	private void saveData()
	{
		PrintWriter pw = null;
		if(jfc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION){
			return;
		}
		
		File f = null;
		String fileName = jfc.getSelectedFile().getAbsolutePath();
		if(fileName.endsWith(".csv"))
			f = jfc.getSelectedFile();
		else
			f = new File(jfc.getSelectedFile().getAbsolutePath() + ".csv");
		
		try {
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f))));
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(this, "An error is occured while openning file!!!", "Save", JOptionPane.ERROR_MESSAGE);
			return;
		}
		double primaryCommDur = Double.parseDouble(priStats[priStats.length-1][priStatNames.length-1]);
		int numberOfFreq = SimulationRunner.wc.numberOfFreq();
		double simDur = 0;
		if(SimulationRunner.animationOnButton.isSelected()){
			simDur = SimulationRunner.crSensor.getSimulationDuration() / WirelessChannel.unitTime;
		}
		else{
			simDur = SimulationRunner.crDesScheduler.getSimulationDuration() / WirelessChannel.unitTime;
		}
		double utilization = (primaryCommDur*100.0)/(simDur*numberOfFreq);
		pw.printf(Locale.US, "Total number of CR Frames:;%d;;;;;;;Primary Node Utilization:;%.2f\n\n",
				CRNode.getTotalNumberOfFrames(),utilization);
		for(int i=0; i<crStatNames.length;i++){
			pw.print(crStatNames[i]+";");
		}
		pw.print(";");
		for(int i=0; i<priStatNames.length;i++){
			pw.print(priStatNames[i]+";");
		}
		pw.print("\n");
		
		int length = priStats.length;
		if(crStats.length > length)
			length = crStats.length;
		for(int i=0;i<length;i++){
			for(int j=0; j<crStatNames.length;j++){
				if(crStats.length > i)
					pw.print(crStats[i][j]);
				pw.print(";");
			}
			pw.print(";");
			for(int j=0; j<priStatNames.length;j++){
				if(priStats.length > i)
					pw.print(priStats[i][j]);
				pw.print(";");
			}
			pw.print("\n");
		}
		
		JOptionPane.showMessageDialog(this, "File \""+f.getName()+"\" Saved!", "Save", JOptionPane.INFORMATION_MESSAGE);
		pw.close();
		
		
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
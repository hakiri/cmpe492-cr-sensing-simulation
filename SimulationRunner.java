package firstproject;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		// TODO code application logic here
		cell = null;
		priTrafGen = new PrimaryTrafficGenerator(5, 4, 100);
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
	private JComboBox channelModel;
	private JButton startSimulation;
	private JTextField alphaNo;

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
				{
					label7 = new JLabel();
					jPanel1.add(label7);
					label7.setText("Channel Model");
					label7.setBounds(12, 85, 165, 16);
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
					channelModel.setBounds(195, 82, 120, 23);
				}
				{
					sectorNo = new JTextField();
					jPanel1.add(sectorNo);
					sectorNo.setBounds(195, 148, 120, 23);
				}
				{
					dNo = new JTextField();
					jPanel1.add(dNo);
					dNo.setBounds(195, 181, 120, 23);
				}
				{
					alphaNo = new JTextField();
					jPanel1.add(alphaNo);
					alphaNo.setBounds(195, 215, 120, 23);
				}
				{
					startSimulation = new JButton();
					jPanel1.add(startSimulation);
					startSimulation.setText("Start");
					startSimulation.setBounds(195, 250, 120, 23);
				}
				{
					label6 = new JLabel();
					jPanel1.add(label6);
					label6.setText("Alpha Number");
					label6.setBounds(12, 218, 165, 16);
				}
				{
					label5 = new JLabel();
					jPanel1.add(label5);
					label5.setText("D Number");
					label5.setBounds(12, 184, 165, 16);
				}
				{
					label4 = new JLabel();
					jPanel1.add(label4);
					label4.setText("Sector Number");
					label4.setBounds(12, 151, 165, 16);
				}
				{
					label3 = new JLabel();
					jPanel1.add(label3);
					label3.setText("Zone");
					label3.setBounds(12, 118, 165, 16);
				}
				{
					noPriNodes = new JTextField();
					jPanel1.add(noPriNodes);
					noPriNodes.setBounds(195, 47, 120, 23);
				}
				{
					label2 = new JLabel();
					jPanel1.add(label2);
					label2.setText("Number of Primary Nodes");
					label2.setBounds(12, 50, 165, 16);
				}
				{
					label1 = new JLabel();
					jPanel1.add(label1);
					label1.setText("Number of CR Nodes");
					label1.setBounds(12, 15, 165, 16);
				}
				{
					noCrNodes = new JTextField();
					jPanel1.add(noCrNodes);
					noCrNodes.setBounds(195, 12, 120, 23);
				}
			}
			pack();
			this.setSize(345, 320);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	public void startSimulation()
	{
		
	}
}

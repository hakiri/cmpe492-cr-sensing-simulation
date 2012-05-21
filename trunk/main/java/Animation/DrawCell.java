package Animation;

import Nodes.Node;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 * This class initiates the animation window. It also provides an interface for
 * drawing new nodes on the animation window or changing the properties of the
 * currently existing ones.
 */
public class DrawCell implements Runnable{
	/**
	 * Runner thread
	 */
	private Thread runner=null;
	/**
	 * boolean variable to terminate thread
	 */
	private boolean finished = false;
	/**
	 * Radius of the cell that is drawn
	 */
	static int radius;
	/**
	 * Radius of the area where the primary users are located
	 */
	static int primaryRadius;
	/**
	 * Number of CR nodes in the zone
	 */
	int numberOfCrNodes;
	/**
	 * Number of primary nodes in the cell
	 */
	int numberOfPriNodes;
	static int unit = 12;
	static Dimension screenDimension;
	static double scale;
	/**
	 * Radius of a node
	 */
	static final int pointRadius = 8;
	private static DrawArea d;
	private JFrame frame;

	/**
	 * Constructs a cell structure to paint
	 * @param priRadius			Radius of primary users cell
     * @param crRadius          Radius of secondary users cell
	 * @param numberOfCrNodes	Number of CR nodes in the zone
	 * @param numberOfPriNodes	Number of primary nodes in the cell
	 */
	public DrawCell(int priRadius, int crRadius, int numberOfCrNodes, int numberOfPriNodes) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		screenDimension = toolkit.getScreenSize();
		int screenHeight = screenDimension.height;
		scale = (2.0*priRadius*unit)/(screenHeight-96);
		
		DrawCell.primaryRadius = (int)(priRadius/scale);
		DrawCell.radius = (int)(crRadius/scale);
		this.numberOfCrNodes=numberOfCrNodes;
		this.numberOfPriNodes=numberOfPriNodes;
		finished = false;
		
		d = new DrawArea(primaryRadius*unit, radius*unit, numberOfCrNodes, numberOfPriNodes);
		if(runner==null){
            runner=new Thread(this);            //Create the thread
            runner.start();			//Start the thread: This method will call run method below
        }
	}

	@Override
	public void run() {
		draw();
		while(!finished){
			try {
				Thread.sleep(50);
			} catch (InterruptedException ex) {
				Logger.getLogger(DrawCell.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		WindowEvent wev = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
		
		finished = true;
	}
	
	static int windowWidth;
	/**
	 * Draws the initial cell
	 */
	public void draw()
    {
        frame=new JFrame("AREA");
        d.setBackground(Color.WHITE);
        frame.add(d);
		windowWidth = primaryRadius*unit*2+25;
        frame.setSize(primaryRadius*unit*2+20, primaryRadius*unit*2+51);
        frame.setVisible(true);
    }
	
	/**
	 * Adds a Primary node to the cell with given color
	 * @param n Node to be added
	 * @param c Nodes color: RED for transmitting or BLACK for sleeping
	 */
	public static void paintPrimaryNode(Node n, Color c)
	{
		PointColor p = new PointColor(n.getPosition(), (int)((pointRadius/8.0)*7.0), c, unit);
		p.convertCoordinate(primaryRadius*unit);
		d.paintPrimary(n.getId(), p);
	}
	
	/**
	 * Adds a CR node to the cell
	 * @param n Node to be added
	 * @param c Color of the node
	 */
	public static void paintCrNode(Node n, Color c)
	{
		PointColor p = new PointColor(n.getPosition(), (int)(pointRadius*2), c, unit);
		p.convertCoordinate(primaryRadius*unit);
		d.paintCR(n.getId(), p);
	}
	
	/**
	 * Draws or erases collision warning for a node.
	 * @param n		Related node
	 * @param draw	<ul>
	 *					<li>If <b><i>True</i></b> draws collision warning</li>
	 *					<li>If <b><i>False</i></b> erases collision warning</li>
	 *				</ul>
	 */
	public static void drawCollision(Node n, boolean draw)
	{
		PointColor p = new PointColor(n.getPosition(), (int)(pointRadius*4), Color.RED, unit);
		p.convertCoordinate(primaryRadius*unit);
		d.paintCollision(n.getId(), p, draw);
	}
	
	/**
	 * Returns whether the thread is finished or not
	 * @return finished
	 */
	public boolean isFinished()
	{
		return finished;
	}
	
	/**
	 * Terminates the thread
	 */
	public void terminate()
	{
		finished=true;
	}
}

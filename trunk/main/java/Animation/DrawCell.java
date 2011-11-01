package Animation;

import Nodes.Node;
import java.awt.Color;
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
	 * Number of sectors in the cell
	 */
	int numberOfSectors;
	/**
	 * Number of alpha sections in a sector
	 */
	int numberOfAlpha;
	/**
	 * Number of CR nodes in the zone
	 */
	int numberOfCrNodes;
	/**
	 * Number of primary nodes in the cell
	 */
	int numberOfPriNodes;
	private static int unit = 11;
	/**
	 * Radius of a node
	 */
	static final int pointRadius = 8;
	private static DrawArea d;
	private JFrame frame;

	/**
	 * Constructs a cell structure to paint
	 * @param radius			Radius of the cell
	 * @param numberOfSectors	Number of sectors in the cell
	 * @param numberOfAlpha		Number of alpha sections in a sector
	 * @param numberOfDSections Number of distance sections in a alpha slice
	 * @param numberOfCrNodes	Number of CR nodes in the zone
	 * @param numberOfPriNodes	Number of primary nodes in the cell
	 */
	public DrawCell(int radius, int numberOfSectors, int numberOfAlpha, int numberOfDSections, int numberOfCrNodes, int numberOfPriNodes) {
		this.radius = radius;
		this.numberOfSectors = numberOfSectors;
		this.numberOfAlpha = numberOfAlpha;
		this.numberOfCrNodes=numberOfCrNodes;
		this.numberOfPriNodes=numberOfPriNodes;
		finished = false;
		
		d = new DrawArea(radius*unit, numberOfSectors, numberOfAlpha, numberOfDSections, numberOfCrNodes, numberOfPriNodes);
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
	
	/**
	 * Draws the initial cell
	 */
	public void draw()
    {
        frame=new JFrame("AREA");
        d.setBackground(Color.WHITE);
        frame.add(d);
        frame.setSize(radius*unit*2+20, radius*unit*2+41);
        frame.setVisible(true);
    }
	
	/**
	 * Adds a Primary node to the cell with given color
	 * @param n Node to be added
	 * @param c Nodes color: RED for transmitting or BLACK for sleeping
	 */
	public static void paintPrimaryNode(Node n, Color c)
	{
		PointColor p = new PointColor(n.getPosition(), pointRadius, c, unit);
		p.convertCoordinate(radius*unit);
		d.paintPrimary(n.getId(), p);
	}
	
	/**
	 * Adds a CR node to the cell
	 * @param n Node to be added
	 */
	public static void paintCrNode(Node n, Color c)
	{
		PointColor p = new PointColor(n.getPosition(), pointRadius, c, unit);
		p.convertCoordinate(radius*unit);
		d.paintCR(n.getId(), p);
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

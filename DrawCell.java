package firstproject;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class DrawCell implements Runnable{
	/**
	 * Runner thread
	 */
	private Thread runner=null;
	/**
	 * boolean variable to terminate thread
	 */
	private boolean finished = false;
	Integer id;
	
	static int radius;
	int numberOfSectors;
	int sector;
	int numberOfAlpha;
	int alpha;
	int dmin;
	int dmax;
	int numberOfCrNodes;
	int numberOfPriNodes;
	private static int unit = 10;
	static final int pointRadius = 10;
	private static DrawArea d;
	private JFrame frame;

	/**
	 * Constructs a cell structure to paint
	 * @param radius Radius of the cell
	 * @param numberOfSectors Number of sectors in the cell
	 * @param sector Used sector by the CR nodes
	 * @param numberOfAlpha Number of alpha sections in a sector
	 * @param alpha Used alpha section by the CR nodes
	 * @param dmin Min distance from the origin
	 * @param dmax Max distance from the origin
	 * @param numberOfCrNodes Number of CR nodes in the zone
	 * @param numberOfPriNodes Number of primary nodes in the cell
	 */
	public DrawCell(int radius, int numberOfSectors, int sector, int numberOfAlpha, int alpha, int dmin, int dmax,
			int numberOfCrNodes, int numberOfPriNodes) {
		this.radius = radius;
		this.numberOfSectors = numberOfSectors;
		this.sector = sector;
		this.numberOfAlpha = numberOfAlpha;
		this.alpha = alpha;
		this.dmin = dmin;
		this.dmax = dmax;
		this.numberOfCrNodes=numberOfCrNodes;
		this.numberOfPriNodes=numberOfPriNodes;
		finished = false;
	
		d=new DrawArea(radius*unit, numberOfSectors, sector, numberOfAlpha, alpha, dmin*unit, dmax*unit, numberOfCrNodes, numberOfPriNodes);
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
				Thread.sleep(100);
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
	 * @param c Nodes color: RED or BLACK
	 */
	public static void paintPrimaryNode(Node n, Color c)
	{
		PointColor p = new PointColor(n.position, unit/2, c, unit);
		p.convertCoordinate(radius*unit);
		d.paintPrimary(n.id, p);
	}
	
	/**
	 * Adds a CR node to the cell
	 * @param n Node to be added
	 */
	public static void paintCrNode(Node n)
	{
		PointColor p = new PointColor(n.position, unit/2, Color.GREEN, unit);
		p.convertCoordinate(radius*unit);
		d.paintCR(n.id, p);
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

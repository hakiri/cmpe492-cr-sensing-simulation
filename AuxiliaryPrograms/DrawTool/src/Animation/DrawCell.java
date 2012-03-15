package Animation;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 * An interface to command DrawArea class objects.
 */
public class DrawCell implements Runnable{

	private Thread runner=null;
	private boolean finished = false;
	static int radius;
	static int numberOfNodes;
	static int numberOfClusters;
	static int unit = 12;
	static final int pointRadius = 4;
	private static DrawArea d;
	private JFrame frame;
	static ArrayList<ArrayList<Integer>> xij = new ArrayList<>();
	static boolean drawDot;
	
	static int CONST = 40;

	/**
	 * Starts a thread for animation/drawing and sets its parameters.
	 * @param cellRadius		Half of the one side of the square
	 * @param numberOfNodes		Number of nodes in the area
	 * @param numberOfClusters	Number of clusters in the area
	 * @param constt			Drawing scale factor (smaller the value larger the screen)
	 * @param xij				Data of which node belongs to which cluster.
	 */
	public DrawCell(int cellRadius, int numberOfNodes, int numberOfClusters, int constt, ArrayList<ArrayList<Integer>> xij, boolean drawDot) {
		DrawCell.CONST = constt;
		DrawCell.radius = cellRadius/CONST;
		DrawCell.numberOfNodes = numberOfNodes;
		DrawCell.numberOfClusters = numberOfClusters;
		DrawCell.xij = xij;
		DrawCell.drawDot = drawDot;
		finished = false;
		
		d = new DrawArea(radius*unit, numberOfNodes, numberOfClusters);
		if(runner==null){
            runner=new Thread(this);            //Create the thread
            runner.start();			//Start the thread: This method will call run method below
        }
	}

	@Override
	public void run() {
		draw();
		WindowEvent wev = new WindowEvent(frame, JFrame.EXIT_ON_CLOSE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
		while(!finished){
			try {
				Thread.sleep(50);
			} catch (InterruptedException ex) {
				Logger.getLogger(DrawCell.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		finished = true;
	}
	
	private void draw()
    {
        frame=new JFrame("AREA");
        d.setBackground(Color.WHITE);
        frame.add(d);
        frame.setSize(radius*unit*2+20, radius*unit*2+51);
		frame.setLocation(900, 0);
        frame.setVisible(true);
    }
	
	/**
	 * Draws a cluster center with given id and position.
	 * @param n		Position of the cluster center
	 * @param id	ID of the cluster center
	 */
	public static void paintClusterCenter(Point2D.Double n, int id)
	{
		PointColor p = new PointColor(n, (int)(pointRadius), Color.RED, unit);
		p.convertCoordinate(radius*unit);
		d.paintClusterCenter(id, p);
	}
	
	/**
	 * Draws a node with given id and position.
	 * @param n		Position of the node
	 * @param id	ID of the node
	 */
	public static void paintNode(Point2D.Double n, int id)
	{
		PointColor p = new PointColor(n, (int)(pointRadius), Color.BLUE, unit);
		p.convertCoordinate(radius*unit);
		d.paintNode(id, p);
	}
	
	/**
	 * Return whether the thread is finished or not
	 * @return True if thread is finished false otherwise
	 */
	public boolean isFinished()
	{
		return finished;
	}
	
	/**
	 * Terminates the main thread
	 */
	public void terminate()
	{
		finished=true;
	}
	
	/**
	 * Set draw cell status of the object
	 * @param status
	 */
	public static void drawCell(boolean status)
	{
		d.setDrawCell(status);
	}
}

package Animation;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

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
	
	static int CONST = 40;

	public DrawCell(int crRadius, int numberOfNodes, int numberOfClusters, ArrayList<ArrayList<Integer>> xij) {
		DrawCell.radius = crRadius/CONST;
		DrawCell.numberOfNodes = numberOfNodes;
		DrawCell.numberOfClusters = numberOfClusters;
		DrawCell.xij = xij;
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
	
	public void draw()
    {
        frame=new JFrame("AREA");
        d.setBackground(Color.WHITE);
        frame.add(d);
        frame.setSize(radius*unit*2+20, radius*unit*2+51);
		frame.setLocation(900, 0);
        frame.setVisible(true);
    }
	
	public static void paintClusterCenter(Point2D.Double n, Color c, int id)
	{
		PointColor p = new PointColor(n, (int)(pointRadius), c, unit);
		p.convertCoordinate(radius*unit);
		d.paintClusterCenter(id, p);
	}
	
	public static void paintNode(Point2D.Double n, Color c, int id)
	{
		PointColor p = new PointColor(n, (int)(pointRadius), c, unit);
		p.convertCoordinate(radius*unit);
		d.paintNode(id, p);
	}
	
	public boolean isFinished()
	{
		return finished;
	}
	
	public void terminate()
	{
		finished=true;
	}
	
	public static void drawCell(boolean status)
	{
		d.setDrawCell(status);
	}
}

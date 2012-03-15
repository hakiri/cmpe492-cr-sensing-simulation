package Animation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 * This class extends JPanel to perform cell, cluster, and node structure specific drawing operations.
 */
public class DrawArea extends JPanel{

	private int radius;
	private HashMap<Integer, PointColor> clusters;
	private HashMap<Integer, PointColor> nodes;
	private Color[] colors = new Color[10]; 
	private boolean drawCell;
    
	/**
	 * Creates a square DrawArea object.
	 * @param cellRadius		Half of the one side of the square
	 * @param numberOfNodes		Number of nodes
	 * @param numberOfClusters	Number of clusters
	 */
	public DrawArea(int cellRadius, int numberOfNodes, int numberOfClusters) {
        super();
		this.radius = cellRadius;
		clusters = new HashMap<Integer, PointColor>();
		for(int i=0;i<numberOfClusters;i++)
			clusters.put(i, null);
		nodes = new HashMap<Integer, PointColor>();
		for(int i=0;i<numberOfNodes;i++)
			nodes.put(i, null);
		colors[0] = Color.BLUE;
		colors[1] = Color.CYAN;
		colors[2] = Color.DARK_GRAY;
		colors[3] = Color.GRAY;
		colors[4] = Color.GREEN;
		colors[5] = Color.LIGHT_GRAY;
		colors[6] = Color.MAGENTA;
		colors[7] = Color.ORANGE;
		colors[8] = Color.PINK;
		colors[9] = Color.YELLOW;
		this.drawCell = false;
    }
    
	@Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
		if(drawCell){
			g.setColor(Color.BLACK);
			g.drawOval(0, 0, radius*2, radius*2);
		}
    }

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		g.setFont(new Font(g.getFont().getName(), Font.PLAIN, 10));
		for(int i=0;i<DrawCell.numberOfClusters;i++){
			PointColor pc = clusters.get(i);
			for(int j=0;j<DrawCell.xij.get(i).size();j++){
				PointColor pn = nodes.get(DrawCell.xij.get(i).get(j));
				if(pc == null || pn == null)
					continue;
				g.setColor(Color.BLACK);
				g.drawLine(pc.x, pc.y, pn.x, pn.y);
				g.setColor(colors[i%10]);
				if(DrawCell.drawDot)
					g.fillOval((int)(pn.x - pn.r/2.0), (int)(pn.y - pn.r/2.0), (int)(pn.r), (int)(pn.r));
				else
					g.drawString(String.valueOf(DrawCell.xij.get(i).get(j)), pn.x, pn.y);
			}
		}
		
		g.setColor(Color.RED);
		g.setFont(new Font(g.getFont().getName(), Font.BOLD, 24));
		for(Integer i:clusters.keySet()){
			PointColor p = clusters.get(i);
			if(p==null)
				continue;
			
			if(DrawCell.drawDot)
				g.fillOval(p.x-p.r, p.y-p.r, 2*p.r, 2*p.r);
			else
				g.drawString(String.valueOf(i), p.x, p.y);
		}
	}
	
	/**
	 * Draws a cluster center with given id and position.
	 * @param id	ID of the cluster center
	 * @param p		Position of the cluster center
	 */
	public void paintClusterCenter(Integer id, PointColor p)
	{
		clusters.put(id, p);
		repaint();
	}
	
	/**
	 * Draws a node with given id and position.
	 * @param id	ID of the node
	 * @param p		Position of the node
	 */
	public void paintNode(Integer id, PointColor p)
	{
		nodes.put(id, p);
		repaint();
	}

	/**
	 * Returns the current positions of the nodes
	 * @return Hash table that contains the positions of the nodes
	 */
	public HashMap<Integer, PointColor> getNodes() {
		return nodes;
	}

	/**
	 * Returns the current positions of the cluster centers
	 * @return Hash table that contains the positions of the cluster centers
	 */
	public HashMap<Integer, PointColor> getClusterCenters() {
		return clusters;
	}

	/**
	 * Returns true if a circular cell is drawn
	 * @return <b>True</b> if a circular cell is drawn, <b>False</b> otherwise
	 */
	public boolean isDrawCell() {
		return drawCell;
	}

	/**
	 * Set draw cell status of the object
	 * @param drawCell
	 */
	public void setDrawCell(boolean drawCell) {
		this.drawCell = drawCell;
	}
}

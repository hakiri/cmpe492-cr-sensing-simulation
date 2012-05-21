package Animation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * This class handles the animation window.
 */
public class DrawArea extends JPanel{
	/**
	 * Radius multiplied with unit
	 */
    private int cellRadius;
	private HashMap<Integer, PointColor> primaryNodes;
	private HashMap<Integer, PointColor> crNodes;
	private HashMap<Integer, PointColor> crNodeCollisionWarning;
	private int primaryRadius;
    
	/**
	 * Creates an animation window.
     * @param priRadius         Radius of the primary users cell
     * @param cellRadius		Radius of the secondary users cell
	 * @param numberOfCrNodes	Number of CR nodes in the zone
	 * @param numberOfPriNodes	Number of Primary nodes in the cell
	 */
    public DrawArea(int priRadius, int cellRadius, int numberOfCrNodes, int numberOfPriNodes) {
        super();
		this.primaryRadius = priRadius;
		this.cellRadius = cellRadius;
		primaryNodes = new HashMap<>();
		for(int i=0;i<numberOfPriNodes;i++)
			primaryNodes.put(i, null);
		crNodes = new HashMap<>();
		for(int i=0;i<numberOfCrNodes;i++)
			crNodes.put(i, null);
		crNodeCollisionWarning = new HashMap<>();
		for(int i=0;i<numberOfCrNodes;i++)
			crNodeCollisionWarning.put(i, null);
    }
    
	@Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
		g.setColor(Color.RED);
        g.drawOval(0, 0, primaryRadius*2, primaryRadius*2);
        g.setColor(Color.BLACK);
        g.drawOval(primaryRadius - cellRadius, primaryRadius - cellRadius, cellRadius*2, cellRadius*2);
    }

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		for(Integer i:primaryNodes.keySet()){
			PointColor p = primaryNodes.get(i);
			if(p==null)
				continue;
			g.setColor(p.c);
			g.fillOval(p.x-p.r/2, p.y-p.r/2, p.r, p.r);
		}
		BufferedImage comm = null;
		BufferedImage idle = null;
		try {
			comm = ImageIO.read(new File("comm.JPG"));
			idle = ImageIO.read(new File("idle.JPG"));
		} catch (IOException ex) {
			Logger.getLogger(DrawArea.class.getName()).log(Level.SEVERE, null, ex);
		}
		for(Integer i:crNodes.keySet()){
			
			PointColor p = crNodes.get(i);
			if(p==null)
				continue;
			g.setColor(p.c);

			if(p.c != Color.GREEN)
				g.drawImage(idle, (int)(p.x - p.r/2.0), (int)(p.y - p.r/2.0), (int)(p.r), (int)(p.r), null);
		}
		for(Integer i:crNodes.keySet()){
			PointColor p = crNodes.get(i);
			if(p==null)
				continue;
			if(p.c == Color.GREEN){
				g.drawImage(comm, (int)(p.x - p.r/2.0), (int)(p.y - p.r/2.0), (int)(p.r), (int)(p.r), null);
			}
		}
		for(Integer i:crNodeCollisionWarning.keySet()){
			PointColor p = crNodeCollisionWarning.get(i);
			if(p==null)
				continue;
			g.setColor(Color.RED);
			g.fillOval(p.x-p.r/16, p.y-p.r/2, p.r/8, (p.r*6)/8);
			g.fillOval(p.x-p.r/16, p.y+(p.r*3)/8, p.r/8, p.r/8);
		}
	}
	
	/**
	 * This method paints a primary node with the given id, position, and color.
	 * @param id	ID of the primary node
	 * @param p		Position of the CR node
	 */
	public void paintPrimary(Integer id, PointColor p)
	{
		primaryNodes.put(id, p);
		repaint();
	}
	
	/**
	 * This method paints a CR node with the given id, position.
	 * @param id	ID of the CR node
	 * @param p		Position of the CR node
	 */
	public void paintCR(Integer id, PointColor p)
	{
		crNodes.put(id, p);
		repaint();
	}
	
	/**
	 * This method paints a CR node with the given id, position.
	 * @param id	ID of the collided CR node
	 * @param p		Position of the CR node
	 * @param draw	<ul>
	 *					<li>If <b><i>True</i></b> draws collision warning</li>
	 *					<li>If <b><i>False</i></b> erases collision warning</li>
	 *				</ul>
	 */
	public void paintCollision(Integer id, PointColor p, boolean draw)
	{
		if(draw)
			crNodeCollisionWarning.put(id, p);
		else
			crNodeCollisionWarning.put(id, null);
		repaint();
	}

	/**
	 * Returns the collision warnings of secondary users
	 * @return The collision warnings of secondary users
	 */
	public HashMap<Integer, PointColor> getCrNodeCollisionWarning() {
		return crNodeCollisionWarning;
	}

	/**
	 * Returns the drawable secondary users and their draw data
	 * @return The drawable secondary users and their draw data
	 */
	public HashMap<Integer, PointColor> getCrNodes() {
		return crNodes;
	}

	/**
	 * Returns the drawable primary users and their draw data
	 * @return The drawable primary users and their draw data
	 */
	public HashMap<Integer, PointColor> getPrimaryNodes() {
		return primaryNodes;
	}
}

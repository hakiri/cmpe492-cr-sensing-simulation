package ALA;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import javax.swing.JPanel;

public class DrawArea extends JPanel{

	private int radius;
	private HashMap<Integer, PointColor> clusters;
	private HashMap<Integer, PointColor> nodes;
	Color[] colors = new Color[10]; 
    
    public DrawArea(int cellRadius, int numberOfCrNodes, int numberOfPriNodes) {
        super();
		this.radius = cellRadius;
		clusters = new HashMap<Integer, PointColor>();
		for(int i=0;i<numberOfPriNodes;i++)
			clusters.put(i, null);
		nodes = new HashMap<Integer, PointColor>();
		for(int i=0;i<numberOfCrNodes;i++)
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
    }
    
	@Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
		g.setColor(Color.BLACK);
        g.drawOval(0, 0, radius*2, radius*2);
    }

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		for(int i=0;i<ALAHueristicMain.numberOfClusters;i++){
			PointColor pc = clusters.get(i);
			for(int j=0;j<ALAHueristicMain.yij.get(i).size();j++){
				PointColor pn = nodes.get(ALAHueristicMain.yij.get(i).get(j));
				if(pc == null || pn == null)
					continue;
				g.setColor(Color.BLACK);
				g.drawLine(pc.x, pc.y, pn.x, pn.y);
				g.setColor(colors[i%10]);
				g.fillOval((int)(pn.x - pn.r/2.0), (int)(pn.y - pn.r/2.0), (int)(pn.r), (int)(pn.r));
			}
		}
		
		g.setColor(Color.RED);
		for(Integer i:clusters.keySet()){
			PointColor p = clusters.get(i);
			if(p==null)
				continue;
			
			g.fillOval(p.x-p.r, p.y-p.r, 2*p.r, 2*p.r);
		}
	}
	
	public void paintClusterCenter(Integer id, PointColor p)
	{
		clusters.put(id, p);
		repaint();
	}
	
	public void paintNode(Integer id, PointColor p)
	{
		nodes.put(id, p);
		repaint();
	}

	public HashMap<Integer, PointColor> getNodes() {
		return nodes;
	}

	public HashMap<Integer, PointColor> getClusterCenters() {
		return clusters;
	}
}

package firstproject;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import javax.swing.JPanel;

public class DrawArea extends JPanel{
	/**
	 * Radius multiplied with unit
	 */
    private int cellRadius;
	private int numberOfSectors, sector, numberOfAlpha, alpha, dmin, dmax, numberOfCrNodes, numberOfPriNodes;
	private HashMap<Integer, PointColor> primaryNodes;
	private HashMap<Integer, PointColor> crNodes;
    //--------------------------------------------------------------------------
    public DrawArea(int cellRadius,int numberOfSectors, int sector, int numberOfAlpha, int alpha, int dmin, int dmax,
			int numberOfCrNodes, int numberOfPriNodes) {
        super();
		this.cellRadius = cellRadius;
		this.numberOfSectors = numberOfSectors;
		this.sector = sector;
		this.numberOfAlpha = numberOfAlpha;
		this.alpha = alpha;
		this.dmin = dmin;
		this.dmax = dmax;
		this.numberOfCrNodes=numberOfCrNodes;
		this.numberOfPriNodes=numberOfPriNodes;
		primaryNodes = new HashMap<Integer, PointColor>();
		for(int i=0;i<numberOfPriNodes;i++)
			primaryNodes.put(i, null);
		crNodes = new HashMap<Integer, PointColor>();
		for(int i=0;i<numberOfCrNodes;i++)
			crNodes.put(i, null);
    }
    //--------------------------------------------------------------------------
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.drawOval(0, 0, cellRadius*2, cellRadius*2);
		int tx,ty;
		int sectorBegDegree = (360/numberOfSectors)*sector;
		int sectorEndDegree = (360/numberOfSectors)*(sector+1);
//		tx=cellRadius+(int)(cellRadius*Math.cos(((double)(sectorBegDegree)/(double)180)*Math.PI));
//		ty=cellRadius-(int)(cellRadius*Math.sin(((double)(sectorBegDegree)/(double)180)*Math.PI));
//		g.drawLine(cellRadius, cellRadius, tx, ty);
//		tx=cellRadius+(int)(cellRadius*Math.cos(((double)(sectorEndDegree)/(double)180)*Math.PI));
//		ty=cellRadius-(int)(cellRadius*Math.sin(((double)(sectorEndDegree)/(double)180)*Math.PI));
//		g.drawLine(cellRadius, cellRadius, tx, ty);
		int sectorInc = 360/numberOfSectors;
		for(int degree=0;degree<360;degree+=sectorInc){
			tx=cellRadius+(int)(cellRadius*Math.cos(((double)(degree)/(double)180)*Math.PI));
			ty=cellRadius-(int)(cellRadius*Math.sin(((double)(degree)/(double)180)*Math.PI));
			g.drawLine(cellRadius, cellRadius, tx, ty);
		}
		
		g.setColor(Color.ORANGE);
		int alphaInc = (sectorEndDegree - sectorBegDegree)/numberOfAlpha;
		for(int degree=sectorBegDegree+alphaInc;degree<sectorEndDegree;degree+=alphaInc){
			tx=cellRadius+(int)(cellRadius*Math.cos(((double)(degree)/(double)180)*Math.PI));
			ty=cellRadius-(int)(cellRadius*Math.sin(((double)(degree)/(double)180)*Math.PI));
			g.drawLine(cellRadius, cellRadius, tx, ty);
		}
		
		g.setColor(Color.BLUE);
		int zoneBegDegree = sectorBegDegree+alphaInc*alpha;
		int zoneEndDegree = sectorBegDegree+alphaInc*(alpha+1);
		tx=cellRadius+(int)(dmin*Math.cos(((double)(zoneBegDegree)/(double)180)*Math.PI));
		ty=cellRadius-(int)(dmin*Math.sin(((double)(zoneBegDegree)/(double)180)*Math.PI));
		int tx2=cellRadius+(int)(dmax*Math.cos(((double)(zoneBegDegree)/(double)180)*Math.PI));
		int ty2=cellRadius-(int)(dmax*Math.sin(((double)(zoneBegDegree)/(double)180)*Math.PI));
		g.drawLine(tx, ty, tx2, ty2);
		tx=cellRadius+(int)(dmin*Math.cos(((double)(zoneEndDegree)/(double)180)*Math.PI));
		ty=cellRadius-(int)(dmin*Math.sin(((double)(zoneEndDegree)/(double)180)*Math.PI));
		tx2=cellRadius+(int)(dmax*Math.cos(((double)(zoneEndDegree)/(double)180)*Math.PI));
		ty2=cellRadius-(int)(dmax*Math.sin(((double)(zoneEndDegree)/(double)180)*Math.PI));
		g.drawLine(tx, ty, tx2, ty2);
		g.drawArc(cellRadius-dmin, cellRadius-dmin, 2*dmin, 2*dmin, zoneBegDegree, alphaInc);
		g.drawArc(cellRadius-dmax, cellRadius-dmax, 2*dmax, 2*dmax, zoneBegDegree, alphaInc);
    }

	public void paint(Graphics g)
	{
		super.paint(g);
		for(Integer i:primaryNodes.keySet()){
			PointColor p = primaryNodes.get(i);
			if(p==null)
				continue;
			g.setColor(p.c);
			g.fillOval(p.x, p.y, p.r, p.r);
		}
		for(Integer i:crNodes.keySet()){
			PointColor p = crNodes.get(i);
			if(p==null)
				continue;
			g.setColor(p.c);
			g.fillOval(p.x-p.r/2, p.y-p.r/2, p.r, p.r);
		}
	}
	
	public void paintPrimary(Integer id, PointColor p)
	{
		primaryNodes.put(id, p);
		repaint();
	}
	
	public void paintCR(Integer id, PointColor p)
	{
		crNodes.put(id, p);
		repaint();
	}
}
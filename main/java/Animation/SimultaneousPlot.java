/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Animation;

import jPlot.ApplicationFrame;
import jPlot.Constant;
import jPlot.jPlot;
import java.awt.Dimension;
import java.util.ArrayList;

public class SimultaneousPlot {
	ApplicationFrame ApplicationFrame;
	Plot plot;
	boolean plotted;
	
	public SimultaneousPlot(int xs, ArrayList<Integer> yPerX)
	{
		plot = new Plot(xs, yPerX);
		plotted = false;
	}
	
	public void resynch()
	{
		ApplicationFrame.resynch();
	}
	
	public void addPoint(int xPos, double xVal, ArrayList<Double> yVals)
	{
		plot.addPoint(xPos, xVal, yVals);
	}
	
	public void plotAllXWithLegend(String title, int xPos, ArrayList<String> names, double simulationDuration)
	{
		ArrayList<Integer> xs = new ArrayList<Integer>();
		xs.add(xPos);
		ArrayList<ArrayList<Integer>> ys = new ArrayList<ArrayList<Integer>>();
		ys.add(new ArrayList<Integer>());
		ArrayList<ArrayList<String>> nameList = new ArrayList<ArrayList<String>>();
		nameList.add(names);
		for(int j=0;j<plot.getY().get(xPos).size();j++){
			ys.get(0).add(j);
		}

		if(simulationDuration > 0)
			plot.plot(title, xs, ys, nameList,0.0,simulationDuration);
		else
			plot.plot(title, xs, ys, nameList);
		
		if(plotted){
			resynch();
			return;
		}
		
		jPlot jPlot = new jPlot();
		String []argv = new String[1];
		argv[0]=title;
		jPlot.passArguments(argv);
		ApplicationFrame = new ApplicationFrame(title, jPlot);
		/* Add the new application frame to the list */

		/* Set the fram size to the dimension above */
		ApplicationFrame.setSize(new Dimension(Constant.CASCADE_WIDTH-20, Constant.CASCADE_HEIGHT));
		/* Set the title of the frame window */
		ApplicationFrame.setTitle(title);
		/* Validates the size of all components within the frame. (Needed for correct display). */
		ApplicationFrame.validate();
		/* Setting the location at which the frame is to be displayed on the screen */
		ApplicationFrame.setLocation(680, 0);
		/* Display the frame */
		ApplicationFrame.show();
		plotted = true;
	}
}

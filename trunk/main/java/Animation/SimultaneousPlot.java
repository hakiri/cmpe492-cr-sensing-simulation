package Animation;

import SimulationRunner.SimulationRunner;
import jPlot.ApplicationFrame;
import jPlot.Constant;
import jPlot.jPlot;
import java.awt.Dimension;
import java.util.ArrayList;

/**
 * This class draws a synchronous plot during animation. Plotted values on the
 * graphs are drawn simultaneously with simulation if simulation is running on
 * animation mode.
 */
public class SimultaneousPlot {
	ApplicationFrame ApplicationFrame;
	Plot plot;
	boolean plotted;
	int xs;
	
	/**
	 * Creates a new plotter object with no x value and y value.
	 * @param xs	 Number of different x values
	 * @param yPerX	 How many y belongs to each x
	 * @param xLabel Label of x axis
	 * @param yLabel Label of y axis
	 * @param xUnits Units of the x axis
	 * @param yUnits Units of the y axis
	 */
	public SimultaneousPlot(int xs, ArrayList<Integer> yPerX, String xLabel, String yLabel, String xUnits, String yUnits)
	{
		this.xs = xs;
		plot = new Plot(xs, yPerX, xLabel, yLabel, xUnits, yUnits);
		plotted = false;
	}
	
	/**
	 * Resynchronize the plot window with the data file.
	 */
	public void resynch()
	{
		ApplicationFrame.resynch();
	}
	
	/**
	 * Adds an x value and its corresponding y values plotter.
	 * @param xVal		x value to be added
	 * @param yVals		y values to be added
	 */
	public void addPoint(double xVal, ArrayList<Double> yVals)
	{
		for(int i = 0; i < xs ; i++){
			ArrayList<Double> temp = new ArrayList<Double>();
			temp.add(yVals.get(i));
			plot.addPoint(i, xVal, temp);
		}
	}
	
	/**
	 * Plots all available y values belongs to given x value onto different
	 * graphs on different windows when first called. In following calls it
	 * just resynchronizes the plot with newly added data.
	 * @param title					Title of the graph
	 * @param xPos					x value to be plotted
	 * @param names					Legend of plots
	 * @param simulationDuration	Duration of the simulation
	 */
	public void plotAllXWithLegend(String title, int xPos, ArrayList<String> names, double simulationDuration)
	{
		ArrayList<Integer> xes = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> ys = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<String>> nameList = new ArrayList<ArrayList<String>>();
		for(int i=0;i<xs;i++){
			ys.add(new ArrayList<Integer>());
			nameList.add(new ArrayList<String>());
			ys.get(i).add(0);
			nameList.get(i).add(names.get(i));
			xes.add(i);
		}
		
		if(simulationDuration > 0)
			plot.plot(title, xes, ys, nameList,0.0,simulationDuration);
		else
			plot.plot(title, xes, ys, nameList);
		
		if(SimulationRunner.args.isBatchMode())
			return;
		
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

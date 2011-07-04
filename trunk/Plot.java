package firstproject;

import jPlot.jPlot;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;


/**
 * This class plots m graphs with the same x values and different y values.
 */
public class Plot {
	/**
	 * Available colors
	 */
	private static final String[] colors={"white", "green", "red", "blue", "yellow", "purple", "orange", "magenta"
											,"pink", "gray20"};
	/**
	 * Available data point shapes
	 */
	private static final String[] shapes={"diamond", "box", "x", "plus"};
	/**
	 * x values of the graphs
	 */
	private ArrayList<Double> x;
	/**
	 * y values of the graphs
	 */
	private ArrayList<ArrayList<Double>> y;
	/**
     * Writer for the log file.
     */
    private static PrintWriter pw = null;
	
	/**
	 * Creates a new plotter object with no x value and y value. It creates a two dimensional
	 * array whose second dimension is given by parameter.
	 * @param numberOfFreq	Number of grahps which also number of different y values
	 */
	public Plot(int numberOfFreq)
	{
		x = new ArrayList<Double>();				//Create an empty array
		y = new ArrayList<ArrayList<Double>>();		//Create an empty array of array for y values
		for(int i=0;i<numberOfFreq;i++){
			y.add(new ArrayList<Double>());			//Then create arrays that will hold y values
		}
	}
	
	/**
	 * Adds an x value and its corresponding y values. This methods insert values
	 * in a way that will keep x values array always sorted
	 * @param xVal		x value to be added
	 * @param yVals		y values to be added
	 * @return			true if values added false otherwise.
	 */
	public boolean addPoint(double xVal, ArrayList<Double> yVals)
	{
		if(yVals.size()!=y.size())
			return false;
		int index = Collections.binarySearch(x, xVal);
		if(index<0)
			index = -(index+1);
		x.add(index, xVal);						//Adds the x value
		for(int i=0;i<y.size();i++){		//Adds the y values one by one
			y.get(i).add(index, yVals.get(i));
		}
		return true;
	}
	
	/**
	 * Creates x versus the given y value file to be used to plot its graph. This 
	 * method returns immediately if the frequency is not valid.
	 * @param title		title of the graph
	 * @param freq		Frequency to be plotted
	 * @param simDur	Duration of the simulation. i.e. max x value
	 */
	public void plot(String title, int freq, double simDur)
	{
		if(freq<0||freq>=y.size())
			return;
        try {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(title))));
        } catch (IOException ex) {    
            System.err.println("Error during file operations");
        }
		
		double max=Collections.max(y.get(freq));
		max = (int)(max*10)+1;
		max/=10.0;
		pw.println("new_plotter");
		pw.println("double double");
		pw.println("title");
		pw.println("Frequency "+title);
		pw.println("xlabel");
		pw.println("time");
		pw.println("ylabel");
		pw.println("Average SNR");
		pw.println("xunits");
		pw.println("tu");
		pw.println("yunits");
		pw.println("dB");
		pw.println("invisible -0.1 0.0");
		pw.println("invisible "+simDur+" "+max);
		pw.println("blue");
		
		for(int i=0;i<x.size();i++){
			pw.println("diamond "+x.get(i)+" "+y.get(freq).get(i));
		}
		
		for(int i=0;i<x.size()-1;i++){
			pw.println("line "+x.get(i)+" "+y.get(freq).get(i)+" "+x.get(i+1)+" "+y.get(freq).get(i+1));
		}
		
		pw.println("go");
		pw.close();
    }
	
	/**
	 * Plots the specified graph onto the same window. It can plot 40 different graphs 
	 * onto the same window. It plots each of the graphs with different colors and data
	 * point shapes. That is, no two different graphs will have the same color and same
	 * data point shapes.
	 * @param title		Title of the graphs
	 * @param freq		Graphs that will be plotted
	 * @param simDur	Duration of the simulation. i.e. max x value
	 * @param names		Names of the graphs
	 */
	public void plot(String title, ArrayList<Integer> freq, double simDur, ArrayList<String> names)
	{
		if(Collections.min(freq)<0||Collections.max(freq)>=y.size())
			return;
		if(freq.size()!=names.size())
			return;
        try {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(title))));
        } catch (IOException ex) {    
            System.err.println("Error during file operations");
        }
		
		double xmin=Collections.min(x);
		double min=Double.POSITIVE_INFINITY;
		for(int i=0;i<freq.size();i++){
			if(min>Collections.min(y.get(freq.get(i))))
				min=Collections.min(y.get(freq.get(i)));
		}
		double max=Double.NEGATIVE_INFINITY;
		for(int i=0;i<freq.size();i++){
			if(max<Collections.max(y.get(freq.get(i))))
				max=Collections.max(y.get(freq.get(i)));
		}
		max = (int)(max*10)+1;
		max/=10.0;
		pw.println("new_plotter");
		pw.println("double double");
		pw.println("title");
		pw.println(title);
		pw.println("xlabel");
		pw.println("time");
		pw.println("ylabel");
		pw.println("Average SNR");
		pw.println("xunits");
		pw.println("tu");
		pw.println("yunits");
		pw.println("dB");
		pw.println("invisible -0.1 0.0");
		pw.println("invisible "+(simDur+(simDur-xmin)*0.3)+" "+max);

		for(int j=0;j<freq.size();j++){
			pw.println(colors[j%10]);
			for(int i=0;i<x.size();i++){
				pw.println(shapes[j/10]+" "+x.get(i)+" "+y.get(freq.get(j)).get(i));
			}

			for(int i=0;i<x.size()-1;i++){
				pw.println("line "+x.get(i)+" "+y.get(freq.get(j)).get(i)+" "+x.get(i+1)+" "+y.get(freq.get(j)).get(i+1));
			}
		}
		legend(xmin, simDur, min, max, names);
		pw.println("go");
		pw.close();
		String[] argv = {"-t",title};
		jPlot.main(argv);
    }
	
	
	/**
	 * Prepares a legend for the graph with the given names
	 * @param xMin		Min x value of the graphs
	 * @param xMax		Max x value of the graphs
	 * @param yMin		Min y value of the graphs
	 * @param yMax		Max y value of the graphs
	 * @param names		Min x value of the graphs 
	 */
	private void legend(double xMin, double xMax, double yMin, double yMax, ArrayList<String> names)
	{
		double yInc = (yMax-yMin)/60.0;
		double xInc = (xMax-xMin)/20.0;
		for(int i=0;i<names.size();i++){
			pw.println(colors[i%10]);
			pw.println(shapes[i/10]+" "+(xMax+xInc)+" "+(yMin+yInc*(2*i+1)));
			pw.println(shapes[i/10]+" "+(xMax+xInc*2)+" "+(yMin+yInc*(2*i+1)));
			pw.println(shapes[i/10]+" "+(xMax+xInc*3)+" "+(yMin+yInc*(2*i+1)));
			pw.println("line "+(xMax+xInc)+" "+(yMin+yInc*(2*i+1))+" "+(xMax+xInc*3)+" "+(yMin+yInc*(2*i+1)));
			pw.println("rtext "+(xMax+xInc*3)+" "+(yMin+yInc*(2*i+1))+"\n"+names.get(i));
		}
	}
	
	/**
	 * Plots all available x versus y values onto different graphs on different windows
	 * @param simDur	Duration of the simulation. i.e. max x value
	 */
	public void plotAll(double simDur)
	{
		String[] argv = new String[y.size()+1];
		argv[0]="-t";
		for(int i=0;i<y.size();i++){
			argv[i+1] = String.format("f_%d", i);
			plot(argv[i+1], i,simDur);
		}
		jPlot.main(argv);
	}
	
}

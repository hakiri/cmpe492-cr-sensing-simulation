package Animation;

import jPlot.jPlot;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


/**
 * This class plots simple graphs. It can plot all graphs on to same window or
 * different windows. For plotting it uses jPlot.
 */
public class Plot {
	/**
	 * Available colors
	 */
	private static final String[] colors={ "green", "orange", "magenta", "red", "white", "yellow", "purple"
											,"pink", "blue"};
	/**
	 * Available data point shapes
	 */
	private static final String[] shapes={"diamond", "box", "x", "plus"};
	/**
	 * x values of the graphs
	 */
	private ArrayList<ArrayList<Double>> x;
	/**
	 * y values of the graphs
	 */
	private ArrayList<ArrayList<ArrayList<Double>>> y;
	/**
     * Writer for the files.
     */
    private static PrintWriter pw = null;
	
	/**
	 * Creates a new plotter object with no x value and y value.
	 * @param numberOfXs	Number of different x values
	 * @param yPerX			How many y belongs to each x
	 */
	public Plot(int numberOfXs, ArrayList<Integer> yPerX)
	{
		if(yPerX.isEmpty()){
			yPerX.add(1);
		}
		while(yPerX.size()<numberOfXs){
			yPerX.add(yPerX.get(yPerX.size()-1));
		}
		x = new ArrayList<ArrayList<Double>>();				//Create an empty array
		y = new ArrayList<ArrayList<ArrayList<Double>>>();		//Create an empty array of array for y values
		for(int i=0;i<numberOfXs;i++){
			x.add(new ArrayList<Double>());
			y.add(new ArrayList<ArrayList<Double>>());
			for(int j=0;j<yPerX.get(i);j++)
				y.get(i).add(new ArrayList<Double>());			//Then create arrays that will hold y values
		}
	}
	
	/**
	 * Adds an x value and its corresponding y values. This methods insert values
	 * in a way that will keep x values array always sorted
	 * @param xPos		Which x value collection the values belong
	 * @param xVal		x value to be added
	 * @param yVals		y values to be added
	 * @return			true if values added false otherwise.
	 */
	public boolean addPoint(int xPos, double xVal, ArrayList<Double> yVals)
	{
		if(yVals.size()!=y.get(xPos).size())
			return false;
		int index = Collections.binarySearch(x.get(xPos), xVal);
		if(index<0)
			index = -(index+1);
		x.get(xPos).add(index, xVal);						//Adds the x value
		for(int i=0;i<y.get(xPos).size();i++){		//Adds the y values one by one
			y.get(xPos).get(i).add(index, yVals.get(i));
		}
		return true;
	}
	
	/**
	 * Creates the given x versus the given y value file to be used to plot its
	 * graph. This method returns immediately if the y values is not valid.
	 * @param title		title of the graph
	 * @param xPos 		x values to be plotted
	 * @param yPos		y values to be plotted
	 */
	public void plot(String title, int xPos, int yPos)
	{
		if(yPos<0||yPos>=y.get(xPos).size())
			return;
        try {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(title))));
        } catch (IOException ex) {    
            System.err.println("Error during file operations");
        }
		
		double min=Collections.min(y.get(xPos).get(yPos));
		double max=Collections.max(y.get(xPos).get(yPos));
		double xMax=Collections.max(x.get(xPos));
		max = (int)(max*10)+1;
		max/=10.0;
		min = (int)(min*10);
		min/=10.0;
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
		pw.println("invisible -0.1 "+(min-0.1));
		pw.println("invisible "+xMax+" "+max);
		pw.println("green");
		
		for(int i=0;i<x.get(xPos).size();i++){
			pw.println("diamond "+x.get(xPos).get(i)+" "+y.get(xPos).get(yPos).get(i));
		}
		
		for(int i=0;i<x.get(xPos).size()-1;i++){
			pw.println("line "+x.get(xPos).get(i)+" "+y.get(xPos).get(yPos).get(i)+" "+x.get(xPos).get(i+1)+" "+y.get(xPos).get(yPos).get(i+1));
		}
		
		pw.println("gray20");
		for(double i=min;i<=max;){
			pw.println("line "+0+" "+i+" "+xMax+" "+i);
			i *= 10;
			i += 1;
			i /= 10;
		}
		
		pw.println("go");
		pw.close();
    }
	
	/**
	 * Plots the specified graphs onto the same window. It can plot 36 different graphs 
	 * onto the same window. It plots each of the graphs with different colors and data
	 * point shapes. That is, no two different graphs will have the same color and same
	 * data point shapes.
	 * @param title		Title of the graphs
	 * @param xs		x values to plotted
	 * @param ys		y values to plotted
	 * @param names		Names of the graphs
	 * @throws IndexOutOfBoundsException
	 */
	public void plot(String title, ArrayList<Integer> xs, ArrayList<ArrayList<Integer>> ys, ArrayList<ArrayList<String>> names)
			throws IndexOutOfBoundsException
	{
        try {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(title))));
        } catch (IOException ex) {    
            System.err.println("Error during file operations");
        }
		
		double temp;
		double xMin=Double.POSITIVE_INFINITY;
		for(int i=0;i<xs.size();i++){
			temp = Collections.min(x.get(xs.get(i)));
			if(xMin > temp)
				xMin = temp;
		}
		double xMax=Double.NEGATIVE_INFINITY;
		for(int i=0;i<xs.size();i++){
			temp = Collections.max(x.get(xs.get(i)));
			if(xMax < temp)
				xMax = temp;
		}
		
		double yMin=Double.POSITIVE_INFINITY;
		for(int i=0;i<xs.size();i++){
			for(int j=0;j<ys.get(i).size();j++){
				temp = Collections.min(y.get(xs.get(i)).get(ys.get(i).get(j)));
				if(yMin > temp)
					yMin = temp;
			}
		}
		double yMax=Double.NEGATIVE_INFINITY;
		for(int i=0;i<xs.size();i++){
			for(int j=0;j<ys.get(i).size();j++){
				temp = Collections.max(y.get(xs.get(i)).get(ys.get(i).get(j)));
				if(yMax < temp)
					yMax = temp;
			}
		}
		yMax = (int)(yMax*10)+1;
		yMax /= 10.0;
		yMin = (int)(yMin*10);
		yMin /= 10;
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
		pw.println("invisible -0.1 "+(yMin-0.1));
		pw.println("invisible "+(xMax+(xMax-xMin)*0.3)+" "+yMax);

		int graphCounter = 0;
		for(int i=0;i<xs.size();i++){
			for(int j=0;j<ys.get(i).size();j++){
				pw.println(colors[graphCounter%colors.length]);
				for(int q=0;q<x.get(xs.get(i)).size();q++){
					pw.println(shapes[graphCounter/colors.length]+" "+x.get(xs.get(i)).get(q)+" "+
							y.get(xs.get(i)).get(ys.get(i).get(j)).get(q));
				}

				for(int q=0;q<x.get(xs.get(i)).size()-1;q++){
					pw.println("line "+x.get(xs.get(i)).get(q)+" "+y.get(xs.get(i)).get(ys.get(i).get(j)).get(q)+" "+
							x.get(xs.get(i)).get(q+1)+" "+y.get(xs.get(i)).get(ys.get(i).get(j)).get(q+1));
				}
				graphCounter++;
			}
		}
		pw.println("gray20");
		double i=0;
		for(i=yMin;i<=yMax;){
			pw.println("line "+0+" "+i+" "+xMax+" "+i);
			i *= 10;
			i += 1;
			i /= 10;
		}
		legend(xMin, xMax, yMin, yMax, names);
		pw.println("go");
		pw.close();
    }
	
	/**
	 * Plots the specified graphs onto the same window. It can plot 36 different graphs 
	 * onto the same window. It plots each of the graphs with different colors and data
	 * point shapes. That is, no two different graphs will have the same color and same
	 * data point shapes.
	 * @param title		Title of the graphs
	 * @param xs		x values to plotted
	 * @param ys		y values to plotted
	 * @param names		Names of the graphs
	 * @param xMin		Minimum x value on the graph
	 * @param xMax		Maximum x value on the graph
	 * @throws IndexOutOfBoundsException
	 */
	public void plot(String title, ArrayList<Integer> xs, ArrayList<ArrayList<Integer>> ys, ArrayList<ArrayList<String>> names, double xMin, double xMax)
			throws IndexOutOfBoundsException
	{
        try {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(title))));
        } catch (IOException ex) {    
            System.err.println("Error during file operations");
        }
		
		double temp;
		
		double yMin=Double.POSITIVE_INFINITY;
		for(int i=0;i<xs.size();i++){
			for(int j=0;j<ys.get(i).size();j++){
				temp = Collections.min(y.get(xs.get(i)).get(ys.get(i).get(j)));
				if(yMin > temp)
					yMin = temp;
			}
		}
		double yMax=Double.NEGATIVE_INFINITY;
		for(int i=0;i<xs.size();i++){
			for(int j=0;j<ys.get(i).size();j++){
				temp = Collections.max(y.get(xs.get(i)).get(ys.get(i).get(j)));
				if(yMax < temp)
					yMax = temp;
			}
		}
		yMax = (int)(yMax*10)+1;
		yMax /= 10.0;
		yMin = (int)(yMin*10);
		yMin /= 10;
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
		pw.println("invisible -0.1 "+(yMin-0.1));
		pw.println("invisible "+(xMax+(xMax-xMin)*0.3)+" "+yMax);

		int graphCounter = 0;
		for(int i=0;i<xs.size();i++){
			for(int j=0;j<ys.get(i).size();j++){
				pw.println(colors[graphCounter%colors.length]);
				for(int q=0;q<x.get(xs.get(i)).size();q++){
					pw.println(shapes[graphCounter/colors.length]+" "+x.get(xs.get(i)).get(q)+" "+
							y.get(xs.get(i)).get(ys.get(i).get(j)).get(q));
				}

				for(int q=0;q<x.get(xs.get(i)).size()-1;q++){
					pw.println("line "+x.get(xs.get(i)).get(q)+" "+y.get(xs.get(i)).get(ys.get(i).get(j)).get(q)+" "+
							x.get(xs.get(i)).get(q+1)+" "+y.get(xs.get(i)).get(ys.get(i).get(j)).get(q+1));
				}
				graphCounter++;
			}
		}
		pw.println("gray20");
		double i=0;
		for(i=yMin;i<=yMax;){
			pw.println("line "+0+" "+i+" "+xMax+" "+i);
			i *= 10;
			i += 1;
			i /= 10;
		}
		legend(xMin, xMax, yMin, yMax, names);
		pw.println("go");
		pw.close();
    }
	
	
	/**
	 * Prepares a legend for the graph with the given names
	 * @param xMin		Min x value of the graphs
	 * @param xMax		Max x value of the graphs
	 * @param yMin		Min y value of the graphs
	 * @param yMax		Max y value of the graphs
	 * @param names		Names of the graphs
	 */
	private void legend(double xMin, double xMax, double yMin, double yMax, ArrayList<ArrayList<String>> names)
	{
		double yInc = (yMax-yMin)/60.0;
		double xInc = (xMax-xMin)/20.0;
		int graphCounter = 0;
		for(int i=0;i<names.size();i++){
			for(int j=0;j<names.get(i).size();j++){
				pw.println(colors[graphCounter%colors.length]);
				pw.println(shapes[graphCounter/colors.length]+" "+(xMax+xInc)+" "+(yMin+yInc*(2*graphCounter+1)));
				pw.println(shapes[graphCounter/colors.length]+" "+(xMax+xInc*2)+" "+(yMin+yInc*(2*graphCounter+1)));
				pw.println(shapes[graphCounter/colors.length]+" "+(xMax+xInc*3)+" "+(yMin+yInc*(2*graphCounter+1)));
				pw.println("line "+(xMax+xInc)+" "+(yMin+yInc*(2*graphCounter+1))+" "+(xMax+xInc*3)+" "+(yMin+yInc*(2*graphCounter+1)));
				pw.println("rtext "+(xMax+xInc*3)+" "+(yMin+yInc*(2*graphCounter+1))+"\n"+names.get(i).get(j));
				graphCounter++;
			}
		}
	}
	
	/**
	 * Plots all available x versus y values onto different graphs on different windows
	 */
	public void plotAll()
	{
		ArrayList<String> arg = new ArrayList<String>();
		
		arg.add("-t");
		int graphCounter = 0;
		for(int i=0;i<x.size();i++){
			for(int j=0;j<y.get(i).size();j++){
				arg.add(String.format(Locale.US,"f_%d", graphCounter));
				plot(arg.get(graphCounter+1), i, j);
				graphCounter++;
			}
		}
		String[] argv = new String[arg.size()];
		for(int i=0;i<argv.length;i++)
			argv[i] = arg.get(i);
		jPlot.main(argv);
	}
	
	/**
	 * Plots all available y values belongs to given x value onto different
	 * graphs on different windows
	 * @param xPos	x value to be plotted
	 */
	public void plotAllX(int xPos)
	{
		ArrayList<String> arg = new ArrayList<String>();
		arg.add("-t");
		int graphCounter = 0;
		for(int j=0;j<y.get(xPos).size();j++){
			arg.add(String.format(Locale.US,"f_%d", graphCounter));
			plot(arg.get(graphCounter+1), xPos, j);
			graphCounter++;
		}
		String[] argv = new String[arg.size()];
		for(int i=0;i<argv.length;i++)
			argv[i] = arg.get(i);
		jPlot.main(argv);
	}
	
	/**
	 * Plots all available y values belongs to given x value onto different
	 * graphs on different windows
	 * @param title Title of the graph
	 * @param xPos	x value to be plotted
	 * @param names Legend of plots
	 */
	public void plotAllXWithLegend(String title, int xPos, ArrayList<String> names)
	{
		int numberOfPlots = 1;
		
		String[] argv = new String[numberOfPlots];
		
		ArrayList<Integer> xs = new ArrayList<Integer>();
		xs.add(xPos);
		ArrayList<ArrayList<Integer>> ys = new ArrayList<ArrayList<Integer>>();
		ys.add(new ArrayList<Integer>());
		ArrayList<ArrayList<String>> nameList = new ArrayList<ArrayList<String>>();
		nameList.add(names);
		for(int j=0;j<y.get(xPos).size();j++){
			ys.get(0).add(j);
		}
		argv[0] = title;

		plot(argv[0], xs, ys, nameList);
		
		jPlot.main(argv);
	}
	
	/**
	 * This method plots corresponding y values of different x values on the same window.
	 * It plots all possible graphs.
	 * @param names		Names of the graphs that will be plotted on the same window
	 */
	public void plotAll(ArrayList<String> names)
	{
		int numberOfPlots = 0;
		for(int i=0;i<x.size();i++){
			int temp = y.get(i).size();
			if(temp > numberOfPlots)
				numberOfPlots = temp;
		}
		
		String[] argv = new String[numberOfPlots+1];
		argv[0] = "-t";
		
		for(int i=0;i<numberOfPlots;i++){
			ArrayList<Integer> xs = new ArrayList<Integer>();
			ArrayList<ArrayList<Integer>> ys = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<String>> nameList = new ArrayList<ArrayList<String>>();
			for(int j=0;j<x.size();j++){
				if(y.get(j).size()>i){
					xs.add(j);
					ys.add(new ArrayList<Integer>());
					ys.get(ys.size()-1).add(i);
					nameList.add(new ArrayList<String>());
					nameList.get(nameList.size()-1).add(names.get(j));
				}
			}
			argv[i+1] = String.format(Locale.US,"f_%d", i);
			
			plot(argv[i+1], xs, ys, nameList);
		}
		
		jPlot.main(argv);
	}

	/**
	 * Returns the accumulated y values.
	 * @return	y values
	 */
	public ArrayList<ArrayList<ArrayList<Double>>> getY() {
		return y;
	}
}
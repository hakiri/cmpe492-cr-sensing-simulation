/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Animation;

import java.util.ArrayList;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class PlotTest extends TestCase {
	
	public PlotTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(PlotTest.class);
		return suite;
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test of addPoint method, of class Plot.
	 */
	public void testAddPoint() {
		System.out.println("addPoint");
		int xPos = 0;
		double xVal = 0.0;
		ArrayList<Double> yVals = null;
		Plot instance = null;
		boolean expResult = false;
		boolean result = instance.addPoint(xPos, xVal, yVals);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of plot method, of class Plot.
	 */
	public void testPlot_3args() {
		System.out.println("plot");
		String title = "";
		int xPos = 0;
		int yPos = 0;
		Plot instance = null;
		instance.plot(title, xPos, yPos);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of plot method, of class Plot.
	 */
	public void testPlot_4args() {
		System.out.println("plot");
		String title = "";
		ArrayList<Integer> xs = null;
		ArrayList<ArrayList<Integer>> ys = null;
		ArrayList<ArrayList<String>> names = null;
		Plot instance = null;
		instance.plot(title, xs, ys, names);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of plotAll method, of class Plot.
	 */
	public void testPlotAll_0args() {
		System.out.println("plotAll");
		Plot instance = null;
		instance.plotAll();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of plotAllX method, of class Plot.
	 */
	public void testPlotAllX() {
		System.out.println("plotAllX");
		int xPos = 0;
		Plot instance = null;
		instance.plotAllX(xPos);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of plotAll method, of class Plot.
	 */
	public void testPlotAll_ArrayList() {
		System.out.println("plotAll");
		ArrayList<String> names = null;
		Plot instance = null;
		instance.plotAll(names);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}

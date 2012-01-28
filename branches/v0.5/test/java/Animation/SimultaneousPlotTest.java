/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Animation;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author acar
 */
public class SimultaneousPlotTest {
	
	public SimultaneousPlotTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of resynch method, of class SimultaneousPlot.
	 */
	@Test
	public void testResynch() {
		System.out.println("resynch");
		SimultaneousPlot instance = null;
		instance.resynch();
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of addPoint method, of class SimultaneousPlot.
	 */
	@Test
	public void testAddPoint() {
		System.out.println("addPoint");
		int xPos = 0;
		double xVal = 0.0;
		ArrayList<Double> yVals = null;
		SimultaneousPlot instance = null;
		//instance.addPoint(xPos, xVal, yVals);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of plotAllXWithLegend method, of class SimultaneousPlot.
	 */
	@Test
	public void testPlotAllXWithLegend() {
		System.out.println("plotAllXWithLegend");
		String title = "";
		int xPos = 0;
		ArrayList<String> names = null;
		double simulationDuration = 0.0;
		SimultaneousPlot instance = null;
		instance.plotAllXWithLegend(title, xPos, names, simulationDuration);
		// TODO review the generated test code and remove the default call to fail.
	}
}

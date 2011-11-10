/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Animation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class AnimationSuite extends TestCase {
	
	public AnimationSuite(String testName) {
		super(testName);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("AnimationSuite");
		suite.addTest(DrawCellTest.suite());
		suite.addTest(PointColorTest.suite());
		suite.addTest(SimulationStatsTableTest.suite());
		suite.addTest(PlotTest.suite());
		suite.addTest(DrawAreaTest.suite());
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
}

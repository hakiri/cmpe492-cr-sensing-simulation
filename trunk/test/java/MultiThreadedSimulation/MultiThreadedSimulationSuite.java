/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MultiThreadedSimulation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class MultiThreadedSimulationSuite extends TestCase {
	
	public MultiThreadedSimulationSuite(String testName) {
		super(testName);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("MultiThreadedSimulationSuite");
		suite.addTest(PrimaryTrafficGeneratorThreadTest.suite());
		suite.addTest(PrimaryTrafficGeneratorTest.suite());
		suite.addTest(CRSensorThreadTest.suite());
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DESSimulation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class DESSimulationSuite extends TestCase {
	
	public DESSimulationSuite(String testName) {
		super(testName);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("DESSimulationSuite");
		suite.addTest(DESPrimaryTrafficGeneratorTest.suite());
		suite.addTest(PrimaryTrafficGeneratorSimEntTest.suite());
		suite.addTest(CRDESSchedulerTest.suite());
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

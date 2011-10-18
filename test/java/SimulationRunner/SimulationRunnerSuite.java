/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimulationRunner;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class SimulationRunnerSuite extends TestCase {
	
	public SimulationRunnerSuite(String testName) {
		super(testName);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("SimulationRunnerSuite");
		suite.addTest(CellTest.suite());
		suite.addTest(CRNodeTest.suite());
		suite.addTest(SimulationRunnerTest.suite());
		suite.addTest(WirelessChannelTest.suite());
		suite.addTest(NodeTest.suite());
		suite.addTest(PrimaryTrafficGeneratorNodeTest.suite());
		suite.addTest(ParetoDistributionTest.suite());
		suite.addTest(CRBaseTest.suite());
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

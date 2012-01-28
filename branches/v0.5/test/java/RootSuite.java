/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import Animation.AnimationSuite;
import CommunicationEnvironment.CommunicationEnvironmentSuite;
import DESSimulation.DESSimulationSuite;
import MultiThreadedSimulation.MultiThreadedSimulationSuite;
import Nodes.NodesSuite;
import SimulationRunner.SimulationRunnerSuite;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class RootSuite extends TestCase {
	
	public RootSuite(String testName) {
		super(testName);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("RootSuite");
		suite.addTest(MultiThreadedSimulationSuite.suite());
		suite.addTest(DESSimulationSuite.suite());
		suite.addTest(NodesSuite.suite());
		suite.addTest(CommunicationEnvironmentSuite.suite());
		suite.addTest(SimulationRunnerSuite.suite());
		suite.addTest(AnimationSuite.suite());
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

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
public class SimulationRunnerTest extends TestCase {
	
	public SimulationRunnerTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(SimulationRunnerTest.class);
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
	 * Test of main method, of class SimulationRunner.
	 */
	public void testMain() {
		System.out.println("main");
		String[] args = null;
		SimulationRunner.main(args);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of startSimulation method, of class SimulationRunner.
	 */
	public void testStartSimulation() {
		System.out.println("startSimulation");
		SimulationRunner instance = new SimulationRunner();
		instance.startSimulation();
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of clear method, of class SimulationRunner.
	 */
	public void testClear() {
		System.out.println("clear");
		SimulationRunner.clear();
		// TODO review the generated test code and remove the default call to fail.
		
	}
}

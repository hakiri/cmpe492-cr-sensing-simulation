/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MultiThreadedSimulation;

import Nodes.PrimaryTrafficGeneratorNode;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class PrimaryTrafficGeneratorTest extends TestCase {
	
	public PrimaryTrafficGeneratorTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(PrimaryTrafficGeneratorTest.class);
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
	 * Test of registerNode method, of class PrimaryTrafficGenerator.
	 */
	public void testRegisterNode() {
		System.out.println("registerNode");
		PrimaryTrafficGeneratorNode n = null;
		PrimaryTrafficGenerator instance = null;
		instance.registerNode(n);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of terminateAllThreads method, of class PrimaryTrafficGenerator.
	 */
	public void testTerminateAllThreads() {
		System.out.println("terminateAllThreads");
		PrimaryTrafficGenerator instance = null;
		instance.terminateAllThreads();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of logStats method, of class PrimaryTrafficGenerator.
	 */
	public void testLogStats() {
		System.out.println("logStats");
		PrimaryTrafficGenerator instance = null;
		String[][] expResult = null;
		String[][] result = instance.logStats();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DESSimulation;

import Nodes.PrimaryTrafficGeneratorNode;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class DESPrimaryTrafficGeneratorTest extends TestCase {
	
	public DESPrimaryTrafficGeneratorTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(DESPrimaryTrafficGeneratorTest.class);
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
	 * Test of registerNode method, of class DESPrimaryTrafficGenerator.
	 */
	public void testRegisterNode() {
		System.out.println("registerNode");
		PrimaryTrafficGeneratorNode n = null;
		DESPrimaryTrafficGenerator instance = null;
		instance.registerNode(n);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of start method, of class DESPrimaryTrafficGenerator.
	 */
	public void testStart() {
		System.out.println("start");
		DESPrimaryTrafficGenerator instance = null;
		instance.start();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of logStats method, of class DESPrimaryTrafficGenerator.
	 */
	public void testLogStats() {
		System.out.println("logStats");
		DESPrimaryTrafficGenerator instance = null;
		String[][] expResult = null;
		String[][] result = instance.logStats();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}

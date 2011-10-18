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
public class PrimaryTrafficGeneratorThreadTest extends TestCase {
	
	public PrimaryTrafficGeneratorThreadTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(PrimaryTrafficGeneratorThreadTest.class);
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
	 * Test of run method, of class PrimaryTrafficGeneratorThread.
	 */
	public void testRun() {
		System.out.println("run");
		PrimaryTrafficGeneratorThread instance = null;
		instance.run();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of isFinished method, of class PrimaryTrafficGeneratorThread.
	 */
	public void testIsFinished() {
		System.out.println("isFinished");
		PrimaryTrafficGeneratorThread instance = null;
		boolean expResult = false;
		boolean result = instance.isFinished();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of terminate method, of class PrimaryTrafficGeneratorThread.
	 */
	public void testTerminate() {
		System.out.println("terminate");
		PrimaryTrafficGeneratorThread instance = null;
		instance.terminate();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}

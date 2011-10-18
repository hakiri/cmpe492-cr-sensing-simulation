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
public class PrimaryTrafficGeneratorNodeTest extends TestCase {
	
	public PrimaryTrafficGeneratorNodeTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(PrimaryTrafficGeneratorNodeTest.class);
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
	 * Test of setRandomPosition method, of class PrimaryTrafficGeneratorNode.
	 */
	public void testSetRandomPosition() {
		System.out.println("setRandomPosition");
		double offDuration = 0.0;
		PrimaryTrafficGeneratorNode instance = null;
		instance.setRandomPosition(offDuration);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of generateTraffic method, of class PrimaryTrafficGeneratorNode.
	 */
	public void testGenerateTraffic() {
		System.out.println("generateTraffic");
		double offDuration = 0.0;
		PrimaryTrafficGeneratorNode instance = null;
		int expResult = 0;
		int result = instance.generateTraffic(offDuration);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getCommunicationFreq method, of class PrimaryTrafficGeneratorNode.
	 */
	public void testGetCommunicationFreq() {
		System.out.println("getCommunicationFreq");
		PrimaryTrafficGeneratorNode instance = null;
		int expResult = 0;
		int result = instance.getCommunicationFreq();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getNumberOfCallAttempts method, of class PrimaryTrafficGeneratorNode.
	 */
	public void testGetNumberOfCallAttempts() {
		System.out.println("getNumberOfCallAttempts");
		PrimaryTrafficGeneratorNode instance = null;
		int expResult = 0;
		int result = instance.getNumberOfCallAttempts();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getNumberOfDrops method, of class PrimaryTrafficGeneratorNode.
	 */
	public void testGetNumberOfDrops() {
		System.out.println("getNumberOfDrops");
		PrimaryTrafficGeneratorNode instance = null;
		int expResult = 0;
		int result = instance.getNumberOfDrops();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getComunicationDuration method, of class PrimaryTrafficGeneratorNode.
	 */
	public void testGetComunicationDuration() {
		System.out.println("getComunicationDuration");
		PrimaryTrafficGeneratorNode instance = null;
		double expResult = 0.0;
		double result = instance.getComunicationDuration();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of incrementTotalCommunicationDuration method, of class PrimaryTrafficGeneratorNode.
	 */
	public void testIncrementTotalCommunicationDuration() {
		System.out.println("incrementTotalCommunicationDuration");
		double commDur = 0.0;
		PrimaryTrafficGeneratorNode instance = null;
		instance.incrementTotalCommunicationDuration(commDur);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setRoutingRadius method, of class PrimaryTrafficGeneratorNode.
	 */
	public void testSetRoutingRadius() {
		System.out.println("setRoutingRadius");
		double routingRadius = 0.0;
		PrimaryTrafficGeneratorNode instance = null;
		instance.setRoutingRadius(routingRadius);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getRoutingRadius method, of class PrimaryTrafficGeneratorNode.
	 */
	public void testGetRoutingRadius() {
		System.out.println("getRoutingRadius");
		PrimaryTrafficGeneratorNode instance = null;
		double expResult = 0.0;
		double result = instance.getRoutingRadius();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}

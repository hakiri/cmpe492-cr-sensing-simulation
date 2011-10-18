/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DESSimulation;

import DES.Event;
import DES.Scheduler.EventHandle;
import DES.SimEnt;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class CRDESSchedulerTest extends TestCase {
	
	public CRDESSchedulerTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(CRDESSchedulerTest.class);
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
	 * Test of start method, of class CRDESScheduler.
	 */
	public void testStart() {
		System.out.println("start");
		CRDESScheduler instance = null;
		instance.start();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of recv method, of class CRDESScheduler.
	 */
	public void testRecv() {
		System.out.println("recv");
		SimEnt src = null;
		Event ev = null;
		CRDESScheduler instance = null;
		instance.recv(src, ev);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of destructor method, of class CRDESScheduler.
	 */
	public void testDestructor() {
		System.out.println("destructor");
		CRDESScheduler instance = null;
		instance.destructor();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of terminate method, of class CRDESScheduler.
	 */
	public void testTerminate() {
		System.out.println("terminate");
		CRDESScheduler instance = null;
		instance.terminate();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of isFinished method, of class CRDESScheduler.
	 */
	public void testIsFinished() {
		System.out.println("isFinished");
		CRDESScheduler instance = null;
		boolean expResult = false;
		boolean result = instance.isFinished();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of deliveryAck method, of class CRDESScheduler.
	 */
	public void testDeliveryAck() {
		System.out.println("deliveryAck");
		EventHandle h = null;
		CRDESScheduler instance = null;
		instance.deliveryAck(h);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getRemainingSimulationDuration method, of class CRDESScheduler.
	 */
	public void testGetRemainingSimulationDuration() {
		System.out.println("getRemainingSimulationDuration");
		CRDESScheduler instance = null;
		double expResult = 0.0;
		double result = instance.getRemainingSimulationDuration();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getSimulationDuration method, of class CRDESScheduler.
	 */
	public void testGetSimulationDuration() {
		System.out.println("getSimulationDuration");
		CRDESScheduler instance = null;
		double expResult = 0.0;
		double result = instance.getSimulationDuration();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}

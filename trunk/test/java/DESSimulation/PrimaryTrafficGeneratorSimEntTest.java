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
public class PrimaryTrafficGeneratorSimEntTest extends TestCase {
	
	public PrimaryTrafficGeneratorSimEntTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(PrimaryTrafficGeneratorSimEntTest.class);
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
	 * Test of start method, of class PrimaryTrafficGeneratorSimEnt.
	 */
	public void testStart() {
		System.out.println("start");
		PrimaryTrafficGeneratorSimEnt instance = null;
		instance.start();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of recv method, of class PrimaryTrafficGeneratorSimEnt.
	 */
	public void testRecv() {
		System.out.println("recv");
		SimEnt src = null;
		Event ev = null;
		PrimaryTrafficGeneratorSimEnt instance = null;
		instance.recv(src, ev);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of deliveryAck method, of class PrimaryTrafficGeneratorSimEnt.
	 */
	public void testDeliveryAck() {
		System.out.println("deliveryAck");
		EventHandle h = null;
		PrimaryTrafficGeneratorSimEnt instance = null;
		instance.deliveryAck(h);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}

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
public class CRSensorThreadTest extends TestCase {
	
	public CRSensorThreadTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(CRSensorThreadTest.class);
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
	 * Test of run method, of class CRSensorThread.
	 */
	public void testRun() {
		System.out.println("run");
		CRSensorThread instance = null;
		instance.run();
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of isFinished method, of class CRSensorThread.
	 */
	public void testIsFinished() {
		System.out.println("isFinished");
		CRSensorThread instance = null;
		boolean expResult = false;
		boolean result = instance.isFinished();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of terminate method, of class CRSensorThread.
	 */
	public void testTerminate() {
		System.out.println("terminate");
		CRSensorThread instance = null;
		instance.terminate();
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of getRemainingSimulationDuration method, of class CRSensorThread.
	 */
	public void testGetRemainingSimulationDuration() {
		System.out.println("getRemainingSimulationDuration");
		CRSensorThread instance = null;
		double expResult = 0.0;
		double result = instance.getRemainingSimulationDuration();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of getSimulationDuration method, of class CRSensorThread.
	 */
	public void testGetSimulationDuration() {
		System.out.println("getSimulationDuration");
		CRSensorThread instance = null;
		double expResult = 0.0;
		double result = instance.getSimulationDuration();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of getUnitTime method, of class CRSensorThread.
	 */
	public void testGetUnitTime() {
		System.out.println("getUnitTime");
		CRSensorThread instance = null;
		double expResult = 0.0;
		double result = instance.getUnitTime();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of getFrameDuration method, of class CRSensorThread.
	 */
	public void testGetFrameDuration() {
		System.out.println("getFrameDuration");
		CRSensorThread instance = null;
		double expResult = 0.0;
		double result = instance.getFrameDuration();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of setCommunationDuration method, of class CRSensorThread.
	 */
	public void testSetCommunationDuration() {
		System.out.println("setCommunationDuration");
		int crnode_id = 0;
		CRSensorThread instance = null;
		instance.setCommunationDuration(crnode_id);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of setInactiveDuration method, of class CRSensorThread.
	 */
	public void testSetInactiveDuration() {
		System.out.println("setInactiveDuration");
		int crnode_id = 0;
		boolean dropped = false;
		CRSensorThread instance = null;
		instance.setInactiveDuration(crnode_id, dropped);
		// TODO review the generated test code and remove the default call to fail.
		
	}
}

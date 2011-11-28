/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Nodes;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class CRBaseTest extends TestCase {
	
	public CRBaseTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(CRBaseTest.class);
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
	 * Test of deploy_freq method, of class CRBase.
	 */
	public void testDeploy_freq() {
		System.out.println("deploy_freq");
		boolean startFromFirst = false;
		CRBase instance = null;
		ArrayList expResult = null;
		ArrayList result = instance.deploy_freq(startFromFirst);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of assignFrequencies method, of class CRBase.
	 */
	public void testAssignFrequencies() {
		System.out.println("assignFrequencies");
		CRBase instance = null;
		instance.assignFrequencies();
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of communicationScheduleAdvertiser method, of class CRBase.
	 */
	public void testCommunicationScheduleAdvertiser() {
		System.out.println("communicationScheduleAdvertiser");
		CRBase instance = null;
		instance.communicationScheduleAdvertiser();
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of setLast_averageSnr method, of class CRBase.
	 */
	public void testSetLast_averageSnr() {
		System.out.println("setLast_averageSnr");
		ArrayList<ArrayList<Double>> current_averageSnr = null;
		CRBase instance = null;
		//instance.setLast_averageSnr(current_averageSnr);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of getFrequency_list method, of class CRBase.
	 */
	public void testGetFrequency_list() {
		System.out.println("getFrequency_list");
		CRBase instance = null;
		ArrayList expResult = null;
		ArrayList result = instance.getFrequency_list();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of registerZone method, of class CRBase.
	 */
	public void testRegisterZone() {
		System.out.println("registerZone");
		int sector = 0;
		int alpha = 0;
		int d = 0;
		int crnodes = 0;
		CRBase instance = null;
		instance.registerZone(sector, alpha, d, crnodes);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of deployNodeinZone method, of class CRBase.
	 */
	public void testDeployNodeinZone() {
		System.out.println("deployNodeinZone");
		int id = 0;
		CRBase instance = null;
		Double expResult = null;
		Point2D.Double result = instance.deployNodeinZone(id);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of findZone method, of class CRBase.
	 */
	public void testFindZone() {
		System.out.println("findZone");
		int id = 0;
		CRBase instance = null;
		int expResult = 0;
		int result = instance.findZone(id);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of farthestZoneDistance method, of class CRBase.
	 */
	public void testFarthestZoneDistance() {
		System.out.println("farthestZoneDistance");
		CRBase instance = null;
		double expResult = 0.0;
		double result = instance.farthestZoneDistance();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		
	}
}

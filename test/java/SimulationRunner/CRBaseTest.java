package SimulationRunner;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import Nodes.CRBase;

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
		CRBase instance = new CRBase(new Point2D.Double(0.0, 0.0), 0, 4);
		ArrayList expResult = null;
		ArrayList result = instance.deploy_freq(true);
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
		instance.setLast_averageSnr(current_averageSnr);
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
}

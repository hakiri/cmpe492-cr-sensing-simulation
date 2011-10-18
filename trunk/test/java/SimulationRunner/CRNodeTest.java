/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimulationRunner;

import java.util.ArrayList;
import java.util.HashMap;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class CRNodeTest extends TestCase {
	
	public CRNodeTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(CRNodeTest.class);
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
	 * Test of sense method, of class CRNode.
	 */
	public void testSense() {
		System.out.println("sense");
		int freq = 0;
		CRNode instance = null;
		instance.sense(freq);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getSnrValues method, of class CRNode.
	 */
	public void testGetSnrValues() {
		System.out.println("getSnrValues");
		CRNode instance = null;
		HashMap expResult = null;
		HashMap result = instance.getSnrValues();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of initializeAverageSnr method, of class CRNode.
	 */
	public void testInitializeAverageSnr() {
		System.out.println("initializeAverageSnr");
		int total_number_of_frequencies = 0;
		CRNode.initializeAverageSnr(total_number_of_frequencies);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of logSnrValues method, of class CRNode.
	 */
	public void testLogSnrValues() {
		System.out.println("logSnrValues");
		CRNode instance = null;
		instance.logSnrValues();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of logAverageSnr method, of class CRNode.
	 */
	public void testLogAverageSnr() {
		System.out.println("logAverageSnr");
		double time = 0.0;
		CRNode.logAverageSnr(time);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of createLogFile method, of class CRNode.
	 */
	public void testCreateLogFile() {
		System.out.println("createLogFile");
		String file_name = "";
		CRNode.createLogFile(file_name);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of writeLogFile method, of class CRNode.
	 */
	public void testWriteLogFile() {
		System.out.println("writeLogFile");
		String log_string = "";
		CRNode.writeLogFile(log_string);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of closeLogFile method, of class CRNode.
	 */
	public void testCloseLogFile() {
		System.out.println("closeLogFile");
		CRNode.closeLogFile();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setFrequencyList method, of class CRNode.
	 */
	public void testSetFrequencyList() {
		System.out.println("setFrequencyList");
		ArrayList<Integer> frequencies = null;
		CRNode instance = null;
		instance.setFrequencyList(frequencies);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setCommunication_frequency method, of class CRNode.
	 */
	public void testSetCommunication_frequency() {
		System.out.println("setCommunication_frequency");
		int communication_frequency = 0;
		CRNode instance = null;
		instance.setCommunication_frequency(communication_frequency);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of communicate method, of class CRNode.
	 */
	public void testCommunicate() {
		System.out.println("communicate");
		double time = 0.0;
		boolean lastReport = false;
		CRNode.communicate(time, lastReport);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setTotalNumberOfFrames method, of class CRNode.
	 */
	public void testSetTotalNumberOfFrames() {
		System.out.println("setTotalNumberOfFrames");
		int totalNumberOfFrames = 0;
		CRNode.setTotalNumberOfFrames(totalNumberOfFrames);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getTotalNumberOfFrames method, of class CRNode.
	 */
	public void testGetTotalNumberOfFrames() {
		System.out.println("getTotalNumberOfFrames");
		int expResult = 0;
		int result = CRNode.getTotalNumberOfFrames();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of logStats method, of class CRNode.
	 */
	public void testLogStats() {
		System.out.println("logStats");
		String[][] expResult = null;
		String[][] result = CRNode.logStats();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Nodes;

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
		
	}

	/**
	 * Test of fuseSensingResults method, of class CRNode.
	 */
	public void testLogAverageSnr() {
		System.out.println("logAverageSnr");
		double time = 0.0;
		CRNode.fuseSensingResults(time);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of createLogFile method, of class CRNode.
	 */
	public void testCreateLogFile() {
		System.out.println("createLogFile");
		String file_name = "";
		CRNode.createLogFile(file_name);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of writeLogFile method, of class CRNode.
	 */
	public void testWriteLogFile() {
		System.out.println("writeLogFile");
		String log_string = "";
		CRNode.writeLogFile(log_string);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of closeLogFile method, of class CRNode.
	 */
	public void testCloseLogFile() {
		System.out.println("closeLogFile");
		CRNode.closeLogFile();
		// TODO review the generated test code and remove the default call to fail.
		
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
		
	}

	/**
	 * Test of getCommunication_frequency method, of class CRNode.
	 */
	public void testGetCommunication_frequency() {
		System.out.println("getCommunication_frequency");
		CRNode instance = null;
		int expResult = 0;
		int result = instance.getCommunication_frequency();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of releaseCommunication_frequency method, of class CRNode.
	 */
	public void testReleaseCommunication_frequency() {
		System.out.println("releaseCommunication_frequency");
		CRNode instance = null;
		instance.releaseCommunication_frequency();
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of communicate method, of class CRNode.
	 */
	public void testCommunicate() {
		System.out.println("communicate");
		double time = 0.0;
		boolean lastReport = false;
        boolean isReg = false;
		CRNode.communicate(time,isReg ,lastReport);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of setTotalNumberOfFrames method, of class CRNode.
	 */
	public void testSetTotalNumberOfFrames() {
		System.out.println("setTotalNumberOfFrames");
		int totalNumberOfFrames = 0;
		CRNode.setTotalNumberOfFrames(totalNumberOfFrames);
		// TODO review the generated test code and remove the default call to fail.
		
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
		
	}

	/**
	 * Test of nextOnDurationDES method, of class CRNode.
	 */
	public void testNextOnDurationDES() {
		System.out.println("nextOnDurationDES");
		double frameDuration = 0.0;
		CRNode instance = null;
		double expResult = 0.0;
		double result = instance.nextOnDurationDES(frameDuration);
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of nextOffDurationDES method, of class CRNode.
	 */
	public void testNextOffDurationDES() {
		System.out.println("nextOffDurationDES");
		double frameDuration = 0.0;
		CRNode instance = null;
		double expResult = 0.0;
		double result = instance.nextOffDurationDES(frameDuration);
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of nextOnDuration method, of class CRNode.
	 */
	public void testNextOnDuration() {
		System.out.println("nextOnDuration");
		double frameDuration = 0.0;
		CRNode instance = null;
		int expResult = 0;
		int result = instance.nextOnDuration(frameDuration);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of nextOffDuration method, of class CRNode.
	 */
	public void testNextOffDuration() {
		System.out.println("nextOffDuration");
		double frameDuration = 0.0;
		CRNode instance = null;
		int expResult = 0;
		int result = instance.nextOffDuration(frameDuration);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of getCommOrNot method, of class CRNode.
	 */
	public void testGetCommOrNot() {
		System.out.println("getCommOrNot");
		CRNode instance = null;
		boolean expResult = false;
		boolean result = instance.getCommOrNot();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of setReadytoComm method, of class CRNode.
	 */
	public void testSetReadytoComm() {
		System.out.println("setReadytoComm");
		boolean readytoComm = false;
		CRNode instance = null;
		instance.setReadytoComm(readytoComm);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of getReadytoComm method, of class CRNode.
	 */
	public void testGetReadytoComm() {
		System.out.println("getReadytoComm");
		CRNode instance = null;
		boolean expResult = false;
		boolean result = instance.getReadytoComm();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of getIsCollided method, of class CRNode.
	 */
	public void testGetIsCollided() {
		System.out.println("getIsCollided");
		CRNode instance = null;
		boolean expResult = false;
		boolean result = instance.getIsCollided();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of setIsCollided method, of class CRNode.
	 */
	public void testSetIsCollided() {
		System.out.println("setIsCollided");
		boolean iscollided = false;
		CRNode instance = null;
		instance.setIsCollided(iscollided);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of getNumberOfForcedHandoff method, of class CRNode.
	 */
	public void testGetNumberOfForcedHandoff() {
		System.out.println("getNumberOfForcedHandoff");
		CRNode instance = null;
		int expResult = 0;
		int result = instance.getNumberOfForcedHandoff();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of setNumberOfForcedHandoff method, of class CRNode.
	 */
	public void testSetNumberOfForcedHandoff() {
		System.out.println("setNumberOfForcedHandoff");
		int numberOfForcedHandoff = 0;
		CRNode instance = null;
		instance.setNumberOfForcedHandoff(numberOfForcedHandoff);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of getNumberOfDrops method, of class CRNode.
	 */
	public void testGetNumberOfDrops() {
		System.out.println("getNumberOfDrops");
		CRNode instance = null;
		int expResult = 0;
		int result = instance.getNumberOfDrops();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of setNumberOfDrops method, of class CRNode.
	 */
	public void testSetNumberOfDrops() {
		System.out.println("setNumberOfDrops");
		int numberOfDrops = 0;
		CRNode instance = null;
		instance.setNumberOfDrops(numberOfDrops);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of getNumberOfBlocks method, of class CRNode.
	 */
	public void testGetNumberOfBlocks() {
		System.out.println("getNumberOfBlocks");
		CRNode instance = null;
		int expResult = 0;
		int result = instance.getNumberOfBlocks();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of setNumberOfBlocks method, of class CRNode.
	 */
	public void testSetNumberOfBlocks() {
		System.out.println("setNumberOfBlocks");
		int numberOfBlocks = 0;
		CRNode instance = null;
		instance.setNumberOfBlocks(numberOfBlocks);
		// TODO review the generated test code and remove the default call to fail.
		
	}
}

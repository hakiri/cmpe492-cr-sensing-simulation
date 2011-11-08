/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimulationRunner;

import java.awt.geom.Point2D.Double;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import Nodes.Node;

/**
 *
 * @author acar
 */
public class NodeTest extends TestCase {
	
	public NodeTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(NodeTest.class);
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
	 * Test of getPosition method, of class Node.
	 */
	public void testGetPosition() {
		System.out.println("getPosition");
		Node instance = new NodeImpl();
		Double expResult = null;
		Double result = instance.getPosition();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getVelocity method, of class Node.
	 */
	public void testGetVelocity() {
		System.out.println("getVelocity");
		Node instance = new NodeImpl();
		double expResult = 0.0;
		double result = instance.getVelocity();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setPosition method, of class Node.
	 */
	public void testSetPosition() {
		System.out.println("setPosition");
		Double position = null;
		Node instance = new NodeImpl();
		instance.setPosition(position);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setVelocity method, of class Node.
	 */
	public void testSetVelocity() {
		System.out.println("setVelocity");
		double velocity = 0.0;
		Node instance = new NodeImpl();
		instance.setVelocity(velocity);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setId method, of class Node.
	 */
	public void testSetId() {
		System.out.println("setId");
		int id = 0;
		Node instance = new NodeImpl();
		instance.setId(id);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getId method, of class Node.
	 */
	public void testGetId() {
		System.out.println("getId");
		Node instance = new NodeImpl();
		int expResult = 0;
		int result = instance.getId();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	public class NodeImpl extends Node {
	}
}

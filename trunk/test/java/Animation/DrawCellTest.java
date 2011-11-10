/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Animation;

import Nodes.Node;
import java.awt.Color;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class DrawCellTest extends TestCase {
	
	public DrawCellTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(DrawCellTest.class);
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
	 * Test of run method, of class DrawCell.
	 */
	public void testRun() {
		System.out.println("run");
		DrawCell instance = null;
		instance.run();
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of draw method, of class DrawCell.
	 */
	public void testDraw() {
		System.out.println("draw");
		DrawCell instance = null;
		instance.draw();
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of paintPrimaryNode method, of class DrawCell.
	 */
	public void testPaintPrimaryNode() {
		System.out.println("paintPrimaryNode");
		Node n = null;
		Color c = null;
		DrawCell.paintPrimaryNode(n, c);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of paintCrNode method, of class DrawCell.
	 */
	public void testPaintCrNode() {
		System.out.println("paintCrNode");
		Node n = null;
		Color c = null;
		DrawCell.paintCrNode(n, c);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of isFinished method, of class DrawCell.
	 */
	public void testIsFinished() {
		System.out.println("isFinished");
		DrawCell instance = null;
		boolean expResult = false;
		boolean result = instance.isFinished();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of terminate method, of class DrawCell.
	 */
	public void testTerminate() {
		System.out.println("terminate");
		DrawCell instance = null;
		instance.terminate();
		// TODO review the generated test code and remove the default call to fail.
	}
}

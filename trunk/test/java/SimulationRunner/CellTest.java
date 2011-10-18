/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimulationRunner;

import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class CellTest extends TestCase {
	
	public CellTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(CellTest.class);
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
	 * Test of deployNodeinCell method, of class Cell.
	 */
	public void testDeployNodeinCell() {
		System.out.println("deployNodeinCell");
		Double expResult = null;
		Double result = Cell.deployNodeinCell();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of deployNodeinZone method, of class Cell.
	 */
	public void testDeployNodeinZone() {
		System.out.println("deployNodeinZone");
		int sector_number = 0;
		int angle_number = 0;
		int distance_number = 0;
		Double expResult = null;
		Double result = Cell.deployNodeinZone(sector_number, angle_number, distance_number);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of deployNodeInRouteCircle method, of class Cell.
	 */
	public void testDeployNodeInRouteCircle() {
		System.out.println("deployNodeInRouteCircle");
		Node node = null;
		double routeRadius = 0.0;
		Double expResult = null;
		Double result = Cell.deployNodeInRouteCircle(node, routeRadius);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setBaseStation method, of class Cell.
	 */
	public void testSetBaseStation() {
		System.out.println("setBaseStation");
		CRBase baseStation = null;
		Cell.setBaseStation(baseStation);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getBaseStation method, of class Cell.
	 */
	public void testGetBaseStation() {
		System.out.println("getBaseStation");
		CRBase expResult = null;
		CRBase result = Cell.getBaseStation();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setPosition method, of class Cell.
	 */
	public void testSetPosition() {
		System.out.println("setPosition");
		Double position = null;
		Cell instance = null;
		instance.setPosition(position);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getPosition method, of class Cell.
	 */
	public void testGetPosition() {
		System.out.println("getPosition");
		Cell instance = null;
		Double expResult = null;
		Double result = instance.getPosition();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setAlpha method, of class Cell.
	 */
	public void testSetAlpha() {
		System.out.println("setAlpha");
		int alpha = 0;
		Cell.setAlpha(alpha);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getAlpha method, of class Cell.
	 */
	public void testGetAlpha() {
		System.out.println("getAlpha");
		int expResult = 0;
		int result = Cell.getAlpha();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setNumber_of_sectors method, of class Cell.
	 */
	public void testSetNumber_of_sectors() {
		System.out.println("setNumber_of_sectors");
		int number_of_sectors = 0;
		Cell.setNumber_of_sectors(number_of_sectors);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getNumber_of_sectors method, of class Cell.
	 */
	public void testGetNumber_of_sectors() {
		System.out.println("getNumber_of_sectors");
		int expResult = 0;
		int result = Cell.getNumber_of_sectors();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setRadius method, of class Cell.
	 */
	public void testSetRadius() {
		System.out.println("setRadius");
		double radius = 0.0;
		Cell.setRadius(radius);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getRadius method, of class Cell.
	 */
	public void testGetRadius() {
		System.out.println("getRadius");
		double expResult = 0.0;
		double result = Cell.getRadius();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setSet_of_d method, of class Cell.
	 */
	public void testSetSet_of_d() {
		System.out.println("setSet_of_d");
		ArrayList<java.lang.Double> set_of_d = null;
		Cell.setSet_of_d(set_of_d);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getSet_of_d method, of class Cell.
	 */
	public void testGetSet_of_d() {
		System.out.println("getSet_of_d");
		ArrayList expResult = null;
		ArrayList result = Cell.getSet_of_d();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}

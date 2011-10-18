/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Animation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class PointColorTest extends TestCase {
	
	public PointColorTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(PointColorTest.class);
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
	 * Test of convertCoordinate method, of class PointColor.
	 */
	public void testConvertCoordinate() {
		System.out.println("convertCoordinate");
		int cellRadius = 0;
		PointColor instance = null;
		PointColor expResult = null;
		PointColor result = instance.convertCoordinate(cellRadius);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}

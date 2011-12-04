/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Animation;

import java.awt.Color;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class DrawAreaTest extends TestCase {
	
	public DrawAreaTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(DrawAreaTest.class);
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
	 * Test of paintPrimary method, of class DrawArea.
	 */
	public void testPaintPrimary() {
		System.out.println("paintPrimary");
		Integer id = 0;
		PointColor p = new PointColor(0, 0, 8, Color.red);
		DrawArea instance = new DrawArea(15, 3, 4, 3, 5, 10);
		instance.paintPrimary(id, p);
		PointColor p2 = instance.getPrimaryNodes().get(id);
		assertEquals(p, p2);
	}

	/**
	 * Test of paintCR method, of class DrawArea.
	 */
	public void testPaintCR() {
		System.out.println("paintCR");
		Integer id = 1;
		PointColor p = new PointColor(3, 5, 8, Color.GREEN);
		DrawArea instance = new DrawArea(15, 3, 4, 3, 5, 10);
		instance.paintCR(id, p);
		assertEquals(p, instance.getCrNodes().get(id));
	}
}

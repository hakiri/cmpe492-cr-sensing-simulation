/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Animation;

import java.awt.Graphics;
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
	 * Test of paintComponent method, of class DrawArea.
	 */
	public void testPaintComponent() {
		System.out.println("paintComponent");
		Graphics g = null;
		DrawArea instance = null;
		//instance.paintComponent(g);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of paint method, of class DrawArea.
	 */
	public void testPaint() {
		System.out.println("paint");
		Graphics g = null;
		DrawArea instance = null;
		//instance.paint(g);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of paintPrimary method, of class DrawArea.
	 */
	public void testPaintPrimary() {
		System.out.println("paintPrimary");
		Integer id = null;
		PointColor p = null;
		DrawArea instance = null;
		//instance.paintPrimary(id, p);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of paintCR method, of class DrawArea.
	 */
	public void testPaintCR() {
		System.out.println("paintCR");
		Integer id = null;
		PointColor p = null;
		DrawArea instance = null;
		//instance.paintCR(id, p);
		// TODO review the generated test code and remove the default call to fail.
	}
}

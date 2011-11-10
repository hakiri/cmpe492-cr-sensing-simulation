/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimulationRunner;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class ParetoDistributionTest extends TestCase {
	
	public ParetoDistributionTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(ParetoDistributionTest.class);
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
	 * Test of cdf method, of class ParetoDistribution.
	 */
	public void testCdf() {
		System.out.println("cdf");
		double x = 0.0;
		ParetoDistribution instance = null;
		double expResult = 0.0;
		double result = instance.cdf(x);
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of nextDouble method, of class ParetoDistribution.
	 */
	public void testNextDouble() {
		System.out.println("nextDouble");
		ParetoDistribution instance = null;
		double expResult = 0.0;
		double result = instance.nextDouble();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of pdf method, of class ParetoDistribution.
	 */
	public void testPdf() {
		System.out.println("pdf");
		double x = 0.0;
		ParetoDistribution instance = null;
		double expResult = 0.0;
		double result = instance.pdf(x);
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of setMinValue method, of class ParetoDistribution.
	 */
	public void testSetMinValue() {
		System.out.println("setMinValue");
		double minValue = 0.0;
		ParetoDistribution instance = null;
		instance.setMinValue(minValue);
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of setShapeParameter method, of class ParetoDistribution.
	 */
	public void testSetShapeParameter() {
		System.out.println("setShapeParameter");
		double shapeParameter = 0.0;
		ParetoDistribution instance = null;
		instance.setShapeParameter(shapeParameter);
		// TODO review the generated test code and remove the default call to fail.
		
	}
}

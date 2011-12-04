package SimulationRunner;

import cern.jet.random.engine.MersenneTwister;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
		ArrayList<Double> x = new ArrayList<Double>();
		ArrayList<Double> expResult = new ArrayList<Double>();
		x.add(2.0);expResult.add(0.6031);
		x.add(3.0);expResult.add(0.7689);
		x.add(4.0);expResult.add(0.8425);
		x.add(5.0);expResult.add(0.8830);
		x.add(6.0);expResult.add(0.9083);
		x.add(7.0);expResult.add(0.9253);
		x.add(8.0);expResult.add(0.9375);
		
		ParetoDistribution instance = new ParetoDistribution(4, new MersenneTwister(new Date()));
		for(int i = 0 ; i < x.size() ; i++){
			double result = instance.cdf(x.get(i));
			assertEquals(expResult.get(i), result, 0.01);
		}
		// TODO review the generated test code and remove the default call to fail.
		
	}

	/**
	 * Test of nextDouble method, of class ParetoDistribution.
	 */
	public void testNextDouble() {
		System.out.println("nextDouble");
		ParetoDistribution instance = new ParetoDistribution(4, new MersenneTwister(new Date()));
		ArrayList<Double> xi = new ArrayList<Double>();
		ArrayList<Double> fxi = new ArrayList<Double>();
		ArrayList<Double> iOverN = new ArrayList<Double>();
		ArrayList<Double> i1OverN = new ArrayList<Double>();
		ArrayList<Double> d = new ArrayList<Double>();
		for(int i = 0; i < 50 ; i++){
			xi.add(instance.nextDouble());
			iOverN.add((i+1)/50.0);
			i1OverN.add(i/50.0);
		}
		Collections.sort(xi);
		for(int i = 0; i < 50 ; i++){
			fxi.add(1 - Math.pow(1.0/xi.get(i), 4.0/3.0));
			d.add(fxi.get(i) - i1OverN.get(i));
			d.add(iOverN.get(i) - fxi.get(i));
		}
		double max = Collections.max(d);
		assertTrue(max < 0.1923);
	}

	/**
	 * Test of pdf method, of class ParetoDistribution.
	 */
	public void testPdf() {
		System.out.println("pdf");
		ArrayList<Double> x = new ArrayList<Double>();
		ArrayList<Double> expResult = new ArrayList<Double>();
		x.add(2.0);expResult.add(0.2646);
		x.add(3.0);expResult.add(0.1027);
		x.add(4.0);expResult.add(0.0525);
		x.add(5.0);expResult.add(0.0312);
		x.add(6.0);expResult.add(0.0204);
		x.add(7.0);expResult.add(0.0142);
		x.add(8.0);expResult.add(0.0104);
		
		ParetoDistribution instance = new ParetoDistribution(4, new MersenneTwister(new Date()));
		for(int i = 0 ; i < x.size() ; i++){
			double result = instance.pdf(x.get(i));
			assertEquals(expResult.get(i), result, 0.01);
		}
	}

	/**
	 * Test of setMinValue method, of class ParetoDistribution.
	 */
	public void testSetMinValue() {
		System.out.println("setMinValue");
		double minValue = 0.5;
		ParetoDistribution instance = new ParetoDistribution(1.5, 1, new MersenneTwister(new Date()));
		ParetoDistribution instance2 = new ParetoDistribution(1.5, 0.5, new MersenneTwister(new Date()));
		instance.setMinValue(minValue);
		assertEquals(instance.cdf(1.5), instance2.cdf(1.5),0.0001);
	}

	/**
	 * Test of setShapeParameter method, of class ParetoDistribution.
	 */
	public void testSetShapeParameter() {
		System.out.println("setShapeParameter");
		double shapeParameter = 1.3;
		ParetoDistribution instance = new ParetoDistribution(1.8, 1, new MersenneTwister(new Date()));
		ParetoDistribution instance2 = new ParetoDistribution(1.3, 1, new MersenneTwister(new Date()));
		instance.setShapeParameter(shapeParameter);
		assertEquals(instance.cdf(1.5), instance2.cdf(1.5),0.0001);
	}
}

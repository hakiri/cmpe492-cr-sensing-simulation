/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimulationRunner;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author acar
 */
public class ArgumentsTest {
	
	public ArgumentsTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of parseArguments method, of class Arguments.
	 */
	@Test
	public void testParseArguments_SimulationRunner() {
		System.out.println("parseArguments");
		SimulationRunner sr = null;
		Arguments instance = new Arguments();
		boolean expResult = false;
		boolean result = instance.parseArguments(sr);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of parseArguments method, of class Arguments.
	 */
	@Test
	public void testParseArguments_String() {
		System.out.println("parseArguments");
		String fileName = "";
		Arguments instance = new Arguments();
		boolean expResult = false;
		boolean result = instance.parseArguments(fileName);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getAlpha method, of class Arguments.
	 */
	@Test
	public void testGetAlpha() {
		System.out.println("getAlpha");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getAlpha();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getAlphaInDegrees method, of class Arguments.
	 */
	@Test
	public void testGetAlphaInDegrees() {
		System.out.println("getAlphaInDegrees");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getAlphaInDegrees();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getAlphaNumbers method, of class Arguments.
	 */
	@Test
	public void testGetAlphaNumbers() {
		System.out.println("getAlphaNumbers");
		Arguments instance = new Arguments();
		ArrayList expResult = null;
		ArrayList result = instance.getAlphaNumbers();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of isAnimationOn method, of class Arguments.
	 */
	@Test
	public void testIsAnimationOn() {
		System.out.println("isAnimationOn");
		Arguments instance = new Arguments();
		boolean expResult = false;
		boolean result = instance.isAnimationOn();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getBandwidth method, of class Arguments.
	 */
	@Test
	public void testGetBandwidth() {
		System.out.println("getBandwidth");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getBandwidth();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of isBatchMode method, of class Arguments.
	 */
	@Test
	public void testIsBatchMode() {
		System.out.println("isBatchMode");
		Arguments instance = new Arguments();
		boolean expResult = false;
		boolean result = instance.isBatchMode();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getCallDura method, of class Arguments.
	 */
	@Test
	public void testGetCallDura() {
		System.out.println("getCallDura");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getCallDura();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getChannelModel method, of class Arguments.
	 */
	@Test
	public void testGetChannelModel() {
		System.out.println("getChannelModel");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getChannelModel();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getCommDur method, of class Arguments.
	 */
	@Test
	public void testGetCommDur() {
		System.out.println("getCommDur");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getCommDur();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getCommScheduleAdvertisement method, of class Arguments.
	 */
	@Test
	public void testGetCommScheduleAdvertisement() {
		System.out.println("getCommScheduleAdvertisement");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getCommScheduleAdvertisement();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getCrAlpha method, of class Arguments.
	 */
	@Test
	public void testGetCrAlpha() {
		System.out.println("getCrAlpha");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getCrAlpha();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getCrD method, of class Arguments.
	 */
	@Test
	public void testGetCrD() {
		System.out.println("getCrD");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getCrD();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getCrSector method, of class Arguments.
	 */
	@Test
	public void testGetCrSector() {
		System.out.println("getCrSector");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getCrSector();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getdNumber method, of class Arguments.
	 */
	@Test
	public void testGetdNumber() {
		System.out.println("getdNumber");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getdNumber();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getdNumbers method, of class Arguments.
	 */
	@Test
	public void testGetdNumbers() {
		System.out.println("getdNumbers");
		Arguments instance = new Arguments();
		ArrayList expResult = null;
		ArrayList result = instance.getdNumbers();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getMaxFreqCR method, of class Arguments.
	 */
	@Test
	public void testGetMaxFreqCR() {
		System.out.println("getMaxFreqCR");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getMaxFreqCR();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getMaxSnr method, of class Arguments.
	 */
	@Test
	public void testGetMaxSnr() {
		System.out.println("getMaxSnr");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getMaxSnr();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getNumberOfCalls method, of class Arguments.
	 */
	@Test
	public void testGetNumberOfCalls() {
		System.out.println("getNumberOfCalls");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getNumberOfCalls();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getNumberOfCrNodes method, of class Arguments.
	 */
	@Test
	public void testGetNumberOfCrNodes() {
		System.out.println("getNumberOfCrNodes");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getNumberOfCrNodes();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getNumberOfFreq method, of class Arguments.
	 */
	@Test
	public void testGetNumberOfFreq() {
		System.out.println("getNumberOfFreq");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getNumberOfFreq();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getNumberOfPriNodes method, of class Arguments.
	 */
	@Test
	public void testGetNumberOfPriNodes() {
		System.out.println("getNumberOfPriNodes");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getNumberOfPriNodes();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getNumberOfZones method, of class Arguments.
	 */
	@Test
	public void testGetNumberOfZones() {
		System.out.println("getNumberOfZones");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getNumberOfZones();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getNumbersOfCrUsersInZone method, of class Arguments.
	 */
	@Test
	public void testGetNumbersOfCrUsersInZone() {
		System.out.println("getNumbersOfCrUsersInZone");
		Arguments instance = new Arguments();
		ArrayList expResult = null;
		ArrayList result = instance.getNumbersOfCrUsersInZone();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of isPlotOn method, of class Arguments.
	 */
	@Test
	public void testIsPlotOn() {
		System.out.println("isPlotOn");
		Arguments instance = new Arguments();
		boolean expResult = false;
		boolean result = instance.isPlotOn();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getRadius method, of class Arguments.
	 */
	@Test
	public void testGetRadius() {
		System.out.println("getRadius");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getRadius();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getSectorNumbers method, of class Arguments.
	 */
	@Test
	public void testGetSectorNumbers() {
		System.out.println("getSectorNumbers");
		Arguments instance = new Arguments();
		ArrayList expResult = null;
		ArrayList result = instance.getSectorNumbers();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getSectrNo method, of class Arguments.
	 */
	@Test
	public void testGetSectrNo() {
		System.out.println("getSectrNo");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getSectrNo();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getSeed method, of class Arguments.
	 */
	@Test
	public void testGetSeed() {
		System.out.println("getSeed");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getSeed();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getSeedModel method, of class Arguments.
	 */
	@Test
	public void testGetSeedModel() {
		System.out.println("getSeedModel");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getSeedModel();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getSenseResultAdvertisement method, of class Arguments.
	 */
	@Test
	public void testGetSenseResultAdvertisement() {
		System.out.println("getSenseResultAdvertisement");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getSenseResultAdvertisement();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getSenseScheduleAdvertisement method, of class Arguments.
	 */
	@Test
	public void testGetSenseScheduleAdvertisement() {
		System.out.println("getSenseScheduleAdvertisement");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getSenseScheduleAdvertisement();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getSetOfD method, of class Arguments.
	 */
	@Test
	public void testGetSetOfD() {
		System.out.println("getSetOfD");
		Arguments instance = new Arguments();
		ArrayList expResult = null;
		ArrayList result = instance.getSetOfD();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getSimDura method, of class Arguments.
	 */
	@Test
	public void testGetSimDura() {
		System.out.println("getSimDura");
		Arguments instance = new Arguments();
		long expResult = 0L;
		long result = instance.getSimDura();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getSinrThreshold method, of class Arguments.
	 */
	@Test
	public void testGetSinrThreshold() {
		System.out.println("getSinrThreshold");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getSinrThreshold();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getSlotDur method, of class Arguments.
	 */
	@Test
	public void testGetSlotDur() {
		System.out.println("getSlotDur");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getSlotDur();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getTimeUnit method, of class Arguments.
	 */
	@Test
	public void testGetTimeUnit() {
		System.out.println("getTimeUnit");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getTimeUnit();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getTrafficModel method, of class Arguments.
	 */
	@Test
	public void testGetTrafficModel() {
		System.out.println("getTrafficModel");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getTrafficModel();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getProgress method, of class Arguments.
	 */
	@Test
	public void testGetProgress() {
		System.out.println("getProgress");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getProgress();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of setProgress method, of class Arguments.
	 */
	@Test
	public void testSetProgress() {
		System.out.println("setProgress");
		int progress = 0;
		Arguments instance = new Arguments();
		instance.setProgress(progress);
		// TODO review the generated test code and remove the default call to fail.
	}
}

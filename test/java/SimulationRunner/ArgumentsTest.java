/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimulationRunner;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArgumentsTest {

	/**
	 * Test of parseArguments method, of class Arguments.
	 */
	@Test
	public void testParseArguments_GraphicalUserInterface() {
		System.out.println("parseArguments");
		GraphicalUserInterface gui = null;
		Arguments instance = new Arguments();
		boolean expResult = false;
		boolean result = instance.parseArguments(gui);
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
	 * Test of parseArguments method, of class Arguments.
	 */
	@Test
	public void testParseArguments_StringArr() {
		System.out.println("parseArguments");
		String[] args = null;
		Arguments instance = new Arguments();
		boolean expResult = false;
		boolean result = instance.parseArguments(args);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getNumberOfAlphaSlices method, of class Arguments.
	 */
	@Test
	public void testGetNumberOfAlphaSlices() {
		System.out.println("getNumberOfAlphaSlices");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getNumberOfAlphaSlices();
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
	 * Test of getAverageCallDur method, of class Arguments.
	 */
	@Test
	public void testGetAverageCallDur() {
		System.out.println("getAverageCallDur");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getAverageCallDur();
		assertEquals(expResult, result, 0.0);
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
	 * Test of getCommScheduleAdvertisementDur method, of class Arguments.
	 */
	@Test
	public void testGetCommScheduleAdvertisementDur() {
		System.out.println("getCommScheduleAdvertisementDur");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getCommScheduleAdvertisementDur();
		assertEquals(expResult, result, 0.0);
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
	 * Test of getNumberOfSensingSlots method, of class Arguments.
	 */
	@Test
	public void testGetNumberOfSensingSlots() {
		System.out.println("getNumberOfSensingSlots");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getNumberOfSensingSlots();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getTransmitPower method, of class Arguments.
	 */
	@Test
	public void testGetTransmitPower() {
		System.out.println("getTransmitPower");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getTransmitPower();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getAverageNumberOfCalls method, of class Arguments.
	 */
	@Test
	public void testGetAverageNumberOfCalls() {
		System.out.println("getAverageNumberOfCalls");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getAverageNumberOfCalls();
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
	 * Test of getNumberOfSectors method, of class Arguments.
	 */
	@Test
	public void testGetNumberOfSectors() {
		System.out.println("getNumberOfSectors");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getNumberOfSectors();
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
	 * Test of getSenseResultAdvertisementDur method, of class Arguments.
	 */
	@Test
	public void testGetSenseResultAdvertisementDur() {
		System.out.println("getSenseResultAdvertisementDur");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getSenseResultAdvertisementDur();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getSenseScheduleAdvertisementDur method, of class Arguments.
	 */
	@Test
	public void testGetSenseScheduleAdvertisementDur() {
		System.out.println("getSenseScheduleAdvertisementDur");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getSenseScheduleAdvertisementDur();
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
	 * Test of getSimulationDuration method, of class Arguments.
	 */
	@Test
	public void testGetSimulationDuration() {
		System.out.println("getSimulationDuration");
		Arguments instance = new Arguments();
		long expResult = 0L;
		long result = instance.getSimulationDuration();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getSensingSlotDur method, of class Arguments.
	 */
	@Test
	public void testGetSensingSlotDur() {
		System.out.println("getSensingSlotDur");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getSensingSlotDur();
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
	 * Test of getPowerThreshold method, of class Arguments.
	 */
	@Test
	public void testGetPowerThreshold() {
		System.out.println("getPowerThreshold");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getPowerThreshold();
		assertEquals(expResult, result, 0.0);
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
	 * Test of getNumberOfReports method, of class Arguments.
	 */
	@Test
	public void testGetNumberOfReports() {
		System.out.println("getNumberOfReports");
		Arguments instance = new Arguments();
		int expResult = 0;
		int result = instance.getNumberOfReports();
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

	/**
	 * Test of getPrimaryRadius method, of class Arguments.
	 */
	@Test
	public void testGetPrimaryRadius() {
		System.out.println("getPrimaryRadius");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getPrimaryRadius();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getNoiseFloor method, of class Arguments.
	 */
	@Test
	public void testGetNoiseFloor() {
		System.out.println("getNoiseFloor");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getNoiseFloor();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getNoiseStdDev method, of class Arguments.
	 */
	@Test
	public void testGetNoiseStdDev() {
		System.out.println("getNoiseStdDev");
		Arguments instance = new Arguments();
		double expResult = 0.0;
		double result = instance.getNoiseStdDev();
		assertEquals(expResult, result, 0.0);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of getLogFileDirectory method, of class Arguments.
	 */
	@Test
	public void testGetLogFileDirectory() {
		System.out.println("getLogFileDirectory");
		Arguments instance = new Arguments();
		String expResult = "";
		String result = instance.getLogFileDirectory();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of setLogFileDirectory method, of class Arguments.
	 */
	@Test
	public void testSetLogFileDirectory() {
		System.out.println("setLogFileDirectory");
		String logFileDirectory = "";
		Arguments instance = new Arguments();
		instance.setLogFileDirectory(logFileDirectory);
		// TODO review the generated test code and remove the default call to fail.
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CommunicationEnvironment;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class CommunicationEnvironmentSuite extends TestCase {
	
	public CommunicationEnvironmentSuite(String testName) {
		super(testName);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("CommunicationEnvironmentSuite");
		suite.addTest(WirelessChannelTest.suite());
		suite.addTest(CellTest.suite());
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
}

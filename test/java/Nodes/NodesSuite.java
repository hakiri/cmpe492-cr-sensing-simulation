/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Nodes;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author acar
 */
public class NodesSuite extends TestCase {
	
	public NodesSuite(String testName) {
		super(testName);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("NodesSuite");
		suite.addTest(CRNodeTest.suite());
		suite.addTest(CRBaseTest.suite());
		suite.addTest(PrimaryTrafficGeneratorNodeTest.suite());
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

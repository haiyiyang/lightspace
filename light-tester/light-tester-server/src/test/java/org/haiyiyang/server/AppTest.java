package org.haiyiyang.server;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {

		String x = "11";
//		long l = Long.parseLong(x, 36);
//		System.out.println(l);
		long i = Integer.parseInt(x, 26);
		System.out.println(i);
		
		assertTrue(true);
	}

}

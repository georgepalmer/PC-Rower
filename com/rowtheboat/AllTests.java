/*
 * File: AllTests.java
 * 
 * Date			Version		User		Description
 * 11-Feb-2004	1.0			GeorgeP		Initial version coded
 * 
 */

package com.rowtheboat;
 
import com.rowtheboat.input.test.StrokeDataTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This class performs all the JUnit tests for PC-Rower
 * 
 * @author GeorgeP
 */
public class AllTests {

	public static Test suite() {
		
		TestSuite suite = new TestSuite("PC-Rower tests");
		
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(StrokeDataTest.class));
		//$JUnit-END$
		
		return suite;
	}
}
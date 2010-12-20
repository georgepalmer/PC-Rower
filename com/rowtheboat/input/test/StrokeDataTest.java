/*
 * File: StrokeDataTest.java
 * 
 * Date			Version		User		Description
 * 11-Feb-2004	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat.input.test;

import com.rowtheboat.input.StrokeData;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * The JUnit test for Stroke Data
 * 
 * @author GeorgeP
 */

public class StrokeDataTest extends TestCase {

	/* Class Variables */
	private StrokeData emptyStroke;
	
	
	/* Constructor */
	public StrokeDataTest(String arg0) {
		super(arg0);
	}

	/* Setup class variables */
	protected void setUp() throws Exception {
		
		emptyStroke = new StrokeData();
	}


	/* Test cases */
	public void testStrokeData() {
		
		Assert.assertEquals( emptyStroke, new StrokeData() );
	}

	public void testStrokeDataintfloat() {
		
		StrokeData stroke1 = new StrokeData(192, 10);
		StrokeData stroke2 = new StrokeData();
		stroke2.setStatus(192);
		stroke2.setDistance(10);
		
		Assert.assertTrue(!stroke1.equals(null));
		Assert.assertEquals(stroke1, stroke1);
		Assert.assertEquals(stroke1, new StrokeData(192, 10));
		Assert.assertEquals(stroke1, stroke2);
	}

	public void testGet500Split() {
		
		StrokeData stroke = new StrokeData();
		stroke.setPower(0);
		Assert.assertTrue( stroke.get500Split() == 0 );
		
		stroke.setPower(2.8);
		Assert.assertTrue( stroke.get500Split() == 8.2 );
	}

	public void testGetCaloriesPerHour() {
		
		StrokeData stroke = new StrokeData();
		stroke.setPower(0);	
		Assert.assertTrue( stroke.getCaloriesPerHour() == 0 );
		
		stroke.setPower(1);
		Assert.assertTrue( stroke.getCaloriesPerHour() == 303.4416 );
	}

	public void testGetMetersPerSecond() {
		
		StrokeData stroke = new StrokeData();
		stroke.setPower(0);
		Assert.assertTrue( stroke.getMetersPerSecond() == 0 );
		
		stroke.setPower(2.8);
		Assert.assertTrue( stroke.getMetersPerSecond() == 1.0 );
	}
	
	public void testSetHeartPeriod() {
		
		StrokeData stroke = new StrokeData();
		stroke.setHeartPeriod(0);
		
		Assert.assertTrue( stroke.getHeartRate() == 0 );
		
		stroke.setHeartPeriod(10000);
		Assert.assertTrue( stroke.getHeartRate() == 57 );
	}
}
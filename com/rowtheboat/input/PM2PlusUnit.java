/*
 * PC-Rower	PC-Rower is a piece of software that allows the connection of a Concept II rowing
 * 			machine to a PC to provide real-time and post workout analysis of performance.
 * Copyright (C) 2003-2005 George Palmer
 * 
 * 
 * This file is part of PC-Rower.  PC-Rower is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License(GPL) as published by the Free 
 * Software Foundation; either version 2 of the License, or (at your option) any later version.  
 * Under the GPL any derivations or alterations of this software must keep this header intact.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 * MA 02111-1307 USA
 * 
 * The author may be contacted at feedback@rowtheboat.com
 */
 
 
 /*
 * File: PM2PlusUnit.java
 * 
 * Date			Version		User		Description
 * 17-Oct-2003	1.0			GeorgeP		Inital version coded
 * 25-Oct-2004	1.02		GeorgeP		gnu.io imports added for gcj native build
 * 30-Oct-2004	1.03		GeorgeP		Updated to RXTX pure serial driver
 * 
 */
 
package com.rowtheboat.input;


import com.rowtheboat.gui.OptionsSingleton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import com.rowtheboat.workout.Workout;

/**
 * PM2PlusUnit provides an implementation for the PM2+ input device unit.  It implements the
 * InputDevice interface. 
 * 
 * @author GeorgeP
 */

public class PM2PlusUnit implements IInputDevice {

	/* Class Variables */
	
	private InputStream pm2PlusInputStream;				/* The PM2+ input stream */
	private OutputStream pm2PlusOutputStream;			/* The PM2+ output stream */
	private SerialPort serialPort;						/* The serial port */
	private static PM2PlusUnit singleton;				/* Self singleton reference */
	
	private static final int PORT_TIMEOUT = 2000;		/* The serial port timeout (=2seconds) */
	private static final String DISPLAY_NUMBER = "99";	/* The hexadecimal code to display a 
														   number */
	private static final String DISPLAY_PLACE = "93";	/* The hexadecimal code to display a rowers
														   place */
	private static final String LOCK_UNIT = "e0";		/* The hexadecimal code to lock the unit */
	private static final String QUERY_DISTANCE = "b0";	/* The hexadecimal code to query distance*/
	private static final String QUERY_PACE = "b1";		/* The hexadecimal code to query pace */
	private static final String QUERY_HEART = "b2";		/* The hexadecimal code to query heart */
	private static final String QUERY_TIME = "b3";		/* The hexadecimal code to query time */
	private static final String START_ON_STROKE = "c5"; /* The hexadecimal code to start on the 
														   next stroke */
	private static final String START_NOW = "c6";		/* The hexadecimal code to start now */
	private static final String SETUP_DISTANCE = "94";	/* The hexadecimal code to setup meters */
	private static final String SETUP_TIME = "95";		/* The hexadecimal code to setup time */
	private static final String UNLOCK_UNIT = "c7";		/* The hexadecimal code to unlock unit*/

	/* The gnu.io.* classes are not used in this class but are here to force gcj to include them in
 	 * the native build */
	private static final Class gcjBodgeClass3 = gnu.io.RXTXCommDriver.class;
	private static final Class gcjBodgeClass4 = gnu.io.RXTXPort.class;
	private static final Class gcjBodgeClass5 = gnu.io.RXTXVersion.class;
	private static final Class gcjBodgeClass6 = gnu.io.UnSupportedLoggerException.class;
	private static final Class gcjBodgeClass7 = gnu.io.Zystem.class;
	

	/* Constructor */
	
	/**
	 * Initialise the PM2PlusUnit class by accessing the serial port and checking its available
	 * 
	 * @param port the port to access (this is OS dependent)
	 */
	private PM2PlusUnit(String port, Workout workout) 
			throws PortInUseException, UnsupportedCommOperationException, 
			TooManyListenersException, NumberFormatException, IOException, InterruptedException  {
		
		/* Find the ports and iterate through them */
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		
		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			
			/* If the port is serial and named as the passed parameter then initialise it */
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL 
				&& portId.getName().equals(port)) {
				
				initialisePort(portId, workout);
			}
		}
	}
	
	
	/* Public Methods */
	
	/**
	 * Return the only instance of this class
	 * 
	 * @param port		the serial port to use
	 * @param workout	the workout to do
	 * @return	a reference to this class
	 * @throws NumberFormatException
	 * @throws PortInUseException
	 * @throws UnsupportedCommOperationException
	 * @throws TooManyListenersException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static PM2PlusUnit getInstance(String port, Workout workout) 
		throws NumberFormatException, PortInUseException, UnsupportedCommOperationException, 
		TooManyListenersException, IOException, InterruptedException {
		
		if (singleton == null) {
			singleton = new PM2PlusUnit(port, workout);
		}
		
		return singleton;
	}
	
	
	/* Inherited Javadoc */
	public void cleanUp() throws NumberFormatException, IOException, InterruptedException {
		
		/* Release the serial port for other applications if it has been used by this program */
		if (serialPort != null) {
			
			/* Unlock the PM2+ */
			pm2PlusOutputStream.write(Integer.parseInt(UNLOCK_UNIT, 16));
			Thread.sleep(100);
			
			/* Close the streams and serial port */
			pm2PlusInputStream.close();
			pm2PlusOutputStream.close();
			serialPort.close();
		}
		
		/* This class must be reinstantiated as no longer of any use */
		singleton = null;
	}
	
	
	/* Inherited Javadoc */
	public void displayCount(byte time) 
			throws NumberFormatException, IOException, InterruptedException {
		
		/* Display a number.  No thread sleeping as done in time for countdown  */
		pm2PlusOutputStream.write(Integer.parseInt(DISPLAY_NUMBER, 16));
		pm2PlusOutputStream.write(time);
	}
	
	
	/* Inherited Javadoc */
	public StrokeData retrieveStrokeData(float ergNumber) 
			throws NumberFormatException, IOException {
		
		/* As we are receiving a stroke we should clear the last one first */
		PM2PlusStrokeManager.getInstance().setStrokeData(null);
		
		/* Generate erg number as string */
		String ergNum = Integer.toHexString(new Float(ergNumber).intValue());
		if (ergNum.length() == 1) {
			ergNum = "0" + ergNum;
		}
		
		/* Query Distance */
		pm2PlusOutputStream.write(Integer.parseInt(QUERY_DISTANCE, 16));
		pm2PlusOutputStream.write(Integer.parseInt(ergNum, 16));
		waitForDataToBeProcessed();
				
		/* Query Time */
		pm2PlusOutputStream.write(Integer.parseInt(QUERY_TIME, 16));
		pm2PlusOutputStream.write(Integer.parseInt(ergNum, 16));
		waitForDataToBeProcessed();
				
		if ( OptionsSingleton.getInstance().getFullStrokeData() ) {
			/* Query Pace */
			pm2PlusOutputStream.write(Integer.parseInt(QUERY_PACE, 16));
			pm2PlusOutputStream.write(Integer.parseInt(ergNum, 16));
			waitForDataToBeProcessed();
		
			/* Query Heart */
			pm2PlusOutputStream.write(Integer.parseInt(QUERY_HEART, 16));
			pm2PlusOutputStream.write(Integer.parseInt(ergNum, 16));
			waitForDataToBeProcessed();
		}
		
		/* Return the stroke data */
		return PM2PlusStrokeManager.getInstance().getStrokeData();
	}
	
	
	/* Inhertied Javadoc */
	public void setupWorkout(Workout workout) 
			throws NumberFormatException, IOException, InterruptedException {
	
		/* Lock the unit */
		pm2PlusOutputStream.write(Integer.parseInt(LOCK_UNIT, 16));
		Thread.sleep(100);

		/* Determine the type of workout and setup accordingly */
		String hexString = "";
		boolean workoutFlag = false;
	
		if (workout.getType() == Workout.DISTANCE_WORKOUT) {
	
			/* Setup as a distance workout and create the hexString  */
			pm2PlusOutputStream.write(Integer.parseInt(SETUP_DISTANCE, 16));
			hexString = Integer.toHexString( Float.floatToIntBits(workout.getDistance()) );
		
			workoutFlag = true;
		}
		if (workout.getType() == Workout.TIME_WORKOUT) {

			/* Setup as a time workout and create the hexString  */
			pm2PlusOutputStream.write(Integer.parseInt(SETUP_TIME, 16));
			hexString = Integer.toHexString( Float.floatToIntBits(workout.getTime()) );
		
			workoutFlag = true;
		}

		/* If a workout of type in the above if statements, send the data through */
		if (workoutFlag) {
			pm2PlusOutputStream.write(Integer.parseInt(hexString.substring(6,8) , 16));
			pm2PlusOutputStream.write(Integer.parseInt(hexString.substring(4,6) , 16));
			pm2PlusOutputStream.write(Integer.parseInt(hexString.substring(2,4) , 16));
			pm2PlusOutputStream.write(Integer.parseInt(hexString.substring(0,2) , 16));
			Thread.sleep(100);
		}

		/* The unit needs to be relocked due an inherent error with the PM2+ */
		pm2PlusOutputStream.write(Integer.parseInt(LOCK_UNIT, 16));
		Thread.sleep(100);
	}
	
	
	/* Inherited Javadoc */
	public void startRowing(int startType) 
			throws NumberFormatException, IOException, InterruptedException {
		/* Thread sleeping is low so start as soon as possible after command issued */
		
		if (startType == Workout.START_ON_STROKE) {
			/* Start rowing on first stroke */
			pm2PlusOutputStream.write(Integer.parseInt(START_ON_STROKE, 16));
			Thread.sleep(10);
		}
		if (startType == Workout.START_AFTER_COUNT) {
			/* Start rowing immediately */
			pm2PlusOutputStream.write(Integer.parseInt(START_NOW, 16));
			Thread.sleep(10);
		}
	}
	
	
	/* Private Methods */
	
	/**
	 * Initialise the serial port
	 * 
	 * @param	port	The serial port identifier
	 * @param	workout	The workout
	 * 
	 * @throws PortInUseException
	 * @throws UnsupportedCommOperationException
	 * @throws IOException
	 * @throws TooManyListenersException
	 */
	private void initialisePort(CommPortIdentifier port, Workout workout) 
		throws PortInUseException, UnsupportedCommOperationException, 
		IOException, TooManyListenersException {
		
		/* Open the serial port */
		serialPort = (SerialPort) port.open(OptionsSingleton.APP_NAME, PORT_TIMEOUT);
		
		/* Set the port up to the defaults required */
		serialPort.notifyOnDataAvailable(true);
		serialPort.setSerialPortParams
			(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		
		/* Get the input and output streams */
		pm2PlusOutputStream = serialPort.getOutputStream();
		pm2PlusInputStream = serialPort.getInputStream();
		
		/* Add the event listener */
		serialPort.addEventListener( 
			new PM2PlusReadEventListener( pm2PlusInputStream, workout.getType() ) );
	}

	
	/**
	 * Ensures that the program cannot progess until the serial port event has fired and completed
	 */
	private void waitForDataToBeProcessed() throws NumberFormatException, IOException {
	
		/* Retrieve the manager */
		PM2PlusStrokeManager manager = PM2PlusStrokeManager.getInstance();
	
		/* Whilst we are waiting for the data to be processed let the current thread
		 * (ie this class) check for other threads - ie the PM2PlusReadEventListener */
		while ( !manager.getDataProcessed() ) {
			Thread.yield();
		}
		
		/* Now that the data has been processed, set the data processed to false for next time */
		manager.setDataProcessed(false);	
	}
}
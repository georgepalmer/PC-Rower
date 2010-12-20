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
 * File: PM2PlusReadEvent.java
 * 
 * Date			Version		User		Description
 * 17-Oct-2003	1.0			GeorgeP		Inital version coded
 * 30-Oct-2004	1.03		GeorgeP		Updated to RXTX pure serial driver
 * 
 */
 
package com.rowtheboat.input;


import java.io.IOException;
import java.io.InputStream;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import com.rowtheboat.workout.Workout;

/**
 * Listens for PM2Plus Serial Port Events
 * 
 * @author GeorgeP
 */

public class PM2PlusReadEventListener implements SerialPortEventListener {

	/* Class Variables */

	private InputStream inputStream;				/* The inputStream to listen to */
	private int workoutType;						/* The workout type */
	
	private static final int DISTANCE_WORKOUT = 196;/* The distance workout - hex c4 */
	private static final int TIME_WORKOUT = 200;	/* The time workout - hex c8 */
	private static final int END_OF_WORKOUT_MASK = 1;
													/* The end of workout mask */
	private static final int END_OF_STROKE_MASK = 2;/* The end of stroke mask */
	private static final int UNKNOWN_MASK = 16;		/* Something to do with venue racing */
	
	private static final int BUFFER_SIZE = 5;		/* The input buffer size */
	private static final char[] HEX_CHARS = 				
		new char[] {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
													/* An array of hex characters */


	/* Constructor */

	/**
	 * Constructs the PM2PlusReadEvent
	 * 
	 * @param inputStream	the input stream to be monitored
	 */
	public PM2PlusReadEventListener(InputStream inputStream, int workoutType) {
	
		/* Set the class variables */
		this.inputStream = inputStream;
		this.workoutType = workoutType;
	}

	
	/* Public Methods */
	
	/**
	 * The method called when a serialEvent occurs
	 * 
	 * @param event	the serial port event
	 */
	public void serialEvent(SerialPortEvent event) {
		
		/* Check the type of event and if data is available from the serial port then process this
		 * so it can be used by the PM2PlusStrokeManager */
		switch(event.getEventType()) {
			case SerialPortEvent.BI:
			case SerialPortEvent.OE:
			case SerialPortEvent.FE:
			case SerialPortEvent.PE:
			case SerialPortEvent.CD:
			case SerialPortEvent.CTS:
			case SerialPortEvent.DSR:
			case SerialPortEvent.RI:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				break;
			case SerialPortEvent.DATA_AVAILABLE:
				byte[] readBuffer = new byte[BUFFER_SIZE];
				try {
					/* Populate the readBuffer */
					while (inputStream.available() > 0) {
						int numBytes = inputStream.read(readBuffer);
					}

					processBufferData(readBuffer);
				}
				catch (IOException e) {
					/* Nothing can be done about this.  Could show error box */
					e.printStackTrace();
				}
				/* Let the manager know that the work here is done */
				PM2PlusStrokeManager.getInstance().setDataProcessed(true);
				break;
		}
	}


	/* Private Methods */

	/**
	 * Converts a byte into a hex string
	 * 
	 * @return the hex string
	 */
	private String hexString (byte nibble) {
	
		return "" + HEX_CHARS [0x0F & (nibble >> 4)] + HEX_CHARS [0x0F & nibble];
	}
	
	
	/**
	 * Processes the buffer turning the data into the correct fields within the StrokeData object
	 * 
	 * @param buffer	the buffer to process
	 */
	private void processBufferData(byte [] buffer) {
		
		/* Construct a byte and float representing the data from the PM2+ */
		int firstByte = Integer.parseInt( hexString(buffer[0]), 16 );
		String remainingBytesString =     hexString(buffer[4]) 
										+ hexString(buffer[3])
										+ hexString(buffer[2])
										+ hexString(buffer[1]);
		float remainingBytes = Float.intBitsToFloat
			(Integer.parseInt(remainingBytesString,16));

		/* The following assumes a query sequence of distance, pace, hr and time in PM2PlusUnit */
		PM2PlusStrokeManager strokeManager = PM2PlusStrokeManager.getInstance();

		/* If there is no stroke data then create a new stroke data */
		if (strokeManager.getStrokeData() == null ) {
	
			/* Change status to a standard regardless of workout type */
			if (workoutType == Workout.DISTANCE_WORKOUT) {
				if (firstByte == DISTANCE_WORKOUT + END_OF_WORKOUT_MASK  ||
					firstByte == DISTANCE_WORKOUT + END_OF_WORKOUT_MASK + UNKNOWN_MASK) {
					firstByte = StrokeData.END_OF_WORKOUT;
				}
				if (firstByte == DISTANCE_WORKOUT + END_OF_STROKE_MASK) {
					firstByte = StrokeData.END_OF_STROKE;
				}
			}
			if (workoutType == Workout.TIME_WORKOUT) {
				if (firstByte == TIME_WORKOUT + END_OF_WORKOUT_MASK ||
				firstByte == TIME_WORKOUT + END_OF_WORKOUT_MASK + UNKNOWN_MASK) {
					firstByte = StrokeData.END_OF_WORKOUT;
				}
				if (firstByte == TIME_WORKOUT + END_OF_STROKE_MASK) {
					firstByte = StrokeData.END_OF_STROKE;
				}
			}
			
			StrokeData newStroke = new StrokeData(firstByte, remainingBytes);
			strokeManager.setStrokeData(newStroke);
		}
		else {
			/* Check to see if the time has been set.  If not update accordingly */
			if (strokeManager.getStrokeData().getTime() == StrokeData.UNSET) {
				
				/* firstByte not passed as status should remain unchanged */
				strokeManager.getStrokeData().setTime(remainingBytes);
			}
			else {
				
				/* Check to see whether the power has been set.  If so then the data must be the
				 * HR value. */
				if (strokeManager.getStrokeData().getPower() == StrokeData.UNSET) {
				
					strokeManager.getStrokeData().setPaceData(firstByte, remainingBytes);
				}
				else {
					
					strokeManager.getStrokeData().setHeartPeriod
						( new Float(remainingBytes).intValue() );
				}
				
			}
		}
	}
}
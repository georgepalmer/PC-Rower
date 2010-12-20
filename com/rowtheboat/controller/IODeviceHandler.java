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
 * File: IODeviceHandler.java
 * 
 * Date			Version		User		Description
 * 28-Nov-2003	1.0			GeorgeP		Initial version coded
 * 30-Oct-2004	1.03		GeorgeP		Updated to RXTX pure serial driver
 * 10-Dec-2004	1.04		GeorgeP		Garbage collection changes made
 * 
 */

package com.rowtheboat.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.TooManyListenersException;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import com.rowtheboat.output.IFileOutputDevice;
import com.rowtheboat.output.XMLWorkoutWriter;
import com.rowtheboat.workout.ComputerRower;
import com.rowtheboat.workout.HumanRower;
import com.rowtheboat.workout.Rower;
import com.rowtheboat.workout.Workout;
import com.rowtheboat.gui.OptionsSingleton;
import com.rowtheboat.input.FixedSplitInput;
import com.rowtheboat.input.IInputDevice;
import com.rowtheboat.input.PM2PlusUnit;
import com.rowtheboat.input.StrokeData;
import com.rowtheboat.input.VariableSplitInput;

/**
 * IODeviceHandler encapsulates the input and output devices for the workout.  It provides the
 * latest StrokeCollection upon request.
 * 
 * @author GeorgeP
 */

public class IODeviceHandler {

	/* Class Variables */
	private float lastStrokeTime = -1;				/* The workout time of the last stroke */
	private long lastStrokeMillis;					/* The last stroke time in milliseconds */
	private int numberOfRowers;						/* The number of rowers */
	private int startType;							/* The workout start type */
	private Core core;								/* Reference to the core */
	

	private int [] humanRowerErgoNumbers;			/* Maintains human rower indexes */
	private IInputDevice [] inputDevices;			/* The input devices */
	private IInputDevice [] shadowInputDevices;		/* The shadow input devices */
	private IFileOutputDevice [] fileOutputDevices;	/* The file output devices */


	/* Constructor */
	
	/**
	 * Construct the collection of strokes
	 * 
	 * @param workout	the workout which the strokes will be part of
	 * @param	core	the Core to which this handler should be references
	 */
	public IODeviceHandler(Workout workout, Core core) throws Exception {
		
		/* Initialise the class fields variables */
		this.core = core;
		numberOfRowers = workout.numberOfRowers();
		startType = workout.getStartType();
		
		/* Initialise the array class variables */
		humanRowerErgoNumbers = new int[numberOfRowers];
		inputDevices = new IInputDevice [numberOfRowers];
		shadowInputDevices = new IInputDevice [numberOfRowers];
		fileOutputDevices = new IFileOutputDevice [numberOfRowers];
		
		/* Initialise the input devices */
		initialiseInputDevices(workout);
		setupWorkout(workout);
	}
	
	
	/* Public Methods */
	
	/**
	 * Clean up any resources associated with the input devices  
	 */
	public void cleanUp() throws Exception {
		
		/* Used so PM methods only called once */
		boolean flag = true;
		
		/* Delegate the responsability to each input device */
		for (int i = 0; i < inputDevices.length; i++) {
			if (humanRowerErgoNumbers[i] == -1) {
				inputDevices[i].cleanUp();
			}
			else {
				if (flag) {
					inputDevices[i].cleanUp();
					flag = false;
				}
			}
		}
	}
	
	
	/**
	 * Called to retrieve the latest stroke collection
	 * 
	 * @return	a stroke collection
	 */
	public StrokeCollection getLatestStrokeCollection() throws Exception {
	
		/* Initialise the stroke collection */
		StrokeCollection strokeCollection = new StrokeCollection(numberOfRowers);
		
		/* The boolean that tracks if the workout has finished for all human boats */
		boolean workoutFinished = true;

		/* The current lag since the last time update */
		float lag = 0;
		
		/* Loop over the input devices */
		for (int i = 0; i < inputDevices.length; i++) {
			
			if (humanRowerErgoNumbers[i] != -1) {
						
				/* If the input device is a human rower, get the stroke data, add this to the 
				 * stroke collection and set the last time */
				
				StrokeData stroke = inputDevices[i].retrieveStrokeData( humanRowerErgoNumbers[i] );
				
				/* Call a garbage collection as retrieving stroke data sets a stroke to null so
				 * it is desirable to recover this space. */
				OptionsSingleton.getInstance().garbageCollect();
				
				/* If the stroke time and last stroke time are not equal (i.e. a second has cligged
				 * on in the PM2+) then calculate the latest stroke distance and time */
				if ( OptionsSingleton.getInstance().getBoatSmoothing() ) {
					
					/* Calculate the lag and use to update the stroke time for more accuracy */
					lag = calculateLag(stroke);
					stroke.setTime( lastStrokeTime + lag );
				}
				else {
					lastStrokeTime = stroke.getTime();
				}
				
				/* Set the stroke in the stroke collection */
				strokeCollection.setStroke(i, stroke);
				
				/* Generate shadow stroke data where appropriate */
				if (shadowInputDevices[i] != null) {
					strokeCollection.setShadowStroke( i, 
						shadowInputDevices[i].retrieveStrokeData( lastStrokeTime + lag ) );
				}
				
				/* If the stroke data is the end of the stroke then update the output file */
				if ( stroke.isEndOfStroke() && fileOutputDevices[i] != null ) {
					fileOutputDevices[i].updateStrokeData(stroke);
				}
				
				/* If the stroke is the the end of the workout then inform the output device
				 * of the last stroke */
				if ( stroke.isEndOfWorkout() && fileOutputDevices[i] != null ) {
					fileOutputDevices[i].workoutFinished(stroke);
				}
				
				/* Update if the workout has finished */
				workoutFinished = workoutFinished && stroke.isEndOfWorkout();
			}
			else {
				/* Else, the rower is a computer generated, so use the time to retrieve the
				 * stroke data.  As there must be one human rower, the last time variable will
				 * always be set. */

				strokeCollection.setStroke
					(i, inputDevices[i].retrieveStrokeData(lastStrokeTime + lag));
			}
		}
		
		
		/* Test to see whether the workout is over */
		if ( workoutFinished ) {
			
			/* Inform the core that the workout has finished */
			core.workoutFinished();
		}
		
		/* Return the stroke collection */
		return strokeCollection;
	}
	
	
	/**
	 * Display the count on each input device.  This is used as the basis of a countdown
	 * 
	 * @param	time	the time (count) to display on the input device
	 */
	public void displayCount(byte time) throws Exception {
		
		/* Used so PM methods only called once */
		boolean flag = true;
		
		/* Delegate the responsability to each input device */
		for (int i = 0; i < inputDevices.length; i++) {
			if (humanRowerErgoNumbers[i] == -1) {
				inputDevices[i].displayCount(time);
			}
			else {
				if (flag) {
					inputDevices[i].displayCount(time);
					flag = false;
				}
			}
		}
	}
	
	
	/**
	 * Starts the rowing by informing the input and output devices to begin
	 */
	public void startRowing() throws Exception {
		
		/* Used so PM methods only called once */
		boolean flag = true;
		
		/* Delegate the responsability to each input device */
		for (int i = 0; i < inputDevices.length; i++) {
			if (humanRowerErgoNumbers[i] == -1) {
				inputDevices[i].startRowing(startType);
			}
			else {
				if (flag) {
					inputDevices[i].startRowing(startType);
					flag = false;
				}
			}
		}
	}
	
	
	/**
	 * This is called if a workout is aborted and essentially cleans up the file output devices,
	 * thus ensuring that any cached data is written out.
	 */
	public void workoutAborted() throws Exception {
		
		for (int i = 0; i < fileOutputDevices.length; i++) {
			if (fileOutputDevices[i] != null) {
				fileOutputDevices[i].workoutAborted();
			}
		}
	}
	
	
	/* Private Methods */
	
	/**
	 * Calculate the lag since the last PM unit update
	 * 
	 * @param	stroke	the stroke
	 */
	private float calculateLag(StrokeData stroke) {
		
		float lag;
		
		/* Calculate the latest distance, time and millis */
		if ( stroke.getTime() != lastStrokeTime ) {
			
			lastStrokeTime = stroke.getTime();
			lastStrokeMillis = System.currentTimeMillis();
		}
					
		/* Calculate the lag */
		lag = (float) ((System.currentTimeMillis() - lastStrokeMillis) / 1000.0);
					
		/* This stops boats moving backwards - although it doesn't solve the
		 * original problem */
		if (lag > 1) {
			lag = 1;
		}
		
		return lag;
	}
	
	
	/**
	 * This method initialises the input devices
	 * 
	 * @param	workout	the workout to initialise the devices for
	 */
	private void initialiseInputDevices(Workout workout) 
			throws ParserConfigurationException, FactoryConfigurationError, NumberFormatException, 
			PortInUseException, UnsupportedCommOperationException, TooManyListenersException, 
			IOException, InterruptedException, SAXException, MalformedURLException, 
			DocumentException {
		
		/* Loop over the human rowers */
		for (int i = 0; i < inputDevices.length; i++) {
			
			/* Test the type of rower */
			Rower rower = workout.getRower(i); 
			if (rower instanceof HumanRower) {
				
				/* If the rower is a human rower then initialise the PM2+ unit and set their ergo
				 * number. */
				inputDevices[i] = PM2PlusUnit.getInstance
					( OptionsSingleton.getInstance().getSerialPort(), workout );
				humanRowerErgoNumbers[i] = ((HumanRower) rower).getErgoNumber();
				
				/* If workout data should be logged then create the xml writer */
				HumanRower humanRower = ((HumanRower) rower);
				if ( humanRower.shouldLogData() ) {
					fileOutputDevices[i] = 
						new XMLWorkoutWriter(humanRower, workout);
				}
				
				/* If the rower has a shadow then initialise this */
				if ( workout.hasShadowRower(i) ) {
					shadowInputDevices[i] = 
						initialiseComputerRower( ((ComputerRower) workout.getShadow(i)), workout );
				}
			}
			else {
				/* Initialise the correct type of computer rower */
				inputDevices[i] = initialiseComputerRower( ((ComputerRower) rower), workout );
					
				/* Set ergo number to -1, i.e. not a human rower */
				humanRowerErgoNumbers[i] = -1;
			}
		}
	}
	
	
	/**
	 * Initilase the computer rower
	 * 
	 * @param	compRower	the computer rower to initialise
	 * @param	workout		the workout that the computer rower is in
	 * @return	a concrete implementation of an input device
	 */
	private IInputDevice initialiseComputerRower(ComputerRower compRower, Workout workout) 
			throws MalformedURLException, DocumentException {
		
		/* Test the computer rower type */
		if ( compRower.getType() == ComputerRower.FIXED_SPLIT ) {
					
			/* Initialise the correct fixed split computer rower depending on the workout type. */
			FixedSplitInput input;
			if (workout.getType() == Workout.DISTANCE_WORKOUT) {
				input = new FixedSplitInput( Workout.DISTANCE_WORKOUT );
				input.setTargetDistance(workout.getDistance());	
			}
			else {
				/* Must be fixed time workout */
				input = new FixedSplitInput( Workout.TIME_WORKOUT );
				input.setTargetSplit(compRower.getSplitToRowAt());
			}
			input.setTargetTime(compRower.getRowTime());
					
			return input;
		}
		else {
			return new VariableSplitInput( compRower.getVariableInputFile() );
		}
	}
	
	
	/**
	 * Setups the input devices for the workout
	 * 
	 * @param workout		the workout to setup
	 * @throws Exception
	 */
	private void setupWorkout(Workout workout) throws Exception {
		
		/* Used so PM methods only called once */
		boolean flag = true;
		
		/* Delegate the responsability to each input device */
		for (int i = 0; i < inputDevices.length; i++) {
			if (humanRowerErgoNumbers[i] == -1) {
				inputDevices[i].setupWorkout(workout);
			}
			else {
				if (flag) {
					inputDevices[i].setupWorkout(workout);
					flag = false;
				}
			}
		}
	}
}
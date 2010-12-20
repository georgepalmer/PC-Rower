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
 * File: FixedSplitInput.java
 * 
 * Date			Version		User		Description
 * 22-Nov-2003	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat.input;

import com.rowtheboat.workout.Workout;

/**
 * FixedSplitInput creates a fixed split target boat against which to race.  It should be 
 * constructed using a workout type, and then the class fields set according to the rules:
 * <p>
 * Workout type...Fields to set<br>
 * Distance.......target distance, target time<br>
 * Time...........target split, target time<br>
 * 
 * @author GeorgeP
 */
public class FixedSplitInput implements IInputDevice {

	/* Class Variables */

	private byte type;								/* The workout type */
	private int workoutDistance;					/* The workout distance */
	private float split;							/* The split to be achieved */
	private float workoutFinishTime;				/* The workout finish time */
	
	
	/* Constructor */

	/**
	 * Constuct a fixed split target boat
	 * 
	 * @param	workoutType	the type of workout (get from the Workout class)
	 */
	public FixedSplitInput(byte workoutType) {
		
		type = workoutType;
	}


	/* Public Methods */

	/* Inherited Javadoc */
	public void cleanUp() throws Exception {/* No action required */}


	/* Inherited Javadoc */
	public void displayCount(byte time) {}
	

	/* Inherited Javadoc */
	public StrokeData retrieveStrokeData(float time) {
		
		/* Construct a stroke.  Only status and distance are set. */
		StrokeData stroke = new StrokeData();
	
		/* Calculate the distance for the stroke */
		float distance;
		if (type == Workout.TIME_WORKOUT) {
			distance = time * 500 / split;
		}
		else {
			/* Must be a distance workout */
			distance = time * workoutDistance / workoutFinishTime;
			
			/* Check that the race distance is not exceeded.  This can happen as this class 
			 * generates data until the human rowers have finished. */
			if (distance > workoutDistance) {
				distance = workoutDistance;
			}

			/* This is a slight dodge but is required for the results window */
			stroke.setTime(workoutFinishTime);
		}
		stroke.setDistance(distance);
		
		/* Set the status */
		if (time >= workoutFinishTime) {
			stroke.setStatus(StrokeData.END_OF_WORKOUT);
		}
		else {
			stroke.setStatus(StrokeData.END_OF_STROKE);
		}
	
		return stroke;
	}
	
	
	/**
	 * Set the target distance for a workout.
	 *
	 * @param	distance	the distance for the workout
	 */
	public void setTargetDistance(int distance) {
		
		this.workoutDistance = distance;
	}
	
	
	/**
	 * Set the target split for a time workout.
	 * 
	 * @param	split	the target split
	 */
	public void setTargetSplit(float split) {
		
		this.split = split;
	}
	
	
	/**
	 * Set the target time for the boat.  In a distance workout this is the point at which the
	 * distance is complete.  In a time workout this is the time the workout should last.
	 * 
	 * @param	time	the target time
	 */
	public void setTargetTime(float time) {
		
		this.workoutFinishTime = time;
	}


	/* (non-Javadoc)
	 * @see input.IInputDevice#setupWorkout(workout.Workout)
	 */
	public void setupWorkout(Workout workout) {
		/* Nothing required */
		
	}
	
	
	/* Inherited Javadoc */
	public void startRowing(int startType) {
		/* Nothing required in this implementation */		
	}
}
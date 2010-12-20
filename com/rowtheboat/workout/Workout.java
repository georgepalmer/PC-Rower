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
 * File: Workout.java
 * 
 * Date			Version		User		Description
 * 22-Nov-2003	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat.workout;

import java.util.Calendar;

/**
 * Workout encapsulates the data about the workout to be undertaken
 * 
 * @author GeorgeP
 */

public class Workout {

	/* Class Variables */

	private Calendar date;							/* The date of the workout */
	private int distance = -1;						/* The distance of the workout */
	private int showDistance = -1;					/* The show distance for the river */
	private int time = -1;							/* The time of the workout */
	private int type;								/* The type of the workout */
	private int startType = START_ON_STROKE;		/* When the workout should start - defaulted to
													   start on stroke */
	
	private Rower [] rowers;						/* The rowers for the workout */
	private Rower [] shadows;						/* The shadows for the workout */
	
	
	/* Constants */
	
	/**
	 * The Just Row constant.  This is for workouts where the rower just wishes to row.
	 */
	public static final byte JUST_ROW = 0;
	
	/**
	 * The fixed distance constant.  This is for workouts where the rower wishes to row a fixed
	 * distance.
	 */
	public static final byte DISTANCE_WORKOUT = 1;
	
	/**
	 * The fixed time constant.  This is for workouts where the rower wished to row for a fixed
	 * time.
	 */
	public static final byte TIME_WORKOUT = 2; 

	/**
	 * Start rowing immediately constant.  This is used at the end of a countdown.
	 */
	public static final byte START_AFTER_COUNT = 3;
	
	/**
	 * Start rowing on stroke.  This is used if the rower wishes to start on the first stroke.
	 */
	public static final byte START_ON_STROKE = 4;


	/* Constructors */
	
	/**
	 * Construct a Workout of the given type.  If the type is invalid, a Just Row workout is 
	 * assumed.
	 * 
	 * @param the type of workout
	 */
	public Workout(int type) {
		
		/* Check the type of the workout.  Default to Just Row if invalid input */
		switch (type) {
			case 1 :
			case 2 :
				this.type = type;
				break;
			default : {
				this.type = JUST_ROW;
				addRower( new HumanRower("", 0, false) );
				setStartType(Workout.START_ON_STROKE);
			}
		}
		
		this.date = Calendar.getInstance();
	}
	
	
	/* Public Methods */
	
	/**
	 * Add a rower to the workout
	 * 
	 * @param	rower	the rower to be added
	 */
	public void addRower(Rower rower) {
		
		/* Increase the array size and add the rower */
		increaseRowerArrays();
		rowers[rowers.length - 1] = rower;
	}
	
	
	/**
	 * Add a shadow to the workout.  This can only be done once the rower to be shadowed has been
	 * added.
	 * 
	 * @param	shadow	the shadow to be added
	 */
	public void addShadow(Rower shadow) {
		
		shadows[rowers.length - 1] = shadow;
	}
	
	
	/**
	 * Returns the date of the workout as a string
	 * 
	 * @return	the date
	 */
	public String getDate() {
	
		int day = date.get(Calendar.DAY_OF_MONTH);
		int month = date.get(Calendar.MONTH) + 1;
		int year = date.get(Calendar.YEAR);
		int hours = date.get(Calendar.HOUR_OF_DAY);
		int minutes = date.get(Calendar.MINUTE);
		int seconds = date.get(Calendar.SECOND);
								
		return year + "-" + month + "-" + day + "-" + hours + ":" + minutes + ":" + seconds; 
	}
	
	
	/**
	 * Return the distance of the workout
	 * 
	 * @return the distance of the workout
	 */
	public int getDistance() {
		
		return distance;
	}
	
	
	/**
	 * Returns a given rower
	 * 
	 * @param index	the index of the rower to return
	 * @return	the rower
	 */
	public Rower getRower(int index) {
		
		return rowers[index];
	}
	
	
	/**
	 * Returns a given shadow.  It is suggested to test whether a shadow first exists. 
	 * 
	 * @param	index	the index of the shadow
	 * @return	the shadow rower
	 */
	public Rower getShadow(int index) {
		
		return shadows[index];
	}
	
	
	/**
	 * Return the workout show distance
	 * 
	 * @return	the show distance
	 */
	public int getShowDistance() {
		
		return showDistance;
	}
	
	
	/**
	 * Returns the start type
	 * 
	 * @return	the start type
	 */
	public int getStartType() {
		
		return startType;
	}
	
	
	/**
	 * Return the time the workout should last
	 * 
	 * @return the time of the workout
	 */
	public int getTime() {
		
		return time;
	}
	
	
	/**
	 * Return the type of the workout
	 * 
	 * @return the int representing the type of the workout as defined by the class fields
	 */
	public int getType() {
		
		return type;
	}
	
	
	/**
	 * Returns whether the given index in the workout represents a human rower
	 * 
	 * @param	index	the index to test
	 * @return	<code>true</code> if the rower is human; <code>false</code> otherwise
	 */
	public boolean isHumanRower(int index) {
		
		return rowers[index] instanceof HumanRower;
	}
	
	
	/**
	 * Returns whether the given indexed rower has a shadow
	 * 
	 * @param	index	the index to check
	 * @return	<code>true</code> if the rower has a shadow; <code>false</code> otherwise
	 */
	public boolean hasShadowRower(int index) {
		
		return shadows[index] != null;
	}
	
	
	/**
	 * Returns the number of human rowers
	 * 
	 * @return	the number of human rowers
	 */
	public int numberOfHumanRowers() {
		
		int count = 0;
		
		/* Loop over the rowers and perform the count */
		for (int rowerNo = 0; rowerNo < rowers.length; rowerNo++) {
			
			if (rowers[rowerNo] instanceof HumanRower) {
				count++;
			}
		}
		
		return count;
	}
	
	
	/**
	 * Return the number of rowers associated with the workout.
	 * 
	 * @return the number of rowers
	 */
	public int numberOfRowers() {
		
		if (rowers != null) {
			return rowers.length;
		}
		else {
			return 0;
		}
	}
	
	
	/**
	 * Sets the date from a string representation in the form yyyy-mm-dd-hh:mm:ss
	 * @param date
	 */
	public void setDate(String date) {
		
		String [] dateSections = date.split("-");
		this.date.set( Calendar.YEAR, 2003 );
		this.date.set( Calendar.MONTH, Integer.parseInt(dateSections[1]) - 1 );
		this.date.set( Calendar.DAY_OF_MONTH, Integer.parseInt(dateSections[2]) );
		
		String [] timeSections = dateSections[3].split(":");
		this.date.set( Calendar.HOUR_OF_DAY, Integer.parseInt(timeSections[0]) );
		this.date.set( Calendar.MINUTE, Integer.parseInt(timeSections[1]) );
		this.date.set( Calendar.SECOND, Integer.parseInt(timeSections[2]) );
	}
	
	
	/**
	 * Set the distance of the workout
	 *
	 * @param distance	the distance of the workout
	 */
	public void setDistance(int distance) {
		
		this.distance = distance;
	}
	
	
	/**
	 * Set the show distance
	 * 
	 * @param distance	the show distance
	 */
	public void setShowDistance(int distance) {
		
		this.showDistance = distance;
	}
	
	
	/**
	 * Set the start type
	 * 
	 * @param type	the start type
	 */
	public void setStartType(int type) {
		
		startType = type;
	}
	
	
	/**
	 * Set the time of the workout
	 * 
	 * @param time	the time of the workout
	 */
	public void setTime(int time) {
		
		this.time = time;
	}
	
	
	/* Private Methods */
	
	/**
	 * Increase the size of the rower and shadown array by one
	 */
	private void increaseRowerArrays() {
		
		 if (rowers == null) {
		 	/* Construct the array if they don't already exist */
		 	rowers = new Rower[1];
			shadows = new Rower[1];
		 }
		 else {
		 	/* Store the old rowers and create a new array */
		 	Rower [] tempRowers = rowers;
		 	Rower [] tempShadows = shadows;
		 	rowers = new Rower[tempRowers.length + 1];
			shadows = new Rower[tempRowers.length + 1];
			
			/* Copy the rower information over */
			for (int i = 0; i < tempRowers.length; i++) {
				rowers[i] = tempRowers[i];
				shadows[i] = tempShadows[i];
			}
		 }
	}
}
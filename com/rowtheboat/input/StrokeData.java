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
 * File: StrokeData.java
 * 
 * Date			Version		User		Description
 * 17-Oct-2003	1.0			GeorgeP		Inital version coded
 * 
 */

package com.rowtheboat.input;

/**
 * StrokeData encapsulates the data of a stroke
 * 
 * @author GeorgeP
 */

public class StrokeData {

	/* Class Variables */
	
	/**
	 * The constant representing an unset class variable.
	 */
	public static final int UNSET = -1;				/* The value for an unset variable */
	
	/**
	 * The constant representing the end of a stroke. 
	 */
	public static final int END_OF_STROKE = -2;		/* The value for the end of a stroke */
	
	/**
	 * The constant representing the end of a workout.
	 */
	public static final int END_OF_WORKOUT = -3;	/* The value for the end of a workout */
	
	
	private int status = UNSET;						/* The current ergo status */
	private int strokeRate = UNSET;					/* The current stroke rate */
	private float heartRate = UNSET;				/* The current heart period */
	private float distance = UNSET;					/* The current distance travelled */
	private double power = UNSET;					/* The current stroke pace */
	private float time = UNSET;						/* The current time of the workout */
	
	
	/* Constructor */
	
	/**
	 * Construct a stoke data object with no fields set.
	 */
	public StrokeData() {
		/* Do nohting */
	}
	
	
	/**
	 * Construct the stroke data
	 * 
	 * @param status	the ergo status
	 * @param distance	the distance travelled
	 */
	public StrokeData(int status, float distance) {
		
		this.status = status;
		this.distance = distance;
	}
	
	
	/* Public Methods */
	
	/* Inherited Javadoc */
	public boolean equals(Object o) {
		
		if (o instanceof StrokeData) {
			StrokeData aStroke = (StrokeData) o;
			return (status == aStroke.getStatus() && strokeRate == aStroke.getStrokeRate() &&
					getHeartRate() == aStroke.getHeartRate() && 
					getDistance() == aStroke.getDistance() && getPower() == aStroke.getPower() &&
					getTime() == aStroke.getTime());
		}
		
		return false; 
	}
	
	
	/**
	 * Returns the current 500m split
	 * 
	 * @return the current 500m split; -1 if not set
	 */
	public double get500Split() {
		
		double power = getPower();
		
		/* If the power is zero or -1 then the split is also zero */
		if (power <= 0) {
			
			return 0;
		}
		else {
			
			/* Calculate the 500 split */
			double metersPerSecond = getMetersPerSecond();
			double minutesPer500 = 500 / ( metersPerSecond * 60 );
		
			/* Grab the pre-decimal numbers (no rounding in Java) and convert the rest to seconds*/
			int mins = new Double(minutesPer500).intValue();
		
			/* Must divide by 100 so seconds occur after the decimal point in combined number */
			double seconds = (minutesPer500 - mins) * 60 / 100;
		
			/* Return the split in minutes.seconds form */
			return mins + seconds;
		}
	}
	
	
	/**
	 * Returns the current calories per hour
	 * 
	 * @return calories per hour
	 */
	public double getCaloriesPerHour() {
		
		double power = getPower();
		
		/* If the power is zero, the calories must be zero and not 300 as the rower is not yet 
		 * moving. */
		if (power <= 0) {
			
			return 0; 
		}
		else {
		
			/* Calculate the calories per hour and return it */
			double caloriesPerHour = (power * (4 * 0.8604) ) + 300;
		
			return caloriesPerHour;
		}
	}
	
	
	/**
	 * Returns the distance travelled so far by the rower
	 * 
	 * @return distance; -1 if not set
	 */
	public float getDistance() {
		
		return distance;
	}
	
	
	/**
	 * Returns the current heart rate of the rower
	 * 
	 * @return heart rate; -1 if not set
	 */
	public float getHeartRate() {
		
		return heartRate;
	}
	
	
	/**
	 * Returns the meters travelled per second at the current pace
	 * 
	 * @return	meters per second
	 */
	public double getMetersPerSecond() {
		
		/* Return the meters per second checking for a power of 0 first */
		if (power <= 0) {
			return 0;
		}
		else {
			return Math.pow( (power / 2.8), (1 / 3.0) );
		}
	}
	
	
	/**
	 * Returns the power of the stroke
	 * 
	 * @return power of the stroke; -1 if not set
	 */
	public double getPower() {
		
		return power;
	}
	
	
	/**
	 * Return the current stroke rate
	 * 
	 * @return stroke rate; -1 if not set
	 */
	public int getStrokeRate() {
		 
		 return strokeRate;
	}
	
	
	/**
	 * Returns the current status of the ergo
	 * 
	 * @return status; -1 if not set
	 */
	public int getStatus() {
		
		return status;
	}
	
	
	/**
	 * Return the current time of the row
	 * 
	 * @return time of the row; -1 if not set
	 */
	public float getTime() {
		
		return time;
	}
	
	
	/**
	 * Returns whether the stroke data is the end of a stroke
	 * 
	 * @return	<code>true</code> if the data is from the end of a stroke; <code>false</code> 
	 * 			otherwise
	 */
	public boolean isEndOfStroke() {
		
		return status == END_OF_STROKE;
	}
	
	
	/**
	 * Returns whether the stroke data is the end of a workout
	 * 
	 * @return	<code>true</code> if the data is from the end of a workout; <code>false</code> 
	 * 			otherwise
	 */
	public boolean isEndOfWorkout() {
		
		return status == END_OF_WORKOUT;
	}
	
	
	/**
	 * Set the distance
	 * 
	 * @param distance	the distance travelled
	 */	
	public void setDistance(float distance) {
		
		this.distance = distance;
	}
	
	
	/**
	 * Set the heart period (native data that comes from rowing machine)
	 * 
	 * @param heartPeriod	the heart period
	 */
	public void setHeartPeriod(int heartPeriod) {
	
		if (heartPeriod == 0) {
			this.heartRate = 0;
		}
		else {
			this.heartRate = 576000.0f / heartPeriod;
		}	
	}
	
	
	
	/**
	 * Set the heart rate
	 * 
	 * @param heartRate	the heart rate
	 */
	public void setHeartRate(float heartRate) {
		
		this.heartRate = heartRate;
	}
	
	
	/**
	 * Set the pace data
	 * 
	 * @param strokeRate	the stroke rate
	 * @param strokePace	the stroke pace
	 */
	public void setPaceData(int strokeRate, float strokePace) {
		
		this.strokeRate = strokeRate;
		
		/* If -1 or 0 these should be set.  Else calculate the power */
		if (strokePace > 0) {
			this.power = 2.8 / Math.pow(strokePace, 3);
		}
		else {
			this.power = strokePace;
		}
	}
	
	
	/**
	 * Set the power
	 * 
	 * @param the power to set
	 */
	public void setPower(double power) {
		
		this.power = power;
	}
	
	
	/**
	 * Set the status
	 * 
	 * @param status	the status to set
	 */
	public void setStatus(int status) {
		
		this.status = status;
	}
	
	
	/**
	 * Set the stroke rate
	 * 
	 * @param strokeRate	the stroke rate to set
	 */
	public void setStrokeRate(int strokeRate) {
		
		this.strokeRate = strokeRate;
	}
	
	
	/**
	 * Set the time data
	 * 
	 * @param time	the time passed so far
	 */
	public void setTime(float time) {
		
		this.time = time; 
	}
}
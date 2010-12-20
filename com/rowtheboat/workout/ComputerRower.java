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
 * File: ComputerRower.java
 * 
 * Date			Version		User		Description
 * 22-Nov-2003	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat.workout;

import java.io.File;

/**
 * ComputerRower encapsulates information about a computer rower
 * 
 * @author GeorgeP
 */

public class ComputerRower extends Rower {

	/* Class Variables */
	
	/**
	 * The fixed split computer rower constant
	 */
	public static final int FIXED_SPLIT = 0;		/* A fixed split computer rower */
	
	/**
	 * The variable split computer rower constant
	 */
	public static final int VARIABLE_SPLIT = 1;		/* A variable split computer rower */
	
	private int type;								/* The type of computer rower */
	private float splitToRowAt;						/* The fixed split - for time workouts */
	private float rowTime;							/* The time it should take to row the course */
	private File variableInputFile;					/* The variable input file */
	

	/* Constructor */
	
	/**
	 * Construct a Computer Rower object.  This is used to hold information about the computer
	 * rower and does not provide the concrete funcitonality for the stroke generation.
	 */
	public ComputerRower(int type) {
		
		/* Construct a rower and set class variables */
		super("Computer");
		this.type = type;
	}

	
	/* Public Methods */
	
	/* Inherited Javadoc */
	public String[] getGUIDisplayForm(boolean shadow, int lane) {
		
		/* If shadow add that */
		String type = "";
		String laneString = "";
		if (shadow) {
			type += "Shadow - ";
			laneString = "  " + lane;
		}
		else {
			laneString = lane + "";
		}
		
		/* Determine the computer type */
		if (getType() == ComputerRower.FIXED_SPLIT) {
			type += "Computer Fixed Split";
		}
		else {
			type += "Computer Variable Split";
		}
		
		return new String [] {laneString, getName(), type};
	}
	
	
	/**
	 * Returns the time to row for
	 * 
	 * @return	row time
	 */
	public float getRowTime() {
		
		return rowTime;
	}
	
	
	/**
	 * Returns the split to row at
	 * 
	 * @return	the split (seconds per 500m)
	 */
	public float getSplitToRowAt() {
	
		return splitToRowAt;
	}
	
	
	/**
	 * Returns the type of the computer rower (from above constants)
	 * 
	 * @return	the rower type
	 */
	public int getType() {
	
		return type;
	}


	/**
	 * Returns the variable split xml file
	 * 
	 * @return	an xml file
	 */
	public File getVariableInputFile() {
	
		return variableInputFile;
	}


	/**
	 * Set the time to row for
	 * 
	 * @param	time	the time to row for
	 */
	public void setRowTime(float time) {
	
		rowTime = time;
	}
	
	
	/**
	 * Set the split to row at
	 * 
	 * @param	split	the split in seconds per 500m
	 */
	public void setSplitToRowAt(float split) {
		
		splitToRowAt = split;
	}
	
	
	/**
	 * Set the xml variable split file location
	 * 
	 * @param	file	a string to the xml input file
	 */
	public void setVariableInputFile(File file) {
	
		variableInputFile = file;
	}
}
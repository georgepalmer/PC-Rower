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
 * File: User.java
 * 
 * Date			Version		User		Description
 * 22-Nov-2003	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat.workout;

import java.io.File;

/**
 * UserRower encapsulates the information about a user rower
 * 
 * @author GeorgeP
 */

public class HumanRower extends Rower {

	/* Class variables */

	private int ergoNumber;							/* The users ergo number */
	private boolean logData;						/* Whether to log workout data to file */
	private File filePath;							/* The log file path */


	/* Constructor */
	
	/**
	 * Construct the rower
	 * 
	 * @param name			the name of the rower
	 * @param ergoNumber	the number of the ergo the user is rowing on
	 * @param logData		whether to log the workout data to file
	 */
	public HumanRower(String name, int ergoNumber, boolean logData) {
		
		super(name);
		this.ergoNumber = ergoNumber;
		this.logData = logData;
	}
	
	
	/* Public Methods */	
	
	/**
	 * Returns the users zero indexed ergo number
	 * 
	 * @return zero indexed ergo number
	 */
	public int getErgoNumber() {
		
		return ergoNumber;
	}
	
	
	/* Inherited Javadoc */
	public String[] getGUIDisplayForm(boolean shadow, int lane) {
		
		return new String [] {lane + "", getName(), "Ergo " + (getErgoNumber() + 1)};
	}
	
	
	/**
	 * Returns the file that should be written
	 * 
	 * @return	a file
	 */
	public File getXMLFileName() {
		
		return filePath;
	}
	
	
	/**
	 * Sets the file that should be written to
	 * 
	 * @param file	the file
	 */
	public void setXMLFileName(File file) {
	
		this.filePath = file;
	}
	
	
	/**
	 * Returns whether the rower requires the workout data to be logged
	 * 
	 * @return <code>true</code> if logging is required; <code>false</code> otherwise
	 */
	public boolean shouldLogData() {
		
		return logData;
	}
}
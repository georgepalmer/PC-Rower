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
 * File: Rower.java
 * 
 * Date			Version		User		Description
 * 22-Nov-2003	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat.workout;

/**
 * Rower holds the generic data common to both a user and computer rower
 * 
 * @author GeorgeP
 */

public abstract class Rower {

	/* Class Variables */
	
	private String name;							/* The name of the rower */


	/* Constructor */

	/**
	 * Construct a rower
	 * 
	 * @param name	the rower name
	 */
	public Rower(String name) {
		
		this.name = name;
	}


	/* Public methods */
	
	/**
	 * Return the rower name
	 * 
	 * @param shadow	set if the rower is a shadow 
	 * @return the rower name
	 */
	public String getName() {
		
		return name;
	}


	/**
	 * Returns the data suitable for displaying in the GUI rower table
	 * 
	 * @return	the data for the table
	 */
	public abstract String[] getGUIDisplayForm(boolean shadow, int lane);


	/**
	 * Set the rower name
	 * 
	 * @param name	the rowers name
	 */
	public void setName(String name) {
		
		this.name = name;
	}
}
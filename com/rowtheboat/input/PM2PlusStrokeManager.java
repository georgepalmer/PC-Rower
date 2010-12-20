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
 * File: PM2PlusStrokeHolder.java
 * 
 * Date			Version		User		Description
 * 25-Oct-2003	1.0			GeorgeP		Inital version coded
 * 
 */
 
package com.rowtheboat.input;


/**
 * PM2PlusStrokeHolder is a singleton that holds a stroke as it is being constructed (whilst the 
 * java events are triggering as the serial port data arrives).
 * 
 * @author GeorgeP
 */

public class PM2PlusStrokeManager {

	/* Class Variables */
	
	private StrokeData stroke;						/* The current stroke being constructed */
	private boolean dataProcessed;					/* Whether the data is finished */
	
	private static PM2PlusStrokeManager instance;	/* The self referencing instance */
	
	
	/* Constructor */
	
	/**
	 * Constructs the PM2PlusStrokeHolder instance
	 */
	private PM2PlusStrokeManager() {}


	/* Methods */

	/**
	 * Returns the instance of the class
	 * 
	 * @return the instance of the class
	 */
	public static PM2PlusStrokeManager getInstance() {
		
		if (instance == null) {
			instance = new PM2PlusStrokeManager();
		}
		
		return instance;
	}
	
	
	/**
	 * Returns the current stroke data
	 * 
	 * @return	the stroke data
	 */
	public StrokeData getStrokeData() {
		
		return stroke;
	}
	
	
	/**
	 * Returns whether the data has been successfully processed
	 * 
	 * @return <code>true</code> if the code has been processed; <code>false</code> otherwise
	 */
	public boolean getDataProcessed() {
		
		return dataProcessed;
	}
	
	
	/**
	 * Set the stroke data to be stored within the class
	 * 
	 * @param 	strokeData	the stroke data
	 */
	public void setStrokeData(StrokeData strokeData) {
		
		stroke = strokeData;
	}


	/**
	 * Set whether the data has been processed
	 * 
	 * @param	processed	the boolean to set
	 */
	public void setDataProcessed(boolean processed) {
		
		dataProcessed = processed;
	}
}
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
 * File: Start.java
 * 
 * Date			Version		User		Description
 * 11-Nov-2003	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat.controller;

/**
 * This class is responsible for starting and exiting PC-Rower properly.
 * 
 * @author GeorgeP
 */
public class Start {

	/* Class Variable */
	
	private static boolean cleanUp = false;			/* Clean up the resources.  If not set and the
													   program crashes then don't lose current
													   workout on the PM2+ */

	/* Main Method */
	
	/**
	 * This method starts the program
	 */
	public static void main(String[] args) {
		
		/* Initiate and run the core class, checking for performance criteria */
		Core core = new Core();
		for (int i = 0; i < args.length; i++) {
			if ( args[i].equals("-performance") ) {
				core.writePerformanceFile();
			}
		}
		core.mainProgramLoop();
		
		/* Clean up any resources associated with the workout before exiting */
		if (cleanUp) {
			core.cleanUpWorkout();
		}
	}
	
	
	/**
	 * This method sets the clean up variable 
	 * 
	 * @param clean	whether to clean up the resources
	 */
	public static void setCleanUp(boolean clean) {
		
		cleanUp = clean;
	}
}
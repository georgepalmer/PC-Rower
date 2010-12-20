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
 * File: IInputDevice.java
 * 
 * Date			Version		User		Description
 * 17-Oct-2003	1.0			GeorgeP		Inital version coded
 * 
 */
 
package com.rowtheboat.input;

import com.rowtheboat.workout.Workout;
import com.rowtheboat.controller.IInputOutputDevice;

/**
 * IInputDevice is the interface that all implementations providing data to the program must 
 * conform to. 
 * 
 * @author GeorgeP
 */

public interface IInputDevice extends IInputOutputDevice {

	/**
	 * This cleans up and resources associated with getting data from the ergo.  It is called 
	 * before the program exits or as the input device is changed.
	 */
	public void cleanUp() throws Exception;

	/**
	 * Retrieves the stroke data
	 * 
	 * @param data			the ergo number for a human rower, the time for a computer rower 
	 * @return				StrokeData object
	 * @throws Exception
	 */
	public StrokeData retrieveStrokeData(float data) throws Exception;
	
	/**
	  * Sets up the input device for a workout.
	  * 
	  * @param workout		the workout to setup
	  * @throws Exception
	  */
	public void setupWorkout(Workout workout) throws Exception;
}
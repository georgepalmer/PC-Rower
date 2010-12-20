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
 * File: IGUIOutputDevice.java
 * 
 * Date			Version		User		Description
 * 19-Nov-2003	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat.gui.output;

import com.rowtheboat.output.IOutputDevice;
import com.rowtheboat.controller.StrokeCollection;

/**
 * IGUIOutputDevice is the interface that all implementations displaying data visually through the
 * GUI must conform to.
 * 
 * @author GeorgeP
 */

public interface IGUIOutputDevice extends IOutputDevice {

	/**
	 * An implementation may display for some but not all rowers.  This method allows this choice
	 * to be made by passing an array(the numbers are zero indexed).  The default array is just the
	 * rower zero.
	 * 
	 * @param rowers	the rowers number whose data is to be displayed (0 indexed)
	 */
	public void chooseRowerToDisplay(int [] rowers);


	/**
	 * Make any necessary adjustments if gui is resized.
	 */
	public void guiResized();


	/**
	 * Update the display with the stroke data.
	 * 
	 * @param strokes	the object encapsulating the stroke data
	 */
	public void updateStrokeData(StrokeCollection strokes);
	
	
	/**
	 * The workout has finished so stop logging data and show any results.
	 */
	public void workoutFinished(StrokeCollection strokes);
}
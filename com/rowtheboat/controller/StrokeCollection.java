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
 * File: StrokeCollection.java
 * 
 * Date			Version		User		Description
 * 01-Dec-2003	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat.controller;

import com.rowtheboat.input.StrokeData;

/**
 * StrokeCollection encapsulates the stroke information for all rowers in a workout
 * 
 * @author GeorgeP
 */

public class StrokeCollection {

	/* Class Variables */
	
	private StrokeData [] strokes;					/* The strokes */
	private StrokeData [] shadowStrokes;			/* The shadow strokes */

	
	/* Constructor */
	
	/**
	 * Constructs a StrokeCollection for the given number of rowers
	 * 
	 * @param	numberOfRowers	the number of rowers
	 */
	public StrokeCollection(int numberOfRowers) {
		
		strokes = new StrokeData[numberOfRowers];
		shadowStrokes = new StrokeData[numberOfRowers];
	}
	
	
	/* Public Methods */

	/**
	 * Returns the number of rowers
	 * 
	 * @return	the number of rowers
	 */
	public int getNumberOfRowers() {
		
		return strokes.length;
	}
	
	
	/**
	 * Returns the number of shadow rowers
	 * 
	 * @return	the number of shadow rowers
	 */
	public int getNumberOfShadowRowers() {
		
		return shadowStrokes.length;
	}
	
	
	/**
	 * Returns the strokes for a given rower
	 * 
	 * @param	the index of the rower
	 * @return	a StrokeData object
	 */
	public StrokeData getStroke(int index) {
	
		return strokes[index];
	}
	
	
	/**
	 * Returns the strokes for a given shadow rower
	 * 
	 * @param	the index of the shadow rower
	 * @return	a StrokeData object
	 */
	public StrokeData getShadowStroke(int index) {
	
		return shadowStrokes[index];
	}
	
	
	/**
	 * Sets the index to be the passed stroke
	 * 
	 * @param index		the index
	 * @param stroke	the stroke
	 */
	public void setStroke(int index, StrokeData stroke) {
		
		strokes[index] = stroke;
	}
	
	
	/**
	 * Sets the index to be the passed stroke (for a shadow boat)
	 * 
	 * @param index		the index
	 * @param stroke	the stroke
	 */
	public void setShadowStroke(int index, StrokeData shadowStroke) {
	
		shadowStrokes[index] = shadowStroke;
	}
}
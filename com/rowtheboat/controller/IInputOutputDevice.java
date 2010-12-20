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
 * File: IInputOutputDevice.java
 * 
 * Date			Version		User		Description
 * 22-Jan-2004	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat.controller;

/**
 * IInputOutputDevice is the top level interface for all input and output devices for the program.
 * 
 * @author GeorgeP
 */
public interface IInputOutputDevice {

	/**
	 * Display a count, which is used to do a countdown
	 * 
	 * @param time			the number to display
	 * @throws Exception
	 */
	public void displayCount(byte time) throws Exception;
	
	
	/**
	 * Starts the rowing.
	 * 
	 * @param	startType	the start type to use (get from Workout)
	 * @throws Exception
	 */
	public void startRowing(int startType) throws Exception;
}

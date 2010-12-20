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
 * File: OptionsSingleton.java
 * 
 * Date			Version		User		Description
 * 21-Jan-2004	1.0			GeorgeP		Initial version coded
 * 12-Oct-2004	1.01		GeorgeP		Corrected file location for platform indepedence
 * 15-Oct-2004	1.02		GeorgeP		Added get method for operating system and os dep. font
 * 15-Nov-2004	1.03		GeorgeP		Changed serial port code to search system for ports
 * 10-Dec-2004	1.04		GeorgeP		Add garbage collection variable and options saving
 * 
 */

package com.rowtheboat.gui;

import gnu.io.CommPortIdentifier;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.xml.sax.SAXException;

/**
 * OptionsSingleton handles the program options
 * 
 * @author	George Palmer
 */

public class OptionsSingleton {

	/* Class Variables */
	
	private boolean garbageCollection = false;		/* Whether garbage collection is manual */
	private boolean smoothing = true;				/* Whether smoothing is on */
	private boolean fullStrokeData = true;			/* Whether the full stroke data should be
													   retrieved, i.e. Power and HR as well */
													   
	private byte delay = 5;							/* The delay before racing starts */
	private String serialPort = "";					/* The serial port to use for the PM2+.  
													   Defaults to COM1 as used by Windows. */
	private String icon = "images" + File.separatorChar + "icon.gif";
													/* The icon location */
	private String os = System.getProperty("os.name").toLowerCase();
													/* The operating system */
	private static final String OPTIONS_FILE_NAME = "options.xml";
													/* The options file name */
	private File optionsFile;						/* The options file full path */
														
	private static OptionsSingleton singleton;		/* Self reference for singleton */
	
	/**
	 * The constant that represents the Windows operating system 
	 */
	public static final int WINDOWS = 1;
	
	/**
	 * The constant that represents the Linux operating system 
	 */
	public static final int LINUX = 2;
	
	
	/**
	 * The application author :)
	 */
	public static final String APP_AUTHOR = "George Palmer";
	
	/**
	 * The application name constant
	 */
	public static final String APP_NAME = "PC-Rower";
	
	/**
	 * The application version constant
	 */
	public static final String APP_VERSION = "1.04";
		
	
	/* Constructor */
	
	/**
	 * A private constructor to ensure that only one instance is created.
	 */
	private OptionsSingleton() {
		
		/* Retrieve the serial ports and set the first as standard */
		Enumeration ports = CommPortIdentifier.getPortIdentifiers();
		serialPort = ((CommPortIdentifier) ports.nextElement()).getName();
		
		/* Find the current location and use this to create the optionsFile.  This resolves issues
		 * with jar files */
		String currentDir = "";
		try {
			currentDir = (new File(".")).getCanonicalPath() + File.separator;
		} catch (IOException e1) {
			/* Nothing we can do here.  This means the optionsFile will just be the options file
			 * name, which will still work in most cases but in jar files on linux */
			 e1.printStackTrace();
		}
		optionsFile = new File(currentDir + OPTIONS_FILE_NAME);
		
		/* Read in existing options, if they exist */
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(optionsFile);
			
			/* Get the root element */
			Element root = document.getRootElement();
		
			/* Update the standard options */
			Element standardElement = root.element("Standard");				
			this.setFullStrokeData( 
				Boolean.valueOf(standardElement.elementText("FullStrokeData")).booleanValue() );
			this.setBoatSmoothing( 
				Boolean.valueOf(standardElement.elementText("BoatSmoothing")).booleanValue() );
			
			/* Update the input options */
			Element inputElement = root.element("Input");
			String portFromFile = inputElement.elementText("SerialPort");
			
			/* Check the port is valid, if not then default is used */
			Enumeration possiblePorts = CommPortIdentifier.getPortIdentifiers();
			while (possiblePorts.hasMoreElements()) {
				if ( ((CommPortIdentifier) possiblePorts.nextElement()).
					getName().equals(portFromFile) ) {
					setSerialPort(portFromFile);
				}
			}
		
			/* Update the race options */
			Element raceElement = root.element("Race");
			this.setDelay( Byte.parseByte(raceElement.elementText("Countdown")) );
		} 
		catch (Exception e) {
			/* Do nothing as file not present - thus defaults will be used */
		}
	}
	
	
	/* Public Methods */
	
	/**
	 * Returns the OptionsSingleton instance
	 * 
	 * @return	An optionsSingleton instance
	 */
	public static OptionsSingleton getInstance() {
		
		if (singleton == null) {
			singleton = new OptionsSingleton();
		}
		
		return singleton;
	}
	
	
	/**
	 * Returns if boat smoothing is on
	 * 
	 * @return	<code>true</code> if switched on; <code>false</code> otherwise
	 */
	public boolean getBoatSmoothing() {
		
		return smoothing;
	}
	
	
	/**
	 * Return the race start delay (in seconds)
	 * 
	 * @return	the delay
	 */
	public byte getDelay() {
	
		return delay;
	}
	
	
	/**
	 * Returns whether the full stroke data should be retrieved.  Full stroke data includes heart
	 * rate and pace data in addition to distance and time.
	 * 
	 * @return	<code>true</code> if full data should be retrieved; <code>false</code> otherwise
	 */
	public boolean getFullStrokeData() {
	
		return fullStrokeData;
	}
	
	
	/**
	 * Returns whether manual garbage collection is switched on
	 * 
	 * @return	<code>true</code> if garbage collection is on; <code>false</code> otherwise
	 */
	public boolean getGarbageCollection() {
		
		return garbageCollection;
	}
	
	
	/**
	 * Returns the icon location
	 * 
	 * @return	the icon location
	 */
	public String getIconLocation() {
		
		return icon;
	}
	
	
	/**
	 * Return the operating system
	 * 
	 * @return a contant representing the operating system
	 */
	public int getOS() {
		
		if (os.startsWith("windows")) {		
			return WINDOWS;
		}
		else {
			return LINUX;
		}
	}
	
	
	/**
	 * Returns the correct font for the platform
	 * 
	 * @return a string representing the font
	 */
	public String getOSDependentFont() {
		
		String font = "Arial";

		if (getOS() == LINUX) {
			font = "Courier";
		}
		
		return font;
	}
	
	
	/**
	 * Return the possible serial ports
	 * 
	 * @return	an enumeration of ports
	 */
	public Enumeration getPossibleSerialPorts() {
		
		return CommPortIdentifier.getPortIdentifiers();
	}
	
	
	/**
	 * Returns the serial port string - eg "COM1" on a windows box
	 * 
	 * @return	a string representing the serial port
	 */
	public String getSerialPort() {
				
		return serialPort;
	}
	
	/**
	 * Return the position of the selected serial port in the serial port enumeration
	 * 
	 * @return an int for the position; -1 if no match
	 */
	public int getSerialPortPosition() {
	
		int count = 0;
		Enumeration ports = CommPortIdentifier.getPortIdentifiers();
		
		/* Whilst there are more elements check for a match, and return the count if there is one */
		while (ports.hasMoreElements()) {
			if (((CommPortIdentifier) ports.nextElement()).getName().equals(getSerialPort())) {
				return count;	
			}
			
			count++;
		}
		
		/* This shouldn't happen */
		return -1;
	}
	
	
	/**
	 * This static method returns the software version without a point.  It is intended for use by
	 * the ant build script only
	 * 
	 * @return	a pointless(!) software version 
	 */
	public static String getVersion() {
		
		int pointIndex = APP_VERSION.indexOf('.');
		String appVersion = APP_VERSION.substring(0, pointIndex) 
			+ APP_VERSION.substring(pointIndex + 1); 
		
		return appVersion;
	}
	
	
	/**
	 * This method will collect the garbage if the garbage collect variable is set
	 *
	 */
	public void garbageCollect() {
		
		if (garbageCollection) {
			System.gc();
		}
	}
	
	
	/**
	 * This method saves the options to the options.xml file
	 */
	public void saveOptions() throws SAXException, IOException {
		
		/* Start the document */
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter( new FileWriter( optionsFile ), format );
		writer.startDocument();
		
		/* Add the main options section */
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement( "Options" );
		
		/* Standard options */
		DefaultElement standardElement = new DefaultElement( "Standard" );
		standardElement.addElement( "FullStrokeData" ).addText( getFullStrokeData() + "" );
		standardElement.addElement( "BoatSmoothing" ).addText( getBoatSmoothing() + "" );
		root.add( standardElement );
		
		/* Input options */
		DefaultElement inputElement = new DefaultElement( "Input" );
		inputElement.addElement( "SerialPort" ).addText( getSerialPort() );
		root.add( inputElement );
		
		/* Race options */
		DefaultElement raceElement = new DefaultElement( "Race" );
		raceElement.addElement( "Countdown" ).addText( getDelay() + "" );
		root.add( raceElement );
		
		/* End the document */
		writer.write(root);
		writer.endDocument();
		writer.close();	
	}
	

	/**
	 * Set boat smoothing
	 * 
	 * @param	boatSmoothing	whether boat smoothing is on
	 */
	public void setBoatSmoothing(boolean smoothing) {
		
		this.smoothing = smoothing;
	}
	
	
	/**
	 * Set the race delay
	 * 
	 * @param	delay	the race delay in seconds
	 */
	public void setDelay(byte delay) {
		
		this.delay = delay;
	}
	

	/**
	 * Set whether full stroke data should be switched on.  Full stroke data includes heart
	 * rate and pace data in addition to distance and time.
	 * 
	 * @param full	whether full stroke data should be switched on
	 */
	public void setFullStrokeData(boolean full) {
	
		fullStrokeData = full;
	}
	
	
	/**
	 * Sets whether manual garbage collection is on or not
	 * 
	 * @param manualGarbage	whether manual garbage collection is on
	 */
	public void setGarbageCollection(boolean manualGarbage) {
	
		garbageCollection = manualGarbage;
	}
	
	
	/**
	 * Sets the serial port string
	 * 
	 * @param	portString	the serial port string
	 */
	public void setSerialPort(String portString) {

		this.serialPort = portString;
	}
}
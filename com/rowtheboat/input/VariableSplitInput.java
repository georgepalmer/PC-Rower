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
 * File: VariableSplitInput.java
 * 
 * Date			Version		User		Description
 * 28-Nov-2003	1.0			GeorgeP		Initial version coded
 * 02-Nov-2004	1.03		GeorgeP		Added apache xerces parser for native build
 * 
 */
 
package com.rowtheboat.input;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.rowtheboat.workout.ComputerRower;
import com.rowtheboat.workout.Workout;

/**
 * VariableSplitInput uses an xml file to create a variable pace boat against which to race
 * 
 * @author GeorgeP
 */

public class VariableSplitInput implements IInputDevice {

	/* Class Variables */
	
	private Element root;							/* The root element */
	private float [] times;							/* The stroke times */
	private int [] timeIndexes;						/* The indexes for the times */
	private int lastStrokeIndex = 0;				/* The last stroke index */ 

	/* This is needed to correct files that are compiled in gcj */
	private static final Class dom4j = org.dom4j.io.aelfred.SAXDriver.class;
	

	/* Constructor */

	/**
	 * Construct the variable split class
	 * 
	 * @param	file	the file from which to pull the xml workout data
	 */
	public VariableSplitInput(File file) throws MalformedURLException, DocumentException {
		
		/* Initialise the arrays */
		times = new float[10];
		timeIndexes = new int[10];
		
		/* Create the reader */
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);

		/* Get the root element */
		root = document.getRootElement();
		
		/* Create the rower */
		for ( Iterator i = root.elementIterator( "Rower" ); i.hasNext(); ) {
			
			Element rowerElement = (Element) i.next();
			ComputerRower rower = new ComputerRower(ComputerRower.VARIABLE_SPLIT);
			rower.setName( rowerElement.elementText("Name") );
		}

		/* Create the workout */
		for ( Iterator i = root.elementIterator( "Details" ); i.hasNext(); ) {
			
			Element wElement = (Element) i.next();
			Workout workout = new Workout( Integer.parseInt(wElement.elementText("Type")) );
			workout.setDate( wElement.elementText("Date") );
			if ( workout.getType() == Workout.DISTANCE_WORKOUT ) {
				workout.setDistance( Integer.parseInt(wElement.elementText("Distance")) );
			}
			else {
				workout.setTime( Integer.parseInt(wElement.elementText("Time")) );
			}
		}
		
		/* Populate the stroke times and time index arrays */
		int arrayIndex = 0;
		for ( int i = 0, size = root.nodeCount(); i < size; i++ ) {
			Node node = root.node(i);
			if ( node instanceof Element && node.getName().equals("Stroke" )) {
				Element el = (Element) node;
				times[arrayIndex] = Float.parseFloat( el.elementText("Time") );
				timeIndexes[arrayIndex] = i;
				arrayIndex++;
				if (arrayIndex == times.length) {
					increaseArraySizes();
				}
			}
		}
	}
	
	
	/* Public Methods */
	
	/* Inherited Javadoc */
	public void cleanUp() throws Exception {
		
	}
	
	
	/* Inherited Javadoc */
	public void displayCount(byte time) {}
	

	/* Inherited Javadoc */
	public StrokeData retrieveStrokeData(float time) throws Exception {
		
		boolean nextStrokeExists = true;
		
		while(true) {
			if (lastStrokeIndex != 0 && times[lastStrokeIndex] == 0.0) {
				/* If this isn't the first stroke index and the distance is 0.0 then the last
				 * distance was the final one because any uninitialised floats are 0.0  Thus
				 * decrease the index and break */
				lastStrokeIndex--;
				nextStrokeExists = false;
				break;
			}
			if (time >= times[lastStrokeIndex]) {
				/* If the time required is greater or equal than the stroke then proceed  */
				
				if ( lastStrokeIndex < times.length - 1 ) {
					/* If the last stroke index is less than the length - 1 then proceed and test
					 * whether the time required is less than the next stroke.  If so break as we
					 * have the correct stroke index.  Otherwise fall out of if statements,
					 * increase index and try again. */
					if (time < times[lastStrokeIndex + 1]) {
						break;
					}
				}
				
				/* Increment the index if not broken out already (i.e. found the required stroke
				 * index) */
				lastStrokeIndex++;
			}
		}
		
		/* Create the stroke */
		StrokeData stroke = new StrokeData();
		Element strokeEl = (Element) root.node(timeIndexes[lastStrokeIndex]);
		
		/* If there's another stroke available create more accurate distances and times */
		if (nextStrokeExists) {
			float time1 = times[lastStrokeIndex];
			float time2 = times[lastStrokeIndex + 1];
			float dist1 = Float.parseFloat( strokeEl.elementText("Distance") );
			Element e = (Element) root.node(timeIndexes[lastStrokeIndex + 1]);
			float dist2 = Float.parseFloat( e.elementText("Distance") );
			double mPerS = (dist2 - dist1) / (time2 - time1);
			stroke.setDistance( (float) (((time - time1) * mPerS) + dist1) );
			stroke.setTime( time );
		}
		else {
			stroke.setDistance( Float.parseFloat( strokeEl.elementText("Distance") ) );
			stroke.setTime( times[lastStrokeIndex] );
		}
		
		/* Check to see whether full stroke information is available */
		double split = Double.parseDouble( strokeEl.elementText("Split") );
		if (split != 0.0) {
			/* Full stroke data was switched on */
			stroke.setPower( Double.parseDouble( strokeEl.elementText("Power") ) );
			stroke.setStrokeRate( Integer.parseInt( strokeEl.elementText("SPM") ) );
			stroke.setHeartRate( Float.parseFloat( strokeEl.elementText("HR") ) );
		}
		
		/* Return the stroke */
		return stroke;
	}
	
	
	/* Inherited Javadoc */
	public void setupWorkout(Workout workout) throws Exception {
		/* Nothing required */		
	}
	
	
	/* Inherited Javadoc */
	public void startRowing(int startType) {
		/* Nothing required in this implementation */		
	}
	
	
	/* Private Methods */
	
	/**
	 * Doubles the times array size
	 */
	private void increaseArraySizes() {
		
		/* Create a temp array to store the times data */
		float [] temp = times;
		int [] indexTemps = timeIndexes;
		times = new float[times.length * 2];
		timeIndexes = new int[timeIndexes.length * 2];
		
		/* Copy over the data */
		for (int i = 0; i < temp.length; i++) {
			times[i] = temp[i];
			timeIndexes[i] = indexTemps[i];
		}
	}
}
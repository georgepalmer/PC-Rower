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
 * File: XMLWorkoutWriter.java
 * 
 * Date			Version		User		Description
 * 28-Nov-2003	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat.output;

import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.xml.sax.SAXException;

import com.rowtheboat.input.StrokeData;
import com.rowtheboat.workout.HumanRower;
import com.rowtheboat.workout.Rower;
import com.rowtheboat.workout.Workout;

/**
 * XMLWorkoutWriter handles the writing of a workout to an XML file.
 * 
 * @author GeorgeP
 */
public class XMLWorkoutWriter implements IFileOutputDevice {

	/* Class Variables */
	private XMLWriter writer;						/* The XML writer */
	private float distance;							/* The workout distance */
	private Element root;							/* The root element */

	/* Constructor */
	
	/**
	 * Constructs an instance of the class to perform the XML workout writing.
	 * 
	 * @param rower		the rower who is to have their data stored
	 * @param workout	the workout
	 */
	public XMLWorkoutWriter(HumanRower rower, Workout workout) 
			throws IOException, SAXException { 
		
		/* Set the distance class variable */
		this.distance = workout.getDistance();
		
		/* Intansiate the writer */
		OutputFormat format = OutputFormat.createPrettyPrint();
		this.writer = new XMLWriter( new FileWriter( rower.getXMLFileName() ), format	);
		writer.startDocument();
		
		Document document = DocumentHelper.createDocument();
		root = document.addElement( "Session" );

		/* Write the rower and workout info */
		createRowerXML(rower);
        createWorkoutXML(workout);
        
        /* Add an intial stroke */
		StrokeData stroke = new StrokeData();
		stroke.setDistance(0);
		stroke.setTime(0);
        updateStrokeData( stroke );
	}


	/* Public Methods */
	
	/* Inherited javadoc */
	public void displayCount(byte time) throws Exception {
		
		/* Not required */	
	}
	
	
	/* Inherited Javadoc */
	public void startRowing(int startType) {
		
		/* Not required in this implementation */		
	}
	
	
	/* Inherited javadoc */
	public void updateStrokeData(StrokeData stroke) throws IOException {
		
		/* Write the stroke */
		DefaultElement strokeElement = new DefaultElement( "Stroke" );
		strokeElement.addElement( "Time" )
			.addText( stroke.getTime() + "" );
		strokeElement.addElement( "Distance" )
			.addText( stroke.getDistance() + "" );
		strokeElement.addElement( "Split" )
			.addText( stroke.get500Split() + "" );
		strokeElement.addElement( "Power" )
			.addText( stroke.getPower() + "" );
		strokeElement.addElement( "SPM" )
			.addText( stroke.getStrokeRate() + "" );
		strokeElement.addElement( "CaloriesPerHour" )
			.addText( stroke.getCaloriesPerHour() + "" );
		strokeElement.addElement( "HR" )
			.addText( stroke.getHeartRate() + "" );
		
		root.add(strokeElement);
	}


	/* Inherited javadoc */
	public void workoutAborted() throws SAXException, IOException {
		
		closeWriter();
	}


	/* Inherited javadoc */
	public void workoutFinished(StrokeData stroke) throws SAXException, IOException {
		
		/* Create the stroke element */
		DefaultElement strokeElement = new DefaultElement( "Stroke" );
		
		/* Check whether the workout is a distance one */
		if (distance == -1) {
			updateStrokeData(stroke);
		}
		else {
			/* On the final stroke the exact time is returned by the distance field */
			strokeElement.addElement( "Time" )
				.addText( stroke.getDistance() + "" );
			strokeElement.addElement( "Distance" )
				.addText( distance + "" );
			strokeElement.addElement( "Split" )
				.addText( stroke.get500Split() + "" );
			strokeElement.addElement( "Power" )
				.addText( stroke.getPower() + "" );
			strokeElement.addElement( "SPM" )
				.addText( stroke.getStrokeRate() + "" );
			strokeElement.addElement( "Calories" )
				.addText( stroke.getCaloriesPerHour() + "" );
			strokeElement.addElement( "HR" )
				.addText( stroke.getHeartRate() + "" );
				
			/* Write the stroke element */
			root.add(strokeElement);
		}
		
		closeWriter();
	}
	
	
	/* Private Methods */
	
	/**
	 * Close the writer so no more data can be logged
	 */
	private void closeWriter() throws SAXException, IOException {
	
		/* Close the writer */
		writer.write(root);
		writer.endDocument();
		writer.close();	
	}
	
	
	/**
	 * Write the rower xml information
	 * 
	 * @param rower	the rower to write information about
	 */
	private void createRowerXML(Rower rower) {
		
		/* Create the rower element */
		DefaultElement rowerElement = new DefaultElement( "Rower" );
		rowerElement.addElement( "Name" )
			.addText( rower.getName() );
		
		root.add( rowerElement );
	}


	/**
	 * Write the workout xml information
	 * 
	 * @param workout	the workout to write information about
	 */
	private void createWorkoutXML(Workout workout) {		
	
		/* Create the workout element */
		DefaultElement detailsElement = new DefaultElement( "Details" );
		detailsElement.addElement( "Date" )
			.addText( workout.getDate() );
		detailsElement.addElement( "Type" )
			.addText( workout.getType() + "" );
			
		/* Add either a time or distance element depending on the workout type */
		if (workout.getType() == Workout.DISTANCE_WORKOUT) {
			detailsElement.addElement( "Distance" )
				.addText( workout.getDistance() + "" );	
		}
		else {
			/* Must be a timed workout */
			detailsElement.addElement( "Time" )
				.addText( workout.getTime() + "" );	
		}
		
		root.add( detailsElement );
	}
}
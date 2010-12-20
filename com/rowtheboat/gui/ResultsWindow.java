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
 * File: ResultsWindow.java
 * 
 * Date			Version		User		Description
 * 19-Jan-2004	1.0			GeorgeP		Initial version coded
 * 15-Oct-2004	1.02		GeorgeP		Made alterations for generic window sizing and seperator
 * 
 */
 
package com.rowtheboat.gui;

import com.rowtheboat.input.StrokeData;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

import com.rowtheboat.controller.StrokeCollection;

import com.rowtheboat.workout.Workout;

/**
 * ResultsWindow displays the results of a race
 * 
 * @author GeorgeP
 */

public class ResultsWindow {
	
	/* Class Variables */
	
	private final Shell shell;						/* The shell for the window */
	
	/* Boat colours */
	private final RGB BOAT0_COLOUR = new RGB( 255, 176, 5 );
	private final RGB BOAT1_COLOUR = new RGB( 222, 24, 231 );
	private final RGB BOAT2_COLOUR = new RGB( 24, 214, 24 );
	private final RGB BOAT3_COLOUR = new RGB( 248, 251, 29 );
	private final RGB BOAT4_COLOUR = new RGB( 227, 13, 13 );

	/* Image locations */
	private static final String BOAT0_IMAGE = 
		"images" + File.separatorChar + "boatOrange.gif";	/* The orange boat image */
	private static final String BOAT1_IMAGE = 
		"images" + File.separatorChar + "boatPurple.gif";	/* The purple boat image */
	private static final String BOAT2_IMAGE = 
		"images" + File.separatorChar + "boatGreen.gif";	/* The green boat image */
	private static final String BOAT3_IMAGE = 
		"images" + File.separatorChar + "boatYellow.gif";	/* The yellow boat image */
	private static final String BOAT4_IMAGE = 
		"images" + File.separatorChar + "boatRed.gif";		/* The red boat image */
	
	
	/* Constructor */
	
	/**
	 * Construct the Results Window
	 */
	public ResultsWindow(Workout workout, StrokeCollection lastStrokes) {
		
		/* Open the shell */
		shell = new Shell();
		shell.setText("Results Window");
		
		/* Setup the icon */
		Image image = 
			new Image( shell.getDisplay(), OptionsSingleton.getInstance().getIconLocation() );
		shell.setImage( image );
		image.dispose();
		
		/* Setup the contents */
		setupContents(workout, lastStrokes);
	}

	
	/* Public Methods */
	
	/**
	 * Return the windows shell
	 * 
	 * @return	the result window shell
	 */
	public Shell getShell() {
		
		return shell;
	}
	
	
	/* Private Methods */
	
	/**
	 * Draw the boat
	 */
	public void drawBoat(final int boatNo) {
		
		/* Create the label */
		Label lane = new Label(shell, SWT.NONE);
		lane.setLayoutData( GUIUtil.getStandardGridData(100, 40) );
		
		/* Determine the image and background colour to use.  
		 * This depends on the type of boat and the lane that it is in. */
		String boatImageString;
		RGB colour;
		switch(boatNo) {
			case 0 : {
				boatImageString = BOAT0_IMAGE;
				colour = BOAT0_COLOUR;
				break;
				}
			case 1 : {
				boatImageString = BOAT1_IMAGE;
				colour = BOAT1_COLOUR;
				break;
			}
			case 2 : {
				boatImageString = BOAT2_IMAGE;
				colour = BOAT2_COLOUR;
				break;
			}
			case 3 : {
				boatImageString = BOAT3_IMAGE;
				colour = BOAT3_COLOUR;
				break;
			}
			case 4 : {
				boatImageString = BOAT4_IMAGE;
				colour = BOAT4_COLOUR;
				break;
			}
			default : {
				boatImageString = BOAT0_IMAGE;
				colour = BOAT0_COLOUR;
				break;
			}
		}
		final String bis = boatImageString;
		final RGB finalColour = colour;
					
		lane.addPaintListener(new PaintListener() { 
			public void paintControl(PaintEvent e) { 
				/* Draw image and dispose of resources */
				Image image = new Image(shell.getDisplay(), bis);
				e.gc.drawImage(image, 0, 0);
				image.dispose();
					
				/* Create font and dispose of resources */
				Font font = new Font(shell.getDisplay(), 
					new FontData("Arial", 24, SWT.BOLD));
				e.gc.setFont( font );
				font.dispose();
					
				/* Set and dispose of the background colour */
				Color backgroundColor = new Color (shell.getDisplay(), finalColour);
				e.gc.setBackground(backgroundColor);
				backgroundColor.dispose();
						
				/* Draw the text */
				e.gc.drawText(boatNo + 1 + "", 10, 1);
			}
		});
	}
	
	
	/**
	 * Setup the shell contents
	 * 
	 * @param workout		the workout
	 * @param lastStrokes	the last stroke data
	 */
	private void setupContents(Workout workout, StrokeCollection lastStrokes) {
		
		/* Add the layout */
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		shell.setLayout(gridLayout);
		
		final Label label = new Label(shell, SWT.NONE);
		label.setText("Summary of results:");
		label.setLayoutData( GUIUtil.getHorizontalSpanGridData(4) );
		
		if (workout.getType() == Workout.DISTANCE_WORKOUT) {
			setupDistanceWorkout(workout, lastStrokes);
		}
		else {
			setupTimeWorkout(workout, lastStrokes);
		}
		
		Label seperator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData seperatorGridData = GUIUtil.getHorizontalSpanGridData(4);
		seperatorGridData.widthHint = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT).x - 10;
		seperator.setLayoutData( seperatorGridData );
		
		Button okButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		GridData okButtonLData = GUIUtil.getHorizontalSpanGridData(4);
		okButtonLData.horizontalAlignment = GridData.END;
		okButton.setLayoutData(okButtonLData);
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		
		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	
	/**
	 * Show the distance results
	 * 
	 * @param workout		the workout
	 * @param lastStrokes	the last stroke data
	 */
	private void setupDistanceWorkout (Workout workout, StrokeCollection lastStrokes) {
		
		/* Keep an array of the rowers and whether they've finished or not */
		ArrayList rowerTimes = new ArrayList();
		boolean [] accessDistance = new boolean[workout.numberOfRowers()];
		
		/* Find the times of the boats */
		for (int i = 0; i < workout.numberOfRowers(); i++) {
			
			StrokeData stroke = lastStrokes.getStroke(i);
			if (stroke.isEndOfWorkout()) {
				/* Note when a boat finishes the distance field is the finish time */
				if (workout.isHumanRower(i)) {
					rowerTimes.add( new Float(stroke.getDistance()) );
					accessDistance[i] = true;
				}
				else {
					rowerTimes.add( new Float(stroke.getTime()) );
					accessDistance[i] = false;
				}
			}
			else {
				rowerTimes.add( new Float(stroke.getTime()) );
				accessDistance[i] = false;
			}
		}
		
		/* Sort the rower times */
		Collections.sort(rowerTimes);
		
		/* Create the columns */
		Label positionLabel = new Label(shell, SWT.NONE);
		positionLabel.setText("Position");
		
		Label laneLabel = new Label(shell, SWT.NONE);
		laneLabel.setText("Lane");
		
		Label nameLabel = new Label(shell, SWT.NONE);
		nameLabel.setText("Name");
		
		Label timeLabel = new Label(shell, SWT.NONE);
		timeLabel.setText("Time");
		
		/* Loop over the finish positions */
		for (int i = 0; i < workout.numberOfRowers(); i++) {
			
			Label position = new Label(shell, SWT.NONE);
			position.setText(i + 1 + "");
			
			/* Loop over the rowers to find the right rower */
			for (int j = 0; j < workout.numberOfRowers(); j++) {
				
				/* Find info */
				StrokeData stroke = lastStrokes.getStroke(j);
				float comparable;
				if (accessDistance[j]) {
					comparable = stroke.getDistance();
				}
				else {
					comparable = stroke.getTime();
				}
				
				/* Create the table entry */
				if ( ((Float)rowerTimes.get(i)).floatValue() == comparable ) {
					
					drawBoat(j);
					
					Label name = new Label(shell, SWT.NONE);
					name.setText(workout.getRower(j).getName());
					
					Label time = new Label(shell, SWT.NONE);
					time.setText( ((Float)rowerTimes.get(i)).floatValue() + "" );
					
					break;
				}
			}
		}
	}
	
	
	/**
	  * Show the time results
	  * 
	  * @param workout		the workout
	  * @param lastStrokes	the last stroke data
	  */
	private void setupTimeWorkout(Workout workout, StrokeCollection lastStrokes) {
		
		/* Keep an array of the rowers and whether they've finished or not */
		ArrayList rowerDistances = new ArrayList();
				
		/* Find the distances of the boats */
		for (int i = 0; i < workout.numberOfRowers(); i++) {
			
			rowerDistances.add( new Float(lastStrokes.getStroke(i).getDistance()) );
		}
				
		/* Sort the rower times */
		Collections.sort(rowerDistances);
				
		/* Create the columns */
		Label positionLabel = new Label(shell, SWT.NONE);
		positionLabel.setText("Position");
				
		Label laneLabel = new Label(shell, SWT.NONE);
		laneLabel.setText("Lane");
				
		Label nameLabel = new Label(shell, SWT.NONE);
		nameLabel.setText("Name");
				
		Label distanceLabel = new Label(shell, SWT.NONE);
		distanceLabel.setText("Distance");
				
		/* Loop over the finish positions */
		for (int i = 0; i < workout.numberOfRowers(); i++) {
					
			Label position = new Label(shell, SWT.NONE);
			position.setText(i + 1 + "");
					
			/* Loop over the rowers to find the right rower */
			for (int j = 0; j < workout.numberOfRowers(); j++) {
				
				StrokeData stroke = lastStrokes.getStroke(j);
						
				/* Create the table entry */
				int special = workout.numberOfRowers() - 1 - i;
				if ( ((Float)rowerDistances.get(special)).floatValue() == stroke.getDistance() ) {
							
					drawBoat(j);
							
					Label name = new Label(shell, SWT.NONE);
					name.setText(workout.getRower(j).getName());
							
					Label time = new Label(shell, SWT.NONE);
					time.setText( ((Float)rowerDistances.get(special)).floatValue() + "" );
							
					break;
				}
			}
		}
	}
}
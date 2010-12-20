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
 * File: River.java
 * 
 * Date			Version		User		Description
 * 19-Nov-2003	1.0			GeorgeP		Initial version coded
 * 15-Oct-2004	1.02		GeorgeP		Corrected fonts and sizes for Linux
 * 10-Dec-2004	1.04		GeorgeP		Garbage collect changes made
 * 
 */

package com.rowtheboat.gui.output;

import java.io.File;

import com.rowtheboat.gui.GUIUtil;
import com.rowtheboat.gui.OptionsSingleton;
import com.rowtheboat.input.StrokeData;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.rowtheboat.controller.StrokeCollection;

import com.rowtheboat.workout.Workout;

/**
 * This class handles the River output
 * 
 * @author GeorgeP
 */

public final class River extends Canvas implements IGUIOutputDevice {

	/* Class Variables */
	
	private Display display;						/* The display */
	private Canvas masterCanvas;					/* The canvas onto which everything is drawn */
	private Canvas topGrass;						/* The top grass canvas */
	private Canvas bottomGrass;						/* The bottom grass canvas */
	private Canvas water;							/* The river canvas */
	private Canvas [] boats;						/* The boats */
	private Canvas [] shadowBoats;					/* The shadow boats */
	private Canvas [] rowerNames;					/* The rower names canvas */
	private Canvas [] signs;						/* The signs canvas */
	private int [] signWidths;						/* The sign widths */
	
	private Workout workout;						/* The workout */
	private int height;								/* The canvas height */
	private int width;								/* The canvas width */
	private int showDistance;						/* The scale to be used - either 100, 250,
													   500 (default) or 1000m */
	private int middleOfWater;						/* The middle of the water */
	private double pixelsPerMeter;					/* The number of pixels per meter */
	private int signCount;							/* The number of the sign */
	private int lastSignMove = 0;					/* The last amount the signs were moved */

	/* Pixel dimensions */
	private static final int BOAT_LENGTH = 100;		/* The length of a boat */
	private static final int BOAT_MARGIN = 5;		/* The boat margin */
	private static final int GRASS_HEIGHT = 40;		/* The height of the grass */
	private static final int LANE_HEIGHT = 50;		/* The width of a river lane */
	private static final int LANE_SEPERATOR_HEIGHT = 2;
													/* The height of the lane seperators */
	private static final int LANE_SEPERATOR_WIDTH = 10;
													/* The width of the lane seperator */
	private static final int LINE_WIDTH = 2;		/* The width of the start/finish line */ 
	private static final int NUMBER_OF_WATER_COLUMNS = 3;
													/* The number of columns on the water canvas */
	private static final int ROWER_NAME_WIDTH = 250;/* The width of the rower name */
	private static final int ROWER_NAME_HEIGHT = 40;/* The height of the rower name */
	private static final int SIGN_MARGIN = 5;		/* The sign margin */
	private static final int SIGN_TEXT_INDENT = 6;	/* The sign text indent */
	private static final int SIGN_WIDTH = 61;		/* The width of the sign */
	private static final int START_BLOCK_SIZE = BOAT_LENGTH + BOAT_MARGIN + 1;
													/* The size of the start block */
	private final int WATER_OFF_SCREEN_WIDTH;		/* The off screen width required.  This is 
													   calculated at run time */ 

	/* Colours */
	private final RGB RIVER_BACKGROUND_COLOUR = new RGB( 76, 84, 236 );
	private final RGB GRASS_BACKGROUND_COLOUR = new RGB( 0, 160, 0 );
	private final RGB ROWER_NAME_COLOUR = new RGB( 255, 255, 255 );
	private final RGB SIGN_BACKGROUND_COLOUR = new RGB( 255, 255, 255 );
	private final RGB BOAT0_COLOUR = new RGB( 255, 176, 5 );
	private final RGB BOAT1_COLOUR = new RGB( 222, 24, 231 );
	private final RGB BOAT2_COLOUR = new RGB( 24, 214, 24 );
	private final RGB BOAT3_COLOUR = new RGB( 248, 251, 29 );
	private final RGB BOAT4_COLOUR = new RGB( 227, 13, 13 );
	private final RGB SHADOW_BOAT_COLOUR = new RGB( 160, 160, 160 );
	
	/* Image locations */
	private static final String BOAT0_IMAGE = 
		"images" + File.separatorChar + "boatOrange.gif";					/* The orange boat image */
	private static final String BOAT1_IMAGE = 
		"images" + File.separatorChar + "boatPurple.gif";					/* The purple boat image */
	private static final String BOAT2_IMAGE = 
		"images" + File.separatorChar + "boatGreen.gif";					/* The green boat image */
	private static final String BOAT3_IMAGE = 
		"images" + File.separatorChar + "boatYellow.gif";					/* The yellow boat image */
	private static final String BOAT4_IMAGE = 
		"images" + File.separatorChar + "boatRed.gif";						/* The red boat image */
	private static final String SHADOW_BOAT_IMAGE = 
		"images" + File.separatorChar + "boatGrey.gif";						/* The shadow boat image */	
	private static final String LANE_SEPERATOR_IMAGE =
		"images" + File.separatorChar + "laneSplit.gif";					/* The lane seperator image */
	private static final String SIGN_BOTTOM_IMAGE = 
		"images" + File.separatorChar + "signBottom.gif";					/* The bottom sign image */
	private static final String SIGN_TOP_IMAGE = 
		"images" + File.separatorChar + "signTop.gif";						/* The top sign image */
	private static final String START_LINE_IMAGE =
		"images" + File.separatorChar + "startLine.gif";				/* The start/finish line image */
	

	/* Constructor */

	/**
	 * Constructs the River instance
	 * 
	 * @param parent		the parent of the canvas
	 * @param workout		the workout
	 * @param showDistance	the distance to show on the screen
	 */
	public River(Composite parent, Workout workout, int showDistance) {
		
		/* Call the super constructor and initialise the class variables */
		super(parent, SWT.NONE);
		
		this.display = this.getDisplay();
		this.workout = workout;
		this.boats = new Canvas[workout.numberOfRowers()];
		this.shadowBoats = new Canvas[workout.numberOfRowers()];
		this.rowerNames = new Canvas[workout.numberOfRowers()];
		this.signs = new Canvas[7];
		this.signCount = 0;
		this.signWidths = new int[5];

		/* Determine the scale to be used */
		switch (showDistance) {
			case 100 :
			case 250 :
			case 500 :
			case 1000 : 
				this.showDistance = showDistance;
				break;
			default: this.showDistance = 500;
		}
		
		
		/* Setup the layout of the canvas that is this class */
		this.setLayout( GUIUtil.getStandardGridLayout(0,0,0,0) );
		setupCanvasDimensions();

		/* Initialise and set up layout of the master canvas */
		masterCanvas = new Canvas(this, SWT.NONE);
		masterCanvas.setLayout( GUIUtil.getStandardGridLayout(0,0,0,0) );
		
		/* Set the layout data - no idea why this has to be height - 1 !!! */
		masterCanvas.setLayoutData( GUIUtil.getStandardGridData(width, height - 1) );
		

		/* Set the off screen limit to be the width of the screen plus the boat plus the boat 
		 * margin plus the finish line width and one (to move the whole line off the screen).  This
		 * is because this is how much the screen will move to the left (first the boat and the
		 * boat margin, then the width of the screen and finally the line + 1). */
		WATER_OFF_SCREEN_WIDTH = width + BOAT_LENGTH + BOAT_MARGIN + LINE_WIDTH + 1;
		
		/* Setup the parts to the river */
		setupTopGrass();
		setupWater();
		setupBottomGrass();
		setupBoatsAndRowerNames();
	}
	
	
	/* Public Methods */
	
	/* Inherited javadoc */
	public void chooseRowerToDisplay(int [] rowers) {
	
		/* Not relevant to this concrete class */
	}
	
	
	/* Inherited javadoc */
	public void displayCount(byte time) throws Exception {
		
		/* Not required as handled by the mainWindow class */	
	}
	
	
	/* Inherited javadoc */
	public void guiResized() {
		
		/* Unsufficient time to implement this */
	}
	
	
	/* Inherited Javadoc */
	public void startRowing(int startType) {
		
		/* Nothing required in this implementation */		
	}
	
	
	/* Inherited javadoc */
	public void updateStrokeData(StrokeCollection strokes) {
		
		/* The distance to move the leading human boat */
		int moveDistance = -1;
		
		/* The total distance travelled by the leading human rower */
		int leadingHumanDistance = -1;
		
		/* The flag that indicates whether the rower names should be moved */
		boolean nameMove = false;
		
		/* Find the leading human rower */		
		float maxStrokeDistance = findMaximumStrokeDistance(strokes);
		
		/* Use the leading human rower's distance to find the number of pixels this equates to */
		leadingHumanDistance = (int) (maxStrokeDistance * pixelsPerMeter + 0.5);
		
		
		/* See if the leading human rower has reached the middle of the water */
		if ( leadingHumanDistance > middleOfWater ) {
			
			/* Calculate the amount to move the boats */
			int amountToMove = leadingHumanDistance - middleOfWater;
			
			/* Move the signs the required amount */
			moveSigns(amountToMove - lastSignMove);
			lastSignMove = amountToMove;

			/* When the boats are in the middle start moving the water until start line has gone
			 * off the left hand side of the screen */
			if (water.getLocation().x > START_BLOCK_SIZE * -1 ) {
				
				moveStartLineWater(amountToMove);
								
				/* Set the moveDistance and put name moving on */
				moveDistance = leadingHumanDistance;
				nameMove = true;
			}
			
			/* Check (and if applicable move) the finish line onto the water */
			int distanceToMove = moveFinishLineWater(leadingHumanDistance, amountToMove);
			if (distanceToMove != -1) {
				moveDistance = distanceToMove;
				nameMove = true; 
			}
			
			/* If none of the statements above are entered then there is no movement in the
			 * leading boat although the leading boat itself could have changed. */
		}
		else {
			/* If the leading human rower has not reached the middle of the screen then set the 
			 * move distance to be the amount they have travelled so far */
			
			moveDistance = leadingHumanDistance;
		}
	
		/* Update boats positions */
		updateBoatPositions(strokes, leadingHumanDistance, moveDistance, nameMove);
	}
	
	
	/* Inherited Javadoc */
	public void workoutAborted() {
		
		/* Do nothing */
	}
	
	
	/* Inherited Javadoc */
	public void workoutFinished(StrokeCollection strokes) {
	
		/* Pass this responsability onto the updateStrokeData method which deals with the issues
		 * of a stroke being the last and the possibility of a workout being a distance one. */
		updateStrokeData(strokes);
	}


	/* Private Methods */
	
	/**
	 * Draw a boat
	 * 
	 * @param index	the index of the boat
	 */
	private void drawBoat(final int boatNo, final boolean shadowBoat) {
		
		/* Setup a canvas variable that can be referenced independent of the boat type */
		Canvas boat;
		if (shadowBoat) {
			shadowBoats[boatNo] = new Canvas(water, SWT.NONE);
			boat = shadowBoats[boatNo];
		}
		else {
			boats[boatNo] = new Canvas(water, SWT.NONE);
			boat = boats[boatNo];
		}
		
		/* Initialise the canvas and set the background */
		Color backgroundColour = new Color (this.getDisplay(), RIVER_BACKGROUND_COLOUR);
		boat.setBackground( backgroundColour );
		backgroundColour.dispose();

		/* Setup the layout data, which includes indenting the boat by the boat margin */
		GridData boatGD = GUIUtil.getStandardGridData(BOAT_LENGTH, LANE_HEIGHT);
		if (shadowBoat) {
			boatGD.horizontalIndent = -BOAT_LENGTH;
		}
		else {
			boatGD.horizontalIndent = BOAT_MARGIN;	
		}
		boat.setLayoutData( boatGD );

		/* Determine the image and background colour to use.  This depends on the type of boat and
		 * the lane that it is in. */
		String boatImageString;
		RGB colour;
		if (shadowBoat) {
			boatImageString = SHADOW_BOAT_IMAGE;
			colour = SHADOW_BOAT_COLOUR;
		}
		else {
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
		}
		final String bis = boatImageString;
		final RGB finalColour = colour;

		/* Draw the boat */
		boat.addPaintListener(new PaintListener() { 
			public void paintControl(PaintEvent e) { 
				/* Draw image and dispose of resources */
				Image image = new Image(display, bis);
				e.gc.drawImage(image, 0, BOAT_MARGIN);
				image.dispose();
				
				/* Create font */
				FontData fontData = new FontData(OptionsSingleton.getInstance().getOSDependentFont(),
					24, SWT.BOLD);
				/* Linux needs a bigger font */
				if (OptionsSingleton.getInstance().getOS() == OptionsSingleton.LINUX) {
					fontData.setHeight(26);
				}
				Font font = new Font(display, fontData);
				e.gc.setFont( font );
				
				/* If the boat is not a shadow then draw on the boat number */
				if (!shadowBoat) {
					/* Set and dispose of the background colour */
					Color backgroundColor = new Color (masterCanvas.getDisplay(), finalColour);
					e.gc.setBackground(backgroundColor);
					backgroundColor.dispose();
					
					/* Draw the text */
					e.gc.drawText(boatNo + 1 + "", 10, 6);
				}
				
				/* Dispose of font */
				font.dispose();
			}
		});
	}
	
	
	/**
	 * Draw a rower name
	 * 
	 * @param index	the index of the rower
	 */
	private void drawRowerName(final int index) {
		
		/* Initialise the canvas and set the background (disposing of the colour) */
		rowerNames[index] = new Canvas(water, SWT.NONE);
		Color backgroundColour = new Color (this.getDisplay(), RIVER_BACKGROUND_COLOUR);
		rowerNames[index].setBackground( backgroundColour );
		backgroundColour.dispose();
		
		/* Setup the layout data, which includes indenting the name */
		GridData nameGridData = GUIUtil.getStandardGridData(ROWER_NAME_WIDTH, ROWER_NAME_HEIGHT);
		nameGridData.horizontalIndent = width - ROWER_NAME_WIDTH - BOAT_LENGTH - BOAT_MARGIN;
		rowerNames[index].setLayoutData(nameGridData);

		/* Draw the name */
		rowerNames[index].addPaintListener(new PaintListener() { 
			public void paintControl(PaintEvent e) { 
				
				/* Set and dispose of the colour */
				Color foregroundColour = 
					new Color (masterCanvas.getDisplay(), ROWER_NAME_COLOUR);
				e.gc.setForeground(foregroundColour);
				foregroundColour.dispose();
				
				/* Set the font and dispose of the resources */
				FontData fontData = new FontData(OptionsSingleton.getInstance().getOSDependentFont(),
					24, SWT.BOLD);
				/* Linux requires a bigger font */
				if (OptionsSingleton.getInstance().getOS() == OptionsSingleton.LINUX) {
					fontData.setHeight(26);
				}
				Font font = new Font(display, fontData);
				e.gc.setFont( font );
				
				e.gc.drawText(workout.getRower(index).getName(),0,0);
				
				font.dispose();
			}
		});
	}
	
	
	/**
	 * Draws the next sign in the sign queue
	 * 
	 * @param	distance	the distance that will have been travelled when the sign is reached.
	 * 						The method determines what to display based on the workout type.
	 */
	private void drawSign(final int distance) {
	
		/* Determine the distance string to use based on the workout type */
		final String distanceString;
		if (workout.getType() == Workout.DISTANCE_WORKOUT) {
			/* If a distance workout, then the distance should decrease. */
			distanceString = (workout.getDistance() - distance) + "";
		}
		else {
			/* Else use the distance */
			distanceString = distance + "";
		}
	
		/* Draw the sign */
		signs[signCount % 7].addPaintListener(new PaintListener() { 
			public void paintControl(PaintEvent e) { 
			
				/* Load the sign image.  Set the background colour, font and write the distance. */
				Color signBackground = new Color( masterCanvas.getDisplay(), SIGN_BACKGROUND_COLOUR );
				e.gc.setBackground(signBackground);
				signBackground.dispose();
				
				/* Create font and dispose of resources */
				Font font = new Font(display, new FontData(OptionsSingleton.getInstance().getOSDependentFont(),
					16, SWT.BOLD));
				e.gc.setFont(font);
				
				/* Draw sign and dispose of resources */
				Image sign = new Image(masterCanvas.getDisplay(), SIGN_TOP_IMAGE); 
				e.gc.drawImage(sign, 0, SIGN_MARGIN);
				sign.dispose();
				
				/* Draw text and dispose of font */
				e.gc.drawText(distanceString, SIGN_TEXT_INDENT, SIGN_MARGIN);
				font.dispose();
			}
		});
	}


	/**
	 * Find the maximum distance attained by a human rower 
	 * 
	 * @param strokes	the stroke collection
	 * @return	the maximum distance found
	 */
	private float findMaximumStrokeDistance(StrokeCollection strokes) {
	
		/* Initialise the maximum stroke distance */	
		float maxStrokeDistance = -1;
		
		/* Loop over the human rowers and check for a new maximum stroke distance */
		for (int i = 0; i < strokes.getNumberOfRowers(); i++) {
			if ( workout.isHumanRower(i) ) {
				float strokeDistance = strokes.getStroke(i).getDistance();
				boolean finished = strokes.getStroke(i).isEndOfWorkout();
		
				/* If the workout has finished and it's a distance workout then set the distance
				 * to be that of the workout so the boats stop at the line. */
				if ( !finished && strokeDistance >  maxStrokeDistance ) {
					maxStrokeDistance = strokeDistance;
				}
				if (finished && workout.getType() == Workout.DISTANCE_WORKOUT) {
					maxStrokeDistance = workout.getDistance();
				}
			}
		}
		
		/* Return the distance */
		return maxStrokeDistance;
	}

	
	/**
	 * Move the boat along the river
	 * 
	 * @param boat						the boat canvas to move
	 * @param stroke					the stroke for the boat
	 * @param leadingHumanDistance		the distance of the leading rower
	 * @param moveDistance				the distance to move the boat
	 * @param laneHeight				the y position of the canvas
	 */
	private void moveBoat(Canvas boat, StrokeData stroke, int leadingHumanDistance, 
		int moveDistance, int laneHeight) {

		/* See if the rower has finished */
		if ( stroke.isEndOfWorkout() && workout.getType() == Workout.DISTANCE_WORKOUT ) {
			boat.setLocation(width + BOAT_LENGTH + BOAT_MARGIN + 2 + 1, laneHeight);
		}
		else {
	
			/* If not work out where the boat should be */
			int diff = (int) (stroke.getDistance() * pixelsPerMeter + 0.5)
						 - leadingHumanDistance;
	
			/* This is when boats are in the middle of the screen */
			if (moveDistance == -1) {
				moveDistance = middleOfWater + START_BLOCK_SIZE;
			}
	
			boat.setLocation(moveDistance + BOAT_MARGIN + diff, laneHeight);
		}
	}
	
	
	/**
	 * Move the water containing the finish line if indeed this section of water is required
	 * 
	 * @param leadingHumanDistance	the leading distance by a human rower
	 * @param amountToMove			the amount to move the water (from the start position)
	 * @return	the amount to move the boats by
	 */
	private int moveFinishLineWater(int leadingHumanDistance, int amountToMove) {
		
		/* If there's a finish to the workout then check to see if it should be moved along by 
		 * calculating the difference. */
		double distanceToFinishLine = (workout.getDistance() * pixelsPerMeter);
		double factor = (width - BOAT_LENGTH - (2 * BOAT_MARGIN)) / 2;//oh bugger should .0 on end
		int difference = (int) ( leadingHumanDistance + factor + 1.5 - distanceToFinishLine );

		/* Make movements of river once again for end */
		int magicDistance = -1;
		if ( workout.getType() == Workout.DISTANCE_WORKOUT && difference > 0 ) {
	
			/* Move the water along */
			magicDistance = difference + START_BLOCK_SIZE;
			water.setLocation( magicDistance * -1 , GRASS_HEIGHT );
			
			/* Add the middle of the water to this - note this must be done after the water has
			 * been moved. */
			magicDistance += middleOfWater;
		}
				
		/* Return the distance */
		return magicDistance;
	}
	
	
	/**
	 * Move the rower name
	 * 
	 * @param name			the name canvas to move
	 * @param moveDistance	the distance to move
	 * @param laneHeight	the y position of the canvas
	 */
	private void moveName(Canvas name, int moveDistance, int laneHeight) {
		
		/* Move the rower names the required amount */
		int baseNamePosition = (width + BOAT_LENGTH - 2 * ROWER_NAME_WIDTH - 1) / 2;
		name.setLocation
			(moveDistance + baseNamePosition, laneHeight + (LANE_HEIGHT - ROWER_NAME_HEIGHT) / 2);
	}
	
	
	/**
	 * Move the signs
	 * 
	 * @param	moveAmount	the amount to move the signs (relative amount from current position)
	 */
	private void moveSigns(int moveAmount) {
		
		/* Loop over the signs, moving them and checking for any that are no longer needed */
		for (int i = 0; i < signs.length; i++) {
			
			/* Move the canvas */
			Canvas canvas = signs[i];
			canvas.setLocation(canvas.getLocation().x - moveAmount, SIGN_MARGIN);
			
			/* Check for any signs that are no longer on the screen */
			if (canvas.getLocation().x + canvas.getSize().x < 0) {
				/* Determine the point where the sign queue finishes */
				int xMoveDistance;
				if (i == 0) {
					xMoveDistance = signs[signs.length - 1].getLocation().x
								  + signs[signs.length - 1].getSize().x - moveAmount;
				}
				else {
					xMoveDistance = signs[i - 1].getLocation().x + signs[i - 1].getSize().x;
				}
				/* Move the canvas to the end of the queue */
				canvas.setLocation(xMoveDistance, SIGN_MARGIN);
				drawSign(signCount * showDistance / 5);
				signCount++;
			}
		}
	}
	
	
	/**
	 * Move the water containing the start line
	 * 
	 * @param	amountToMove	the amount to move the water (from the starting position)
	 */
	private void moveStartLineWater(int amountToMove) {
		
		/* Set moveWaterAmount to amountToMove and ensure that the moveWaterAmount does not
		 * exceed the start block size (distance to move start line off screen). */
		int moveWaterAmount = amountToMove;
		if (amountToMove > START_BLOCK_SIZE) {
			moveWaterAmount = START_BLOCK_SIZE;
		}

		/* Move the water the required amount */
		water.setLocation(moveWaterAmount * -1, GRASS_HEIGHT);
	}

	
	/**
	 * Setup the boats and rower names 
	 */
	private void setupBoatsAndRowerNames() {
	
		/* Initialise the river background colour */
		Color riverBackground = new Color( this.getDisplay(), RIVER_BACKGROUND_COLOUR );
	
		/* Loop over the number of rowers */
		for (int i = 0; i < workout.numberOfRowers(); i++) {
			
			/* Draw the normal boat */
			drawBoat(i, false);
			
			/* If there's a shadow boat draw that, otherwise create a filler block */
			if ( workout.hasShadowRower(i) ) {
				drawBoat(i, true);
			}
			else {
				Canvas filler = new Canvas(water, SWT.NONE);
				filler.setBackground(riverBackground);
				GridData fillerGridData = GUIUtil.getStandardGridData(BOAT_LENGTH, LANE_HEIGHT);
				fillerGridData.horizontalIndent = -BOAT_LENGTH;
				filler.setLayoutData(fillerGridData);
			}
			
			/* Draw the rower name */
			drawRowerName(i);

			/* If the current rower isn't the last, draw a lane seperator */
			if (i != workout.numberOfRowers() - 1) {

				/* Initialise the canvas and set the background colour */
				Canvas laneSeperator = new Canvas(water, SWT.NONE);
				laneSeperator.setBackground(riverBackground);
				
				/* Setup the grid data */
				GridData laneGridData = GUIUtil.getStandardGridData
					(width + WATER_OFF_SCREEN_WIDTH, LANE_SEPERATOR_HEIGHT);
				laneGridData.horizontalSpan = NUMBER_OF_WATER_COLUMNS;
				laneSeperator.setLayoutData(laneGridData);

				/* Draw the seperator along the lane */
				laneSeperator.addPaintListener(new PaintListener() { 
					public void paintControl(PaintEvent e) { 
						/* Load the image */
						Image image = new Image(display, LANE_SEPERATOR_IMAGE);
						
						/* Loop the length of the water */
						int limit = (width + WATER_OFF_SCREEN_WIDTH) / LANE_SEPERATOR_WIDTH;
						for (int i = 0; i < limit; i++) {
							e.gc.drawImage(image, i * LANE_SEPERATOR_WIDTH, 0);
						}
						
						/* Dispose of image resources */
						image.dispose();
					}
				});
			}
		}
		
		/* Dispose of the background colour */
		riverBackground.dispose();
	}
	
	
	/**
	 * Setup the grass at the bottom of the water
	 */
	private void setupBottomGrass() {
		
		/* Setup the bottom grass */
		bottomGrass = new Canvas(masterCanvas, SWT.NONE);
		bottomGrass.setLayoutData(GUIUtil.getStandardGridData(width, GRASS_HEIGHT));
		
		Color grassBackground = new Color( this.getDisplay(), GRASS_BACKGROUND_COLOUR );
		bottomGrass.setBackground( grassBackground );
		grassBackground.dispose();
	}
	
	
	/**
	 * This methods works out the scale to be used to move the boats given the show distance and
	 * the size of the canvas.  It should be recalled if either of these values is altered.
	 */
	private void setupCanvasDimensions() {
		
		/* Grab the parent size and use this to set the size for this canvas */
		Point parentSize = this.getParent().getSize();
		parentSize.x = parentSize.x - 18;
		parentSize.y = (GRASS_HEIGHT * 2) + 
					   (LANE_HEIGHT + LANE_SEPERATOR_HEIGHT) * workout.numberOfRowers();
		
		this.setSize(parentSize);


		/* Check the canvas is big enough */
		Point size = this.getSize();
		 
		if (size.x < 500) {
			size.x = 500;
			this.setSize(size);
		}

		/* Set the width and height */
		width = this.getSize().x;
		height = this.getSize().y;
		
		/* The middle of the water */
		middleOfWater = (width - BOAT_LENGTH) / 2;

		/* Determine the number of pixels per meter */
		pixelsPerMeter = (new Integer(width)).doubleValue() / showDistance;
		
		/* Calculate the sign widths */
		for (int i = 0; i < signWidths.length; i++) {
			signWidths[i] = (int) (((i+1) * width / 5.0) + 0.5);
		}
		/* Remove the previous distance as the calculation above is total distance */
		for (int i = signWidths.length - 1; i > 0; i--) {
			signWidths[i] -= signWidths[i - 1];
		}
	}
	
	
	/**
	 * Setup the grass to the top of the river
	 */
	private void setupTopGrass() {
		
		/* Initialise the background colour */
		Color grassBackground = new Color( this.getDisplay(), GRASS_BACKGROUND_COLOUR );
		
		/* Initialise and setup the canvas (background colout, layout and grid data) */
		topGrass = new Canvas(masterCanvas, SWT.NONE);
		GridLayout grassGridLayout = new GridLayout();
		grassGridLayout.numColumns = 8;
		grassGridLayout.horizontalSpacing = 0;
		grassGridLayout.marginWidth = 0;
		topGrass.setLayout(grassGridLayout);
		topGrass.setBackground(grassBackground);
		topGrass.setLayoutData(
			GUIUtil.getStandardGridData(width + WATER_OFF_SCREEN_WIDTH, GRASS_HEIGHT) );
		 
		/* Create the signs */
		for (int i = 0; i < signs.length; i++) {
			
			/* Construct the sign canvas */
			signs[signCount % 7] = new Canvas(topGrass, SWT.NONE);
			signs[signCount % 7].setBackground(grassBackground);
		
			/* Set the canvas to the right size */
			int grassUnitSize = signWidths[signCount % 5];
			GridData grassUnitGridData = new GridData(); 
			grassUnitGridData.widthHint = grassUnitSize;
			/* Indent the first sign due to the start block */
			if (i == 0) {
				grassUnitGridData.horizontalIndent = 
					BOAT_LENGTH + BOAT_MARGIN - (SIGN_WIDTH / 2) - 1;
			}
			signs[signCount % 7].setLayoutData(grassUnitGridData);
			
			/* Draw the sign and increment the count */
			drawSign( signCount * showDistance / 5 );
			signCount++;
		}
		
		/* Dipose of the colour resources */
		grassBackground.dispose();
	}
	
	
	/**
	 * Setups the water canvas
	 */
	private void setupWater() {
		
		/* Initialise the water and its background */
		water = new Canvas(masterCanvas, SWT.NONE);
		Color waterBackground = new Color( this.getDisplay(), RIVER_BACKGROUND_COLOUR );
		water.setBackground(waterBackground);
		waterBackground.dispose();
		
		/* Add the layout data */
		GridLayout waterLayout = GUIUtil.getStandardGridLayout(0,0,0,0);
		waterLayout.numColumns = NUMBER_OF_WATER_COLUMNS;
		water.setLayout(waterLayout);
		
		/* Add the grid data */
		GridData waterGridData = new GridData();
		waterGridData.widthHint = width + WATER_OFF_SCREEN_WIDTH;
		water.setLayoutData(waterGridData);
		
		/* Add the start and finish line */
		water.addPaintListener(new PaintListener() { 
			public void paintControl(PaintEvent e) { 
				
				/* Load the image */
				Image image = new Image(display, START_LINE_IMAGE);
				
				for (int i = 0; i < workout.numberOfRowers(); i++) {
					int yPosition = i * (LANE_HEIGHT + LANE_SEPERATOR_HEIGHT);
					e.gc.drawImage(image, BOAT_LENGTH + BOAT_MARGIN - 1, yPosition);
					e.gc.drawImage(image, width + START_BLOCK_SIZE, yPosition);
				}
				
				/* Dispose of image resources */
				image.dispose();
			}
		});
	}
	
	
	/**
	 * Update the boat positions
	 * 
	 * @param strokes				the boat strokes data
	 * @param leadingHumanDistance	the leading human distance
	 * @param moveDistance			the distance that the river has moved
	 * @param nameMove				whether the names should be moved
	 */
	private void updateBoatPositions
		(StrokeCollection strokes, int leadingHumanDistance, int moveDistance, boolean nameMove) {
			
		/* If there is movement required */
		for (int i = 0; i < strokes.getNumberOfRowers(); i++) {
	
			/* Calculate the lane position */
			int y = ((LANE_HEIGHT + LANE_SEPERATOR_HEIGHT) * i);
	
			/* Move the boats */
			moveBoat(boats[i], strokes.getStroke(i), leadingHumanDistance, moveDistance, y);
		
			/* If a shadow exists then move it */
			if ( workout.hasShadowRower(i) ) {
				moveBoat(shadowBoats[i], strokes.getShadowStroke(i), leadingHumanDistance,
						 moveDistance, y);
			}
		
			/* See if the names need moving. */
			if (nameMove) {
				moveName(rowerNames[i], moveDistance, y);
			}
		}
	
	
		/* Important this is called so boat movements are updated */
		this.update();
		
		/* A garbage collection is used so that the memory usage associated with an update is kept
		 * down. */
		 OptionsSingleton.getInstance().garbageCollect();
	}
}
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
 * File: NewWorkoutWindow.java
 * 
 * Date			Version		User		Description
 * 11-Nov-2003	1.0			GeorgeP		Initial version coded
 * 15-Oct-2004	1.02		GeorgeP		Made alterations for generic window sizing.  Sorted layout
 * 										issues.
 * 
 */

package com.rowtheboat.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.SWT;

import com.rowtheboat.workout.ComputerRower;
import com.rowtheboat.workout.HumanRower;
import com.rowtheboat.workout.Rower;
import com.rowtheboat.workout.Workout;

import com.rowtheboat.controller.Core;


/**
 * This is the class that provides the new workout window
 * 
 * @author George Palmer
 */

public class NewWorkoutWindow {
	
	/* Class Variables */
	
	private Shell shell;							/* The shell */
	private Core controller;						/* The controller */
	private AddRowerWindow rowerWindow;				/* The rower window */
	private ArrayList rowers;						/* The rowers */
	private ArrayList shadows;						/* The shadows */
	
	private Button addRowerButton;
	private Button addShadowButton;
	private Button cancelButton;
	private Button deleteRowerButton;
	private Button distanceButton;
	private Button downButton;
	private Button okButton;
	private Button timeButton;
	private Button upButton;
	private Combo distanceCombo;
	private Combo startTypeCombo;
	private Label metersLabel;
	private Label minutesLabel;
	private Label showDistanceLabel;
	private Label startType;
	private Label workoutLabel;
	private Table rowerTable;
	private Text timeField;
	private Text distanceField;
	
	
	/* Constructor */
	
	/**
	 * Construct the new workout window
	 */
	public NewWorkoutWindow(Shell parent, Core core) {
		
		/* Initialise class variables */
		this.controller = core;
		rowers = new ArrayList();
		shadows = new ArrayList();
		
		/* Create the shell */
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		shell.setText("New Workout");
		
		GridLayout thisLayout = new GridLayout(7, false);
		shell.setLayout(thisLayout);
		
		/* Setup the icon */
		Image image = 
			new Image( shell.getDisplay(), OptionsSingleton.getInstance().getIconLocation() );
		shell.setImage( image );
		image.dispose();
		
		/* Setup the contents */
		setupContents();
		
		/* Set the size of the window */
		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	
	/* Public Methods */
	
	/**
	 * Called by the AddRowerWindow when a rower is added
	 */
	public void addRower(boolean shadow) {
		
		Rower rower;
		String name = rowerWindow.getRowerName();
		switch (rowerWindow.getRowerType()) {
			case 1 : {
				try {
					/* See if the rower requires logging */
					File logDataFile = rowerWindow.getXMLSaveFile();
					boolean log = logDataFile != null;
					
					/* Create the rower */
					rower = new HumanRower(name, rowerWindow.getErgoNumber(), log);
					if (log) {
						((HumanRower) rower).setXMLFileName(logDataFile);
					}
				} catch (IOException e) {
					// Would have been caught by the error checking so this shouldn't occur
					rower = new HumanRower(name, rowerWindow.getErgoNumber(), false);
				}
				
				break;
			}
			case 2 : {
				/* Create the rower */
				rower = new ComputerRower(ComputerRower.FIXED_SPLIT);
				ComputerRower compRower = (ComputerRower) rower;
				compRower.setName(name);
				
				/* Add the split or row time */
				if (getWorkoutType() == Workout.DISTANCE_WORKOUT) {
					compRower.setRowTime( rowerWindow.getSplitOrTime() );
				}
				else {
					compRower.setSplitToRowAt( rowerWindow.getSplitOrTime() );
				}
				
				break;
			}
			default : {
				/* Create the rower - actually case 3 */
				rower = new ComputerRower(ComputerRower.VARIABLE_SPLIT);
				ComputerRower compRower = (ComputerRower) rower;
				compRower.setName(name);
				compRower.setVariableInputFile( rowerWindow.getXMLReadFile() );
				break;
			}
		}
		
		/* Add the rower to the right collection */
		if (shadow) {
			
			/* Add the shadow if possible (ie not a shadow selected) */
			TableItem [] row = rowerTable.getSelection();
			String lane;
			
			/* If more than one selection or no selection use the last rower */
			if (row.length != 1) {
				row = new TableItem [] {rowerTable.getItem( rowerTable.getItemCount() - 1 )};
			}
			
			/* Check the lane isn't a shadow */
			lane = row[0].getText(0);
			if (!lane.startsWith(" ")) {
				int index = Integer.parseInt(lane) - 1;
	
				/* Remove the null and add the new shadow */
				shadows.remove(index);
				shadows.add(index, rower);
			}
		}
		else {
			/* Add the rower and add a null shadow - this will be overwritten if a shadow is added
			 * at a later point */
			rowers.add(rower);
			shadows.add(null);
		}
		
		updateRowerTable();
	}
	
	
	/**
	 * Get the workout distance
	 * 
	 * @return	the workout distance
	 */
	public int getDistance() {
		
		return Integer.parseInt(distanceField.getText());
	}

	
	/**
	 * Return the rowers
	 * 
	 * @return	an array of rowers
	 */
	public Object [] getRowers() {
		
		return rowers.toArray();
	}
	
	
	/**
	 * Return the shadows
	 * 
	 * @return	an array of rowers
	 */
	public Object [] getShadows() {
		
		return shadows.toArray();
	}
	
		
	/**
	 * Return the shell
	 * 
	 * @return	the shell
	 */
	public Shell getShell() {
		
		return shell;
	}
	
	
	/**
	 * Return the show distance
	 * 
	 * @return	the show distance
	 */
	public int getShowDistance() {
		
		return Integer.parseInt( distanceCombo.getText() );
	}
	
	
	/**
	 * Return the start type for the workout
	 * 
	 * @return	the start type as a workout constant
	 */
	public int getStartType() {
		
		if (startTypeCombo.getSelectionIndex() == 0) {
			return Workout.START_AFTER_COUNT;
		}
		else {
			return Workout.START_ON_STROKE;
		}
	}
	
	
	/**
	 * Get the workout time in seconds
	 * 
	 * @return	the workout time; -1 if this undeterminable
	 */
	public int getTime() {
	
		/* Split the time field */
		String [] time = timeField.getText().split(":");
		
		/* Check the length and calculate the time */
		if (time.length == 2) {
			int mins = Integer.parseInt(time[0]);
			int secs = Integer.parseInt(time[1]);
			
			return mins * 60 + secs;
		}
		else {
			return -1;
		}
	}
	
	
	/**
	 * Return the workout type
	 * 
	 * @return	the relevant Workout constant
	 */
	public byte getWorkoutType() {
		
		/* Check the workout type and return the appropriate Workout constant */
		if ( distanceButton.getSelection() ) {
			
			return Workout.DISTANCE_WORKOUT;
		}
		else {
			
			return Workout.TIME_WORKOUT;
		}
	}
	
	
	/* Private Methods */
	
	/**
	 * Checks for rowers before changing the workout type and warns that any existing rowers will
	 * be lost.
	 * 
	 * @return <code>true</code> if the user wishes to change despite the warning;
	 * 		   <code>false</code> otherwise
	 */
	private boolean checkForRowers() {
		
		/* If there are no rowers return true */
		if (rowers.size() > 0) {
			
			/* Display the message box */
			MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
			box.setText("Warning");
			box.setMessage("Changing the workout type will remove all rowers.  Contine?");
			
			/* Check the return type and return accordingly */
			if (box.open() == SWT.CANCEL) {
				return false;
			}
			else {
				return true;
			}
		}
		
		return true;
	}
	
	
	/**
	* Initializes the GUI
	*/
	private void setupContents() {
		
		final NewWorkoutWindow thisClass = this;
		
		workoutLabel = new Label(shell, SWT.NONE);
		workoutLabel.setText("Please select the workout information:");
		workoutLabel.setLayoutData( GUIUtil.getHorizontalSpanGridData(7) );
		
		distanceButton = new Button(shell, SWT.RADIO | SWT.LEFT);
		distanceButton.setLayoutData( GUIUtil.getHorizontalSpanGridData(3) );
		distanceButton.setText("Distance");
		distanceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/* If they want to change then remove the rowers */
				distanceField.setEnabled(true);
				timeField.setEnabled(false);
				for (int i = 0; i < rowers.size(); i++) {
					rowers.remove(i);
					shadows.remove(i);	
				}
				updateRowerTable();
			}
		});
		distanceButton.setSelection(true);
		
		timeButton = new Button(shell, SWT.RADIO | SWT.LEFT);
		timeButton.setLayoutData( GUIUtil.getHorizontalSpanGridData(4) );
		timeButton.setText("Time");
		timeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/* If they want to change then remove the rowers */
				distanceField.setEnabled(false);
				timeField.setEnabled(true);
				for (int i = 0; i < rowers.size(); i++) {
					rowers.remove(i);
					shadows.remove(i);	
				}
				updateRowerTable();
			}
		});
		
		distanceField = new Text(shell, SWT.BORDER);
		distanceField.setLayoutData( GUIUtil.getHorizontalSpanGridData(1, 43) );
		distanceField.setText("0");
		
		metersLabel = new Label(shell, SWT.NULL);
		metersLabel.setText("meters");
		metersLabel.setLayoutData( GUIUtil.getHorizontalSpanGridData(2) );
		
		timeField = new Text(shell, SWT.BORDER);
		timeField.setEnabled(false);
		timeField.setText("00:00");
		
		minutesLabel = new Label(shell, SWT.NULL);
		GridData minutesLayout = GUIUtil.getHorizontalSpanGridData(3);
		//minutesLayout.horizontalIndent = - 10;
		minutesLabel.setLayoutData( minutesLayout );
		minutesLabel.setText("minutes");
		
		showDistanceLabel = new Label(shell, SWT.NULL);
		showDistanceLabel.setText("Show Distance: ");
		showDistanceLabel.setLayoutData( GUIUtil.getHorizontalSpanGridData(2) );
		
		distanceCombo = new Combo(shell, SWT.BORDER | SWT.READ_ONLY);
		GridData distanceLayout = GUIUtil.getHorizontalSpanGridData(1, 50);
		//distanceLayout.horizontalIndent = 25;
		//GridData distanceLayout = GUIUtil.getIndentGridData(14, 50);
		distanceCombo.setLayoutData( distanceLayout );
		distanceCombo.add("100");
		distanceCombo.add("250");
		distanceCombo.add("500");
		distanceCombo.add("1000");
		distanceCombo.select(2);
		
		startType = new Label(shell, SWT.NULL);
		startType.setText("Start Type:");
		startType.setLayoutData( GUIUtil.getHorizontalSpanGridData(2) );
		
		startTypeCombo = new Combo(shell, SWT.BORDER | SWT.READ_ONLY);
		startTypeCombo.setLayoutData( GUIUtil.getHorizontalSpanGridData(2) );
		startTypeCombo.add("Count");
		startTypeCombo.add("Stroke");
		startTypeCombo.select(0);

		
		/* Create the table */
		rowerTable = new Table(shell, SWT.BORDER | SWT.MULTI);
		rowerTable.setHeaderVisible(true);
		rowerTable.setLinesVisible(true);
		rowerTable.setLayoutData( GUIUtil.getStandardGridData(6, 2, 257, 115) );
	
		TableColumn laneColumn = new TableColumn(rowerTable, SWT.LEFT);
		laneColumn.setText("");
		laneColumn.setWidth(15);
		laneColumn.setResizable(false);
		
		TableColumn nameColumn = new TableColumn(rowerTable, SWT.LEFT);
		nameColumn.setText("Name");
		nameColumn.setWidth(110);
		
		TableColumn typeColumn = new TableColumn(rowerTable, SWT.LEFT);
		typeColumn.setText("Type");
		typeColumn.setWidth(149);
		


		upButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		upButton.setText(" Up ");
		
		GridData upButtonGridData = GUIUtil.getHorizontalSpanGridData(1, 50);
		upButton.setLayoutData( upButtonGridData );
		upButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/* Only perform if one selection and not a shadow */
				TableItem [] items = rowerTable.getSelection();
				if (items.length == 1) {
					String lane = items[0].getText(0);
					if (!lane.startsWith(" ")) {
						int laneNo = Integer.parseInt(lane) - 1;
						if (laneNo > 0) {
							Rower rower = (Rower) rowers.remove(laneNo);
							Rower shadow = (Rower) shadows.remove(laneNo);
							rowers.add(laneNo - 1, rower);
							shadows.add(laneNo - 1, shadow);
						}
					}
				}
				updateRowerTable();
			}
		});
		
		downButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		downButton.setText("Down");
		GridData downButtonGridData = GUIUtil.getHorizontalSpanGridData(1, 50);
		downButton.setLayoutData( downButtonGridData );
		downButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/* Only perform if one selection and not a shadow */
				TableItem [] items = rowerTable.getSelection();
				if (items.length == 1) {
					String lane = items[0].getText(0);
					if (!lane.startsWith(" ")) {
						int laneNo = Integer.parseInt(lane) - 1;
						if (laneNo < rowers.size() - 1) {
							Rower rower = (Rower) rowers.remove(laneNo);
							Rower shadow = (Rower) shadows.remove(laneNo);
							rowers.add(laneNo + 1, rower);
							shadows.add(laneNo + 1, shadow);
						}
					}
				}
				updateRowerTable();
			}
		});
		
		addRowerButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		addRowerButton.setText("Add Rower");
		addRowerButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/* Create the window */
				rowerWindow = new AddRowerWindow(thisClass, getWorkoutType(), false);
				Shell rowerShell = rowerWindow.getShell();
				rowerShell.open();
		
				/* Wait until its closed */
				while ( !rowerShell.isDisposed() ) {

					if ( !rowerShell.getDisplay().readAndDispatch() ) {
						rowerShell.getDisplay().sleep();
					}
				}
		
				/* Dispose of the about shell resources */
				rowerShell.dispose();
			}
		});
		
		addShadowButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		GridData shadowButtonLayout = GUIUtil.getHorizontalSpanGridData(2);
		shadowButtonLayout.horizontalAlignment = GridData.END;
		addShadowButton.setLayoutData( shadowButtonLayout );
		addShadowButton.setText("Add Shadow");
		addShadowButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/* Check there's actually a rower to add a shadow to */
				if (rowers.size() != 0) {
					/* Create the window */
					rowerWindow = new AddRowerWindow(thisClass, getWorkoutType(), true);
					Shell rowerShell = rowerWindow.getShell();
					rowerShell.open();

					/* Wait until its closed */
					while ( !rowerShell.isDisposed() ) {

						if ( !rowerShell.getDisplay().readAndDispatch() ) {
							rowerShell.getDisplay().sleep();
						}
					}

					/* Dispose of the about shell resources */
					rowerShell.dispose();
				}
			}
		});
		
		
		deleteRowerButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		GridData deleteGridData = GUIUtil.getHorizontalSpanGridData(3);
		deleteGridData.horizontalAlignment = GridData.END;
		deleteRowerButton.setLayoutData( deleteGridData );
		deleteRowerButton.setText("Delete Rower");
		deleteRowerButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				/* Delete the first item in the selection if indeed there is a selection */
				TableItem [] deletionItem = rowerTable.getSelection();

				if (deletionItem.length > 0) {
					String lane = deletionItem[0].getText(0);
					
					/* Check the type and act accordingly */
					if (lane.startsWith(" ")) {
						int laneNo = Integer.parseInt(lane.substring(2,3)) - 1;
						shadows.remove(laneNo);
						shadows.add(laneNo, null);
					}
					else {
						int laneNo = Integer.parseInt(lane) - 1;
						rowers.remove(laneNo);
						shadows.remove(laneNo);
					}
				
					/* Update the rower table */	
					updateRowerTable();
				}
			}
		});
		
		/* As delete button doesn't fill last grid square */
		Label filler = new Label(shell, SWT.NONE);
		
		
		Label seperator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData seperatorGridData = GUIUtil.getHorizontalSpanGridData(7);
		seperatorGridData.widthHint = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT).x - 10;
		seperator.setLayoutData( seperatorGridData );
		
		okButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		GridData okGridData = GUIUtil.getHorizontalSpanGridData(6);
		okGridData.widthHint = 60;
		okGridData.horizontalAlignment = GridData.END;
		okButton.setLayoutData( okGridData );
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/* If the workout was created successfully let the rowing begin */
				if ( validateWorkout() ) {
					controller.createWorkout();
					shell.dispose();
					controller.beginWorkout();
				}
			}
		});
		
		cancelButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		GridData cancelGridData = new GridData();
		cancelGridData.widthHint = 60;
		cancelButton.setLayoutData(cancelGridData);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
	}
	
	
	/**
	 * Update the contents of the rower table 
	 */
	private void updateRowerTable() {
		
		/* Remove all the table items */
		rowerTable.removeAll();
		
		/* Add back the ones in the rowers and shadows arraylist  */
		for (int i = 0; i < rowers.size(); i++) {
			
			/* Get the rower */
			Rower rower = (Rower) rowers.get(i);
			TableItem tableItem = new TableItem(rowerTable, SWT.NONE);
			tableItem.setText( rower.getGUIDisplayForm(false, i + 1) );
			
			/* Add the shadow if appropriate */
			if (i < shadows.size()) {
				Rower shadow = (Rower) shadows.get(i);
				if ( shadow != null ) {
					TableItem shadowItem = new TableItem(rowerTable, SWT.NONE);
					shadowItem.setText( shadow.getGUIDisplayForm(true, i + 1) );
				}
			}
		}
	}
	
	
	/**
	 * Validate the workout details
	 */
	private boolean validateWorkout() {
		
		/* Create the error string */
		String errorString = "";
		
		/* Try retrieving the fields and if there's an error update the error string. */
		if ( getWorkoutType() == Workout.DISTANCE_WORKOUT ) {
			try{
				int dist = getDistance();
				if (dist == 0) {
					errorString += "Please enter a distance greater than 0\n";
				}
			}
			catch(NumberFormatException n) {
				errorString += "Please enter a valid number for the distance\n";
			}
		}
		else {
			try {
				if (getTime() == -1) {
					errorString += "Please enter the time in mm:ss form\n";
				}
			}
			catch (NumberFormatException n) {
				errorString += "Please enter the time in mm:ss form\n";
			}
		}
		
		if (rowers.size() == 0) {
			errorString += "Please enter at least one rower\n";
		}
		
		/* If there's an error show the dialog box */
		if (!errorString.equals("")) {
			MessageBox errorBox = new MessageBox(shell, SWT.ICON_ERROR);
			errorBox.setText("New Workout Error");
			errorBox.setMessage(errorString);
			errorBox.open();
		}
		
		/* Return whether there's an error */
		return errorString.equals("");
	}
}
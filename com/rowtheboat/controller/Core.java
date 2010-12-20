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
 * File: Core.java
 * 
 * Date			Version		User		Description
 * 11-Nov-2003	1.0			GeorgeP		Initial version coded
 * 30-Oct-2004	1.03		GeorgeP		Updated to RXTX pure serial driver
 * 10-Dec-2004	1.04		GeorgeP		Garbage collection changes made.  Sorted Just Row not
 * 										displaying boats straight away issue.  Fixed xml file saving
 * 										issue if quit without selecting stop rowing.
 * 
 */
 
package com.rowtheboat.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

import gnu.io.PortInUseException;

import com.rowtheboat.gui.AboutWindow;
import com.rowtheboat.gui.CountdownWindow;
import com.rowtheboat.gui.GUIUtil;
import com.rowtheboat.gui.MainWindow;
import com.rowtheboat.gui.NewWorkoutWindow;
import com.rowtheboat.gui.OptionsSingleton;

import org.dom4j.DocumentException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.xml.sax.SAXException;

import com.rowtheboat.workout.Rower;
import com.rowtheboat.workout.Workout;

/**
 * This class is the core of the controller, tying together the input, output, gui, user and
 * workout packages.
 * @author GeorgeP
 */

public class Core {

	/* Class Variables */
	
	private boolean aborted;								/* This flag is set when the race is
															   aborted */
	private boolean racing;									/* This flag is set when the racing
															   is taking place */
	private long lastTime;									/* Used for performance ratings */
	private BufferedWriter performanceWriter;				/* The performance writer */
	private MainWindow window;								/* The main program window shell */
	private NewWorkoutWindow newWorkoutWindow;				/* The new workout window */
	private IODeviceHandler ioDevices;						/* The io device handler */
	private Workout workout;								/* The workout */


	/* Constructor */
	
	/**
	 * Constructs the Core class and associated data
	 */
	public Core() {
			
		/* Initialise the new window */
		window = new MainWindow(this);
		lastTime = System.currentTimeMillis();
	}
	
	
	/* Public Methods */
	
	/**
	 * Display the about box
	 */
	public void aboutBox() {
		
		/* Construct the shell and open it */
		AboutWindow win = new AboutWindow
			( window.getShell().getSize(), window.getShell().getLocation() );
		Shell aboutShell = win.getShell();
		aboutShell.open();
		
		/* Wait until its closed */
		while ( !aboutShell.isDisposed() ) {

			if ( !aboutShell.getDisplay().readAndDispatch() ) {
				aboutShell.getDisplay().sleep();
			}
		}
		
		/* Dispose of the about shell resources */
		aboutShell.dispose();
		
		/* Update the shell so no remains of box showing */
		window.getShell().update();
	}
	
	
	/**
	 * Begin the workout
	 */
	public void beginWorkout() {
		
		/* Clear the window of the results box if not happened already */
		window.getShell().update();
	
		/* If a new workout is successfully entered by the user then we must first remove the old
		 * workout as it will be holding resources for a user - eg a serial port. */
		cleanUpWorkout();
	
		/* Setup the workout */
		setupIODevices();
		
		/* Display a click OK to start workout dialog */
		MessageBox clickToStart = new MessageBox(window.getShell(), SWT.ICON_INFORMATION | SWT.OK);
		clickToStart.setText("Start Workout");
		clickToStart.setMessage("Click OK to start the workout");
		clickToStart.open();
		
		/* Start the countdown */
		startRowing();
	}
	
	
	/**
	 * This is called before another workout can take place or as the program exits to clear the 
	 * resources associated with the old workout.
	 */
	public void cleanUpWorkout() {
		
		/* These should be set in case user aborts half way through race */
		racing = false;
		aborted = false;
		
		/* Clean up the devices */
		try {
			if (ioDevices != null) {
				ioDevices.cleanUp();
			}
		} 
		catch (Exception e) {
			boolean flag = true;
			if (e instanceof NumberFormatException) {
				GUIUtil.showNumberFormatException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (e instanceof IOException) {
				GUIUtil.showIOException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (e instanceof InterruptedException) {
				GUIUtil.showInterruptedException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (flag) {
				GUIUtil.showErrorDialog(window.getShell(), "Program Error\n( " + 
					e.getMessage() + " )");
			}
		}
	}
	
	
	/**
	 * This is called by the GUI when the new workout is created
	 */
	public void createWorkout() {
	
		/* Create the workout */
		workout = new Workout( newWorkoutWindow.getWorkoutType() );
		workout.setShowDistance( newWorkoutWindow.getShowDistance() );
		workout.setStartType( newWorkoutWindow.getStartType() );
	
		if ( workout.getType() == Workout.DISTANCE_WORKOUT ) {
			workout.setDistance( newWorkoutWindow.getDistance() );
		}
		else {
			workout.setTime( newWorkoutWindow.getTime() );
		}	

		/* Get the rowers and shadows */
		Object [] rowers = newWorkoutWindow.getRowers();
		Object [] shadows = newWorkoutWindow.getShadows();
	
		/* Add these to the workout */
		for (int i = 0; i < rowers.length; i++) {
			workout.addRower((Rower) rowers[i]);
			if (shadows[i] != null) {
				workout.addShadow((Rower) shadows[i]);
			}
		}
	}
	
	
	/**
	 * Exits the program.
	 */
	public void exitProgram() {
		
		/* Dispose of the window.  The control will then go back to the Start class and the 
		 * cleanUpWorkout() method will be called to ensure any resources are free again. */
		Start.setCleanUp(true);
		if (performanceWriter != null) {
			try {
				performanceWriter.close();
			} catch (IOException e) {
				GUIUtil.showIOException( window.getShell(), e.getMessage() );
			}
		}
		
		/* Save the options if possible */
		try {
			OptionsSingleton.getInstance().saveOptions();
		}
		catch (Exception e) {
			GUIUtil.showErrorDialog(newWorkoutWindow.getShell(), "Error saving options");
		}
		
		/* Call workout aborted to clear up any iodevices and window resources (assuming there's a
		 * reference in the first place) */
		if (ioDevices != null && window != null) {
			workoutAborted();			
		}
		
		/* Dispose of the shell */
		window.getShell().dispose();
	}
	
	
	/**
	 * Start a just row workout where the rower just rows down the river.
	 */
	public void justRow() {
		
		/* CleanUp any previous workouts */
		if (ioDevices != null) {
			cleanUpWorkout();
		}
		
		/* Construct the new workout */
		workout = new Workout(Workout.JUST_ROW);
		setupIODevices();
		startRowing();
	}
	
	
	/**
	 * The main program loop.  This opens the main window, retrieves the input data and delegates 
	 * the data to output devices. 
	 */
	public void mainProgramLoop() {
		
		/* Open the main window */
		Shell mainWindowShell = window.getShell();
		mainWindowShell.open();
		
		try {
			/* Main window loop */
			while ( !mainWindowShell.isDisposed() ) {

				/* If in racing mode then retrieve the latest data and update the outputs */
				if (racing) {
					updateStrokeData();
					
					/* Clear up any garbage.  This is done with each stroke to keep ongoing
					 * memory requirements to a minimum. */
					OptionsSingleton.getInstance().garbageCollect();
				}

				if ( !mainWindowShell.getDisplay().readAndDispatch() ) {
					mainWindowShell.getDisplay().sleep();
				}
			}
		}
		catch (Exception e) {
			boolean flag = true;
			if (e instanceof NumberFormatException) {
				GUIUtil.showNumberFormatException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (e instanceof IOException) {
				GUIUtil.showIOException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (e instanceof SAXException) {
				GUIUtil.showSAXException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (flag) {
				GUIUtil.showErrorDialog(window.getShell(), "Program Error\n( " + 
					e.getMessage() + " )");
			}
		}
	}


	/**
	 * This is called by the GUI when new workout is selected from the menu
	 */
	public void newWorkout() {
		
		newWorkoutWindow = new NewWorkoutWindow( window.getShell(), this );
		Shell newWorkoutShell = newWorkoutWindow.getShell();
		newWorkoutShell.open();
		
		while ( !newWorkoutShell.isDisposed() ) {

			if ( !newWorkoutShell.getDisplay().readAndDispatch() ) {
				newWorkoutShell.getDisplay().sleep();
			}
		}
		
		/* Dispose of the shell */
		newWorkoutShell.dispose();
	}
	
	
	/**
	 * This is called to setup the devices used in the workout
	 */
	public void setupIODevices() {
			
		try {
			/* Remove old ioDevices and set new one */
			ioDevices = null;
			ioDevices = new IODeviceHandler(workout, this);
			
			/* Construct outputs and turn on racing mode */
			window.setupWorkout(workout);
			racing = true;
		}
		catch (Exception e) {
			boolean flag = true;
			if (e instanceof NumberFormatException) {
				GUIUtil.showNumberFormatException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (e instanceof IOException) {
				GUIUtil.showIOException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (e instanceof SAXException) {
				GUIUtil.showSAXException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (e instanceof InterruptedException) {
				GUIUtil.showInterruptedException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (e instanceof PortInUseException) {
				GUIUtil.showErrorDialog(window.getShell(), "The selected serial port is in use." +
					"Please select another from the options dialog");
				flag = false;
			}
			if (e instanceof MalformedURLException || e instanceof DocumentException) {
				GUIUtil.showXMLReadException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (flag) {
				GUIUtil.showErrorDialog(window.getShell(), "Program Error\n( " + 
					e.getMessage() + " )");
			}
		}
	}
	
	
	/**
	 * Start the workout.  This starts the countdown and once finished starts the logging of the
	 * data.
	 */
	public void startRowing() {
		
		/* Update the display now everything has been drawn */
		window.getShell().getDisplay().update();
		
		/* Clear up any garbage so that the first garbage collection after the first stroke isn't
		 * too big and thus too time consuming. */
		OptionsSingleton.getInstance().garbageCollect();
		
		/* Start the workout */
		try {
			if (workout.getStartType() == Workout.START_ON_STROKE) {
				ioDevices.startRowing();
			}
			else {
			
				/* Construct the countdown window */
				CountdownWindow cw = new CountdownWindow
					( window.getShell().getSize(), window.getShell().getLocation() );
				Shell countdownShell = cw.getShell();
				countdownShell.open();
				
				
				/* Whilst the shell is still there */
				while (!countdownShell.isDisposed()) {
					if (!countdownShell.getDisplay().readAndDispatch()) {
						countdownShell.getDisplay().sleep();
					}
				
					/* Get the countdown delay from the options singleton */
					byte count = OptionsSingleton.getInstance().getDelay();
			
					/* Perform the countdown on both the window and input devices */
					while (count >= 0) {
						cw.setCount(count);
						ioDevices.displayCount(count);
						if (count != 0) {
							Thread.sleep(1000);	
						}
						count--;
					}
					
					/* Start the rowing */
					ioDevices.startRowing();
	
					/* Dispose of the countdown and refresh the display so we can see the
					 * gui outputs */
					countdownShell.dispose();
					window.getShell().getDisplay().update();
				}
			}
		}
		catch (Exception e) {
			boolean flag = true;
			if (e instanceof NumberFormatException) {
				GUIUtil.showNumberFormatException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (e instanceof IOException) {
				GUIUtil.showIOException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (e instanceof InterruptedException) {
				GUIUtil.showInterruptedException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (flag) {
				GUIUtil.showErrorDialog(window.getShell(), "Program Error\n( " + 
					e.getMessage() + " )");
					
			}
		}
	}
  	
  	
  	/**
  	 * This is called when a workout is aborted.  It ensures that all devices are aware of this
  	 * regardless of the location in the program loop (so window.workoutFinished() in 
  	 * updateStrokeData() can't be entered due to the if statement on the aborted flag but the 
  	 * call to window.workoutFinished in this method will be).
  	 */
  	public void workoutAborted() {
  		
  		/* Set the aborted flag and racing flags */
  		aborted = true;
		workoutFinished();
		
		/* Call workout finished in the IODevice handler and main window */
		try {
			ioDevices.workoutAborted();
			window.workoutAborted();
		} catch (Exception e) {
			boolean flag = true;
			if (e instanceof IOException) {
				GUIUtil.showIOException(window.getShell(), e.getMessage());
				flag = false;
			}
			if (flag) {
				GUIUtil.showErrorDialog(window.getShell(), "Program Error\n( " + 
					e.getMessage() + " )");
			}
		}
  	}
  	
	
	/**
	 * This is called when a workout is finished.  It is distinct from cleanUpWorkout() which
	 * removes the associated input resources.  This is not required at this point as the user
	 * may wish to cool down and see their split.
	 */
	public void workoutFinished() {
		
		racing = false;
	}
	
	
	/**
	 * If called performace figures are written to performance.txt
	 */
	public void writePerformanceFile() {
		
		try {
			performanceWriter = new BufferedWriter( new FileWriter("performance.txt") );
		} catch (IOException e) {
			GUIUtil.showIOException( window.getShell(), e.getMessage() );
		}
	}
	
	
	/* Private Methods */
	
	/**
	 * Updates the stroke data for every rower and passes this to the window 
	 */
	private void updateStrokeData() throws Exception  {
		
		/* Performance note:
		 * The retrieval of stroke data takes approximately 50ms per PM2Unit - this is due to 
		 * 		waiting for the PM2PlusUnit event to fire (v. annoying).  However this can be
		 * 		dropped to 30ms by not retrieving power and hr (option in tools->options).
		 * The updating of the GUI outputs approximately 10ms */
		 
		/* Generate the latest stroke data and pass this to the GUI */
		if (performanceWriter != null) {
			long current = System.currentTimeMillis();
			performanceWriter.write(current - lastTime + " ");
			lastTime = current;
		}
		StrokeCollection strokes = ioDevices.getLatestStrokeCollection();
		if (performanceWriter != null) {
			long current = System.currentTimeMillis();
			performanceWriter.write(current - lastTime + "\n");
			lastTime = current;
		}
		
		/* If it's the last stroke retrieved then inform main window of workout end.  Otherwise
		 * just update as normal. */
		if (racing) {
			window.updateStrokeData( strokes );
		}
		else {
			if (!aborted) {
				window.workoutFinished( strokes );
			}
		}
	}
}
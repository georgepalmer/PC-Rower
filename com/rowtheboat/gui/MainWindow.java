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
 * File: MainWindow.java
 * 
 * Date			Version		User		Description
 * 11-Nov-2003	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat.gui;

import com.rowtheboat.gui.output.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.rowtheboat.controller.Core;
import com.rowtheboat.controller.StrokeCollection;

import com.rowtheboat.workout.Workout;

/**
 * This is the class that provides the main GUI window
 * 
 * @author GeorgeP
 */

public class MainWindow {

	/* Class Variables */
	
	private Shell shell;							/* The shell of this window */
	private Workout workout;						/* The workout */
	private StrokeCollection lastStrokeSet;			/* The last recorded stroke set - used so that
													   if a workout is aborted then the resuls
													   window can stil be shown */
	private final Core core;						/* A reference to the core (also controller)
													   for this window */
	private IGUIOutputDevice [] canvasDisplays;		/* The canvas displays for the window */
	
	
	/* Constructor */

	/**
	 * Construct the MainWindow
	 */
	public MainWindow(final Core core) {

		/* Set the core class variable */
		this.core = core;

		/* Setup the shell */
		shell = new Shell();
		shell.setText(OptionsSingleton.APP_NAME);
		
		/* Setup the icon */
		Image image = 
			new Image( shell.getDisplay(), OptionsSingleton.getInstance().getIconLocation() );
		shell.setImage( image );
		image.dispose();
		
		/* Create the menus */
		createMenus();
		
		/* Add a resize listener and delegate responsability to the canvas's */
		shell.addControlListener(new ControlAdapter() {
			public void controlResized (ControlEvent e) {
				if (canvasDisplays != null) {
					for (int i = 0; i < canvasDisplays.length; i++) {
						canvasDisplays[i].guiResized();	
					}
				}
			}
		});
		
		/* Add a program exit listener to ensure resources are cleaned up properly */
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent event) {
				MessageBox exit = new MessageBox( shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL ); 
				exit.setText("Exit PC-Rower?");
				exit.setMessage
					("Exiting PC-Rower will lose current PM2+ workout data.  Are you sure?");
		
				if (exit.open() == SWT.OK) {
					event.doit = true;
					core.exitProgram();
				}
				else {
					event.doit = false;
				}
			}
		});
		
		/* Setup the window layout */
		GridLayout shellLayout = new GridLayout();
		shell.setLayout(shellLayout);
	}
	
	
	/* Public Methods */
	
	
	/**
	 * This returns the main window shell
	 * 
	 * @return the shell
	 */
	public Shell getShell() {
		
		return shell;
	}
	
	
	/**
	 * Construct the canvases that display the input data
	 */
	public void setupWorkout(Workout workout) {

		this.workout = workout;

		/* Determine the number of displays */
		int displays;
		if (workout.numberOfHumanRowers() == 1) {
			displays = 2; 
		}
		else {
			displays = 1;
		}
	
		/* Check to see if the canvases have been constructed before */
		if (canvasDisplays == null) {
		
			/* If not construct the array */
			canvasDisplays = new IGUIOutputDevice[displays];	
		}
		else {
		
			/* If so, go through the current canvases and dispose of them */
			for (int i = 0; i < canvasDisplays.length; i++) {
				IGUIOutputDevice canvas = canvasDisplays[i];
				if (canvas instanceof River) {
					((River) canvas).dispose();
				}
				if (canvas instanceof PMDisplay) {
					((PMDisplay) canvas).dispose();
				}
			}
		
			/* If the array sizes don't match initialise a new array */
			if (canvasDisplays.length != displays) {
				canvasDisplays = new IGUIOutputDevice[displays];
			}
		}
	
		/* Create the new canvases */
		canvasDisplays[0] = new River( shell, workout, workout.getShowDistance() );
		if (displays == 2) {
			canvasDisplays[1] = new PMDisplay( shell, SWT.NONE, workout );
		}
	
		/* Refesh the layout to take account of the new canvas sizes */
		shell.layout();
		shell.update();
	}
		
		
	/**
	 * This updates the GUI with the new stroke data
	 * 
	 * @param	strokes	the stroke collection to update the displays with 
	 */
	public void updateStrokeData(StrokeCollection strokes) {
		
		/* Delegate the update responsability */
		for (int i = 0; i < canvasDisplays.length; i++) {
			lastStrokeSet = strokes;
			canvasDisplays[i].updateStrokeData(lastStrokeSet);
		}
	}
	
	
	/* Inherited Javadoc */
	public void workoutAborted() throws Exception {
		
		/* Delegate the responsability */
		for (int i = 0; i < canvasDisplays.length; i++) {
			canvasDisplays[i].workoutAborted();
		}
		
		showResultsWindow(lastStrokeSet);
	}
	
	
	/**
	 * This informs the canvases that the workout is finished and they can stop logging data
	 * and display any results.
	 * 
	 * @param	strokes	the final stroke collection of the race
	 */
	public void workoutFinished(StrokeCollection strokes) {
		
		/* Delegate the responsability */
		for (int i = 0; i < canvasDisplays.length; i++) {
			canvasDisplays[i].workoutFinished(strokes);
		}
		
		showResultsWindow(strokes);
	}
	
	
	/* Private Methods */
	
	/**
	 * Create the menus for the shell
	 */
	private void createMenus() {
		
		/* Create the menu bar */
		Menu bar = new Menu (shell, SWT.BAR);
		shell.setMenuBar(bar);
		
		createFileMenu(bar);
		createToolsMenu(bar);
		createHelpMenu(bar);
	}
	
	
	/**
	 * Create the file menu
	 * 
	 * @param bar	the menu bar
	 */
	private void createFileMenu(Menu bar) {
		
		/* Create the file menu */
		MenuItem fileItem = new MenuItem (bar, SWT.CASCADE);
		fileItem.setText ("&File");
		fileItem.setAccelerator(SWT.CTRL + 'F');
		
		Menu fileMenu = new Menu (shell, SWT.DROP_DOWN);
		fileItem.setMenu(fileMenu);
		
		
		/* Create the new workout menu item */
		MenuItem newWorkout = new MenuItem (fileMenu, SWT.PUSH);
		newWorkout.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				core.newWorkout();
			}
		});
		newWorkout.setText ("&New Workout\tCtrl+N");
		newWorkout.setAccelerator (SWT.CTRL + 'N');
		
		
		/* Create the just row menu item */
		MenuItem justRow = new MenuItem (fileMenu, SWT.PUSH);
		justRow.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				core.justRow();
			}
		});
		justRow.setText ("&Just Row\tCtrl+J");
		justRow.setAccelerator (SWT.CTRL + 'J');
		
		
		/* Create the stop rowing menu item */
		MenuItem stopRowing = new MenuItem (fileMenu, SWT.PUSH);
		stopRowing.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				core.workoutAborted();
			}
		});
		stopRowing.setText ("&Stop Rowing\tCtrl+S");
		stopRowing.setAccelerator (SWT.CTRL + 'S');
				
		
		/* Create the exit program menu item */
		MenuItem exitProgram = new MenuItem (fileMenu, SWT.PUSH);
		exitProgram.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				MessageBox exit = new MessageBox( shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL ); 
				exit.setText("Exit PC-Rower?");
				exit.setMessage
					("Exiting PC-Rower will lose current PM2+ workout data.  Are you sure?");
				
				if (exit.open() == SWT.OK) {
					core.exitProgram();
				}
			}
		});
		exitProgram.setText ("E&xit\tCtrl+X");
		exitProgram.setAccelerator (SWT.CTRL + 'X');
	}
	
	
	/**
	 * Create the help menu
	 * 
	 * @param bar	the menu bar
	 */
	private void createHelpMenu(Menu bar) {
	
		/* Create the help menu */
		MenuItem helpItem = new MenuItem (bar, SWT.CASCADE);
		helpItem.setText ("&Help");
		
		Menu helpMenu = new Menu (shell, SWT.DROP_DOWN);
		helpItem.setMenu(helpMenu);
		
		
		/* Create the new workout menu item */
		MenuItem aboutBox = new MenuItem (helpMenu, SWT.PUSH);
		aboutBox.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				core.aboutBox();
			}
		});
		aboutBox.setText ("&About\tCtrl+A");
		aboutBox.setAccelerator (SWT.CTRL + 'A');
	}
	
	
	/**
	 * Create the tools menu
	 * 
	 * @param bar	the menu bar
	 */
	private void createToolsMenu(Menu bar) {
		
		/* Create the help menu */
		MenuItem toolItem = new MenuItem (bar, SWT.CASCADE);
		toolItem.setText ("&Tools");
		
		Menu toolMenu = new Menu (shell, SWT.DROP_DOWN);
		toolItem.setMenu(toolMenu);
		
		
		/* Create the new workout menu item */
		//MenuItem crash = new MenuItem (toolMenu, SWT.PUSH);
		//crash.addListener (SWT.Selection, new Listener () {
		//	public void handleEvent (Event e) {
		//		int [] bob = new int[2];
		//		int x = bob[3];
		//	}
		//});
		//crash.setText ("&Crash");
		
		
		MenuItem optionsBox = new MenuItem (toolMenu, SWT.PUSH);
		optionsBox.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				OptionsWindow optionsWin = new OptionsWindow(shell);
				Shell shell = optionsWin.getShell();
				shell.open();
				
				while ( !shell.isDisposed() ) {

				if ( !shell.getDisplay().readAndDispatch() ) {
						shell.getDisplay().sleep();
					}
				}
				
				/* Dispose of the about shell resources */
				shell.dispose();
			}
		});
		optionsBox.setText ("&Options\tCtrl+O");
		optionsBox.setAccelerator (SWT.CTRL + 'O');
	}
	
	
	/**
	 * Show the results window
	 */
	private void showResultsWindow(StrokeCollection strokes) {
		
		/* Create popup window with results */
		ResultsWindow results = new ResultsWindow(workout, strokes);
		Shell resultsShell = results.getShell();
		resultsShell.open();
	
		/* Keep the results window open */
		while ( !resultsShell.isDisposed() ) {
					
			if ( !resultsShell.getDisplay().readAndDispatch() ) {
				resultsShell.getDisplay().sleep();
			}
		}
		
		/* Dispose of the shell */
		resultsShell.dispose();
	}
}
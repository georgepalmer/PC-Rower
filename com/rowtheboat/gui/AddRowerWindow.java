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
 * File: AddRower.java
 * 
 * Date			Version		User		Description
 * 11-Nov-2003	1.0			GeorgeP		Initial version coded
 * 15-Oct-2004	1.02		GeorgeP		Made alterations for generic window sizing and seperator.
 * 										Allowed for different file seperators for XML file paths.
 * 30-Oct-2004	1.03		GeorgeP		Renamed shadow to isShadow and controller to parentWindow
 * 10-Nov-2004	1.04		GeorgeP		Changed the 0 for split to 00:00. Garbage collect changes
 * 										Sorted out sizing on Linux Gtk
 * 
 */
 
package com.rowtheboat.gui;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;

import com.rowtheboat.workout.Workout;

/**
 * This is the class that provides the add rower window
 * 
 * @author George Palmer
 */

public class AddRowerWindow {
	
	/* Class Variables */
	
	private boolean isShadow;						/* Whether the window is to add a shadow */
	private Shell shell;							/* This shell */
	private NewWorkoutWindow parentWindow;			/* The parent window/controller */
	
	private Button cancelButton;
	private Button okButton;
	private Button xmlReadButton;
	private Text xmlReadText;
	private Label readXMLLabel;
	private Button variableSplitButton;
	private Text splitOrTimeText;
	private Label splitOrTimeLabel;
	private Button fixedSplitButton;
	private Button writeXMLButton;
	private Text writeXMLLogText;
	private Button writeXMLLogButton;
	private Text ergoText;
	private Label ergoLabel;
	private Button humanRowerButton;
	private Label rowerTypeLabel;
	private Text nameText;
	private Label nameLabel;
	
	
	/* Constructor */
	
	public AddRowerWindow(NewWorkoutWindow parent, int workoutType, boolean shadow) {

		/* Set the class variables */		
		this.parentWindow = parent;
		this.isShadow = shadow;
		
		/* Create the shell */
		shell = new Shell(parent.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE );
		if (shadow) {
			shell.setText("Add shadow");
		}
		else {
			shell.setText("Add rower");
		}
		
		GridLayout thisLayout = new GridLayout(6, false);
		shell.setLayout(thisLayout);

		/* Setup the icon */
		Image image = 
			new Image( shell.getDisplay(), OptionsSingleton.getInstance().getIconLocation() );
		shell.setImage( image );
		image.dispose();

		/* Setup the contents */
		setupContents(workoutType);
		
		/* Set the size of the window */
		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	
	/* Public Methods */
	
	/**
	 * Return teh ergo number
	 * 
	 * @return the ergo number
	 */
	public byte getErgoNumber() {
		
		/* Convert the ergo number to be zero indexed and return this */
		int ergoZeroIndexed = (new Integer( ergoText.getText() )).intValue() - 1;
		
		return new Integer(ergoZeroIndexed).byteValue();
	}
	
	
	/**
	 * Return the rower name
	 * 
	 * @return the rower name
	 */
	public String getRowerName() {
		
		return nameText.getText();
	}
	
	
	/**
	 * Return the rower type as an int:
	 * <br>
	 * 1 = Human Rower<br>
	 * 2 = Fixed Split Rower<br>
	 * 3 = Variable Split Rower<br>
	 * 
	 * @return	the rower type as described above or -1 if an error
	 */
	public int getRowerType() {
		
		/* Determine the rower type and return an int value */
		int type = -1;
		
		if (humanRowerButton.getSelection()) {
			type = 1;
		}
		if (fixedSplitButton.getSelection()) {
			type = 2;
		}
		if (variableSplitButton.getSelection()) {
			type = 3;
		}
		
		return type;
	}
	
	
	/**
	 * Return the current shell
	 * 
	 * @return	the shell
	 */
	public Shell getShell() {
		
		return shell;
	}
	
	
	/**
	 * Get the split or the time for the fixed pace boat.  Both are in seconds and the caller will
	 * know which to expect as they will have constructed the class.
	 * 
	 * @return	the split or time
	 */
	public int getSplitOrTime() {
		
		String [] split = splitOrTimeText.getText().split(":");
		if (split.length == 2) {
			int mins = Integer.parseInt( split[0] );
			int secs = Integer.parseInt( split[1] );
		
			return mins * 60 + secs;
		}
		else {
			if (split.length == 1) {
				return Integer.parseInt( split[0] );
			}
			else {
				return -1;
			}
		}
	}
	
	
	/**
	 * Return the xml read file location
	 * 
	 * @return	the read file location
	 */
	public File getXMLReadFile() {
	
		/* Return the file replacing \ with \\ */
		return new File( xmlReadText.getText().replaceAll("\\\\", "\\\\\\\\") );
	}
	
	
	/**
	 * Return the xml save file location
	 * 
	 * @return	the xml save file
	 */
	public File getXMLSaveFile() throws IOException {
		
		/* Return the file location if the box is ticked */
		if ( writeXMLLogButton.getSelection() ) {
			
			/* Replace \ with \\ */
			File file = new File( writeXMLLogText.getText().replaceAll("\\\\", "\\\\\\\\") );
			
			/* If the file doesn't exist then create it */
			if (!file.canWrite()) {
				file.createNewFile();
			}
			
			return file;
		}
		else {
			return null;
		}
	}
	
	
	/* Private Methods */
	
	/**
	* Setup the GUI components
	*/
	private void setupContents(int workoutType) {
		
		nameLabel = new Label(shell, SWT.NULL);
		nameLabel.setText("Name:");
		
		nameText = new Text(shell, SWT.BORDER);
		nameText.setLayoutData( GUIUtil.getHorizontalSpanGridData(5, 80) );
		
		rowerTypeLabel = new Label(shell, SWT.NULL);
		rowerTypeLabel.setLayoutData( GUIUtil.getHorizontalSpanGridData(6) );
		rowerTypeLabel.setText("Type:");
		
		humanRowerButton = new Button(shell, SWT.RADIO | SWT.LEFT);
		humanRowerButton.setText("Ergo");
		if (isShadow) {
			humanRowerButton.setEnabled(false);
		}
		else {
			humanRowerButton.setSelection(true);
		}
		
		ergoLabel = new Label(shell, SWT.NULL);
		ergoLabel.setText("Ergo Number:");
		
		ergoText = new Text(shell, SWT.BORDER);
		ergoText.setLayoutData( GUIUtil.getHorizontalSpanGridData(4, 20) );
		ergoText.setText("1");
		if (isShadow) {
			ergoText.setEnabled(false);
		}
		
		writeXMLLogButton = new Button(shell, SWT.CHECK | SWT.LEFT);
		GridData writeXMLLogButtonLData = GUIUtil.getHorizontalGridData(2, 87);
		writeXMLLogButton.setLayoutData(writeXMLLogButtonLData);
		writeXMLLogButton.setText("Write XML Log:");
		if (isShadow) {
			writeXMLLogButton.setEnabled(false);
		}
		
		writeXMLLogText = new Text(shell, SWT.BORDER);
		if (isShadow) {
			writeXMLLogText.setEnabled(false);
		}
		writeXMLLogText.setLayoutData( GUIUtil.getHorizontalSpanGridData(3, 80) );
		
		writeXMLButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		writeXMLButton.setText("...");
		writeXMLButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/* Create an open a save dialog */
				FileDialog saveDialog = new FileDialog(shell, SWT.SAVE);
				saveDialog.setFilterExtensions(new String [] {"*.xml"});
				saveDialog.open();				
				
				/* Ensure the path has the xml file extension */
				String filename = saveDialog.getFileName();
				if ( !filename.endsWith(".xml") ) {
					filename += ".xml";
				}
				writeXMLLogText.setText( saveDialog.getFilterPath() + File.separatorChar + filename );
				
				/* Do a garbage collection due to the amount of resources associated with a file
				 * dialog. */
				OptionsSingleton.getInstance().garbageCollect();
			}
		});
		if (isShadow) {
			writeXMLButton.setEnabled(false);
		}
		
		fixedSplitButton = new Button(shell, SWT.RADIO | SWT.LEFT);
		fixedSplitButton.setText("Fixed Split");
		if (isShadow) {
			fixedSplitButton.setSelection(true);
		}
		
		splitOrTimeLabel = new Label(shell, SWT.NULL);
		String labelText = "Time:";
		if (workoutType == Workout.TIME_WORKOUT) {
			labelText = "Split:";
		}
		splitOrTimeLabel.setText(labelText);
		
		splitOrTimeText = new Text(shell, SWT.BORDER);
		splitOrTimeText.setLayoutData( GUIUtil.getHorizontalSpanGridData(4, 40) );
		String splitText = "00:00";
		if (workoutType == Workout.TIME_WORKOUT) {
			splitText = "00:00";
		}
		splitOrTimeText.setText(splitText);
		
		variableSplitButton = new Button(shell, SWT.RADIO | SWT.LEFT);
		variableSplitButton.setText("Variable Split");
		
		readXMLLabel = new Label(shell, SWT.NULL);
		readXMLLabel.setText("XML file location:");
		
		xmlReadText = new Text(shell, SWT.BORDER);
		xmlReadText.setLayoutData( GUIUtil.getHorizontalSpanGridData(3, 80) );
		
		xmlReadButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		xmlReadButton.setText("...");
		xmlReadButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/* Create an open a save dialog */
				FileDialog openDialog = new FileDialog(shell, SWT.OPEN);
				openDialog.setFilterExtensions(new String [] {"*.xml"});
				openDialog.open();				
				
				/* Ensure the path has the xml file extension */
				String filename = openDialog.getFileName();
				if ( !filename.endsWith(".xml") ) {
					filename += ".xml";
				}
				xmlReadText.setText( openDialog.getFilterPath() + File.separatorChar + filename );
				
				/* Do a garbage collection due to the amount of resources associated with a file
				 * dialog. */
				OptionsSingleton.getInstance().garbageCollect();
			}
		});
		
		Label seperator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData seperatorGridData = GUIUtil.getHorizontalSpanGridData(6);
		seperatorGridData.widthHint = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		seperator.setLayoutData( seperatorGridData );
		
		okButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		GridData okButtonData = GUIUtil.getHorizontalSpanGridData(5, 60);
		okButtonData.horizontalAlignment = GridData.END;
		okButton.setLayoutData(okButtonData);
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( validateRower() ) {
					shell.dispose();
				}
			}
		});
		
		cancelButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		cancelButton.setText("Cancel");
		GridData cancelButtonGridData = GUIUtil.getHorizontalSpanGridData(1, 60);
		cancelButtonGridData.horizontalAlignment = GridData.END;
		cancelButton.setLayoutData( cancelButtonGridData );
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
	}
	
	
	/**
	 * Validate the rower about to be created.
	 */
	private boolean validateRower() {
		
		/* Create an error string */
		String errorString = "";
		
		/* Check the selected rower type */
		switch ( getRowerType() ) {
			case 1 : {
				/* If a human rower check the ergo number and file if applicable */
				try {
					getErgoNumber();
				}
				catch (NumberFormatException n) {
					errorString += "Please enter a valid ergo number\n";
				}
				
				try {
					File file = getXMLSaveFile();
					if ( file != null && !file.canWrite() ) {
						errorString += "Please enter a valid file path\n";
					}
				}
				catch (IOException i) {
					errorString += "Error writing file\n";
				}
				
				break;
			}
			case 2 : {
				/* If a fixed split rower check the split or time and use the correct error text */
				try {
					if (getSplitOrTime() == -1) {
						if ( splitOrTimeLabel.getText().equals("Time:") ) {
							errorString += "Please enter a time in mm:ss form\n";
						}
						else {
							errorString += "Please enter split in mm:ss form\n"; 
						}
					}
				}
				catch (NumberFormatException n) {
					if ( splitOrTimeLabel.getText().equals("Time:") ) {
						errorString += "Please enter a time in mm:ss form\n";
					}
					else {
						errorString += "Please enter split in mm:ss form\n"; 
					}
				}
				
				break;
			}
			case 3 : {
				/* If a variable split rower check the input file can be read */
				if (!getXMLReadFile().canRead()) {
					errorString += "Please enter a valid file path\n";
				}
				
				break;
			}
		}
		
		/* If there's no error add the rower, else show the dialog box */
		if (errorString.equals("")) {
			parentWindow.addRower(isShadow);
		}
		else {
			MessageBox errorBox = new MessageBox(shell, SWT.ICON_ERROR);
			errorBox.setText("New Rower Error");
			errorBox.setMessage(errorString);
			errorBox.open();
		}
		
		/* Return whether there's an error */
		return errorString.equals("");
	}
}
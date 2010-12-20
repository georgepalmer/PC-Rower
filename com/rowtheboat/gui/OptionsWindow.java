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
 * File: OptionsWindow.java
 * 
 * Date			Version		User		Description
 * 11-Nov-2003	1.0			GeorgeP		Initial version coded
 * 15-Oct-2004	1.02		GeorgeP		Made alterations for generic window sizing and sorted
 * 										sizing for seperator.  Allowed for serial port differences
 * 15-Nov-2004	1.03		GeorgeP		Changed population of serial box to be from system
 * 
 */

package com.rowtheboat.gui;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.SWT;

/**
 * This is the class that provides the options dialog
 * 
 * @author George Palmer
 */

public class OptionsWindow {
	
	/* Class Variables */
	
	private Shell shell;							/* The shell */
	
	private Button cancelButton;
	private Button okButton;
	private Label fullStrokeLabel;
	private Label smoothingLabel;
	private Combo serialCombo;
	private Label serialPortLabel;
	private Text countdownText;
	private Label countdownLabel;
	private Button fullStrokeDataButton;
	private Button smoothingButton;
	
	
	/* Constructor */
	
	/**
	 * Construct the OptionsWindow
	 * 
	 * @param parent	the windows parent
	 */
	public OptionsWindow(Shell parent) {
	
		/* Create the shell */
		shell = new Shell(parent, SWT.DIALOG_TRIM  | SWT.RESIZE);
		shell.setText("Options Window");
		
		GridLayout thisLayout = new GridLayout(3, false);
		shell.setLayout(thisLayout);
		
		/* Setup the icon */
		Image image = 
			new Image( shell.getDisplay(), OptionsSingleton.getInstance().getIconLocation() );
		shell.setImage( image );
		image.dispose();
		
		/* Setup the contents */
		setupContents();
		
		/* Set the size */
		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	
	/* Public Methods */

	/**
	 * Setup the shell
	 */
	public Shell getShell() {
		
		return shell;
	}

	
	/* Private Methods */

	/**
	 * Setup the contents 
	 */
	private void setupContents() {

		final OptionsSingleton options = OptionsSingleton.getInstance();

		smoothingLabel = new Label(shell, SWT.NULL);
		smoothingLabel.setText("Computer Boat Smoothing");
		
		smoothingButton = new Button(shell, SWT.CHECK | SWT.LEFT);
		smoothingButton.setLayoutData( GUIUtil.getHorizontalSpanGridData(2) );
		smoothingButton.setSelection( options.getBoatSmoothing() );
		
		fullStrokeLabel = new Label(shell, SWT.NULL);
		fullStrokeLabel.setText("Retrieve Full Stroke Data");
		
		fullStrokeDataButton = new Button(shell, SWT.CHECK | SWT.LEFT);
		fullStrokeDataButton.setLayoutData( GUIUtil.getHorizontalSpanGridData(2) );
		fullStrokeDataButton.setSelection( options.getFullStrokeData() );
		
		countdownLabel = new Label(shell, SWT.NULL);
		countdownLabel.setText("Countdown Time:");
		
		countdownText = new Text(shell, SWT.BORDER);
		countdownText.setLayoutData( GUIUtil.getHorizontalSpanGridData(2, 25) );
		countdownText.setText( options.getDelay() + "" );
		
		serialPortLabel = new Label(shell, SWT.NULL);
		serialPortLabel.setText("Serial Port:");
		
		serialCombo = new Combo(shell, SWT.BORDER);
		GridData serialComboLData = new GridData();
		serialCombo.setLayoutData( GUIUtil.getHorizontalSpanGridData(2, 95));
		
		OptionsSingleton optionsSingleton = OptionsSingleton.getInstance();
		Enumeration ports = optionsSingleton.getPossibleSerialPorts();
		
		while (ports.hasMoreElements()) {
			serialCombo.add(((CommPortIdentifier) ports.nextElement()).getName());
		}
		serialCombo.select(optionsSingleton.getSerialPortPosition());
		
		Label seperator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData seperatorGridData = GUIUtil.getHorizontalSpanGridData(3);
		seperatorGridData.widthHint = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT).x - 18;
		seperator.setLayoutData( seperatorGridData );
		
		okButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		okButton.setText("OK");
		GridData okGridData = GUIUtil.getHorizontalSpanGridData(2);
		okGridData.horizontalAlignment = GridData.END;
		okGridData.widthHint = 60;
		okButton.setLayoutData( okGridData );
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/* Update the settings */
				options.setBoatSmoothing( smoothingButton.getSelection() );
				options.setFullStrokeData( fullStrokeDataButton.getSelection() );
				options.setDelay( (new Byte(countdownText.getText())).byteValue() );
				options.setSerialPort( serialCombo.getText() );
				
				/* Dispose of the GUI */
				shell.dispose();
			}
		});
		
		cancelButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		cancelButton.setText("Cancel");
		GridData cancelGridData = new GridData();
		cancelGridData.horizontalAlignment = GridData.END;
		cancelGridData.widthHint = 60;
		cancelButton.setLayoutData( cancelGridData );
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
	}
}
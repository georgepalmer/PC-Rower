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
 * File: AboutWindow.java
 * 
 * Date			Version		User		Description
 * 25-Jan-2004	1.0			GeorgeP		Initial version coded
 * 12-Oct-2004	1.01		GeorgeP		Corrected file location for platform indepedence
 * 15-Oct-2004	1.02		GeorgeP		Made alterations for generic window sizing
 * 
 */
 
package com.rowtheboat.gui;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

/**
 * AboutWindow provides the about box
 * 
 * @author GeorgeP
 */

public class AboutWindow {
	
	/* Class Variables */
	
	private Shell shell;							/* The shell for this window */
	
	private static final String BOAT_SPACER_IMAGE = 
		"images" + File.separatorChar + "boatSpacer.gif";
													/* The boat spacer image location */
	private final RGB BACKGROUND_COLOUR = new RGB(222, 231, 231);
													/* The shell background colour */
	
	
	/* Constructor */ 
	
	/**
	 * Construct the about window
	 *
	 * @param	parentSize		the parent window size
	 * @param	parentLocation	the parent window location
	 */
	public AboutWindow(Point parentSize, Point parentLocation) {
		
		/* Initialise the window */
		shell = new Shell( SWT.DIALOG_TRIM | SWT.RESIZE );
		final GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		shell.setText("About Box");
		
		Color backgroundColour = new Color(shell.getDisplay(), BACKGROUND_COLOUR);
		shell.setBackground( backgroundColour );
		backgroundColour.dispose();
		
		/* Setup the window */
		Image image = 
			new Image( shell.getDisplay(), OptionsSingleton.getInstance().getIconLocation() );
		shell.setImage( image );
		image.dispose();

		/* Setup the contents */
		setupContents();

		/* Set the shell size and centre */
		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		int x = parentLocation.x + ((parentSize.x - shell.getSize().x) / 2);
		int y = parentLocation.y + ((parentSize.y - shell.getSize().y) / 4);
		shell.setLocation(x, y);
	}

	
	/* Public Methods */
	
	/**
	 * Returns the shell for this window
	 * 
	 * @return	the shell
	 */
	public Shell getShell() {
		
		return shell;
	}
	
	
	/* Private Methods */
	
	/**
	 * Setup the contents of the window
	 */
	private void setupContents() {
		
		/* Set the background colour */
		Color backgroundColour = new Color(shell.getDisplay(), BACKGROUND_COLOUR);
		
		/* Application name */
		Label appName = new Label(shell, SWT.NONE);
		
		/* Set the font */
		Font font = new Font(shell.getDisplay(), new FontData("Arial", 24, SWT.BOLD));
		appName.setFont( font );
		
		/* Set and dispose of the foreground colout */
		Color foreground = new Color(shell.getDisplay(), 123, 148, 181);
		appName.setForeground( foreground );
		foreground.dispose();
		
		/* Set the background colour and text */
		appName.setBackground( backgroundColour );
		appName.setText("  " + OptionsSingleton.APP_NAME + " ");
		
		
		/* Version information */
		Label version = new Label(shell, SWT.NONE);
		version.setText("Version: " + OptionsSingleton.APP_VERSION);
		version.setBackground( backgroundColour );
		GridData versionLayout = new GridData();
		versionLayout.horizontalAlignment = GridData.CENTER;
		version.setLayoutData(versionLayout);
		
		/* Boat spacer */
		Label boat = new Label(shell, SWT.NONE);
		Image image = new Image(shell.getDisplay(), BOAT_SPACER_IMAGE);
		boat.setImage( image );
		boat.setBackground( backgroundColour );
		GridData boatSpacerLayout = new GridData();
		boatSpacerLayout.horizontalAlignment = GridData.CENTER;
		boat.setLayoutData(boatSpacerLayout);
		
		/* Author */
		Label author = new Label(shell, SWT.NONE);
		author.setBackground( backgroundColour );
		author.setText("Written by: " + OptionsSingleton.APP_AUTHOR);
		
		/* Thanks to */
		Label thanks = new Label(shell, SWT.NONE);
		thanks.setBackground( backgroundColour );
		thanks.setText("Thanks to: ");
		
		Label liz = new Label(shell, SWT.NONE);
		liz.setBackground( backgroundColour );
		liz.setText("\tDr. Liz Burd");
		
		Label wade = new Label(shell, SWT.NONE);
		wade.setBackground( backgroundColour );
		wade.setText("\tOwen Wade Hall-Craggs");	
		
		
		/* Dispose of the background colour */
		backgroundColour.dispose();
	}
}
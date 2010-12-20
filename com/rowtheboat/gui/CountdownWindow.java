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
 * File: CountdownWindow.java
 * 
 * Date			Version		User		Description
 * 22-Jan-2004	1.0			GeorgeP		Initial version coded
 * 15-Oct-2004	1.02		GeorgeP		Fixed countdown update, font and position for Linux
 * 11-Dec-2004	1.04		GeorgeP		Fixed double numbers bug in setCount.  Added shell update
 */
 
package com.rowtheboat.gui;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

/**
 * CountdownWindow is the window that displays the countdown timer
 * 
 * @author GeorgeP
 */
public class CountdownWindow {

	/* Class Variables */
	
	private Shell shell;									/* The shell */
	private Label time;										/* The time label */
	
	private static final int SHELL_WIDTH = 225;				/* The width to use */
	private static final int SHELL_HEIGHT = 165;			/* The height to use */

	/* Constructor */
	
	/**
	 * Construct the countdown window
	 * 
	 * @param	parentSize		the parent size
	 * @param	parentLocation	the parent location
	 */
	public CountdownWindow(Point parentSize, Point parentLocation) {
	
		/* Construct the shell */
		shell = new Shell(SWT.NONE);
		shell.setLayout(new GridLayout());
		shell.setText("Countdown");
		
		/* Set the window icon and dispose of it */
		Image image = 
			new Image( shell.getDisplay(), OptionsSingleton.getInstance().getIconLocation() );
		shell.setImage( image );
				
		/* Setup the shell */
		setupShell(parentSize, parentLocation);
	}


	/* Public Methods */

	/**
	 * Return the shell
	 * 
	 * @return	the shell
	 */
	public Shell getShell() {
		
		return shell;
	}
	
	
	/**
	 * Set the count on the window
	 * 
	 * @param	count	the count to set on the countdown
	 */
	public void setCount(byte count) {
	
		if (count == 0) {
			time.setText("GO");
		}
		else {
			/* Create the spacer.  If sized window automatically this wouldn't be needed */
			String spacer = "  ";
			if (OptionsSingleton.getInstance().getOS()==OptionsSingleton.LINUX) {
				spacer = " ";
			}
			
			/* If the count is double figures then reduce the spacing */
			if (count > 9) {
				spacer = spacer.substring(1);
			}
			
			time.setText(spacer + count);
		}
		
		time.update();
		shell.update();
	}
	
	
	/* Private Methods */
	
	/**
	 * Sets the shell up.  Includes size, location and contents.
	 */
	private void setupShell(Point parentSize, Point parentLocation) {
		
		/* Set the shell size and centre */
		shell.setSize(SHELL_WIDTH,SHELL_HEIGHT);
		int x = parentLocation.x + ((parentSize.x - SHELL_WIDTH) / 2);
		int y = parentLocation.y + ((parentSize.y - SHELL_HEIGHT) / 2);
		shell.setLocation(x, y);
		
		/* Construct the contents */
		time = new Label(shell, SWT.NONE);
		GridData gd = new GridData();
		gd.widthHint = 200;
		gd.heightHint = 125;
		time.setLayoutData(gd);
		
		/* Set the font and dispose of it */
		Font font = new Font(shell.getDisplay(), 
			new FontData(OptionsSingleton.getInstance().getOSDependentFont(), 100, SWT.BOLD));
		time.setFont( font );
	}
}
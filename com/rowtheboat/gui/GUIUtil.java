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
 * File: GUIUtil.java
 * 
 * Date			Version		User		Description
 * 05-Feb-2004	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * GUIUtil provides a selection of utilities that can are used by the varous GUI classes. 
 * 
 * @author George Palmer
 */

public class GUIUtil {
	
	/**
	 * Create a standard grid data with a horizontal span
	 * 
	 * @param horizontalSpan	the number of columns to span
	 * @param hIndent			the horizontal indent
	 * @return the grid data object
	 */
	public static GridData getHorizontalGridData(int horizontalSpan, int hIndent) {
		
		GridData gridData = new GridData();
		gridData.horizontalSpan = horizontalSpan;
		gridData.horizontalIndent = hIndent;
		
		return gridData;
	}
	
	
	/**
	 * Create a standard grid data with a horizontal span
	 * 
	 * @param horizontalSpan	the number of columns to span
	 * @return the grid data object
	 */
	public static GridData getHorizontalSpanGridData(int horizontalSpan) {
		
		GridData gridData = new GridData();
		gridData.horizontalSpan = horizontalSpan;
		
		return gridData;
	}
	
	
	/**
	 * Create a standard grid data with a horizontal span and width hint
	 * 
	 * @param horizontalSpan		the number of columns to span
	 * @param widthHint				the width hint
	 * @return the grid data object
	 */
	public static GridData getHorizontalSpanGridData(int horizontalSpan, int widthHint) {
		
		GridData gridData = getHorizontalSpanGridData(horizontalSpan);
		gridData.widthHint = widthHint;
	
		return gridData;
	}
	
	
	/**
	 * Create a standard grid data with a horizontal and vertical span
	 * 
	 * @param horizontalSpan		the number of columns to span
	 * @param verticalSpan			the number of rows to span
	 * @return the grid data object
	 */
	public static GridData getHorizontalAndVerticalSpanGridData
			(int horizontalSpan, int verticalSpan) {
		
		GridData gridData = getHorizontalSpanGridData(horizontalSpan);
		gridData.verticalSpan = verticalSpan;
		
		return gridData;
	}
	
	
	/**
	 * Returns a standard GridData
	 * 
	 * @param	width	the width hint to apply to the grid data
	 * @param	height	the height hint to apply to the grid data
	 * 
	 * @return the GridData object 
	 */
	public static GridData getStandardGridData(int width, int height) {
		
		/* Create and return the standard grid data */
		GridData gridData = new GridData();
		gridData.widthHint = width;
		gridData.heightHint = height;
		
		return gridData;
	}
	
	
	/**
	 * Return a grid data with an indent and width hint
	 * 
	 * @param indent	the amount of the indent 
	 * @param width		the width hint
	 * @return	the GridData object
	 */
	public static GridData getIndentGridData(int indent) {
		
		GridData gridData = new GridData();
		gridData.horizontalIndent = indent;
		
		return gridData;
	}
	
	
	/**
	 * Return a grid data with an indent and width hint
	 * 
	 * @param indent	the amount of the indent 
	 * @param width		the width hint
	 * @return	the GridData object
	 */
	public static GridData getIndentGridData(int height, int width) {
		
		GridData gridData = new GridData();
		gridData.widthHint = width;
		
		return gridData;
	}
	
	
	/**
	 * Returns a standard GridData
	 * 
	 * @param	width	the width hint to apply to the grid data
	 * @param	height	the height hint to apply to the grid data
	 * 
	 * @return the GridData object 
	 */
	public static GridData getStandardGridData(int hSpan, int vSpan, int width, int height) {
		
		/* Create and return the standard grid data */
		GridData gridData = new GridData();
		gridData.horizontalSpan = hSpan;
		gridData.verticalSpan = vSpan;
		gridData.widthHint = width;
		gridData.heightHint = height;
		
		return gridData;
	}
	
	
	/**
	 * Returns a standard GridLayout
	 * 
	 * @param	hSpacing	the horizontal spacing
	 * @param	vSpacing	the vertical spacing
	 * @param	mHeight		the margin height
	 * @param	mWidth		the margin width
	 * 
	 * @return	the GridLayout object
	 */
	public static GridLayout getStandardGridLayout
			(int hSpacing, int vSpacing, int mHeight, int mWidth) {
		
		/* Create and return the standard grid layout */
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = hSpacing;
		layout.verticalSpacing = vSpacing;
		layout.marginHeight = mHeight;
		layout.marginWidth = mWidth;
		
		return layout;
	}
	
	
	/**
	 * Show a generic error message
	 * 
	 * @param parent	the parent shell
	 * @param errorText	the text to display
	 */
	public static void showErrorDialog(Shell parent, String errorText) {
		
		MessageBox box = new MessageBox(parent, SWT.ICON_ERROR | SWT.OK);
		box.setText("Program Error");
		box.setMessage(errorText);
		box.open();
	}
	
	
	/**
	 * Show an IO error message
	 * 
	 * @param parent	the parent shell
	 * @param error		the text to display
	 */
	public static void showInterruptedException(Shell parent, String error) {
	
		showErrorDialog(parent, "There has been an internal program error - Interrupt error\n("
			 + error + ")");
	}
	
	
	/**
	 * Show an IO error message
	 * 
	 * @param parent	the parent shell
	 * @param error		the text to display
	 */
	public static void showIOException(Shell parent, String error) {
		
		showErrorDialog(parent, "There has been a communication error.  Please check the serial" +
			"port in the options dialog\n(" + error + ")");
	}
	

	/**
	 * Show a number format error message
	 * 
	 * @param parent	the parent shell
	 * @param error		the text to display
	 */
	public static void showNumberFormatException(Shell parent, String error) {
		
		showErrorDialog(parent, "There has been an internal program error - number format " +
			"exception\n(" + error + ")");
	}
	
	
	/**
	 * Show a number format error message
	 * 
	 * @param parent	the parent shell
	 * @param error		the text to display
	 */
	public static void showSAXException(Shell parent, String error) {
		
		showErrorDialog(parent, "There has been an XML parsing error.  Please check the xml file" +
			"is not corrupt\n(" + error + ")");
	}
	
	
	/**
	 * Show an xml read file error message
	 * 
	 * @param parent	the parent shell
	 * @param error		the text to display
	 */
	public static void showXMLReadException(Shell parent, String error) {
		
		showErrorDialog(parent, "There has been an error reading the XML file.  Please check this " +
			"the location is correct and the file not corrupt\n(" + error + ")");
	}
}
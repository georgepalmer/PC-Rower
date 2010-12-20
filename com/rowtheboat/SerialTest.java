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
 * File: SerialTest.java
 * 
 * Date			Version		User		Description
 * 28-Nov-2004	1.0			GeorgeP		Initial version coded
 * 
 */
 
package com.rowtheboat;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

/**
 * Type class description here
 * 
 * @author GeorgeP
 */
public class SerialTest {

	public static void main(String[] args) {
		
		CommPortIdentifier [] commPorts = new CommPortIdentifier[10];
		Enumeration ports = CommPortIdentifier.getPortIdentifiers();
		int i = 0;
		while (ports.hasMoreElements()) {
			
			commPorts[i] = (CommPortIdentifier) ports.nextElement();
			System.out.println(i + ". " + commPorts[i].getName());
			i++;
		}
		
		System.out.println("Please select a port");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int choice = -1;
		try {
			choice = Integer.parseInt(br.readLine());
		} catch (NumberFormatException e) {
			System.out.println("Error formating number");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Exception.  Check running java not javaw");
			e.printStackTrace();
		}
		
		if (choice != -1) {
			Enumeration ports2 = CommPortIdentifier.getPortIdentifiers();
			int j = 0;
			while (ports2.hasMoreElements()) {
			
				if (j == choice) {
					initialisePort(commPorts[j]);
					return;
				}
				j++;
			}
		}
	}

	/**
	 * 
	 */
	private static void initialisePort(CommPortIdentifier port) {
		
		/* Open the serial port */
		SerialPort serialPort;
		int section = 0;
		try {
			serialPort = (SerialPort) port.open("PC-Rower", 2000);
			section = 1;
			
			/* Set the port up to the defaults required */
			serialPort.notifyOnDataAvailable(true);
			serialPort.setSerialPortParams
				(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			section = 2;
		
			/* Get the input and output streams */
			OutputStream pm2PlusOutputStream = serialPort.getOutputStream();
			InputStream pm2PlusInputStream = serialPort.getInputStream();
			section = 3;
			
			System.out.println("Successfully opened!!");
			/* Add the event listener */
			//serialPort.addEventListener( 
			//	new PM2PlusReadEventListener( pm2PlusInputStream, workout.getType() ) );
		} catch (PortInUseException e) {
			System.out.println("PortInUseException.  Section: " + section);
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			System.out.println("UnsupportedCommOperationException.  Section: " + section);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException.  Section: " + section);
			e.printStackTrace();
		}		
	}
}
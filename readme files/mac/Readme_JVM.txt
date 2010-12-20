PC-Rower 1.04
-------------

Install:
To install copy the PC-Rower folder to the destination of your choice.  Move the javax.comm.properties file to the java jre\lib folder

Next the serial drivers must be installed.  Open ForPackageMaker.zip and run the relevent .hqx file (Jaguar and Panthar users go for the _Jag version).  This sometimes doesn't install properly so check the folder System/Library/Java/Extensions for the files RXTXcomm.jar and libSerial.jnilib.  If they are not present then copy them from the folder ForPackageMaker/Install/Library/Java/Extensions within the PC-Rower folder to System/Library/Java/Extensions

You may now remove the ForPackageMaker folder as well as the associated zip and hqx files.

Until the MAC build process is automated the software files default to windoze permissions.  Therefore you will need to give java_swt and PC-Rower executable permissions (eg chmod 755 filename).

I recommend you create a data directory within the PC-Rower folder to save your workouts in.  This will obviously need write permissions

Starting:
From a shell window, run the PC-Rower file.

Run time:
Make sure the PM2+ is plugged in and your serial port setup to 9600 bits per second, 8 databits, 1 stopbit, and no parity (this is the standard setup so you should be ok).

In the options dialog the 'Retrieve full stroke data' when switched off will only retrieve the time and distance of each boat. This speeds up the software which is useful when there are lots of PM2+ units plugged in but means logging would not have any data for stroke split, power etc.

The rest of the program options should be intuitive.


Problems/Bugs:
Please submit to bugs@rowtheboat.com
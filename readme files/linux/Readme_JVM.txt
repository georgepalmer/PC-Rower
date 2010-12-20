PC-Rower 1.04
-------------

Prequities:
You MUST have java 1.4 installed to run this software.


Install:
To install copy the PC-Rower folder to the destination of your choice.  Add the javax.comm.properties file to the java jre\lib folder

You must be a member of the uucp group to allow serial port locking. You may need to change the /var/lock group to be uucp (using chgrp)
I recommend you create a data directory within the PC-Rower folder to save your workouts in

If you are using the Motif version and the program dialogs have unreadable/missing sections then you will need to create two symbolic links to the file libXm.so.2.1  To do this type:
ln -s libXm.so.2.1 libXm.so.2
ln -s libXm.so.2.1 libXm.so
If you need to do this will depend on your Linux setup and will be obvious as soon as you click on a menu dialog.


Starting:
Once in the PC-Rower directory type ./PC-Rower


Run time:
Make sure the PM2+ is plugged in and your serial port setup to 9600 bits per second, 8 databits, 1 stopbit, and no parity (this is the standard setup so you should be ok).

In the options dialog the 'Retrieve full stroke data' when switched off will only retrieve the time and distance of each boat. This speeds up the software which is useful when there are lots of PM2+ units plugged in but means logging would not have any data for stroke split, power etc.

The rest of the program options should be intuitive.


Problems/Bugs:
Please submit to bugs@rowtheboat.com
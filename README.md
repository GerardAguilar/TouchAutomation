# TouchAutomation
Simple java application that records mouse clicks and replays them
Intro: Allows recording and replaying of mouse clicks.

Instructions: 
1. Unzip the Touch Automation folder anywhere
2. Open up the command prompt and navigate to the extracted folder
3. Type in the following:
	java -jar TouchAutomation.jar
4. Wait for the screen to be tinted blue
5. Press "R" to start recording (no mouse clicks will go through until "R" is toggled)
6. Click around (easier to see with paint)
7. Once done, press Ctrl + # (any digit from 0 to 9)
8. To replay, press Shift + # (number that was recorded into previously)
9. To exit out of the application, press Ctrl + Shift + 0

Note:
* Requires the following in the same directory as the jar file 
	- libvlc.dll
	- libvlccore.dll
	- plugins folder for vlc
* Currently only covers a 1920x1080 Landscape screen.

Known bugs:
* Replays aren't interruptible at the moment
* A video is recorded only once for every replay id
* Timing feels a bit slower
* Keyboard events for controls aren't consumed and goes through to the other windows

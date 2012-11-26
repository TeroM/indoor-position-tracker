Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
.___            .___                                    
|   | ____    __| _/____   ___________                  
|   |/    \  / __ |/  _ \ /  _ \_  __ \  ______         
|   |   |  \/ /_/ (  <_> |  <_> )  | \/ /_____/         
|___|___|  /\____ |\____/ \____/|__|                    
         \/      \/                                     
                    .__  __  .__                        
______   ____  _____|__|/  |_|__| ____   ____           
\____ \ /  _ \/  ___/  \   __\  |/  _ \ /    \   ______ 
|  |_> >  <_> )___ \|  ||  | |  (  <_> )   |  \ /_____/ 
|   __/ \____/____  >__||__| |__|\____/|___|  /         
|__|              \/                        \/          
  __                        __                          
_/  |_____________    ____ |  | __ ___________          
\   __\_  __ \__  \ _/ ___\|  |/ // __ \_  __ \         
 |  |  |  | \// __ \\  \___|    <\  ___/|  | \/         
 |__|  |__|  (____  /\___  >__|_ \\___  >__|            
                  \/     \/     \/    \/       

1 Purpose of this program

This program was made in fall of 2012 as part of the Interface Technologies course in University of Helsinki. The program demonstrates how indoor positioning can be enstablished on a coarse level using wifi fingerprints.



2 Requirements

An Android phone with Android version 2.2 and up.



3 Installing the software

If you are installing this software from the .apk package use these instructions: 

If you want to use the source code:
	1. 'git clone git@github.com:TeroM/indoor-position-tracker.git'
	2. Load the project to Eclipse
	3. Build



4 Usage

Indoor position tracker stores wifi fingerprints from selected locations and detects which one matches the users current wifi fingerprint.

The indoor position tracker doesn't currently have any built in wifi fingerprints, so the user has to configure the detectable wifi-spots on the map by hand.

	4.1 Adding detectable points to the map
	Open menu and select 'Edit mode'. Go to the location where you want a wifi fingerprint recorded. Now press on the map where you are. Open menu and select 'Scan', and the program starts to record the wifi fingerprint. The fingerprint is an average value from many recordings so turning yourself 360 degrees while recording might help getting a more accurate fingerprint. The recording stops automatically after about 3 seconds. To add an other point on map just move to an other location, tap it on the map and scan again.

	4.2 Using positioning
	Exit the 'Edit mode' by pressing back button. Now you see the map which also displays a green circle which is your calculated position.

	4.3 Removing fingerprints
	Open menu and select "Delete all fingerprints".
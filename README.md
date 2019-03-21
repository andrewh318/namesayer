# SE 206 Project

# Documentation

Welcome to Andrew and Vincent's NameSayer 4 Assignment.

## What is Name Sayer?

Name Sayer is a software tool to assist users in learning unfamiliar names. Users are able to listen to the correct pronunciation spoken by the owner of the name themselves from the database, as well as record their own version to compare and contrast. If a database recording is deemed of bad quality, users can choose to flag it. 

## Execution

1. Download NameSayer4.jar and move it to the desired directory
2. Move the folder "names" (containing the database names- **case sensitive**) to the same directory as the jar file
3. Open the terminal and navigate to where the jar and Names file are located
4. Enter the command 'java -jar NameSayer4.jar'
5. If execution is successful, the following directories will be created: 'userNames', 'combinedNames', 'playlists', and 'trimmedNormalised'. The 'trimmedNormalised' directory should also contain the 'userNames', 'combinedNames', and 'names' directories. The 'BadNames.txt' file should also be created (used to store recordings that have been flagged as bad by the user). 

## How to Use
**Navigation**
- The green navigation bar at the top of the application allows you to navigate between the 'Manage' mode and 'Practice' mode, as well as the option to upload a custom list of names.
- The black playback bar at the bottom of the application is responsible for displaying progress when playing and recording names, as well changing volume controls.

**Manage**

- The program initially loads the manage screen where you can view and manage all your playlists.
- All existing playlists will be shown on the left of the screen.
- Create a new playlist by clicking the new button under the list of playlists, and delete a playlist by selecting the desired playlist and clicking delete.
- Double click a playlist to rename it and press enter or click on another playlist to confirm.
- Selecting a playlist displays its contents on the right side of the screen.
- To add to the selected playlist, enter the name you want to add in the search bar below the list of all names. 
- As you type, the list of names will be updated to predict what name you want to add. You can double click on a name in the list to complete what you are typing to this name. Pressing the space bar will auto-complete what you are typing by grabbing the first name in the updated list.
- You can combine names into one by simply typing the names separated by spaces or hyphens.
- Finally, press the add button to add the name in the search bar to the playlist
- You can press the play button after selecting a name in your playlist to hear the best database recording of the name.
- You can delete a name from your playlist by selecting the name and clicking delete.

**Practice Mode**

- Clicking the 'PRACTICE' button on the navigation bar takes you to the practice set up screen.
- Select an existing playlist you would like to practice and then press 'Let's go!' to continue
- Note: Only **non-empty** playlists can be taken to the practice mode
- The button with the play arrow plays the best **database** recording for the currently displayed name
- Clicking on the mic button will give you 5 seconds to make your own recording of the currently displayed name
- After recording is complete, it will show up in the combo box below.
- Click play to listen to it, or delete if you are unsatisfied with the recording.
- Select a recording you have made from the combo box and click compare to hear it played consecutively after the database version. 
- Clicking the flag button will mark the database recording as bad. The application will then switch the recording out for the **next** best recording.
- You can cycle to the next and previous names in your playlist by pressing the arrow buttons.

**Upload**

- Clicking upload allows you to choose a .txt file containing a playlist you wish to add to the program. This will automatically be created as one of your playlists.
- When you close the program, you will be prompted to save your playlists. Choosing "Ok", will save all playlists in the current session to .txt files which will automatically be read in upon the next launch. Choosing "cancel" keeps whatever playlists were saved before you made any changes in your session, meaning on next launch, your edits from the last session will not appear, but older playlists will still be saved.

## Special Features

- Multiple playlists- facilitates flexibility when practising
- Dynamic flagging- best recording for a database name will always be played
- Convenient session saving- pick up right where you left off. 
- Smart predictive search- easily find and add the names you want to practice

## Screenshots
*Main Menu Screen*
![Main Menu](https://i.imgur.com/GwKcQVb.png)
*Practice Mode*
![Practice](https://i.imgur.com/uvVi6Kw.png)
*Themes!*
![Shop](https://i.imgur.com/JnOY16e.png)
## Soon TM
- ~~Full volume controls built into application~~
- ~~Currency system allowing you to purchase new colour themes~~
- Login system to enable multiple users on the same machine



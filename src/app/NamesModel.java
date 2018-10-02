// Author: Andrew Hu and Vincent Tunnell

package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class NamesModel {
    private ObservableList<Name> _databaseNames = FXCollections.observableArrayList();
    private ObservableList<Name> _userNames = FXCollections.observableArrayList();
    private ObservableList<Name> _playlist = FXCollections.observableArrayList();

    public static final String DATABASERECORDINGSDIRECTORY = "names";
    public static final String USERRECORDINGSDIRECTORY = "userNames";
    public static final String BADNAMESFILE = "BadNames.txt";



    public ObservableList<Name> getDatabaseNames() {
        return _databaseNames;
    }

    public ObservableList<Name> getUserNames() {
        return _userNames;
    }

    public ObservableList<Name> getPlaylist() {
        return _playlist;
    }

    // renamed this from 'readDirectory' to setUp() toto prevent confusion
    public void setUp(){
        // remove all database/user names when directories are read
        // to prevent double reading of names
        _databaseNames.clear();
        _userNames.clear();
        createErrorFile();

        readDirectory(DATABASERECORDINGSDIRECTORY);
        readDirectory(USERRECORDINGSDIRECTORY);
    }

    private void createErrorFile() {
        File file = new File(BADNAMESFILE);
        try {
            file.createNewFile();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Parses a wav file specified by filename, creates a recording object, and adds it to the appropriate name object
    //in the database

    // made this method private
    private void parse(String fileName, String directory) {

        //Extracts information from the filename and directory
        String path = directory + "/" + fileName;
        String fullPath = new File(path).toURI().toString();
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        String[] parts = fileName.split("_");
        String date = parts[1];
        String time = parts[2];
        String stringName = parts[3].substring(0,1).toUpperCase() + parts[3].substring(1);

        //Creates a new recording object with the extracted information
        Recording recording = new Recording(stringName, date, path, fullPath, time);

        //Checks if the name is already in the database and saves that object to a variable if found
        Name nameObject = null;
        for (Name databaseName : _databaseNames) {
            if (databaseName.getName().equals(stringName)) {
                nameObject = databaseName;
                break;
            }
        }

        //if the name is not in the database so create a new Name object and add it to the database
        if (nameObject == null) {
            nameObject = new Name(stringName);
            _databaseNames.add(nameObject);
        }

        // if the name is already in the database, then add it as a recording to the name object
        if (directory.equals(DATABASERECORDINGSDIRECTORY)) {
            nameObject.addDatabaseRecording(recording);
        } else if (directory.equals(USERRECORDINGSDIRECTORY)) {
            nameObject.addUserRecording(recording);
        }
    }

    //Reads a folder specified by name for wav files, and creates the folder if it doesn't exist

    // made this method private
    private void readDirectory(String directory) {
        File Folder = new File(directory);

        //Creates directory if it doesn't exist and returns
        if (!Folder.exists()) {
            Boolean success = Folder.mkdir();
            if (!success) {
                System.out.println("Failed to create directory");
            }
            return;
        }

        //Reads files from directory
        File[] listOfFiles = Folder.listFiles();

        if (listOfFiles == null){
            // add exception here
            System.out.println("NULL");
        } else {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    String fileName = listOfFiles[i].getName();
                    parse(fileName, directory);
                }
            }
        }
    }

    //Method that reads in a directory containing text files containing playlist information
    private void readPlaylists(String fileName) {
        File Folder = new File(fileName);


        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(Folder));
            String st;
            while ((st = br.readLine()) != null) {

                st.replaceAll("-", " ");
                String[] names = st.split(" ");

                for (String name : names) {
                    name.toLowerCase();
                    name.trim();
                    name.substring(0,1).toUpperCase();
                    for (Name nameObject : _databaseNames) {
                        if (nameObject.getName().equals(name)) {

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }





    public void addNameToPlaylist(Name name){
        _playlist.add(name);
    }

    public boolean searchPlaylist(Name name){
        return _playlist.contains(name);
    }

    public void removeNameFromPlaylist(Name name) {
        _playlist.remove(name);
    }


    public void clearPlaylist() {
        _playlist.clear();
    }

}
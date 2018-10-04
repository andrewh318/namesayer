// Author: Andrew Hu and Vincent Tunnell

package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NamesModel {
    private ObservableList<Name> _databaseNames = FXCollections.observableArrayList();
    private ObservableList<Name> _userNames = FXCollections.observableArrayList();
    private ObservableList<Playlist> _allPlaylists = FXCollections.observableArrayList();
    private Playbar _playbar = new Playbar();

    public static final String DATABASERECORDINGSDIRECTORY = "names";
    public static final String USERRECORDINGSDIRECTORY = "userNames";
    public static final String PLAYLISTS_DIRECTORY = "playlists";
    public static final String BADNAMESFILE = "BadNames.txt";
    public static final String DEFAULT_PLAYLIST_NAME = "Default Playlist";
    public static final String NEW_PLAYLIST_NAME = "New Playlist";



    public ObservableList<Name> getDatabaseNames() {
        return _databaseNames;
    }

    public ObservableList<Name> getUserNames() {
        return _userNames;
    }

    public ObservableList<Playlist> getPlaylists() {
        return _allPlaylists;
    }

    public Playbar getPlaybar() {
        return _playbar;
    }


    // renamed this from 'readDirectory' to setUp() toto prevent confusion
    public void setUp(){
        // remove all database/user names when directories are read
        // to prevent double reading of names
        _databaseNames.clear();
        _userNames.clear();

        createErrorFile();
        readDirectory(new File(DATABASERECORDINGSDIRECTORY));
        readDirectory(new File(PLAYLISTS_DIRECTORY));
        readDirectory(new File(USERRECORDINGSDIRECTORY));

        setUpDefaultPlaylist();
    }

    private void setUpDefaultPlaylist(){
        if (_allPlaylists.size() == 0){
            // if there are no playlists create a default playlist
            Playlist playlist = new Playlist(DEFAULT_PLAYLIST_NAME);
            _allPlaylists.add(playlist);
        }
    }

    private void createErrorFile() {
        File file = new File(BADNAMESFILE);
        try {
            file.createNewFile();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }




    private void readDirectory(File directory) {
        directory.mkdir();
        File[] files =  directory.listFiles();

        if (directory.isDirectory()) {

            if (directory.getName().equals(DATABASERECORDINGSDIRECTORY) || directory.getName().equals(USERRECORDINGSDIRECTORY)) {
                for (File file : files) {
                    readName(file);
                }
            } else if (directory.getName().equals(PLAYLISTS_DIRECTORY)) {
                for (File file : files) {
                    readPlaylist(file);
                }
            }

        }
    }




    //Parses a wav file specified by filename, creates a recording object, and adds it to the appropriate name object
    //in the database
    private void readName(File file) {

        String fileName = file.getName();
        String directory = file.getParent();

        //Extracts information from the filename and directory
        String path = directory + "/" + fileName;
        String fullPath = new File(path).toURI().toString();
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        String[] parts = fileName.split("_");
        String date = parts[1];
        String time = parts[2];
        String stringName = parts[3].substring(0, 1).toUpperCase() + parts[3].substring(1);

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


    //Method that reads a playlist file
    private List<String> readPlaylist(File file) {

        String playlistName = file.getName().substring(0, file.getName().lastIndexOf('.'));
        Playlist playlist = new Playlist(playlistName);
        List<String> invalidNames = new ArrayList<String>();

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                List<Name> namesList = generateListOfNames(st);

                if (namesList == null) {
                    invalidNames.add(st);
                } else {
                    playlist.addName(namesList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addPlaylist(playlist);

        return invalidNames;

    }

    //Method that reads in a string containing names separated by spaces and returns a list of Name object if successful,
    //or null if unsuccessful.

    public List<Name> generateListOfNames(String names) {
        List<Name> namesList = new ArrayList<Name>();

        names.replaceAll("-", " ");
        String[] namesArray = names.split(" ");

        for (String name : namesArray) {

            name = name.trim();
            name = name.toLowerCase();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            boolean valid = false;
            for (Name databaseName : _databaseNames) {
                if (databaseName.getName().equals(name)) {
                    valid = true;
                    namesList.add(databaseName);
                    break;
                }
            }

            if (!valid) {
                return null;
            }
        }
        return namesList;
    }




    public void addPlaylist(Playlist playlist){
        _allPlaylists.add(playlist);
    }
//
//    public boolean searchPlaylist(Name name){
//        return _playlist.contains(name);
//    }
//
//    public void removeNameFromPlaylist(Name name) {
//        _playlist.remove(name);
//    }
//
//
//    public void clearPlaylist() {
//        _playlist.clear();
//    }

}
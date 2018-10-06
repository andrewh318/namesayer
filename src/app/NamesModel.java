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
    public static final String TRIMMED_NORMALISED_DIRECTORY = "trimmedNormalised";



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

        new File(TRIMMED_NORMALISED_DIRECTORY).mkdir();
        new File(TRIMMED_NORMALISED_DIRECTORY + "/" + DATABASERECORDINGSDIRECTORY).mkdir();
        new File(TRIMMED_NORMALISED_DIRECTORY + "/" + USERRECORDINGSDIRECTORY).mkdir();

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


    private void normaliseAndTrimAudioFile(File file) {


        String fileName = file.getName();
        String directory = file.getParent();
        String path = directory + "/" + fileName;


        String audioCommand = "ffmpeg -i ./" + path + " -af silenceremove=1:0:-30dB" + " ./" + TRIMMED_NORMALISED_DIRECTORY
                + "/" + path;
        BashCommand create = new BashCommand(audioCommand);
        create.startProcess();
        try {
            create.getProcess().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    //Parses a wav file specified by filename, creates a recording object, and adds it to the appropriate name object
    //in the database
    private void readName(File file) {

        normaliseAndTrimAudioFile(file);

        String fileName = file.getName();
        String directory = file.getParent();

        //Extracts information from the filename and directory
        String path = TRIMMED_NORMALISED_DIRECTORY + "/" + directory + "/" + fileName;
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
    public List<String> readPlaylist(File file) {

        String playlistName = file.getName().substring(0, file.getName().lastIndexOf('.'));
        Playlist playlist = new Playlist(playlistName);
        List<String> invalidNames = new ArrayList<String>();

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                Name namesList = generateListOfNames(st);

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

    //Method that reads in a string containing names separated by spaces and returns a Name object if only 1 name, a
    //combined name object if multiple names, or null if unsuccessful.

    public Name generateListOfNames(String names) {

        if (names.isEmpty()){
            return null;
        }
        List<Name> namesList = new ArrayList<Name>();

        // fixed this, didn't have the 'names =' before so it wasn't replacing the hyphens properly.
        names = names.replaceAll("-", " ");
        String[] namesArray = names.split(" ");

        String stringName = "";

        for (String name : namesArray) {
            name = name.trim();
            name = name.toLowerCase();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            boolean valid = false;
            for (Name databaseName : _databaseNames) {
                if (databaseName.getName().equals(name)) {
                    valid = true;
                    namesList.add(databaseName);
                    stringName = stringName + databaseName.getName() + " ";
                    break;
                }
            }

            if (!valid) {
                return null;
            }
        }
        stringName.trim();

        if (namesList.size() == 1) {
            return namesList.get(0);
        } else {
            CombinedName combinedName = new CombinedName(stringName);

            for (Name name : namesList) {
                combinedName.addName(name);
            }

            return combinedName;
        }
    }




    public void addPlaylist(Playlist playlist){
        _allPlaylists.add(playlist);
    }

    public void deletePlaylist(Playlist playlist){
        _allPlaylists.remove(playlist);
    }

    public void savePlaylists(){
        ObservableList<Playlist> playlists = this.getPlaylists();
        // loop through list of playlists
        for (Playlist playlist : playlists){
            // for each playlist create a new BufferedReader
            ObservableList<Name> listNames = playlist.getPlaylist();
            String playlistName = playlist.getName();
            try {
                FileWriter fileWriter = new FileWriter("playlists/" + playlistName + ".txt");
                BufferedWriter out = new BufferedWriter(fileWriter);
                // for each list of names (for each playlist entry)
                for (Name name: listNames){
                    // as each playlist entry can contain multiple names, print entries names on the same line
                    out.append(name.getName() + " ");
                    // after a playlist entry has finished printing, add a new line to indicate next entry
                    out.newLine();
                }
                // after playlist is complete, close the writer so a new one can be created for next playlist
                out.close();
            } catch (IOException e){
                e.printStackTrace();
            }

        }
    }
}